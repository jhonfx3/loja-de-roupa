package br.com.validacao;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.interfaces.Cep;
import br.com.interfaces.Telefone;

public class TelefoneValidator implements ConstraintValidator<Telefone, String> {

	private String value;

	@Override
	public void initialize(Telefone telefone) {
		this.value = telefone.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// return value.matches("^\\([1-9]{2}\\)\\ [2-9][0-9]{3,4}\\-[0-9]{4}$");
		// Uso uma expressão regular para validar um telefone ou celular
		// OBS: Os links usados estão nos documentos de texto
		// Enontrei no stack over flow (site)
		return value.matches(
				"/^1\\d\\d(\\d\\d)?$|^0800 ?\\d{3} ?\\d{4}$|^(\\(0?([1-9a-zA-Z][0-9a-zA-Z])?[1-9]\\d\\) ?|0?([1-9a-zA-Z][0-9a-zA-Z])?[1-9]\\d[ .-]?)?(9|9[ .-])?[2-9]\\d{3}[ .-]?\\d{4}$");
	}

}