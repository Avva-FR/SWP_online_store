package kickstart.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * http://dolszewski.com/java/multiple-field-validation/
 * Überwiegend davon inspiriert bzw. stellenweise übernommen.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CheckEqualValidator.class})
public @interface CheckEqual {
	String message() default "not equal";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

	String field1();
	String field2();
	boolean allowNull() default false;
}