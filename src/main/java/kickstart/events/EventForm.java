package kickstart.events;

import lombok.*;
import org.apache.tomcat.jni.Local;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
public class EventForm {
	private String start;

	private String end;

	@NotEmpty
	private String title;

	@NotEmpty
	private String description;

	@SneakyThrows
	/**
	 * Converts the form to an actual event.
	 */
	public Event toEvent(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(sdf.parse(this.start));
		end.setTime(sdf.parse(this.end));
		return new Event(description, title, start, end);
	}
}
