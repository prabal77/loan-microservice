/**
 * 
 */
package com.prabal.loanservice.command.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prabal.loanservice.controller.InvalidDataException;
import com.prabal.loanservice.controller.LoanAccountNotFoundException;
import com.prabal.loanservice.domain.model.AccountAggregate;
import com.prabal.loanservice.domain.model.TransactionAggregate;
import com.prabal.loanservice.events.AddPaymentEvent;
import com.prabal.loanservice.store.repo.LoanAccountRepository;
import com.prabal.loanservice.store.repo.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Command handler class for add payment operation
 * 
 * @author Prabal Nandi
 *
 */
@Service()
@Slf4j
public class AddPaymentCommandHandler {
	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private LoanAccountRepository accountRepo;

	public AddPaymentCommandHandler() {
	}

	/**
	 * Handles the addPaymentCommand as received form the User
	 * 
	 * @param command
	 * @return
	 */
	public TransactionInfo handle(AddPaymentCommand command) {
		try {
			log.debug("AddPayment Start " + command.toString());
			AccountAggregate accountAggregate = this.accountRepo.getLoanAccount(command.getAccountId());
			// If Account is not present throw error
			if (accountAggregate == null) {
				throw new LoanAccountNotFoundException(command.getAccountId());
			}

			TransactionAggregate transactionAggregate = this.transactionRepo.getAggregateObject(command.getAccountId());
			// add the account info to transaction aggregate
			transactionAggregate.setAccountInfo(accountAggregate.getAccountInfo());
			// Apply AddPayment event to the aggregate
			transactionAggregate.applyChange(new AddPaymentEvent(accountAggregate.getAccountId(), command.getAmount(),
					command.getTransactionDate()));

			// If events were reverted by this transaction, Apply them again now
			if (transactionAggregate.getPreviousEventsForReconciliation() != null
					&& transactionAggregate.getPreviousEventsForReconciliation().size() > 0) {

				transactionAggregate.getPreviousEventsForReconciliation().stream().forEach(_event -> {
					transactionAggregate.applyChange(_event);
				});
			}
			// Save data to EventStore
			this.transactionRepo.saveData(accountAggregate.getAccountId(), transactionAggregate);

			log.debug("CreateNewLoanAccount End " + command.toString());
			return new TransactionInfo(transactionAggregate.getId());

		} catch (InvalidDataException e) {
			log.error("Invalid data " + e.getMessage());
			throw e;
		} catch (LoanAccountNotFoundException execption) {
			log.error("Account not found " + command.getAccountId().toString());
			throw execption;
		} catch (Exception exception) {
			log.error("AddNewPayment Operation failed. Error message " + exception.getMessage() + " Command = "
					+ command.toString());
			return new TransactionInfo("Unable to process the payment");
		}
	}
}
