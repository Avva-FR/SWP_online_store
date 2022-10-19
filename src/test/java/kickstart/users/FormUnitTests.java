package kickstart.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FormUnitTests {
	@Autowired
	private Validator validator;

	@Test
	void registrationFormCreation() {
		RegistrationForm registrationForm = new RegistrationForm("user", "test@test.de", "P@ssword123",
				"P@ssword123", "firstname", "lastname", "address", "11111", "city");

		assertThat(registrationForm.getUsername()).isEqualTo("user");
		assertThat(registrationForm.getEmail()).isEqualTo("test@test.de");
		assertThat(registrationForm.getPassword()).isEqualTo("P@ssword123");
		assertThat(registrationForm.getPasswordValidation()).isEqualTo("P@ssword123");
		assertThat(registrationForm.getFirstname()).isEqualTo("firstname");
		assertThat(registrationForm.getLastname()).isEqualTo("lastname");
		assertThat(registrationForm.getAddress()).isEqualTo("address");
		assertThat(registrationForm.getZip()).isEqualTo("11111");
		assertThat(registrationForm.getCity()).isEqualTo("city");

		Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(registrationForm);
		assertTrue(violations.isEmpty());
	}

	@Test
	void employeeRegistrationFormCreation() {

		EmployeeRegistrationForm employeeRegistrationForm = new EmployeeRegistrationForm("username", "P@ssword123", "P@ssword123", "email@schiller.de");

		assertThat(employeeRegistrationForm.getUsername()).isEqualTo("username");
		assertThat(employeeRegistrationForm.getPassword()).isEqualTo("P@ssword123");
		assertThat(employeeRegistrationForm.getPasswordValidation()).isEqualTo("P@ssword123");
		assertThat(employeeRegistrationForm.getEmail()).isEqualTo("email@schiller.de");

		Set<ConstraintViolation<EmployeeRegistrationForm>> violations = validator.validate(employeeRegistrationForm);
		assertTrue(violations.isEmpty());
	}

	@Test
	void EditFormUnitTest() {

		EditForm editForm = new EditForm("email@newemail.de", "newPassword1!", "newPassword1!",
				"newlastname", "newaddress", "22222", "newcity");

		assertThat(editForm.getEmail()).isEqualTo("email@newemail.de");
		assertThat(editForm.getPassword()).isEqualTo("newPassword1!");
		assertThat(editForm.getPasswordValidation()).isEqualTo("newPassword1!");
		assertThat(editForm.getLastname()).isEqualTo("newlastname");
		assertThat(editForm.getAddress()).isEqualTo("newaddress");
		assertThat(editForm.getZip()).isEqualTo("22222");
		assertThat(editForm.getCity()).isEqualTo("newcity");

		Set<ConstraintViolation<EditForm>> violations = validator.validate(editForm);
		assertTrue(violations.isEmpty());
	}

	@Test
	void EmployeeEditFormUnitTest() {
		EmployeeEditForm employeeEditForm = new EmployeeEditForm("newmail@mail.mail", "newPassword123!", "newPassword123!");

		assertThat(employeeEditForm.getEmail()).isEqualTo("newmail@mail.mail");
		assertThat(employeeEditForm.getPassword()).isEqualTo("newPassword123!");
		assertThat(employeeEditForm.getPasswordValidation()).isEqualTo("newPassword123!");

		Set<ConstraintViolation<EmployeeEditForm>> violations = validator.validate(employeeEditForm);
		assertTrue(violations.isEmpty());
	}

}