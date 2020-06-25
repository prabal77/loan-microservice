/**
 * 
 */
package com.prabal.loanservice.events;

import java.time.LocalDate;
import java.util.UUID;

import com.prabal.loanservice.common.TransactionTypeEnum;
import com.prabal.loanservice.domain.model.BaseAggregateRoot;
import com.prabal.loanservice.domain.model.TransactionAggregate;

/**
 * "Create New Loan" Event class. This will be added to the TransactionEvent
 * store as the first transaction
 * 
 * @author Prabal Nandi
 *
 */
public class CreateLoanEvent extends Event {
	private final EventTypeEnum eventType = EventTypeEnum.CREATE_LOAN;
	private final TransactionTypeEnum type = TransactionTypeEnum.DEBIT;

	private UUID accountId;
	private String loanAmount;
	private String interestRate;
	private LocalDate startDate;

	public CreateLoanEvent(UUID accountId, String loanAmount, String interestRate, LocalDate startDate) {
		super();
		this.accountId = accountId;
		this.loanAmount = loanAmount;
		this.interestRate = interestRate;
		this.startDate = startDate;
		this.finalPrincipalAmount = loanAmount;
	}

	/**
	 * Applies this Event to input Aggregate instances
	 */
	@Override
	public void applyOn(BaseAggregateRoot aggregate) {
		((TransactionAggregate) aggregate).addFirstTransaction(this);
	}

	public String getFinalPrincipalValue() {
		return this.finalPrincipalAmount;
	}

	public void setFinalPrincipalValue(String finalPrincipalValue) {
		this.finalPrincipalAmount = finalPrincipalValue;
	}

	public EventTypeEnum getEventType() {
		return eventType;
	}

	public TransactionTypeEnum getType() {
		return type;
	}

	public UUID getAccountId() {
		return accountId;
	}

	public String getLoanAmount() {
		return loanAmount;
	}

	public String getInterestRate() {
		return interestRate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	@Override
	public String getAuditString() {
		return "CreateLoanEvent [ accountId=" + accountId + ", loanAmount=" + loanAmount + ", startDate=" + startDate
				+ "]";
	}

	@Override
	public String toString() {
		return "CreateLoanEvent [eventType=" + eventType + ", type=" + type + ", accountId=" + accountId
				+ ", loanAmount=" + loanAmount + ", interestRate=" + interestRate + ", startDate=" + startDate
				+ ", finalPrincipalAmount=" + finalPrincipalAmount + "]";
	}

}
