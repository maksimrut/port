package com.rutkouski.port.parser.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rutkouski.port.entity.Ship;
import com.rutkouski.port.parser.ShipParser;

public class ShipParserImpl implements ShipParser {
	
	static Logger logger = LogManager.getLogger();
	
	@Override
	public List<Ship> parseShips(List<String> shipLines) {
		
		List<Ship> shipList;
		shipList = shipLines.stream()
				.map(Ship.ShipTask :: valueOf)
				.map(Ship :: new)
				.toList();
		
		logger.info("Parsing was successful");
		return shipList;
	}
}
