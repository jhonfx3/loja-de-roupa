package teste;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import br.com.modelo.Cliente;
import util.JPAUtil;

public class TesteCliente {
	public static void main(String[] args) {

		Cliente cli = new Cliente();

		cli.setNome("AAAAAABBC");
		cli.setCpf("090768408-49");

		boolean deuErro = false;

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		Set<ConstraintViolation<Cliente>> constraintViolations = validator.validate(cli);

		if (!constraintViolations.isEmpty()) {
			deuErro = true;
		}

		for (ConstraintViolation error : constraintViolations) {
			String msgError = error.getMessage();
			System.out.println(msgError);
		}
		if (deuErro) {
			System.out.println("Deu erro, o objeto não está apto a ser persistido");
		} else {
			System.out.println("O objeto está apto a ser persistido");
		}

		if (!deuErro) {
			EntityManager em = new JPAUtil().getEntityManager();
			em.getTransaction().begin();
			em.persist(cli);
			em.getTransaction().commit();
		}

		System.out.println("FIM DO PROGRAMA");

	}
}
