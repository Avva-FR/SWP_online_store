package kickstart.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerTest {
	@Test
	void testCustomerGetter() {
		UserAccount userAccount = mock(UserAccount.class);

		Customer customer = new Customer(userAccount, "Straße 1", "01234", "Dresden");
		assertEquals(customer.getUserAccount(), userAccount);
		assertEquals(customer.getAddress(), "Straße 1");
		assertEquals(customer.getZip(), "01234");
		assertEquals(customer.getCity(), "Dresden");
	}

	@Test
	void testCustomerSetter() {
		UserAccount userAccount = mock(UserAccount.class);

		Customer customer = new Customer(userAccount, "Straße 1", "01234", "Dresden");

		assertEquals(customer.getAddress(), "Straße 1");
		customer.setAddress("Andere-Straße 2");
		assertEquals(customer.getAddress(), "Andere-Straße 2");

		assertEquals(customer.getZip(), "01234");
		customer.setZip("56789");
		assertEquals(customer.getZip(), "56789");

		assertEquals(customer.getCity(), "Dresden");
		customer.setCity("München");
		assertEquals(customer.getCity(), "München");
	}
}
