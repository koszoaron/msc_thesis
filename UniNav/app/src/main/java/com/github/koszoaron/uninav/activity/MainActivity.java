package com.github.koszoaron.uninav.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.koszoaron.uninav.R;
import com.github.koszoaron.uninav.Util;
import com.github.koszoaron.uninav.footpath.core.BestFitPositioner;
import com.github.koszoaron.uninav.footpath.graph.Graph;
import com.github.koszoaron.uninav.footpath.graph.GraphEdge;
import com.github.koszoaron.uninav.footpath.graph.GraphNode;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private ListView lvDrawer;
    private MapView mvOsm;
    private FloatingActionButton btnNav;
    private FloatingActionButton btnX;

    private boolean irPositioning = false;
    private boolean wifiPositioning = false;
    private boolean btPositioning = false;
    private boolean gpsPositioning = true;

    private DrawerAdapter adapter;

    /* FP vars */
    private String nodeStart;
    private String nodeDest;
    private List<String> roomsList;
    private Graph graph;
    private double pathLength = 0.0;
    private List<GraphEdge> pathEdges;

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == btnNav) {
                NavParamsDialog npDialog = new NavParamsDialog();
                npDialog.show(getFragmentManager(), NavParamsDialog.class.getSimpleName());
            } else if (view == btnX) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        lvDrawer = (ListView)findViewById(R.id.lvDrawer);
        mvOsm = (MapView)findViewById(R.id.mapOsm);
        btnNav = (FloatingActionButton)findViewById(R.id.btnNav);
        btnX = (FloatingActionButton)findViewById(R.id.btnX);

        adapter = new DrawerAdapter();
        lvDrawer.setAdapter(adapter);

        btnNav.setOnClickListener(buttonClickListener);
        btnX.setOnClickListener(buttonClickListener);

        mvOsm.setTileSource(TileSourceFactory.MAPNIK);
        mvOsm.setMultiTouchControls(true);
        mvOsm.setMaxZoomLevel(25);

        mvOsm.getController().setZoom(18);
        GeoPoint startPoint = new GeoPoint(46.24675983979, 20.14669969074);
        mvOsm.getController().setCenter(startPoint);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initData();
    }

    private class DrawerAdapter extends BaseAdapter {
        private String[] itemNames = {"GPS", "IR", "BT", "WIFI", "Settings", "Beep", "Vibrate"};

        @Override
        public int getCount() {
            return itemNames.length;
        }

        @Override
        public Object getItem(int i) {
            return itemNames[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.drawer_list_item_checked, parent, false);
                holder = new ViewHolder();
                holder.cb = (CheckBox)convertView.findViewById(R.id.ctvText);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.cb.setText(itemNames[i]);
            switch (i) {
                case 0:
                    holder.cb.setChecked(gpsPositioning);
                    break;
                case 1:
                    holder.cb.setChecked(irPositioning);
                    break;
                case 2:
                    holder.cb.setChecked(btPositioning);
                    break;
                case 3:
                    holder.cb.setChecked(wifiPositioning);
                    break;
            }

            return convertView;
        }

        private class ViewHolder {
            CheckBox cb;
        }
    }

    private void initData() {
        graph = new Graph(MainActivity.this);

        try {
//			graph.addToGraphFromXMLResourceParser(getResources().getXml(R.xml.fesu5_mod));
            graph.addToGraphFromXMLResourceParser(getResources().getXml(R.xml.ir1));
            graph.mergeNodes();  /* called after all XML resources have been added */

            List<Polyline> walls = graph.getWalls(1);
            if (walls != null && walls.size() > 0) {
                mvOsm.getOverlays().addAll(walls);
                mvOsm.invalidate();
            }

            roomsList = graph.getRoomsList();
            for (String s : roomsList) {
                Util.logv("Room: " + s);
            }
        } catch (Resources.NotFoundException|XmlPullParserException|IOException e) {
            e.printStackTrace();
        }
    }

    private void initNavigation() {
        Stack<GraphNode> navPathStack;

        if (nodeStart == null || nodeDest == null) {
            Toast.makeText(MainActivity.this, R.string.you_must_select_a_destination, Toast.LENGTH_LONG).show();

            return;
        }

        btnNav.setVisibility(View.GONE);
        btnX.setVisibility(View.VISIBLE);

		/* calculate the route */
        navPathStack = graph.getShortestPath(nodeStart, nodeDest, true, false, true);

        if (navPathStack != null) {
			/* the navPathStack consists of the correct order of nodes on the path
			 * from these nodes the corresponding edge is used to get the original
			 * data connected to it. What has to be recalculated is the initial bearing
			 * because this is depending on the order of nodes being passed */

			/* list to store the new edges in */
            List<GraphEdge> tempEdges = new LinkedList<>();
			/* get the first node
			 * this is always the 'left' node, when considering
			 * a path going from left to right. */
            GraphNode node0 = navPathStack.pop();

            while (!navPathStack.isEmpty()) {
				/* get the 'right' node */
                GraphNode node1 = navPathStack.pop();
				/* get the edge connecting 'left' and 'right' nodes */
                GraphEdge origEdge = graph.getEdge(node0, node1);

				/* get the data which remains unchanged */
                double length = origEdge.getLength();
                float level = origEdge.getLevel();
                boolean indoor = origEdge.isIndoor();

				/* the direction has to be recalculated */
                double direction = graph.getInitialBearing(node0.getLat(), node0.getLon(), node1.getLat(), node1.getLon());

				/* create a new edge */
                GraphEdge tempEdge = new GraphEdge(node0, node1, length, direction, level, indoor);
				/* update additional values */
                tempEdge.setElevator(origEdge.isElevator());
                tempEdge.setStairs(origEdge.isStairs());
                tempEdge.setSteps(origEdge.getSteps());
                tempEdges.add(tempEdge);

				/* update the path length */
                pathLength += origEdge.getLength();

				/* the 'right' node is the new 'left' node */
                node0 = node1;
            }

			/* now that we have the correct order of nodes and initial bearings of edges
			 * we look for successive edges with little difference in their bearing
			 * to simplify the path and have less but longer edges */

			/* allow a difference of 5 degrees to both sides */
            double diff = 8.0;
            List<GraphEdge> simplifiedEdges = new LinkedList<>();
			/* the current edge to find equaling edges to */
            GraphEdge edgeI;
			/* the first node of the current edge */
            GraphNode nodeI0;
			/* this will be the last node of the last edge equaling edgeI */
            GraphNode nodeX1 = null;

			/* the data to sum up for the merging */
            float level;
            boolean indoor;
            boolean stairs;
            boolean elevator;
            int steps;
            int lastI = -1;

			/* iterate over all edges */
            for (int i = 0; i < tempEdges.size(); i++) {
                edgeI = tempEdges.get(i);
                nodeI0 = tempEdges.get(i).getNode0();
                level = edgeI.getLevel();
                indoor = edgeI.isIndoor();
                stairs = edgeI.isStairs();
                elevator = edgeI.isElevator();
                steps = edgeI.getSteps();

                lastI = i;
                for (int j = i + 1; j < tempEdges.size(); j++) {
                    GraphEdge edgeJ = tempEdges.get(j);
					/* only merge edges if they are identical in their characteristics */
                    if (edgeI.getLevel() == edgeJ.getLevel()
                            && edgeI.isElevator() == edgeJ.isElevator()
                            && edgeI.isIndoor() == edgeJ.isIndoor()
                            && edgeI.isStairs() == edgeJ.isStairs()
                            && BestFitPositioner.isInRange(edgeI.getCompDir(), tempEdges.get(j).getCompDir(), diff)) {

						/* edge_i and edge_j can be merged
						 * save the last node1 of last edge_j equaling edge_i */
                        nodeX1 = edgeJ.getNode1();

						/* set the number of steps only if defined (-1 := undefined, but steps) */
                        if (steps != -1) {
							/* only change 0 or defined steps */
                            if (edgeJ.getSteps() != -1) {
                                steps += edgeJ.getSteps();
                            } else {
								/* edge_j has no defined step count so set to undefined */
                                steps = -1;
                            }
                        }
                    } else {
						/* edge_i and edge_j can not be merged
						 * merge possible previously found edges and add them */

						/* point to the latest edge to try matching from */
                        i = j-1;

						/* nothing can be merged, leave edge_i as is */
                        if (nodeX1 == null) {
							/* add the same edge_i */
                            simplifiedEdges.add(edgeI);

                            break;
                        } else {
							/* add the modified new edge */
                            double bearing = graph.getInitialBearing(nodeI0.getLat(), nodeI0.getLon(), nodeX1.getLat(), nodeX1.getLon());
                            double length = graph.getDistance(nodeI0.getLat(), nodeI0.getLon(), nodeX1.getLat(), nodeX1.getLon());
                            GraphEdge tempEdge = new GraphEdge(nodeI0, nodeX1, length, bearing, level, indoor);

                            tempEdge.setElevator(elevator);
                            tempEdge.setStairs(stairs);
                            tempEdge.setSteps(steps);
                            simplifiedEdges.add(tempEdge);

							/* reset the last node to null to distinguish if something has to be merged */
                            nodeX1 = null;

                            break;
                        }
                    }
                }
            }

            if (lastI != -1) {
                for (int i = lastI; i < tempEdges.size(); i++) {
                    simplifiedEdges.add(tempEdges.get(i));
                }
            }

			/* set the current path */
            pathEdges = simplifiedEdges;

			/* load fancy graphics */

            Polyline p = new Polyline(MainActivity.this);
            List<GeoPoint> tempList = new ArrayList<>();
            for (GraphEdge e : pathEdges) {
                tempList.add(new GeoPoint(e.getNode0().getLat(), e.getNode0().getLon()));
            }
            p.setPoints(tempList);
            p.setWidth(2);
            p.setColor(Color.GREEN);
            mvOsm.getOverlays().add(p);
            mvOsm.invalidate();
        } else { /* navPathStack was null */
            this.setResult(RESULT_CANCELED);
            this.finish();
        }
    }

    /**
     * Custom {@link DialogFragment} to display the navigation parameters before starting to navigate.
     *
     * @author Aron Koszo <koszoaron@gmail.com>
     */
    private class NavParamsDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_navigation, null);

            final List<String> tempLocList = new LinkedList<>(roomsList);
            tempLocList.add(0, getString(R.string.select_or_click_find));

            final Spinner spLocation = (Spinner) dialogView.findViewById(R.id.spLocation);
            ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, tempLocList);
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLocation.setAdapter(locationAdapter);

            final List<String> tempDestList = new LinkedList<>(roomsList);
            tempDestList.add(0, getString(R.string.select_a_destination));

            final Spinner spDestination = (Spinner) dialogView.findViewById(R.id.spDestination);
            ArrayAdapter<String> destinationAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, tempDestList);
            destinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spDestination.setAdapter(destinationAdapter);

            builder.setView(dialogView)
                    .setTitle("Navigation")
                    .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO check source and destination
                            //TODO if correct: call initNavigation()

                            if (spDestination.getSelectedItemPosition() != 0) {
                                if (spLocation.getSelectedItemPosition() != 0) {
                                    nodeStart = tempLocList.get(spLocation.getSelectedItemPosition());
                                    nodeDest = tempDestList.get(spDestination.getSelectedItemPosition());

                                    Util.logv("Start: " + nodeStart + ", destination: " + nodeDest);

                                    initNavigation();
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.select_a_destination_or_wait_for_location, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, R.string.you_must_select_a_destination, Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavParamsDialog.this.getDialog().cancel();
                        }
                    });


            return builder.create();
        }
    }
}
