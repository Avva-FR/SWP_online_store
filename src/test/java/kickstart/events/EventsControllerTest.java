package kickstart.events;

import kickstart.catalog.ShopItemCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest
@AutoConfigureMockMvc
public class EventsControllerTest {

	@Autowired
	WebApplicationContext webApplicationContext;
	MockMvc mvc;

	@Autowired
	EventRepository eventRepository;


	@BeforeEach
	void prepare() {
		mvc = MockMvcBuilders
				.webAppContextSetup(this.webApplicationContext)
				.apply(sharedHttpSession()) // use this session across requests
				.build();
	}

	@Test
	@WithMockUser(username = "tester", roles = "CUSTOMER")
	public void getEvents() throws Exception {
		mvc.perform(get("/events"))
				.andExpect(status().isOk())
				.andExpect(view().name("events"))
				.andExpect(model().attributeExists("eventsOpen"));
	}

	@Test
	@WithMockUser(username = "tester", roles = "CUSTOMER")
	public void denyAccessForCustomer()   {
		try{
			mvc.perform(get("/addevent"));
			fail();
		} catch (AccessDeniedException denied){
		} catch (Exception e) {
		}
	}

	@Test
	@WithMockUser(username = "tester", roles = "BOSS")
	public void allowAsBoss() throws Exception {
		mvc.perform(get("/addevent"))
				.andExpect(status().isOk())
				.andExpect(view().name("addevent"));
	}
	@Test
	@WithMockUser(username = "tester", roles = "CUSTOMER")
	public void densAccessForCustomerPost()   {
		try{
			mvc.perform(post("/addevent"));
			fail();
		} catch (AccessDeniedException denied){
		} catch (Exception e) {
		}
	}

	@Test
	@WithMockUser(username = "tester", roles = "BOSS")
	public void allowAsBossPost() throws Exception {
		mvc.perform(post("/addevent")
						.param("title", "testevent")
						.param("description", "desc")
						.param("start", "2022-01-23T21:00")
						.param("end", "2022-01-23T23:00"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"))
				.andExpect(model().hasNoErrors());
	}

	@Test
	@WithMockUser(username = "tester", roles = "BOSS")
	public void denyEndBeforeStart() throws Exception {
		mvc.perform(post("/addevent")
						.param("title", "testevent")
						.param("description", "desc")
						.param("end", "2022-01-23T21:00")
						.param("start", "2022-01-23T23:00"))
				.andExpect(status().isOk())
				.andExpect(view().name("addevent"))
				.andExpect(model().hasErrors());
	}

	@Test
	@WithMockUser(username = "tester", roles = "CUSTOMER")
	public void registerWrongId() throws Exception {
		mvc.perform(get("/event/100/register"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"))
				.andExpect(model().hasNoErrors());
	}

	@Test
	@WithMockUser(username = "test", roles = "CUSTOMER")
	public void showById() throws Exception {
		Event event = eventRepository.save(new Event("test", "test", Calendar.getInstance(), Calendar.getInstance()));
		mvc.perform(get("/event/" + event.getId()))
				.andExpect(status().isOk())
				.andExpect(model().hasNoErrors())
				.andExpect(model().attribute("event", event))
				.andExpect(model().attribute("registered", false));
	}
/**
	@Test
	@WithMockUser(username = "tester", roles = "BOSS")
	public void registerNoCustomer() throws Exception {
		Event event = eventRepository.save(new Event("test", "test", Calendar.getInstance(), Calendar.getInstance()));
		mvc.perform(get("/event/" + event.getId() + "/register"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/event/" + event.getId()))
				.andExpect(model().attribute("successful", is(false)));
	}




	@Test
	@WithMockUser(username = "test", roles = "CUSTOMER")
	public void registerCustomer() throws Exception {
		Event event = eventRepository.save(new Event("test", "test", Calendar.getInstance(), Calendar.getInstance()));
		mvc.perform(get("/event/" + event.getId() + "/register"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/event/" + event.getId()))
				.andExpect(model().attribute("successful", false));

	}*/

	@Test
	public void addEventValid() {
	}
}