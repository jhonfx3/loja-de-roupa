package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ListParcela extends Application {

	private static Scene mainScene;

	private static ListParcela instancia = null;

	public static ListParcela getInstance() {
		if (instancia == null) {
			System.out.println("Instância sendo criada.");
			instancia = new ListParcela();
		}
		return instancia;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			if (mainScene == null) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListParcela.fxml"));
				AnchorPane scrollPane = loader.load();

				// Scene mainScene = new Scene(scrollPane);

				// Ajustar a janela o scrollpane - largura e altura
				Thread.setDefaultUncaughtExceptionHandler(new TratadorDeExcecao());
				// scrollPane.setFitToHeight(true);
				// scrollPane.setFitToWidth(true);

				mainScene = new Scene(scrollPane);

				primaryStage.setScene(mainScene);
				primaryStage.setTitle("Lista de Parcelas");
				primaryStage.show();
				primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			          public void handle(WindowEvent we) {
			              mainScene = null;
			        	  instancia = null;
			        	  System.out.println("Fechando o formulário de listagem de parcela");
			          }
			      });
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Scene getMainScene() {
		return mainScene;
	}

	public static void setMainScene(Scene mainScene) {
		ListParcela.mainScene = mainScene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
