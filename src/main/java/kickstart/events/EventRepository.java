package kickstart.events;

import kickstart.users.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface EventRepository extends CrudRepository<Event, Long> {
	/**
	 * Gets all events.
	 */
	@Override
	Streamable<Event> findAll();
}