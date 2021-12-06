package com.rutkouski.port.entity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Port {

	static Logger logger = LogManager.getLogger();
	private static Port instance;
	private static ReentrantLock portLock = new ReentrantLock();
	private static AtomicBoolean create = new AtomicBoolean(false);
	private static final String PROPERTY_FILE_PATH = "property/port.properties";
	private static final double MAX_LOAD_FACTOR = 0.75;
	private static final double MIN_LOAD_FACTOR = 0.25;
	private static final int TIMER_DELAY_MILLIS = 500;
	private static final int TIMER_INTERVAL_MILLIS = 350;
	private ReentrantLock pierLock = new ReentrantLock();
	private Deque<Pier> freePiers = new ArrayDeque<>();
	private Condition freePierCondition = pierLock.newCondition();
	private Deque<Pier> busyPiers = new ArrayDeque<>();
	private ReentrantLock containerStorageLock = new ReentrantLock();
	private Condition availableLoadCondition = containerStorageLock.newCondition();
	private Condition unloadCondition = containerStorageLock.newCondition();
	private int currentContainerAmount;
	private final int MAX_CAPACITY;
	private final int PIER_AMOUNT;
	
	private Port() {
		InputStream propertyFileStream = getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_PATH);
		Properties properties = new Properties();
		
		try {
			properties.load(propertyFileStream);
		} catch (IOException e) {
			logger.warn("Input stream is invalid");
		}
		PIER_AMOUNT = Integer.parseInt(properties.getProperty("pier_amount"));
		MAX_CAPACITY = Integer.parseInt(properties.getProperty("capacity"));
		currentContainerAmount = Integer.parseInt(properties.getProperty("container_amount"));
		
		for (int i = 0; i < PIER_AMOUNT; i++) {
			freePiers.add(new Pier());
		}
		timerTrain();
	}

	public static Port getInstance() {
		if (!create.get()) {
			try {
				portLock.lock();
				if (instance == null) {
					instance = new Port();
					create.set(true);
				}
			} finally {
				portLock.unlock();
			}
		}
		return instance;
	}

	public Pier obtainPier() {
		logger.info("Start pier obtaining");
		try {
			pierLock.lock();
			
			if (freePiers.isEmpty()) {
				try {
					freePierCondition.await();
					logger.info("Waiting for free pier");
				} catch (InterruptedException e) {
					logger.error("Obtaining pier error: {}", e.getMessage());
					Thread.currentThread().interrupt();
				}
			}
			Pier pier = freePiers.removeFirst();
			busyPiers.add(pier);
			logger.info("Obtained pier {}", pier.getPierId());
			return pier;
			
		} finally {
			pierLock.unlock();
		}
	}

	public boolean releasePier(Pier pier) {
		try {
			pierLock.lock();
			busyPiers.remove(pier);
			boolean pierReleasing = freePiers.add(pier);
			freePierCondition.signal();
			logger.info("Pier {} is released", pier.getPierId());
			return pierReleasing;
			
		} finally {
			pierLock.unlock();
		}
	}

	public void loadContainer() {
		try {
			logger.info("Start container loading process");
			containerStorageLock.lock();
			
			if (currentContainerAmount == 0) {
				logger.info("Waiting for load");
				try {
					availableLoadCondition.await();
				} catch (InterruptedException e) {
					logger.error("Error while container loading: {}", e.getMessage());
					Thread.currentThread().interrupt();
				}
			}
			currentContainerAmount--;
			logger.info("Container was successfully loaded");
			
		} finally {
			containerStorageLock.unlock();
		}
	}
	
	public void unloadContainer() {
		try {
			logger.info("Start container unload");
			containerStorageLock.lock();
			
			if (currentContainerAmount == MAX_CAPACITY) {
				try {
					unloadCondition.await();
				} catch (InterruptedException e) {
					logger.error("Error while container unloading: {}", e.getMessage());
					Thread.currentThread().interrupt();
				}
			}
			currentContainerAmount++;
			logger.info("Container was successfully unloaded");
			
		} finally {
			containerStorageLock.unlock();
		}
	}

	private void timerTrain() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					containerStorageLock.lock();
					double currentLoadFactor = (double) currentContainerAmount / MAX_CAPACITY;
					
					if (currentLoadFactor > MAX_LOAD_FACTOR) {
						currentContainerAmount -= (int) (MIN_LOAD_FACTOR * MAX_CAPACITY + 1);
						logger.info("currentContainerAmount = {} has changed by timerTask", currentContainerAmount);
					} else if (currentLoadFactor < MIN_LOAD_FACTOR) {
						currentContainerAmount += (int) (MIN_LOAD_FACTOR * MAX_CAPACITY + 1);
						logger.info("currentContainerAmount = {} has changed by timerTask", currentContainerAmount);
					}
					
					for (int i = 0; i < currentContainerAmount; i++) {
						availableLoadCondition.signal();
					}
					
					for (int i = 0; i < MAX_CAPACITY - currentContainerAmount; i++) {
						unloadCondition.signal();
					}
					
				} finally {
					containerStorageLock.unlock();
				}
			}
		}, TIMER_DELAY_MILLIS, TIMER_INTERVAL_MILLIS);
	}
}
