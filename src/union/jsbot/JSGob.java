package union.jsbot;

import union.JSBotUtils;
import haven.*;

public class JSGob {
	private int gob_id;
	
	public JSGob(int id) {
		gob_id = id;
	}
	
	/**
	 * Проверяет, является ли объект персонажем
	 * @return true если объект - персонаж
	 */
	public boolean isPlayer() {
		return gob().isPlayer();
	}
	
	/**
	 * Проверяет, находится ли объект в пати с вами
	 * @return true если объект в пати с вами
	 */
	public boolean isInParty() {
		synchronized (JSBotUtils.glob.party.memb) {
			for (Party.Member m : JSBotUtils.glob.party.memb.values()) {
				if (m.gobid == gob_id)
					return true;
			}
			return false;
		}
	}

	/**
	 * Проверяет, является ли объект окрашенным в определенный цвет в кинах
	 * @param group номер группы в кинах (начинается с 0)
	 * @return true если объект в кинах и окрашен заданным цветом
	 */
	public boolean isGroupKin(int group) {
		return gob().isGroupKin(group);
	}
	
	/**
	 * Проверяет, находится ли объект в кинах
	 * @return true - если находится
	 */
	public boolean isKin() {
		return gob().isKin();
	}
	
	/**
	 * Проверяет, состоит ли объект с вами в одной деревне
	 * @return true - если состоит
	 */
	public boolean isInYourVillage() {
		return gob().isInYourVillage();
	}
	
	/**
	 * Возвращает размер хитбокса, если он есть
	 * @return переменная типа Coord, содержащяа размеры хитбокса
	 */
	public Coord negSize() {
		if (gob().getneg() != null)
			return gob().getneg().bs;
		return new Coord(0, 0);
	}
	
	/**
	 * Возвращает группу в которую окрашен данный объект в кинах
	 * @return -1, если не в кинах, иначе номер группы (с 0)
	 */
	public int getKinGroup() {
		return gob().getKinGroup();
	}
	
	/**
	 * Возвращает тип кина для данного объекта (я хз что это, но в ней содержится флаг деревни)
	 * @return Тип кина
	 */
	public int getKinType() {
		return gob().getKinType();
	}
	
	/**
	 * Возвращает идентификатор объекта
	 * @return
	 */
	public int getID() {
		return gob_id;
	}
	
	/**
	 * Возвращает здоровье объекта (в процентах)
	 * @return
	 */
	public int health() {
		return gob().getHealth();
	}
	
	/**
	 * Возвращает значение из блоба по заданному индексу
	 * @param index Индекс
	 * @return Значение блоба
	 */
	public int blob(int index) {
		return gob().GetBlob(index);
	}
	
	/**
	 * Возвращает весь блоб целиком (в виде массива)
	 * @return Блоб
	 */
	public int[] blobAll() {
		return gob().getBlob();
	}
	
	/**
	 * Проверяет, существует ли еще объект
	 * @return true если объект существует
	 */
	public boolean isActual() {
		return gob() != null;
	}
	
	/**
	 * Возвращает абсолютные координаты объекта
	 * @return Абсолютные координаты объекта
	 */
	public Coord position() {
		return gob().position();
	}
	
	/**
	 * Проверяет наличие подстроки в слоях ресурсов
	 * @param layer подстрока ресурса
	 * @return true, если один из слоев содержит указанную подстроку
	 */
	public boolean hasLayer(String layer) {
		String[] lrs = gob().resnames();
		for(int i = 0; i < lrs.length; i++)
			if(lrs[i].contains(layer)) return true;
		return false;
	}
	
	/**
	 * Клик по объекту
	 * @param btn кнопка мыши (1 - LMB, 3 - RMB)
	 * @param mod модификатор клавиатуры (1 - SHIFT; 2 - CTRL; 4 - ALT; 8 - WIN)
	 */
	public void doClick(int btn, int mod) {
		if(!isActual()) return;
		if (UI.instance.mapview != null) {
			Coord sz = UI.instance.mapview.sz;
			Coord sc = new Coord((int) Math.round(Math.random() * 200 + sz.x / 2
					- 100), (int) Math.round(Math.random() * 200 + sz.y / 2
					- 100));
			Coord oc = position();
			UI.instance.mapview.wdgmsg("click", sc, oc, btn, mod, gob_id,
					oc);
		}
	}
	
	/**
	 * Клик в указанном оффсете от объекта (в точках карты)
	 * @param offset оффсет
	 */
	public void offsetMove(Coord offset) {
		if(!isActual()) return;
		if(UI.instance.mapview != null) {
			Coord sz = UI.instance.mapview.sz;
			Coord sc = new Coord((int) Math.round(Math.random() * 200 + sz.x / 2 - 100),
					(int) Math.round(Math.random() * 200 + sz.y / 2 - 100));
			Coord oc = position().add(offset);
			UI.instance.mapview.wdgmsg("click", sc, oc, 1, 0, gob_id, oc);
		}
	}
	
	/**
	 * Взаимодействие данного гоба с предметом в руках (на курсоре) персонажа
	 * @param mod модификатор клавиатуры (1 - SHIFT; 2 - CTRL; 4 - ALT; 8 - WIN)
	 */
	public void interactClick(int mod) {
		if(!isActual()) return;
		if (UI.instance.mapview != null) {
			UI.instance.mainview.wdgmsg("itemact", JSBotUtils.getCenterScreenCoord(), position(),
					mod, gob_id, position());
		}
	}
	
	/**
	 * Возвращает полное имя ресурса объекта
	 * @return Имя ресурса объекта
	 */
	public String name() {
		return gob().resname();
	}
	
	/**
	 * Проверяет сидит ли объект
	 * @return
	 */
	public boolean isSitting() {
		Layered layered = gob().getattr(Layered.class);
		if (layered != null) {
			return layered.containsLayerName("gfx/borka/body/sitting/");
		}
		return false;
	}
	
	/**
	 * Проверяет держит ли объект что-либо в руках (над головой)
	 * @return
	 */
	public boolean isCarrying() {
		Layered layered = gob().getattr(Layered.class);
		if (layered != null) {
			return layered.containsLayerName("gfx/borka/body/standing/arm/banzai/") ||
					layered.containsLayerName("gfx/borka/body/walking/arm/banzai/");
		}
		return false;
	}
	
	/**
	 * Проверяет движется ли объект
	 * @return
	 */
	public boolean isMoving() {
		Moving m = gob().getattr(Moving.class);
		if(m == null)
			return false;
		else return true;
	}
	
	/**
	 * Проверяет лежит ли объект
	 * @return
	 */
	public boolean isLaying() {
		Layered layered = gob().getattr(Layered.class);
		if (layered != null) {
			return layered.containsLayerName("gfx/borka/body/dead/");
		}
		return false;
	}
	
	/**
	 * Проверяет подсвечен ли объект
	 * @return
	 */
	public boolean isOverlayed() {
		return gob().getDrawOlay();
	}
	
	/**
	 * Включает/выключает подсвечивание объектов
	 * @param b
	 */
	public void setOverlay(boolean b) {
		gob().setDrawOlay(b);
	}
	
	private Gob gob() {
		synchronized (JSBotUtils.glob.oc) {
			return JSBotUtils.glob.oc.getgob(gob_id);
		}
	}
}
