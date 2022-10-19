package kickstart.users;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Employee {
	@Id
	@GeneratedValue
	private long id;

	@OneToOne
	private UserAccount userAccount;

	public Employee() {
	}

	public Employee(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public long getId() {
		return id;
	}

	public String getEmail() {
		return userAccount.getEmail();
	}
}
