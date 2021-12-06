package com.rutkouski.port.util;

public class PierIdGenerator {
	
	private static int counter;
	
	private PierIdGenerator() {}
	
	public static int generateId() {
		return ++counter;
	}
}
