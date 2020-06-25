/**
 * 
 */
package com.prabal.loanservice.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.prabal.loanservice.events.Event;

/**
 * Primitive message object. It encapsulates the aggregate id and all the events
 * generate every time aggregate is saved to EventStore
 * 
 * @author Prabal Nandi
 *
 */
public class TransactionMessageObject {
	private UUID aggregateId;
	private List<Event> events;

	public TransactionMessageObject(UUID aggregateId, List<Event> events) {
		super();
		this.aggregateId = aggregateId;
		this.events = events;
	}

	public UUID getAggregateId() {
		return aggregateId;
	}

	public List<Event> getEvents() {
		return events;
	}

	@Override
	public String toString() {
		return "MessageObject [aggregateId=" + aggregateId + ", events="
				+ events.stream().map(_a -> _a.toString()).collect(Collectors.joining("\n")) + "]";
	}

}
