package kickstart.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.salespointframework.useraccount.UserAccount;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AnswerForm {
	Boolean closed;

	@NotEmpty
	String message;

	public Boolean isClosed() {
		return closed != null && closed;
	}

	public Message toMessage(UserAccount author){
		return Message.builder()
				.author(author)
				.content(message)
				.timeSent(LocalDateTime.now())
				.build();
	}
}
