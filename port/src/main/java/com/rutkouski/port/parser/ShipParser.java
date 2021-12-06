package com.rutkouski.port.parser;

import java.util.List;

import com.rutkouski.port.entity.Ship;

public interface ShipParser {
	
	List<Ship> parseShips(List<String> shipLines);
}
