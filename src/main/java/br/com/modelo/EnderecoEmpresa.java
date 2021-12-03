package br.com.modelo;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.validator.constraints.NotEmpty;

import br.com.interfaces.Telefone;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class EnderecoEmpresa extends Endereco {

	@NotEmpty(message = "Nome é obrigatório")
	private String nome;
	
	@NotEmpty(message = "Telefone é obrigatório")
	@Telefone
	private String telefone;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	
}
