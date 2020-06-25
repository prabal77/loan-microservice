/**
 * 
 */
package com.prabal.loanservice.controller;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prabal.loanservice.command.create.CreateAccountCommand;
import com.prabal.loanservice.command.create.CreateLoanAccountHandler;
import com.prabal.loanservice.command.payment.AddPaymentCommand;
import com.prabal.loanservice.command.payment.AddPaymentCommandHandler;
import com.prabal.loanservice.command.payment.TransactionInfo;
import com.prabal.loanservice.model.AccountBalance;
import com.prabal.loanservice.model.AccountInfo;
import com.prabal.loanservice.query.audit.GetAllTransactionEventsQuery;
import com.prabal.loanservice.query.balance.GetBalanceQuery;
import com.prabal.loanservice.query.balance.GetBalanceQueryHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Main controller service which exposes all the REST endpoints
 * 
 * @author Prabal Nandi
 *
 */
@RestController
@Slf4j
public class LoanServiceController {

	@Autowired
	private CreateLoanAccountHandler loanAccountHandler;

	@Autowired
	private AddPaymentCommandHandler addPaymentCommandHandler;

	@Autowired
	private GetBalanceQueryHandler getBalanceQueryHandler;
	
	@Autowired
	private GetAllTransactionEventsQuery getAuditStringsHandler;

	/**
	 * Command end point. Allows to create loan account in the system
	 * 
	 * @param command
	 * @return
	 */
	@PostMapping("/loan")
	public AccountInfo createLoan(@RequestBody @Valid CreateAccountCommand command) {
		log.debug("Received CreateLoan Command " + command.toString());
		command.validate();
		return this.loanAccountHandler.handle(command);
	}

	/**
	 * Command end point, allows to add payment to existing Loan account
	 * 
	 * @param accountId
	 * @param command
	 * @return
	 */
	@PostMapping("/payment/{id}")
	public TransactionInfo addPayment(@PathVariable(value = "id") UUID accountId,
			@RequestBody @Valid AddPaymentCommand command) {
		log.debug("Received AddPayment Command " + command.toString() + " Account Id " + accountId.toString());
		command.setAccountId(accountId);
		command.validate();
		return this.addPaymentCommandHandler.handle(command);
	}

	/**
	 * Query end point to get current balance on the current loan account. If Date
	 * is passed: Principal balance till the passed date will be displayed,
	 * otherwise principal on last payment date will be displayed
	 * 
	 * @param accountId
	 * @param date
	 * @return
	 */
	@GetMapping("/balance/{id}")
	public AccountBalance getAccountBalance(@PathVariable(value = "id") UUID accountId,
			@RequestParam(name = "date", required = false) String date) {
		LocalDate queryDate = null;
		try {
			queryDate = LocalDate.parse(date);
		} catch (Exception exception) {
			log.warn("Invalid date passed " + date);
		}
		GetBalanceQuery query = new GetBalanceQuery(accountId, queryDate);
		log.debug("Received GetAccountBalance Query " + query.toString());
		return this.getBalanceQueryHandler.handle(query);
	}

	/**
	 * API exposing audit capability. This is an internal API just for testing
	 * purpose, which returns all the events stored in EVent store
	 * 
	 * @return
	 */
	@GetMapping("/audit")
	public String auditEventStore() {
		return this.getAuditStringsHandler.handle();
	}
}
