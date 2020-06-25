/**
 * 
 */
package com.prabal.loanservice.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.prabal.loanservice.events.Event;

/**
 * Base aggregate root class.
 * 
 * @author Prabal Nandi
 *
 */
public abstract class BaseAggregateRoot {
	private UUID id;
	private long versionId;
	// Stores the Pending Events which have not be saved to EventStore yet
	private List<Event> pendingEvents;

	public BaseAggregateRoot(UUID id) {
		this.id = id;
		this.versionId = -1;
		this.pendingEvents = new ArrayList<Event>();
	}

	/**
	 * Replays the Historic events form EventStore and recreate the AggregateState
	 * 
	 * @param events
	 */
	public void loadFrom(List<Event> events) {
		for (Event event : events) {
			event.applyOn(this);
			this.versionId++;
		}
	}

	/**
	 * Apply the Event to this Aggregate and save it to the list of PendingEvents.
	 * Pending events will be persisted to the EventStore later
	 * 
	 * @param event
	 */
	public void applyChange(Event event) {
		event.applyOn(this);
		// save the event to pending events store
		this.pendingEvents.add(event);
		this.versionId++;
	}

	public UUID getId() {
		return id;
	}

	public long getVersionId() {
		return versionId;
	}

	public List<Event> getPendingEvents() {
		return pendingEvents;
	}

}
