package br.com.modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Documento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer num_doc;

	public Integer getNum_doc() {
		return num_doc;
	}

	public void setNum_doc(Integer num_doc) {
		this.num_doc = num_doc;
	}
	
	
}
