package kickstart.events;

import kickstart.users.Customer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String description;
	private String title;

	private Calendar date;
	private Calendar end;
	private boolean allDay; // in case an event takes the whole day

	@ManyToMany
	@Setter
	@CollectionTable(name = "event_registered")
	private Set<Customer> participants;

	public Event(String description, String title, Calendar date, Set<Customer> participants) {
		this.description = Objects.requireNonNull(description);
		this.date = Objects.requireNonNull(date);
		this.allDay = true;
		this.participants = Objects.requireNonNull(participants);
		this.end = Calendar.getInstance();
		this.title = Objects.requireNonNull(title);
	}

	/**
	 * Creates a new event with predefined customers
	 * @param description The description of the Event.
	 * @param title The title of the event.
	 * @param date The start of the event.
	 * @param end The end of the event
	 * @param participants The predefined participants
	 */
	public Event(String description, String title, Calendar date, Calendar end, Set<Customer> participants) {
		this.description = Objects.requireNonNull(description);
		this.date = Objects.requireNonNull(date);
		this.title = Objects.requireNonNull(title);
		this.end = Objects.requireNonNull(end);
		this.allDay = false;
		this.participants = Objects.requireNonNull(participants);
	}

	/**
	 * Creates a new event with no customers
	 * @param description The description of the Event.
	 * @param title The title of the event.
	 * @param date The start of the event.
	 * @param end The end of the event
	 */
	public Event(String description, String title, Calendar date, Calendar end) {
		this.description = Objects.requireNonNull(description);
		this.date = Objects.requireNonNull(date);
		this.title = Objects.requireNonNull(title);
		this.end = Objects.requireNonNull(end);
		this.allDay = false;
		participants = new HashSet<>();
	}

	/**
	 * Gets the start as {@link LocalDateTime}
	 * @return The start as {@link LocalDateTime}
	 */
	public LocalDateTime asLocalDateTime(){
		return LocalDateTime.ofInstant(date.toInstant(), date.getTimeZone().toZoneId());
	}

	/**
	 * Gets the end as {@link LocalDateTime}
	 * @return The end as {@link LocalDateTime}
	 */
	public LocalDateTime endAsLocalDateTime(){
		return LocalDateTime.ofInstant(this.end.toInstant(), this.end.getTimeZone().toZoneId());
	}

	/**
	 * Gets the duration of the event in minutes
	 * @return The duration in minutes
	 */
	public long getMinutesDuration(){
		return TimeUnit.MILLISECONDS.toMinutes(end.getTimeInMillis() - date.getTimeInMillis());
	}

	/***
	 * Whether the event is in the future
	 * @return true if the event is in the future false otherwise
	 */
	public boolean isInFuture(){
		return date.after(Calendar.getInstance());
	}

	/**
	 * Adds an Customer to the participants for this event-
	 * @param customer
	 */
	public void register(Customer customer){
		participants.add(customer);
	}

	/**
	 * Removes a Customer from the participants for this event.
	 * @param customer the customer to unregister
	 * @return Whether the user was registered before
	 */
	public boolean unregister(Customer customer){
		return participants.remove(customer);
	}

	/**
	 * Gets the amount of participants
	 * @return The amount of participants
	 */
	public int getRegistrations(){
		return participants.size();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Event event = (Event) o;
		return allDay == event.allDay && id.equals(event.id) && Objects.equals(description, event.description)
				&& Objects.equals(title, event.title) && Objects.equals(date, event.date)
				&& Objects.equals(end, event.end) && Objects.equals(participants, event.participants);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, description, title, date, end, allDay, participants);
	}
}
