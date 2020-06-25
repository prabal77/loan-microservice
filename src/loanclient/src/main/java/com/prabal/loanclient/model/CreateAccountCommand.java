/**
 * 
 */
package com.prabal.loanclient.model;

import java.time.LocalDate;

import org.springframework.lang.NonNull;

/**
 * Class representing Create New Loan Account Command. User can pass this
 * command via any API (currently via REST endpoint as POST body)
 * 
 * @author Prabal Nandi
 *
 */
public class CreateAccountCommand{
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

	@Override
	public String toString() {
		return "CreateAccountCommand [amount=" + amount + ", interest=" + interest + ", startDate=" + startDate + "]";
	}

}
