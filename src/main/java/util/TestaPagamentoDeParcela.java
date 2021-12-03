package util;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.modelo.Parcela;
import dao.ParcelaDAO;

public class TestaPagamentoDeParcela {

	public static void main(String[] args) {
		EntityManager em = new JPAUtil().getEntityManager();
		Integer id = 91;
		List<Parcela> parcelas = new ParcelaDAO().getParcelaByClienteEDoc(4, 28);
		for (int i = 0; i < parcelas.size(); i++) {
			if (parcelas.get(i).getId() == id) {
				if (i == 0 && !parcelas.get(i).isStatus()) {
					System.out.println("Posso pagar a parcela");
					System.out.println("i = " + i);
				}else if (parcelas.get(i).isStatus()) {
					System.out.println("A parcela já está paga");
				}else if (i > 0 && parcelas.get(i - 1).isStatus()) {
					System.out.println("Posso pagar a parcela");
				} else {
					System.out.println("Não posso pagar a parcela, pois a anterior não está paga");
				}

			}
		}

	}

}
