package it.eng.opsi.cape.auditlogmanager.model.event;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Document("eventLogs")
public class AccountEventLog extends EventLog {

	@NotNull(message = "action field is mandatory")
	private AccountActionType action;

	public AccountEventLog(ZonedDateTime created, EventType type, String accountId, LegalBasis legalBasis,
			String message, AccountActionType action) {
		super(created, type, accountId, legalBasis, message);
		this.action = action;
	}

	public AccountEventLog(ZonedDateTime created, EventType type, String accountId, String message,
			AccountActionType action) {
		super(created, type, accountId, message);
		this.action = action;
	}

}
