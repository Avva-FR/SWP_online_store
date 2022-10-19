package kickstart.inventory;

import kickstart.catalog.ShopItemCatalog;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Order(20)
class InventoryInitializer implements DataInitializer {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final ShopItemCatalog shopItemCatalog;

	InventoryInitializer(UniqueInventory<UniqueInventoryItem> inventory, ShopItemCatalog shopItemCatalog) {

		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(shopItemCatalog, "ShopitemCatalog must not be null!");

		this.inventory = inventory;
		this.shopItemCatalog = shopItemCatalog;
	}

	@Override
	public void initialize() {

		shopItemCatalog.findAll().forEach(shopItem -> {
			if (inventory.findByProduct(shopItem).isEmpty()) {
				inventory.save(new UniqueInventoryItem(shopItem, Quantity.of(10)));
			}
		});
	}
}
