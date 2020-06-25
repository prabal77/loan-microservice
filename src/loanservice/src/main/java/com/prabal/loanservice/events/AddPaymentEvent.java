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
 * Event: Add new payment to the loan account
 * 
 * @author Prabal Nandi
 *
 */
public class AddPaymentEvent extends Event {
	private final EventTypeEnum eventType = EventTypeEnum.ADD_PAYMENT;
	private final TransactionTypeEnum type = TransactionTypeEnum.CREDIT;

	private UUID accountId;
	private String amount;
	private LocalDate paymentDate;

	public AddPaymentEvent(UUID accountId, String amount, LocalDate paymentDate) {
		super();
		this.accountId = accountId;
		this.amount = amount;
		this.paymentDate = paymentDate;
	}

	@Override
	public void applyOn(BaseAggregateRoot aggregate) {
		((TransactionAggregate) aggregate).addPayment(this);
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

	public String getAmount() {
		return amount;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	@Override
	public String getAuditString() {
		return "AddPaymentEvent [ accountId=" + accountId + ", amount=" + amount + ", paymentDate=" + paymentDate
				+ ", BalanceAfterPayment = " + this.getFinalPrincipalValue() + " ]";
	}

	@Override
	public String toString() {
		return "AddPaymentEvent [eventType=" + eventType + ", type=" + type + ", accountId=" + accountId + ", amount="
				+ amount + ", paymentDate=" + paymentDate + ", finalPrincipalAmount=" + this.finalPrincipalAmount + "]";
	}

}
