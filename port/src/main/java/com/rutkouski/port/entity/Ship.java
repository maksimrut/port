package com.rutkouski.port.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rutkouski.port.util.ShipIdGenerator;

public class Ship extends Thread {
	
	static Logger logger = LogManager.getLogger();
	private final int shipId;
	private ShipState shipState;
	private ShipTask task;
	
	public enum ShipState {
		NEW, PROCESSING, COMPLETE
	}
	
	public enum ShipTask {
		LOAD, UNLOAD
	}
	
	public Ship(ShipTask task) {
		this.task = task;
		shipId = ShipIdGenerator.generateId();
		shipState = ShipState.NEW;
	}
	
	public int getShipId() {
		return shipId;
	}

	public ShipState getShipState() {
		return shipState;
	}
	
	public ShipTask getShipTask() {
		return task;
	}

	@Override
	public void run() { 
		logger.info("Ship {} started", shipId);
		shipState = ShipState.PROCESSING;
		Port port = Port.getInstance();
		Pier pier = port.obtainPier();
		pier.operateShip(this);
		port.releasePier(pier);
		shipState = ShipState.COMPLETE;
		logger.info("Ship ({}) process completed successfully", shipId);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Ship [shipId=");
		builder.append(shipId);
		builder.append("]");
		return builder.toString();
	}
}
