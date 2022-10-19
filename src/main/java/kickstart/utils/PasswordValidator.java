package kickstart.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String>  {
	private static final Pattern PASSWORD = Pattern.compile("^(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*\\d+)(?=.*\\W+).{8,}$");
	private boolean allowEmpty;

	@Override
	public void initialize(Password constraint) {
		allowEmpty = constraint.allowEmpty();
	}

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		if (s == null) { // by default this shouldn't happen
			return false;
		}

		if (allowEmpty && s.equals("")) {
			return true;
		}

		Matcher m = PASSWORD.matcher(s);
		return m.matches();
	}
}
