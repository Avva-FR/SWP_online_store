package kickstart.events;

import kickstart.users.Customer;
import kickstart.users.UserManagement;

import lombok.RequiredArgsConstructor;

import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class EventsController {

	private final EventRepository events;
	private final UserManagement userManagement;

	/**
	 * Displays all {@link InventoryItem}s in the system
	 *
	 * @param model will never be {@literal null}.
	 * @return the view name. the view name.
	 */
	@GetMapping("/events")
	@PreAuthorize("isAuthenticated()")
	String event(Model model) {
		List<Event> events = this.events.findAll().stream().collect(Collectors.toList());
		model.addAttribute("eventsOpen", events);
		return "events";
	}

	/**
	 * Shows details for an event
	 * @param optionalAccount
	 * @param id
	 * @param model
	 * @return the view name.
	 */
	@GetMapping("/event/{id}")
	String eventDetail(@LoggedIn Optional<UserAccount> optionalAccount, @PathVariable long id, Model model) {
		Optional<Event> optionalEvent = events.findById(id);
		if (optionalEvent.isEmpty()) {
			return "redirect:/events";
		}

		Event event = optionalEvent.get();
		model.addAttribute("event", event);
		if (optionalAccount.isPresent()){
			Optional<Customer> optionalCustomer = userManagement.findCustomer(optionalAccount.get());
			if (optionalCustomer.isPresent()){
				Set<Customer> participants = event.getParticipants();
				if (participants.contains(optionalCustomer.get())) {
					model.addAttribute("registered", true);
				}else{
					model.addAttribute("registered", false);
				}
			}else {
				model.addAttribute("registered", false);
			}
		} else {
			model.addAttribute("registered", false);
		}

		model.addAttribute("successful", false);
		model.addAttribute("nobutton", true);

		return "eventdetail";
	}

	/**
	 * Registers the current user for an event
	 * @param optAcc the event of the logged in user
	 * @param id the id of the event to register for
	 * @param model the model of the mvc
	 * @return the view name.
	 */
	@GetMapping("/event/{id}/register")
	@PreAuthorize("isAuthenticated()")
	String register(@LoggedIn Optional<UserAccount> optAcc, @PathVariable long id, Model model) {
		Optional<Event> optionalEvent = events.findById(id);
		if (!optionalEvent.isPresent() || !optAcc.isPresent()) {
			return "redirect:/events";
		}

		Event event = optionalEvent.get();
		model.addAttribute("id", event.getId());
		model.addAttribute("event", event);

		Optional<Customer> optionalCustomer = userManagement.findCustomer(optAcc.get());
		if (optionalCustomer.isPresent()) {
			Set<Customer> old = event.getParticipants();
			old.add(optionalCustomer.get());
			event.setParticipants(old);
			events.save(event);
			model.addAttribute("successful", true);
		} else {
			model.addAttribute("successful", false);
		}

		return "redirect:/event/{id}";
	}

	/**
	 * Unregisters an customer from an event
	 * @param optAcc The account of the logged in user
	 * @param id Id of the event
	 * @param model The MVC Model
	 * @return the view name.
	 */
	@GetMapping("/event/{id}/unregister")
	@PreAuthorize("isAuthenticated()")
	String unregister(@LoggedIn Optional<UserAccount> optAcc, @PathVariable long id, Model model) {
		Optional<Event> optionalEvent = events.findById(id);

		if (!optionalEvent.isPresent() || !optAcc.isPresent()) {
			return "redirect:/events";
		}

		Event event = optionalEvent.get();
		model.addAttribute("id", event.getId());
		model.addAttribute("event", event);

		Optional<Customer> optionalCustomer = userManagement.findCustomer(optAcc.get());
		if (optionalCustomer.isPresent()) {
			Set<Customer> old = event.getParticipants();
			old.remove(optionalCustomer.get());
			event.setParticipants(old);
			events.save(event);
			model.addAttribute("successful", true);
		} else {
			model.addAttribute("successful", false);
			return "redirect:/event/{id}";
		}
		return "redirect:/event/{id}";
	}

	/**
	 * Presents the add event form
	 * @param model The MVC Model
	 * @param eventForm
	 * @return the view name.
	 */
	@GetMapping("/addevent")
	@PreAuthorize("hasRole('EMPLOYEE')" + "||hasRole('BOSS')")
	String addEvent(Model model, EventForm eventForm) {
		return "addevent";
	}

	/**
	 * Adds an event
	 * @param model The MVC Model
	 * @param eventForm The form with the data
	 * @param result The errors
	 * @return the view name. 
	 */
	@PostMapping("/addevent")
	@PreAuthorize("hasRole('EMPLOYEE')" + "||hasRole('BOSS')")
	String addEventValid(Model model, @Valid EventForm eventForm, Errors result) {
		Event entity = eventForm.toEvent();

		if (!entity.getDate().before(entity.getEnd())) {
			result.rejectValue("start", "events.BeginBeforeEnd");
			return "addevent";
		}
		events.save(entity);
		return "redirect:/events";
	}
}
