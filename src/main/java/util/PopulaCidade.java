package util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import br.com.modelo.Cidade;
import br.com.modelo.Estado;
import dao.EstadoDAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

public class PopulaCidade {

	public static void main(String[] args) throws IOException {
		EntityManager em = new JPAUtil().getEntityManager();
		Reader reader = Files.newBufferedReader(Paths.get("C:\\Users\\João\\eclipse-workspace\\cadastro\\src\\main\\java\\util\\municipios.csv"));
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        EstadoDAO dao = new EstadoDAO();
        String[] nextLine;
        ArrayList<String> lista = new ArrayList<>();
        while ((nextLine = csvReader.readNext()) != null) {
            lista.add(nextLine[1]+"+"+nextLine[5]);            
        }   
		em.getTransaction().begin();
        for (String string : lista) {
			System.out.println(string.substring(0,string.indexOf("+")));
			String uf = string.substring(string.indexOf("+")+1);
			Integer i = Integer.valueOf(uf);
			string = string.substring(0,string.indexOf("+"));
			Cidade cidade = new Cidade();
			cidade.setNome(string);
			Estado estado = dao.getEstado(i);
			cidade.setEstado(estado);
			em.persist(cidade);
		}
        em.getTransaction().commit();
        em.close();

	}
}
