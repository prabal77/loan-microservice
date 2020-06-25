/**
 * 
 */
package com.prabal.loanservice.command.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.prabal.loanservice.common.TransactionTypeEnum;
import com.prabal.loanservice.domain.model.AccountAggregate;
import com.prabal.loanservice.domain.model.TransactionAggregate;
import com.prabal.loanservice.events.AddPaymentEvent;
import com.prabal.loanservice.events.CreateLoanEvent;
import com.prabal.loanservice.events.Event;
import com.prabal.loanservice.events.EventTypeEnum;
import com.prabal.loanservice.model.AccountInfo;
import com.prabal.loanservice.service.TransactionMessageObject;
import com.prabal.loanservice.service.TransactionMessageService;
import com.prabal.loanservice.store.repo.LoanAccountRepository;
import com.prabal.loanservice.store.repo.TransactionRepository;

import io.reactivex.schedulers.TestScheduler;

/**
 * @author Prabal Nandi
 *
 */
@SpringBootTest
class AddPaymentCommandHandlerTest {

	@Autowired
	private AddPaymentCommandHandler commandHandler;

	@Autowired
	private TransactionMessageService messageService;

	@Autowired
	private LoanAccountRepository accountRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	private AccountInfo account;

	private List<TransactionMessageObject> messageList;
	private TestScheduler scheduler;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.scheduler = new TestScheduler();
		this.messageList = new ArrayList<TransactionMessageObject>();
		// subscribe to the message list
		this.messageService.toObservable().subscribe(_obj -> this.messageList.add(_obj));

		AccountAggregate aggregate = AccountAggregate.createLoanAccount("1000", "10", LocalDate.now());
		this.account = aggregate.getAccountInfo();
		this.accountRepo.saveAccount(aggregate);

		TransactionAggregate trans = TransactionAggregate.createFirstTransaction(this.account.getAccountId(),
				this.account.getLoanAmount(), this.account.getInterestRate(), this.account.getStartDate());
		this.transactionRepo.saveData(this.account.getAccountId(), trans);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void testInitialTransaction() {
		this.scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		assertEquals(1, this.messageList.size());
		assertEquals(this.account.getAccountId(), this.messageList.get(0).getAggregateId());
		assertEquals(1, this.messageList.get(0).getEvents().size());
		CreateLoanEvent event = (CreateLoanEvent) this.messageList.get(0).getEvents().get(0);
		this.testCreateLoanEvent(event, this.account);
	}

	/**
	 * Test method for
	 * {@link com.prabal.loanservice.command.payment.AddPaymentCommandHandler#handle(com.prabal.loanservice.command.payment.AddPaymentCommand)}.
	 */
	@Test
	void testTransactionHandler() {
		String amount = "50";
		LocalDate transDate = this.account.getStartDate().plusYears(1);
		AddPaymentCommand command = new AddPaymentCommand();
		command.setAccountId(this.account.getAccountId());
		command.setAmount(amount);
		command.setTransactionDate(transDate);
		this.commandHandler.handle(command);
		this.scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		assertEquals(2, this.messageList.size());
		assertEquals(1, this.messageList.get(1).getEvents().size());
		AddPaymentEvent event = (AddPaymentEvent) this.messageList.get(1).getEvents().get(0);
		// Principal should still be same,as payment is less than the interest
		this.testAddPaymentEvent(event, amount, "1050.00", transDate);

		// Call second time
		transDate = this.account.getStartDate().plusYears(2);
		command = new AddPaymentCommand();
		command.setAccountId(this.account.getAccountId());
		command.setAmount(amount);
		command.setTransactionDate(transDate);
		this.commandHandler.handle(command);
		this.scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

		assertEquals(3, this.messageList.size());
		assertEquals(1, this.messageList.get(2).getEvents().size());

		// Since Interest is not compounding
		this.testAddPaymentEvent((AddPaymentEvent) this.messageList.get(2).getEvents().get(0), amount, "1105.00",
				transDate);
	}

	@Test
	void testReversalTransaction() {
		this.addThreePayments();

		// Check final balance amount
		String previousFinalAmount = this.messageList.get(3).getEvents().get(0).getFinalPrincipalValue();
		this.testBalance("514.00", previousFinalAmount);

		// Create back dated payment of 150 after 1.5 years
		AddPaymentCommand command = new AddPaymentCommand();
		command.setAccountId(this.account.getAccountId());
		command.setAmount("150");
		command.setTransactionDate(this.account.getStartDate().plusYears(1).plusMonths(6));
		this.commandHandler.handle(command);
		this.scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

		assertEquals(5, this.messageList.size());
		// 5 events. 2 reversals, 1 actual and 2 recon payments
		assertEquals(5, this.messageList.get(4).getEvents().size());
		List<Event> events = this.messageList.get(4).getEvents();
		assertEquals(EventTypeEnum.REVERSAL, events.get(0).getEventType());
		assertEquals(EventTypeEnum.REVERSAL, events.get(1).getEventType());
		assertEquals(EventTypeEnum.ADD_PAYMENT, events.get(2).getEventType());
		assertEquals(EventTypeEnum.ADD_PAYMENT, events.get(3).getEventType());
		assertEquals(EventTypeEnum.ADD_PAYMENT, events.get(4).getEventType());

		String currentFinalAmount = this.messageList.get(4).getEvents().get(4).getFinalPrincipalValue();
		this.testBalance("342.00", currentFinalAmount);

		assertNotEquals(previousFinalAmount, currentFinalAmount);

		command = new AddPaymentCommand();
		command.setAccountId(this.account.getAccountId());
		command.setAmount("50");
		command.setTransactionDate(this.account.getStartDate().plusMonths(6));
		this.commandHandler.handle(command);
		this.scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

		assertEquals(6, this.messageList.size());
		assertEquals(9, this.messageList.get(5).getEvents().size());
		AddPaymentEvent event = (AddPaymentEvent) this.messageList.get(5).getEvents().get(8);
		this.testBalance("282.00", event.getFinalPrincipalValue());
	}

	private void addThreePayments() {
		// Add 200 after 1 year
		AddPaymentCommand command = new AddPaymentCommand();
		command.setAccountId(this.account.getAccountId());
		command.setAmount("200");
		command.setTransactionDate(this.account.getStartDate().plusYears(1));
		this.commandHandler.handle(command);
		this.scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

		// Add 250 after 2 years
		command = new AddPaymentCommand();
		command.setAccountId(this.account.getAccountId());
		command.setAmount("250");
		command.setTransactionDate(this.account.getStartDate().plusYears(2));
		this.commandHandler.handle(command);
		this.scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

		// Add 300 after 3 years
		command = new AddPaymentCommand();
		command.setAccountId(this.account.getAccountId());
		command.setAmount("300");
		command.setTransactionDate(this.account.getStartDate().plusYears(3));
		this.commandHandler.handle(command);
		this.scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
	}

	/**
	 * Test Create Loan Event
	 * 
	 * @param event
	 * @param account
	 */
	public void testCreateLoanEvent(CreateLoanEvent event, AccountInfo account) {
		assertEquals(account.getAccountId(), event.getAccountId());
		assertEquals(account.getLoanAmount(), event.getFinalPrincipalValue());
		assertEquals(account.getInterestRate(), event.getInterestRate());
		assertEquals(account.getLoanAmount(), event.getLoanAmount());
		assertEquals(account.getInterestRate(), event.getInterestRate());
		assertEquals(account.getLoanAmount(), event.getFinalPrincipalValue());
	}

	public void testAddPaymentEvent(AddPaymentEvent event, String amount, String finalPrincipalValue, LocalDate date) {
		System.out.println(event);
		assertEquals(EventTypeEnum.ADD_PAYMENT, event.getEventType());
		assertEquals(TransactionTypeEnum.CREDIT, event.getType());
		assertEquals(amount, event.getAmount());
		assertEquals(finalPrincipalValue, event.getFinalPrincipalValue());
		assertEquals(date, event.getPaymentDate());
	}

	// FLoating point might causing some precision lose
	public void testBalance(String expectedStr, String actualStr) {
		BigDecimal expected = new BigDecimal(expectedStr);
		BigDecimal actual = new BigDecimal(actualStr);
		BigDecimal precisionLose = new BigDecimal(2);
		assertTrue(expected.subtract(precisionLose).compareTo(actual) == -1);
		assertTrue(expected.add(precisionLose).compareTo(actual) == 1);
	}
}
