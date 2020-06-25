/**
 * 
 */
package com.prabal.loanservice.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import com.prabal.loanservice.model.AccountInfo;

/**
 * Aggregate object for a single Account Detail record. Doesn't use EventStore
 * pattern as it is not required for AccountDetails
 * 
 * 
 * @author Prabal Nandi
 *
 */
public class AccountAggregate {

	private AccountInfo accountInfo;

	public AccountAggregate(AccountInfo accountInfo) {
		super();
		this.accountInfo = accountInfo;
	}

	/**
	 * Static Factory method to create a new Loan Account aggregate object
	 * 
	 * @param loanAmount
	 * @param interestRate
	 * @param startDate
	 * @return {@link AccountAggregate}
	 */
	public static AccountAggregate createLoanAccount(String loanAmount, String interestRate,
			LocalDate startDate) {
		return new AccountAggregate(new AccountInfo(UUID.randomUUID(), loanAmount, interestRate, startDate));
	}

	public UUID getAccountId() {
		return this.accountInfo.getAccountId();
	}

	public String getLoanAmount() {
		return this.accountInfo.getLoanAmount();
	}

	public String getInterestRate() {
		return this.accountInfo.getInterestRate();
	}

	public LocalDate getLoanStartDate() {
		return this.accountInfo.getStartDate();
	}

	/**
	 * Returns the internal LoanAccount representation object
	 * 
	 * @return
	 */
	public AccountInfo getAccountInfo() {
		return this.accountInfo;
	}

}
