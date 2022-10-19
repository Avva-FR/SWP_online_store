package kickstart.events;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.*;

public class EventFormTest {

	@Test
	public void toEvent() throws ParseException {
		EventForm form = new EventForm("2022-01-22T11:11", "2022-01-22T12:11", "Test", "Test");

		Event got = form.toEvent();

		assertEquals("Test", got.getTitle());
		assertEquals("Test", got.getDescription());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(sdf.parse("2022-01-22T11:11"));
		end.setTime(sdf.parse("2022-01-22T12:11"));

		assertEquals(start, got.getDate());
		assertEquals(end, got.getEnd());
	}

	@Test
	public void setStart() {
		EventForm form = new EventForm("2022-01-22T11:11", "2022-01-22T12:11", "Test", "Test");

		form.setStart("2022-01-22T11:11");

		assertEquals("2022-01-22T11:11", form.getStart());
	}

	@Test
	public void setEnd() {
		EventForm form = new EventForm("2022-01-22T11:11", "2022-01-22T12:11", "Test", "Test");

		form.setEnd("2022-01-22T11:11");

		assertEquals("2022-01-22T11:11", form.getEnd());
	}

	@Test
	public void setTitle() {
		EventForm form = new EventForm("2022-01-22T11:11", "2022-01-22T12:11", "Test", "Test");

		form.setTitle("Other");

		assertEquals("Other", form.getTitle());
	}

	@Test
	public void setDescription() {
		EventForm form = new EventForm("2022-01-22T11:11", "2022-01-22T12:11", "Test", "Test");

		form.setDescription("Other");

		assertEquals("Other", form.getDescription());
	}
}