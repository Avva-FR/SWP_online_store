package kickstart.support;

import org.h2.engine.User;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnswerFormTest {


	@Test
	public void toMessage() {
		UserAccount mockAccount = mock(UserAccount.class);
		AnswerForm answerForm = new AnswerForm(false, "Neue Nachricht");

		Message m = answerForm.toMessage(mockAccount);

		assertEquals("Neue Nachricht", m.getContent());
	}

	@Test
	public void setClosed() {
		AnswerForm answerForm = new AnswerForm(false, "Neue Nachricht");

		answerForm.setClosed(true);

		assertTrue(answerForm.isClosed());
	}

	@Test
	public void setMessage() {
		AnswerForm answerForm = new AnswerForm(false, "Neue Nachricht");

		answerForm.setMessage("Test");

		assertEquals("Test", answerForm.getMessage());
	}
}