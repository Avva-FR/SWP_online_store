package kickstart.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;

/*
 * http://dolszewski.com/java/multiple-field-validation/
 * Überwiegend davon inspiriert bzw. stellenweise übernommen.
 */
public class CheckEqualValidator implements ConstraintValidator<CheckEqual, Object> {
	private String field1;
	private String field2;
	private boolean allowNull;

	@Override
	public void initialize(CheckEqual constraint) {
		field1 = constraint.field1();
		field2 = constraint.field2();
		allowNull = constraint.allowNull();
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		try {
			Object baseFieldValue = getFieldValue(object, field1);
			Object matchFieldValue = getFieldValue(object, field2);
			if (allowNull && Objects.isNull(baseFieldValue) && Objects.isNull(matchFieldValue)) {
				return true;
			}

			return baseFieldValue != null && baseFieldValue.equals(matchFieldValue);
		} catch (ReflectiveOperationException ex) {
			return false;
		}
	}

	private Object getFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Class<?> clazz = object.getClass();
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(object);
	}
}
