package com.rutkouski.port.exception;

public class PortException extends Exception {

	public PortException() {
		super();
	}

	public PortException(String message, Throwable cause) {
		super(message, cause);
	}

	public PortException(String message) {
		super(message);
	}

	public PortException(Throwable cause) {
		super(cause);
	}
}
