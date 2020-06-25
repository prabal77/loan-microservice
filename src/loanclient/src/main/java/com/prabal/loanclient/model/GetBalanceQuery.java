/**
 * 
 */
package com.prabal.loanclient.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Class representing Get Balance Query.
 * @author Prabal Nandi
 *
 */
public class GetBalanceQuery {
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

	@Override
	public String toString() {
		return "GetBalanceQuery [accountId=" + accountId + ", date=" + date + "]";
	}

}
