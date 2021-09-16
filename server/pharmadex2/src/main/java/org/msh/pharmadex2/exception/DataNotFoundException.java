package org.msh.pharmadex2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Common exception for Controller. 404
 * @author Alex Kurasoff
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataNotFoundException extends Exception {
	private static final long serialVersionUID = 74063558965747015L;

	public DataNotFoundException() {
		super();
	}

	public DataNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataNotFoundException(String message) {
		super(message);
	}

	public DataNotFoundException(Throwable cause) {
		super(cause);
	}

}
