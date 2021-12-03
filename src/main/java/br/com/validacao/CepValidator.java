package br.com.validacao;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.interfaces.Cep;

public class CepValidator implements ConstraintValidator<Cep, String> {

	private String value;

	@Override
	public void initialize(Cep cep) {
		this.value = cep.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Verifica se é nulo, se é, deixa passar.
		// Significa que é um campo opcional
		if (value == null) {
			System.out.println("é null");
			return true;
		}
		// Retorna se é um CEP válido
		return value.matches("[0-9]{5}-[0-9]{3}");
	}

}