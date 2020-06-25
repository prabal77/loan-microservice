/**
 * 
 */
package com.prabal.loanservice.store.repo;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prabal.loanservice.domain.model.AccountAggregate;
import com.prabal.loanservice.model.AccountInfo;
import com.prabal.loanservice.service.AccountInfoMessageService;

/**
 * InMemory local repository to store loan accounts. This can be stored in plain
 * RDBMS, NoSQL or EventStore
 * 
 * @author Prabal Nandi
 *
 */
@Service()
public class LoanAccountRepository {

	@Autowired
	private AccountInfoMessageService service;

	private ConcurrentHashMap<UUID, AccountInfo> accountsMap;

	public LoanAccountRepository() {
		this.accountsMap = new ConcurrentHashMap<UUID, AccountInfo>();
	}

	/**
	 * Get Account account detail associated with the account id
	 * 
	 * @param accountId
	 * @return
	 */
	public AccountAggregate getLoanAccount(UUID accountId) {
		AccountInfo account = this.accountsMap.getOrDefault(accountId, null);
		if (account != null) {
			return new AccountAggregate(account);
		}
		return null;
	}

	/**
	 * Add loan Account to repository
	 * 
	 * @param account
	 */
	public void saveAccount(AccountAggregate aggregate) {
		this.accountsMap.put(aggregate.getAccountId(), aggregate.getAccountInfo());
		// Add event to the message bus for subscribed listeners
		this.service.send(aggregate.getAccountInfo());
	}
}
