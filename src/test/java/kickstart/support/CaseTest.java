package kickstart.support;

import kickstart.users.Customer;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CaseTest {

	@Test
	public void setId() {
		Customer customer = mock(Customer.class);
		Case c = new Case(2L, "Test", false, customer, new ArrayList<>());

		c.setId(3L);

		assertEquals((long) c.getId(), 3L);
	}

	@Test
	public void setTitle() {
		Customer customer = mock(Customer.class);
		Case c = new Case(2L, "Test", false, customer, new ArrayList<>());

		c.setTitle("Title");

		assertEquals("Title", c.getTitle());
	}

	@Test
	public void setClosed() {
		Customer customer = mock(Customer.class);
		Case c = new Case(2L, "Test", false, customer, new ArrayList<>());

		c.setClosed(true);

		assertTrue(c.isClosed());
	}

	@Test
	public void setCreator() {
		Customer customer = mock(Customer.class);
		Customer otherCustomer = mock(Customer.class);

		Case c = new Case(2L, "Test", false, customer, new ArrayList<>());

		c.setCreator(otherCustomer);

		assertEquals(otherCustomer, c.getCreator());
	}

	@Test
	public void setMessages() {
		Customer customer = mock(Customer.class);
		ArrayList<Message> otherMessage = new ArrayList<>();
		Message message = mock(Message.class);
		otherMessage.add(message);


		Case c = new Case(2L, "Test", false, customer, new ArrayList<>());

		c.setMessages(otherMessage);

		assertEquals(otherMessage, c.getMessages());
	}

	@Test
	public void testDefaultFalseConstructor(){
		Customer customer = mock(Customer.class);
		Case c = new Case("title", customer, new ArrayList<>());

		assertFalse(c.isClosed());
	}

	@Test
	public void testChronoOder(){
		Customer customer = mock(Customer.class);
		Message a = new Message("A", LocalDateTime.now());
		Message b = new Message("B", LocalDateTime.MAX);
		Case c = new Case("title", customer, new ArrayList<>(List.of(b,a)));

		assertEquals(a, c.getMessagesChrono().get(0));
		assertEquals(b, c.getMessagesChrono().get(1));

	}
}