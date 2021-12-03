package util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.EntityManager;

import br.com.modelo.Cliente;
import br.com.modelo.Documento;
import br.com.modelo.Parcela;
import dao.ClienteDAO;

public class TestaParcela {

	public static void main(String[] args) throws ParseException {
		EntityManager em = new JPAUtil().getEntityManager();
		em.getTransaction().begin();
		Documento documento = new Documento();
		Cliente cli = new ClienteDAO().getEm().find(Cliente.class, 60);
		Integer qtdParcelas = 5;

		Calendar vencimento = Calendar.getInstance();

		for (int i = 0; i < qtdParcelas; i++) {
			Parcela parcela = new Parcela();
			parcela.setDocumento(documento);
			// parcela.setValor(new BigDecimal("4500.50"));
			parcela.setDataCompra(Calendar.getInstance());
			parcela.setVencimento(vencimento);
			vencimento.add(Calendar.MONTH, +1);
			vencimento = (Calendar) vencimento.clone();

			parcela.setCliente(cli);
			em.persist(parcela);
		}

		em.getTransaction().commit();

	}

}
