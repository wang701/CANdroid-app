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

	public J1939MsgEvent(String errMsg) {
		this.errMsg = errMsg;
	}
}
