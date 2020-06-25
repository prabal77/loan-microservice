/**
 * 
 */
package com.prabal.loanclient.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Command object representing Add New Payment command.
 * 
 * @author Prabal Nandi
 *
 */
public class AddPaymentCommand{
	private UUID accountId;
	private String amount;
	private LocalDate transactionDate;

	public AddPaymentCommand() {
		super();
	}

	public UUID getAccountId() {
		return accountId;
	}

	public void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}

	@Override
	public String toString() {
		return "AddPaymentCommand [accountId=" + accountId + ", amount=" + amount + ", transactionDate="
				+ transactionDate + "]";
	}

}
