package kickstart.inventory;

import kickstart.catalog.ShopItem;
import kickstart.catalog.ShopItemCatalog;
import kickstart.order.ShopOrder;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderManagement;

import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTests {
	@Autowired
	MockMvc mvc;
	@Autowired
	private UniqueInventory<UniqueInventoryItem> inventory;
	@Autowired
	private ShopItemCatalog shopItemCatalog;
	@Autowired
	private OrderManagement<ShopOrder> orderManagement;

	@Test
	void inventoryControllerCreation() {
		InventoryController inventoryController = new InventoryController(inventory, shopItemCatalog, orderManagement);
		assertThat(inventoryController != null);
	}

	@Test
	@WithMockUser(username = "bob", roles = "EMPLOYEE")
	void inventoryViewExist() throws Exception {
		mvc.perform(get("/inventory"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("stock"))
				.andExpect(view().name("inventory"));
	}

	@Test
	@WithMockUser(username = "boss", roles = {"BOSS", "EMPLOYEE"})
	void inventoryEdit() throws Exception {
		ShopItem item = shopItemCatalog.findAll().get().findFirst().get();

		mvc.perform(post("/inventory")
				.param("productId", item.getId().toString())
				.param("action", "delete")
		)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("stock"))
				.andExpect(view().name("inventory"));

		assertTrue(shopItemCatalog.findById(item.getId()).isEmpty());
		assertTrue(inventory.findByProduct(item).isEmpty());

		ShopItem item2 = shopItemCatalog.findAll().get().findFirst().get();

		Quantity item2Quantity = inventory.findByProduct(item2).get().getQuantity();
		mvc.perform(post("/inventory")
				.param("productId", item2.getId().toString())
				.param("action", "add")
		)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("stock"))
				.andExpect(view().name("inventory"));

		assertTrue(inventory.findByProduct(item2).get().getQuantity().isEqualTo(item2Quantity.add(Quantity.of(1))));

		ShopItem item3 = shopItemCatalog.findAll().get().findFirst().get();

		Quantity item3Quantity = inventory.findByProduct(item3).get().getQuantity();
		mvc.perform(post("/inventory")
				.param("productId", item3.getId().toString())
				.param("action", "remove")
		)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("stock"))
				.andExpect(view().name("inventory"));

		assertTrue(inventory.findByProduct(item3).get().getQuantity().isEqualTo(item3Quantity.subtract(Quantity.of(1))));
	}

	@Test
	@WithMockUser(username = "boss", roles = {"BOSS", "EMPLOYEE"})
	void inventoryEditInvalidData() throws Exception {
		mvc.perform(post("/inventory")
				.param("productId", "5353")
				.param("action", "add")
		)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/inventory"));
	}
}