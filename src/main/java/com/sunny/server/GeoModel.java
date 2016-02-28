/**
 * 
 */
package com.sunny.server;

/**
 * 
 *
 * Create on Jan 28, 2016 6:52:14 PM
 *
 * @author TonyZhou
 * 
 */
public class GeoModel {

	private long id;
	private float longitude;
	private float latitude;
	private long timestamp;
	private String dateTime;
	private int direction;
	private int hourSpeed;

	/**
	 * @return the hourSpeed
	 */
	public int getHourSpeed() {
		return hourSpeed;
	}

	/**
	 * @param hourSpeed
	 *            the hourSpeed to set
	 */
	public void setHourSpeed(int hourSpeed) {
		this.hourSpeed = hourSpeed;
	}

	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * @return the dateTime
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime
	 *            the dateTime to set
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the longitude
	 */
	public float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GeoModel [id=" + id + ", longitude=" + longitude + ", latitude=" + latitude + ", timestamp=" + timestamp + ", dateTime=" + dateTime + ", direction=" + direction + ", hourSpeed=" + hourSpeed + "]";
	}

}
