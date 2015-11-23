package com.github.koszoaron.uninav.pojo;

/**
 * Class describing a message coming via IR signals
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class IrMessage {
	private int code;
	private int extraCode;
	
	/**
	 * Creates a new instance of the {@link IrMessage} class.
	 * 
	 * @param code The infrared message code.
	 */
	public IrMessage(int code) {
		this(code, 0);
	}
	
	/**
	 * Creates a new instance of the {@link IrMessage} class.
	 * 
	 * @param code The infrared message code.
	 * @param extraCode The extra message code.
	 */
	public IrMessage(int code, int extraCode) {
		this.code = code;
		this.extraCode = extraCode;
	}
	
	/**
	 * Returns the IR message code.
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Returns the extra message code.
	 */
	public int getExtraCode() {
		return extraCode;
	}
	
	@Override
	public String toString() {
		return "ir code: " + code + ", extra code: " + extraCode;
	}
}