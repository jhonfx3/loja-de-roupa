package dao;

import javax.persistence.EntityManager;

import br.com.modelo.Cliente;
import br.com.modelo.Endereco;
import br.com.modelo.EnderecoEmpresa;
import util.JPAUtil;

public class EnderecoDAO {

	private EntityManager em;

	public EnderecoDAO() {
		this.em = new JPAUtil().getEntityManager();
	}

	// Pega o endereço por ID
	public Endereco getById(Integer id) {

		return em.createQuery("SELECT e from Endereco e WHERE e.id = :id", Endereco.class).setParameter("id", id)
				.getSingleResult();
	}

	// Exclui um endereço
	public void excluir(Endereco endereco) {
		em.getTransaction().begin();
		em.remove(endereco);
		em.getTransaction().commit();
	}

}
