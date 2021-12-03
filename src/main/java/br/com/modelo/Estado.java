package br.com.modelo;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Estado {

	@Id
	private Integer codigo_uf;
	
	private String uf;
	
	private String nome;

	public Integer getCodigo_uf() {
		return codigo_uf;
	}

	public void setCodigo_uf(Integer codigo_uf) {
		this.codigo_uf = codigo_uf;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
	
	
	
}
