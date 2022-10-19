package kickstart.users;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerWebIntegrationTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	UserController userController;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	EmployeeRepository employeeRepository;

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void employeemanagementMVCnotempty() throws Exception {
		mvc.perform(get("/employeemanagement"))
				.andExpect(status().isOk())
				.andExpect(view().name("employeemanagement"))
				.andExpect(model().attributeExists("employeeList"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void customerManagementMVCnotempty() throws Exception {
		mvc.perform(get("/customermanagement"))
				.andExpect(status().isOk())
				.andExpect(view().name("customermanagement"))
				.andExpect(model().attributeExists("customerList"))
				.andExpect(model().attributeExists("openOrders"));
	}

	@Test
	@WithAnonymousUser
	void getRegisterPage() throws Exception {
		mvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("registration"));
	}

	@Test
	@WithAnonymousUser
	void testRegistration() throws Exception {
		mvc.perform(post("/register")
				.param("username", "Test1")
				.param("email", "test1@test1.de")
				.param("password", "P@ssword123")
				.param("passwordValidation", "P@ssword123")
				.param("firstname", "TestVorname")
				.param("lastname", "TestNachname")
				.param("address", "address")
				.param("zip", "11111")
				.param("city", "city")
		).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/")).andExpect(model().hasNoErrors());
	}

	@Test
	@WithAnonymousUser
	void testRegistrationInvalidData() throws Exception {
		mvc.perform(post("/register")
				.param("username", "Test1")
				.param("email", "test1.de")
				.param("password", "password")
				.param("passwordValidation", "password")
				.param("firstname", "")
				.param("lastname", "TestNachname")
				.param("address", "address")
				.param("zip", "zzzzzz")
				.param("city", "city")
		).andExpect(view().name("registration")).andExpect(model().hasErrors());
	}

	@Test
	@WithAnonymousUser
	void testRegistrationTakenUsername() throws Exception {
		mvc.perform(post("/register")
				.param("username", "boss")
				.param("email", "test1@test1.de")
				.param("password", "P@ssword123")
				.param("passwordValidation", "P@ssword123")
				.param("firstname", "TestVorname")
				.param("lastname", "TestNachname")
				.param("address", "address")
				.param("zip", "11111")
				.param("city", "city")
		)
				.andExpect(view().name("registration"))
				.andExpect(model().hasErrors())
				.andExpect(model().attributeHasFieldErrors("registrationForm", "username"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testDeleteCustomer() throws Exception {
		Optional<Customer> first = customerRepository.findAll().stream().findFirst();
		Long id = first.get().getId();

		mvc.perform(post("/customermanagement")
				.param("action", "delete")
				.param("customerId", id.toString())
		).andExpect(redirectedUrl("/customermanagement"));

		assertFalse(customerRepository.findById(id).isPresent());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testDeleteNonExistingCustomer() throws Exception {
		Long count = customerRepository.findAll().stream().count();

		mvc.perform(post("/customermanagement")
				.param("action", "delete")
				.param("customerId", "8734783")
		).andExpect(redirectedUrl("/customermanagement"));

		assertEquals(count, customerRepository.findAll().stream().count());
	}

	@Test
	@WithMockUser(username = "test", roles = "CUSTOMER")
	void testGetAccountManagement() throws Exception {
		mvc.perform(get("/accountmanagement"))
				.andExpect(view().name("accountmanagement"))
				.andExpect(model().attributeExists("customer"));
	}

	@Test
	@WithMockUser(username = "test", roles = "CUSTOMER")
	void testPostAccountManagement() throws Exception {
		mvc.perform(post("/accountmanagement")
				.param("email", "testedit@testedit.de")
				.param("password", "P@ssword123")
				.param("passwordValidation", "P@ssword123")
				.param("lastname", "TestEdit")
				.param("address", "address")
				.param("zip", "11111")
				.param("city", "city")
		)
				.andExpect(view().name("accountmanagement"))
				.andExpect(model().attributeExists("customer"))
				.andExpect(model().hasNoErrors());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void getAddEmployeePage() throws Exception {
		mvc.perform(get("/addemployee"))
				.andExpect(status().isOk())
				.andExpect(view().name("addemployee"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testAddEmployee() throws Exception {
		mvc.perform(post("/addemployee")
				.param("username", "employee1")
				.param("email", "test1@employee.de")
				.param("password", "P@ssword123")
				.param("passwordValidation", "P@ssword123")
		)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/employeemanagement"))
				.andExpect(model().hasNoErrors());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testAddEmployeeInvalidData() throws Exception {
		mvc.perform(post("/addemployee")
				.param("username", "employee1")
				.param("email", "test1.de")
				.param("password", "password")
				.param("passwordValidation", "password")
		).andExpect(view().name("addemployee")).andExpect(model().hasErrors());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testAddEmployeeTakenUsername() throws Exception {
		mvc.perform(post("/addemployee")
				.param("username", "boss")
				.param("email", "test1@employee.de")
				.param("password", "P@ssword123")
				.param("passwordValidation", "P@ssword123")
		)
				.andExpect(view().name("addemployee"))
				.andExpect(model().hasErrors())
				.andExpect(model().attributeHasFieldErrors("employeeRegistrationForm", "username"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testEditEmployee() throws Exception {
		Optional<Employee> first = employeeRepository.findAll().stream().findFirst();
		Long id = first.get().getId();

		mvc.perform(post("/editemployee")
				.param("action", "edit")
				.param("employeeId", id.toString())
		)
				.andExpect(redirectedUrl("editemployee?employeeId=" + id))
				.andExpect(model().attribute("employeeId", id.toString()));

		mvc.perform(get("/editemployee")
				.param("action", "edit")
				.param("employeeId", id.toString())
		)
				.andExpect(view().name("editemployee"))
				.andExpect(model().attributeExists("employee"))
				.andExpect(model().attribute("employeeId", id));

		mvc.perform(post("/editemployee")
				.param("employeeId", id.toString())
				.param("email", "editor@employee.de")
				.param("password", "Edit@123")
				.param("passwordValidation", "Edit@123")
		)
				.andExpect(redirectedUrl("/employeemanagement"));

	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testDeleteEmployee() throws Exception {
		Optional<Employee> first = employeeRepository.findAll().stream().findFirst();
		Long id = first.get().getId();

		mvc.perform(post("/editemployee")
				.param("action", "delete")
				.param("employeeId", id.toString())
		).andExpect(redirectedUrl("/employeemanagement"));

		assertFalse(employeeRepository.findById(id).isPresent());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testDeleteNonExistingEmployee() throws Exception {
		Long count = employeeRepository.findAll().stream().count();

		mvc.perform(post("/editemployee")
				.param("action", "delete")
				.param("employeeId", "457386")
		).andExpect(redirectedUrl("/employeemanagement"));

		assertEquals(count, employeeRepository.findAll().stream().count());
	}
}