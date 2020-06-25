/**
 * 
 */
package com.prabal.loanservice.store;

import java.util.List;
import java.util.UUID;

import com.prabal.loanservice.events.Event;

/**
 * Interface defining an EventStore.
 * 
 * @author Prabal Nandi
 *
 */
public interface EventStore {

	/**
	 * Returns list of all events associated with the particular aggregate object
	 * 
	 * @param aggregateId
	 * @return
	 */
	List<Event> replayEvents(UUID aggregateId);

	/**
	 * Save the list of Events to EventStore
	 * @param aggregateId
	 * @param events
	 */
	void saveEvents(UUID aggregateId, List<Event> events);
}
