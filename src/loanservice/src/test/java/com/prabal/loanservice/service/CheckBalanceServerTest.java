/**
 * 
 */
package com.prabal.loanservice.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.prabal.loanservice.events.AddPaymentEvent;
import com.prabal.loanservice.events.CreateLoanEvent;
import com.prabal.loanservice.events.Event;
import com.prabal.loanservice.model.AccountInfo;

/**
 * @author Prabal Nandi
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
class CheckBalanceServerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private TransactionMessageService transactionServiceBus;

	@Autowired
	private AccountInfoMessageService accountServiceBus;

	private AccountInfo testAccount;
	private List<TransactionMessageObject> transactions;

	@BeforeEach()
	public void init() {
		this.transactions = new ArrayList<TransactionMessageObject>();
		this.loadData();
		this.accountServiceBus.send(this.testAccount);
	}

	@Test
	public void invalidLoanAccount() throws Exception {
		// Incorrect loan account test
		mvc.perform(MockMvcRequestBuilders.get("/balance/" + UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()));
		// Invalid UUID passed
		mvc.perform(MockMvcRequestBuilders.get("/balance/asdasdasd").accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		// No loan account number is passed
		mvc.perform(MockMvcRequestBuilders.get("/balance/").accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	public void validAccountForDate() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/balance/" + this.testAccount.getAccountId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"principalAmount\":\"70\"")));
	}

	@Test
	public void validAccountInvalidDate() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/balance/" + this.testAccount.getAccountId() + "?date=asdasd")
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"principalAmount\":\"70\"")));

		mvc.perform(MockMvcRequestBuilders.get("/balance/" + this.testAccount.getAccountId() + "?date=" + LocalDate.MIN)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	public void validAccountWithSpecificDate() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(
				"/balance/" + this.testAccount.getAccountId() + "?date=" + this.testAccount.getStartDate().plusYears(2))
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"principalAmount\":\"60\"")));
	}

	@Test
	public void validAccountWithFutureDate() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(
				"/balance/" + this.testAccount.getAccountId() + "?date=" + this.testAccount.getStartDate().plusYears(5))
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"principalAmount\":\"84.02\"")));
	}

	/**
	 * Create and load data for testing
	 */
	private void loadData() {
		this.testAccount = new AccountInfo(UUID.randomUUID(), "1000", "10", LocalDate.now());
		this.createTransactions();
		this.transactions.forEach(_a -> this.transactionServiceBus.send(_a));
	}

	/**
	 * Create transactions
	 */
	private void createTransactions() {
		CreateLoanEvent event = new CreateLoanEvent(this.testAccount.getAccountId(), this.testAccount.getLoanAmount(),
				this.testAccount.getInterestRate(), this.testAccount.getStartDate());
		event.setFinalPrincipalValue("1000");
		TransactionMessageObject trans1 = new TransactionMessageObject(this.testAccount.getAccountId(),
				Collections.singletonList(event));
		// Add first payment after 1 year
		AddPaymentEvent event1 = new AddPaymentEvent(this.testAccount.getAccountId(), "50",
				this.testAccount.getStartDate().plusYears(1l));
		event1.setFinalPrincipalValue("50");
		TransactionMessageObject trans2 = new TransactionMessageObject(this.testAccount.getAccountId(),
				Collections.singletonList(event1));

		// Add second payment after 2nd Year
		AddPaymentEvent event2 = new AddPaymentEvent(this.testAccount.getAccountId(), "50",
				this.testAccount.getStartDate().plusYears(2l));
		event2.setFinalPrincipalValue("60");
		// Add third payment after 3rd year
		AddPaymentEvent event3 = new AddPaymentEvent(this.testAccount.getAccountId(), "50",
				this.testAccount.getStartDate().plusYears(3l));
		event3.setFinalPrincipalValue("70");

		// Batch 2nd and 3rd year payment
		ArrayList<Event> batch = new ArrayList<Event>(2);
		batch.add(event2);
		batch.add(event3);
		TransactionMessageObject trans3 = new TransactionMessageObject(this.testAccount.getAccountId(), batch);

		transactions.add(trans1);
		transactions.add(trans2);
		transactions.add(trans3);
	}

}
