/**
 * 
 */
package com.prabal.loanservice.query.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prabal.loanservice.store.repo.TransactionRepository;

/**
 * Creates audit string from all the data stored in the EventStore. Internal and
 * only for testing
 * 
 * @author Prabal Nandi
 *
 */
@Service
public class GetAllTransactionEventsQuery {

	@Autowired
	private TransactionRepository transRepo;

	public String handle() {
		StringBuilder sb = new StringBuilder();
		this.transRepo.getInternalStore().getInternalDetail().entrySet().forEach(_entry -> {
			sb.append(">>>>>>>>>>>>>>>>>>\n");
			sb.append("Audit Log for Loan Account Number: ").append(_entry.getKey().toString()).append("\n\n");
			_entry.getValue().forEach(_event -> {
				sb.append(_event.getAuditString()).append("\n");
			});
			sb.append("\n\nAudit Long end\n");
			sb.append("<<<<<<<<<<<<<<<<<<<\n\n");
		});
		return sb.toString();
	}
}
