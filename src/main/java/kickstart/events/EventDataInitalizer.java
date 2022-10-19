package kickstart.events;

import lombok.RequiredArgsConstructor;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A {@link DataInitializer} implementation that will create dummy data for the application on application startup.
 *
 * @author Paul Henke
 * @author Oliver Gierke
 * @see DataInitializer
 */
@Component
@Order(20)
@RequiredArgsConstructor
class EventDataInitalizer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(EventDataInitalizer.class);


	private final EventRepository eventRepository;

	/*
	 * (non-Javadoc)
	 * @see org.salespointframework.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {

		if (eventRepository.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default catalog entries.");
		//Always generate in the future
		Calendar firstDate = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
		firstDate.set(Calendar.DAY_OF_MONTH, firstDate.get(Calendar.DAY_OF_MONTH) + 1);
		Calendar endDate = (Calendar) firstDate.clone();
		endDate.add(Calendar.MINUTE, 60);
		eventRepository.save(new Event("This is a very fancy event", "Tootle Lesung", firstDate, endDate));

	}
}