package br.com.modelo;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.annotations.Cascade;

@Entity
public class Parcela implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull(message = "O campo valor é obrigatório")
	private BigDecimal valorDaCompra;

	private BigDecimal valorDaParcela;

	@Temporal(TemporalType.DATE)
	@Past(message = "Esse campo de data deve estar no passado ou presente")
	@NotNull(message = "Esse campo de data é obrigatório")
	private Calendar dataCompra;

	@Temporal(TemporalType.DATE)
	private Calendar vencimento;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Documento documento;

	private boolean status = false;

	@ManyToOne
	// Não posso remover o cliente quando se exclui uma parcela
	// @Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
	private Cliente cliente;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getValorDaCompra() {
		return valorDaCompra;
	}

	public void setValorDaCompra(BigDecimal valorDaCompra) {
		this.valorDaCompra = valorDaCompra;
	}

	public BigDecimal getValorDaParcela() {
		return valorDaParcela;
	}

	public void setValorDaParcela(BigDecimal valorDaParcela) {
		this.valorDaParcela = valorDaParcela;
	}

	public Integer getId() {
		return id;
	}

	public void setIdc(Integer id) {
		this.id = id;
	}

	public Calendar getDataCompra() {
		return dataCompra;
	}

	public void setDataCompra(Calendar dataCompra) {
		this.dataCompra = dataCompra;
	}

	public Calendar getVencimento() {
		return vencimento;
	}

	public void setVencimento(Calendar vencimento) {
		this.vencimento = vencimento;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	@Override
	public Parcela clone() throws CloneNotSupportedException {
		return (Parcela) super.clone();
	}

}
