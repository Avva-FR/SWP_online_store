package kickstart.events;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CalendarControllerTests {
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	MockMvc mvc;
	@Autowired
	EventRepository eventRepository;

	@Test
	void calendarViewExist() throws Exception {
		mvc.perform(get("/calendar"))
				.andExpect(status().isOk())
				.andExpect(view().name("calendar"));
	}

	@Test
	void testApiEndpoint() throws Exception {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date start = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
		Date end = calendar.getTime();

		mvc.perform(get("/getevents")
				.param("start", sdf.format(start))
				.param("end", sdf.format(end))
		)
				.andExpect(status().isOk());
		// it's unlikely that Spring's @ResponseBody sends invalid JSON
		// maybe we add a test another time, but for now this is ok
	}
}
