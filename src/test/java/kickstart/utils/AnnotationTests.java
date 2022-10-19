package kickstart.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnnotationTests {
	@Test
	void testPasswordValidatorNonNull() {
		Password password = mock(Password.class);
		when(password.allowEmpty()).thenReturn(false);

		PasswordValidator validator = new PasswordValidator();
		validator.initialize(password);

		// ValidatorContext is not used. In this case it should be okay to set it to null
		assertTrue(validator.isValid("Test@123", null));
		assertFalse(validator.isValid("Test123", null));
		assertFalse(validator.isValid("", null));
		assertFalse(validator.isValid(null, null)); // null should always return false
	}

	@Test
	void testPasswordValidatorNull() {
		Password password = mock(Password.class);
		when(password.allowEmpty()).thenReturn(true);

		PasswordValidator validator = new PasswordValidator();
		validator.initialize(password);

		// ValidatorContext is not used. In this case it should be okay to set it to null
		assertTrue(validator.isValid("Test@123", null));
		assertFalse(validator.isValid("Test123", null));
		assertTrue(validator.isValid("", null));
		assertFalse(validator.isValid(null, null)); // null should always return false
	}

	@Test
	void testCheckEqual() {
		CheckEqual checkEqual = mock(CheckEqual.class);
		when(checkEqual.field1()).thenReturn("field1");
		when(checkEqual.field2()).thenReturn("field2");
		when(checkEqual.allowNull()).thenReturn(false);

		Object equalObj = new Object() {
			String field1 = "";
			String field2 = "";
		};

		CheckEqualValidator validator = new CheckEqualValidator();
		validator.initialize(checkEqual);

		assertTrue(validator.isValid(equalObj, null));

		Object nonEqualObj = new Object() {
			String field1 = "alpha";
			String field2 = "beta";
		};
		assertFalse(validator.isValid(nonEqualObj, null));

		Object withNull = new Object() {
			String field1 = null;
			String field2 = null;
		};
		assertFalse(validator.isValid(withNull, null));
	}

	@Test
	void testCheckEqualNull() {
		CheckEqual checkEqual = mock(CheckEqual.class);
		when(checkEqual.field1()).thenReturn("field1");
		when(checkEqual.field2()).thenReturn("field2");
		when(checkEqual.allowNull()).thenReturn(true);

		Object equalObj = new Object() {
			String field1 = "";
			String field2 = "";
		};

		CheckEqualValidator validator = new CheckEqualValidator();
		validator.initialize(checkEqual);

		assertTrue(validator.isValid(equalObj, null));

		Object nonEqualObj = new Object() {
			String field1 = "alpha";
			String field2 = "beta";
		};
		assertFalse(validator.isValid(nonEqualObj, null));

		Object withNull = new Object() {
			String field1 = null;
			String field2 = null;
		};
		assertTrue(validator.isValid(withNull, null));
	}

	@Test
	void testInvalidObject() {
		CheckEqual checkEqual = mock(CheckEqual.class);
		when(checkEqual.field1()).thenReturn("field1");
		when(checkEqual.field2()).thenReturn("field2");

		Object obj = new Object();

		CheckEqualValidator validator = new CheckEqualValidator();
		validator.initialize(checkEqual);

		assertFalse(validator.isValid(obj, null));
	}
}
