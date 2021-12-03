package dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.interfaces.UserService;
import br.com.modelo.Cliente;
import br.com.modelo.Endereco;
import br.com.modelo.EnderecoEmpresa;
import gui.FormClienteController;
import gui.util.Alerts;
import util.JPAUtil;

public class ClienteDAO implements UserService {

	private EntityManager em;

	public ClienteDAO() {
		this.em = new JPAUtil().getEntityManager();
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	// Grava ou atualiza um cliente
	public void gravarOuAtualizar(Cliente cliente) {
		// Se o cliente ainda não possui um ID. Então eu tenho que cadastrar
		// pois null indica isso, ainda não possui chave. é nulo
		if (cliente.getId() == null) {
			em.getTransaction().begin();
			System.out.println("Tenho que cadastrar");
			em.persist(cliente);
			em.getTransaction().commit();
			Alerts.showInformation("Sucesso", "Cliente cadastrado com sucessso", "");

		} else {
			em.getTransaction().begin();
			System.out.println("Tenho que editar");
			if (cliente.getEnderecoEmpresa() != null) { // Se eu tenho um endereço empresa
				if (cliente.getEnderecoEmpresa().getId() == null) { // Mas ele ainda necessita ser criado
					em.persist(cliente.getEnderecoEmpresa());
				} else {// Não necessita ser criado, já existe, só preciso atualizar
					em.merge(cliente.getEnderecoEmpresa());
				}

			}
			em.merge(cliente);
			em.merge(cliente.getEndereco());

			em.getTransaction().commit();
			Alerts.showInformation("Sucesso", "Cliente alterado com sucessso", "");

		}
	}

	// Pega um cliente de acordo com um ID
	public Cliente getById(Integer id) {

		return em.createQuery("SELECT c from Cliente c JOIN FETCH c.endereco endereco WHERE c.id = :id", Cliente.class)
				.setParameter("id", id).getSingleResult();
	}

	// Pega um cliente de acordo com o CPF
	public Cliente getByCPf(String cpf) {

		return em
				.createQuery("SELECT c from Cliente c JOIN FETCH c.endereco endereco WHERE c.cpf = :cpf", Cliente.class)
				.setParameter("cpf", cpf).getSingleResult();
	}

	// Pega uma lista de clientes de acordo com o nome ou CPF
	public List<Cliente> getByCPfOuNome(String cpf, String nome) {

		return em.createQuery(
				"SELECT c from Cliente c JOIN FETCH c.endereco endereco WHERE c.cpf = :cpf OR c.nome LIKE :nome",
				Cliente.class).setParameter("cpf", cpf).setParameter("nome", "%" + nome + "%").getResultList();
	}

	// Pega um cliente de acordo com o nome
	public List<Cliente> getByNome(String nome) {

		return em.createQuery("SELECT c from Cliente c JOIN FETCH c.endereco endereco WHERE c.nome LIKE :nome",
				Cliente.class).setParameter("nome", "%" + nome + "%").getResultList();
	}

// Pega todos os clientes
	public List<Cliente> findAll() {
		return em.createQuery("SELECT c from Cliente c JOIN FETCH c.endereco endereco", Cliente.class).getResultList();
	}

	// Método responsável por validar um fieldname. Nesse caso são: E-mail e CPF
	// Para ver se já existe na base de dados
	@Override
	public boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException {
		// Se o valor é null, então eu deixo passar, retornando false.
		if (value == null) {
			return false;
		}
		System.out.println(value.toString());
		System.out.println(fieldName);
		String valueStr = value.toString();
		System.out.println("Valor original: " + valueStr);
		Integer id;
		// Se o valor contém vírgula
		// significa que é um processo de edição. Porque na edição eu mando ID,cpf ou
		// ID,email
		if (valueStr.contains(",") && FormClienteController.getController().getEntity().getId() != null) {
			// Eu capturo numa variável Integer a substring de 0 até o índice que encontrar
			// a vírgula.
			// Ou seja, capturo o ID
			id = Integer.valueOf(valueStr.substring(0, valueStr.indexOf(",")));
			// Eu corto do value o ID + e a vírgula.
			// Ex: 123,cpf ficaria somente o cpf
			// Curto após encontrar a vírgual, para pegar somente o CPF ou e-mail.
			valueStr = valueStr.substring(valueStr.indexOf(",") + 1);
		} else {
			// Senão não tem ID, então ele é null
			id = null;
		}

		System.out.println("Valor --> " + valueStr);
		List<Cliente> clientes;
		// Esse if verifica se eu estou editando, se eu estiver eu preciso deixar passar
		// caso o CPF já seja o que ele está utilizando
		if (id != null) {
			// Se for um processo de edição, que é quando o id é diferente de null
			// que é esse caso:
			// Eu preciso verificar se já tem alguém na base de dados com
			// esse cpf ou e-mail, mas além disso que seja diferente do ID do dono do cpf ou
			// e-mail
			// Ou seja para poder permitir que seja editado o campo cpf ou e-mail do cliente
			// possuiente
			// desses atributos
			// Ex: Cliente: João ID 4 e cpf ABC. Eu preciso verificar se na base de dados já
			// existe alguém
			// com esses dados. Mas que seja DIFERENTE desse ID. Porque ele é o único que
			// pode editar esse valor
			// então ele não entra na regra
			clientes = em.createQuery("SELECT c from Cliente c WHERE c." + fieldName + " = :value AND c.id <> :id",
					Cliente.class).setParameter("value", valueStr).setParameter("id", id).getResultList();
		} else {
			// Senão eu simplesmente verifico se já existe alguém na base de dados com esse
			// e-mail ou cpf
			// Se já existir, retorna um registro e por isso não é vazio. Logo não dará
			// certo o
			// cadastro.
			// Ex: Já existe um cliente com o cpf ABC que é o que eu estou tentando
			// cadastrar para um cliente novo
			clientes = em.createQuery("SELECT c from Cliente c WHERE c." + fieldName + " = :value", Cliente.class)
					.setParameter("value", valueStr).getResultList();
		}
		// Simplesmente retorna se a lista veio não vazia
		return !clientes.isEmpty();
	}

	// Lógica para remover um cliente
	public void remover(Cliente cliente) {
		em.getTransaction().begin();

		cliente = em.find(Cliente.class, cliente.getId());
		cliente.setEndereco(em.find(Endereco.class, cliente.getEndereco().getId()));

		if (cliente.getEnderecoEmpresa() != null) {
			cliente.setEnderecoEmpresa(em.find(EnderecoEmpresa.class, cliente.getEnderecoEmpresa().getId()));
		}

		em.remove(cliente);
		em.getTransaction().commit();
	}

	// Método que não utilizo mais, era para tornar um objeto gerenciavel
	// Utilizando a mesma lógica acima
	public Object transformaEmGerenciavel(Object objeto) {
		Object obj = em.merge(objeto);
		return obj;
	}

}
