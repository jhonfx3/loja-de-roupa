package gui;

import java.lang.reflect.Field;
import java.math.BigDecimal;
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
import br.com.enums.TipoResidencia;
import br.com.formatador.MaskFieldUtil;
import br.com.formatador.MaskFormatter;
import br.com.interfaces.Injeta;
import br.com.modelo.Cidade;
import br.com.modelo.Cliente;
import br.com.modelo.Endereco;
import br.com.modelo.EnderecoEmpresa;
import br.com.modelo.Parcela;
import dao.CidadeDAO;
import dao.ClienteDAO;
import dao.EnderecoDAO;
import gui.listeners.DataChangeListener;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class FormClienteController implements Initializable, Injeta {

	private ListClienteController listClienteController;

	private static FormClienteController controller;

	public static FormClienteController getController() {
		return controller;
	}

	public static void setController(FormClienteController controller) {
		FormClienteController.controller = controller;
	}

	private ClienteDAO clienteDAO;

	private CidadeDAO cidadeDAO;

	private EnderecoDAO enderecoDAO;

	private Cliente entity;

	private List<DataChangeListener> dataChanceListeners = new ArrayList<>();

	// Combo Box
	@FXML
	private ComboBox<Cidade> comboboxCidade;

	@FXML
	private ComboBox<Cidade> comboboxCidadeEmpresa;

	private ObservableList<Cidade> obsList;

	// Radio buttons

	@FXML
	private RadioButton radioCelular9Digitos;

	@FXML
	private RadioButton radioCelular8Digitos;

	@FXML
	private RadioButton radioMascaraTelOn;

	@FXML
	private RadioButton radioMascaraTelOff;
	@FXML
	private RadioButton radioPropria;

	@FXML
	private RadioButton radioAlugada;

	@FXML
	private RadioButton radioTrabalha;

	@FXML
	private RadioButton radioNaoTrabalha;

	// Text Fields

	@FXML
	private TextField txtNomeEnderecoEmpresa;

	@FXML
	private TextField txtTelefoneEnderecoEmpresa;

	@FXML
	private TextField txtEnderecoEnderecoEmpresa;

	@FXML
	private TextField txtBairroEnderecoEmpresa;

	@FXML
	private TextField txtNumeroEnderecoEmpresa;

	@FXML
	private TextField txtProfissaoCliente;

	@FXML
	private TextField txtAssinaturaCliente;

	@FXML
	private TextField txtFiltro;

	@FXML
	private TextField txtFiltro2;

	@FXML
	private TextField txtEmailCliente;

	@FXML
	private TextField txtRgCliente;

	@FXML
	private TextField txtRendaMensalCliente;

	@FXML
	private TextField txtObsCliente;

	@FXML
	private TextField txtCpfCliente;

	@FXML
	private TextField txtNumeroEndereco;

	@FXML
	private TextField txtTelefoneCliente;

	@FXML
	private TextField txtCelularCliente;

	@FXML
	private TextField txtCepEndereco;

	@FXML
	private Button btnCadastrar;

	@FXML
	private TextField txtNomeCliente;

	@FXML
	private TextField txtEnderecoEndereco;

	@FXML
	private TextField txtBairroEndereco;

	// Data de aniversário controle
	@FXML
	private TextField txtAniversarioCliente;

	@FXML
	private TextField txtClienteDesdeCliente;

	// labels de erro

	@FXML
	private Label nomeEnderecoEmpresa;

	@FXML
	private Label telefoneEnderecoEmpresa;

	@FXML
	private Label enderecoEnderecoEmpresa;

	@FXML
	private Label bairroEnderecoEmpresa;

	@FXML
	private Label numeroEnderecoEmpresa;

	@FXML
	private Label nomeCliente;

	@FXML
	private Label emailCliente;

	@FXML
	private Label rgCliente;

	@FXML
	private Label aniversarioCliente;

	@FXML
	private Label cpfCliente;

	@FXML
	private Label enderecoEndereco;

	@FXML
	private Label bairroEndereco;

	@FXML
	private Label numeroEndereco;

	@FXML

	private Label telefoneCliente;
	@FXML

	private Label celularCliente;
	@FXML
	private Label cepEndereco;

	@FXML
	private Label clienteDesdeCliente;

	public Cliente getEntity() {
		return entity;
	}

	public void setEntity(Cliente entity) {
		this.entity = entity;
	}

	// Método responsável por setar na combobox a cidade desejada de acordo com o
	// text field de filtro
	// Pega o nome da cidade e seta na combobox de cidade
	@FXML
	public void onTxtFiltroAction(ComboBox<Cidade> comboBox, TextField txt) {
		Cidade cidadeFiltro = new Cidade();
		System.out.println("Parabéns, você clicou no botão!");
		Integer id = null;
		for (Cidade cidade : comboBox.getItems()) {
			if (cidade.getNome().equalsIgnoreCase(txt.getText())) {
				id = cidade.getId();
				cidadeFiltro = cidade;
			}
		}

		if (id != null) {
			comboBox.getSelectionModel().select(cidadeFiltro);
		}
	}

	@FXML
	public void onComboBoxAction() {
		// Cidade cidade = comboboxCidade.getSelectionModel().getSelectedItem();
		// System.out.println("Cidade selecionada:" + cidade.getNome());
		preencheFiltroCidade(comboboxCidade, txtFiltro);
	}

	@FXML
	public void onComboBoxEmpresaAction() {
		// Cidade cidade = comboboxCidade.getSelectionModel().getSelectedItem();
		// System.out.println("Cidade selecionada:" + cidade.getNome());
		preencheFiltroCidade(comboboxCidadeEmpresa, txtFiltro2);
	}

	private void preencheFiltroCidade(ComboBox<Cidade> comboBox, TextField filtro) {
		Cidade cidade = comboBox.getSelectionModel().getSelectedItem();
		filtro.setText(cidade.getNome());
	}

	public ListClienteController getListClienteController() {
		return listClienteController;
	}

	public void setListClienteController(ListClienteController listClienteController) {
		this.listClienteController = listClienteController;
	}

	// Método responsável por preencher os campos text field para edição
	private void preencheCamposParaEdicao(Class<?> classe, Object objeto, String nomeDaClasse)
			throws IllegalArgumentException, IllegalAccessException {
		System.out.println("OLHA AQUI O NOME DESSA CLASSE -----> " + nomeDaClasse);
		// Pego a cena principal do form cliente
		Scene mainScene = FormCliente.getMainScene();
		AnchorPane anchorPane = (AnchorPane) mainScene.getRoot();
		GridPane grid = (GridPane) anchorPane.getChildren().get(0);
		if (nomeDaClasse.equals("EnderecoEmpresa")) {
			System.out.println("é igual a endereço empresa");
		}
		// Aqui eu estou percorrendo todos os atributos da classe
		for (Field f : classe.getDeclaredFields()) {
			// Desse modo eu consigo pegar o nome da classe
			System.out.println("OLHA O NOME DESSA CLASSE AQUI" + classe.getSimpleName());
			// Deixando o field(campo) acessível
			f.setAccessible(true);
			if (nomeDaClasse.equals("EnderecoEmpresa")) {
				System.out.println("é igual a endereço empresa de novo");
			}
			System.out.println("NOME DA CLASSE: " + nomeDaClasse);
			// Aqui eu estou concatenando txt + nome do atributo começando em maiúsculo
			// Seria mais performático não realizar o for e setar manualmente um a um
			String teste = "txt";
			// Aqui eu estou cortando(tirando) o primeiro caracter do nome do campo
			// Exemplo: Nome fica ome
			// Estou fazendo isso porque o primeiro caracter tem que ser maiúsculo
			String fnameSub = f.getName().substring(1);
			// Estou pegando a primeira letra e colocando em maiúsculo
			char fname = f.getName().toUpperCase().charAt(0);
			// txt + primeira letra do nome do atributo começando com maiúsculo + nome do
			// atributo
			// sem o
			// primeiro caracter + nome da classe
			teste = teste + fname + fnameSub + nomeDaClasse;
			if (nomeDaClasse.equals("EnderecoEmpresa")) {
				System.out.println("CARAMBAAAAAAAAAAAAAAAAAAA");
			}
			// Nome do campo (ID)
			System.out.println("Campo ---> " + teste);
			System.out.println("o campo é esse acima");
			// System.out.println("Node id: " + node.getId());
			// Estou pegando um nó da cena de acordo com o seu ID
			// Que no caso é o da variável teste
			Node nodeToFind = FormCliente.getMainScene().lookup("#" + teste);
			// Verifico se o nó é diferente de null e uma instância de text field
			if (nodeToFind != null && nodeToFind instanceof TextField) {
				// Vendo se o ID do nó é igual a variável teste
				if (nodeToFind.getId().equals(teste)) {
					// Se o nó for diferente de null e uma instância de text field
					if (nodeToFind != null && nodeToFind instanceof TextField) {
						// Hora de setar no text field o valor que está no campo do objeto
						String campoObjeto = String.valueOf(f.get(objeto));

						// Se for uma instância de BigDecimal
						if (f.get(objeto) instanceof BigDecimal) {
							// Troca . por vírgula para poder exbir corretamente no formulário
							campoObjeto = campoObjeto.replace(".", ",");
							((TextField) nodeToFind).setText(campoObjeto);
						} else {
							// Somente seta
							// Verifica se é diferente de null
							// porque se for null ele pega a string null e seta
							if (f.get(objeto) != null) {
								((TextField) nodeToFind).setText(campoObjeto);

							}

						}

						// Verifica se é o text field do cliente desde
						if (nodeToFind.getId().equals("txtClienteDesdeCliente") && f.get(objeto) == null) {
							// Seta para vazio o text field se é null o atributo
							((TextField) nodeToFind).setText("");

						}
					}
				}
			}

		}

	}

	// Método responsável por inscrever um listener
	public void subscribeDataChangerListener(DataChangeListener listener) {
		dataChanceListeners.add(listener);
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChanceListeners) {
			listener.onDataChanged();
		}

	}

	// Método responsável por atualizar os campos do formulário em caso de edição
	public void updateFormData() throws IllegalArgumentException, IllegalAccessException {
		// Popula as informações
		// Popular as caixas de texto

		// Quer dizer que é cadastro e não é edição, caso ocorra o else abaixo,
		// significa que é edição porque já tem ID
		if (entity.getId() == null) {
			System.out.println("é igual a null");
		} else {
			System.out.println("Não é igual a null");
			// Pegando a classe da entity
			Class<Cliente> clazz = (Class<Cliente>) entity.getClass();
			// Preenchendo os campos da entidade cliente
			// Passando como parâmetro, a clazz, a entity, e o nome da classe
			preencheCamposParaEdicao(clazz, entity, clazz.getSimpleName());
			// Pegando o endereço do cliente a ser editado
			Endereco endereco = enderecoDAO.getById(entity.getEndereco().getId());
			// Pegando a clazz do endereço
			Class<Endereco> clazz2 = (Class<Endereco>) endereco.getClass();
			// Passando como parâmetro: a clazz, o objeto endereço e o nome da clazz
			preencheCamposParaEdicao(clazz2, endereco, clazz2.getSimpleName());
			// Se o cliente tiver um endereço empresa, ou seja, se ele trabalhar em uma
			// empresa
			if (entity.getEnderecoEmpresa() != null) {
				System.out.println("Entrei nesse if aqui caralho");
				// Pegando a clazz do endereço empresa
				Class<EnderecoEmpresa> clazz3 = (Class<EnderecoEmpresa>) entity.getEnderecoEmpresa().getClass();
				// Imprimindo o nome da classe: clazz3
				System.out.println(clazz3.getSimpleName());
				// Preenchendo os campos para edição, passando como parâmetro:
				// Estou passando no parâmetro a super classe, ou seja, os campos da classe pai
				// Que no caso é o Endereço e a classe filha EnderecoEmpresa
				// Para poder puxar os dados de EnderecoEmpresa que são os mesmos dados que a
				// classe pai tem
				// Eu tenho que pegar a super classe
				// Por exemplo
				// Endereco tem endereco, bairro e numero
				// EnderecoEmpresa extends Endereco
				// Para puxar esses campos herdados(Java Reflection) eu preciso passar a classe
				// pai e não a classe fila
				// Senão não vai dar certo, vai dar problema
				preencheCamposParaEdicao(clazz3.getSuperclass(), entity.getEnderecoEmpresa(), clazz3.getSimpleName());
				// Aqui eu estou passando novamente a clazz3 mas dessa vez é para
				// puxar os campos específicos do EnderecoEmpresa
				// Logo, eu devo passsar somente clazz3 e não a sua super classe
				// Exemplo:
				// Campos que o EnderecoEmpresa tem mas não são herdados:
				// Nome e telefone
				preencheCamposParaEdicao(clazz3, entity.getEnderecoEmpresa(), clazz3.getSimpleName());
				// Aqui eu estou marcando o radio button para: Ele trabalha em empresa
				// Porque o EnderecoEmpresa é diferente de null
				radioTrabalha.setSelected(true);
				// Capturando a cidade em que ele trabalha
				Cidade cidade = entity.getEnderecoEmpresa().getCidade();
				// Setando a combobox da cidade em que o cliente trabalha para
				// a cidade em que ele trabalha
				// Passando como parâmetro o ID da combobox, a cidade em que trabalha
				// E o text field do filtro onde o usuário digita o nome da cidade em que ele
				// trabalha
				preencheComboBox(comboboxCidadeEmpresa, cidade, txtFiltro2);
				desativaCamposEnderecoEmpresa(false);
			}
			// Capturando a cidade do cliente
			Cidade cidade = entity.getEndereco().getCidade();
			// Mesmo coisa que acima /\, estou setando na combobox a cidade em que o cliente
			// mora
			preencheComboBox(comboboxCidade, cidade, txtFiltro);

			// Aqui estava acontecendo um problema nesses dois sets:
			// Edita um registro e remove outro, e volta no anterior.
			// O problema era que os dados continuavam em memória no setnumero = null
			// entity.getEndereco().setNumero(null);
			// entity.getEndereco().setCidade(null);
			// Esse problema fazia com que de alguma forma os dados ao abrir e fechar os
			// formulários
			// No momento da edição, se setassem para null
			// Por exemplo:
			// Eu abri o form para edição e aparecia o número correto. Ex: 1234
			// Aí eu fechava e abria de novo, mas ao abrir de novo não preenchia o número
			// mais
			// Porque ele se tornava null
			String x;
			// Capturando em string o tipo de residência do cliente
			x = entity.getResidencia().toString();
			System.out.println("O valor de x é: " + x);
			// Verifica se é casa própria
			if (x.equals("PROPRIA")) {
				// Como é própria, setava a radio button correspondente
				radioPropria.setSelected(true);
			} else if (x.equals("AlUGADA")) {
				// Mesma coisa que acima, mas dessa vez é alugada
				radioAlugada.setSelected(true);
			}
			// Preenchendo o campo de data do aniversário do cliente
			// Passando como parâmetro o ID do DatePicker e o Calendar do aniversário
			// Fazendo um getter
			// Não preciso mais preencher o aniversário como calendar porque agora ele é
			// String
			// preencheCampoDeCalendario(txtDataAniversarioCliente,
			// entity.getAniversario());
			if (entity.getClienteDesde() != null) {
				// Mesma coisa para o DatePicker de cliente Desde
				preencheCampoDeCalendario(txtClienteDesdeCliente, entity.getClienteDesde());
			}

		}

		//

	}

	@FXML
	private void habilitaCamposEnderecoEmpresa() {
		desativaCamposEnderecoEmpresa(false);
	}

	@FXML
	private void desabilitaCamposEnderecoEmpresa() {
		desativaCamposEnderecoEmpresa(true);
		limpaCamposEnderecoEmpresa();
	}

	// Método responsável por preencher uma combobox de acordo com:
	// O ID da combobox, ou seja, o componente em si. A cidade a ser setada
	// E o text field de filtro onde o usuário digita o nome da cidade
	// Isso serve para preencher o campo de texto do filtro de acordo com o nome da
	// cidade
	private void preencheComboBox(ComboBox<Cidade> comboBox, Cidade cidade, TextField txtFiltro) {
		Integer id = null;
		for (Cidade cidadeFor : comboBox.getItems()) {
			if (cidadeFor.getNome().equalsIgnoreCase(cidade.getNome())) {
				id = cidadeFor.getId();
				cidade = cidadeFor;
			}
		}

		if (id != null) {
			comboBox.getSelectionModel().select(cidade);
			txtFiltro.setText(cidade.getNome());
		}

	}

	// Método responsável por preencher um campo de calendário:
	// Recebe como parâmetro o DatePicker e um objeto Calendar
	private void preencheCampoDeCalendario(TextField txtData, Calendar data) {
		// Lógica para setar a data de aniversário
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dataStr = sdf.format(data.getTime()).toString();
		System.out.println("Data de aniversário:" + dataStr);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate local = LocalDate.parse(dataStr, formatter);
		txtData.setText(dataStr);

	}

	// Responsável por setar a máscara do celular para 8 dígitos
	@FXML
	public void onRadioCelular8() {
		System.out.println("8 dígitos");
		// Instancia maskFOrmatter como parâmetro o text field a ser aplicado a máscara
		MaskFormatter formatter = new MaskFormatter(txtTelefoneCliente);
		formatter = new MaskFormatter(txtCelularCliente);
		// O terceiro parâmetro boolean é se a máscara vai ser aplicada ou não. False =
		// desativado
		formatter.setMask(MaskFormatter.TEL_8DIG, true);
	}

	// Responsável por setar a máscara do celular para 9 dígitos
	@FXML
	public void onRadioCelular9() {
		System.out.println("9 dígitos");
		MaskFormatter formatter = new MaskFormatter(txtTelefoneCliente);
		formatter = new MaskFormatter(txtCelularCliente);
		formatter.setMask(MaskFormatter.TEL_9DIG, true);
	}

	@FXML
	public void onRadioTelOn() {
		System.out.println("Tenho que ligar a máscara");
		ativaOuDesativaMascaraTelefone(true);
	}

	@FXML
	public void onRadioTelOff() {
		System.out.println("Tenho que desligar a máscara");
		ativaOuDesativaMascaraTelefone(false);
	}

	// Ativa ou desativa a máscara do telefone
	private void ativaOuDesativaMascaraTelefone(boolean status) {
		// Nesse caso são 2 telefone: Cliente e Empresa que ele trabalha
		MaskFormatter formatter = new MaskFormatter(txtTelefoneCliente);
		formatter.setMask(MaskFormatter.TEL_8DIG, status);
		formatter = new MaskFormatter(txtTelefoneEnderecoEmpresa);
		formatter.setMask(MaskFormatter.TEL_8DIG, status);
	}

	// Evento do botão de cadastrar
	@FXML
	public void onBtnCadastrarAction(ActionEvent event) throws ParseException {
		System.out.println("Você clicou no botão!");
		limpaCampos();
		Cliente cli = new Cliente();
		// Se a entidade tiver ID, então eu estou alterando
		if (entity.getId() != null) {
			// CLi aponta para entity
			cli = entity;
		}
		Endereco endereco = new Endereco();
		// Se o endereço for diferente de null
		if (entity.getEndereco() != null) {
			System.out.println("Entrei aqui");
			// Seto o ID do endereço para o ID do endereço atual
			endereco.setId(entity.getEndereco().getId());
			System.out.println("----> " + cli.getEndereco().getId());
			// cli.getEndereco().setId(entity.getEndereco().getId());
		}
		endereco.setEndereco(txtEnderecoEndereco.getText());
		endereco.setBairro(txtBairroEndereco.getText());
		// Se eu preenchi o número
		if (!txtNumeroEndereco.getText().isEmpty()) {
			endereco.setNumero(Integer.valueOf(txtNumeroEndereco.getText()));
		}
		// Se eu preenchi a renda mensal
		if (!txtRendaMensalCliente.getText().isEmpty()) {
			// Verifica se o primeiro caracter é um ponto
			if (txtRendaMensalCliente.getText().charAt(0) == '.') {
				// System.out.println("Existe um . na frente da string: " +
				// txtRendaMensalCliente.getText());
				// System.out.println("Nova string");
				// System.out.println(txtRendaMensalCliente.getText().substring(1));
				txtRendaMensalCliente.setText(txtRendaMensalCliente.getText().substring(1));
			}
			cli.setRendaMensal(new BigDecimal(txtRendaMensalCliente.getText().replace(",", ".")));
		} else {
			cli.setRendaMensal(null);
		}

		// endereco.setTelefone(txtTelefone.getText());
		// endereco.setCelular(txtCelular.getText());
		// Capturo a cidade da combobox
		Cidade cidade = comboboxCidade.getSelectionModel().getSelectedItem();
		// System.out.println("Cidade que eu devo setar: "+cidade.getNome());
		endereco.setCidade(cidade);
		endereco.setCep(txtCepEndereco.getText());
		cli.setNome(txtNomeCliente.getText());

		// Se o cliente não tiver ID
		if (cli.getId() == null) {
			// Então quer dizer que eu estou cadastrando, logo devo setar somente o que foi
			// digitado
			// pelo usuário
			cli.setCpf(txtCpfCliente.getText());
		} else {
			// Estou editando então eu tenho que concatenar junto o ID para excluir da busca
			// ao banco de dados
			// o ID atual, porque somente ele pode te esse CPF
			// Explicando melhor:
			// No momento da edição de um registro, somente aquele cliente pode ter aquele
			// CPF
			// Eu não posso verificar se para aquele cliente, já existe aquele CPF no banco
			// de dadoss
			// Porque não iria deixar eu editar, porque já existe alguém com aquele CPF
			// Mas essa pessoa é a dona do CPF, então eu tenho que poder cadastrar como
			// exceção aquele cliente
			// Ou seja, se for o CPF que pertence ao cliente a ser editado, eu tenho que
			// deixar passar
			// A verificação de CPF já existente é realmente feita mas no caso de edição
			// Eu excluo o ID do cliente a ser editado, porque ele já é o dono do CPF
			cli.setCpf(cli.getId() + "," + txtCpfCliente.getText());
		}

		// Verificando se está registrando ou editando
		// Aqui eu estou fazendo a mesma coisa do CPF com o e-mail
		// Não é possível utilizar a tag @Email do hibernate por causa da alteração
		// Na alteração eu devo permitir que o ID a ser editado possa colocar aquele
		// e-mail de novo

		// Aqui acontece a mesma coisa do CPF. Leia acima.
		// Detalhe: Por isso eu não posso utilizar as tags do prontas do hibernate
		// Como por ex: @CPF e @Email
		// Porque elas vão dar campo inválido por enviar no caso de edição o ID junto.
		// Ou seja: Caso eu tivesse o cliente a ser editao de ID 45
		// Eu mandaria 45,email_cliente
		// A tag pronta do hibernate daria e-mail inválido
		// Por isso eu necesito criar minhas próprias validações
		if (!txtEmailCliente.getText().isEmpty()) {
			// Se eu estou cadastrando
			if (cli.getId() == null) {
				cli.setEmail(txtEmailCliente.getText());
			} else {
				cli.setEmail(cli.getId() + "," + txtEmailCliente.getText());
			}
		} else {
			cli.setEmail(null);
		}

		cli.setTelefone(txtTelefoneCliente.getText());
		cli.setCelular(txtCelularCliente.getText());
		cli.setEndereco(endereco);
		cli.setRg(txtRgCliente.getText());
		cli.setObs(txtObsCliente.getText());
		// Aniversário é String agora
		cli.setAniversario(txtAniversarioCliente.getText());
		// Não é mais necessário porque ele
		// deve ser unique, logo precisa de outra lógica
		// cli.setEmail(txtEmailCliente.getText());
		cli.setProfissao(txtProfissaoCliente.getText());
		cli.setAssinatura(txtAssinaturaCliente.getText());
		// Verificando se o DatePicker é tem o valor diferente de null
		// Se tiver eu seto o aniversário do cliente

		/**
		 * Agora o data aniversário é String e não calendar
		 * 
		 * if (!txtDataAniversarioCliente.getText().isEmpty()) { Calendar cal =
		 * setaCampoData(txtDataAniversarioCliente); cli.setAniversario(cal); } else {
		 * cli.setAniversario(null); }
		 * 
		 **/

// Mesma coisa acima /\
		if (!txtClienteDesdeCliente.getText().isEmpty()) {
			Calendar cal = setaCampoData(txtClienteDesdeCliente);
			cli.setClienteDesde(cal);
		} else {
			cli.setClienteDesde(null);
		}

		// System.out.println("Data de aniversário:
		// "+txtDataAniversario.getValue().toString());

		// boolean var1 = false;
		// boolean var2 = false;
		// var1 = verificaErros(cli);
		// var2 = verificaErros(endereco);
		EnderecoEmpresa enderecoEmp = new EnderecoEmpresa();
		boolean temQueExcluir = false;
		Integer idASerExcluido = 0;
		// Se trabalha em empresa
		if (radioTrabalha.isSelected()) {
			System.out.println("Esse cliente trabalha em empresa");
			// Seto o endereço e bairro \/
			enderecoEmp.setEndereco(txtEnderecoEnderecoEmpresa.getText());
			enderecoEmp.setBairro(txtBairroEnderecoEmpresa.getText());
			// Seta o número
			if (!txtNumeroEnderecoEmpresa.getText().isEmpty()) {
				enderecoEmp.setNumero(Integer.valueOf(txtNumeroEnderecoEmpresa.getText()));
			}
			// Seta nome e telefone
			enderecoEmp.setNome(txtNomeEnderecoEmpresa.getText());
			enderecoEmp.setTelefone(txtTelefoneEnderecoEmpresa.getText());

			// Verificando qual cidade foi escolhida
			Cidade cidade2 = comboboxCidadeEmpresa.getSelectionModel().getSelectedItem();
			enderecoEmp.setCidade(cidade2);

			// Aqui eu estou verificando se na edição eu já tenho um endereço empresa
			if (entity.getEnderecoEmpresa() != null) {
				// Verifico se o ID da empresa é diferente de null
				if (entity.getEnderecoEmpresa().getId() != null) {
					// Seto o ID
					enderecoEmp.setId(entity.getEnderecoEmpresa().getId());
				}
			}
			// Na minha regra de negócio o endereço empresa não precisa ter CEP
			enderecoEmp.setCep(null);
			// Seto o endereço empresa
			cli.setEnderecoEmpresa(enderecoEmp);
		} else {
			// Aqui ele não trabalha em empresa
			// Se eu estou editando
			if (entity.getId() != null) {
				// Se eu tenho um endereço empresa
				if (entity.getEnderecoEmpresa() != null) {
					// Como eu estou definindo que ele não trabalha em empresa
					// Mas eu estou editando e atualmente ele tem um endereço empresa
					// Eu vou precisar excluir esse endereço empresa
					// Logo eu seto a variável para true
					temQueExcluir = true;
					// Armazeno o ID do endereço empresa a ser excluido
					idASerExcluido = entity.getEnderecoEmpresa().getId();
					System.out.println("A ser excluido: " + idASerExcluido);
				}
			}
			// Removo setando para null o relacionamento endereço -> endereço empresa
			cli.setEnderecoEmpresa(null);
		}
		// Instancio uma lista de objetos
		List<Object> objetos = new ArrayList<>();
		objetos.add(cli);
		objetos.add(endereco);
		// Se ele trabalha em empresa
		// Então eu adiciono o endereço empresa
		if (radioTrabalha.isSelected()) {
			objetos.add(enderecoEmp);
		}
		if (verificaErros(objetos)) {
			System.out.println("O objeto não pode ser persistido");
		} else {
			System.out.println("O objeto pode ser persistido");
			// String residencia;
			if (radioPropria.isSelected()) {
				// residencia = "PROPRIA";
				cli.setResidencia(TipoResidencia.PROPRIA);
			} else {
				// residencia = "AlUGADA";
				cli.setResidencia(TipoResidencia.AlUGADA);
			}
			// TipoResidencia tipoResidencia = TipoResidencia.valueOf(residencia);

			// Nessa lógica da linha 716 até 720
			// Eu estou apenas imprimindo o CPF correto, sem a vírgula
			// E posteriormente setando no cliente
			String cpf = cli.getCpf();
			System.out.println("O CPF correto é:" + cpf.substring(cpf.indexOf(",") + 1));
			cpf = cpf.substring(cpf.indexOf(",") + 1);
			System.out.println("Correto: " + cpf);
			cli.setCpf(cpf);

			// Fazer a mesma lógica acima do cpf mas com email
			if (cli.getEmail() != null) {
				String email = cli.getEmail();
				// Esse código aqui abaixo já não é mais necessário porque o e-mail não é mais
				// obrigatório
				// Portanto, estou comentando ele
				// Também tem que tirar a inserção da vírgula por causa disso
				System.out.println("O e-mail correto é:" + email.substring(email.indexOf(",") + 1));
				email = email.substring(email.indexOf(",") + 1);
				System.out.println("Correto: " + email);
				cli.setEmail(email);
			}

			// cli.setEnderecoEmpresa(enderecoEmp);
			// Finalnente eu gravo ou edito o cliente
			clienteDAO.gravarOuAtualizar(cli);
			// Crio o objeto cliente
			Cliente selectedItemListParcela = new Cliente();
			Cliente selectedItemCadParcela = new Cliente();

			// Se a tela de listagem de parcela tiver aberta, pois é diferente de null
			if (ListParcela.getMainScene() != null) {
				// Armazeno o cliente selecionado na combobox da tela de listagem de parcela
				selectedItemListParcela = ListParcelaController.getController().getComboboxCliente().getSelectionModel()
						.getSelectedItem();
			}

			if (FormParcela.getMainScene() != null) {
				selectedItemCadParcela = FormParcelaController.getController().getComboboxCliente().getSelectionModel()
						.getSelectedItem();
			}

			// Notifico os forms que tiverem escutando
			notifyDataChangeListeners();
			// Mesma verificação: Se a tela de listagem de parcela tiver aberta
			if (ListParcela.getMainScene() != null) {
				preencheClienteSelecionadoCombo(selectedItemListParcela,
						ListParcelaController.getController().getComboboxCliente());
				// Se a table view da list parcela não está vazia
				if (!ListParcelaController.getController().getTableViewParcela().getItems().isEmpty()) {
					Parcela parcela = ListParcelaController.getController().getTableViewParcela().getItems().get(0);
					// Se o ID do cliente que foi alterado equivale ao mesmo cliente das parcelas da
					// tabela
					if (cli.getId() == parcela.getCliente().getId()) {
						// ListParcelaController.getController().onBtnPesquisarAction();
						ListParcelaController.getController().realizaPesquisa(true);
					}
				}
			}
			if (entity.getId() != null) {
				if (ListParcela.getMainScene() != null) {
					if (ListParcelaController.getController().getUltimoClienteBuscado().getId() == cli.getId()) {
						ListParcelaController.getController().setUltimoClienteBuscado(cli);
						ListParcelaController.getController().getLabelClienteSelecionado()
								.setText("Cliente selecionado: " + cli.getNome() + " CPF: " + cli.getCpf());
					}
				}

			}
			List<Cliente> findAll = new ClienteDAO().findAll();
			if (entity.getId() == null) {
				if (findAll.size() == 1) {
					if (ListParcela.getMainScene() != null) {
						if (ListParcelaController.getController().getUltimoClienteBuscado() == null) {
							Cliente selectedItem = ListParcelaController.getController().getComboboxCliente()
									.getSelectionModel().getSelectedItem();
							if (selectedItem.getId() == cli.getId()) {
								ListParcelaController.getController().setUltimoClienteBuscado(selectedItem);
								ListParcelaController.getController().realizaPesquisa(true);
							}
						}
					}
				}
			}

			if (FormParcela.getMainScene() != null) {
				preencheClienteSelecionadoCombo(selectedItemCadParcela,
						FormParcelaController.getController().getComboboxCliente());
			}
			// Se a tela de listagem de cliente tiver aberta. Pois é diferente de null
			if (listClienteController != null) {
				// Atualizado a table view dessa tela de listagem de clientes
				listClienteController.updateTableView();
				// Atualizo a pesquisa por filtro
				// Necessito, senão ao clicar em um cliente que foi editado, ele pega
				// os dados do cliente antigo ao invés do novo
				// Ex: Seto a renda mensal para 10500, e depois clico no msm cliente,
				// a renda mensal vai ser a antiga ao invés da nova
				// por isso, necessito atualizar a pesquisa do botão de filtro

				// Comentei porque não preciso mais, agora eu fiz um new ClienteDAO(). no
				// ListClienteController - updatetableview
				// listClienteController.onBtnPesquisar();
			}
			// Se eu tiver que excluir algum endereço empresa e tenho um ID a ser excluído
			if (temQueExcluir && idASerExcluido != null) {
				System.out.println("Tenho que excluir o registro de ID: " + idASerExcluido);
				Endereco enderecoEx = enderecoDAO.getById(idASerExcluido);
				// Excluo o endereço empresa
				enderecoDAO.excluir(enderecoEx);
			}
			Scene mainScene = Main.getMainSceneMain();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			ObservableList<Node> children = mainVBox.getChildren();
			MenuBar node = (MenuBar) children.get(0);
			// Fechando o formulário
			Utils.currentStage(event).close();
			FormCliente.setMainScene(null);
			// EntityManager em = new JPAUtil().getEntityManager();
			// em.getTransaction().begin();
			// em.persist(cli);
			// em.getTransaction().commit();
			// em.close();
		}

	}

	private void preencheClienteSelecionadoCombo(Cliente selectedItem, ComboBox<Cliente> combo) {
		// Se o cliente selecionado for diferente de null
		if (selectedItem != null) {
			// Toda essa lógica até a linha 764 é responsável por setar
			// na combobox de cliente da tela de listagem de parcela
			// novamente o cliente que já estava selecionado na combobox
			Cliente clienteFiltro = new Cliente();
			Integer id = null;
			for (Cliente cliente : combo.getItems()) {
				if (cliente.getId() == selectedItem.getId()) {
					id = cliente.getId();
					clienteFiltro = cliente;
				}
			}

			if (id != null) {
				combo.getSelectionModel().select(clienteFiltro);
			}
		}
	}

	// Método responsável por retornar um Calendar através do TextField
	// Pega o conteúdo do TextField e transforma em Calendar.
	private Calendar setaCampoData(TextField txtData) throws ParseException {
		Calendar cal = Calendar.getInstance();
		try {
			String data = txtData.getText();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			cal.setTime(sdf.parse(data));
		} catch (ParseException e) {
			// e.printStackTrace();
			System.err.println("A data é inválida");
			if (txtData.getId().equals("txtDataAniversarioCliente")) {
				// Não preciso mais disso porque não farei mais com data aniversário
				// aniversarioCliente.setText("A data aniversário é inválida");
			} else {
				clienteDesdeCliente.setText("A data é inválida");
			}

		}
		return cal;

	}

	// Percorre todas as labels de erro e limpa elas
	// Ela não limpa as que ficam na frente dos text field porque elas não tem ID
	private void limpaCampos() {
		Scene mainScene = FormCliente.getMainScene();
		AnchorPane anchorPane = (AnchorPane) mainScene.getRoot();
		GridPane grid = (GridPane) anchorPane.getChildren().get(0);
		for (Node node : grid.getChildren()) {
			// System.out.println("Id: " + node.getId());
			if (node instanceof Label) {
				if (node.getId() != null) {
					((Label) node).setText("");
				}

			}
		}

	}

	// Percorre todas as labels de erro do EnderecoEmpresa e limpa elas
	// Ela não limpa as que ficam na frente dos text field porque elas não tem ID
	private void limpaCamposEnderecoEmpresa() {
		Scene mainScene = FormCliente.getMainScene();
		AnchorPane anchorPane = (AnchorPane) mainScene.getRoot();
		GridPane grid = (GridPane) anchorPane.getChildren().get(0);
		for (Node node : grid.getChildren()) {
			// System.out.println("Id: " + node.getId());
			if (node instanceof Label) {
				if (node.getId() != null && node.getId().contains("EnderecoEmpresa")) {
					((Label) node).setText("");
				}

			}
		}

	}

	// Método que verifica se os objetos contém erros de validação
	public boolean verificaErros(List<Object> objetos) {
		// Variável que vai indicar se deu erro
		boolean deuErro = false;
		// Pego a cena principal do form cliente
		// OBS: Eu poderia pegar a cena através de qualquer componente do form cliente
		Scene mainScene = FormCliente.getMainScene();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		String nomeDaClasse = "";
		for (Object object : objetos) {
			Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
			// Pego o nome da classe
			nomeDaClasse = object.getClass().getSimpleName();
			// Se não tiver vazio então deu algum erro
			if (!constraintViolations.isEmpty()) {
				deuErro = true;
			}
			// Percorro as contraints violatons
			for (ConstraintViolation error : constraintViolations) {
				// Capturo a msg de erro
				String msgError = error.getMessage();
				// Capturo a propriedade que deu erro
				String propriedade = error.getPropertyPath().toString();
				System.out.println("Propriedade: " + propriedade + " msg de erro:" + msgError);
				AnchorPane anchorPane = (AnchorPane) mainScene.getRoot();
				GridPane grid = (GridPane) anchorPane.getChildren().get(0);
				// Percorro os nós
				for (Node node : grid.getChildren()) {
					// System.out.println("Id: " + node.getId());
					// Se for uma instância de Label
					if (node instanceof Label) {
						// Se o nó tiver um ID. Ou Seja ID diferente de null
						if (node.getId() != null) {
							// Se o ID do nó for igual a propriedade+nomeDaClasse
							// Exemplo: nomeCliente
							if (node.getId().equals(propriedade + nomeDaClasse)) {
								// seto a mensagem de erro para aquela label
								((Label) node).setText(msgError);
							}
						}

					}
				}

			}
		}
		// Não faço mais com aniversário
		if (!clienteDesdeCliente.getText().isEmpty()) {
			deuErro = true;
		}
		// Não preciso mais fazer isso com aniversário
		// boolean aniversario = verificaDataInvalida(txtDataAniversarioCliente);
		boolean desde = verificaDataInvalida(txtClienteDesdeCliente);
		if (desde) {
			deuErro = true;
		}
		return deuErro;
	}

	public ClienteDAO getClienteDAO() {
		return clienteDAO;
	}

	public void setClienteDAO(ClienteDAO clienteDAO) {
		this.clienteDAO = clienteDAO;
	}

	public void setCliente(Cliente entity) {
		this.entity = entity;
	}

	public CidadeDAO getCidadeDAO() {
		return cidadeDAO;
	}

	public void setCidadeDAO(CidadeDAO cidadeDAO) {
		this.cidadeDAO = cidadeDAO;
	}

	public EnderecoDAO getEnderecoDAO() {
		return enderecoDAO;
	}

	public void setEnderecoDAO(EnderecoDAO enderecoDAO) {
		this.enderecoDAO = enderecoDAO;
	}

	private boolean verificaDataInvalida(TextField txt) {
		boolean invalido = false;
		try {
			if (!txt.getText().isEmpty()) {
				String data = txt.getText();
				String dia, mes;
				dia = data.substring(0, data.indexOf("/"));
				mes = data.substring(data.indexOf("/") + 1, data.indexOf("/") + 3);
				System.out.println("Dia: " + dia + " Mês: " + mes);

				if (dia.equals("00") || mes.equals("00")) {
					System.out.println("00 ou 00 por isso recebeu true");
					invalido = true;
				}

				if (Integer.valueOf(dia) > 31 || Integer.valueOf(mes) > 12) {
					System.out.println("> 31 ou > 12 por isso recebeu true");
					invalido = true;
				}
			}
		} catch (Exception e) {
			System.out.println("Deu exceção de tamanho de string");
			invalido = true;
		}
		if (invalido) {
			System.out.println("é inválido");
			if (txt.getId().equals("txtDataAniversarioCliente")) {
				System.out.println("Caiu no if do aniversario");
				aniversarioCliente.setText("Data anivesário inválida");
			} else {
				System.out.println("Caiu no else");
				clienteDesdeCliente.setText("Data inválida");
			}
		}
		return invalido;
	}

	public void initialize(URL url, ResourceBundle rb) {
		setController(this);
		desativaCamposEnderecoEmpresa(true);
		System.out.println("OIE");
		// A máscara de telefone começa ligada
		radioMascaraTelOn.setSelected(true);
		// A máscara de 9 dígitos do celular começa ligada
		radioCelular9Digitos.setSelected(true);
		setClienteDAO(new ClienteDAO());
		setCidadeDAO(new CidadeDAO());
		setEnderecoDAO(new EnderecoDAO());
		// Se a tela de listagem de cliente tiver aberta. Ou seja: diferente de null
		if (ListCliente.getMainScene() != null) {
			// Pego o controller da tela de listaggem de clientes e seto como dependência
			// aqui nesse controller
			setListClienteController(ListClienteController.getController());
		}
		// Se a tela de listaggem de parcela tiver aberta. Ou seja: diferente de null
		if (ListParcela.getMainScene() != null) {
			System.out.println("list parcela está aberto");
			// Inscrevo aqui a listagem de parcela para escutar os eventos aqui
			subscribeDataChangerListener(ListParcelaController.getController());
			// ListParcelaController controller = ListParcelaController.getController();
			// controller.subscribeDataChangerListener(this);
		}
		// Se a tela de form cad parcela tiver aberta. Ou seja: diferente de null
		if (FormParcela.getMainScene() != null) {
			// Inscrevo aqui o form de cad parcela para escutar os eventos aqui
			subscribeDataChangerListener(FormParcelaController.getController());
		}
		Constraints.setTextFieldDouble(txtCpfCliente);
		Constraints.setTextFielNumeros(txtNumeroEndereco);
		Constraints.setTextFielNumeros(txtNumeroEnderecoEmpresa); // Também não pode ter . Por isso mudei
		Constraints.setTextFieldDouble(txtRendaMensalCliente);
		Constraints.setTextFieldMaxValor(txtRendaMensalCliente);
		Constraints.setTextFieldLetra(txtNomeCliente);
		Constraints.setTextFieldLetra(txtBairroEndereco);
		Constraints.setTextFieldMaxLength(txtNumeroEndereco, 7);
		Constraints.setTextFieldMaxLength(txtNumeroEnderecoEmpresa, 7);
		// Constraints.setTextFieldLetra(txtEndereco); Rua pode conter números sim. Ex:
		// Rua 15 de Maio

		MaskFormatter formatter = new MaskFormatter(txtTelefoneCliente);
		formatter.setMask(MaskFormatter.TEL_8DIG, true);

		formatter = new MaskFormatter(txtCelularCliente);
		formatter.setMask(MaskFormatter.TEL_9DIG, true);

		formatter = new MaskFormatter(txtRgCliente);
		formatter.setMask(MaskFormatter.RG, true);

		formatter = new MaskFormatter(txtTelefoneEnderecoEmpresa);
		formatter.setMask(MaskFormatter.TEL_8DIG, true);

		// Aplicando as máscaras de data
		MaskFieldUtil.dateField(txtAniversarioCliente);
		MaskFieldUtil.dateField(txtClienteDesdeCliente);
		// Quando eu utilizava DatePicker
		// txtDataAniversarioCliente.getEditor().setDisable(true);
		// txtClienteDesdeCliente.getEditor().setDisable(true);

		// Carregar as cidades
		updadeComboBox(comboboxCidade, txtFiltro);
		updadeComboBox(comboboxCidadeEmpresa, txtFiltro2);

	}

	// Método responsável por preencher uma combobox, de acordo com:
	// A combo box em si e um text field.
	// O text field é o text field que vai ter um evento em tempo real.
	// Esse evento a cada caracter digitado, ele seta na combobox a string
	// correspondente
	// Exemplo: Se eu digitar Itararé, seta a cidade de Itararé na combobox
	private void updadeComboBox(ComboBox<Cidade> ComboBox, TextField txt) {
		List<Cidade> list = cidadeDAO.findAllCidades();
		obsList = FXCollections.observableArrayList(list);
		ComboBox.setItems(obsList);

		ComboBox.getSelectionModel().select(0);

		Callback<ListView<Cidade>, ListCell<Cidade>> factory = lv -> new ListCell<Cidade>() {
			@Override
			protected void updateItem(Cidade item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		ComboBox.setCellFactory(factory);
		ComboBox.setButtonCell(factory.call(null));
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			onTxtFiltroAction(ComboBox, txt);
		});

	}

	private void desativaCamposEnderecoEmpresa(boolean status) {
		txtNomeEnderecoEmpresa.setDisable(status);
		txtTelefoneEnderecoEmpresa.setDisable(status);
		txtEnderecoEnderecoEmpresa.setDisable(status);
		txtBairroEnderecoEmpresa.setDisable(status);
		txtNumeroEnderecoEmpresa.setDisable(status);
		comboboxCidadeEmpresa.setDisable(status);
	}

	// Método do contrato. Ele seta as dependências. OBS: Não seta corretamente
	// igual o Initialize
	// Ou seja, dependendo do lugar que são chamadas pode dar Null Pointer Exception
	@Override
	public void setaDependencias(Object obj, Scene scene) throws IllegalArgumentException, IllegalAccessException {
		setCliente((Cliente) obj);
		setCidadeDAO(new CidadeDAO());
		setClienteDAO(new ClienteDAO());
		FormCliente.setMainScene(scene);
		updateFormData();
	}

	@Override
	public void setMainScene(Scene scene) {
		FormCliente.setMainScene(null);
	}

}
