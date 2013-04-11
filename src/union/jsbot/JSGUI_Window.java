package union.jsbot;

import haven.Button;
import haven.Widget;
import haven.Window;
import union.JSBot;
import union.JSGUI.JSGUI_Widget;

public class JSGUI_Window extends JSGUI_Widget {
	public JSGUI_Window(int lid) {
		super(lid);
	}
	
	/**
	 * Показать/скрыть кнопку "закрыть" у окна
	 */
	public void toggleCloseButton() {
		((Window)wdg()).cbtn.visible = !((Window)wdg()).cbtn.visible;
	}
	
	/**
	 * Закрыть окно
	 */
	public void close() {
		((Window)wdg()).cbtn.click();
	}
	
	/**
	 * Ждет нажатия кнопки в окне
	 * @return текст нажатой кнопки
	 * Других идей у меня небыло
	 */
	public String waitButtonClick() {
		while(true){
			for(Widget i = wdg().child; i != null; i = i.next) {
				if(i instanceof Button){
					Button b = (Button) i;
					if(b.isChanged()) return b.text.text;
				}
			}
			if (!JSBot.Sleep(25)) break;
		}
		return "";
	}
}