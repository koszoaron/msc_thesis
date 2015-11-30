package com.github.koszoaron.uninav.footpath.graph;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.github.koszoaron.uninav.pojo.Location;

import android.content.Context;
import android.content.res.XmlResourceParser;

/**
 * Graph representation of an XML map.
 * 
 * This class is used to create a graph from XML files stored in the directory
 * res/xml.
 * Data from multiple files/layers can be joined into a single map/graph
 * with the function mergeNodes(). After graph creation use functions implemented
 * in this class to find routes, nodes, etc. 
 * 
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class Graph {
	
	private static final String OSM = "osm";
	private static final String DOOR = "door";
	private static final String ELEVATOR = "elevator";
	private static final String HIGHWAY = "highway";
	private static final String FOOTWAY = "footway";
	private static final String ID = "id";
	private static final String INDOOR = "indoor";
	private static final String K = "k";
	private static final String LAT = "lat";
	private static final String LON = "lon";
	private static final String LEVEL = "level";
	private static final String MERGE_ID = "merge_id";
	private static final String NAME = "name";
	private static final String ND = "nd";
	private static final String NODE = "node";
	private static final String REF = "ref";
	private static final String STEP_COUNT = "step_count";
	private static final String STEPS = "steps";
	private static final String TAG = "tag";
	private static final String WAY = "way";
	private static final String YES = "yes";
	
	private static final String WIFI = "wifi";
	private static final String IR = "ir";
	private static final String BT = "bt";
	private static final String ROOM = "room";
	private static final String WALL = "wall";
	
	private LinkedList<GraphNode> nodes;
	private LinkedList<GraphEdge> edges;
	
	private LinkedList<Polyline> wallPolys;
	
	private GraphNode[] nodesById;
	private GraphNode[] nodesByName;

	private Context context;
	
	public Graph(Context context) {
		this.context = context;
		nodes = new LinkedList<>();
		edges = new LinkedList<>();
		wallPolys = new LinkedList<>();
	}
	
	public boolean addToGraphFromXMLResourceParser(XmlResourceParser parser) throws XmlPullParserException, IOException {
		if (parser == null) {
			return false;
		}
		
		boolean res = false;
		boolean isOsmData = false;  /* flag to wait for osm data */
		GraphNode tempNode = new GraphNode();  /* temporary node to be added to all nodes in file */
		GraphNode nullNode = new GraphNode();  /* 'NULL' node to point to for dereferencing */
		GraphWay tempWay = new GraphWay();  /* temporary way to be added to all nodes in file */
		GraphWay nullWay = new GraphWay();  /* 'NULL' node to point to for dereferencing */
		LinkedList<GraphNode> allNodes = new LinkedList<>();  /* store all nodes found in file */
		LinkedList<GraphWay> allWays = new LinkedList<>();  /* store all ways found in file */
		LinkedList<GraphWay> remainingWays = new LinkedList<>();
		LinkedList<GraphWay> wallWays = new LinkedList<>();

		parser.next();
		int eventType = parser.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (!isOsmData) {
						if (parser.getName().equals(OSM)) {
							isOsmData = true;
							// TODO: Test for correct version? (v0.6)
						}
					} else {
						int attributeCount = parser.getAttributeCount();
						
						if (parser.getName().equals(NODE)) {
							tempNode = new GraphNode();
							
							for (int i = 0; i < attributeCount; i++) {
								if (parser.getAttributeName(i).equals(ID)) {
									tempNode.setId(parser.getAttributeIntValue(i, 0));
								} else if (parser.getAttributeName(i).equals(LAT)) {
									tempNode.setLat(Double.parseDouble(parser.getAttributeValue(i)));
								} else if (parser.getAttributeName(i).equals(LON)) {
									tempNode.setLon(Double.parseDouble(parser.getAttributeValue(i)));
								}
							}							
						} else if (parser.getName().equals(TAG)) { 
							if (tempNode != nullNode) {
								for (int i = 0; i < attributeCount; i++) {
									if (parser.getAttributeName(i).equals(K)) {
										if (parser.getAttributeValue(i).equals(INDOOR)) {
											String v = parser.getAttributeValue(i + 1);
											
											tempNode.setIndoors(v.equals(YES));
											if (v.equals(DOOR)) {
												tempNode.setIndoors(true);  /* this is a door (which is always inDOORS) ;) */
											}
										} else if (parser.getAttributeValue(i).equals(NAME)) {
											tempNode.setName(parser.getAttributeValue(i + 1));
										} else if (parser.getAttributeValue(i).equals(MERGE_ID)) {
											tempNode.setMergeId(parser.getAttributeValue(i + 1));
										} else if (parser.getAttributeValue(i).equals(STEP_COUNT)) {
											tempNode.setSteps(parser.getAttributeIntValue(i + 1, Integer.MAX_VALUE));
										} else if (parser.getAttributeValue(i).equals(LEVEL)) {
											float f = Float.parseFloat(parser.getAttributeValue(i + 1));
											tempNode.setLevel(f);
										} 
									}
								}
							} else {  /* way */
								for (int i = 0; i < attributeCount; i++) {
									if (parser.getAttributeName(i).equals(K)) {
										if (parser.getAttributeValue(i).equals(STEP_COUNT)) {
											tempWay.setSteps(parser.getAttributeIntValue(i + 1, Integer.MAX_VALUE));
										} else if (parser.getAttributeValue(i).equals(LEVEL)) {
											float f = Float.parseFloat(parser.getAttributeValue(i + 1));
											tempWay.setLevel(f);
										} else if (parser.getAttributeValue(i).equals(INDOOR)) {
											String v = parser.getAttributeValue(i + 1);
											
											if (v.equals(YES)) {
												tempWay.setIndoor(true);
											} else if (v.equals(ROOM) || v.equals(WALL)) {
												tempWay.setIndoor(true);
												tempWay.setWall(true);
											} else {
												tempWay.setIndoor(false);
											}
										} else if (parser.getAttributeValue(i).equals(HIGHWAY)) {
											String v = parser.getAttributeValue(i + 1);
											if (v.equals(STEPS)){
												if (tempWay.getSteps() == 0) { 	/* no steps configured before
												 								 * so set to undefined (but present),
																				 * otherwise might be set later */
													tempWay.setSteps(-1);
												}
											}
											if (v.equals(ELEVATOR)) {
												tempWay.setSteps(-2);
											}
											if (v.equals(FOOTWAY)) {
												tempWay.setFootway(true);
											}
										}
									}
								}
							}
							
						} else if (parser.getName().equals(WAY)) {
							tempWay = new GraphWay();
							
							for (int i = 0; i < attributeCount; i++) {
								if (parser.getAttributeName(i).equals(ID)) {
									tempWay.setId(parser.getAttributeIntValue(i, 0));
								}
							}	
						} else if (parser.getName().equals(ND)) {
							for (int i = 0; i < attributeCount; i++) {
								if (parser.getAttributeName(i).equals(REF)) {
									//int ref = Integer.parseInt(parser.getAttributeValue(i));
									long ref = Long.parseLong(parser.getAttributeValue(i));
									tempWay.addRef(ref);
								}
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (isOsmData) {
						if (parser.getName().equals(OSM)) {
							res = true;
						} else if (parser.getName().equals(NODE)) {
							allNodes.add(tempNode);
							tempNode = nullNode;		
						} else if (parser.getName().equals(TAG)) { 
							/* nothing */
						} else if (parser.getName().equals(WAY)) {
							allWays.add(tempWay);
							tempWay = nullWay;
						} else if (parser.getName().equals(ND)) {
							/* nothing */
						}
					}
					break;
				default:
			}
			
			eventType = parser.next();			
		}
		
		for (GraphWay way : allWays) {  /* find ways which are indoors at some point */
			LinkedList<Long> refs = way.getRefs();
			
			if (way.isWall()) {  /* walls shall be added to a separate list */
				wallWays.add(way);
			} else if (way.isIndoor()) {  /* whole path is indoors, keep it */
				if (way.isFootway()) {
					remainingWays.add(way);
				}
			} else {  /* check for path with indoor node */
				boolean stop = false;
				
				for (Long ref : refs) {  /* check if there is a node on the path which is indoors */
					for (GraphNode node : allNodes) {
						if (node.getId() == ref.intValue()) {
							if (way.isFootway()) {
								remainingWays.add(way);
							}
							stop = true;  /* found indoor node on path to be added to graph
										   * so stop both for loops and continue with the next way */
						}

						if (stop) {
							break;
						}
					}
					
					if (stop) {
						break;
					}
				}
			}
		}

		if (remainingWays.size() == 0) {
			/* nothing to be added to the graph */
			return false;
		}

		for (GraphWay way : wallWays) {
			Polyline pl = new Polyline(context);
			pl.setWidth(1);
			List<GeoPoint> points = new LinkedList<>();
			for (int i = 0; i < way.getRefs().size(); i++) {
				GraphNode n = getNode(allNodes, way.getRefs().get(i).intValue());
				if (n != null) {
					GeoPoint gp = new GeoPoint(n.getLat(), n.getLon(), n.getLevel());
					points.add(gp);
				}
			}
			pl.setPoints(points);
			wallPolys.add(pl);
		}
		
		for (GraphWay way : remainingWays) {
			float level = way.getLevel();
			boolean indoor = way.isIndoor();
			GraphNode firstNode = getNode(allNodes, way.getRefs().get(0).intValue());
			
			for (int i = 1; i <= (way.getRefs().size() - 1); i++) {
				GraphNode nextNode = getNode(allNodes, way.getRefs().get(i).intValue());
				double len = getDistance(firstNode.getLat(), firstNode.getLon(), nextNode.getLat(), nextNode.getLon());  /* get length between P1 and P2 */
				double compDegree = getInitialBearing(firstNode.getLat(), firstNode.getLon(), nextNode.getLat(), nextNode.getLon());  /* get initial bearing between P1 and P2 */
				GraphEdge tempEdge = new GraphEdge(firstNode, nextNode, len, compDegree, level, indoor);

				if (way.getSteps() > 0) {  /* make the edge a staircase if the steps count was set correctly */
					tempEdge.setStairs(true);
					tempEdge.setElevator(false);
					tempEdge.setSteps(way.getSteps());
				} else if (way.getSteps() == -1) {  /* make the edge a staircase if the steps count was set to -1 (undefined steps) */
					tempEdge.setStairs(true);
					tempEdge.setElevator(false);
					tempEdge.setSteps(-1); 
				} else if (way.getSteps() == -2) {  /* make the edge an elevator if the steps count was set to -2 */
					tempEdge.setStairs(false);
					tempEdge.setElevator(true);
					tempEdge.setSteps(-2);
				} else if (way.getSteps() == 0) {
					tempEdge.setStairs(false);
					tempEdge.setElevator(false);
					tempEdge.setSteps(0);
				}
				
				edges.add(tempEdge);  /* add the edge to the graph */
				if (!nodes.contains(firstNode)) {
					nodes.add(firstNode);  /* add the node to the graph if it's not present */
				}
				firstNode = nextNode;
			}
			
			if (!nodes.contains(firstNode)) {
				nodes.add(firstNode);  /* add the last node to the graph if it's not present */
			}
		}
		
		return res;
	}
	
	// use this to add edges for stairs to flags, this should be called once
	/**
	 * 
	 */
	public void mergeNodes() {
		LinkedList<GraphNode> nodesWithMergeId = new LinkedList<>();
		
		/* collect all relevant nodes to merge */
		for (GraphNode node : nodes) {
			if (node.getMergeId() != null) {
				nodesWithMergeId.add(node);
			}
		}
		
		for (GraphNode node : nodesWithMergeId) {
			for (GraphNode otherNode : nodesWithMergeId) {
				/* only merge if the IDs are the same but not the nodes */
				if (node.getMergeId() != null && node.getMergeId().equals(otherNode.getMergeId()) && !node.equals(otherNode)) {
					/* update all references pointing to otherNode to node */
					for (GraphEdge edge: edges) {
						if (edge.getNode0().equals(otherNode)) {
							edge.setNode0(node);
						}
						if (edge.getNode1().equals(otherNode)) {
							edge.setNode1(node);
						}
					}
					/* otherNode was merged/removed, do not check */
					otherNode.setMergeId(null);
				}
			}
		}
		
		/* create arrays for binary search */
		nodesById = sortNodesById(nodes);
		nodesByName = sortNodesByName(nodes);

		/* add edges to node, faster look up for neighbors */
		for (GraphEdge edge : edges) {
			GraphNode n0 = edge.getNode0();
			GraphNode n1 = edge.getNode1();
			if (!n0.getLocEdges().contains(edge)) {
				n0.getLocEdges().add(edge);
			}
			if (!n1.getLocEdges().contains(edge)) {
				n1.getLocEdges().add(edge);
			}
		}
	}

	public Stack<GraphNode> getShortestPath(String from, String to, boolean staircase, boolean elevator, boolean outside) {
		return getShortestPath(getNodeFromName(from), getNodeFromName(to), staircase, elevator, outside);
	}
	
	public Stack<GraphNode> getShortestPath(int from, String to, boolean staircase, boolean elevator, boolean outside) {
		return getShortestPath(getNode(from), getNodeFromName(to), staircase, elevator, outside);
	}
	
	/**
	 * This is the faster version which can be used after parsing the data
	 * @param id
	 * @return
	 */
	public GraphNode getNode(int id) {
		int u = 0;
        int o = nodesById.length - 1;
        int m = 0;

        while (!(o < u)) {
            m = (u + o) / 2;
            if (id == nodesById[m].getId()) {
            	return nodesById[m];
            }
            if (id < nodesById[m].getId()) {
            	o = m - 1;
            } else {
            	u = m + 1;
            }
        }
        
		return null;		
	}
	
	/**
	 * returns the node with the given name, binary search
	 * @param name
	 * @return
	 */
	public GraphNode getNodeFromName(String name) {
	    int u = 0;
	    int o = nodesByName.length - 1;
	    int m = 0;
	
	    while(!(o < u)) {
	        m = (u + o) / 2;
	        if (name.equals(nodesByName[m].getName())) {
	        	return nodesByName[m];
	        }
	        if (name.compareTo(nodesByName[m].getName()) < 0) {
	        	o = m - 1;
	        } else {
	        	u = m + 1;
	        }
	    }
	
		return null;
	}

	/**
	 * return all names of nodes != null in a String array
	 * @return
	 */
	public String[] getRoomList() {
		String[] retArray = new String[nodesByName.length];
		
		for (int i = 0; i < retArray.length; i++) {
			retArray[i] = nodesByName[i].getName();
		}
		
		return retArray; 
	}
	
	public List<String> getRoomsList() {
		return Arrays.asList(getRoomList());
	}

	/**
	 * returns the edge containing nodes a and b
	 * @param a
	 * @param b
	 * @return
	 */
	public GraphEdge getEdge(GraphNode a, GraphNode b) {
		GraphEdge res = null;
		
		for (GraphEdge edge : a.getLocEdges()) { 
			if (edge.getNode0().equals(a) && edge.getNode1().equals(b)) {
				res = edge;
			} else if (edge.getNode1().equals(a) && edge.getNode0().equals(b)) {
				res = edge;
			}
		}
		
		return res;
	}
	
	public List<Polyline> getWalls(float level) {
		List<Polyline> res = new LinkedList<>();
		
		for (Polyline w : wallPolys) {
			res.add(w);
		}
		
		return res;
	}
	
	/**
	 * Returns the distance between two points given in latitude/longitude
	 * @param lat1 latitude of first point
	 * @param lon1 longitude of first point
	 * @param lat2 latitude of second point
	 * @param lon2 longitude of second point
	 * @return the distance in meters
	 */
	public double getDistance(double lat1, double lon1, double lat2, double lon2) {
		/* source: http://www.movable-type.co.uk/scripts/latlong.html */
		double dLon = lon2 - lon1;
		double dLat = lat2 - lat1;
		
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		dLon = Math.toRadians(dLon);
		dLat = Math.toRadians(dLat);
		
		double r = 6378137;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + 
					Math.cos(lat1) * Math.cos(lat2) *
					Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		return c * r;
	}
	
	public double getInitialBearing(double lat1, double lon1, double lat2, double lon2) {	
		/* source: http://www.movable-type.co.uk/scripts/latlong.html */
		double dLon = lon2 - lon1;

		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		dLon = Math.toRadians(dLon);
		
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
		double b = Math.atan2(y, x);
		b = Math.toDegrees(b);
		
		return ((b < 0) ? b + 360.0 : b);
	}
	
	/**
	 * Returns the closest node to a position at the given level
	 * @param pos the position 
	 * @param level the level
	 * @param indoor set to true if indoor nodes should be included
	 * @param maxMeters limit of distance to a node
	 * @return the closest GraphNode
	 */
	public GraphNode getClosestNodeToLatLonPos(Location pos, float level, boolean indoor, int maxMeters) {
		double minDistance = Double.MAX_VALUE;
		double tempDistance = Double.MAX_VALUE;
		GraphNode minDistNode = null;
		
		for (GraphNode node : nodes) {
			/* first: the node has to be at the same level
			 * second: if indoors then take all nodes
			 * third: if not indoors then check if the node is not indoors */
			if (node.getLevel() == level && (indoor || (node.isIndoors() == indoor))) {
				tempDistance = getDistance(pos, node);
				if (tempDistance < minDistance) {
					minDistance = tempDistance;
					minDistNode = node;
				}
			}
		}
		
		if (minDistance < maxMeters) {
			return minDistNode;
		} else { 
			return null; 
		}
	}
	
	public double getClosestDistanceToNode(Location pos, float level, boolean indoor) {
		double minDistance = Double.MAX_VALUE;
		double tempDistance = Double.MAX_VALUE;
		
		for (GraphNode node : nodes) {
			/* first: the node has to be at the same level
			 * second: if indoors then take all nodes
			 * third: if not indoors then check if the node is not indoors */
			if (node.getLevel() == level && (indoor || (node.isIndoors()!=indoor))) {
				tempDistance = getDistance(pos, node);
				if (tempDistance < minDistance) {
					minDistance = tempDistance;
				}
			}
		}
		
		return minDistance;
	}
	
	/** 
	 * Creates a stack of nodes with the destination at the bottom using Dijkstra's algorithm
	 * 
	 * @param from
	 * @param to
	 * @param staircase
	 * @param elevator
	 * @param outside
	 * @return
	 */
	private Stack<GraphNode> getShortestPath(GraphNode from, GraphNode to, boolean staircase, boolean elevator, boolean outside) {		
		if (from == null || to == null) {
			return null;
		}
		
		int remainingNodes = nodesById.length;
		GraphNode[] previous = new GraphNode[nodesById.length];
		double[] dist = new double[nodesById.length];
		boolean[] visited = new boolean[nodesById.length];
		
		/* set the initial values */
		for (int i = 0; i < nodesById.length; i++) {
			dist[i] = Double.POSITIVE_INFINITY;
			previous[i] = null;
			visited[i] = false;
		}
		dist[getNodePosInIdArray(from)] = 0;
		
		while (remainingNodes > 0) {
			/* vertex u in q with smallest dist[] */
			GraphNode u;
			double minDist = Double.POSITIVE_INFINITY;
			int uI = -1;
			for (int i = 0; i < nodesById.length; i++) {
				if (!visited[i] && dist[i] < minDist) {
					uI = i;
					minDist = dist[i];
				}
			}
			
			if (uI == -1) {
				break;
			}
			
			/* u was found */
			u = nodesById[uI];
			visited[uI] = true;
			if (dist[uI] == Double.POSITIVE_INFINITY) {
				/* all remaining nodes are unreachable from source */
				break;
			}
			
			/* get neighbors of u in q */
			LinkedList<GraphNode> nOuIq = getNeighbours(visited, u, staircase, elevator, outside);
			if (u.equals(to)) {
				/* u = to -> found path to destination
				 * build a stack of nodes, destination is at the bottom */
				Stack<GraphNode> s = new Stack<>();
				while (previous[uI] != null) {
					s.push(u);
					uI = getNodePosInIdArray(u);
					u = previous[uI];
				}
				return s;
			} else {
				remainingNodes--;
			}
			for (GraphNode v : nOuIq) {
				double distAlt = dist[uI] + getEdge(u, v).getLength();
				int vI = getNodePosInIdArray(v);
				if (distAlt < dist[vI]) {
					dist[vI] = distAlt;
					previous[vI] = u;
				}
			}
		}
	
		return null;
	}

	/**
	 * This is the slower version which is used during parsing
	 * @param list
	 * @param id
	 * @return
	 */
	private GraphNode getNode(LinkedList<GraphNode> list, int id) {
		for (GraphNode node : list) {
			if (node.getId() == id) {
				return node;
			}
		}
		
		return null;
	}

	/**
	 * Return node pos via binary search
	 * @param node
	 * @return
	 */
	private int getNodePosInIdArray(GraphNode node) {
		int u = 0;
	    int o = nodesById.length - 1;
	    int m = 0;
	
	    while (!(o < u)) {
	        m = (u + o) / 2;
	        if (node.getId() == nodesById[m].getId()) {
	        	return m;
	        }
	        if (node.getId() < nodesById[m].getId()) {
	        	o = m - 1;
	        } else {
	        	u = m + 1;
	        }
	    }
	    
		return -1;
	}

	/**
	 * Collects all neighbors of a given node from a given subset of nodes in the graph.
	 * 
	 * @param visited
	 * @param node
	 * @param staircase
	 * @param elevator
	 * @param outside
	 * @return
	 */
	private LinkedList<GraphNode> getNeighbours(boolean[] visited, GraphNode node, boolean staircase, boolean elevator, boolean outside) {
		LinkedList<GraphNode> res = new LinkedList<>();
		
		for (GraphEdge edge : node.getLocEdges()) {  /* check all edges if they contain the node */
			if (edge.isStairs() && !staircase ) {  /* the edge has steps, but it's not allowed -> skip */
				continue;														
			}
			if (edge.isElevator() && !elevator ) {  /* the edge has an elevator, but it's not allowed -> skip */
				continue;
			}
			if (!edge.isIndoor() && !outside) {  /* the edge is outdoors, but it's not allowed -> skip */
				continue;
			}
			
			GraphNode buf = null;
			
			if (edge.getNode0().equals(node)) {			/* node0 is node */
				buf = edge.getNode1();					/* add node1 */
			} else if (edge.getNode1().equals(node)) {	/* node 1 is node */
				buf = edge.getNode0();					/* add node0 */
			}
			
			if (outside) {
				if (buf != null) {  /* if outside all nodes are allowed */
					if (!res.contains(buf) && !visited[getNodePosInIdArray(buf)]) {
						res.add(buf);  /* add buf only once, when it's not visited */
					}
				}
			} else {  /* if !outside, only indoor nodes are allowed */
				if (buf != null && buf.isIndoors()) {
					if (!res.contains(buf) && !visited[getNodePosInIdArray(buf)]) {
						res.add(buf);  /* add buf only once, when it's not visited */
					}
				}
			}
		}
		return res;
	}

	/**
	 * Returns the distance between two nodes
	 * @param pos0 first position
	 * @param node1 second node
	 * @return the distance in meters
	 */
	private double getDistance(Location pos0, GraphNode node1) {
		return getDistance(pos0.getLatitude(), pos0.getLongitude(), node1.getLat(), node1.getLon());
	}

	/**
	 * creates an array containing only nodes with a name, sorted in ascending order
	 * @param nodes
	 * @return
	 */
	private GraphNode[] sortNodesByName(LinkedList<GraphNode> nodes) {
		GraphNode[] nodeArray;
		GraphNode temp;
		int numNulls = 0;
		int c = 0;
		boolean notSorted = true;
		
		/* count the number of nodes without a name */
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) != null && nodes.get(i).getName() == null) {
				numNulls++;
			}
		}
		
		/* create an array for nodes with a name */
		nodeArray = new GraphNode[nodes.size() - numNulls];
		for (GraphNode node : nodes) {
			if (node != null && node.getName() != null) {
				/* insert the node with a name into the array */
				nodeArray[c] = node;
				c++;
			}
		}
		
		/* sort by name (bubble sort) */
		while (notSorted) {
			notSorted = false;
			for (int i = 0; i < nodeArray.length - 1; i++) {
				if (nodeArray[i].getName().compareTo(nodeArray[i + 1].getName()) > 0) {
					temp = nodeArray[i];
					nodeArray[i] = nodeArray[i + 1];
					nodeArray[i + 1] = temp;
					notSorted = true;
				}
			}
		}
		
		return nodeArray;
	}
	
	/**
	 * creates an array, sorted by the ID in ascending order
	 * @param nodes
	 * @return
	 */
	private GraphNode[] sortNodesById(LinkedList<GraphNode> nodes) {
		GraphNode[] nodeArray;
		GraphNode temp;
		int c = 0;
		boolean notSorted = true;
		
		/* create an array for all nodes */
		nodeArray = new GraphNode[nodes.size()];
		for (GraphNode node : nodes) {
			if (node != null) {
				/* insert the node */
				nodeArray[c] = node;
				c++;
			}
		}
		
		/* sort by ID (bubble sort) */
		while (notSorted) {
			notSorted = false;
			for (int i = 0; i < nodeArray.length - 1; i++) {
				if (nodeArray[i].getId() > nodeArray[i + 1].getId()) {
					temp = nodeArray[i];
					nodeArray[i] = nodeArray[i + 1];
					nodeArray[i + 1] = temp;
					notSorted = true;
				}
			}
		}
		
		return nodeArray;
	}

	/**
	 * A class to represent a way in the map/graph.
	 *
	 * @author Paul Smith
	 * @author Aron Koszo <koszoaron@gmail.com>
	 */
	public class GraphWay {

		/** All nodes on this path (ref0 -> ref1 -> ref2  -> ...) */
		private LinkedList<Long> refs;
		private int id;

		/* >0 := number correct steps given
         *  0 := no steps
         * -1 := undefined number of steps
         * -2 := elevator */
		private int numSteps = 0;

		private float level;  /* Float.MAX_VALUE == undefined */
		private boolean isIndoor;
		private boolean footway = false;
		private boolean wall = false;

		/**
		 * Constructor to create an empty way.
		 */
		public GraphWay() {
			this.refs = new LinkedList<>();
			this.id = 0;
			this.level = Float.MAX_VALUE;
		}

		public LinkedList<Long> getRefs() {
			return refs;
		}

		public void addRef(long ref) {
			this.refs.add(ref);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public float getLevel() {
			return level;
		}

		public void setLevel(float level) {
			this.level = level;
		}

		public int getSteps() {
			return numSteps;
		}

		public void setSteps(int numSteps) {
			this.numSteps = numSteps;
		}

		public boolean isIndoor() {
			return isIndoor;
		}

		public void setIndoor(boolean isIndoor) {
			this.isIndoor = isIndoor;
		}

		public boolean isFootway() {
			return this.footway;
		}

		public void setFootway(boolean footway) {
			this.footway = footway;
		}

		public boolean isWall() {
			return this.wall;
		}

		public void setWall(boolean wall) {
			this.wall = wall;
		}

		public String toString(){
			String ret = "\nWay(" + this.id +"): ";
			ret += "\nRefs:";
			for (Long ref : refs) {
				ret += "\n    " + ref.intValue();
			}

			return ret;
		}
	}

}
