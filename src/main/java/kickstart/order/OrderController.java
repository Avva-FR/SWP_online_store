package kickstart.order;

import kickstart.catalog.ShopItem;
import lombok.SneakyThrows;
import org.javamoney.moneta.Money;
import org.salespointframework.order.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.util.Streamable;
import org.springframework.web.bind.annotation.*;
import kickstart.catalog.ShopItemCatalog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import static org.salespointframework.core.Currencies.EURO;

import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.salespointframework.time.Interval;

import javax.money.MonetaryAmount;

@Controller
@SessionAttributes("cart")
public class OrderController {
	private final OrderManagement<ShopOrder> orderManagement;
	private final ShopItemCatalog shopItemCatalog;

	OrderController(OrderManagement<ShopOrder> orderManagement, ShopItemCatalog shopItemCatalog) {
		Assert.notNull(orderManagement, "OrderManagement must not be null!");
		this.orderManagement = orderManagement;
		this.shopItemCatalog = shopItemCatalog;
	}

	class OrderLineWithImageLink{
		public OrderLine orderLine;
		public String imageLink;

		OrderLineWithImageLink(OrderLine orderLine, String imageLink){
			this.orderLine = orderLine;
			this.imageLink = imageLink;
		}
		public String getImageLink() {return imageLink;}
		public OrderLine getOrderLine() {return orderLine;}
	}

	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@PostMapping("/cartAddShopItem")
	String addShopItemToCart(@RequestParam("pid") ShopItem shopItem, @RequestParam("number") int number,
							 @ModelAttribute Cart cart) {
		cart.addOrUpdateItem(shopItem, Quantity.of(number));

		switch (shopItem.getType()) {
			case BOOK:
			default:
				return "redirect:/bookshop";
			case CD:
				return "redirect:/cdshop";
			case DVD:
				return "redirect:/dvdshop";
		}
	 }

	@GetMapping("/cart")
	String cart(){
		return "cart";
	}

	@PostMapping("/removeItem")
	String removeItem(@RequestParam("id") String id, @ModelAttribute Cart cart){
		cart.removeItem(id);
		return "redirect:/cart";
	}

	@PostMapping("/editCart")
	String edit(@RequestParam("id") String id, @RequestParam("number") int number, @ModelAttribute Cart cart){
		//int amount = number <= 0 || number > 5 ? 1 : number;
		if(number == 0){
			cart.removeItem(id);
			return "redirect:/cart";
		}else{
			Optional<CartItem> maybeItem = cart.getItem(id);
			if(maybeItem.isPresent()){
				CartItem item = maybeItem.get();
				Quantity quantity = Quantity.of(number).subtract(item.getQuantity());
				cart.addOrUpdateItem(item.getProduct(),quantity);
			}
			return "redirect:/cart";
		}
	}

	@PostMapping("/clearCart")
	String clear(@ModelAttribute Cart cart) {
		cart.clear();
		return "redirect:/cart";
	}

	@PostMapping("/buyCheckout")
	String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount, Model model) {
		if (userAccount.isEmpty()) {
			return "redirect:/login";
		}

		var order = new ShopOrder(userAccount.get(), Cash.CASH);

		cart.addItemsTo(order);

		order.markShopOrderAsPaid();
		orderManagement.save(order);

		model.addAttribute("total", cart.getPrice());

		cart.clear();

		return "paymentinstruction";
	}

	@PostMapping("/pickupCheckout")
	String pickup(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount) {
		if (userAccount.isEmpty()) {
			return "redirect:/login";
		}

		var order = new ShopOrder(userAccount.get(), Cash.CASH);

		cart.addItemsTo(order);
		orderManagement.save(order);

		cart.clear();

		return "orderconfirmation";
	}

	@GetMapping("/completedOrders")
	@PreAuthorize("hasRole('EMPLOYEE')")
	String completedOrders(Model model) {
		model.addAttribute("orders", orderManagement.findBy(OrderStatus.COMPLETED));
		return "orders";
	}

	@GetMapping("/openOrders")
	@PreAuthorize("hasRole('EMPLOYEE')")
	String openOrders(Model model) {
		model.addAttribute("orders", orderManagement.findBy(OrderStatus.OPEN));
		return "orders";
	}

	@PreAuthorize("hasRole('EMPLOYEE')")
	@GetMapping("/order/{id}")
	String orderDetail(@PathVariable OrderIdentifier id, Model model) {
		Optional<ShopOrder> maybeOrder = orderManagement.get(id);
		if (maybeOrder.isEmpty()) {
			return "orderdetail";
		}

		ShopOrder order = maybeOrder.get();
		List<OrderLine> orderLines = new ArrayList<>();
		order.getOrderLines().iterator().forEachRemaining(orderLines::add);

		List<OrderLineWithImageLink> orderLinesWithImageLink = new ArrayList<>();
		for (int i = 0; i < orderLines.size(); i++) {
			Optional<ShopItem> maybeItem = shopItemCatalog.findById(orderLines.get(i).getProductIdentifier());
			if (maybeItem.isEmpty()) {
				continue;
			}

			String image = maybeItem.get().getPhotosImagePath();
			orderLinesWithImageLink.add(new OrderLineWithImageLink(orderLines.get(i), image));
		}

		model.addAttribute("order", order);
		model.addAttribute("Lines", orderLinesWithImageLink);
		return "orderdetail";
	}

	@PreAuthorize("hasRole('EMPLOYEE')")
	@PostMapping("/order/{pid}/markShopOrderAsPaid")
	String markShopOrderAsPaid(@RequestParam("id") OrderIdentifier id, @PathVariable OrderIdentifier pid, Model model){
		Optional<ShopOrder> maybeOrder = orderManagement.get(id);
		if (maybeOrder.isEmpty()) {
			return "orderdetail";
		}

		ShopOrder order = maybeOrder.get();
		List<OrderLine> orderLines = new ArrayList<>();
		order.getOrderLines().iterator().forEachRemaining(orderLines::add);

		List<OrderLineWithImageLink> orderLinesWithImageLink = new ArrayList<>();
		for (int i = 0; i <orderLines.size(); i++) {
			Optional<ShopItem> maybeItem = shopItemCatalog.findById(orderLines.get(i).getProductIdentifier());
			if (maybeItem.isEmpty()) {
				continue;
			}

			ShopItem item = maybeItem.get();
			String image = item.getPhotosImagePath();
			orderLinesWithImageLink.add(new OrderLineWithImageLink(orderLines.get(i), image));
		}

		order.markShopOrderAsPaid();
		orderManagement.save(order);
		model.addAttribute("order", order);
		model.addAttribute("Lines", orderLinesWithImageLink);
		return "orderdetail";
	}

	@PreAuthorize("hasRole('EMPLOYEE')")
	@PostMapping("/order/{pid}/markShopOrderAsShipped")
	String markShopOrderAsShipped(@RequestParam("id") OrderIdentifier id, @PathVariable OrderIdentifier pid, Model model){
		Optional<ShopOrder> maybeOrder = orderManagement.get(id);
		if (maybeOrder.isEmpty()) {
			return "orderdetail";
		}

		ShopOrder order = maybeOrder.get();
		List<OrderLine> orderLines = new ArrayList<OrderLine>();
		order.getOrderLines().iterator().forEachRemaining(orderLines::add);
		List<OrderLineWithImageLink> orderLinesWithImageLink = new ArrayList<OrderLineWithImageLink>();
		for (int i = 0; i < orderLines.size(); i++){
			Optional<ShopItem> maybeItem = shopItemCatalog.findById(orderLines.get(i).getProductIdentifier());
			if (maybeItem.isEmpty()) {
				continue;
			}

			ShopItem item = maybeItem.get();
			String image = item.getPhotosImagePath();
			orderLinesWithImageLink.add(new OrderLineWithImageLink(orderLines.get(i), image));
		}

		order.markShopOrderAsShipped();
		orderManagement.save(order);
		model.addAttribute("order", order);
		model.addAttribute("Lines", orderLinesWithImageLink);
		return "orderdetail";
	}

	@PreAuthorize("hasRole('EMPLOYEE')")
	@PostMapping("/completeOrder")
	String completeOrder(@RequestParam("id") OrderIdentifier id, Model model) {
		Optional<ShopOrder> maybeOrder = orderManagement.get(id);
		if (maybeOrder.isEmpty()) {
			return "orderdetail";
		}

		ShopOrder order = maybeOrder.get();
		if (order.isShopOrderPaid() == order.isShopOrderShipped()){
			orderManagement.payOrder(order);
			orderManagement.completeOrder(order);
			model.addAttribute("ordersCompleted", orderManagement.findBy(OrderStatus.COMPLETED));
			model.addAttribute("ordersOpen", orderManagement.findBy(OrderStatus.OPEN));
		}
		return "orders";
	}

	@SneakyThrows
	@PreAuthorize("hasRole('BOSS')")
	@GetMapping("/finances")
	String postFinances(Model model) {

		String str = "1986-04-08 12:30";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime q1 = LocalDateTime.parse("2022-01-01 00:00", formatter);
		LocalDateTime q2 = LocalDateTime.parse("2022-04-01 00:00", formatter);
		LocalDateTime q3 = LocalDateTime.parse("2022-07-01 00:00", formatter);
		LocalDateTime q4 = LocalDateTime.parse("2022-10-01 00:00", formatter);
		LocalDateTime q4e = LocalDateTime.parse("2022-12-31 23:59", formatter);


		MonetaryAmount q1orders = orderManagement.findBy(Interval.from(q1).to(q2)).stream().map(Order::getTotal).
				reduce(Money.of(0, EURO), (MonetaryAmount::add));
		MonetaryAmount q2orders = orderManagement.findBy(Interval.from(q2).to(q3)).stream().map(Order::getTotal).
				reduce(Money.of(0, EURO), (MonetaryAmount::add));
		MonetaryAmount q3orders = orderManagement.findBy(Interval.from(q3).to(q4)).stream().map(Order::getTotal).
				reduce(Money.of(0, EURO), (MonetaryAmount::add));
		MonetaryAmount q4orders = orderManagement.findBy(Interval.from(q4).to(q4e)).stream().map(Order::getTotal).
				reduce(Money.of(0, EURO), (MonetaryAmount::add));

		model.addAttribute("q1", q1orders);
		model.addAttribute("q2", q2orders);
		model.addAttribute("q3", q3orders);
		model.addAttribute("q4", q4orders);


		return "finances";
	}
}
