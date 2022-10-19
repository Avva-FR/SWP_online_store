package kickstart.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.UserAccount;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShopOrderTests {
	@Test
	void testShopOrder() {
		UserAccount userAccount = mock(UserAccount.class);

		ShopOrder order = new ShopOrder(userAccount);

		assertEquals(order.getOrderPaymentStatus(), PaymentStatus.OPEN); // default value
		order.markShopOrderAsPaid();
		assertEquals(order.getOrderPaymentStatus(), PaymentStatus.PAID);
		assertThrows(IllegalArgumentException.class, order::markShopOrderAsPaid); // only one-time call

		assertEquals(order.getOrderShipmentStatus(), ShipmentStatus.TO_SHIP); // default value
		order.markShopOrderAsShipped();
		assertEquals(order.getOrderShipmentStatus(), ShipmentStatus.SHIPPED);
		assertThrows(IllegalArgumentException.class, order::markShopOrderAsShipped); // only one-time call
	}

	@Test
	void testShopOrderWithPaymentMethod() {
		UserAccount userAccount = mock(UserAccount.class);

		ShopOrder order = new ShopOrder(userAccount, Cash.CASH);

		assertEquals(order.getOrderPaymentStatus(), PaymentStatus.OPEN); // default value
		order.markShopOrderAsPaid();
		assertEquals(order.getOrderPaymentStatus(), PaymentStatus.PAID);
		assertThrows(IllegalArgumentException.class, order::markShopOrderAsPaid); // only one-time call

		assertEquals(order.getOrderShipmentStatus(), ShipmentStatus.TO_SHIP); // default value
		order.markShopOrderAsShipped();
		assertEquals(order.getOrderShipmentStatus(), ShipmentStatus.SHIPPED);
		assertThrows(IllegalArgumentException.class, order::markShopOrderAsShipped); // only one-time call
	}
}
