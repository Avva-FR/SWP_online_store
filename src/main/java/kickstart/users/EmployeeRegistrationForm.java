package kickstart.users;

import kickstart.utils.CheckEqual;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@CheckEqual(field1 = "password", field2 = "passwordValidation")
public class EmployeeRegistrationForm {
	@NotEmpty(message = "{RegistrationForm.NotEmpty}")
	private final String username;

	@NotEmpty(message = "{RegistrationForm.NotEmpty}")
	// Recommendation from BSI
	@Pattern(regexp = "^(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*\\d+)(?=.*\\W+).{8,}$",
			message = "{RegistrationForm.InvalidPassword}")
	private final String password;

	private final String passwordValidation;

	@NotEmpty(message = "{RegistrationForm.NotEmpty}")
	//siehe https://www.ietf.org/rfc/rfc5322.txt
	@Pattern(regexp = "(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
			"|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
			"|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
			"@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?" +
			"|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}" +
			"(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])" +
			"|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]" +
			"|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
			message = "{RegistrationForm.InvalidEmail}")
	private final String email;

	public EmployeeRegistrationForm(String username, String password, String passwordValidation, String email) {
		this.username = username;
		this.password = password;
		this.passwordValidation = passwordValidation;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordValidation() {
		return passwordValidation;
	}

	public String getEmail() {
		return email;
	}
}
