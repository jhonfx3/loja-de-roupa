package br.com.validacao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.interfaces.Cep;
import br.com.interfaces.Data;
import br.com.interfaces.PassadoOuPresente;

public class PassadoPresenteValidator implements ConstraintValidator<PassadoOuPresente, String> {

	private String value;

	@Override
	public void initialize(PassadoOuPresente passadoOuPresente) {
		this.value = passadoOuPresente.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean invalido = true;
		String valor = value;
		int total = valor.replaceAll("[^//]", "").length();
		System.out.println("Quantidade de barras: " + total);
		// Se não tem duas barras então não é data completa
		// Logo devo deixar passar, porque não é apto para poder validar
		if (total < 2) {
			return true;
		}
		SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
		// Captura a data atual
		Calendar dataAtual = Calendar.getInstance();
		Calendar dataInformada = Calendar.getInstance();
		Date parse = null;
		try {
			parse = s.parse(valor);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataInformada.setTime(parse);

		if (dataInformada.after(dataAtual)) {
			System.out.println(s.format(dataInformada.getTime()));
			System.out.println("AFTER");
			invalido= false;
		}

		/**
		 * Não preciso verificar se tem pelo menos 1 caracter após a segunda barra
		 * porque ao ter 2 barras, já tem um número após String cortar =
		 * valor.substring(0,valor.indexOf("/")); cortar =
		 * valor.substring(0,valor.indexOf("/"));
		 **/
		return invalido;
	}

}