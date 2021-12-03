package br.com.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.validacao.CepValidator;
import br.com.validacao.DataValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DataValidator.class)
@Documented
public @interface Data {

	String message() default "Data inválida";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String value() default "";
}