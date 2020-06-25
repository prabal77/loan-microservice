/**
 * 
 */
package com.prabal.loanservice.service;

import org.springframework.stereotype.Service;

/**
 * Concrete implementation of the {@link MessageBusService}. This message bus is
 * pub-sub queue for payment transactions events
 * 
 * @author Prabal Nandi
 *
 */
@Service
public class TransactionMessageService extends MessageBusService<TransactionMessageObject> {

}
