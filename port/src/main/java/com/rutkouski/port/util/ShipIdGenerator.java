package com.rutkouski.port.util;

public class ShipIdGenerator {
	
	private static int counter;
	
	private ShipIdGenerator() {}
	
	public static int generateId() {
		return ++counter;
	}
}
