package union.jsbot;
/*
import static haven.MCache.tileSize;
import haven.Coord;
import haven.UI;
import union.APXUtils;
import union.JSBotUtils;
*/
public class JSMap {
	public JSMap() {
	}
//
//	/**
//	 * Возвращает массив идентификаторов объектов в указанном радиусе с
//	 * оффсетом от игрока (в тайлах)
//	 * 
//	 * @param rad
//	 *            радиус поиска объектов (в точках)
//	 * @param offset
//	 *            оффсет от игрока (в тайлах)
//	 * @param mask
//	 *            перечисление имен необходимых объектов (подстрока ресурса,
//	 *            "!" в наччале исключает объект из поиска)
//	 * @return массив идентификаторов
//	 */
//	public int[] getObjects(int rad, Coord offset, String... mask) {
//		return JSBotUtils.objectIdList(rad, offset, mask);
//	}
//
//	/**
//	 * Возвращает значение из BLOB по указанному индексу
//	 * 
//	 * @param id
//	 *            идентификатор объекта
//	 * @param index
//	 *            номер индекса в BLOB
//	 * @return значение по указанному индексу
//	 */
//	public int objectBLOB(int id, int index) {
//		return JSBotUtils.getObjectBlob(id, index);
//	}
//
//	/**
//	 * Возвращает тип тайла в оффсете от игрока (в тайлах)
//	 * 
//	 * @param offset
//	 *            оффсет в тайлах
//	 * @return тип тайла, -1 если не удалось получить тип тайла
//	 */
//	public int getTileType(Coord offset) {
//		return JSBotUtils.tileType(offset.x, offset.y);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public int getTileType(int x, int y) {
//		return JSBotUtils.tileType(x, y);
//	}
//
//	/**
//	 * Отправляет на сервер щелчек мыши по объекту
//	 * 
//	 * @param objid
//	 *            идентификатор объекта
//	 * @param btn
//	 *            кнопка мыши (1 - левая, 3 - правая)
//	 * @param mod
//	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
//	 *            win)
//	 */
//	public void doClick(int objid, int btn, int mod) {
//		JSBotUtils.doClick(objid, btn, mod);
//	}
//
//	/**
//	 * Двигаться на PF к указанной точке, координаты задавать абсолютные
//	 * 
//	 * @param point
//	 *            координаты точки назначения
//	 * @return количество кусков пути. 0, если путь не найден
//	 */
//	public int PFMove(Coord point) {
//		return UI.instance.mapview.map_pf_move(point);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public int PFMove(int x, int y) {
//		return UI.instance.mapview.map_pf_move(new Coord(x, y));
//	}
//
//	/**
//	 * Кликнуть по объекту правой кнопкой мыши, используя PF
//	 * 
//	 * @param id
//	 *            идентификатор объекта
//	 */
//	public int PFClick(int id) {
//		return UI.instance.mapview.map_pf_interact(id);
//	}
//
//	/**
//	 * Отправляет на сервер щелчек по карте в указанные координаты
//	 * относительно игрока (в тайлах)
//	 * 
//	 * @param c
//	 *            координаты
//	 * @param btn
//	 *            кнопка мыши (1 - левая, 3 - правая)
//	 * @param mod
//	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
//	 *            win)
//	 */
//	public void offsetClick(Coord c, int btn, int mod) {
//		JSBotUtils.mapClick(c.x, c.y, btn, mod);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public void offsetClick(int x, int y, int btn, int mod) {
//		JSBotUtils.mapClick(x, y, btn, mod);
//	}
//
//	/**
//	 * Отправляет на сервер щелчек по карте в указанные координаты
//	 * (абсолютные, в точках карты)
//	 * 
//	 * @param c
//	 *            координаты
//	 * @param btn
//	 *            кнопка мыши (1 - левая, 3 - правая)
//	 * @param mod
//	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
//	 *            win)
//	 */
//	public void absClick(Coord c, int btn, int mod) {
//		JSBotUtils.mapAbsClick(c.x, c.y, btn, mod);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public void absClick(int x, int y, int btn, int mod) {
//		JSBotUtils.mapAbsClick(x, y, btn, mod);
//	}
//
//	/**
//	 * Передвигается на указанное количество тайлов относительно игрока
//	 * 
//	 * @param c
//	 *            координаты передвижения
//	 */
//	public void moveStep(Coord c) {
//		JSBotUtils.mapMoveStep(c.x, c.y);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public void moveStep(int x, int y) {
//		JSBotUtils.mapMoveStep(x, y);
//	}
//
//	/**
//	 * Передвигается к указанному объекту, с оффсетом
//	 * 
//	 * @param objid
//	 *            объект
//	 * @param c
//	 *            координаты оффсета в точках карты
//	 */
//	public void objectOffsetMove(int objid, Coord c) {
//		JSBotUtils.mapMove(objid, c.x, c.y);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public void objectOffsetMove(int objid, int x, int y) {
//		JSBotUtils.mapMove(objid, x, y);
//	}
//
//	/**
//	 * Возвращает координаты игрока
//	 * 
//	 * @return координаты игрока
//	 */
//	public Coord myCoords() {
//		return JSBotUtils.MyCoord();
//	}
//
//	/**
//	 * Взаимодествие предмета в руках (на курсоре) с точкой на карте (в
//	 * тайлах) относительно игрока
//	 * 
//	 * @param c
//	 *            координаты оффсета
//	 * @param mod
//	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
//	 *            win)
//	 */
//	public void interactClick(Coord c, int mod) {
//		JSBotUtils.mapInteractClick(c.x, c.y, mod);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public void interactClick(int x, int y, int mod) {
//		JSBotUtils.mapInteractClick(x, y, mod);
//	}
//
//	/**
//	 * Взаимодествие предмета в руках с точкой на карте (абсолютные, в
//	 * точках карты)
//	 * 
//	 * @param c
//	 *            абсолютные координаты
//	 * @param mod
//	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
//	 *            win)
//	 */
//	public void absInteractClick(Coord c, int mod) {
//		JSBotUtils.mapAbsInteractClick(c.x, c.y, mod);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public void absInteractClick(int x, int y, int mod) {
//		JSBotUtils.mapAbsInteractClick(x, y, mod);
//	}
//
//	/**
//	 * Взаимодействие предмета в руках с объектом на карте
//	 * 
//	 * @param objid
//	 *            объект
//	 * @param mod
//	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
//	 *            win)
//	 */
//	public void objectInteractClick(int objid, int mod) {
//		JSBotUtils.mapInteractClick(objid, mod);
//	}
//
//	/**
//	 * Поставить объект который хотим построить в указанные координаты (в
//	 * тайлах) относительно игрока
//	 * 
//	 * @param c
//	 *            координаты оффсета от игрока
//	 * @param btn
//	 *            кнопка мыши (1 - левая, 3 - правая)
//	 * @param mod
//	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
//	 *            win)
//	 */
//	public void place(Coord c, int btn, int mod) {
//		JSBotUtils.mapPlace(c.x, c.y, btn, mod);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public void place(int x, int y, int btn, int mod) {
//		JSBotUtils.mapPlace(x, y, btn, mod);
//	}
//
//	/**
//	 * Возвращает полное имя ресурса объекта
//	 * 
//	 * @param oid
//	 *            идентификатор объекта
//	 * @return имя ресурса
//	 */
//	public String objectName(int oid) {
//		return JSBotUtils.objectResName(oid);
//	}
//
//	/**
//	 * Возвращает координаты объекта
//	 * 
//	 * @param oid
//	 *            идентификатор объекта
//	 * @return координаты объекта
//	 */
//	public Coord objectPos(int oid) {
//		return JSBotUtils.objCoords(oid);
//	}
//
//	/**
//	 * Проверяет, является ли указанный объект игроком
//	 * 
//	 * @param oid
//	 *            идентификатор объекта
//	 * @return true - если объект - игрок
//	 */
//	public boolean objectIsAvatar(int oid) {
//		return JSBotUtils.objIsPlayer(oid);
//	}
//
//	/**
//	 * Проверяет, несет ли аватар что-то в руках
//	 * 
//	 * @param oid
//	 *            идентификатор объекта
//	 * @return true - если аватар несет что-то в руках
//	 */
//	public boolean avatarIsCarrying(int oid) {
//		return JSBotUtils.objIsCarrying(oid);
//	}
//
//	/**
//	 * Проверяет, сидит ли аватар
//	 * 
//	 * @param oid
//	 *            идентификатор объекта
//	 * @return true - если аватар сидит
//	 */
//	public boolean avatarIsSitting(int oid) {
//		return JSBotUtils.objIsSitting(oid);
//	}
//
//	/**
//	 * Проверяет можно ли дойти до указанной точки напрямую
//	 * 
//	 * @param rc
//	 *            Абсолютные координаты точки
//	 * @return true если можно пройти напрямую
//	 */
//	public boolean isPathFree(Coord rc) {
//		return APXUtils.isPathFree(rc);
//	}
//
//	/**
//	 * 'Просит' игрока выбрать объект мышкой, пользователь должен щелкнуть
//	 * на любой объект, тогда управление вернется в скрипт. По сути скрипт
//	 * не продолжит выполнение, пока пользователь не выберет объект.
//	 * 
//	 * @param text
//	 *            сообщение в игре (как в inGamePrint объекта JSHaven)
//	 * @return идентификатор объекта
//	 */
//	public int selectObject(String text) {
//		if (UI.instance.mapview == null)
//			return 0;
//		JSBotUtils.slenPrint(text);
//		UI.instance.mapview.objectSelecting = true;
//		while (UI.instance.mapview.objectSelecting)
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				// e.printStackTrace();
//			}
//		// JSBot.Sleep(200);
//		if (UI.instance.mapview.objectUnderMouse != null) {
//			return UI.instance.mapview.objectUnderMouse.id;
//		}
//		return 0;
//	}
//
//	/**
//	 * Возвращает идентификатор объекта с указанным именем ресурса в
//	 * указанном радиусе (тайлы) от игрока
//	 * 
//	 * @param name
//	 *            имя ресурса
//	 * @param rad
//	 *            радиус от игрока (в тайлах)
//	 * @return идентификатор объекта
//	 */
//	public int findObjectByName(String name, int rad) {
//		return JSBotUtils.findObjectByName(name, rad);
//	}
//
//	/**
//	 * Находит объект в указанном радиусе с оффсетом от игрока
//	 * 
//	 * @param name
//	 *            имя ресурса
//	 * @param rad
//	 *            радиус поиска (в точках карты)
//	 * @param c
//	 *            координаты оффсета от игрока
//	 * @return идентификатор первого найденного объекта
//	 */
//	public int findObjectWithOffset(String name, int rad, Coord c) {
//		return JSBotUtils.findMapObject(name, rad, c.x, c.y);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public int findObjectWithOffset(String name, int rad, int x, int y) {
//		return JSBotUtils.findMapObject(name, rad, x, y);
//	}
//
//	/**
//	 * Возвращает координаты 'центрирования' по тайлу
//	 * 
//	 * @param c
//	 *            передаваемые координаты
//	 * @return 'центрированный тайл'
//	 */
//	public Coord tilify(Coord c) {
//		c = c.div(tileSize);
//		c = c.mul(tileSize);
//		c = c.add(tileSize.div(2));
//		return (c);
//	}
//
//	/**
//	 * Перегруженная функция
//	 */
//	public Coord tilify(int x, int y) {
//		return tilify(new Coord(x, y));
//	}
//
//	/**
//	 * Возвращает координаты оффсета в тайлах ближайшего тайла с указанным
//	 * типом
//	 * 
//	 * @param raduis
//	 *            радиус поиска тайла (в тайлах)
//	 * @param type
//	 *            тип тайла
//	 * @return координаты оффсета от игрока в тайлах (P.S.: масло масло
//	 *         масло.)
//	 */
//	public Coord getNearestTileCoord(int raduis, int type) {
//		double maxRealRad = 100000;
//		Coord ret = null;
//		for (int step = 1; step < raduis; step++) {
//			for (int i = 0; i < step; i++) {
//				int x, y;
//				x = i - step;
//				y = i;
//				if (JSBotUtils.tileType(x, y) == type) {
//					double realRad = Math.sqrt(x * x + y * y); // чтобы ты
//																// сдох со
//																// своей
//																// явой, Арх
//																// NOOOOO
//																// Kerri я
//																// нихотеть
//																// -_-
//					if (realRad < maxRealRad) {
//						maxRealRad = realRad;
//						ret = new Coord(x, y);
//						raduis += 2;
//						continue;
//					}
//				}
//				x = i;
//				y = step - i;
//				if (JSBotUtils.tileType(x, y) == type) {
//					double realRad = Math.sqrt(x * x + y * y);
//					if (realRad < maxRealRad) {
//						maxRealRad = realRad;
//						ret = new Coord(x, y);
//						raduis += 2;
//						continue;
//					}
//				}
//				x = step - i;
//				y = -i;
//				if (JSBotUtils.tileType(x, y) == type) {
//					double realRad = Math.sqrt(x * x + y * y);
//					if (realRad < maxRealRad) {
//						maxRealRad = realRad;
//						ret = new Coord(x, y);
//						raduis += 2;
//						continue;
//					}
//				}
//				x = -i;
//				y = i - step;
//				if (JSBotUtils.tileType(x, y) == type) {
//					double realRad = Math.sqrt(x * x + y * y);
//					if (realRad < maxRealRad) {
//						maxRealRad = realRad;
//						ret = new Coord(x, y);
//						raduis += 2;
//						continue;
//					}
//				}
//			}
//		}
//		return ret;
//	}
}