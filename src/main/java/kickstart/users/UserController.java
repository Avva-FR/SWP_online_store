package kickstart.users;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

@Controller
public class UserController {
	private static final String REGISTRATION_ROUTE = "registration";

	private final UserManagement userManagement;
	private final OrderManagement orderManagement;

	public UserController(UserManagement userManagement, OrderManagement orderManagement) {
		Assert.notNull(userManagement, "UserManagement must not be null!");
		Assert.notNull(orderManagement, "OrderManagement must not be null!");

		this.userManagement = userManagement;
		this.orderManagement = orderManagement;
	}

	@PostMapping("/register")
	public String registerNew(@Valid RegistrationForm form, Errors result) {
		if (result.hasErrors()) {
			return REGISTRATION_ROUTE;
		}

		// Check if there is already a user with this name present
		if (userManagement.findByUsername(form.getUsername()).isPresent()) {
			result.rejectValue("username", "RegistrationForm.TakenUsername");
			return REGISTRATION_ROUTE;
		}

		userManagement.createCustomer(form);

		return "redirect:/";
	}

	@GetMapping("/register")
	public String register(Model model, RegistrationForm form) {
		return REGISTRATION_ROUTE;
	}
	
	@GetMapping("/customermanagement")
	@PreAuthorize("hasRole('BOSS')")
	public String customers(Model model) {
		model.addAttribute("customerList", userManagement.findAllCustomers());

		HashMap<Long, Long> OPEN_ORDERS = new HashMap<>();
		userManagement.findAllCustomers().forEach(c -> OPEN_ORDERS.put(c.getId(),
				orderManagement.findBy(c.getUserAccount()).get().filter(order -> ((Order) order).isOpen()).count()));
		model.addAttribute("openOrders", OPEN_ORDERS);

		return "customermanagement";
	}

	@PostMapping(value = "/customermanagement")
	@PreAuthorize("hasRole('BOSS')")
	public String customers(@RequestParam("customerId") long id) {
		Optional<Customer> optionalCustomer = userManagement.findCustomerById(id);
		if (optionalCustomer.isEmpty()) { // should never happen
			return "redirect:/customermanagement";
		}

		Customer customer = optionalCustomer.get();
		userManagement.deleteCustomer(customer);

		return "redirect:/customermanagement";
	}

	@GetMapping("/accountmanagement")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String accountmanagement(@LoggedIn Optional<UserAccount> optAcc, EditForm form, Model model) {
		Optional<Customer> optionalCustomer = userManagement.findCustomer(optAcc.get());
		if (optionalCustomer.isEmpty()) { // should never happen
			return "redirect:/";
		}

		model.addAttribute("customer", optionalCustomer.get());

		return "accountmanagement";
	}

	@PostMapping("/accountmanagement")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String editAccount(@LoggedIn Optional<UserAccount> optAcc, @Valid EditForm form, Errors result, Model model) {
		Optional<Customer> optionalCustomer = userManagement.findCustomer(optAcc.get());
		if (optionalCustomer.isEmpty()) { // should never happen
			return "redirect:/";
		}

		// in case there is a error, we need to add this
		model.addAttribute("customer", optionalCustomer.get());

		if (result.hasErrors()) {
			return "accountmanagement";
		}

		UserAccount acc = optAcc.get();
		userManagement.editCustomer(acc, form);

		return "accountmanagement";
	}

	@GetMapping("/employeemanagement")
	@PreAuthorize("hasRole('BOSS')")
	public String employees(Model model) {
		model.addAttribute("employeeList", userManagement.findAllEmployees());

		return "employeemanagement";
	}

	@PostMapping(value = "/editemployee", params = "action=delete")
	@PreAuthorize("hasRole('BOSS')")
	public String editEmployee(@RequestParam("employeeId") long id) {
		Optional<Employee> optionalEmployee = userManagement.findEmployeeById(id);
		if (optionalEmployee.isEmpty()) { // should never happen
			return "redirect:/employeemanagement";
		}

		Employee employee = optionalEmployee.get();
		userManagement.deleteEmployee(employee);
		return "redirect:/employeemanagement";
	}

	@PostMapping(value = "/editemployee", params = "action=edit")
	@PreAuthorize("hasRole('BOSS')")
	public RedirectView editEmployee(@RequestParam("employeeId") long id, RedirectAttributes redir) {
		RedirectView red = new RedirectView("editemployee", true);
		redir.addAttribute("employeeId", id);
		return red;
	}

	@GetMapping("/editemployee")
	@PreAuthorize("hasRole('BOSS')")
	public String editEmployee(@RequestParam("employeeId") long id, EmployeeEditForm form, Model model) {
		Optional<Employee> optionalEmployee = userManagement.findEmployeeById(id);
		if (optionalEmployee.isEmpty()) { // should never happen
			return "redirect:/employeemanagement";
		}

		Employee employee = optionalEmployee.get();

		model.addAttribute("employee", employee);
		model.addAttribute("employeeId", id);
		return "editemployee";
	}

	@PostMapping("/editemployee")
	@PreAuthorize("hasRole('BOSS')")
	public String editEmployee(@RequestParam("employeeId") long id, @Valid EmployeeEditForm form,
							   Errors result, Model model) {
		Optional<Employee> optionalEmployee = userManagement.findEmployeeById(id);
		if (optionalEmployee.isEmpty()) { // should never happen
			return "redirect:/employeemanagement";
		}

		Employee employee = optionalEmployee.get();
		model.addAttribute("employee", employee);
		if (result.hasErrors()) {
			return "editemployee";
		}

		userManagement.editEmployee(employee.getUserAccount(), form);

		return "redirect:/employeemanagement";
	}

	@GetMapping("/addemployee")
	@PreAuthorize("hasRole('BOSS')")
	public String addEmployee(EmployeeRegistrationForm form, Model model) {
		return "addemployee";
	}

	@PostMapping("/addemployee")
	@PreAuthorize("hasRole('BOSS')")
	public String addEmployee(@Valid EmployeeRegistrationForm form, Errors result, Model model) {
		if (result.hasErrors()) {
			return "addemployee";
		}

		// Check if there is already a user with this name present
		if (userManagement.findByUsername(form.getUsername()).isPresent()) {
			result.rejectValue("username", "RegistrationForm.TakenUsername");
			return "addemployee";
		}

		userManagement.createEmployee(form);

		return "redirect:/employeemanagement";
	}
}
