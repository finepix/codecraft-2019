package com.huawei.pojo;

public class Road {
	private int roadID;
	private int length;
	private int speed;
	private int channel;
	private int from;
	private int to;
	private boolean isDuplex;
	
	/**
	 * @param roadID
	 * @param length
	 * @param speed
	 * @param channel
	 * @param from
	 * @param to
	 * @param isDuplex 
	 */
	public Road(int roadID, int length, int speed, int channel, int from, int to, int isDuplex) {
		super();
		this.roadID = roadID;
		this.length = length;
		this.speed = speed;
		this.channel = channel;
		this.from = from;
		this.to = to;
		if(isDuplex == 1)
			this.isDuplex = true;
		else
			this.isDuplex = false;
	}
	public int getRoadID() {
		return roadID;
	}
	public void setRoadID(int roadID) {
		this.roadID = roadID;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public boolean isDuplex() {
		return isDuplex;
	}
	public void setDuplex(boolean isDuplex) {
		this.isDuplex = isDuplex;
	}
	

}
