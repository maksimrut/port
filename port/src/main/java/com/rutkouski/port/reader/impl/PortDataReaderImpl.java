package com.rutkouski.port.reader.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rutkouski.port.exception.PortException;
import com.rutkouski.port.reader.PortDataReader;

public class PortDataReaderImpl implements PortDataReader {
	
	static Logger logger = LogManager.getLogger();

	public List<String> readPortData(String filePath) throws PortException {
		
		if (filePath == null) {
			logger.error("File {} is not readable", filePath);
			throw new PortException("File is not readable" + filePath);
		}
		
		ClassLoader loader = getClass().getClassLoader();
		URL resourse = loader.getResource(filePath);
		String path = new File(resourse.getFile()).getAbsolutePath();
		
		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Failed or interrupted I/O operations", e);
			throw new PortException("Failed or interrupted I/O operations", e);
		}
		logger.info("Ships data was successfully read from file: {}", filePath);
		return lines;
	}
}
