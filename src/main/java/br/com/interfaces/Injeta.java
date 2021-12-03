package br.com.interfaces;

import javafx.scene.Scene;

public interface Injeta {

	public void setaDependencias(Object obj, Scene scene)throws IllegalArgumentException, IllegalAccessException;
	public void setMainScene(Scene scene);
}
