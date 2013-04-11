package union.jsbot;

import haven.Buff;
import haven.UI;

public class JSBuff {
	private int remote_id;

	public JSBuff(int rid) {
		remote_id = rid;
	}

	/**
	 * Возвращает имя баффа
	 * 
	 * @return имя
	 */
	public String name() {
		return wdg().GetName();
	}

	/**
	 * Возвращает прогресс под баффом (в процентах)
	 * 
	 * @return прогресс
	 */
	public int meter() {
		return wdg().ameter;
	}

	/**
	 * Возвращает время до завершения баффа (0..100)
	 * 
	 * @return время
	 */
	public int time() {
		return wdg().GetTimeLeft();
	}

	private Buff wdg() {
		return UI.instance.sess.glob.buffs.get((Integer) remote_id);
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