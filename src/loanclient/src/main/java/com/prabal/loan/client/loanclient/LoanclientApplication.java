package com.prabal.loan.client.loanclient;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import com.prabal.loanclient.model.AccountBalance;
import com.prabal.loanclient.model.AccountInfo;
import com.prabal.loanclient.model.AddPaymentCommand;
import com.prabal.loanclient.model.CreateAccountCommand;

public class LoanclientApplication {
	private static final String BASE_URL = "http://localhost:8080/";

	public static void main(String[] args) {
		AccountInfo account1 = createLoanAcount("1000", "10", LocalDate.now());
		System.out.println(account1);
		AccountInfo account2 = createLoanAcount("2000", "10", LocalDate.now());
		System.out.println(account2);
		System.out.println(makePayment(account1.getAccountId(), "200", account1.getStartDate().plusYears(1)));
		System.out.println(makePayment(account1.getAccountId(), "250", account1.getStartDate().plusYears(2)));
		System.out.println(makePayment(account1.getAccountId(), "300", account1.getStartDate().plusYears(3)));
		System.out.println(getBalance(account1.getAccountId(), account1.getStartDate().plusYears(3)));
		System.out.println(getBalance(account1.getAccountId(), account1.getStartDate().plusYears(5)));

		// Make a backdated transaction
		System.out.println(
				makePayment(account1.getAccountId(), "150", account1.getStartDate().plusYears(1).plusMonths(6)));
		System.out.println(getBalance(account1.getAccountId(), account1.getStartDate().plusYears(3)));

		System.out.println(makePayment(account1.getAccountId(), "50", account1.getStartDate().plusMonths(6)));
		System.out.println(getBalance(account1.getAccountId(), account1.getStartDate().plusYears(3)));
		
		// get balance from second account
		System.out.println(getBalance(account2.getAccountId(), account1.getStartDate().plusYears(3)));

		System.out.println("\n\n ---- Priniting Audit Logs from the Event Store ----\n\n");
		System.out.println(getAuditLog());
	}

	public static AccountInfo createLoanAcount(String amount, String interest, LocalDate startDate) {
		CreateAccountCommand createLoan = new CreateAccountCommand();
		createLoan.setAmount(amount);
		createLoan.setInterest(interest);
		createLoan.setStartDate(startDate);
		HttpEntity<CreateAccountCommand> entity = new HttpEntity<CreateAccountCommand>(createLoan);
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForObject(BASE_URL + "loan", entity, AccountInfo.class);
	}

	public static String makePayment(UUID accountId, String amount, LocalDate paymentDate) {
		AddPaymentCommand payment = new AddPaymentCommand();
		payment.setAmount(amount);
		payment.setTransactionDate(paymentDate);
		HttpEntity<AddPaymentCommand> entity = new HttpEntity<AddPaymentCommand>(payment);
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForObject(BASE_URL + "payment/" + accountId.toString(), entity, String.class);
	}

	public static AccountBalance getBalance(UUID accountId, LocalDate balanceDate) {
		RestTemplate restTemplate = new RestTemplate();
		if (balanceDate == null) {
			return restTemplate.getForObject(BASE_URL + "balance/" + accountId.toString(), AccountBalance.class);
		} else {
			return restTemplate.getForObject(BASE_URL + "balance/" + accountId.toString(), AccountBalance.class,
					Collections.singletonMap("date", balanceDate.toString()));
		}
	}

	public static String getAuditLog() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(BASE_URL + "audit", String.class);
	}
}
