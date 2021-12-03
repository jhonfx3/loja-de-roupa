package br.com.validacao;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.interfaces.Cep;
import br.com.interfaces.EstaVazio;

public class VazioValidator implements ConstraintValidator<EstaVazio, String> {

	private String value;

	@Override
	public void initialize(EstaVazio vazio) {
		this.value = vazio.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Verifico se o valor é null, se for eu deixo passar
		// É porque se é null, é porque é um campo(atributo) opcional
		if (value == null) {
			System.out.println("é null");
			return true;
		}
		// Retorna se não está vazio
		return !value.isEmpty();
	}

}