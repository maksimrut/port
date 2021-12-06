package com.rutkouski.port.entity;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rutkouski.port.util.PierIdGenerator;

public class Pier {
	
	static Logger logger = LogManager.getLogger();
	private int pierId;
	
	public Pier() {
		pierId = PierIdGenerator.generateId();
	}

	public int getPierId() {
		return pierId;
	}

	public void operateShip(Ship ship) { 
		logger.info("Ship [shipId={}] operation on the pier [pierId={}] has started", ship.getShipId(), pierId);
		
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			logger.error("Erorr while ship operation: {}", e.getMessage());
			Thread.currentThread().interrupt();
		}
		Port port = Port.getInstance();
		switch (ship.getShipTask()) {
			case LOAD -> port.loadContainer();
			case UNLOAD -> port.unloadContainer();
		}
		logger.info("Ship [shipId={}] operation on the pier [pierId={}] is completed successfully", ship.getShipId(), pierId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pierId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Pier other = (Pier) obj;
		return pierId == other.pierId;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pier [pierId=");
		builder.append(pierId);
		builder.append("]");
		return builder.toString();
	}
}
