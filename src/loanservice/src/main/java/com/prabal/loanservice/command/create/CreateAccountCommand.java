/**
 * 
 */
package com.prabal.loanservice.command.create;

import java.time.LocalDate;
import java.util.regex.Pattern;

import org.springframework.lang.NonNull;

import com.prabal.loanservice.common.Command;
import com.prabal.loanservice.controller.InvalidDataException;
import com.prabal.loanservice.model.AccountInfo;

/**
 * Class representing Create New Loan Account Command. User can pass this
 * command via any API (currently via REST endpoint as POST body)
 * 
 * @author Prabal Nandi
 *
 */
public class CreateAccountCommand implements Command<AccountInfo> {
	@NonNull
	private String amount;
	@NonNull
	private String interest;
	@NonNull
	private LocalDate startDate;

	public CreateAccountCommand() {
		super();
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public void validate() {
		if (this.amount == null || this.amount.trim().isEmpty() || this.interest == null
				|| this.interest.trim().isEmpty() || !Pattern.matches("^[0-9]*$", this.amount)
				|| this.amount.trim().equals("0") || !Pattern.matches("^[0-9]*$", this.interest)
				|| this.interest.trim().equals("0") || this.startDate == null) {
			throw new InvalidDataException("Input is not valid");
		}
	}

	@Override
	public String toString() {
		return "CreateAccountCommand [amount=" + amount + ", interest=" + interest + ", startDate=" + startDate + "]";
	}

}
