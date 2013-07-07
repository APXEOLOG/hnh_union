package union.jsbot;

import union.JSBotUtils;
import haven.Coord;
import haven.Item;
import haven.UI;
import haven.Widget;

public class JSItem {
	private int remote_id;

	public JSItem(int rid) {
		remote_id = rid;
	}

	/**
	 * Возвращает качество вещи
	 * 
	 * @return качество
	 */
	public int quality() {
		return wdg().quality;
	}
	
	/**
	 * Проверяет предмет на принадлежность к курьезам
	 * @return true если предмет курьез (содержится в curio.conf)
	 */
	public boolean isCurio() {
		return wdg().curio_stat != null;
	}
	
	/**
	 * Возвращает аттеншн курьеза
	 * @return attention или -1
	 */
	public int curioAttention() {
		if (wdg().curio_stat != null)
			return wdg().curio_stat.attention;
		return -1;
	}
	
	/**
	 * Возвращает время изучения курьеза
	 * @return время изучения или -1
	 */
	public int curioStudyTime() {
		if (wdg().curio_stat != null)
			return wdg().curio_stat.studyTime;
		return -1;
	}
	
	/**
	 * Возвращает множитель вещи в зависимости от качества
	 * @return
	 */
	public double multiply() {
		return wdg().qmult;
	}
	
	/**
	 * Возвращает базовое лп курьеза, работает с теми что описаны в curio.conf
	 * @return базовое лп курьеза или -1
	 */
	public double baseCurioLP() {
		if (wdg().curio_stat != null) {
			return wdg().curio_stat.baseLP;
		}
		return -1;
	}
	
	/**
	 * Возвращает лп курьеза зависящее от множителя и текущего ла, работает с теми что описаны в curio.conf
	 * @return текущее лп курьеза или -1
	 */
	public long currentCurioLP() {
		if (wdg().curio_stat != null) {
			return Math.round(wdg().curio_stat.baseLP * wdg().qmult * UI.instance.wnd_char.getExpMode());
		}
		return -1;
	}

	/**
	 * Возвращает имя вещи (полный тултип без имени русерса)
	 * 
	 * @return имя вещи
	 */
	public String name() {
		return wdg().name();
	}

	/**
	 * Возвращает координаты вещи в инвентаре
	 * 
	 * @return координаты вещи
	 */
	public Coord coord() {
		return wdg().c.div(31);
	}

	/**
	 * Возвращает полное имя ресурса вещи
	 * 
	 * @return имя ресурса
	 */
	public String resName() {
		return wdg().GetResName();
	}

	/**
	 * Возвращает результат прогресса вещи
	 * 
	 * @return прогресс
	 */
	public int meter() {
		return wdg().meter;
	}

	/**
	 * Возвращает номер стадии вещи (например у червей шелкопряда)
	 * 
	 * @return стадия
	 */
	public int stage() {
		return wdg().num;
	}

	/**
	 * Возвращает "внутреннее" качество вещи (качество воды в ведре)
	 * 
	 * @return "внутреннее" качество
	 */
	public int innerQuality() {
		return wdg().get_inner_quality();
	}

	/**
	 * Возвращает текущее количество содержимого вещи-контейнера
	 * 
	 * @return количество содержимого
	 */
	public double currentAmount() {
		return wdg().count_of_value;
	}

	/**
	 * Бросает вещь на землю Не работает с вещью на курсоре
	 */
	public void drop() {
		wdg().wdgmsg("drop", JSBotUtils.getCenterScreenCoord());
	}

	/**
	 * Перемещеат вещь в другой инвентарь
	 */
	public void transfer() {
		wdg().wdgmsg("transfer", JSBotUtils.getCenterScreenCoord());
	}

	/**
	 * Берет вещь на курсор
	 */
	public void take() {
		wdg().wdgmsg("take", JSBotUtils.getCenterScreenCoord());
	}

	/**
	 * Взаимодействие вещи с вещью на курсоре (чтото держим в руках и жмем
	 * правой кнопкой мыши на текущую вещь)
	 * 
	 * @param mod
	 *            модификатор клавиатуры
	 */
	public void itemact(int mod) {
		if (!JSBotUtils.isDragging())
			return;
		wdg().wdgmsg("itemact", mod);
	}

	/**
	 * Вызов контекстного меню у вещи Не работает с вещью на курсоре
	 */
	public void iact() {
		wdg().wdgmsg("iact", JSBotUtils.getCenterScreenCoord());
	}

	/**
	 * Возвращает максимальную вместимость вещи (например, 10 литров у
	 * ведра)
	 * 
	 * @return максимальная вместимость
	 */
	public double maxAmount() {
		return wdg().count_of_maximum;
	}

	/**
	 * Перемещает все предметы данного типа в соседний инвентарь.
	 */
	public void transferSuchAll() {
		wdg().wdgmsg("transfer_such_all", wdg().GetResName());
	}

	/**
	 * Выбрасывает из инвентаря все предметы даноого типа.
	 */
	public void dropSuchAll() {
		wdg().wdgmsg("drop_such_all", wdg().GetResName());
	}

	/**
	 * Возвращает размер объекта (в клетках инвентаря)
	 * 
	 * @return Размер объекта, минимум 1*1
	 */
	public Coord size() {
		return wdg().size();
	}

	private Item wdg() {
		Widget wdg = UI.instance.getWidget(remote_id);
		if (wdg instanceof Item) {
			return (Item) wdg;
		} else
			return null;
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