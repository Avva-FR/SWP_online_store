/**
package kickstart.catalog;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ShopItemCatalogIntegrationTest {

	@Autowired
	ShopItemCatalog shopItemCatalog;

	@Test // adjust this to the ammount of data initialized in
	void findAllCatalogitems() {
		Iterable<ShopItem> result = shopItemCatalog.findAll();
		assertThat(result).hasSize(14);

	}
}
*/