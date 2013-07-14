package union.jsbot;

import haven.Coord;
import haven.Inventory;
import haven.Item;
import haven.UI;
import haven.Widget;

import java.util.ArrayList;
import java.util.Comparator;

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

public class JSInventory {
	private int remote_id;

	public JSInventory(int rid) {
		remote_id = rid;
	}

	protected static class ItemQualityComparator implements Comparator<JSItem> {
		int mod = -1;

		@Override
		public int compare(JSItem obj1, JSItem obj2) {
			if (obj1.isActual() && obj2.isActual())
				return mod * (obj1.quality() - obj2.quality());
			else
				return 0;
		}

		public ItemQualityComparator(boolean desc) {
			mod = desc ? 1 : -1;
		}
	}

	protected static class ItemInnerQualityComparator implements
			Comparator<JSItem> {
		int mod = -1;

		@Override
		public int compare(JSItem obj1, JSItem obj2) {
			if (obj1.isActual() && obj2.isActual())
				return mod * (obj1.innerQuality() - obj2.innerQuality());
			else
				return 0;
		}

		public ItemInnerQualityComparator(boolean desc) {
			mod = desc ? 1 : -1;
		}
	}

	protected static class ItemAmountComparator implements Comparator<JSItem> {
		int mod = -1;

		@Override
		public int compare(JSItem obj1, JSItem obj2) {
			if (obj1.isActual() && obj2.isActual())
				return (int) (mod * (obj1.currentAmount() - obj2
						.currentAmount()));
			else
				return 0;
		}

		public ItemAmountComparator(boolean desc) {
			mod = desc ? 1 : -1;
		}
	}

	/**
	 * Возвращает список вещей
	 * 
	 * @param itemmasks
	 *            Перечисление имен вещей которые надо включить в список
	 *            также можно исключить вещь из списка добавив !имявещи
	 * @return массив вещей
	 */
	public JSItem[] getItems(String... itemmasks) {
		ArrayList<JSItem> items = new ArrayList<JSItem>();
		for (Widget i = wdg().child; i != null; i = i.next) {
			if (i instanceof Item) {
				Item buf = (Item) i;
				if (itemmasks.length > 0 && itemmasks[0].equals(""))
					items.add(new JSItem(UI.instance.getId(buf)));
				else {
					for (String iname : itemmasks) {
						boolean exclude = iname.startsWith("!");
						if (buf.GetResName().contains(iname) && !exclude) {
							items.add(new JSItem(UI.instance.getId(buf)));
							break;
						}
					}
				}
			}
		}
		JSItem[] ret = new JSItem[items.size()];
		for (int i = 0; i < items.size(); i++)
			ret[i] = items.get(i);
		return ret;
	}
	
	/**
	 * Возвращает список вещей с полным совпадением по имени ресурса
	 * 
	 * @param itemmasks
	 *            Перечисление имен вещей которые надо включить в список
	 *            также можно исключить вещь из списка добавив !имявещи
	 * @return массив вещей
	 */
	public JSItem[] getEqualItems(String... itemmasks) {
		ArrayList<JSItem> items = new ArrayList<JSItem>();
		for (Widget i = wdg().child; i != null; i = i.next) {
			if (i instanceof Item) {
				Item buf = (Item) i;
				if (itemmasks.length > 0 && itemmasks[0].equals(""))
					items.add(new JSItem(UI.instance.getId(buf)));
				else {
					for (String iname : itemmasks) {
						boolean exclude = iname.startsWith("!");
						if (buf.GetResName().equalsIgnoreCase(iname) && !exclude) {
							items.add(new JSItem(UI.instance.getId(buf)));
							break;
						}
					}
				}
			}
		}
		JSItem[] ret = new JSItem[items.size()];
		for (int i = 0; i < items.size(); i++)
			ret[i] = items.get(i);
		return ret;
	}

	/**
	 * Сортирует массив вещей.
	 * 
	 * @param items
	 *            Массив вещей
	 * @param type
	 *            Тип сортировки. quality - по качеству, iquality -
	 *            внутреннее качество (например качество воды в ведре)
	 * @param desc
	 *            - флаг сортировки. true - по убыванию, false - по
	 *            возрастанию
	 * @return true в случае успеха, иначе false
	 */
	// меняю название функции, т.к. жс сам по себе имеет функцию SORT
	// дабы небыло никакой неведомой хуйни
	public static boolean sortItems(JSItem[] items, String type,
			boolean desc) {
		try {
			if (type.equals("quality"))
				Arrays.sort(items, new ItemQualityComparator(!desc));
			else if (type.equals("iquality"))
				Arrays.sort(items, new ItemInnerQualityComparator(!desc));
			else if (type.equals("amount"))
				Arrays.sort(items, new ItemAmountComparator(!desc));
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Кладет в инвентарь вещь в указанные координаты инвентаря. Кладет то,
	 * что на курсоре.
	 * 
	 * @param c
	 *            Координаты
	 */
	public void drop(Coord c) {
		wdg().wdgmsg("drop", c);
	}

	/**
	 * Перегруженная функция
	 */
	public void drop(int x, int y) {
		drop(new Coord(x, y));
	}

	/**
	 * Возврашает размер инвентаря
	 * 
	 * @return Размер инвентаря
	 */
	public Coord size() {
		return wdg().size();
	}

	/**
	 * Возвращает количество незанятых слотов в инвентаре
	 * 
	 * @return Количество пустых слотов
	 */
	public int freeSlots() {
		int takenSlots = 0;
		for (Widget i = wdg().child; i != null; i = i.next) {
			if (i instanceof Item) {
				Item buf = (Item) i;
				takenSlots += buf.size().x * buf.size().y;
			}
		}
		int allSlots = size().x * size().y;
		return allSlots - takenSlots;
	}

	/**
	 * Возвращает массив координат пустых слотов инвентаря. Слоты как всегда
	 * считаются с верхнего левого угла (0;0).
	 * 
	 * @return массив координат
	 */
	public Coord[] freeSlotsCoords() {
		boolean[][] matrix = new boolean[wdg().size().x][wdg().size().y];
		for (int w = 0; w < wdg().size().x; w++)
			for (int h = 0; h < wdg().size().y; h++)
				matrix[w][h] = false;
		for (Widget i = wdg().child; i != null; i = i.next) {
			if (i instanceof Item) {
				Item tmpItem = (Item) i;
				Coord itemC = tmpItem.coord();
				for (int width = 0; width < tmpItem.size().x; width++)
					for (int height = 0; height < tmpItem.size().y; height++)
						matrix[itemC.x + width][itemC.y + height] = true;
			}
		}
		ArrayList<Coord> list = new ArrayList<Coord>();
		for (int w = 0; w < wdg().size().x; w++)
			for (int h = 0; h < wdg().size().y; h++)
				if (!matrix[w][h])
					list.add(new Coord(w, h));
		Coord[] ret = new Coord[list.size()];
		for (int i = 0; i < list.size(); i++)
			ret[i] = list.get(i);
		return ret;
	}
	
	/**
	 * Возвращает координаты слота инвентаря для предмета с указанным размером в слотах
	 * @param size размер предмета в слотах инвентаря (к примеру ведро 2х2)
	 * @return координаты пустого слота для предмета либо null
	 */
	public Coord freeSlotSizeCoord(Coord size) {
		if (size == null || size.x < 1 || size.y < 1)
			return null;
		if (size.x == 1 && size.y == 1) {
			if (freeSlotsCoords().length > 0)
				return freeSlotsCoords()[0];
			else
				return null;
		}
		
		Coord ret= null;
		Coord[] fslots = freeSlotsCoords();
		for (int i = 0; i < fslots.length; ++i) {
			if (fslots[i].x+1+size.x > size().x || fslots[i].y+1+size.y > size().y)
				continue;
			boolean free = true;
			for (int w = 0; w < size.x; ++w) {
				for (int h = 0; h < size.y; ++h) {
					Coord tmp = fslots[i].add(new Coord(w, h));
					if (!isFreeSlot(tmp)) {
						free = false;
						break;
					}
				}
			}
			if (free) {
				ret = fslots[i];
				return ret;
			}
		}
		return ret;
	}

	/**
	 * Проверяет пустой ли в инвентаре слот с заданными координатами. Слоты
	 * как всегда считаются с верхнего левого угла (0;0).
	 * 
	 * @param slot
	 *            координаты слота
	 * @return true, если слот пустой
	 */
	public boolean isFreeSlot(Coord slot) {
		boolean[][] matrix = new boolean[wdg().size().x][wdg().size().y];
		for (int w = 0; w < wdg().size().x; w++)
			for (int h = 0; h < wdg().size().y; h++)
				matrix[w][h] = false;
		for (Widget i = wdg().child; i != null; i = i.next) {
			if (i instanceof Item) {
				Item tmpItem = (Item) i;
				Coord itemC = tmpItem.coord();
				for (int width = 0; width < tmpItem.size().x; width++)
					for (int height = 0; height < tmpItem.size().y; height++)
						matrix[itemC.x + width][itemC.y + height] = true;
			}
		}
		if (matrix[slot.x][slot.y] == false)
			return true;
		else
			return false;
	}

	/**
	 * Перегруженная функция
	 */
	public boolean isFreeSlot(int x, int y) {
		return isFreeSlot(new Coord(x, y));
	}

	private Inventory wdg() {
		Widget wdg = UI.instance.getWidget(remote_id);
		if (wdg instanceof Inventory) {
			return (Inventory) wdg;
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
