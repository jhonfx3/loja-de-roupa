package br.com.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.validacao.CepValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CepValidator.class)
@Documented
public @interface Cep {
   
  String message() default "Cep inválido";
  Class<?>[] groups() default { };
  Class<? extends Payload>[] payload() default { };
  String value() default "";
}