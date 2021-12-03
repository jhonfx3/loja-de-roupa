package gui;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.FormCliente;
import application.FormParcela;
import application.ListCliente;
import application.ListParcela;
import application.Main;
import br.com.modelo.Cliente;
import br.com.modelo.Parcela;
import dao.CidadeDAO;
import dao.ClienteDAO;
import dao.ParcelaDAO;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class ListParcelaController implements Initializable, DataChangeListener {
	// Uma instância dela mesmo
	private static ListParcelaController controller;

	private boolean pesquisarPorDocumento;
	private boolean pesquisarParcelasVencidas;
	private Integer ultimoNumeroDoc;
	private Cliente ultimoClienteBuscado;
	@FXML
	private Label labelClienteSelecionado;

	@FXML
	private Label labelFiltroSelecionado;

	public Cliente getUltimoClienteBuscado() {
		return ultimoClienteBuscado;
	}

	public void setUltimoClienteBuscado(Cliente ultimoClienteBuscado) {
		this.ultimoClienteBuscado = ultimoClienteBuscado;
	}

	@FXML
	private RadioButton radioVencidas;

	@FXML
	private Button btnPesquisar;

	private List<DataChangeListener> dataChanceListeners = new ArrayList<>();

	@FXML
	private TableView<Parcela> tableViewParcela;

	@FXML
	private TableColumn<Parcela, Parcela> tableColumnEDIT;

	@FXML
	private TableColumn<Parcela, Parcela> tableColumnEditParcela;

	@FXML
	private TableColumn<Parcela, Parcela> tableColumnRemoveParcela;

	@FXML
	private TableColumn<Parcela, Integer> tableColumnId;

	@FXML
	private TableColumn<Parcela, Number> tableColumnNumDoc;

	@FXML
	private TableColumn<Parcela, Calendar> tableColumndataCompra;

	@FXML
	private TableColumn<Parcela, Calendar> tableColumnVencimento;

	@FXML
	private TableColumn<Parcela, Boolean> tableColumnStatus;

	@FXML
	private TableColumn<Parcela, BigDecimal> tableColumnValorDaCompra;

	@FXML
	private TableColumn<Parcela, BigDecimal> tableColumnValorDaParcela;

	@FXML
	private TableColumn<Parcela, String> tableColumnCpf;

	@FXML
	private TableColumn<Parcela, String> tableColumnNome;

	@FXML
	private TextField txtNomeOuCpf;

	@FXML
	private TextField txtPesquisarDocumento;

	private ClienteDAO clienteDAO;

	private ParcelaDAO parcelaDAO;

	public ClienteDAO getClienteDAO() {
		return clienteDAO;
	}

	public void setClienteDAO(ClienteDAO clienteDAO) {
		this.clienteDAO = clienteDAO;
	}

	public ParcelaDAO getParcelaDAO() {
		return parcelaDAO;
	}

	public Label getLabelClienteSelecionado() {
		return labelClienteSelecionado;
	}

	public void setLabelClienteSelecionado(Label labelClienteSelecionado) {
		this.labelClienteSelecionado = labelClienteSelecionado;
	}

	public static ListParcelaController getController() {
		return controller;
	}

	public static void setController(ListParcelaController controller) {
		ListParcelaController.controller = controller;
	}

	public void setParcelaDAO(ParcelaDAO parcelaDAO) {
		this.parcelaDAO = parcelaDAO;
	}

	public void subscribeDataChangerListener(DataChangeListener listener) {
		dataChanceListeners.add(listener);
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChanceListeners) {
			listener.onDataChanged();
		}

	}

	public ComboBox<Cliente> getComboboxCliente() {
		return comboboxCliente;
	}

	public void setComboboxCliente(ComboBox<Cliente> comboboxCliente) {
		this.comboboxCliente = comboboxCliente;
	}

	@FXML
	public void onComboBoxCLienteAction() {
		// Cliente cliente = comboboxCliente.getSelectionModel().getSelectedItem();
		// System.out.println("Cliente selecionado: " + cliente.getNome());
	}

	// Combo Box
	@FXML
	private ComboBox<Cliente> comboboxCliente;

	private ObservableList<Cliente> obsList;

	private ObservableList<Parcela> obsListParcela;

	// Método responsável por inicializar as colunas da tabela
	private void initializeNodes(List<Parcela> parcelas) {
		// É um padrão para iniciar o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumndataCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompra"));
		tableColumnVencimento.setCellValueFactory(new PropertyValueFactory<>("vencimento"));
		tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		tableColumnValorDaCompra.setCellValueFactory(new PropertyValueFactory<>("valorDaCompra"));
		tableColumnValorDaParcela.setCellValueFactory(new PropertyValueFactory<>("valorDaParcela"));
		transformaTableColumnData(tableColumndataCompra);
		transformaTableColumnData(tableColumnVencimento);

		// Perocorro a coluna de status e seto PAGO ou PENDENTE de acordo com a variábel
		// boolean
		tableColumnStatus.setCellFactory(col -> new TableCell<Parcela, Boolean>() {
			@Override
			public void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setText(null);
				} else {
					if (item == true) {
						setText("PAGO");
					} else {
						setText("PENDENTE");
					}
				}
			}
		});
		// Percorro a coluna e pego o número do documento através de um getter da
		// Parcela
		tableColumnNumDoc
				.setCellValueFactory(new Callback<CellDataFeatures<Parcela, Number>, ObservableValue<Number>>() {
					@Override
					public ObservableValue<Number> call(CellDataFeatures<Parcela, Number> c) {
						return new SimpleIntegerProperty(c.getValue().getDocumento().getNum_doc());
					}
				});
		// Faço a mesma coisa acima mas dessa vez com o CPF. Através de um getter da
		// Parcela
		tableColumnCpf.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Parcela, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(TableColumn.CellDataFeatures<Parcela, String> param) {
						return new SimpleStringProperty(param.getValue().getCliente().getCpf());
					}
				});
		// Mesma coisa das anteriores, mas dessa vez com o nome do cliente
		tableColumnNome.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Parcela, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(TableColumn.CellDataFeatures<Parcela, String> param) {
						return new SimpleStringProperty(param.getValue().getCliente().getNome());
					}
				});

		// Vamos fazer a table view acompanhar a janela:
		Stage stage = (Stage) Main.getMainSceneMain().getWindow();
		// Macete para fazer o tbl acomapnhar a altura da janela
		tableViewParcela.prefHeightProperty().bind(stage.heightProperty());
		updateTableView(parcelas);
	}

	private void createDialogForm(Parcela obj, String absolutName, Stage parentStage)
			throws IllegalArgumentException, IllegalAccessException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();

			FormParcelaController controller = loader.getController();
			Scene cena = new Scene(pane);
			FormParcela.setMainScene(cena);
			controller.setParcela(obj);
			setParcelaDAO(new ParcelaDAO());
			controller.updateFormData();
			controller.initSpinner();
			// Me inscrever para escutar
			controller.subscribeDataChangerListener(this);
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edição de Parcela");
			dialogStage.setScene(cena);
			dialogStage.setResizable(true);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					System.out.println("Fechando formulário de parcela");
					FormParcela.setMainScene(null);

				}
			});
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error Loading View", e.getMessage(), AlertType.ERROR);
		}
	}

	// Método responsável por atualizar a tabela
	public void updateTableView(List<Parcela> parcelas) {
		// if (departmentService == null) {
		// throw new IllegalStateException("O service estava nulo");
		// }
		// List<Parcela> list = parcelaDAO.getParcelaByClienteId(1);
		// parcelas = parcelaDAO.findAll();
		obsListParcela = FXCollections.observableArrayList(parcelas);
		tableViewParcela.setItems(obsListParcela);
		// Inicializa os botões de pagar parcela
		initEditButtons();
		// Inicializa os botões de editar parcela
		initEditButtonsAlterarParcela();
		// Mesma coisa que acima mas dessa vez os de excluir
		initRemoveButtons();
	}

	// Converte as datas(Calendar) que estão no padrão americano para o brasileiro
	private void transformaTableColumnData(TableColumn<Parcela, Calendar> tableColumn) {
		tableColumn.setCellFactory(col -> new TableCell<Parcela, Calendar>() {
			@Override
			public void updateItem(Calendar item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setText(null);
				} else {
					String pattern = "dd/MM/yyyy";
					DateFormat df = new SimpleDateFormat(pattern);
					setText(df.format(item.getTime()));
				}
			}
		});
	}

	// Método de evento que seta o cliente selecionado
	// de acordo com um text field onde o usuário preenche o CPF ou nome
	@FXML
	public void onTxtFiltroAction() {
		Cliente clienteFiltro = new Cliente();
		// System.out.println("Parabéns, você clicou no botão!");
		Integer id = null;
		for (Cliente cliente : comboboxCliente.getItems()) {
			if (cliente.getCpf().equalsIgnoreCase(txtNomeOuCpf.getText())
					|| cliente.getNome().equalsIgnoreCase(txtNomeOuCpf.getText())) {
				id = cliente.getId();
				clienteFiltro = cliente;
			}
		}

		if (id != null) {
			comboboxCliente.getSelectionModel().select(clienteFiltro);
		}
	}

	// Método responsável por atualizasr a combobox
	private void updadeComboBox(ComboBox<Cliente> ComboBox) {
		// Por algum motivo ele não pega os dados atualizados caso utilize ClienteDAo da
		// dependência
		// No momento em que o form de list de parcela já está aberto e eu acabo de
		// alterar um cliente
		// Esse cliente, caso eu utilize a dependência ClienteDAO, ele não pega os dados
		// atualizados por algum motivo
		List<Cliente> list = new ClienteDAO().findAll();
		obsList = FXCollections.observableArrayList(list);
		ComboBox.setItems(obsList);
		for (Cliente cliente : list) {
			System.out.println("Nome do cliente é: " + cliente.getNome());
		}
		// Seto o índice da combobox para 0
		ComboBox.getSelectionModel().select(0);

		// Escolho quais dados e como vou exibir os clientes da combobox
		Callback<ListView<Cliente>, ListCell<Cliente>> factory = lv -> new ListCell<Cliente>() {
			@Override
			protected void updateItem(Cliente item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome() + " - " + item.getCpf());
			}
		};
		ComboBox.setCellFactory(factory);
		ComboBox.setButtonCell(factory.call(null));
		// Associo o text field a um método de evento
		// que nesse caso é aquele onde o usuário preenche o nome ou CPF
		txtNomeOuCpf.textProperty().addListener((obs, oldValue, newValue) -> {
			onTxtFiltroAction();
		});
		// Se a lista tiver vazia então eu não posso deixar o usuário pesquisar
		if (list.isEmpty()) {
			// Desativo o botão de pesquisa
			btnPesquisar.setDisable(true);
		} else { // Já que não está vazia, eu posso deixar o usuário pesquisar
			btnPesquisar.setDisable(false);

		}
	}

	public TableView<Parcela> getTableViewParcela() {
		return tableViewParcela;
	}

	public void setTableViewParcela(TableView<Parcela> tableViewParcela) {
		this.tableViewParcela = tableViewParcela;
	}

	public void realizaPesquisa(boolean pegarUltimoClienteBuscado) {
		if (getUltimoClienteBuscado() != null) {
			System.out.println("ultimo cliente buscado: " + ultimoClienteBuscado.getNome());
		}
		if (btnPesquisar.isDisable()) {
			labelClienteSelecionado.setText("");
			// labelFiltroSelecionado.setText("");
			setUltimoClienteBuscado(null);
		}
		// Se o botão tiver habilitado, eu tenho que pesquisar
		if (!btnPesquisar.isDisable()) {
			System.out.println("Tenho que pesquisar");
			// As duas variáveis boolean abaixo são responsáveis por determinar como farei a
			// pesquisa
			// boolean pesquisarPorDocumento = false;
			// boolean pesquisarParcelasVencidas = false;

			// Se o usuário preenheu algum documento, então ele quer procurar por documento
			if (pegarUltimoClienteBuscado == false && !txtPesquisarDocumento.getText().isEmpty()) {
				pesquisarPorDocumento = true;
				ultimoNumeroDoc = Integer.valueOf(txtPesquisarDocumento.getText());
			} else if (pegarUltimoClienteBuscado == false && txtPesquisarDocumento.getText().isEmpty()) {
				pesquisarPorDocumento = false;
			}

			// Se ele marcou a opção de parcelas vencidas, ele quer que venham as parcelas
			// vencidas
			if (pegarUltimoClienteBuscado == false && radioVencidas.isSelected()) {
				System.out.println("VENCIDAS");
				pesquisarParcelasVencidas = true;
			} else if (pegarUltimoClienteBuscado == false && !radioVencidas.isSelected()) {
				pesquisarParcelasVencidas = false;
			}
			// Pego o cliente selecionado na combobox
			Cliente cli = comboboxCliente.getSelectionModel().getSelectedItem();
			labelClienteSelecionado.setText("Cliente selecionado: " + cli.getNome() + " CPF: " + cli.getCpf());
			if (!pegarUltimoClienteBuscado) {
				setUltimoClienteBuscado(cli);
			}
			if (pegarUltimoClienteBuscado) {
				cli = getUltimoClienteBuscado();
				labelClienteSelecionado.setText("Cliente selecionado: " + cli.getNome() + " CPF: " + cli.getCpf());
			}

			List<Parcela> parcelas = new ArrayList<>();

			// Se ele deseja pesquisar por documento e por parcelas vencidas
			if (pesquisarPorDocumento && pesquisarParcelasVencidas) {
				// parcelas = parcelaDAO.getParcelaByClienteEDoc(cli.getId(),
				// Integer.valueOf(txtPesquisarDocumento.getText()));

				// Faço a pesquisa no DAO
				parcelas = new ParcelaDAO().getParcelaByClienteEDocVencidas(cli.getId(),
						Integer.valueOf(ultimoNumeroDoc));
				labelFiltroSelecionado.setText("Documento selecionado: " + ultimoNumeroDoc + " Parcelas vencidas? Sim");
			} else if (pesquisarPorDocumento) { // Se ele deseja somente pesquisar por documento

				// parcelas = parcelaDAO.getParcelaByClienteId(cli.getId());
				parcelas = new ParcelaDAO().getParcelaByClienteEDoc(cli.getId(), ultimoNumeroDoc);
				labelFiltroSelecionado.setText("Documento selecionado: " + ultimoNumeroDoc + " Parcelas vencidas? Não");
			} else if (pesquisarParcelasVencidas) {// Se ele deseja pesquisar somente parcelas vencidas
				parcelas = new ParcelaDAO().getParcelaByClienteIdVencidas(cli.getId());
				labelFiltroSelecionado.setText("Documento selecionado: Todos Parcelas vencidas? Sim");
			} else {
				// Ou por fim, se ele somente quer todas as parcelas do cliente selecionado na
				// combobox
				parcelas = new ParcelaDAO().getParcelaByClienteId(cli.getId());
				labelFiltroSelecionado.setText("");
			}
			// Inicializo tudo com as parcelas
			initializeNodes(parcelas);
		}
	}

	// Método do evento de pesquisar parcelas
	@FXML
	public void onBtnPesquisarAction() {
		realizaPesquisa(false);
	}

	// Método que incicializa os botões de pagar parcela
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Parcela, Parcela>() {
			private final Button button = new Button("Pagar");

			@Override
			protected void updateItem(Parcela obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				// Evento de pagar parcela
				button.setOnAction(event -> {
					try {
						System.out.println("Você vai pagar uma parcela!");
						// Pego todas as parcelas do número do documento da parcela selecionada
						List<Parcela> parcelas = parcelaDAO.getParcelaByClienteEDoc(obj.getCliente().getId(),
								obj.getDocumento().getNum_doc());
						// Pago ou não a parcela
						// tudo acontece de acordo com condições no DAO
						parcelaDAO.pagarParcela(parcelas, obj);
						// Atualizo a pesquisa
						// onBtnPesquisarAction(); (modo antigo)
						realizaPesquisa(true);
					} catch (Exception e) {

						e.printStackTrace();
					}
				});
			}
		});
	}

	// Método que incicializa os botões de editar parcela
	private void initEditButtonsAlterarParcela() {
		tableColumnEditParcela.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEditParcela.setCellFactory(param -> new TableCell<Parcela, Parcela>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Parcela obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				// Evento de pagar parcela
				button.setOnAction(event -> {
					// System.out.println("Tenho que alterar essa parcela");
					try {
						Parcela obj2 = new Parcela();
						obj2 = obj.clone();
						if (FormParcela.getMainScene() != null) {
							Alerts.showError("Erro ao abrir formulário", null,
									"Já existe um formulário de Parcela aberto, primeiro feche-o");
						} else {
							createDialogForm(obj2, "/gui/FormParcela.fxml", Utils.currentStage(event));
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
		tableColumnRemoveParcela.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemoveParcela.setCellFactory(param -> new TableCell<Parcela, Parcela>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Parcela obj, boolean empty) {
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
	private void removeEntity(Parcela obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que deseja remover?");
		// Se o usuário confirmou que ele deseja excluir o registro
		if (result.get() == ButtonType.OK) {
			// Removo as parcelas daquela compra
			// Tem que ser tipo primitivo
			int id = obj.getDocumento().getNum_doc();
			// System.out.println("NUM DOC " + id);
			parcelaDAO.remover(obj);
			// onBtnPesquisarAction();
			realizaPesquisa(true);
			if (FormParcela.getMainScene() != null) {
				ObservableList<Parcela> items = FormParcelaController.getController().getTableViewParcela().getItems();
				if (!items.isEmpty()) {
					Parcela parcela = items.get(0);
					if (parcela.getDocumento().getNum_doc() == id) {
						FormParcelaController.getController().getTableViewParcela().getItems().clear();
					}
				}
			}
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Constraints.setTextFielNumeros(txtPesquisarDocumento);
		Integer limite = 2000000000;
		Constraints.setTextFieldMaxValorCustom(txtPesquisarDocumento, limite);
		pesquisarParcelasVencidas = false;
		pesquisarPorDocumento = false;
		ultimoNumeroDoc = null;
		setClienteDAO(new ClienteDAO());
		setParcelaDAO(new ParcelaDAO());
		// Inicio a combobox
		updadeComboBox(comboboxCliente);
		// List<Parcela> parcelas = new ArrayList<>();
		// Cliente cli = comboboxCliente.getSelectionModel().getSelectedItem();
		// parcelas = parcelaDAO.findAll();
		// initializeNodes(parcelas);

		// Seto o controller(ListParcelaController)
		setController(this);
		// Realizo a pesquisa de acordo com os text fields e radio button
		onBtnPesquisarAction();

		// Se a tela de Listagem de cliente tiver aberta. Ou seja: Diferente de null
		if (ListCliente.getMainScene() != null) {
			// Eu inscrevo a tela atual(essa tela) para escutar eventos da tela de List
			// Cliente (Para atualizar a combobox)
			ListClienteController controller = ListClienteController.getController();
			controller.subscribeDataChangerListener(this);
		}
		// Verifico se a main scene ainda é null
		if (ListParcela.getMainScene() == null) {
			System.out.println("Progrmação defensiva ativada. ListParcela é null");
			// Seto a main scene de acordo com um os controles
			ListParcela.setMainScene(txtNomeOuCpf.getScene());
		}
	}

	// Método do escutador que atualiza a combobox quando um evento acontece em
	// outra tela
	@Override
	public void onDataChanged() {
		System.out.println("Combo box atualizada");
		updadeComboBox(comboboxCliente);
		// if (FormParcela.getMainScene() != null) {
		// onBtnPesquisarAction();
		// }
	}

}
