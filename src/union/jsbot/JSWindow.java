package union.jsbot;

import haven.Button;
import haven.Inventory;
import haven.Label;
import haven.UI;
import haven.VMeter;
import haven.Widget;
import haven.Window;

import java.util.ArrayList;

public class JSWindow {
	private int remote_id;

	public JSWindow(int rid) {
		remote_id = rid;
	}

	/**
	 * Возвращает массив инвентарей окна. Например в столе 2 инвентаря или в
	 * сталеварке.
	 * 
	 * @return Массив инвентарей
	 */
	public JSInventory[] getInventories() {
		ArrayList<JSInventory> items = new ArrayList<JSInventory>();
		try {
			for (Widget i = wdg().child; i != null; i = i.next) {
				if (i instanceof Inventory) {
					items.add(new JSInventory(UI.instance.getId(i)));
				}
			}
		} catch (Exception ex) {
			// Do nothing
		}
		JSInventory[] ret = new JSInventory[items.size()];
		for (int i = 0; i < items.size(); i++)
			ret[i] = items.get(i);
		return ret;
	}

	/**
	 * Закрывает окно.
	 * 
	 * @return ture, если удалось закрыть окно.
	 */
	public boolean close() {
		try {
			wdg().cbtn.click();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Нажимает кнопку с указанным названием в этом окне
	 * 
	 * @param bname
	 *            Точное название кнопки
	 */
	public void pushButton(String bname) {
		for (Widget i = wdg().child; i != null; i = i.next) {
			if (i instanceof Button) {
				Button b = (Button) i;
				if (b.text.text.equals(bname)) {
					b.click();
					break;
				}
			}
		}
	}

	/**
	 * Это перегруженная функция. Нажимает на кнопку с указанным номером в
	 * порядке создания их в окне. Удобно для кроссроадов, кнопки в нем идут
	 * сверху в низ.
	 * 
	 * @param pos
	 *            номер позиции кнопки в окне, начинаются с 1.
	 */
	public void pushButton(int pos) {
		int current = 0;
		for (Widget i = wdg().child; i != null; i = i.next) {
			if (i instanceof Button) {
				current++;
				if (current == pos) {
					Button b = (Button) i;
					b.click();
					break;
				}
			}
		}
	}

	/**
	 * Возвращает текст лейбла в окне (например в окне с бочкой)
	 * 
	 * @param pos
	 *            позиция лейбла в окне (с единицы)
	 * @return текст лейбла
	 */
	public String getLabelText(int pos) {
		if (pos < 1)
			pos = 1;
		int labelPos = 1;
		for (Widget i = wdg().child; i != null; i = i.next) {
			if (i instanceof Label) {
				if (labelPos == pos) {
					Label l = (Label) i;
					return l.text.text;
				}// pos==
				else {
					labelPos++;
					continue;
				}// else
			}// inst
		}
		return "";
	}

	/**
	 * Возвращает значение количества ресурсов в показателе (измерителе или
	 * хуй знает в чем)
	 * 
	 * @param pos
	 *            позиция измерителя в окне
	 * @return значение измерителя
	 */
	public int getMeterValue(int pos) {
		if (pos < 1)
			pos = 1;
		int meterPos = 1;
		for (Widget i = wdg().child; i != null; i = i.next) {
			if (i instanceof VMeter) {
				if (meterPos == pos) {
					VMeter v = (VMeter) i;
					return v.amount;
				}// pos==
				else {
					meterPos++;
					continue;
				}// else
			}// inst
		}
		return 0;
	}

	private Window wdg() {
		Widget wdg = UI.instance.getWidget(remote_id);
		if (wdg instanceof Window) {
			return (Window) wdg;
		} else {
			return null;
		}
	}

	/**
	 * Проверяет, существует ли еще объект
	 * 
	 * @return true если объект существует
	 */
	public boolean isActual() {
		return wdg() != null;
	}
}