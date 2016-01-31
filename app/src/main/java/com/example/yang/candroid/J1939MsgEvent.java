package com.example.yang.candroid;

import org.isoblue.can.CanSocket;
import org.isoblue.can.CanSocketJ1939;
import org.isoblue.can.CanSocketJ1939.J1939Message;

public class J1939MsgEvent {
	public String ifName;
	public String errMsg;
	public long name;
	public int addr;
	public long dstName;
	public int dstAddr;
	public int pgn;
	public int len;
	public int priority;
	public byte[] data;
	public int timestamp;
	
	public J1939MsgEvent(J1939Message msg) {
		this.ifName = msg.ifName;
		this.name = msg.name;
		this.addr = msg.addr;
		this.dstName = msg.dstName;
		this.dstAddr = msg.dstAddr;
		this.pgn = msg.pgn;
		this.len = msg.len;
		this.priority = msg.priority;
		this.data = msg.data;
		this.timestamp = msg.timestamp;		
	}

	private static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for(byte b: a)
			sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
    }
	
	public String toString() {
		String msgStr = String.format("%d %s %d %d %d %d %d %d %d %s",
				timestamp, ifName, name,
				addr, dstName, dstAddr,
				pgn, len, priority,
				byteArrayToHex(data));
        
		return msgStr;
	}
}
