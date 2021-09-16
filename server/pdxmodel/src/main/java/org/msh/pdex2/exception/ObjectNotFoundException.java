package org.msh.pdex2.exception;

import org.slf4j.Logger;

/**
 * Something not found here
 * @author Alex Kurasoff
 *
 */
public class ObjectNotFoundException extends Exception {
	private static final long serialVersionUID = 2835333466065571475L;

	public ObjectNotFoundException() {
		super();
	}

	public ObjectNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectNotFoundException(String message) {
		super(message);
	}
	/**
	 * throw-and-log
	 * @param message
	 * @param logger
	 */
	public ObjectNotFoundException(String message, Logger logger) {
		super(message);
		logger.error(message);
	}

	public ObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	public ObjectNotFoundException(Throwable cause, Logger logger) {
		super(cause);
		logger.error(cause.getMessage());
	}



}
