package kickstart.events;

import kickstart.order.ShopOrder;
import kickstart.order.ShopOrderTests;
import kickstart.users.Customer;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.salespointframework.useraccount.UserAccount;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EventTest {

	@Test
	void expectParameterNullException(){
		try {
			new Event("a", null, Calendar.getInstance(), new HashSet<>());
			fail();
		}catch (NullPointerException npe){
		}
	}
	@Test
	void setDate() {
		//Arrange
		Calendar toProvide = Calendar.getInstance();
		toProvide.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  Calendar.getInstance(), Calendar.getInstance());

		//Act
		event.setDate(toProvide);

		assertEquals(event.getDate(), toProvide);
	}

	@Test
	void testConvert() {
		//Arrange
		Calendar toProvide = Calendar.getInstance();
		toProvide.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  Calendar.getInstance(), Calendar.getInstance());

		//Act
		event.setDate(toProvide);

		assertEquals(LocalDateTime.ofInstant(toProvide.toInstant(), toProvide.getTimeZone().toZoneId()), event.asLocalDateTime());
	}

	@Test
	void endAsLocalDateTime() {
		//Arrange
		Calendar toProvide = Calendar.getInstance();
		toProvide.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  Calendar.getInstance(), Calendar.getInstance());

		//Act
		event.setEnd(toProvide);

		assertEquals(event.endAsLocalDateTime(), LocalDateTime.ofInstant(toProvide.toInstant(), toProvide.getTimeZone().toZoneId()));

	}

	@Test
	void getMinutesDuration() {
		Calendar end = Calendar.getInstance();
		end.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  Calendar.getInstance(), end);

		assertEquals(event.getMinutesDuration(), 60);
	}

	@Test
	void isInFuture() {
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  start, Calendar.getInstance());

		assertTrue(event.isInFuture());
	}

	@Test
	void register() {
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  start, Calendar.getInstance());
		Customer customer = mock(Customer.class);

		event.register(customer);

		assertTrue(event.getParticipants().contains(customer));
	}

	@Test
	void unregister() {
		Customer customer = mock(Customer.class);
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  start, Calendar.getInstance(), new HashSet<>(Set.of(customer)));

		event.unregister(customer);

		assertTrue(!event.getParticipants().contains(customer));
	}

	@Test
	void getRegistrations() {
		Customer customer = mock(Customer.class);
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  start, Calendar.getInstance(), new HashSet<>(Set.of(customer)));

		assertEquals(event.getRegistrations(), 1);


	}

	@Test
	void getId() {
		Event event = new Event("Test", "test",   Calendar.getInstance(), Calendar.getInstance());

		event.setId(3L);

		assertEquals(event.getId(), 3L);
	}

	@Test
	void getDescription() {
		Event event = new Event("Test", "test",  Calendar.getInstance(), Calendar.getInstance());

		event.setDescription("other");

		assertEquals(event.getDescription(), "other");
	}

	@Test
	void getTitle() {
		Event event = new Event("Test", "test",  Calendar.getInstance(), Calendar.getInstance());

		event.setTitle("other");

		assertEquals(event.getTitle(), "other");
	}

	@Test
	void getDate() {

		Calendar start = Calendar.getInstance();
		start.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  Calendar.getInstance(), Calendar.getInstance());

		event.setDate(start);

		assertEquals(event.getDate(), start);
	}

	@Test
	void getEnd() {
		Calendar end = Calendar.getInstance();
		end.add(Calendar.MINUTE, 60);
		Event event = new Event("Test", "test",  Calendar.getInstance(), Calendar.getInstance());

		event.setEnd(end);

		assertEquals(event.getEnd(), end);
	}

	@Test
	void isAllDay() {

		Event event = new Event("Test", "test",  Calendar.getInstance(), Calendar.getInstance());

		event.setAllDay(true);

		assertTrue(event.isAllDay());
	}

	@Test
	void setParticipants() {
		Event event = new Event("Test", "test",  Calendar.getInstance(), Calendar.getInstance());
		Customer customer = mock(Customer.class);
		HashSet set = new HashSet(Set.of(customer));

		event.setParticipants(set);

		assertEquals(event.getParticipants(), set);
	}
}