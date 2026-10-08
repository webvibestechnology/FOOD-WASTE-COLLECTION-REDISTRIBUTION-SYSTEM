package com.food_waste_backend.exception;

public class UnauthorizedException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnauthorizedException(String message) {
        super(message);
    }
}