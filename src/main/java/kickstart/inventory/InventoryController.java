package kickstart.inventory;

import kickstart.catalog.ShopItem;
import kickstart.catalog.ShopItemCatalog;

import kickstart.order.ShopOrder;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
class InventoryController {
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final ShopItemCatalog shopItemCatalog;
	private final OrderManagement<ShopOrder> orderManagement;

	InventoryController(UniqueInventory<UniqueInventoryItem> inventory, ShopItemCatalog shopItemCatalog,
						OrderManagement orderManagement) {
		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(shopItemCatalog, "ShopitemCatalog must not be null!");

		this.inventory = inventory;
		this.shopItemCatalog = shopItemCatalog;
		this.orderManagement = orderManagement;
	}

	@GetMapping("/inventory")
	@PreAuthorize("hasRole('EMPLOYEE')") // this includes Boss
	String stock(Model model) {
		model.addAttribute("stock", inventory.findAll());
		return "inventory";
	}

	@PostMapping("/inventory")
	@PreAuthorize("hasRole('EMPLOYEE')")
	String stock(@LoggedIn Optional<UserAccount> optAcc, @RequestParam("productId") ProductIdentifier productId,
				 @RequestParam("action") String action, Model model) {
		Optional<ShopItem> p = shopItemCatalog.findById(productId);
		if (p.isEmpty()) {
			return "redirect:/inventory";
		}

		Optional<UniqueInventoryItem> optItem = inventory.findByProduct(p.get());

		if (optItem.isPresent()) {
			UniqueInventoryItem item = optItem.get();
			if (item.getQuantity().isZeroOrNegative()) {
				return "redirect:/inventory";
			}

			if ("add".equals(action)) {
				item.increaseQuantity(Quantity.of(1));
				inventory.save(item);
			} else if ("remove".equals(action)) {
				item.decreaseQuantity(Quantity.of(1));
				inventory.save(item);
			} else if ("delete".equals(action) && optAcc.get().hasRole(Role.of("BOSS"))
					&& orderManagement.findAll(Pageable.unpaged()).get()
							.map(d -> d.getOrderLines(p.get()).get())
							.count() < 1) {
				inventory.delete(item);
				shopItemCatalog.deleteById(productId);
			}
		}

		model.addAttribute("stock", inventory.findAll());
		return "inventory";
	}
}