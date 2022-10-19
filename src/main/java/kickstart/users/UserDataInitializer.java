package kickstart.users;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/*
 * Initialisierung von Standard Benutzeraccounts.
 */
@Component
public class UserDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(UserDataInitializer.class);

	private final UserAccountManagement userAccountManagement;
	private final UserManagement userManagement;

	/**
	 * Creates a new {@link UserDataInitializer} with the given {@link UserAccountManagement} and
	 * {@link CustomerRepository}.
	 *
	 * @param userAccountManagement must not be {@literal null}.
	 * @param userManagement must not be {@literal null}.
	 */
	UserDataInitializer(UserAccountManagement userAccountManagement, UserManagement userManagement) {
		Assert.notNull(userAccountManagement, "UserAccountManagement must not be null!");
		Assert.notNull(userManagement, "CustomerRepository must not be null!");

		this.userAccountManagement = userAccountManagement;
		this.userManagement = userManagement;
	}

	/*
	 * (non-Javadoc)
	 * @see org.salespointframework.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {
		// Do not allow re-initialization
		if (userAccountManagement.findByUsername("boss").isPresent()) {
			return;
		}

		LOG.info("Creating default users and customers.");

		// Zur Vereinfachung der Arbeit wird bei den Test-Usern auf valide Passwörter, Adressen usw. verzichtet.
		var password = "123";

		userAccountManagement.create("boss", Password.UnencryptedPassword.of("123"), Role.of("BOSS"), Role.of("EMPLOYEE"));

		List.of(
				new RegistrationForm("test", "N/A", password, password, "N/A", "N/A", "test", "N/A", "N/A")
		).forEach(userManagement::createCustomer);

		List.of(
				new EmployeeRegistrationForm("bob", password, password, "bob@buchhandel.de")
		).forEach(userManagement::createEmployee);
	}
}
