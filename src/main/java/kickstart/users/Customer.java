package kickstart.users;

import kickstart.events.Event;
import lombok.Setter;
import org.salespointframework.useraccount.UserAccount;

import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.List;

@Entity
public class Customer {
	@Id
	@GeneratedValue
	private long id;

	private String address;
	private String zip;
	private String city;

	@ManyToMany(mappedBy = "participants")
	private List<Event> events;

	@OneToOne
	private UserAccount userAccount;

	public Customer() {
	}

	public Customer(UserAccount userAccount, String address, String zip, String city) {
		this.userAccount = userAccount;
		this.address = address;
		this.zip = zip;
		this.city = city;
	}

	public long getId() {
		return id;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		Assert.hasText(address, "Address must not be empty.");
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		Assert.hasText(zip, "Zip must not be empty.");
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		Assert.hasText(city, "Zip must not be empty.");
		this.city = city;
	}
}
