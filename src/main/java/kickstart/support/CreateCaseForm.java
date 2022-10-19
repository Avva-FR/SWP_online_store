package kickstart.support;

import kickstart.users.Customer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CreateCaseForm {
	@NotEmpty
	private String title;

	@NotEmpty
	private String firstMessage;

	public Case toCase(Customer customer){
		return Case.builder()
				.title(title)
				.messages(List.of(Message.builder()
						.content(firstMessage)
						.timeSent(LocalDateTime.now())
						.author(customer.getUserAccount())
						.build()))
				.creator(customer)
				.build();
	}
}
