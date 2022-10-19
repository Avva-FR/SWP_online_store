package kickstart.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.salespointframework.useraccount.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;

import java.util.HashMap;
import java.util.Optional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserManagementTests {
	private UserAccountManagement userAccountManager;
	private CustomerRepository customerRepository;
	private EmployeeRepository employeeRepository;
	private UserManagement userManagement;

	@BeforeEach
	void prepareData() {
		// Mocks
		userAccountManager = mock(UserAccountManagement.class);

		customerRepository = mock(CustomerRepository.class);
		HashMap<Long, Customer> customers = new HashMap<>();
		when(customerRepository.save(any())).then(i -> {
			Customer customer = i.getArgument(0);
			customers.put(customer.getId(), customer);
			return customer;
		});

		when(customerRepository.findAll()).then(p -> Streamable.of(customers.values()));
		when(customerRepository.findById(anyLong())).then(p -> {
			if (!customers.containsKey(p.getArgument(0))) {
				return Optional.empty();
			}
			return Optional.of(customers.get(p.getArgument(0)));
		});
		doAnswer(p -> customers.remove(((Customer) p.getArgument(0)).getId())).when(customerRepository).delete(any());

		employeeRepository = mock(EmployeeRepository.class);
		HashMap<Long, Employee> employees = new HashMap<>();
		when(employeeRepository.save(any())).then(i -> {
			Employee employee = i.getArgument(0);
			employees.put(employee.getId(), employee);
			return employee;
		});

		when(employeeRepository.findAll()).then(p -> Streamable.of(employees.values()));
		when(employeeRepository.findById(anyLong())).then(p -> {
			if (!employees.containsKey(p.getArgument(0))) {
				return Optional.empty();
			}
			return Optional.of(employees.get(p.getArgument(0)));
		});
		doAnswer(p -> employees.remove(((Employee) p.getArgument(0)).getId())).when(employeeRepository).delete(any());

		userManagement = new UserManagement(customerRepository, employeeRepository, userAccountManager);
	}

	@Test
	void testCustomerMethods() {
		UserAccount userAccount = mockUserAccount();

		when(userAccountManager.create(eq("TestCustomer"), any(Password.UnencryptedPassword.class), anyString(),
				eq(Role.of("CUSTOMER")))).thenReturn(userAccount);
		when(userAccountManager.findByUsername(eq("TestCustomer"))).thenReturn(Optional.of(userAccount));

		RegistrationForm registrationForm = new RegistrationForm("TestCustomer", "test@test.de", "P@ssword123",
				"P@ssword123", "firstname", "lastname", "address", "11111", "city");

		Customer customer = userManagement.createCustomer(registrationForm);

		Optional<UserAccount> testCustomer = userManagement.findByUsername("TestCustomer");
		assertTrue(testCustomer.isPresent());
		assertEquals(testCustomer.get(), userAccount);

		Optional<Customer> findCustomer = userManagement.findCustomer(testCustomer.get());
		assertTrue(findCustomer.isPresent());
		assertEquals(findCustomer.get(), customer);

		long id = customer.getId();
		Optional<Customer> customerById = userManagement.findCustomerById(id);
		assertTrue(customerById.isPresent());

		userManagement.deleteCustomer(customerById.get());

		customerById = userManagement.findCustomerById(id);
		assertFalse(customerById.isPresent());
	}

	@Test
	void testEditCustomer() {
		UserAccount userAccount = mockUserAccount();

		when(userAccountManager.create(eq("TestCustomer"), any(Password.UnencryptedPassword.class), anyString(),
				eq(Role.of("CUSTOMER")))).thenReturn(userAccount);
		when(userAccountManager.findByUsername(eq("TestCustomer"))).thenReturn(Optional.of(userAccount));

		RegistrationForm registrationForm = new RegistrationForm("TestCustomer", "test@test.de", "P@ssword123",
				"P@ssword123", "firstname", "lastname", "address", "11111", "city");

		Customer customer = userManagement.createCustomer(registrationForm);

		EditForm editForm = new EditForm("neue@email.eml", "Test@123", "Test@123", "lastername",
				"Neue Adresse 1", "22222", "Dresden");

		userManagement.editCustomer(customer.getUserAccount(), editForm);
		assertEquals(customer.getAddress(), editForm.getAddress());
		assertEquals(customer.getZip(), editForm.getZip());
		assertEquals(customer.getCity(), editForm.getCity());
		assertEquals(customer.getUserAccount().getEmail(), editForm.getEmail());
		assertEquals(customer.getUserAccount().getLastname(), editForm.getLastname());
	}

	@Test
	void testNoCustomerEdit() {
		UserAccount userAccount = mockUserAccount();

		when(userAccountManager.create(eq("TestCustomer"), any(Password.UnencryptedPassword.class), anyString(),
				eq(Role.of("CUSTOMER")))).thenReturn(userAccount);
		when(userAccountManager.findByUsername(eq("TestCustomer"))).thenReturn(Optional.of(userAccount));

		RegistrationForm registrationForm = new RegistrationForm("TestCustomer", "test@test.de", "P@ssword123",
				"P@ssword123", "firstname", "lastname", "address", "11111", "city");

		Customer customer = userManagement.createCustomer(registrationForm);

		EditForm editForm = new EditForm("test@test.de", "", "", "lastname", "address", "11111", "city");

		userManagement.editCustomer(customer.getUserAccount(), editForm);
		assertEquals(customer.getAddress(), editForm.getAddress());
		assertEquals(customer.getZip(), editForm.getZip());
		assertEquals(customer.getCity(), editForm.getCity());
		assertEquals(customer.getUserAccount().getEmail(), editForm.getEmail());
		assertEquals(customer.getUserAccount().getLastname(), editForm.getLastname());
	}

	@Test
	void testEmployeeMethods() {
		UserAccount userAccount = mockUserAccount();

		when(userAccountManager.create(eq("TestEmployee"), any(Password.UnencryptedPassword.class), anyString(),
				eq(Role.of("EMPLOYEE")))).thenReturn(userAccount);
		when(userAccountManager.findByUsername(eq("TestEmployee"))).thenReturn(Optional.of(userAccount));

		EmployeeRegistrationForm registrationForm = new EmployeeRegistrationForm("TestEmployee","P@ssword123",
				"P@ssword123", "test@test.de");

		Employee employee = userManagement.createEmployee(registrationForm);

		Optional<UserAccount> testEmployee = userManagement.findByUsername("TestEmployee");
		assertTrue(testEmployee.isPresent());
		assertEquals(testEmployee.get(), userAccount);

		Optional<Employee> employeeById = userManagement.findEmployeeById(employee.getId());
		assertTrue(employeeById.isPresent());
		assertEquals(employeeById.get(), employee);

		userManagement.deleteEmployee(employeeById.get());

		employeeById = userManagement.findEmployeeById(employee.getId());
		assertFalse(employeeById.isPresent());
	}

	@Test
	void testEditEmployee() {
		UserAccount userAccount = mockUserAccount();

		when(userAccountManager.create(eq("TestEmployee"), any(Password.UnencryptedPassword.class), anyString(),
				eq(Role.of("EMPLOYEE")))).thenReturn(userAccount);
		when(userAccountManager.findByUsername(eq("TestEmployee"))).thenReturn(Optional.of(userAccount));

		EmployeeRegistrationForm registrationForm = new EmployeeRegistrationForm("TestEmployee","P@ssword123",
				"P@ssword123", "test@test.de");

		Employee employee = userManagement.createEmployee(registrationForm);

		EmployeeEditForm editForm = new EmployeeEditForm("neue@email.eml", "Test@123", "Test@123");

		userManagement.editEmployee(employee.getUserAccount(), editForm);
		assertEquals(employee.getUserAccount().getEmail(), editForm.getEmail());
	}

	@Test
	void testNoEditEmployee() {
		UserAccount userAccount = mockUserAccount();

		when(userAccountManager.create(eq("TestEmployee"), any(Password.UnencryptedPassword.class), anyString(),
				eq(Role.of("EMPLOYEE")))).thenReturn(userAccount);
		when(userAccountManager.findByUsername(eq("TestEmployee"))).thenReturn(Optional.of(userAccount));

		EmployeeRegistrationForm registrationForm = new EmployeeRegistrationForm("TestEmployee","P@ssword123",
				"P@ssword123", "test@test.de");

		Employee employee = userManagement.createEmployee(registrationForm);

		EmployeeEditForm editForm = new EmployeeEditForm("test@test.de", "", "");

		userManagement.editEmployee(employee.getUserAccount(), editForm);
		assertEquals(employee.getUserAccount().getEmail(), editForm.getEmail());
	}

	private UserAccount mockUserAccount() {
		UserAccountIdentifier userAccountIdentifier = mock(UserAccountIdentifier.class);
		// Create test user
		UserAccount userAccount = mock(UserAccount.class, RETURNS_DEEP_STUBS);
		when(userAccount.getFirstname()).thenCallRealMethod();
		doCallRealMethod().when(userAccount).setFirstname(anyString());

		when(userAccount.getLastname()).thenCallRealMethod();
		doCallRealMethod().when(userAccount).setLastname(anyString());

		when(userAccount.getEmail()).thenCallRealMethod();
		doCallRealMethod().when(userAccount).setEmail(anyString());

		when(userAccount.getId()).thenReturn(userAccountIdentifier);
		return userAccount;
	}
}
