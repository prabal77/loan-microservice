/**
 * 
 */
package com.prabal.loanservice.domain.model;

import java.time.LocalDate;

import com.prabal.loanservice.events.Event;
import com.prabal.loanservice.events.EventTypeEnum;

/**
 * Internal Data structure to hold transaction event record within an aggregate
 * 
 * @author Prabal Nandi
 *
 */
public class TransactionDataWrapper {
	private Event sourceEvent;
	private LocalDate transactionDate;
	private String previousPrincipalAmount;
	private String finalPrincipalAmount;
	private EventTypeEnum sourceEventType;

	public TransactionDataWrapper(Event sourceEvent, LocalDate transactionDate, String previousPrincipalAmount,
			String finalPrincipalAmount) {
		super();
		this.sourceEvent = sourceEvent;
		this.transactionDate = transactionDate;
		this.previousPrincipalAmount = previousPrincipalAmount;
		this.finalPrincipalAmount = finalPrincipalAmount;
		this.sourceEventType = sourceEvent.getEventType();
	}

	public Event getSourceEvent() {
		return sourceEvent;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public String getPreviousPrincipalAmount() {
		return previousPrincipalAmount;
	}

	public String getFinalPrincipalAmount() {
		return finalPrincipalAmount;
	}

	public EventTypeEnum getSourceEventType() {
		return sourceEventType;
	}

	@Override
	public String toString() {
		return "TransactionDataWrapper [ transactionDate=" + transactionDate
				+ ", previousPrincipalAmount=" + previousPrincipalAmount + ", finalPrincipalAmount="
				+ finalPrincipalAmount + ", sourceEventType=" + sourceEventType + "]";
	}

}
