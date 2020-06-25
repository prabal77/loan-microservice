/**
 * 
 */
package com.prabal.loanservice.query.store;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prabal.loanservice.controller.InvalidDataException;
import com.prabal.loanservice.controller.LoanAccountNotFoundException;
import com.prabal.loanservice.events.AddPaymentEvent;
import com.prabal.loanservice.events.CreateLoanEvent;
import com.prabal.loanservice.events.PaymentReversalEvent;
import com.prabal.loanservice.model.AccountBalance;
import com.prabal.loanservice.model.AccountInfo;
import com.prabal.loanservice.service.AccountInfoMessageService;
import com.prabal.loanservice.service.TransactionMessageObject;
import com.prabal.loanservice.service.TransactionMessageService;
import com.prabal.loanservice.util.InterestCalculator;

import lombok.extern.slf4j.Slf4j;

/**
 * InMemory read-store for storing principal balance amount against any account
 * id. This is an eventually consistent read-store, which subscribes to the
 * MessageBus
 * 
 * @author Prabal Nandi
 *
 */
@Service
@Slf4j
public class BalanceCheckStore {

	@Autowired
	private TransactionMessageService transactionServiceBus;

	@Autowired
	private AccountInfoMessageService accountServiceBus;

	private Map<UUID, TreeMap<LocalDate, AccountBalance>> store = new HashMap<>();

	private Map<UUID, AccountInfo> accountStore = new HashMap<UUID, AccountInfo>();

	public BalanceCheckStore() {
	}

	@PostConstruct
	public void init() {
		this.transactionServiceBus.toObservable().subscribe(this::recreateView);
		this.accountServiceBus.toObservable().subscribe(this::updateAccountInfo);
	}

	/**
	 * Update the internal AccountInfo Store with values from message bus
	 * 
	 * @param accountInfo
	 */
	private void updateAccountInfo(AccountInfo accountInfo) {
		this.accountStore.computeIfAbsent(accountInfo.getAccountId(), _id -> accountInfo);
	}

	/**
	 * With a new message from MessageBus, this method updates the read-only store
	 * 
	 * @param message
	 */
	private void recreateView(TransactionMessageObject message) {
		log.debug("Transaction received " + message.getAggregateId());
		TreeMap<LocalDate, AccountBalance> map = this.store.computeIfAbsent(message.getAggregateId(),
				_id -> new TreeMap<LocalDate, AccountBalance>());
		// Iterate over each Event and update the local read store
		message.getEvents().stream().forEach(_event -> {
			AccountBalance updatedBalance = null;
			LocalDate transactionDate = null;
			// Generalization of Event interface will avoid this if-else checks
			if (_event instanceof CreateLoanEvent) {
				transactionDate = ((CreateLoanEvent) _event).getStartDate();
				updatedBalance = new AccountBalance(((CreateLoanEvent) _event).getAccountId(),
						((CreateLoanEvent) _event).getStartDate(), _event.getFinalPrincipalValue());
			} else if (_event instanceof PaymentReversalEvent) {
				transactionDate = ((PaymentReversalEvent) _event).getTransactionDate();
				updatedBalance = new AccountBalance(((PaymentReversalEvent) _event).getAccountId(),
						((PaymentReversalEvent) _event).getTransactionDate(), _event.getFinalPrincipalValue());

			} else if (_event instanceof AddPaymentEvent) {
				transactionDate = ((AddPaymentEvent) _event).getPaymentDate();
				updatedBalance = new AccountBalance(((AddPaymentEvent) _event).getAccountId(),
						((AddPaymentEvent) _event).getPaymentDate(), _event.getFinalPrincipalValue());
			}
			map.put(transactionDate, updatedBalance);
		});
	}

	/**
	 * Get or calculate the Principal balance of given account on a particular day
	 * 
	 * @param accountId
	 * @param date
	 * @return {@link AccountBalance}
	 */
	public AccountBalance getPrincipalBalance(UUID accountId, LocalDate date) {
		if (!this.store.containsKey(accountId) || this.store.get(accountId) == null
				|| this.store.get(accountId).size() == 0 || !this.accountStore.containsKey(accountId)) {
			throw new LoanAccountNotFoundException(accountId);
		}
		// Get Account Information object
		AccountInfo accountInfo = this.accountStore.get(accountId);
		if (date.isBefore(accountInfo.getStartDate())) {
			throw new InvalidDataException("Invalid date " + date);
		}

		TreeMap<LocalDate, AccountBalance> transactionsMap = this.store.get(accountId);
		Map.Entry<LocalDate, AccountBalance> entryObject = transactionsMap.floorEntry(date);

		// If the date is same as the key, then we have the principal balance, otherwise
		// we have to calculate based on the last principal amount
		if (entryObject != null && entryObject.getKey().compareTo(date) == 0) {
			return entryObject.getValue();
		} else {
			// calculate the principal amount
			String totalAmount = InterestCalculator.calculateSimpleInterest(entryObject.getValue().getDate(), date,
					entryObject.getValue().getPrincipalAmount(), accountInfo.getInterestRate());

			// Account balance should be last principal plus interest added to it
			return new AccountBalance(accountInfo.getAccountId(), date, totalAmount);
		}
	}

	/**
	 * Returns the latest account balance for the Account Id
	 * 
	 * @param accountId
	 * @return
	 */
	public AccountBalance getLatestBalance(UUID accountId) {
		if (!this.store.containsKey(accountId) || this.store.get(accountId) == null
				|| this.store.get(accountId).size() == 0 || !this.accountStore.containsKey(accountId)) {
			throw new LoanAccountNotFoundException(accountId);
		}
		return this.store.get(accountId).lastEntry().getValue();
	}
}
