/**
 * 
 */
package com.prabal.loanservice.store.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prabal.loanservice.domain.model.BaseAggregateRoot;
import com.prabal.loanservice.domain.model.TransactionAggregate;
import com.prabal.loanservice.events.Event;
import com.prabal.loanservice.service.TransactionMessageObject;
import com.prabal.loanservice.service.TransactionMessageService;
import com.prabal.loanservice.store.EventStore;
import com.prabal.loanservice.store.InMemoryEventStore;

/**
 * Repository to store all Transactions
 * 
 * @author Prabal Nandi
 *
 */
@Service()
public class TransactionRepository {
	private final EventStore eventStore;

	@Autowired
	private TransactionMessageService serviceBus;

	public TransactionRepository() {
		this.eventStore = new InMemoryEventStore();
	}

	/**
	 * Get the TransactionAggregate object of associated aggregate id
	 * 
	 * @param aggregateId
	 * @return {@link TransactionAggregate}
	 */
	public TransactionAggregate getAggregateObject(UUID aggregateId) {
		List<Event> historicEvents = this.eventStore.replayEvents(aggregateId);
		return new TransactionAggregate(aggregateId, historicEvents);
	}

	/**
	 * Persists/Appends the Events to the EventStore
	 * 
	 * @param aggregateId
	 * @param aggregate
	 */
	public void saveData(UUID aggregateId, BaseAggregateRoot aggregate) {
		this.eventStore.saveEvents(aggregateId, aggregate.getPendingEvents());
		// Emit the values to message bus
		this.serviceBus.send(new TransactionMessageObject(aggregateId, aggregate.getPendingEvents()));

	}

	/**
	 * This is an internal method only for testing. Exposes the current internal
	 * Event store object
	 * 
	 * @return
	 */
	public InMemoryEventStore getInternalStore() {
		return (InMemoryEventStore) this.eventStore;
	}
}
