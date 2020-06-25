/**
 * 
 */
package com.prabal.loanservice.controller;

/**
 * @author Prabal Nandi
 *
 */
public class InvalidDataException extends RuntimeException {

	private static final long serialVersionUID = 571507254837147705L;

	public InvalidDataException(String message) {
		super(message);
	}
}
