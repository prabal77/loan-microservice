/**
 * 
 */
package com.prabal.loanservice.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

/**
 * Error object which will be sent by the application to end user
 * 
 * @author Prabal Nandi
 *
 */
public class ErrorObjectToSend {
	private HttpStatus status;
	private LocalDateTime timestamp;
	private String message;

	public ErrorObjectToSend(HttpStatus status, String message) {
		super();
		this.status = status;
		this.timestamp = LocalDateTime.now();
		this.message = message;
	}

	public ErrorObjectToSend() {
		super();
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ErrorObjectToSend [status=" + status + ", timestamp=" + timestamp + ", message=" + message + "]";
	};

}
