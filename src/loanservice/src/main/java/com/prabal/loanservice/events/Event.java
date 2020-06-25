/**
 * 
 */
package com.prabal.loanservice.events;

import java.util.UUID;

import com.prabal.loanservice.domain.model.BaseAggregateRoot;

/**
 * Base abstract class for Event
 * 
 * @author Prabal Nandi
 *
 */
public abstract class Event {
	private UUID id = UUID.randomUUID();
	private int version = 1;
	protected String finalPrincipalAmount;

	/**
	 * Apply this Event on the Aggregate object
	 * 
	 * @param aggregate
	 */
	public abstract void applyOn(BaseAggregateRoot aggregate);

	public UUID getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	/**
	 * Extract the final Principal value from Event. Note: It's not a correct
	 * implementation of Event pattern, but is fine for this use case
	 * 
	 * @return
	 */
	public String getFinalPrincipalValue() {
		return this.finalPrincipalAmount;
	}

	public abstract String getAuditString();

	public abstract EventTypeEnum getEventType();
}
