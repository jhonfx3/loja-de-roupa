package br.com.validacao;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.interfaces.Cep;
import br.com.interfaces.Data;

public class DataValidator implements ConstraintValidator<Data, String> {

	private String value;

	@Override
	public void initialize(Data data) {
		this.value = data.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		String data = value;
		// Eu preciso verificar se também não está vazio
		// Porque se estiver vazio ele entra nesse if(porque o tamanho é 0 <5) e dá:
		// data inválida
		// De qualquer forma a data é inválida, mas a mensagem ideal é de que é
		// obrigatório
		// O que é feito por outra validação
		
		// Se a data é vazia então eu deixo passar, e deixo outro validator ficar encarregado(IsEmpty)
		if (data.isEmpty())
			return true;
		// Se não tiver quantidade de caracteres suficientes
		// Então é data inválida
		if (data.length() < 5)
			return false;
		String dia, mes;
		dia = data.substring(0, data.indexOf("/"));
		mes = data.substring(data.indexOf("/") + 1, data.indexOf("/") + 3);
		System.out.println("Dia: " + dia + " Mês: " + mes);
		boolean invalido = true;
		if (dia.equals("00") || mes.equals("00")) {
			System.out.println("00 ou 00 por isso recebeu true");
			invalido = false;
		}

		if (Integer.valueOf(dia) > 31 || Integer.valueOf(mes) > 12) {
			System.out.println("> 31 ou > 12 por isso recebeu true");
			invalido = false;
		}

		return invalido;
	}

}