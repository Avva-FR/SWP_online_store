package kickstart.order;

import kickstart.catalog.ShopItem;
import kickstart.catalog.ShopItemCatalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.salespointframework.order.Cart;

import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTests {
	@Autowired
	WebApplicationContext webApplicationContext;
	MockMvc mvc;
	@Autowired
	ShopItemCatalog shopItemCatalog;

	@BeforeEach
	void prepare() {
		mvc = MockMvcBuilders
				.webAppContextSetup(this.webApplicationContext)
				.apply(sharedHttpSession()) // use this session across requests
				.build();
	}

	@Test
	@WithMockUser(username = "boss", roles = {"BOSS", "EMPLOYEE"})
	void accessingCompletedOrdersAsBoss() throws Exception {
		mvc.perform(get("/completedOrders"))
				.andExpect(status().isOk())
				.andExpect(view().name("orders"))
				.andExpect(model().attributeExists("orders"));
	}

	@Test
	@WithMockUser(username = "bob", roles = "EMPLOYEE")
	void accessingCompletedOrdersAsEmployee() throws Exception {
		mvc.perform(get("/completedOrders"))
				.andExpect(status().isOk())
				.andExpect(view().name("orders"))
				.andExpect(model().attributeExists("orders"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void finances() throws Exception {
		mvc.perform(get("/finances"))
				.andExpect(status().isOk())
				.andExpect(view().name("finances"));
	}

	@Test
	@WithMockUser(username = "boss", roles = {"BOSS", "EMPLOYEE"})
	void accessingOpenOrdersAsBoss() throws Exception {
		mvc.perform(get("/openOrders"))
				.andExpect(status().isOk())
				.andExpect(view().name("orders"))
				.andExpect(model().attributeExists("orders"));
	}

	@Test
	@WithMockUser(username = "bob", roles = "EMPLOYEE")
	void accessingOpenOrdersAsEmployee() throws Exception {
		mvc.perform(get("/openOrders"))
				.andExpect(status().isOk())
				.andExpect(view().name("orders"))
				.andExpect(model().attributeExists("orders"));
	}

	@Test
	@WithMockUser(username = "test", roles = "CUSTOMER")
	void testCart() throws Exception {
		mvc.perform(get("/cart"))
				.andExpect(status().isOk())
				.andExpect(view().name("cart"));
	}

	@Test
	@WithMockUser(username = "test", roles = "CUSTOMER")
	void testCartAddRemoveClear() throws Exception {
		MvcResult result = mvc.perform(get("/cart"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("cart"))
				.andReturn();

		Cart cart = (Cart) result.getModelAndView().getModel().get("cart");
		ShopItem dvd = shopItemCatalog.findAll().filter(i -> i.getType().equals(ShopItem.ShopItemType.DVD))
				.get().findFirst().get();

		mvc.perform(post("/cartAddShopItem")
				.param("pid", dvd.getId().toString())
				.param("number", "1")
				.param("cart", cart.toString())
		).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(getRedirectUrlByType(dvd.getType())));

		ShopItem cd = shopItemCatalog.findAll().filter(i -> i.getType().equals(ShopItem.ShopItemType.CD))
				.get().findFirst().get();

		mvc.perform(post("/cartAddShopItem")
				.param("pid", cd.getId().toString())
				.param("number", "1")
				.param("cart", cart.toString())
		).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(getRedirectUrlByType(cd.getType())));

		ShopItem book = shopItemCatalog.findAll().filter(i -> i.getType().equals(ShopItem.ShopItemType.BOOK))
				.get().findFirst().get();

		mvc.perform(post("/cartAddShopItem")
				.param("pid", book.getId().toString())
				.param("number", "1")
				.param("cart", cart.toString())
		).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(getRedirectUrlByType(book.getType())));

		assertTrue(cart.stream().anyMatch(i -> i.getProduct().equals(dvd)));
		assertTrue(cart.stream().anyMatch(i -> i.getProduct().equals(cd)));
		assertTrue(cart.stream().anyMatch(i -> i.getProduct().equals(book)));

		CartItem ci = cart.stream().filter(i -> i.getProduct().equals(book)).findFirst().get();

		mvc.perform(post("/removeItem")
				.param("id", ci.getId())
				.param("cart", cart.toString())
		).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/cart"));

		assertTrue(cart.stream().noneMatch(i -> i.getProduct().equals(book)));

		mvc.perform(post("/clearCart").param("cart", cart.toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/cart"));
		assertTrue(cart.isEmpty());
	}

	@Test
	@WithMockUser(username = "test", roles = "CUSTOMER")
	void testEditCart() throws Exception {
		MvcResult result = mvc.perform(get("/cart"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("cart"))
				.andReturn();

		Cart cart = (Cart) result.getModelAndView().getModel().get("cart");
		ShopItem dvd = shopItemCatalog.findAll().filter(i -> i.getType().equals(ShopItem.ShopItemType.DVD))
				.get().findFirst().get();

		mvc.perform(post("/cartAddShopItem")
				.param("pid", dvd.getId().toString())
				.param("number", "1")
				.param("cart", cart.toString())
		).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(getRedirectUrlByType(dvd.getType())));

		ShopItem cd = shopItemCatalog.findAll().filter(i -> i.getType().equals(ShopItem.ShopItemType.CD))
				.get().findFirst().get();

		mvc.perform(post("/cartAddShopItem")
				.param("pid", cd.getId().toString())
				.param("number", "1")
				.param("cart", cart.toString())
		).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(getRedirectUrlByType(cd.getType())));

		assertTrue(cart.stream().anyMatch(i -> i.getProduct().equals(dvd)));
		assertTrue(cart.stream().anyMatch(i -> i.getProduct().equals(cd)));

		CartItem dvdCi = cart.stream().filter(i -> i.getProduct().equals(dvd)).findFirst().get();

		mvc.perform(post("/editCart")
				.param("id", dvdCi.getId())
				.param("number", "0")
				.param("cart", cart.toString())
		).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/cart"));

		assertTrue(cart.stream().noneMatch(i -> i.getProduct().equals(dvd)));

		CartItem cdCi = cart.stream().filter(i -> i.getProduct().equals(cd)).findFirst().get();
		assertTrue(cdCi.getQuantity().isEqualTo(Quantity.of(1)));
		mvc.perform(post("/editCart")
				.param("id", cdCi.getId())
				.param("number", "5")
				.param("cart", cart.toString())
		).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/cart"));

		assertTrue(cart.getItem(cdCi.getId()).isPresent());
		assertTrue(cart.getItem(cdCi.getId()).get().getQuantity().isEqualTo(Quantity.of(5)));
	}

	private String getRedirectUrlByType(ShopItem.ShopItemType type) {
		switch (type) {
			case BOOK:
			default:
				return "/bookshop";
			case CD:
				return "/cdshop";
			case DVD:
				return "/dvdshop";
		}
	}

	/** remove those im leaving them here so i can update on another machine on another machine
	 *
	 * @Test
	 @WithMockUser(username = "boss", roles = "BOSS")
	 void acessingOrderDetailAsBoss() throws Exception {
	 mvc.perform(get("/order/{*id}"))
	 .andExpect(status().isOk())
	 .andExpect(view().name("orderdetail"))
	 .andExpect(model().attributeExists("order"))
	 .andExpect(model().attributeExists("orderLines"));

	 }
	 @Test
	 @WithMockUser(username = "bob", roles = "EMPLOYEE")
	 void acessingOrderDetailAsEmployee() throws Exception {
	 mvc.perform(get("/order/{*id}"))
	 .andExpect(status().isOk())
	 .andExpect(view().name("orderdetail"))
	 .andExpect(model().attributeExists("order"))
	 .andExpect(model().attributeExists("orderLines"));

	 }**/

}