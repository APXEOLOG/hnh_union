package union.jsbot;

import haven.TextEntry;
import union.JSGUI.JSGUI_Widget;

public class JSGUI_TextEntry extends JSGUI_Widget {
	public JSGUI_TextEntry(int lid) {
		super(lid);
	}
	
	/**
	 * Установить текст в поле ввода
	 * @param text текст
	 */
	public void setText(String text) {
		((TextEntry)wdg()).text = text;
	}
	
	/**
	 * Получить текст из поля ввода
	 * @return текст
	 */
	public String getText() {
		return ((TextEntry)wdg()).text;
	}
	
	/**
	 * Получить целочисленное значение из поля ввода, если значение не удается перевести в целочисленное, то
	 * функция вернет 0
	 * @return целочисленное значение
	 */
	public int getInt() {
		String textv = ((TextEntry)wdg()).text;
		try{
			Integer ival = Integer.parseInt(textv);
			return ival.intValue();
		}
		catch(NumberFormatException e){
			return 0;
		}
	}
}