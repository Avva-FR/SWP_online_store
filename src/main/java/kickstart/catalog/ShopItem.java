package kickstart.catalog;

import lombok.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.salespointframework.catalog.Product;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopItem extends Product {
	private String creator;
	private String publisher;
	private String idnumber;
	private String description;
	private String imageLink;
	private String genre;
	private ShopItemType type;

	public ShopItem(String name, MonetaryAmount price, String creator, String idnumber, String publisher,
					String description, String imageLink, String genre, ShopItemType type) {
		super(name, price);
		this.creator = creator;
		this.idnumber = idnumber;
		this.publisher = publisher;
		this.description = description;
		this.imageLink = imageLink;
		this.genre = genre;
		this.type = type;
	}

	@Transient
	public String getPhotosImagePath() {
		if (Objects.isNull(imageLink)) {
			return null;
		}

		return "/user-photos/" + imageLink;
	}

	public enum ShopItemType {CD, DVD, BOOK}

}