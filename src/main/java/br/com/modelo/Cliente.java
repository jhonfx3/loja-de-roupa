package br.com.modelo;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;

import br.com.enums.TipoResidencia;
import br.com.interfaces.Data;
import br.com.interfaces.PassadoOuPresente;
import br.com.interfaces.Telefone;
import br.com.interfaces.Unique;
import br.com.interfaces.ValidaCpf;
import br.com.interfaces.ValidaEmail;
import dao.ClienteDAO;

@Entity
public class Cliente implements Cloneable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotEmpty(message = "Nome é obrigatório")
	// @Pattern(regexp="[a-zA-Z]+",message="Não pode digitar números nesse campo")
	private String nome;

	@JoinColumn(unique = true)
	@OneToOne(cascade = CascadeType.PERSIST)
	@Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
	private Endereco endereco;

	@JoinColumn(unique = true)
	@OneToOne(cascade = CascadeType.PERSIST)
	@Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
	private EnderecoEmpresa enderecoEmpresa;

	@OneToMany(mappedBy = "cliente")
	@Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
	private List<Parcela> parcela;

	public EnderecoEmpresa getEnderecoEmpresa() {
		return enderecoEmpresa;
	}

	public void setEnderecoEmpresa(EnderecoEmpresa enderecoEmpresa) {
		this.enderecoEmpresa = enderecoEmpresa;
	}

	@ValidaCpf
	@Unique(service = ClienteDAO.class, fieldName = "cpf", message = "CPF já registrado! Digite outro")
	@Column(unique = true)
	private String cpf;

	@NotEmpty(message = "Telefone é obrigatório")
	@Telefone
	private String telefone;

	@NotEmpty(message = "Celular é obrigatório")
	@Telefone
	private String celular;

	//@Temporal(TemporalType.DATE)
	@NotEmpty(message = "A data de aniversário é obrigatório")
	//@Past(message = "A data de aniversário deve estar no passado")
	@Data
	@PassadoOuPresente
	private String aniversario;

	@Temporal(TemporalType.DATE)
	// @NotNull(message = "Esse campo de data é obrigatório")
	@Past(message = "Esse campo de data deve estar no passado ou presente")
	private Calendar clienteDesde;

	@NotEmpty(message = "RG é obrigatório")
	private String rg;

	// @Email(message = "E-mail inválido")
	@ValidaEmail
	// A partir de agora não é mais obrigatório
	//@NotEmpty(message = "E-mail é obrigatório")
	@Unique(service = ClienteDAO.class, fieldName = "email", message = "E-mail já registrado! Digite outro")
	@Column(unique = true)
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private BigDecimal rendaMensal;

	private String obs;

	private String profissao;

	private String assinatura;

	@Enumerated(EnumType.STRING)
	private TipoResidencia residencia;

	public String getRg() {
		return rg;
	}

	public Calendar getClienteDesde() {
		return clienteDesde;
	}

	public void setClienteDesde(Calendar clienteDesde) {
		this.clienteDesde = clienteDesde;
	}

	public String getProfissao() {
		return profissao;
	}

	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}

	public String getAssinatura() {
		return assinatura;
	}

	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}

	public TipoResidencia getResidencia() {
		return residencia;
	}

	public void setResidencia(TipoResidencia residencia) {
		this.residencia = residencia;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public BigDecimal getRendaMensal() {
		return rendaMensal;
	}

	public void setRendaMensal(BigDecimal rendaMensal) {
		this.rendaMensal = rendaMensal;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getAniversario() {
		return aniversario;
	}

	public void setAniversario(String aniversario) {
		this.aniversario = aniversario;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	@Override
	public Cliente clone() throws CloneNotSupportedException {
		return (Cliente) super.clone();
	}

}
