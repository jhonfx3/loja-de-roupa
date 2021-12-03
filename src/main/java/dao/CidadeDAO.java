package dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.modelo.Cidade;
import br.com.modelo.Estado;
import util.JPAUtil;

public class CidadeDAO {

	private EntityManager em;

	public CidadeDAO() {
		this.em = new JPAUtil().getEntityManager();
	}

	public void gravar(Cidade cidade) {
		em.getTransaction().begin();
		em.persist(cidade);
		em.getTransaction().commit();
		// em.close();
	}
	// Retorna todas as cidades
	public List<Cidade> findAllCidades() {
		return em.createQuery("SELECT c from Cidade c", Cidade.class).getResultList();
	}

}
