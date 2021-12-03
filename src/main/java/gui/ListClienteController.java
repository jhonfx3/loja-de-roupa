package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.FormCliente;
import application.FormParcela;
import application.ListParcela;
import application.Main;
import br.com.modelo.Cidade;
import br.com.modelo.Cliente;
import br.com.modelo.Endereco;
import br.com.modelo.EnderecoEmpresa;
import br.com.modelo.Parcela;
import dao.CidadeDAO;
import dao.ClienteDAO;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class ListClienteController implements Initializable, DataChangeListener {

	@FXML
	private Button btnPesquisar;

	@FXML
	private TextField txtFiltroNome;

	@FXML
	private TextField txtFiltroCpf;

	private static ListClienteController controller;

	@FXML
	private TableView<Cliente> tableViewCliente;

	private List<DataChangeListener> dataChanceListeners = new ArrayList<>();

	@FXML
	private ClienteDAO clienteDAO;

	public ClienteDAO getClienteDAO() {
		return clienteDAO;
	}

	public void setClienteDAO(ClienteDAO clienteDAO) {
		this.clienteDAO = clienteDAO;
	}

	@FXML
	private TableColumn<Cliente, String> TableColumnEndereco;

	@FXML
	private TableColumn<Cliente, Integer> tableColumnId;

	@FXML
	private TableColumn<Cliente, String> tableColumnNome;

	@FXML
	private TableColumn<Cliente, Integer> tableColumnCpf;

	@FXML
	private TableColumn<Cliente, Cliente> tableColumnEDIT;

	@FXML
	private TableColumn<Cliente, Cliente> tableColumnREMOVE;

	private ObservableList<Cliente> obsList;

	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		controller = this;
		// Se a tela de listagem de parcela está aberta. Ou seja: Diferente de null
		if (ListParcela.getMainScene() != null) {
			// Eu inscrevo essa tela aqui para escutar eventos
			subscribeDataChangerListener(ListParcelaController.getController());
		}
		// Se a tela de form parcela está aberta. Ou seja: Diferente de null
		if (FormParcela.getMainScene() != null) {
			// Eu a inscrevo aqui para escutar eventos
			subscribeDataChangerListener(FormParcelaController.getController());
		}
	}

	public static ListClienteController getController() {
		return controller;
	}

	public static void setController(ListClienteController controller) {
		ListClienteController.controller = controller;
	}

	public void subscribeDataChangerListener(DataChangeListener listener) {
		dataChanceListeners.add(listener);
	}

	@FXML
	public void onBtnPesquisar() {
		System.out.println("Hora de pesquisar");
		initializeNodes();
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChanceListeners) {
			listener.onDataChanged();
		}

	}

	// Inicializa os nós da table view
	private void initializeNodes() {
		// É um padrão para iniciar o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
		// Pego o endereço através do relacionamento de cliente e endereço.
		// Dou um getter: getEndereco
		TableColumnEndereco.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Cliente, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(TableColumn.CellDataFeatures<Cliente, String> param) {
						return new SimpleStringProperty(param.getValue().getEndereco().getEndereco());
					}
				});
		// Vamos fazer a table view acompanhar a janela:
		Stage stage = (Stage) Main.getMainSceneMain().getWindow();
		// Macete para fazer o tbl acomapnhar a altura da janela
		tableViewCliente.prefHeightProperty().bind(stage.heightProperty());
		setClienteDAO(new ClienteDAO());
		updateTableView();
	}

	// Atualizo a tabela de acordo com filtros
	public void updateTableView() {
		// if (departmentService == null) {
		// throw new IllegalStateException("O service estava nulo");
		// }

		// List<Cliente> list = clienteDAO.findAll();
		List<Cliente> list = new ArrayList<>();
		Cliente cli = new Cliente();

		// Se o usuário preencheu o nome e CPF
		if (!txtFiltroNome.getText().isEmpty() && !txtFiltroCpf.getText().isEmpty()) {
			list = new ClienteDAO().getByCPfOuNome(txtFiltroCpf.getText(), txtFiltroNome.getText());
		} // Se ele preencheu somente o CPF
		else if (!txtFiltroCpf.getText().isEmpty()) {
			try {
				cli = new ClienteDAO().getByCPf(txtFiltroCpf.getText());
				if (cli != null) {
					list.add(cli);
				}
			} catch (Exception e) {

			}

		} // Se ele preencheu apenas o nome
		else if (!txtFiltroNome.getText().isEmpty()) {
			list = new ClienteDAO().getByNome(txtFiltroNome.getText());
		} else {
			// Ou se ele quer mostrar todos os clientes(Sem Filtros)
			list = new ClienteDAO().findAll();
		}
		// O código abaixo da linha 193 até a linha 204 é responsável por tornar toda a
		// lista de clientes
		// no estado managed do Hibernate. Utilizando o merge
		for (int i = 0; i < list.size(); i++) {
			Endereco endereco = list.get(i).getEndereco();
			endereco = new ClienteDAO().getEm().merge(endereco);
			list.get(i).setEndereco(endereco);

			if (list.get(i).getEnderecoEmpresa() != null) {
				EnderecoEmpresa enderecoEmp = list.get(i).getEnderecoEmpresa();
				enderecoEmp = new ClienteDAO().getEm().merge(enderecoEmp);
				list.get(i).setEnderecoEmpresa(enderecoEmp);
			}

		}

		obsList = FXCollections.observableArrayList(list);
		tableViewCliente.setItems(obsList);
		// Inicializa as configurações dos botões de edição
		initEditButtons();
		// Mesma coisa que acima mas dessa vez os de excluir
		initRemoveButtons();
	}

	// Método que cria o formulário de edição mas aproveitando o mesmo formulário do
	// FormClienteController
	private void createDialogForm(Cliente obj, String absolutName, Stage parentStage)
			throws IllegalArgumentException, IllegalAccessException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();

			FormClienteController controller = loader.getController();
			Scene cena = new Scene(pane);
			FormCliente.setMainScene(cena);
			controller.setCliente(obj);
			controller.setCidadeDAO(new CidadeDAO());
			controller.setClienteDAO(new ClienteDAO());
			controller.updateFormData();
			// Me inscrever para escutar
			controller.subscribeDataChangerListener(this);
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edição de Cliente");
			dialogStage.setScene(cena);
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					System.out.println("Fechando formulário de cliente");
					FormCliente.setMainScene(null);

				}
			});
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error Loading View", e.getMessage(), AlertType.ERROR);
		}
	}

	// Inicializa os botões de edição
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> {
					try {
						Cliente obj2 = new Cliente();
						// Crio um clone do objeto
						obj2 = obj.clone();
						// Verifico se já existe um formulário de cliente aberto
						// Verificando se scene é diferente de null
						if (FormCliente.getMainScene() != null) {
							Alerts.showError("Erro ao abrir formulário", null,
									"Já existe um formulário de cliente aberto, primeiro feche-o");
						} else {
							// Senão eu posso criar um formulário de edição de cliente
							createDialogForm(obj2, "/gui/FormClienteVBox.fxml", Utils.currentStage(event));
						}
					} catch (IllegalArgumentException | IllegalAccessException | CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
		});
	}

// Inicializa os botões de remoção
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	// Método responsável por excluir um cliente
	private void removeEntity(Cliente obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que deseja remover?");
		// Se o usuário confirmou que ele deseja excluir o registro
		if (result.get() == ButtonType.OK) {
			try {
				Integer idCliente2 = obj.getId();
				if (FormParcela.getMainScene() != null
						&& FormParcelaController.getController().getEntity().getCliente() != null
						&& (idCliente2 == FormParcelaController.getController().getEntity().getCliente().getId())) {
					Alerts.showError("Erro ao excluir", null,
							"Você não pode excluir esse cliente no momento porque existe um formulário de edição de parcela"
									+ "\n"
									+ "para esse cliente em aberto. Primeiro feche o formulário de edição de parcela");
				} else {
					// remover
					boolean isIgual = false;
					boolean isIgualCadparcela = false;
					boolean isExcluindoClienteDaParcelaTabela = false;
					Integer idCliente = obj.getId();

					// Se a tela de listagem de parcela está aberta
					if (ListParcela.getMainScene() != null) {
						// Pego o cliente selecionado na combobox de lá através do controller
						Cliente selectedItem = ListParcelaController.getController().getComboboxCliente()
								.getSelectionModel().getSelectedItem();
						// É o mesmo cliente que eu acabei de excluir
						if (idCliente == selectedItem.getId()) {
							// Logo, eu tenho que limpar a table view de lá
							isIgual = true;
							// ListParcelaController.getController().getTableViewParcela().getItems().clear();
						}
					}

					if (ListParcela.getMainScene() != null) {
						if (!ListParcelaController.getController().getTableViewParcela().getItems().isEmpty()) {
							Cliente cliente = ListParcelaController.getController().getTableViewParcela().getItems()
									.get(0).getCliente();
							isExcluindoClienteDaParcelaTabela = verificaSeEstaExcluindoUmClienteDaTabela(idCliente,
									cliente);
						}
					}

					// Se o formulário de cadastro de parcela tiver aberta
					if (FormParcela.getMainScene() != null) {
						// Pego numa lista as parcelas da table view de lá
						ObservableList<Parcela> items = FormParcelaController.getController().getTableViewParcela()
								.getItems();
						Cliente selectedItem = new Cliente();
						// Verifico se essa lista não está vazia. Para não dar NullPointerException
						if (!items.isEmpty()) {
							// Pego a primeira parcela da tabela
							Parcela parcela = items.get(0);
							// Armazenado o cliente dessa parcela
							selectedItem = parcela.getCliente();
						}
						// É o mesmo cliente que eu acabei de excluir
						if (idCliente == selectedItem.getId()) {
							// Verifico que é o mesmo cliente que eu acabei de excluir
							// Logo eu tenho que limpar a table view de lá
							isIgualCadparcela = true;
						}
					}
					// Removo o cliente
					clienteDAO.remover(obj);
					updateTableView();
					notifyDataChangeListeners();
					if (ListParcela.getMainScene() != null) {
						if (idCliente != ListParcelaController.getController().getUltimoClienteBuscado().getId()) {
							Cliente clienteFiltro = new Cliente();
							System.out.println("Parabéns, você clicou no botão!");
							Integer id = null;
							for (Cliente cliente : ListParcelaController.getController().getComboboxCliente()
									.getItems()) {
								if (cliente.getId() == ListParcelaController.getController().getUltimoClienteBuscado()
										.getId()) {
									id = cliente.getId();
									clienteFiltro = cliente;
								}
							}

							if (id != null) {
								ListParcelaController.getController().getComboboxCliente().getSelectionModel()
										.select(clienteFiltro);
							}

						}
					}

					// Se o cliente na combobox é o mesmo que acabei de excluir
					if (isIgual) {
						// Limpa a tabela
						ListParcelaController.getController().getTableViewParcela().getItems().clear();
						// Carrega o novo cliente selecionado na combo box
						// ListParcelaController.getController().onBtnPesquisarAction();
						Cliente selectedItem = getClienteNaComboBoxListParcela(
								ListParcelaController.getController().getComboboxCliente());
						if (selectedItem != null) {
							ListParcelaController.getController().setUltimoClienteBuscado(selectedItem);
						}
						ListParcelaController.getController().realizaPesquisa(true);
					}
					if (isIgualCadparcela) {
						// Limpa a tabela da tela de Form Cad Parcela(FormParcelaController)
						FormParcelaController.getController().getTableViewParcela().getItems().clear();
					}
					if (isExcluindoClienteDaParcelaTabela) {
						ListParcelaController.getController().getTableViewParcela().getItems().clear();
						Cliente selectedItem = getClienteNaComboBoxListParcela(
								ListParcelaController.getController().getComboboxCliente());
						if (selectedItem != null) {
							ListParcelaController.getController().setUltimoClienteBuscado(selectedItem);
						}
						ListParcelaController.getController().realizaPesquisa(true);
					}
				}

			} catch (Exception e) {
				Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
				e.printStackTrace();
			}
		}
	}

	private Cliente getClienteNaComboBoxListParcela(ComboBox<Cliente> combo) {
		return combo.getSelectionModel().getSelectedItem();
	}

	private boolean verificaSeEstaExcluindoUmClienteDaTabela(Integer idASerExcluido, Cliente clienteDaTabela) {

		return idASerExcluido == clienteDaTabela.getId();
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

}
