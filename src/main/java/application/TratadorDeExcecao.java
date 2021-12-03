package application;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class TratadorDeExcecao implements Thread.UncaughtExceptionHandler {
	private static final Logger logger = Logger.getLogger(TratadorDeExcecao.class);

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.println("Aconteceu uma exceção!");
		FileWriter arq = null;
		try {
			arq = new FileWriter("tabuada.txt");
			PrintWriter gravarArq = new PrintWriter(arq);

			e.printStackTrace(gravarArq);
			logger.error("Fatal error", e);
			// gravarArq.printf("testando salvar arquivo");
			arq.close();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}

}
