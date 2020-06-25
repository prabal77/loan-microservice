/**
 * 
 */
package com.prabal.loanservice.controller;

import java.util.UUID;

/**
 * @author Prabal Nandi
 *
 */
public class LoanAccountNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 9049525661128184778L;

	public LoanAccountNotFoundException(UUID accountId) {
		super("Account with id = " + accountId.toString() + " not found");
	}
}
