package util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import br.com.modelo.Estado;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.EntityManager;

public class PopulaEstado {

	public static void main(String[] args) throws IOException {
		
		EntityManager em = new JPAUtil().getEntityManager();
		em.getTransaction().begin();
		
		Reader reader = Files.newBufferedReader(
				Paths.get("C:\\Users\\João\\eclipse-workspace\\cadastro\\src\\main\\java\\util\\estados.csv"));
		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();

		List<String[]> pessoas = csvReader.readAll();
		for (String[] pessoa : pessoas) {
			//System.out.println("Codigo_uf : " + pessoa[0] + " - uf : " + pessoa[1] + " - Nome : " + pessoa[2]);
			Integer id = Integer.valueOf(pessoa[0]);
			String nome, uf;
			uf = pessoa[1];
			nome = pessoa[2];
			System.out.println("Código_uf: "+id+"  UF: "+uf+"  Nome: "+nome);
			Estado estado = new Estado();
			estado.setCodigo_uf(id);
			estado.setUf(uf);
			estado.setNome(nome);
			em.persist(estado);
		}
		em.getTransaction().commit();
		em.close();
	}

}
