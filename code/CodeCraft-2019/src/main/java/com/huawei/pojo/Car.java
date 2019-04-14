package com.huawei.pojo;

/**
 * @author lenovo02
 *
 */
public class Car implements Comparable<Car>{
	private int car_id;
	private int sourceID;
	private int targetID;
	private int speed;
	private int planTime;
	private int priority;
	private int preset;
	
	/**
	 * @param car_id	
	 * @param sourceID	
	 * @param targetID	
	 * @param planTime	
	 * @param priority	
	 * @param preset	
	 */
	public Car(int car_id, int sourceID, int targetID,int speed, int planTime, int priority, int preset) {
		super();
		this.car_id = car_id;
		this.sourceID = sourceID;
		this.targetID = targetID;
		this.speed = speed;
		this.planTime = planTime;
		this.priority = priority;
		this.preset = preset;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getPreset() {
		return preset;
	}
	public void setPreset(int preset) {
		this.preset = preset;
	}
	public int getCar_id() {
		return car_id;
	}
	public void setCar_id(int car_id) {
		this.car_id = car_id;
	}
	public int getSourceID() {
		return sourceID;
	}
	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}
	public int getTargetID() {
		return targetID;
	}
	public void setTargetID(int targetID) {
		this.targetID = targetID;
	}
	public int getPlanTime() {
		return planTime;
	}
	public void setPlanTime(int planTime) {
		this.planTime = planTime;
	}
	@Override
	public String toString() {
		return "Car [car_id=" + car_id + ", sourceID=" + sourceID + ", targetID=" + targetID + ", speed=" + speed
				+ ", planTime=" + planTime + ", priority=" + priority + ", preset=" + preset + "]";
	}
	
	/* sorted by: priority -> speed
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Car o) {
		if (o.getPriority() != this.priority) {
			return o.getPriority() - this.priority;
		}
		else {
			return o.getSpeed() - this.speed;
		}
	}


}
