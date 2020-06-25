/**
 * 
 */
package com.prabal.loanservice.command.payment;

import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import com.prabal.loanservice.common.Command;
import com.prabal.loanservice.controller.InvalidDataException;

/**
 * Command object representing Add New Payment command.
 * 
 * @author Prabal Nandi
 *
 */
public class AddPaymentCommand implements Command<TransactionInfo> {
	private static final String AMOUNT_REGEX = "^[0-9]+(\\.[0-9]{1,2})?$";
	private static final String AMOUNT_ZERO = "^[0.]+$";
	private UUID accountId;
	@NotNull
	private String amount;
	@NotNull
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

	public void validate() {
		if (this.accountId == null || this.amount.trim().isEmpty() || !Pattern.matches(AMOUNT_REGEX, this.amount)
				|| Pattern.matches(AMOUNT_ZERO, this.amount) || this.transactionDate == null) {
			throw new InvalidDataException("Input is not valid");
		}
	}

	@Override
	public String toString() {
		return "AddPaymentCommand [accountId=" + accountId + ", amount=" + amount + ", transactionDate="
				+ transactionDate + "]";
	}

}
