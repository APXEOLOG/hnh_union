package union.jsbot;

import haven.Button;
import union.JSBot;
import union.JSGUI.JSGUI_Widget;

public class JSGUI_Button extends JSGUI_Widget {
	public JSGUI_Button(int lid) {
		super(lid);
	}
	
	/**
	 * Устанавливает текст кнопки
	 * @param text текст
	 */
	public void setText(String text) {
		((Button)wdg()).settext(text);
	}
	
	/**
	 * Ожидание нажатия кнопки
	 * @return true, если кнопка была нажата
	 */
	public boolean waitClick() {
		while (true) {
			if (((Button)wdg()).isChanged()) return true;
			if (!JSBot.Sleep(25)) return false;
		}
	}
}