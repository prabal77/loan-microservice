/**
 * 
 */
package com.prabal.loanservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.prabal.loanservice.controller.InvalidDataException;
import com.prabal.loanservice.events.AddPaymentEvent;
import com.prabal.loanservice.events.CreateLoanEvent;
import com.prabal.loanservice.events.Event;
import com.prabal.loanservice.events.EventTypeEnum;
import com.prabal.loanservice.events.PaymentReversalEvent;
import com.prabal.loanservice.model.AccountInfo;
import com.prabal.loanservice.util.InterestCalculator;

/**
 * Aggregate Object definition for each Transaction Event Chain. Holds a list of
 * all transactions performed against a particular account.
 * 
 * All actions related to transactions are performed in this class.
 * 
 * @author Prabal Nandi
 *
 */
public class TransactionAggregate extends BaseAggregateRoot {

	private AccountInfo account;
	private List<TransactionDataWrapper> transactionList;

	private List<Event> previousValidEvents = new LinkedList<Event>();

	public TransactionAggregate(UUID id, List<Event> events) {
		super(id);
		this.loadFrom(events);
	}

	public TransactionAggregate(UUID id) {
		super(id);
	}

	/**
	 * Static factory method to create a new TransactionAggregate. It also adds a
	 * new CreateLoanEvent to the transactions event store
	 * 
	 * @param accountId
	 * @param amount
	 * @param paymentDate
	 * @return
	 */
	public static TransactionAggregate createFirstTransaction(UUID accountId, String amount, String interestRate,
			LocalDate paymentDate) {
		// Create a new "CreateLoanEvent"
		CreateLoanEvent createLoanEvent = new CreateLoanEvent(accountId, amount, interestRate, paymentDate);
		// Create a new TransactionAggregate object
		TransactionAggregate aggregate = new TransactionAggregate(accountId);
		// Apply the new "CreateLoanEvent" to the newly created Aggregate object
		aggregate.applyChange(createLoanEvent);
		return aggregate;
	}

	/**
	 * Creates a new Loan Account, deletes the existing account data and clears up
	 * the pending transaction event if any This will not clear already persisted
	 * transaction event in event store
	 * 
	 * @param event
	 */
	public void addFirstTransaction(CreateLoanEvent event) {
		// Internally used AccountInfoObject
		this.account = new AccountInfo(event.getAccountId(), event.getLoanAmount(), event.getInterestRate(),
				event.getStartDate());
		this.transactionList = new ArrayList<TransactionDataWrapper>();

		this.transactionList.add(new TransactionDataWrapper(event, event.getStartDate(), BigDecimal.ZERO.toString(),
				event.getFinalPrincipalValue()));
	}

	/**
	 * Add the new payment against Loan account. Adjust any payment made with future
	 * payment date with respect to this payment.
	 * 
	 * @param event
	 */
	public void addPayment(AddPaymentEvent event) {
		if (!this.isPaymentDateValid(event.getPaymentDate())) {
			throw new InvalidDataException("Payment date cannot be before loan account start date");
		}

		// Iterate over "transactionList" backwards and revert all payments with payment
		// date after this PaymentEvent (exception first transaction).
		// Once all previous payments are reverted, apply this payment, which will alter
		// the current principal amount.
		// Then re-apply the payments which were reverted
		List<PaymentReversalEvent> reversalEventList = new LinkedList<PaymentReversalEvent>();
		this.previousValidEvents = new LinkedList<Event>();
		TransactionDataWrapper lastValidTransaction = null;

		// Maintain a hash set to excluded computing reverted transaction again
		Set<UUID> revertedEvents = new HashSet<UUID>();

		for (int i = this.transactionList.size() - 1; i >= 0; i--) {
			TransactionDataWrapper existingEvent = this.transactionList.get(i);

			// If this event was a reversal event then skip this and in process also we will
			// be skipping the actual event which was reverted by this
			if (existingEvent.getSourceEvent().getEventType() == EventTypeEnum.REVERSAL) {
				revertedEvents.add(((PaymentReversalEvent) existingEvent.getSourceEvent()).getRevertedTransactionId());
				continue;
			}
			// Skip the reverted transaction
			if (revertedEvents.contains(existingEvent.getSourceEvent().getId())) {
				continue;
			}

			if (!existingEvent.getTransactionDate().isEqual(event.getPaymentDate())
					&& existingEvent.getTransactionDate().isAfter(event.getPaymentDate())) {

				// Create a reversal event and append to the reversalEventList
				reversalEventList.add(new PaymentReversalEvent(this.account.getAccountId(), existingEvent));
				this.previousValidEvents.add(existingEvent.getSourceEvent());
			} else {
				// Found the last transaction with date less than equal to current event date,
				// break out of
				// the loop
				lastValidTransaction = existingEvent;
				break;
			}
		}

		// Apply the reversal Events first
		reversalEventList.stream().forEach((PaymentReversalEvent _event) -> this.applyChange(_event));
		// reverse the compensation transaction list, as it will be played in original
		// sequence
		Collections.reverse(this.previousValidEvents);
		// Now apply the current event (calculate interest based on the last valid
		// Transaction)
		this.creditPayment(event, lastValidTransaction);
	}

	/**
	 * Apply Transaction to the TransactionStore
	 * 
	 * @param event
	 */
	public void reconcileTransaction(Event event) {
		if (event instanceof AddPaymentEvent) {
			this.creditPayment((AddPaymentEvent) event, null);
		} else if (event instanceof PaymentReversalEvent) {
			this.applyReversalTransaction((PaymentReversalEvent) event);
		}
	}

	/**
	 * Re apply the Payment reversal Transaction
	 * 
	 * @param event
	 */
	private void applyReversalTransaction(PaymentReversalEvent event) {
		TransactionDataWrapper lastTransaction = this.latestTransaction();
		this.transactionList.add(new TransactionDataWrapper(event, event.getTransactionDate(),
				lastTransaction.getFinalPrincipalAmount(), event.getFinalPrincipalValue()));
	}

	/**
	 * Apply the addPayment event to the balance amount and get the next
	 * TransactionDataWrapper object
	 * 
	 * @param event
	 * @return TransactionDataWrapper
	 */
	public void creditPayment(AddPaymentEvent event, TransactionDataWrapper lastTransaction) {
		// If last valid transaction is not passed, take it from the list
		if (lastTransaction == null) {
			// Get latest record
			lastTransaction = this.latestTransaction();
		}

		String totalAmount = null;
		// Short circuit if both the dates are same (No Interest)
		if (event.getPaymentDate().isEqual(lastTransaction.getTransactionDate())) {
			totalAmount = lastTransaction.getFinalPrincipalAmount();
		} else {
			// calculate applicable interest
			totalAmount = InterestCalculator.calculateSimpleInterest(lastTransaction.getTransactionDate(),
					event.getPaymentDate(), lastTransaction.getFinalPrincipalAmount(), this.account.getInterestRate());
		}
		// Reject payment if it is more than balance (plus interest) as Account will go
		// to negative
		// As part of this requirement, i am not allowing negative loan balance
		if (new BigDecimal(totalAmount).subtract(new BigDecimal(event.getAmount())).signum() == -1) {
			throw new InvalidDataException("Transaction amount more than balance amount");
		}
		// Debit the Payment from total payable amount.
		String finalAmount = new BigDecimal(totalAmount).subtract(new BigDecimal(event.getAmount())).toString();
		event.setFinalPrincipalValue(finalAmount);

		this.transactionList.add(new TransactionDataWrapper(event, event.getPaymentDate(),
				lastTransaction.getFinalPrincipalAmount(), finalAmount));
	}

	/**
	 * Get the latest principal amount and transaction date
	 * 
	 * @return
	 */
	private TransactionDataWrapper latestTransaction() {
		return this.transactionList.get(this.transactionList.size() - 1);
	}

	/**
	 * Check if the payment transaction date is after loan account start date.
	 * 
	 * @param paymentDate
	 * @return
	 */
	public boolean isPaymentDateValid(LocalDate paymentDate) {
		return this.account.getStartDate().isBefore(paymentDate);
	}

	/**
	 * Save Account Info in the aggregate object. Required for Interest calculation
	 * 
	 * @param accountInfo
	 */
	public void setAccountInfo(AccountInfo accountInfo) {
		this.account = accountInfo;
	}

	/**
	 * Expose the list of reverted event which needs to be reconciled
	 * 
	 * @return
	 */
	public List<Event> getPreviousEventsForReconciliation() {
		return Collections.unmodifiableList(this.previousValidEvents);
	}

	public List<TransactionDataWrapper> getTransactionWrappers() {
		return this.transactionList;
	}
}
