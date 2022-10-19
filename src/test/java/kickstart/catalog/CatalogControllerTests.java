package kickstart.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CatalogControllerTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	private CatalogController catalogController;
	@Autowired
	private ShopItemCatalog shopItemCatalog;
	@Autowired
	private UniqueInventory<UniqueInventoryItem> inventory;
	@Autowired
	private BusinessTime businessTime;
	@Autowired
	private ImagesRepo imagesRepo;

	//CatalogControllerUnitTest01
	@Test
	void catalogControllerCreation() {

		CatalogController catalogController = new CatalogController(shopItemCatalog, inventory, businessTime, imagesRepo);
		assertThat(catalogController != null);
	}

	//CatalogControllerWebIntegrationTest01
	@Test
	void bookShopViewExist() throws Exception {
		mvc.perform(get("/bookshop"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("shopItemCatalog"))
				.andExpect(model().attributeExists("title"))
				.andExpect(view().name("shop"));
	}

	//CatalogControllerWebIntegrationTest02
	@Test
	void dvdShopViewExist() throws Exception {
		mvc.perform(get("/dvdshop"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("shopItemCatalog"))
				.andExpect(model().attributeExists("title"))
				.andExpect(view().name("shop"));

	}

	//CatalogControllerWebIntegrationTest03
	@Test
	void cdShopViewExist() throws Exception {
		mvc.perform(get("/cdshop"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("shopItemCatalog"))
				.andExpect(model().attributeExists("title"))
				.andExpect(view().name("shop"));
	}
}
