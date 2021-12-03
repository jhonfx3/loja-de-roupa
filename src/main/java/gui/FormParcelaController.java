package gui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import application.FormCliente;
import application.FormParcela;
import application.ListCliente;
import application.ListParcela;
import application.Main;
import br.com.interfaces.Injeta;
import br.com.modelo.Cidade;
import br.com.modelo.Cliente;
import br.com.modelo.Parcela;
import dao.ClienteDAO;
import dao.ParcelaDAO;
import gui.listeners.DataChangeListener;
import gui.util.Constraints;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FormParcelaController implements Initializable, Injeta, DataChangeListener {
	private static FormParcelaController controller;

	private List<DataChangeListener> dataChanceListeners = new ArrayList<>();

	@FXML
	private Label labelAddParcela;

	@FXML
	private Label labelInformaQtdParcelas;

	@FXML
	private RadioButton simAddParcela;

	@FXML
	private RadioButton naoAddParcela;

	@FXML
	private TableView<Parcela> tableViewParcela;

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

	private ClienteDAO clienteDAO;

	// Combo Box
	@FXML
	private ComboBox<Cliente> comboboxCliente;

	private ObservableList<Cliente> obsList;

	private ObservableList<Parcela> obsListParcela;

	private ParcelaDAO parcelaDAO;

	// Labels de erro

	@FXML
	private Label dataCompraParcela;

	@FXML
	private Label valorDaCompraParcela;

	private Parcela entity;

	public ComboBox<Cliente> getComboboxCliente() {
		return comboboxCliente;
	}

	public Parcela getEntity() {
		return entity;
	}

	public Button getBtnCadastrarParcela() {
		return btnCadastrarParcela;
	}

	public void setBtnCadastrarParcela(Button btnCadastrarParcela) {
		this.btnCadastrarParcela = btnCadastrarParcela;
	}

	public void setEntity(Parcela entity) {
		this.entity = entity;
	}

	public void setComboboxCliente(ComboBox<Cliente> comboboxCliente) {
		this.comboboxCliente = comboboxCliente;
	}

	public TableView<Parcela> getTableViewParcela() {
		return tableViewParcela;
	}

	public void setTableViewParcela(TableView<Parcela> tableViewParcela) {
		this.tableViewParcela = tableViewParcela;
	}

	@FXML
	private Button btnCadastrarParcela;

	@FXML
	private Spinner<Integer> spinnerParcelas;

	@FXML
	private TextField txtNomeOuCpf;

	@FXML
	private TextField txtValorDaCompra;

	@FXML
	private TextField txtValorDaParcela;

	@FXML
	private DatePicker dataCompra;

	// Inicializa os nodes da tabela
	private void initializeNodes(List<Parcela> parcelas) {
		// É um padrão para iniciar o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumndataCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompra"));
		tableColumnVencimento.setCellValueFactory(new PropertyValueFactory<>("vencimento"));
		tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		tableColumnValorDaCompra.setCellValueFactory(new PropertyValueFactory<>("valorDaCompra"));
		tableColumnValorDaParcela.setCellValueFactory(new PropertyValueFactory<>("valorDaParcela"));
		// Converte a coluna de data para o formato brasileiro. dd/mm/YYYY
		transformaTableColumnData(tableColumndataCompra);
		transformaTableColumnData(tableColumnVencimento);

		// Percorre a coluna de status para preencher com PAGO ou PENDENTE dependendo da
		// variável boolean
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
		// Preeenche a coluna do número de documento através de um getter da classe
		// Parcela
		tableColumnNumDoc
				.setCellValueFactory(new Callback<CellDataFeatures<Parcela, Number>, ObservableValue<Number>>() {
					@Override
					public ObservableValue<Number> call(CellDataFeatures<Parcela, Number> c) {
						return new SimpleIntegerProperty(c.getValue().getDocumento().getNum_doc());
					}
				});

		// Vamos fazer a table view acompanhar a janela:
		Stage stage = (Stage) Main.getMainSceneMain().getWindow();
		// Macete para fazer o tbl acomapnhar a altura da janela
		tableViewParcela.prefHeightProperty().bind(stage.heightProperty());
		// Faz um update da table view. Ou seja: Preenche a table view através da lista
		// de parcelas
		updateTableView(parcelas);
	}

	// Responsável por inscrever quem vai escutar os eventos desse
	// controller(Classe)
	public void subscribeDataChangerListener(DataChangeListener listener) {
		dataChanceListeners.add(listener);
	}

	// Notifica quem está escutando
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChanceListeners) {
			listener.onDataChanged();
		}

	}

	public void updateFormData() {
		if (entity.getId() == null) {

		} else {
			String valor = String.valueOf(entity.getValorDaCompra());
			valor = valor.replace(".", ",");
			txtValorDaCompra.setText(valor);
			DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String dataStr = sdf.format(entity.getDataCompra().getTime()).toString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate local = LocalDate.parse(dataStr, formatter);
			dataCompra.setValue(local);
			dataCompra.setDisable(true);

		}
	}

	// Preenche a table view através da lista de parcelas
	public void updateTableView(List<Parcela> parcelas) {
		// if (departmentService == null) {
		// throw new IllegalStateException("O service estava nulo");
		// }
		// List<Parcela> list = parcelaDAO.getParcelaByClienteId(1);

		obsListParcela = FXCollections.observableArrayList(parcelas);
		tableViewParcela.setItems(obsListParcela);
	}

	// Converte o campo de data para o formato brasileiro
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

	@FXML
	private void onBtnCadastrarParcelaAction() throws ParseException {
		System.out.println("Tenho que cadastrar uma parcela");
		// Limpa os campos
		limpaCampos();
		Parcela parcela = new Parcela();

		// Seta os dados da parcela \/

		if (entity.getId() != null) {
			// System.out.println("O entity existe");
			parcela = entity;
		}

		// Se o o DatePicker for diferente de null
		if (dataCompra.getValue() != null) {
			// Pega o dado do DatePicker e transforma em Calendar
			Calendar cal = setaCampoData(dataCompra);
			parcela.setDataCompra(cal);
		}
		// Se o usuário preencheu o valor da parcela
		if (!txtValorDaCompra.getText().isEmpty()) {
			if (txtValorDaCompra.getText().charAt(0) == '.') {
				txtValorDaCompra.setText(txtValorDaCompra.getText().substring(1));
			}
			parcela.setValorDaCompra(new BigDecimal(txtValorDaCompra.getText().replace(",", ".")));
		} else {
			parcela.setValorDaCompra(null);
		}
		// Verifica se o objeto pode ser persistido
		if (verificaErros(parcela)) {
			System.out.println("O objeto não pode ser persistido");
		} else {
			System.out.println("Valor das parcelas" + spinnerParcelas.getValue());
			// Jeito antigo
			// Cliente cli = new ClienteDAO().getByCPf(txtNomeOuCpf.getText());

			// Pega o cliente selecionado na combobox
			Cliente cli = comboboxCliente.getSelectionModel().getSelectedItem();
			// Pega em Integer a quantidade de parcelas que está contido no Spinner
			Integer qtd = spinnerParcelas.getValue();
			// Coloca numa lista de parcelas o retorno do método de gravar ou atualizar
			// parcela
			List<Parcela> parcelas = new ArrayList<>();
			if (simAddParcela.isSelected()) {
				parcelas = parcelaDAO.gravarOuAtualizar(parcela, qtd, cli, true);
			} else {
				parcelas = parcelaDAO.gravarOuAtualizar(parcela, qtd, cli, false);
			}
			// Preciso atualizar o text field de valor da parcela
			if (simAddParcela.isSelected()) {
				preencheCampoValorDaParcela();
			}

			// Preciso recarregar a inicialização do spinner
			initSpinner();
			// Carrega a table view passando como parâmetro as parcelas
			initializeNodes(parcelas);
			// notifyDataChangeListeners();
			// Se a tela de listagem de parcelas tiver aberta. Ou seja: Diferente de null
			if (ListParcela.getMainScene() != null) {
				// Eu atualizo a tabela de parcelas desse controller de acordo com
				// o cliente selecionado lá
				// ListParcelaController.getController().onBtnPesquisarAction();
				Cliente ultimoClienteBuscado = ListParcelaController.getController().getUltimoClienteBuscado();
				if (ultimoClienteBuscado != null && cli.getId() == ultimoClienteBuscado.getId()) {
					ListParcelaController.getController().realizaPesquisa(true);
					ListParcelaController.getController().getLabelClienteSelecionado()
							.setText("Cliente selecionado: " + cli.getNome() + " CPF: " + cli.getCpf());
				}
			}
		}

	}

	public static FormParcelaController getController() {
		return controller;
	}

	public static void setController(FormParcelaController controller) {
		FormParcelaController.controller = controller;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		spinnerParcelas.valueProperty().addListener((obs, oldValue, newValue) -> {
			preencheCampoValorDaParcela();
		});

		txtValorDaCompra.textProperty().addListener((obs, oldValue, newValue) -> {
			// System.out.println("Valor: " + newValue);
			preencheCampoValorDaParcela();
		});

		txtValorDaParcela.setDisable(true);
		// Não é mais necessário chamar no initialize
		// initSpinner();
		setParcelaDAO(new ParcelaDAO());
		setClienteDAO(new ClienteDAO());
		// Preenche a combobox de cliente
		updadeComboBox(comboboxCliente);

		// Eu desativo a inserção de texto manual no DatePicker
		dataCompra.getEditor().setDisable(true);
		// Não é necessário chamar na inicialização do formulário
		// initializeNodes();

		// Coloco para apenas aceitar double nesse text field
		Constraints.setTextFieldDouble(txtValorDaCompra);
		Constraints.setTextFieldMaxValor(txtValorDaCompra);
		// Atribuo ao controller, a sua respectiva instância.
		controller = this;
		// Se a listagem de clientes tiver aberto
		if (ListCliente.getMainScene() != null) {
			ListClienteController controller = ListClienteController.getController();
			// Eu me inscrevo naquele controller para escutar...
			// Explicando: Eu preciso atualizar a combobox de cliente caso lá no
			// formulário de listagem de cliente, algum cliente seja editado
			controller.subscribeDataChangerListener(this);
		}

		if (ListParcela.getMainScene() != null) {
			// subscribeDataChangerListener(ListParcelaController.getController());
		}
		// Se eu verifico que a scene é null
		if (FormParcela.getMainScene() == null) {
			System.out.println("Progrmação defensiva ativada. FormParcela é null");
			// Seto a scene pegando ela de algum controle do controller
			FormParcela.setMainScene(txtNomeOuCpf.getScene());
		}
		// Verifico se o form de cliente está aberto
		if (FormCliente.getMainScene() != null) {
			FormClienteController.getController().subscribeDataChangerListener(this);
		}
	}

	public void setParcelaDAO(ParcelaDAO parcelaDAO) {
		this.parcelaDAO = parcelaDAO;
	}

	public void setClienteDAO(ClienteDAO clienteDAO) {
		this.clienteDAO = clienteDAO;
	}

	// Responsável por limpar todas as labels de erro
	private void limpaCampos() {
		Scene mainScene = FormParcela.getMainScene();
		AnchorPane anchorPane = (AnchorPane) mainScene.getRoot();
		GridPane grid = (GridPane) anchorPane.getChildren().get(0);
		for (Node node : grid.getChildren()) {
			// System.out.println("Id: " + node.getId());
			if (node instanceof Label) {
				if (node.getId() != null && !node.getId().equals("labelAddParcela")) {
					((Label) node).setText("");
				}

			}
		}

	}

	// Inicializa as configurações do Spinner
	public void initSpinner() {
		// Value factory.
		SpinnerValueFactory<Integer> valueFactory;
		Integer valorSelecionado = null;

		// Se eu estou cadastrando
		if (entity.getId() == null) {
			System.out.println("ID é null");
			if (spinnerParcelas.getValue() != null) {
				valorSelecionado = spinnerParcelas.getValue();
			}
			valueFactory = //
					new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
			// Limpa a label
			// labelAddParcela.setText("");
			// Desativa os radio buttons
			simAddParcela.setDisable(true);
			naoAddParcela.setDisable(true);
		} else { // Se eu estou editando
			System.out.println("Caiu no else");
			Integer quantidadeParcelas = parcelaDAO.getQuantidadeParcelas(entity.getDocumento().getNum_doc())
					.intValue();
			Integer sub = 10 - quantidadeParcelas;
			// Seta a combobox o cliente a ser editado
			preencheComboBox(comboboxCliente, entity.getCliente());
			comboboxCliente.setDisable(true);
			// Tenho que desativar também o text field de filtro para buscar cliente
			txtNomeOuCpf.setDisable(true);
			if (sub == 0) {
				valueFactory = //
						new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1);
				spinnerParcelas.setDisable(true);
				// Desativa os radio buttons
				simAddParcela.setDisable(true);
				// Não posso mais adicionar parcelas, portanto esse radio tem que ficar marcado
				naoAddParcela.setSelected(true);
				naoAddParcela.setDisable(true);
			} else {
				valueFactory = //
						new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10 - quantidadeParcelas, 1);
			}
			setalabelInformaQtdParcelas();
		}
		spinnerParcelas.setValueFactory(valueFactory);
		if (entity.getId() == null && valorSelecionado != null) {
			spinnerParcelas.getValueFactory().setValue(valorSelecionado);
		}
	}

	public void setParcela(Parcela entity) {
		this.entity = entity;
	}

	private void preencheComboBox(ComboBox<Cliente> comboBox, Cliente cliente) {
		Integer id = null;
		for (Cliente clienteFor : comboBox.getItems()) {
			if (clienteFor.getId() == cliente.getId()) {
				id = clienteFor.getId();
				cliente = clienteFor;
			}
		}

		if (id != null) {
			comboBox.getSelectionModel().select(cliente);
		}

	}

	// Retorna um Calendar a partir de um DatePicker
	private Calendar setaCampoData(DatePicker txtData) throws ParseException {
		Calendar cal = Calendar.getInstance();
		String data = txtData.getValue().toString();
		System.out.println(data);
		data = data.replaceAll("-", "/");
		System.out.println(data);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/ddd");
		cal.setTime(sdf.parse(data));
		return cal;

	}

	// Método que verifica se o objeto está apto a ser persistido
	public boolean verificaErros(Object objeto) {
		boolean deuErro = false;
		Scene mainScene = FormParcela.getMainScene();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Parcela parcela = (Parcela) objeto;
		String nomeDaClasse = "";
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(parcela);
		nomeDaClasse = parcela.getClass().getSimpleName();
		if (!constraintViolations.isEmpty()) {
			deuErro = true;
		}
		// Atenção: Existe uma explicação detalhada dessa parte do código no
		// FormClienteController
		for (ConstraintViolation error : constraintViolations) {
			String msgError = error.getMessage();
			String propriedade = error.getPropertyPath().toString();
			System.out.println("Propriedade: " + propriedade + " msg de erro:" + msgError);
			AnchorPane anchorPane = (AnchorPane) mainScene.getRoot();
			GridPane grid = (GridPane) anchorPane.getChildren().get(0);
			for (Node node : grid.getChildren()) {
				// System.out.println("Id: " + node.getId());
				if (node instanceof Label) {
					if (node.getId() != null) {
						if (node.getId().equals(propriedade + nomeDaClasse)) {
							// clear
							((Label) node).setText(msgError);
						}
					}

				}
			}

		}

		return deuErro;
	}

	// Método que seta a combobox de cliente de acordo com o nome ou cpf do cliente
	// digitado no textfield
	@FXML
	public void onTxtFiltroAction() {
		Cliente clienteFiltro = new Cliente();
		System.out.println("Parabéns, você clicou no botão!");
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

	private void preencheCampoValorDaParcela() {
		if (entity.getId() == null) {
			if (spinnerParcelas.getValue() != null && !txtValorDaCompra.getText().isEmpty()
					&& verificaSeENumero(txtValorDaCompra.getText())) {
				if (!txtValorDaCompra.getText().isEmpty()) {
					BigDecimal divisor = new BigDecimal(spinnerParcelas.getValue());
					String valorCompraStr = txtValorDaCompra.getText();
					System.out.println("V: " + txtValorDaCompra.getText());
					valorCompraStr = valorCompraStr.replace(",", ".");
					System.out.println("ABACAXI AMARELO: " + valorCompraStr);
					BigDecimal valorCompra = new BigDecimal(valorCompraStr);
					BigDecimal valorDaParcela = valorCompra.divide(divisor, 2, RoundingMode.HALF_UP);
					txtValorDaParcela.setText(String.valueOf(valorDaParcela));
				}
			} else if (txtValorDaCompra.getText().isEmpty()) {
				txtValorDaParcela.setText("");
			}
		} else {
			// Estou editando

			// Quero adicionar + parcelas
			if (simAddParcela.isSelected()) {

				if (spinnerParcelas.getValue() != null && !txtValorDaCompra.getText().isEmpty()
						&& verificaSeENumero(txtValorDaCompra.getText())) {
					if (!txtValorDaCompra.getText().isEmpty()) {
						Integer quantidadeAtual = new ParcelaDAO()
								.getQuantidadeParcelas(entity.getDocumento().getNum_doc()).intValue();
						Integer novaQuantidade = spinnerParcelas.getValue();
						Integer valor = quantidadeAtual + novaQuantidade;
						System.out.println("Divisor: " + valor);
						BigDecimal divisor = new BigDecimal(valor);
						String valorCompraStr = txtValorDaCompra.getText();
						System.out.println("V: " + txtValorDaCompra.getText());
						valorCompraStr = valorCompraStr.replace(",", ".");
						System.out.println("ABACAXI AMARELO: " + valorCompraStr);
						BigDecimal valorCompra = new BigDecimal(valorCompraStr);
						BigDecimal valorDaParcela = valorCompra.divide(divisor, 2, RoundingMode.HALF_UP);
						txtValorDaParcela.setText(String.valueOf(valorDaParcela));
					}
				} else if (txtValorDaCompra.getText().isEmpty()) {
					txtValorDaParcela.setText("");
				}
			} else {
				// Quero manter a quantidade de parcelas da edição
				if (spinnerParcelas.getValue() != null && !txtValorDaCompra.getText().isEmpty()
						&& verificaSeENumero(txtValorDaCompra.getText())) {
					if (!txtValorDaCompra.getText().isEmpty()) {
						BigDecimal divisor = new BigDecimal(
								new ParcelaDAO().getQuantidadeParcelas(entity.getDocumento().getNum_doc()));
						String valorCompraStr = txtValorDaCompra.getText();
						System.out.println("V: " + txtValorDaCompra.getText());
						valorCompraStr = valorCompraStr.replace(",", ".");
						System.out.println("ABACAXI AMARELO: " + valorCompraStr);
						BigDecimal valorCompra = new BigDecimal(valorCompraStr);
						BigDecimal valorDaParcela = valorCompra.divide(divisor, 2, RoundingMode.HALF_UP);
						txtValorDaParcela.setText(String.valueOf(valorDaParcela));
					}
				} else if (txtValorDaCompra.getText().isEmpty()) {
					txtValorDaParcela.setText("");
				}
			}

		}
	}

	private boolean verificaSeENumero(String txt) {
		// Tive que mudar de . para , porque tava colocando somente números inteiros e
		// desconsiderando após o ponto .
		return txt.matches("\\d*([\\,]\\d*)?");
	}

	private void setalabelInformaQtdParcelas() {

		if (entity.getId() != null) {
			labelInformaQtdParcelas.setText("Quantidade de parcelas dessa compra em edição: "
					+ new ParcelaDAO().getQuantidadeParcelas(entity.getDocumento().getNum_doc()));
		}
	}

	@FXML
	public void onRadioAddParcelaAction() {
		System.out.println("Eu desejo adicionar mais parcelas a essa compra");
		preencheCampoValorDaParcela();
	}

	@FXML
	public void onRadioNaoAddParcelaAction() {
		System.out.println("Eu não desejo adicionar mais parcelas a essa compra");
		preencheCampoValorDaParcela();
	}

	// Método que atualiza a combobox e suas configurações
	private void updadeComboBox(ComboBox<Cliente> ComboBox) {
		// Por algum motivo no escutador do formCliente
		// Ao registrar ou editar um cliente
		// Ele não pega atualizado os registros e seta errado, seta os dados anteriores
		// na combo box
		// Portanto preciso instanciar um novo clientedao ao invés de UTILIZAR O DA
		// DEPENDÊNCIA DO CONTROLLER
		List<Cliente> list = new ClienteDAO().findAll();
		obsList = FXCollections.observableArrayList(list);
		ComboBox.setItems(obsList);

		ComboBox.getSelectionModel().select(0);

		Callback<ListView<Cliente>, ListCell<Cliente>> factory = lv -> new ListCell<Cliente>() {
			@Override
			protected void updateItem(Cliente item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome() + " - " + item.getCpf());
			}
		};
		ComboBox.setCellFactory(factory);
		ComboBox.setButtonCell(factory.call(null));
		txtNomeOuCpf.textProperty().addListener((obs, oldValue, newValue) -> {
			onTxtFiltroAction();
		});

		if (list.isEmpty()) {
			btnCadastrarParcela.setDisable(true);
		} else {
			btnCadastrarParcela.setDisable(false);

		}

	}

	// Método do contrato que seta as dependências
	// OBS*: Pode apresentar problemas no método initialize causando
	// NullPointerException
	@Override
	public void setaDependencias(Object obj, Scene scene) throws IllegalArgumentException, IllegalAccessException {
		setParcela((Parcela) obj);
		setParcelaDAO(new ParcelaDAO());
		FormParcela.setMainScene(scene);

	}

	// Método que atualiza a combobox quando um evento de outro formulário acontece
	@Override
	public void onDataChanged() {
		updadeComboBox(comboboxCliente);

	}

	@Override
	public void setMainScene(Scene scene) {
		FormParcela.setMainScene(null);
	}
}
