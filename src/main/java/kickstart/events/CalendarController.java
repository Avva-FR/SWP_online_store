package kickstart.events;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CalendarController {
	private final EventRepository eventRepository;

	/**
	 * Shows the calendar
	 * @param model The MVC Model
	 * @return the view name.
	 */
	@GetMapping("/calendar")
	public String calendar(Model model) {
		return "calendar";
	}

	/**
	 * Gets all events as JSON for the calender to read.
	 * @param start
	 * @param end
	 * @return the view name.
	 */
	@ResponseBody
	@GetMapping("/getevents")
	public List<Event> getEvents(@RequestParam("start") @DateTimeFormat(pattern="yyyy-MM-dd") Date start,
								 @RequestParam("end") @DateTimeFormat(pattern="yyyy-MM-dd") Date end) {
		return eventRepository
				.findAll()
				.filter(e -> start.getTime() <= e.getDate().getTimeInMillis()
						&& e.getDate().getTimeInMillis() <= end.getTime())
				.toList();
	}
}
