/**
 * 
 */
package com.prabal.loanservice.command.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prabal.loanservice.domain.model.AccountAggregate;
import com.prabal.loanservice.domain.model.TransactionAggregate;
import com.prabal.loanservice.model.AccountInfo;
import com.prabal.loanservice.store.repo.LoanAccountRepository;
import com.prabal.loanservice.store.repo.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Command Handler for "Create new loan account" operation. The handler create a
 * new record in AccountStore and also adds a new TransactionEvent
 * 
 * @author Prabal Nandi
 *
 */
@Service()
@Slf4j
public class CreateLoanAccountHandler {
	@Autowired
	private LoanAccountRepository accountRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	public CreateLoanAccountHandler() {
	}

	/**
	 * Handle CreateNew Loan account command by creating new account and initiating
	 * a new transaction
	 * 
	 * @param command
	 * @return {@link CreateAccountResult}
	 */
	public AccountInfo handle(CreateAccountCommand command) {
		try {
			log.debug("CreateNewLoanAccount Start " + command.toString());
			// create new AccountAggregate Object
			AccountAggregate accountAggregate = AccountAggregate.createLoanAccount(command.getAmount(),
					command.getInterest(), command.getStartDate());

			// Create new Transaction Aggregate object
			TransactionAggregate transactionAggregate = TransactionAggregate.createFirstTransaction(
					accountAggregate.getAccountId(), command.getAmount(), command.getInterest(),
					command.getStartDate());

			// Save both the Events to EventStore
			this.accountRepo.saveAccount(accountAggregate);
			this.transactionRepo.saveData(accountAggregate.getAccountId(), transactionAggregate);

			log.info("CreateNewLoanAccount End " + command.toString());
			// Events successfully saved to EventStore
			return accountAggregate.getAccountInfo();

		} catch (Exception exception) {
			log.error("CreateNewLoanAccount Operation failed. Error message " + exception.getMessage() + " Command = "
					+ command.toString());
			throw new RuntimeException(exception.getMessage());
		}
	}
}
