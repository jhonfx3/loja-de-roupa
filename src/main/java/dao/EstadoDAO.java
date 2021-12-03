package dao;

import javax.persistence.EntityManager;

import br.com.modelo.Estado;
import util.JPAUtil;

public class EstadoDAO {

	private EntityManager em;

	public EstadoDAO() {
		this.em = new JPAUtil().getEntityManager();
	}

// Grava um estado
	public void gravar(Estado estado) {
		em.getTransaction().begin();
		em.persist(estado);
		em.getTransaction().commit();
		// em.close();
	}

	// Pega um estado por ID
	public Estado getEstado(Integer id) {
		return em.find(Estado.class, id);
	}

}
