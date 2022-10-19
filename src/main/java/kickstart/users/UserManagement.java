package kickstart.users;

import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserManagement {
	private final CustomerRepository customers;
	private final EmployeeRepository employees;
	private final UserAccountManagement userAccounts;

	private static final Role ROLE_CUSTOMER = Role.of("CUSTOMER");

	/**
	 * Creates a new {@link UserManagement} with the given {@link CustomerRepository} and
	 * {@link UserAccountManagement}.
	 * @param customers must not be {@literal null}.
	 * @param employees must not be {@literal null}.
	 * @param userAccounts must not be {@literal null}.
	 */
	UserManagement(CustomerRepository customers, EmployeeRepository employees, UserAccountManagement userAccounts) {
		Assert.notNull(customers, "CustomerRepository must not be null!");
		Assert.notNull(employees, "EmployeeRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.customers = customers;
		this.employees = employees;
		this.userAccounts = userAccounts;
	}

	/**
	 * Creates a new {@link Customer} using the information given in the {@link RegistrationForm}.
	 *
	 * @param form must not be {@literal null}.
	 * @return the view name. the new {@link Customer} instance.
	 */
	public Customer createCustomer(RegistrationForm form) {
		Assert.notNull(form, "Registration form must not be null!");

		var password = Password.UnencryptedPassword.of(form.getPassword());
		var userAccount = userAccounts.create(form.getUsername(), password, form.getEmail(), Role.of("CUSTOMER"));
		userAccount.setFirstname(form.getFirstname());
		userAccount.setLastname(form.getLastname());

		return customers.save(new Customer(userAccount, form.getAddress(), form.getZip(), form.getCity()));
	}

	/**
	 * Edits a {@link Customer} using the information given in the {@link EditForm}.
	 *
	 * @param userAccount must not be {@literal null}.
	 * @param form must not be {@literal null}.
	 */
	public void editCustomer(UserAccount userAccount, EditForm form) {
		Assert.notNull(userAccount, "UserAccount must not be null!");
		Assert.notNull(form, "Edit form must not be null!");

		Optional<Customer> optionalCustomer = findCustomer(userAccount);
		if (optionalCustomer.isEmpty()) { // should never happen
			return;
		}

		if (!form.getEmail().equals(userAccount.getEmail())) {
			userAccount.setEmail(form.getEmail());
		}

		if (form.getPassword().equals(form.getPasswordValidation()) && !form.getPassword().isEmpty()) {
			userAccounts.changePassword(userAccount, Password.UnencryptedPassword.of(form.getPassword()));
		}

		Customer customer = optionalCustomer.get();
		if (!form.getLastname().equals(userAccount.getLastname())) {
			userAccount.setLastname(form.getLastname());
		}

		if (!form.getAddress().equals(customer.getAddress())) {
			customer.setAddress(form.getAddress());
		}

		if (!form.getCity().equals(customer.getCity())) {
			customer.setCity(form.getCity());
		}

		if (!form.getZip().equals(customer.getZip())) {
			customer.setZip(form.getZip());
		}
	}

	/**
	 * Edits a {@link Employee} using the information given in the {@link EmployeeEditForm}.
	 *
	 * @param userAccount must not be {@literal null}.
	 * @param form must not be {@literal null}.
	 */
	public void editEmployee(UserAccount userAccount, EmployeeEditForm form) {
		Assert.notNull(userAccount, "UserAccount must not be null!");
		Assert.notNull(form, "Edit form must not be null!");

		if (!form.getEmail().equals(userAccount.getEmail())) {
			userAccount.setEmail(form.getEmail());
		}

		if (form.getPassword().equals(form.getPasswordValidation()) && !form.getPassword().isEmpty()) {
			userAccounts.changePassword(userAccount, Password.UnencryptedPassword.of(form.getPassword()));
		}
	}

	/**
	 * Creates a new {@link Employee} using the information given in the {@link RegistrationForm}.
	 *
	 * @param form must not be {@literal null}.
	 * @return the view name. the new {@link Employee} instance.
	 */
	public Employee createEmployee(EmployeeRegistrationForm form) {
		Assert.notNull(form, "Employee Registration form must not be null!");

		var password = Password.UnencryptedPassword.of(form.getPassword());
		var userAccount = userAccounts.create(form.getUsername(), password, form.getEmail(), Role.of("EMPLOYEE"));

		return employees.save(new Employee(userAccount));
	}

	/**
	 * Deletes a {@link Employee} from our database.
	 *
	 * @param employee must not be {@literal null}.
	 */
	public void deleteEmployee(Employee employee) {
		Assert.notNull(employee, "Employee form must not be null!");

		userAccounts.delete(employee.getUserAccount());
		employees.delete(employee);
	}

	/**
	 * Deletes a {@link Customer} from our database.
	 *
	 * @param customer must not be {@literal null}.
	 */
	public void deleteCustomer(Customer customer) {
		Assert.notNull(customer, "Customer form must not be null!");

		userAccounts.delete(customer.getUserAccount());
		customers.delete(customer);
	}

	/**
	 * Returns all {@link Customer}s currently available in the system.
	 *
	 * @return the view name. all {@link Customer} entities.
	 */
	public Streamable<Customer> findAllCustomers() {
		return customers.findAll();
	}

	/**
	 * Returns {@link Customer} by {@link UserAccount} if existing.
	 *
	 * @return the view name. Optional with {@link Customer} entity.
	 */
	public Optional<Customer> findCustomer(UserAccount acc) {
		return customers.findAll().filter(u -> u.getUserAccount().getId().equals(acc.getId())).stream().findFirst();
	}

	/**
	 * Returns {@link Customer} by given id.
	 *
	 * @return the view name. {@link Customer} entitiy.
	 */
	public Optional<Customer> findCustomerById(long id) {
		return customers.findById(id);
	}

	/**
	 * Returns all {@link Employee}s currently available in the system.
	 *
	 * @return the view name. all {@link Employee} entities.
	 */
	public Streamable<Employee> findAllEmployees() {
		return employees.findAll();
	}

	/**
	 * Returns {@link Employee} by given id.
	 *
	 * @return the view name. {@link Employee} entity.
	 */
	public Optional<Employee> findEmployeeById(long id) {
		return employees.findById(id);
	}

	/**
	 * Returns {@link UserAccount} by username if existing.
	 *
	 * @return the view name. Optional with {@link UserAccount} entity.
	 */
	public Optional<UserAccount> findByUsername(String username) {
		return userAccounts.findByUsername(username);
	}
}
