package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jdk.javadoc.internal.tool.Start;

public class ListCliente extends Application {

	private static Scene mainScene;

	private static ListCliente instancia = null;

	private ListCliente() {

	}

	public static ListCliente getInstance() {
		if (instancia == null) {
			System.out.println("Instância sendo criada.");
			instancia = new ListCliente();
		}
		return instancia;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			if (mainScene == null) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListCliente.fxml"));
				VBox scrollPane = loader.load();

				// Scene mainScene = new Scene(scrollPane);

				// Ajustar a janela o scrollpane - largura e altura
				Thread.setDefaultUncaughtExceptionHandler(new TratadorDeExcecao());
				// scrollPane.setFitToHeight(true);
				// scrollPane.setFitToWidth(true);

				mainScene = new Scene(scrollPane);

				primaryStage.setScene(mainScene);
				primaryStage.setTitle("Lista de clientes");
				primaryStage.show();
				primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			          public void handle(WindowEvent we) {
			              mainScene = null;
			        	  instancia = null;
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
		ListCliente.mainScene = mainScene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
