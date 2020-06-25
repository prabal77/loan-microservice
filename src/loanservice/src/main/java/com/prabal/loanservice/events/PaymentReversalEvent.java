/**
 * 
 */
package com.prabal.loanservice.events;

import java.time.LocalDate;
import java.util.UUID;

import com.prabal.loanservice.domain.model.BaseAggregateRoot;
import com.prabal.loanservice.domain.model.TransactionAggregate;
import com.prabal.loanservice.domain.model.TransactionDataWrapper;

/**
 * Payment reversal event. This is a compensating transaction which undo the
 * effect of previous transaction.
 * 
 * @author Prabal Nandi
 *
 */
public class PaymentReversalEvent extends Event {
	private final EventTypeEnum eventType = EventTypeEnum.REVERSAL;

	private UUID accountId;
	private LocalDate transactionDate;
	private UUID revertedTransactionId;
	private String previousPrincipalAmount;

	@Override
	public void applyOn(BaseAggregateRoot aggregate) {
		((TransactionAggregate) aggregate).reconcileTransaction(this);
	}

	/**
	 * Reversal Event. It store currentAmount as previous and vice versa
	 * @param accountId
	 * @param wrapper
	 */
	public PaymentReversalEvent(UUID accountId, TransactionDataWrapper wrapper) {
		super();
		this.accountId = accountId;
		this.revertedTransactionId = wrapper.getSourceEvent().getId();
		this.transactionDate = wrapper.getTransactionDate();
		this.previousPrincipalAmount = wrapper.getFinalPrincipalAmount();
		this.finalPrincipalAmount = wrapper.getPreviousPrincipalAmount();
	}

	public UUID getAccountId() {
		return accountId;
	}

	public String getFinalPrincipalValue() {
		return this.finalPrincipalAmount;
	}

	public void setFinalPrincipalValue(String finalPrincipalValue) {
		this.finalPrincipalAmount = finalPrincipalValue;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public String getPreviousPrincipalAmount() {
		return previousPrincipalAmount;
	}

	public UUID getRevertedTransactionId() {
		return revertedTransactionId;
	}

	@Override
	public EventTypeEnum getEventType() {
		return this.eventType;
	}

	@Override
	public String getAuditString() {
		return "PaymentReversalEvent [ accountId=" + accountId + ", previousPrincipalAmount=" + previousPrincipalAmount
				+ ", finalPrincipalAmount=" + finalPrincipalAmount + ", transactionDate=" + transactionDate + "]";
	}

	@Override
	public String toString() {
		return "PaymentReversalEvent [eventType=" + eventType + ", accountId=" + accountId + ", transactionDate="
				+ transactionDate + ", revertedTransactionId=" + revertedTransactionId + ", previousPrincipalAmount="
				+ previousPrincipalAmount + ", finalPrincipalAmount=" + finalPrincipalAmount + ", getId()=" + getId()
				+ "]";
	}

}
