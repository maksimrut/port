package com.rutkouski.port.main;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rutkouski.port.entity.Ship;
import com.rutkouski.port.exception.PortException;
import com.rutkouski.port.parser.impl.ShipParserImpl;
import com.rutkouski.port.reader.impl.PortDataReaderImpl;

public class Main {
	
	private static final String FILE_PATH = "shipData/data.txt";
	
	public static void main(String[] args) {
		
		PortDataReaderImpl reader = new PortDataReaderImpl();
		ShipParserImpl parser = new ShipParserImpl();
		List<String> fileLines;
        List<Ship> ships;
		
        try {
        	fileLines = reader.readPortData(FILE_PATH);
			ships = parser.parseShips(fileLines);
		} catch (PortException e) {
			e.printStackTrace();
			return;
		}
        
        ExecutorService service = Executors.newFixedThreadPool(ships.size());
        ships.forEach(service :: execute);
        service.shutdown();
	}
}
