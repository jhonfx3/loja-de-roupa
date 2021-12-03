package br.com.validacao;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.interfaces.FieldValueExists;
import br.com.interfaces.Unique;
import dao.ClienteDAO;

public class UniqueValidator implements ConstraintValidator<Unique, Object> {

	private ClienteDAO service;
	private String fieldName;

	@Override
	public void initialize(Unique unique) {
		// Class<? extends FieldValueExists> clazz = unique.service();
		// Faço o fieldname receber o fieldName
		this.fieldName = unique.fieldName();
		// System.out.println("FIELD NAME ====> " + this.fieldName);
//		String serviceQualifier = unique.serviceQualifier();
		try {
			// Atributo ao clienteDAO a instanciação do contrato
			// Porém, acredito que isso nem seja necessário, eu provavelmente nem
			// estou utilizando esse service.
			// OBS: Pode dar exceção, por isso o try catch.
			this.service = unique.service().getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		// Instancio um DAO
		ClienteDAO servico = new ClienteDAO();
		// Uso para validar o retorno do método fieldValueExists
		return !servico.fieldValueExists(value, this.fieldName);
	}

}
