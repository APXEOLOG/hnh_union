package union.jsbot;

import haven.Label;
import union.JSGUI.JSGUI_Widget;

public class JSGUI_Label extends JSGUI_Widget {
	public JSGUI_Label(int lid) {
		super(lid);
	}
	
	/**
	 * Установить текст лейбла
	 * @param text текст
	 */
	public void setText(String text) {
		((Label)wdg()).settext(text);
	}
	
	/**
	 * Возвращает текст лейбла
	 * @return текст лейбла
	 */
	public String getText() {
		return ((Label)wdg()).text.text;
	}
}
