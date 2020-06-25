/**
 * 
 */
package com.prabal.loanclient.model;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Account Information Model class
 * 
 * @author Prabal Nandi
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountInfo {
	private UUID accountId;
	private String loanAmount;
	private String interestRate;
	private LocalDate startDate;

	public AccountInfo() {
		super();
	}

	public AccountInfo(UUID accountId, String loanAmount, String interestRate, LocalDate startDate) {
		super();
		this.accountId = accountId;
		this.loanAmount = loanAmount;
		this.interestRate = interestRate;
		this.startDate = startDate;
	}

	public UUID getAccountId() {
		return accountId;
	}

	public void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}

	public String getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}

	public String getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	@Override
	public String toString() {
		return "AccountInfo [accountId=" + accountId + ", loanAmount=" + loanAmount + ", interestRate=" + interestRate
				+ ", startDate=" + startDate + "]";
	}

}
