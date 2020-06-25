/**
 * 
 */
package com.prabal.loanclient.model;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model class representing Account Balance
 * @author Prabal Nandi
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountBalance {
	private UUID accountId;
	private LocalDate date;
	private String principalAmount;

	public AccountBalance() {
	}

	public AccountBalance(UUID accountId, LocalDate date, String principalAmount) {
		super();
		this.accountId = accountId;
		this.date = date;
		this.principalAmount = principalAmount;
	}

	public UUID getAccountId() {
		return accountId;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getPrincipalAmount() {
		return principalAmount;
	}

	@Override
	public String toString() {
		return "AccountBalance [accountId=" + accountId + ", date=" + date + ", principalAmount=" + principalAmount
				+ "]";
	}

}
