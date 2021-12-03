package gui.util;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.TextField;

public class Constraints {
	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*")) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldLetra(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			txt.setText(txt.getText().replaceAll("[^A-Za-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ | ^a-z]", ""));
		});
	}

	public static void setTextFieldMaxLength(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldMaxValor(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.isEmpty() && newValue.charAt(0) != ','
					&& Double.valueOf(newValue.replace(",", ".")) > 999999) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldMaxValorCustom(TextField txt, Integer max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.isEmpty()) {
				BigInteger valor = new BigInteger(newValue);
				BigInteger maxValor = new BigInteger("2000000000");
				if (valor.compareTo(maxValor) == 1) {
					txt.setText(oldValue);
				} else {
					if (Integer.valueOf(newValue) > 2000000000) {
						txt.setText(oldValue);
					}
				}
				// txt.setText(oldValue);
			}
		});
	}

	public static void setTextFielNumeros(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			txt.setText(txt.getText().replaceAll("[^0-9]", ""));
		});
	}

	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			// Estou mudando a expressão regular de . para ,
			if (newValue != null && !newValue.matches("\\d*([\\,]\\d*)?")
					|| (newValue.length() > 0 && newValue.charAt(0) == ',')) {
				txt.setText(oldValue);
			}
		});
	}
}