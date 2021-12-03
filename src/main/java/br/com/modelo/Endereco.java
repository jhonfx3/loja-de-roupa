package br.com.modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import br.com.interfaces.Cep;
import br.com.interfaces.EstaVazio;
import br.com.interfaces.Telefone;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Endereco {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotEmpty(message = "Endereço é obrigatório")
	@Size(min=3,message="O endereço deve ter no mínimo 3 caracteres")  
	private String endereco;
	@NotEmpty(message = "Bairro é obrigatório")
	private String bairro;
	
	@NotNull(message = "Número é obrigatório")
	private Integer numero;
	
	
	
	@EstaVazio(message = "CEP é obrigatório")
	@Cep
	private String cep;
	
	@ManyToOne
	private Cidade cidade;
	
	
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	
	
	
	
	
	
	
}
