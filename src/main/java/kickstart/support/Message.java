package kickstart.support;

import kickstart.users.Employee;
import lombok.*;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class Message {

	@Id
	@GeneratedValue
	private Long id;

	private String content;

	private LocalDateTime timeSent;

	@ManyToOne
	private UserAccount author;

	@ManyToOne(fetch = FetchType.LAZY)
	private Case belongsTo;

	public Case getBelongsTo() {
		return belongsTo;
	}

	public void setBelongsTo(Case belongsTo) {
		this.belongsTo = belongsTo;
	}

	public Message(String content, LocalDateTime timeSent, Case belongsTo) {
		this.content = content;
		this.timeSent = timeSent;
		this.belongsTo = belongsTo;
	}

	public Message(String content, LocalDateTime timeSent) {
		this.content = content;
		this.timeSent = timeSent;
	}
}