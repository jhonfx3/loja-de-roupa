package br.com.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.validacao.UniqueValidator;
import dao.ClienteDAO;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueValidator.class)
@Documented
public @interface Unique {
	String message() default "{unique.value.violation}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<ClienteDAO> service();

	String serviceQualifier() default "";

	String fieldName();
}