package kickstart.catalog;

import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;

public interface ShopItemCatalog extends Catalog<ShopItem> {

	static final Sort DEFAULT_SORT = Sort.by("productIdentifier").descending();

	Iterable<ShopItem> findByType(ShopItem.ShopItemType type, Sort sort);

	boolean existsByProductIdentifier_Id(String id);




	default Iterable<ShopItem> findByType(ShopItem.ShopItemType type) {
		return findByType(type, DEFAULT_SORT);
	}
}