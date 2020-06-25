/**
 * 
 */
package com.prabal.loanservice.service;

import org.springframework.stereotype.Service;

import com.prabal.loanservice.model.AccountInfo;

/**
 * Concrete implementation of the {@link MessageBusService}.
 * This message bus is pub-sub queue for AccountInfo events
 * @author Prabal Nandi
 *
 */
@Service
public class AccountInfoMessageService extends MessageBusService<AccountInfo>{

}
