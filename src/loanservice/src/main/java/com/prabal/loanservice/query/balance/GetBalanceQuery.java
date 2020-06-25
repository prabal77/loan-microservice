/**
 * 
 */
package com.prabal.loanservice.query.balance;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.prabal.loanservice.common.Query;
import com.prabal.loanservice.controller.InvalidDataException;
import com.prabal.loanservice.model.AccountBalance;

/**
 * Class representing Get Balance Query.
 * 
 * @author Prabal Nandi
 *
 */
public class GetBalanceQuery implements Query<AccountBalance> {
	@NotNull
	private UUID accountId;
	private LocalDate date;

	public GetBalanceQuery() {
	}

	public GetBalanceQuery(UUID accountId, LocalDate date) {
		super();
		this.accountId = accountId;
		this.date = date;
	}

	public UUID getAccountId() {
		return accountId;
	}

	public void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void validate() {
		if (this.accountId == null) {
			throw new InvalidDataException("Invalid Account Id");
		}
	}

	@Override
	public String toString() {
		return "GetBalanceQuery [accountId=" + accountId + ", date=" + date + "]";
	}

}
