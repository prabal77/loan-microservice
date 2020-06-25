/**
 * 
 */
package com.prabal.loanservice.query.balance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prabal.loanservice.model.AccountBalance;
import com.prabal.loanservice.query.store.BalanceCheckStore;

import lombok.extern.slf4j.Slf4j;

/**
 * Singleton GetBalance Query Handler class
 * 
 * @author Prabal Nandi
 *
 */
@Service
@Slf4j
public class GetBalanceQueryHandler {

	@Autowired
	private BalanceCheckStore store;

	public AccountBalance handle(GetBalanceQuery query) {
		try {
			// If no data is passed display the latest balance
			if(query.getDate() == null) {
				return this.store.getLatestBalance(query.getAccountId());
			}else {
				return this.store.getPrincipalBalance(query.getAccountId(), query.getDate());	
			}
		} catch (Exception exception) {
			log.error("Getting query result = " + query.toString() + ". ErrorMessage = " + exception.getMessage());
			throw exception;
		}
	}
}
