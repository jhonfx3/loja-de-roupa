package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import br.com.modelo.Cliente;
import br.com.modelo.Documento;
import br.com.modelo.Parcela;
import gui.util.Alerts;
import util.JPAUtil;

public class ParcelaDAO {

	private EntityManager em;

	public ParcelaDAO() {
		this.em = new JPAUtil().getEntityManager();
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	// Pega uma lista de parcelas de acordo com o ID do cliente
	public List<Parcela> getParcelaByClienteId(Integer id) {

		return em.createQuery("SELECT p from Parcela p JOIN FETCH p.cliente c WHERE c.id = :id", Parcela.class)
				.setParameter("id", id).getResultList();
	}

// Pega uma lista de parcelas de acordo com o ID do cliente e se já está vencida
	public List<Parcela> getParcelaByClienteIdVencidas(Integer id) {

		return em.createQuery(
				"SELECT p from Parcela p JOIN FETCH p.cliente c WHERE c.id = :id AND CURRENT_DATE > p.vencimento AND p.status = false",
				Parcela.class).setParameter("id", id).getResultList();
	}

	// Pega todas as parcelas
	public List<Parcela> findAll() {

		return em.createQuery("SELECT p from Parcela p", Parcela.class).getResultList();
	}

	// Pega uma lista de parcelas de acordo com o ID do cliente e número do
	// documento
	public List<Parcela> getParcelaByClienteEDoc(Integer id, Integer doc) {

		return em.createQuery(
				"SELECT p from Parcela p JOIN FETCH p.cliente c JOIN FETCH p.documento doc WHERE c.id = :id AND doc.num_doc = :doc",
				Parcela.class).setParameter("id", id).setParameter("doc", doc).getResultList();
	}

	public Parcela getParcelaByIdEDoc(Integer id, Integer doc) {
		try {
			return em.createQuery(
					"SELECT p from Parcela p JOIN FETCH p.cliente c JOIN FETCH p.documento doc WHERE p.id = :id AND doc.num_doc = :doc",
					Parcela.class).setParameter("id", id).setParameter("doc", doc).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Parcela getParcelaByDataEDoc(Calendar vencimento, Integer doc) {
		try {
			return em.createQuery(
					"SELECT p from Parcela p JOIN FETCH p.cliente c JOIN FETCH p.documento doc WHERE p.vencimento = :vencimento AND doc.num_doc = :doc",
					Parcela.class).setParameter("vencimento", vencimento).setParameter("doc", doc).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	// Pega uma lista de parcelas de acordo com o ID do cliente, número do documento
	// e se já está vencida
	public List<Parcela> getParcelaByClienteEDocVencidas(Integer id, Integer doc) {

		return em.createQuery(
				"SELECT p from Parcela p JOIN FETCH p.cliente c JOIN FETCH p.documento doc WHERE c.id = :id AND doc.num_doc = :doc AND CURRENT_DATE > p.vencimento AND p.status = false",
				Parcela.class).setParameter("id", id).setParameter("doc", doc).getResultList();
	}

	// Lógica para efetuar o pagamento de uma parcela
	public void pagarParcela(List<Parcela> parcelas, Parcela parcela) {
		// Pego o id da parcela
		Integer id = parcela.getId();
		// Variável que vai exibir uma mensagem
		String msg = "";
		// Vai controlar se foi sucesso ou falha
		boolean sucesso = false;

		if (parcela.isStatus()) {
			// System.out.println("A parcela já está paga");
			msg = "A parcela já está paga";
			sucesso = true;
		} else {
			// Modo antigo \/
			// Parcela parcelaByIdEDoc = getParcelaByIdEDoc(parcela.getId() - 1,
			// parcela.getDocumento().getNum_doc());
			Calendar vencimento = parcela.getVencimento();
			vencimento = (Calendar) vencimento.clone();
			vencimento.add(Calendar.MONTH, -1);
			Parcela parcelaByDataEDoc = getParcelaByDataEDoc(vencimento, parcela.getDocumento().getNum_doc());
			if (parcelaByDataEDoc == null) {
				// System.out.println("É a primeira parcela, pode pagar");
				msg = "Parcela paga com sucesso";
				sucesso = true;
			} else {
				if (parcelaByDataEDoc.isStatus()) {
					// System.out.println("A parcela pode ser paga porque a anterior já está paga");
					msg = "Parcela paga com sucesso";
					sucesso = true;
				} else {
					// System.out.println("A parcela não pode ser paga porque a anterior ainda não
					// está paga");
					msg = "Você não pode pagar essa parcela pois a anterior ainda NÃO ESTÁ PAGA" + "\n"
							+ "Dados dessa parcela não paga: ID " + parcelaByDataEDoc.getId() + " Num Doc: "
							+ parcelaByDataEDoc.getDocumento().getNum_doc();
					sucesso = false;
				}
			}

		}
		// SE EU CONSEGUI PAGAR A PARCELA
		if (sucesso) {
			// LÓGICA PARA PAGAR A PARCELA ATUALIZANDO SEU STATUS PARA TRUE
			em.getTransaction().begin();
			Parcela parcelaPagar = em.find(Parcela.class, id);
			parcelaPagar.setStatus(true);
			em.merge(parcelaPagar);
			em.getTransaction().commit();
			// EXIBO UMA CAIXA DE MENSAGEM COM SUCESSO PARA O USUÁRIO
			Alerts.showInformation("Sucesso", msg, "");
		} else {
			// EXIBO UMA CAIXA DE FALHA PARA O USUÁRIO
			Alerts.showError("Falha", msg, "");
		}

	}

	// Lógica para gravar ou atualizar uma parcela, ou melhor, uma lista de parcelas
	// Mas ela tem um retorno. Fique atento a isso.
	public List<Parcela> gravarOuAtualizar(Parcela parcela, Integer qtd, Cliente cli, boolean maisParcelas) {
		List<Parcela> parcelas = new ArrayList<>();
		// Se a minha parcela não tem ID, então é cadastro e não consulta
		if (parcela.getId() == null) {
			em.getTransaction().begin();
			// Capturo numa variável a data da compra. Mas .clone e não referencia
			Calendar data = (Calendar) parcela.getDataCompra().clone();
			// Instancio um documento
			Documento documento = new Documento();
			// Capturo no estado managed o cliente da parcelas que desejo cadastrar
			cli = em.find(Cliente.class, cli.getId());
			BigDecimal divisor = new BigDecimal(qtd);
			BigDecimal valorDaParcela = parcela.getValorDaCompra().divide(divisor, 2, RoundingMode.HALF_UP);
			data = (Calendar) data.clone();
			data.add(Calendar.MONTH, +1);
			List<Parcela> devolveParcelas = devolveParcelas(qtd, documento, parcela, valorDaParcela, data, cli);

			for (Parcela parcela2 : devolveParcelas) {
				em.persist(parcela2);
			}
			em.getTransaction().commit();
			return devolveParcelas;
		} else {
			// System.out.println("Cheguei aqui, tenho que alterar essa parcela");
			em.getTransaction().begin();

			Long qtdParcelasLong = getQuantidadeParcelas(parcela.getDocumento().getNum_doc());
			Integer qtdParcelas = qtdParcelasLong.intValue();
			BigDecimal divisor;
			// Se eu tenho que adicionar mais parcelas
			// A quantidade é a quantidade já existente + qtd
			if (maisParcelas) {
				divisor = new BigDecimal(qtdParcelas + qtd);
			} else {
				divisor = new BigDecimal(qtdParcelas);
			}
			BigDecimal valorDaParcela = parcela.getValorDaCompra().divide(divisor, 2, RoundingMode.HALF_UP);
			List<Parcela> parcelasAtualizar = getParcelaByClienteEDoc(parcela.getCliente().getId(),
					parcela.getDocumento().getNum_doc());
			BigDecimal valorDaCompra = parcela.getValorDaCompra();

			for (Parcela parcela2 : parcelasAtualizar) {
				parcela2.setValorDaCompra(valorDaCompra);
				parcela2.setValorDaParcela(valorDaParcela);
				em.merge(parcela2);
			}

			// Caso eu fosse atualizar uma parcela só
			// em.merge(parcela);

			em.getTransaction().commit();
			if (maisParcelas) {
				// System.out.println("Tenho que adicionar mais algumas parcelas");
				em.getTransaction().begin();
				Documento documento = new Documento();
				documento = em.find(Documento.class, parcela.getDocumento().getNum_doc());
				Calendar data = parcelasAtualizar.get(parcelasAtualizar.size() - 1).getVencimento();
				// Já estou somando 1 mês
				data = (Calendar) data.clone();
				data.add(Calendar.MONTH, +1);
				// Tenho que calcular o novo valor da parcela já que estou adicionando mais
				// parcelas
				Integer qtd2 = qtdParcelas + qtd;
				BigDecimal divisor2 = new BigDecimal(qtd2);
				BigDecimal valorDaParcela2 = parcela.getValorDaCompra().divide(divisor2, 2, RoundingMode.HALF_UP);
				// Tenho que passar o cliente já setado nas parcelas e ignorar da combobox
				List<Parcela> devolveParcelas = devolveParcelas(qtd, documento, parcela, valorDaParcela2, data,
						parcelasAtualizar.get(0).getCliente());
				for (Parcela parcela2 : devolveParcelas) {
					em.persist(parcela2);
				}
				em.getTransaction().commit();
				parcelasAtualizar.addAll(devolveParcelas);
			}
			return parcelasAtualizar;
		}
		// Quando eu apenas tinha o registrar, agora eu tenho o editar também, logo são
		// 2 retornos
		// return parcelas;
	}

	private List<Parcela> devolveParcelas(Integer qtd, Documento documento, Parcela parcela, BigDecimal valorDaParcela,
			Calendar data, Cliente cli) {
		List<Parcela> parcelas = new ArrayList<>();
		for (int i = 0; i < qtd; i++) {
			Parcela parcelaParaGravar = new Parcela();
			parcelaParaGravar.setDocumento(documento);
			parcelaParaGravar.setValorDaCompra(parcela.getValorDaCompra());
			parcelaParaGravar.setValorDaParcela(valorDaParcela);
			parcelaParaGravar.setDataCompra(parcela.getDataCompra());
			parcelaParaGravar.setVencimento(data);
			data = (Calendar) data.clone();
			data.add(Calendar.MONTH, +1);
			parcelaParaGravar.setCliente(cli);

			// Adiciono ela na lista
			parcelas.add(parcelaParaGravar);
		}
		return parcelas;
	}

	public void remover(Parcela parcela) {
		// System.out.println("Tenho que remover essa parcela");
		em.getTransaction().begin();
		List<Parcela> parcelaByClienteEDoc = getParcelaByClienteEDoc(parcela.getCliente().getId(),
				parcela.getDocumento().getNum_doc());
		for (Parcela parcela2 : parcelaByClienteEDoc) {
			// Não há necessidade disso, pois a lista já é managed
			// parcela2 = em.merge(parcela2);
			em.remove(parcela2);
		}
		em.getTransaction().commit();
	}

	public Long getQuantidadeParcelas(Integer num_doc) {
		return em.createQuery("select count(p) from Parcela p JOIN p.documento doc WHERE doc.num_doc = :num_doc",
				Long.class).setParameter("num_doc", num_doc).getSingleResult();
	}

}
