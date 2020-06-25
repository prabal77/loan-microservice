/**
 * 
 */
package com.prabal.loanservice.command.payment;

import java.util.UUID;

/**
 * Result of Add New Payment command
 * 
 * @author Prabal Nandi
 *
 */
public class TransactionInfo {
	private UUID transactionId;

	public TransactionInfo(UUID transactionId) {
		super();
		this.transactionId = transactionId;
	}

	public TransactionInfo(String errorMessage) {
		super();
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	@Override
	public String toString() {
		return "TransactionInfo [transactionId=" + transactionId + "]";
	}

}
