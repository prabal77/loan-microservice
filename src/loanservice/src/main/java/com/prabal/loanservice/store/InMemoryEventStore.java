/**
 * 
 */
package com.prabal.loanservice.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.prabal.loanservice.events.Event;

/**
 * Concrete Implementation of EventStore. This event store, saves all the Events
 * in-memory and in form of an LinkedHashMap
 * 
 * @author Prabal Nandi
 *
 */
public class InMemoryEventStore implements EventStore {



	private HashMap<UUID, List<Event>> eventMap;

	public InMemoryEventStore() {
		this.eventMap = new HashMap<UUID, List<Event>>();
	}

	/**
	 * Replay all events from event store for particular aggregate
	 */
	@Override
	public List<Event> replayEvents(UUID aggregateId) {
		return this.eventMap.getOrDefault(aggregateId, Collections.emptyList());
	}

	/**
	 * Append all passed events to the store for appropriate Aggregate Id
	 */
	@Override
	public void saveEvents(UUID aggregateId, List<Event> events) {
		this.eventMap.computeIfAbsent(aggregateId, (id) -> new ArrayList<Event>()).addAll(events);

	}
	
	/**
	 * Internal method again for testing only
	 * It exposes the internal data.
	 * 
	 * @return
	 */
	public HashMap<UUID, List<Event>> getInternalDetail(){
		return this.eventMap;
	}
}
