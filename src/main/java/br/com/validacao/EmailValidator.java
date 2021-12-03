package br.com.validacao;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.interfaces.ValidaEmail;

public class EmailValidator implements ConstraintValidator<ValidaEmail, String> {

	private String value;

	@Override
	public void initialize(ValidaEmail cep) {
		this.value = cep.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null)
			return true;
		// Se eu verifico que está vazio
		// Então eu deixo outro validator(IsEmpty) ficar encarregado dessa validação
		// Faço isso para aparecer a mensagem de erro adequada...
		if (value.isEmpty()) {
			return true;
		}
		// Se o valor contém vírgula
		if (value.contains(",")) {
			// Corto o value a partir da vírgula + 1
			// fazendo value receber ele mesmo
			value = value.substring(value.indexOf(",") + 1);
			System.out.println("O email é: " + value);
		}
		// Utilizo expressão regular para validar um e-mail
		String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		return value.matches(regex) == true;

	}

}