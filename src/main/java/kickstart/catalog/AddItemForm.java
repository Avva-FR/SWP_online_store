package kickstart.catalog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.javamoney.moneta.Money;

import static org.salespointframework.core.Currencies.EURO;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@Getter
@Setter
public class AddItemForm {

	@NotEmpty(message = "{AddItemForm.NotEmpty}")
	private final String title;

	@Min(value = 0, message = "{AddItemForm.NotNegative}")
	private float price;

	@NotEmpty(message = "{AddItemForm.NotEmpty}")
	private final String creator;

	@NotEmpty(message = "{AddItemForm.NotEmpty}")
	private final String publisher;

	@NotEmpty(message = "{AddItemForm.NotEmpty}")
	/*@Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$",
			message = "{AddItemForm.InvalidISBN}")*/
	private final String idnumber;

	@NotEmpty(message = "{AddItemForm.NotEmpty}")
	private final String description;

	@NotEmpty(message = "{AddItemForm.NotEmpty}")
	private final String genre;

	@NotEmpty(message = "{AddItemForm.NotEmpty}")
	private final String type;

	@Min(value = 0)
	private int quantity;

	public ShopItem toShopItem(String productImage){
		return new ShopItem(title, Money.of(price, EURO), creator ,idnumber , publisher , description, productImage,
				genre, ShopItem.ShopItemType.valueOf(type));
	}
}
