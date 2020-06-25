/**
 * 
 */
package com.prabal.loanservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Prabal Nandi
 *
 */
@ControllerAdvice
@Slf4j
public class ErrorHandlerAdvice {

	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<ErrorObjectToSend> handleRunTimeException(RuntimeException e) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, e);
	}

	@ExceptionHandler({ LoanAccountNotFoundException.class })
	public ResponseEntity<ErrorObjectToSend> handleNotFoundException(LoanAccountNotFoundException e) {
		return error(HttpStatus.NOT_FOUND, e);
	}

	@ExceptionHandler({ InvalidDataException.class })
	public ResponseEntity<ErrorObjectToSend> handleInvalidDataException(InvalidDataException e) {
		return error(HttpStatus.BAD_REQUEST, e);
	}

	private ResponseEntity<ErrorObjectToSend> error(HttpStatus status, Exception e) {
		log.error("Exception : ", e);
		return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorObjectToSend(status, e.getMessage()));
	}
}
