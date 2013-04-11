package union.jsbot;

import haven.Coord;
import haven.Inventory;
import haven.UI;

public class JSEquip {
	public JSEquip() {
	}

	/**
	 * Возвращает количество пустых слотов эквипа
	 * 
	 * @return количество пустых слотов
	 */
	public int emptyCount() {
		int count = 0;
		if (UI.instance.equip.equed != null) {
			for (int i = 0; i < UI.instance.equip.equed.size(); i++)
				if (UI.instance.equip.equed.get(i) != null)
					count++;
		}
		return (16 - count);
	}

	/**
	 * Возвращает качество вещи в указанном слоте эквипа
	 * 
	 * @param slot
	 *            номер слота
	 * @return качество вещи
	 */
	public int quality(int slot) {
		if (slot < 0)
			slot = 0;
		if (slot > 15)
			slot = 15;
		int q = -1;
		if (UI.instance.equip.equed != null)
			if (UI.instance.equip.equed.get(slot) != null)
				q = UI.instance.equip.equed.get(slot).quality;
		return q;
	}

	/**
	 * Возвращает полное имя ресурса
	 * 
	 * @param slot
	 *            слот эквипа
	 * @return имя ресурса
	 */
	public String resName(int slot) {
		if (slot < 0)
			slot = 0;
		if (slot > 15)
			slot = 15;
		String n = "";
		if (UI.instance.equip.equed != null)
			if (UI.instance.equip.equed.get(slot) != null)
				n = UI.instance.equip.equed.get(slot).GetResName();
		return n;
	}

	/**
	 * Возвращает имя вещи эквипа
	 * 
	 * @param slot
	 *            слот эквипа
	 * @return имя вещи (как в подсказке, без качества)
	 */
	public String name(int slot) {
		if (slot < 0)
			slot = 0;
		if (slot > 15)
			slot = 15;
		String n = "";
		if (UI.instance.equip.equed != null)
			if (UI.instance.equip.equed.get(slot) != null)
				n = UI.instance.equip.equed.get(slot).name();
		return n;
	}

	/**
	 * Бросить вещь в указанный слот
	 * 
	 * @param slot
	 *            слот, в который нужно бросить вещь
	 */
	public void dropTo(int slot) {
		if (slot < 0)
			slot = 0;
		if (slot > 15)
			slot = 15;
		if (UI.instance.equip.epoints != null)
			if (UI.instance.equip.epoints.get(slot) != null) {
				Inventory i = UI.instance.equip.epoints.get(slot);
				Coord c = new Coord(0, 0);
				i.wdgmsg("drop", c);
			}
	}

	/**
	 * Взять вещь из указанного слота
	 * 
	 * @param slot
	 *            номер слота
	 */
	public void takeAt(int slot) {
		itemAction(slot, "take");
	}

	/**
	 * Взаимодействие вещи на курсоре с вещью эквипа
	 * 
	 * @param slot
	 *            слот вещи, с которой хотим взаимодействовать
	 */
	public void itemact(int slot) {
		itemAction(slot, "itemact");
	}

	/**
	 * Переместить вещь из эквипа в активный инвентарь
	 * 
	 * @param slot
	 *            номер слота
	 */
	public void transfer(int slot) {
		itemAction(slot, "transfer");
	}

	/**
	 * Вызвать контекстное меню вещи
	 * 
	 * @param slot
	 *            номер слота
	 */
	public void iact(int slot) {
		itemAction(slot, "iact");
	}

	private void itemAction(int slot, String act) {
		if (slot < 0)
			slot = 0;
		if (slot > 15)
			slot = 15;
		Coord c = new Coord(0, 0);
		if (UI.instance.equip.equed != null)
			if (UI.instance.equip.equed.get(slot) != null) {
				if (act.equals("itemact")) {
					UI.instance.equip.wdgmsg("itemact", slot);
				} else
					UI.instance.equip.wdgmsg(act, slot, c);
			}
	}
}
