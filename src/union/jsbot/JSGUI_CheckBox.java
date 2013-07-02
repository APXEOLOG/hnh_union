package union.jsbot;

import union.JSGUI.JSGUI_Widget;
import haven.CheckBox;

public class JSGUI_CheckBox extends JSGUI_Widget {
	public JSGUI_CheckBox(int lid) {
		super(lid);
	}

	/**
	 * Установить положение чекбокса
	 * @param b выбрано/не выбрано (true/false)
	 */
	public void setChecked(boolean b) {
		((CheckBox)wdg()).a = b;
	}

	/**
	 * Проверить, установлен ли флаг
	 * @return true, если установлен
	 */
	public boolean isChecked() {
		return ((CheckBox)wdg()).a;
	}

	/**
	 * Установить текст чекбокса
	 * @param text текст
	 */
	public void setText(String text) {
		((CheckBox)wdg()).setText(text);
	}
}