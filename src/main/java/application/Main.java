package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {

	private static Scene mainSceneMain;

	// Versão em que os escutadores estão corretos!
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();

			// Scene mainScene = new Scene(scrollPane);

			// Ajustar a janela o scrollpane - largura e altura

			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);

			mainSceneMain = new Scene(scrollPane);
			Thread.setDefaultUncaughtExceptionHandler(new TratadorDeExcecao());
			primaryStage.setScene(mainSceneMain);
			primaryStage.setTitle("Tela Principal");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Scene getMainSceneMain() {
		return mainSceneMain;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
