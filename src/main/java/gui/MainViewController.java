package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.FormCliente;
import application.FormParcela;
import application.ListCliente;
import application.ListParcela;
import application.Main;
import application.TratadorDeExcecao;
import br.com.interfaces.Injeta;
import br.com.modelo.Cliente;
import br.com.modelo.Parcela;
import dao.CidadeDAO;
import dao.ClienteDAO;
import dao.ParcelaDAO;
import gui.util.Alerts;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemCadastrarParcela;

	@FXML
	private MenuItem menuItemCadastrar;

	@FXML
	private MenuItem menuItemListarOuAlterar;

	@FXML
	private MenuItem menuItemListarParcela;

	@FXML
	private Menu menuLogout;

	@FXML
	private MenuBar menuBar;

	private ClienteDAO clienteDAO;

	@FXML
	private void onMenuItemListarOuAlterarAction() {
		System.out.println("Tenho que abrir a listagem de clientes");
		Stage stg = new Stage();
		ListCliente listCliente = ListCliente.getInstance();
		listCliente.start(stg);
	}

	@FXML
	private void onMenuItemListarParcelaAction() {
		System.out.println("Tenho que abrir a listagem de parcelas");
		Stage stg = new Stage();
		// Jeito antigo
		// ListParcela listParcela = new ListParcela();
		ListParcela listParcela = ListParcela.getInstance();
		listParcela.start(stg);
	}

	@FXML
	public void onMenuItemCadastrarParcelaAction() throws IllegalArgumentException, IllegalAccessException {
		// System.out.println("Tenho que abrir o cadastro de parcelas");
		// Stage stg = new Stage();
		// FormParcela listCliente = new FormParcela();
		// listCliente.start(stg);

		Stage stage = (Stage) menuBar.getScene().getWindow();
		Parcela obj = new Parcela();
		if (obj.getId() == null) {
			System.out.println("Obj id é null");
		}
		// FormParcelaController controller = new FormParcelaController();
		Thread.setDefaultUncaughtExceptionHandler(new TratadorDeExcecao());
		if (FormParcela.getMainScene() != null) {
			Alerts.showError("Erro ao abrir formulário", null,
					"Já existe um formulário de Parcela aberto, primeiro feche-o");
		} else {
			createDialogForm(obj, "Cadastro de parcela", "/gui/FormParcela.fxml", stage,
					(FormParcelaController controller) -> {
						controller.setParcela((Parcela) obj);
						controller.setParcelaDAO(new ParcelaDAO());
						controller.initSpinner();
					});
		}

	}

	@FXML
	public void onMenuItemCadastrarAction() throws IllegalArgumentException, IllegalAccessException {
		// System.out.println("Tenho que abrir o formulário de cadastro");
		// Stage stg = new Stage();
		// FormCliente form = new FormCliente();
		// form.start(stg);
		Thread.setDefaultUncaughtExceptionHandler(new TratadorDeExcecao());

		// loadView("/gui/FormCliente.fxml", x -> {
		// });

		Stage stage = (Stage) menuBar.getScene().getWindow();
		// Stage stage = Utils.currentStage(menuBar); Aqui está errado. Se eu passar
		// dessa forma, ao fechar o form de cadastro, fecha tudo.

		Cliente obj = new Cliente();
		// Cliente obj = clienteDAO.getById(38);
		// FormClienteController controller = new FormClienteController();

		if (FormCliente.getMainScene() != null) {
			Alerts.showError("Erro ao abrir formulário", null,
					"Já existe um formulário de cliente aberto, primeiro feche-o");
		} else {
			createDialogForm(obj, "Cadastro de cliente", "/gui/FormClienteVBox.fxml", stage,
					(FormClienteController controller) -> {
						controller.setCliente((Cliente) obj);
						controller.setCidadeDAO(new CidadeDAO());
						controller.setClienteDAO(new ClienteDAO());

					});

		}

	}

	public void setClienteDAO(ClienteDAO clienteDAO) {
		this.clienteDAO = clienteDAO;
	}

	private synchronized <T> void createDialogForm(Object obj, String nomeTela, String absolutName, Stage parentStage,
			Consumer<T> initializingAction) throws IllegalArgumentException, IllegalAccessException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();

			Injeta injeta;
			T controller2 = loader.getController();
			injeta = (Injeta) controller2;

			Scene cena = new Scene(pane);
			injeta.setaDependencias(obj, cena);
			// FormCliente.setMainScene(cena);

			Stage dialogStage = new Stage();
			dialogStage.setTitle(nomeTela);
			dialogStage.setScene(cena);
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					// System.out.println("Fechando formulário de cad cliente ou cad parcela");
					// if (dialogStage.getTitle().equals("Cadastro de cliente")) {
					// System.out.println("Fechando formulário de cliente");
					// FormCliente.setMainScene(null);
					// }
					injeta.setMainScene(null);
				}
			});
			T controller = loader.getController();
			initializingAction.accept(controller);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error Loading View", e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@FXML
	public void onMenuLogoutAction() {
		System.out.println("Tenho que fazer logout");
	}

	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			AnchorPane newAnchorPane = loader.load();
			Scene mainScene = Main.getMainSceneMain();
			// Pega a cena principal e chama a partir dela o getRoot, ele traz
			// o primeiro elemento da view (ScrollPane)
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newAnchorPane.getChildren());
			// COm tudo isso estamos conseguindo manipular a cena principal
			// Incluindo nela além do main menu, os filhos da janela que eu tiver abrindo

			T controller = loader.getController();
			initializingAction.accept(controller);
		} catch (IOException e) {
			// Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(),
			// AlertType.ERROR);
		}
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		setClienteDAO(new ClienteDAO());

	}

}
