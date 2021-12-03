package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class FormCliente extends Application {

	private static Scene mainScene;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/FormClienteVBox.fxml"));
			AnchorPane scrollPane = loader.load();

			// Scene mainScene = new Scene(scrollPane);

			// Ajustar a janela o scrollpane - largura e altura

		//	scrollPane.setFitToHeight(true);
		//	scrollPane.setFitToWidth(true);

			mainScene = new Scene(scrollPane);

			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Cadastro de cliente");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Scene getMainScene() {
		return mainScene;
	}
	
	
	public static void setMainScene(Scene mainScene) {
		FormCliente.mainScene = mainScene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
