package kickstart.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@NoArgsConstructor
public class ShopOrder extends Order {
	@Enumerated(EnumType.STRING)
	private PaymentStatus orderPaymentStatus;
	private ShipmentStatus orderShipmentStatus;

	public ShopOrder(UserAccount userAccount) {
		super(userAccount);
		this.orderPaymentStatus = PaymentStatus.OPEN;
		this.orderShipmentStatus = ShipmentStatus.TO_SHIP;
	}

	public ShopOrder(UserAccount userAccount, PaymentMethod paymentMethod) {
		super(userAccount, paymentMethod);
		this.orderPaymentStatus = PaymentStatus.OPEN;
		this.orderShipmentStatus = ShipmentStatus.TO_SHIP;
	}

	public ShopOrder markShopOrderAsPaid() {
		Assert.isTrue(!this.isShopOrderPaid(), "Order is already paid!");
		this.orderPaymentStatus = PaymentStatus.PAID;
		return this;
	}

	public ShopOrder markShopOrderAsShipped() {
		Assert.isTrue(!this.isShopOrderShipped(), "Order is already shipped!");
		this.orderShipmentStatus = ShipmentStatus.SHIPPED;
		return this;
	}

	public boolean isShopOrderPaid() {
		return this.orderPaymentStatus == PaymentStatus.PAID;
	}

	public boolean isShopOrderShipped() {
		return this.orderShipmentStatus == ShipmentStatus.SHIPPED;
	}
}
