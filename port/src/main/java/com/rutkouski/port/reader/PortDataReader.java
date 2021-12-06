package com.rutkouski.port.reader;

import java.util.List;

import com.rutkouski.port.exception.PortException;

public interface PortDataReader {
	
	public List<String> readPortData(String filePath) throws PortException;
}
