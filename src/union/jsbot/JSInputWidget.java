package union.jsbot;

import haven.Button;
import haven.Coord;
import haven.Label;
import haven.TextEntry;
import haven.UI;
import haven.Widget;
import haven.Window;

public class JSInputWidget extends Window {

		private boolean okClicked = false;
		private TextEntry lineEdit;

		/**
		 * Конструктор JSInputWidget. Его трогать не нужно. Здесь будет описана
		 * краткая работа с ним.
		 * 
		 * Пример: var iw = haven.getInputWidget(250, 250, "Input smth",
		 * "Label text"); var ID = 0; if(iw != null) {
		 * if(iw.waitForPress(20000)) { ID = iw.intValue(); iw.close(); } else
		 * iw.close(); } Данный пример показывает как в переменную ID считать
		 * числовое значение. При нажатии на кнопку "Ок" в окне, оно не
		 * закрывается, а скрывается, поэтому, хорошим тоном считается закрытие
		 * окна (с помощью метода close()), когда оно уже не нужно.
		 */
		public JSInputWidget(Coord c, String header, String label) {
			super(c, Coord.z, UI.instance.root, header);
			cbtn.visible = false;
			new Label(Coord.z, this, label);
			lineEdit = new TextEntry(new Coord(0, 20), new Coord(180, 20),
					this, "");
			new Button(new Coord(185, 22), 40, this, "Ok") {
				public void click() {
					okClicked = true;
					hide();
				}
			};
			pack();
		}

		/**
		 * Ожидание нажатия кнопки "Ок"
		 * 
		 * @param timeout
		 *            время ожидания
		 * @return true, если за указанный период времени была нажата кнопка
		 *         "Ок"
		 */
		public boolean waitForPress(int timeout) {
			if (timeout == 0)
				timeout = 0;
			int curr = 0;
			while (!okClicked) {
				if (curr > timeout)
					return false;
				curr += 25;
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return true;
		}

		/**
		 * Закрывает окно
		 */
		public void close() {
			ui.destroy(this);
		}

		/**
		 * Возвращает текст из поля ввода
		 * 
		 * @return текст, либо "", если кнопка "Ок" еще не была нажата
		 */
		public String textValue() {
			if (okClicked)
				return lineEdit.text;
			return "";
		}

		/**
		 * Возвращает числовое значение из поля ввода
		 * 
		 * @return числовое значение, либо 0, если в поле ввода хуита
		 */
		public int intValue() {
			if (okClicked) {
				try {
					Integer ival = Integer.parseInt(lineEdit.text);
					return ival.intValue();
				} catch (NumberFormatException e) {
					return 0;
				}
			}
			return 0;
		}

		/**
		 * Метод, который не надо трогать
		 */
		public void destroy() {
			super.destroy();
		}

		/**
		 * Метод, который не надо трогать
		 */
		public void wdgmsg(Widget sender, String msg, Object... args) {
			if (sender == cbtn) {
				close();
				return;
			}
			super.wdgmsg(sender, msg, args);
		}

		/**
		 * Метод, который не надо трогать
		 */
		public boolean type(char key, java.awt.event.KeyEvent ev) {
			if (key == 27) {
				close();
			}
			if (key == 10) {
				// позволяет "нажать" кнопку Ok нажав клавишу Enter
				okClicked = true;
				hide();
			}
			return (super.type(key, ev));
		}
	}