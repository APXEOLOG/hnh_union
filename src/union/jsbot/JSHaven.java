package union.jsbot;

import haven.Audio;
import haven.BuddyWnd;
import haven.Charlist;
import haven.Coord;
import haven.LoginScreen;
import haven.MainFrame;
import haven.Music;
import haven.Partyview;
import haven.Resource;
import haven.UI;
import haven.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import union.APXUtils;
import union.JSBot;
import union.JSBotUtils;
import union.JSGUI;
import union.JSThread;

public class JSHaven {
	private static Coord unWrapCoord(Object obj) {
		if (obj instanceof org.mozilla.javascript.Wrapper) {
			Object temp = ((org.mozilla.javascript.Wrapper)obj).unwrap();
			if (temp instanceof Coord)
				return (Coord) temp;
		}
		return Coord.z;
	}
	
	private static JSGob unWrapGob(Object obj) {
		if (obj instanceof org.mozilla.javascript.Wrapper) {
			Object temp = ((org.mozilla.javascript.Wrapper)obj).unwrap();
			if (temp instanceof JSGob)
				return (JSGob) temp;
		}
		return null;
	}
	
	private static String[] unWrapStringArray(Object obj) {
		if (obj instanceof String) 
			return new String[] { (String) obj };
		if (obj instanceof org.mozilla.javascript.NativeArray) {
			String[] temp = (String[]) ((org.mozilla.javascript.NativeArray)obj).toArray(new String[((org.mozilla.javascript.NativeArray)obj).size()]);
			return temp;
		}
		return new String[0];
	}
	
	
	/**
	 * Подгружает файл в скрипт.
	 * @param filename имя файла без расширения, сам файл должен иметь расширение .japi и лежать в папке scripts
	 * @return	true если инклюд прошел успешно, иначе false
	 * @since 7.1
	 */
	public static boolean include(String filename) {
		if (!(Thread.currentThread() instanceof JSThread)) return false;
		JSThread currentThread = (JSThread) Thread.currentThread();
		File japi = new File("scripts", filename + ".japi");
		
		try {
			FileReader freader = new FileReader(japi);
			BufferedReader reader = new BufferedReader(freader);
			StringBuilder builder = new StringBuilder();
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				builder.append(buffer);
				builder.append('\n');
			}
			reader.close();
			freader.close();
			currentThread.jsContext.evaluateString(currentThread.jsScope, builder.toString(), filename, 1, null);
		} catch (Exception e) {
			JSBot.JSError(e);
			return false;
		}
		return true;
	}
	
	/*******************************************************************
	 * Рака яичек всем, я угощаю!
	 * 
	 * Ниже следует раковая копипаста предыдущей хуйни с еба префиксом.
	 * Теперь в скриптах можно будет творить просто лютый пиздец,
	 * запутывая противников и себя в говнокоде на ЖС.
	 * 
	 * Зато с версии 7.1 мы начнем уважать пользователей (наверное) и
	 * теперь у нас одновременно будет поддержка старых и новых скриптов,
	 * пока кого-нибудь это не заебет.
	 *******************************************************************
	 * Напутственное слово от Арха!
	 * Да здравствует стильный и молодежный j-префикс! 
	 * Свершилось то, чего вы так давно ждали, больше никаких хавен..... и мап.....
	 * Теперь наш бот будет самым охуенным ботом в хахачике!
	 * � не важно что в яве половина функций будет обернута через хуйпизду и
	 * тупой рино не поддерживает недефолтные аргументы, чтоб он сдох!!11
	 * � вообще педикам молчать и кодить в тряпочкэ!
	 * 
	 * *******************************************************************
	 * Попизди мне тут!
	 **/
	
	/**
	 * Возвращает переменную типа Coord с заданными координатами
	 * @param x координата x
	 * @param y координата y
	 * @return переменная типа Coord
	 */
	public static Coord jCoord(int x, int y){
		return new Coord(x, y);
	}

	/**
	 * Функция создает окно "ввода". �спользуется для ввода текстового или числового значения.
	 * @param x координата x для отрисовки окна на экране
	 * @param y координата y для отрисовки окна на экране
	 * @param header заголовок окна
	 * @param label текст лейбла над полем ввода
	 * @return объект JSInputWidget
	 */
	public static JSInputWidget jGetInputWidget(int x, int y, String header, String label){
		return new JSInputWidget(new Coord(x, y), header, label);
	}

	/**
	 * Приостанавливает текущий поток в котором выполняется бот
	 * @param timeout Время на которое бот приостанавливается (в милисекундах)
	 * @return true если слип завершился без интерраптов
	 */
	public static boolean jSleep(int timeout) {
		return JSBot.Sleep(timeout);
	}

	/**
	 * Выводит сообщение в консоль (системную)
	 * Функция также позволяет выводить числа и другие объекты без дополнительных преобразований
	 * @param str Объект который нужно вывести в консоль
	 */
	public static void jPrint(String str) {
		System.out.println(str);
	}

	/**
	 * Выводит сообщение в игровую консоль (Вкладка Messages)
	 * @param str строка сообщения
	 */
	public static void jToConsole(String str) {
		if(UI.instance.cons != null) UI.instance.cons.out.println(str);
	}

	/**
	 * Возвращет объект класса JSWindow указанного окна
	 * Работа с окнами в соответствующей документации
	 * @param name имя окна
	 * @return окно
	 */
	public static JSWindow jGetWindow(String name) {
		return JSBotUtils.getWindow(name);
	}
	
	/**
	 * Возвращает стадик персонажа, с котороым можно работать как с обычным инвентарем
	 * @return объект типа JSInventory
	 */
	public static JSInventory jGetStudy() {
		return JSBotUtils.getStudy();
	}

	/**
	 * Посылает запрос на пати указанному игроку
	 * @param charname имя персонажа
	 * @return true в случае успеха
	 */
	public static boolean jSendParty(String charname) {
		return JSBotUtils.buddyAct(charname, "inv"); 
	}

	/**
	 * Открывает личную переписку с указанным персонажем из кин листа
	 * @param charname имя персонажа
	 * @return true в случае успеха
	 */
	public static boolean jPrivateChat(String charname) {
		return JSBotUtils.buddyAct(charname, "chat"); 
	}

	/**
	 * Дискрайбнуть (передать кин) указанного чара
	 * @param charname имя персонажа
	 * @return true в случае успеха
	 */
	public static boolean jDescribeChar(String charname) {
		return JSBotUtils.buddyAct(charname, "desc"); 
	}

	/**
	 * Выгнать чара из деревни (доступно лауспикеру) указанного чара
	 * @param charname имя персонажа
	 * @return true в случае успеха
	 */
	public static boolean jExileChar(String charname) {
		return JSBotUtils.buddyAct(charname, "exile"); 
	}

	/**
	 * Закончить анальную "дружбу" с персонажем из кин листа, работает как "End kinship".
	 * Повторный вызов команды сработает как "Forget".
	 * @param charname имя персонажа
	 * @return true в случае успеха
	 */
	public static boolean jEndKinshipForget(String charname) {
		return JSBotUtils.buddyAct(charname, "rm"); 
	}

	/**
	 * Возвращает масив окон с указанным именем (удобно для Seedbag)
	 * @param name имя окна
	 * @return массив окон
	 */
	public static JSWindow[] jGetWindows(String name) {
		return JSBotUtils.getWindows(name);
	}

	/**
	 * Завершает выполнение скрипта
	 */
	public static void jExit() {
		JSBot.Stop();
	}

	/**
	 * Завершает текущую сессию персонажа (логаут)
	 */
	public static void jLogout() {
		JSBotUtils.logoutChar();
	}

	/**
	 * Залогиниться за персонажа с указанным логином
	 * @param acc логин персонажа
	 * @return true, если удалось залогиниться
	 */
	public static boolean jLogin(String acc) {
		return LoginScreen.login(acc);
	}

	/**
	 * Выбрать чара с указанным именем
	 * @param charname имя чара
	 * @return true, если выбрать чара удалось
	 */
	public static boolean jSelectChar(String charname) {
		return Charlist.choose_player(charname);
	}
	
	/**
	 * Возвращает true если на экране есть список выбора персонажа.
	 * @return
	 */
	public static boolean jHaveCharlist(){
		return JSBotUtils.haveCharlist();
	}

	/**
	 * Проверяет, находится ли персонаж под аггро
	 * @return true, если под аггро
	 */
	public static boolean jHaveAggro() {
		return JSBotUtils.haveAggro;
	}

	/**
	 * Функция получает значение счетчика верования для определенного параметра
	 * Окно белифсов должно быть открыто
	 * @param name �мя белифса. Допустимые значения: life, night, civil, nature, martial, change
	 * @return Возвращает значение -5...5 в случае успеха или -255 в случае ошибки.
	 */
	public static int jGetBelief(String name) {
		return JSBotUtils.get_belief(name);
	}

	/**
	 * Функция двигает ползунок белифсов. Окно белифсов должно быть открыто
	 * @param name �мя белифса. Допустимые значения: life, night, civil, nature, martial, change
	 * @param val Значение. Должно быть -1...1
	 * @return true в случае успеха, иначе false
	 */
	public static boolean jBuyBelief(String name, int val) {
		return JSBotUtils.buy_belief(name, val);
	}

	/**
	 * Возвращает список текущих чатов
	 * Работу с чатами смотреть в соответствующей документации
	 * @return Список чатов
	 */
	public static JSChat[] jGetChats() {
		return JSBotUtils.getChats();
	}

	/**
	 * Выбрать нужный пункт контекстного меню
	 * @param option_name имя пункта
	 */
	public static void jSelectContextMenu(String option_name) {
		JSBotUtils.selectPopupMenuOpt(option_name);
	}
	
	/**
	 * Выбрать нужный пункт контекстного меню
	 * @param option_name имя пункта
	 */
	public static void jSelectPopupMenu(String option_name) {
		jSelectContextMenu(option_name);
	}
	
	/**
	 * Закрывает контекстное меню, если оно есть
	 */
	public static void jClosePopup() {
		JSBotUtils.closePopup();
	}

	/**
	 * Послать действие на сервер
	 * @param name имя действия (например "mine")
	 */
	public static void jSendAction(String name) {
		JSBotUtils.sendAction(name);
	}

	/**
	 * Послать действие на сервер, но с двумя параметрами
	 * @param name имя первого действия
	 * @param name2 имя второго действия
	 * Пример: sendDoubleAction("craft", "axe");
	 */
	public static void jSendDoubleAction(String name, String name2) {
		JSBotUtils.sendAction(name, name2);
	}

	/**
	 * Выводит текст сообщения в игре (там где всякие "This land is owned by someone")
	 * @param message текст сообщения
	 */
	public static void jInGamePrint(String message) {
		JSBotUtils.slenPrint(message);
	}

	/**
	 * Открывает/закрывает инвентарь игрока в зависимости от того закрыт/открыт ли он
	 */
	public static void jToggleInventory() {
		JSBotUtils.openInventory();
	}

	/**
	 * Открывает/закрывает эквип (Equipment) игрока...
	 */
	public static void jToggleEquipment() {
		JSBotUtils.openEquipment();
	}

	/**
	 * Открывает/закрывает окно статистики (Character Sheet) игрока...
	 */
	public static void jToggleSheet() {
		JSBotUtils.openSheet();
	}

	/**
	 * Возвращает значение подсказки в указанном (по счету) блоке в окне постройки
	 * @param name имя окна
	 * @param pos позиция блока с ресурсами (нумерация с 1)
	 * @return текст подсказки
	 */
	public static String jGetBuildToolTip(String name, int pos) {
		return JSBotUtils.getISBoxValue(name, pos, 0);
	}

	/**
	 * Возвращает имя ресурса в указанном (по счету) блоке в окне постройки
	 * @param name имя окна
	 * @param pos позиция блока с ресурсами (нумерация с 1)
	 * @return имя ресурса
	 */
	public static String jGetBuildResName(String name, int pos) {
		return JSBotUtils.getISBoxValue(name, pos, 1);
	}

	/**
	 * Возвращает значения в указанном (по счету) блоке в окне постройки
	 * @param name имя окна
	 * @param pos позиция блока с ресурсами (нумерация с 1)
	 * @return значение в виде a/b/c
	 */
	public static String jGetBuildValues(String name, int pos) {
		return JSBotUtils.getISBoxValue(name, pos, 2);
	}

	/**
	 * Берет в руки вещь из окна постройки
	 * @param name имя окна постройки
	 * @param pos позиция блока с ресурсами
	 */
	public static void jTakeBuildItem(String name, int pos) {
		JSBotUtils.isBoxAct(name, pos, 0);
	}

	/**
	 * Перемещает в инвентарь игрока вещь (одну за один вызов функции) из окна постройки
	 * @param name имя окна постройки
	 * @param pos позиция блока с ресурсами
	 */
	public static void jTransferBuildItem(String name, int pos) {
		JSBotUtils.isBoxAct(name, pos, 1);
	}

	/**
	 * Проверяет открыто ли окно
	 * @param wnd имя окна
	 * @return true, если открыто
	 */
	public static boolean jHaveWindow(String wnd) {
		return JSBotUtils.haveWindow(wnd);
	}

	/**
	 * Проверяет имя текущего курсора с указанным
	 * @param cur имя курсора (можно посмотреть по ctrl+D)
	 * @return true, если именя совпадают
	 */
	public static boolean jIsCursor(String cur) {
		cur = cur.toLowerCase();
		return JSBotUtils.isCursorName(cur);
	}

	/**
	 * Возвращает имя текущего курсора
	 * @return имя курсора
	 */
	public static String jGetCursor() {
		return JSBotUtils.getCursorName();
	}

	/**
	 * Возвращает объект типа JSEquip для работы с инвентарем. 
	 * Доступные методы в соответствующей документации. 
	 * @return объект типа JSEquip, ели откруто окно эквипа, иначе null
	 */
	public static JSEquip jGetJSEquip(){
		if(JSBotUtils.haveWindow("Equipment"))
			return new JSEquip();
		return null;
	}

	/**
	 * Выбросить на землю объект, который на курсоре
	 * @param mod модификатор клавиатуры
	 */
	public static void jDropObject(int mod) {
		JSBotUtils.dropObj(mod);
	}

	/**
	 * Возвращает вещь на курсоре
	 * @return объект JSItem или null, если ничего не держим в руках
	 */
	public static JSItem jGetDraggingItem() {
		return JSBotUtils.getItemDrag();
	}

	/**
	 * Проверяет наличие окна крафта
	 * @param wnd название крафта
	 * @return true, если окно есть
	 */
	public static boolean jHaveCraft(String wnd) {
		return JSBotUtils.checkCraft(wnd);
	}

	/**
	 * Ждет появления окна крафта
	 * @param wnd название крафта
	 */
	public static void jWaitCraft(String wnd, int timeout) {
		int cur = 0;
		while (true) {
			if(cur > timeout)
				break;
			if (UI.instance.make_window != null)
				if ((UI.instance.make_window.is_ready) &&
						(UI.instance.make_window.craft_name.equals(wnd))) return;
			if (!JSBot.Sleep(25)) return;
			cur += 25;
		}
	}
	
	/**
	 * Закрывает окно крафта
	 */
	public static void jCloseCraft() {
		if(UI.instance.make_window != null)
			UI.instance.make_window.closeMe();
	}

	/**
	 * Скрафтить вещь
	 * @param all если true, то крафтит все вещи, иначе крафтит одну
	 */
	public static void jCraftItem(boolean all) {
		if(all)
			JSBotUtils.craftItem(1);
		else
			JSBotUtils.craftItem(0);
	}

	/**
	 * Включает/отключает рендеринг
	 * @param b включает если true
	 */
	public static void jSetRendering(boolean b) {
		JSBotUtils.setRenderMode(b);
	}

	/**
	 * Возвращает все баффы, которые сейчас есть
	 * Работа с баффами в соответствующей документации
	 * @return массив баффов
	 */
	public static JSBuff[] jGetBuffs(){
		return JSBotUtils.getBuffs();
	}

	/**
	 * Проверяет наличие контекстного меню
	 * @return true, если меню открыто
	 */
	public static boolean jHavePopup() {
		return JSBotUtils.havePopupMenu();
	}

	/**
	 * Проверяет наличие пункта в контекстном меню
	 * @param opt имя пункта
	 * @return true, меню содержит указанный пункт
	 */
	public static boolean jHavePopupOption(String opt) {
		if(!JSBotUtils.havePopupMenu()) return false;
		return JSBotUtils.popupBtn(opt);
	}

	/**
	 * Проверяет наличие прогресса (песочные часы)
	 * @return true, если они есть
	 */
	public static boolean jHaveHourglass() {
		return JSBotUtils.hourGlass;
	}
	
	/**
	 * Проверяет наличие прогресса
	 * @return
	 */
	public static boolean jHaveProgress() {
		return jHaveHourglass();
	}

	/**
	 * Возвращает текущую скорость персонажа (0..3)
	 * @return скорость
	 */
	public static int jGetSpeed() {
		return JSBotUtils.getSpeed();
	}

	/**
	 * Установливает текущую скорость передвижения персонажа
	 * @param speed скорость персонажа (0..3)
	 */
	public static void jSetSpeed(int speed) {
		if(speed < 0)
			speed = 0;
		else if(speed > 3)
			speed = 3;
		JSBotUtils.setSpeed(speed);
	}

	/**
	 * Возвращает абсолютный голод персонажа
	 * @return голод
	 */
	public static int jGetHungry() {
		return JSBotUtils.playerHungry;
	}

	/**
	 * Возвращает софтХП игрока
	 * @return софтХП
	 */
	public static int jGetSHP() {
		return JSBotUtils.playerSHP;
	}

	/**
	 * Возвращает хардХП игрока
	 * @return хардХП
	 */
	public static int jGetHHP() {
		return JSBotUtils.playerHHP;
	}

	/**
	 * Возвращает максимальное количество ХП игрока
	 * @return максХП
	 */
	public static int jGetMHP() {
		return JSBotUtils.playerMHP;
	}

	/**
	 * Возвращает усталость персонажа
	 * @return усталость
	 */
	public static int jGetStamina() {
		return JSBotUtils.playerStamina;
	}

	/**
	 * Возвращает идентификатор персонажа
	 * @return идентификатор
	 */
	public static int jGetMyID() {
		return JSBotUtils.playerID;
	}
	
	/**
	 * Возвращает объект персонажа в качестве JSGob
	 * @return
	 */
	public static JSGob jGetMyGob() {
		return new JSGob(jGetMyID());
	}

	/**
	 * Проверяет двигается ли персонаж
	 * @return true, если двигается
	 */
	public static boolean jIsMoving() {
		return JSBotUtils.isMoving();
	}

	/**
	 * Проверяет наличие предмета в руках (на курсоре)
	 * @return true, если персонаж что-то держит
	 */
	public static boolean jIsDragging() {
		return JSBotUtils.isDragging();
	}

	/**
	 * Проверяет готово ли окно крафта
	 * @return true, если готово
	 */
	public static boolean jIsCraftReady() {
		return JSBotUtils.isCraftReady();
	}

	/**
	 * Проигрывает музыку указанное количество миллесекунд, не прерывая
	 * выполнение скрипта
	 * @param msec время проигрывания
	 */
	public static void jPlayBeep(final int msec) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Music.startPlayBeep();
				JSBot.Sleep(msec);
				Music.stopPlayBeep();
			}
		}).run();
	}
	
	/**
	 * Проигрывает звук указанное количество раз с определенной задержкой
	 * @param resname имя ресурса из папки res/sfx (пример: jPlaySound("sfx/chop", 500, 5);)
	 * @param delay время задержки между проигрыванием звука
	 * @param times количество повторений
	 */
	public static void jPlaySound(final String resname, final int delay, final int times) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Resource alert_sound = Resource.fromFile(resname);
				if (alert_sound == null) {
						System.out.println("file does not exists.");
						return;
				}
				int ltimes;
				if (times < 1)
					ltimes = 1;
				else
					ltimes = times;
				int t = 0;
				while (t != ltimes) {
					Audio.play(alert_sound);
					JSBot.Sleep(delay);
					++t;
				}
			}
		}).run();
	}

	/**
	 * Функция ждет появления контекстного меню
	 */
	public static boolean jWaitPopup(int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(!JSBotUtils.havePopupMenu())
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}

	/**
	 * Функция ждет начала движения персонажа
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitStartMove(int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(!JSBotUtils.isMoving())
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}

	/**
	 * Функция ждет конца завершения движения игрока
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitEndMove(int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(JSBotUtils.isMoving())
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}

	/**
	 * Ждет начало, а затем завершение движения персонажа
	 * @param timeout максимальное время ожидания
	 */
	public static void jWaitMove(int timeout) {
		if (!jWaitStartMove(timeout)) return;
		jWaitEndMove(timeout);
	}

	/**
	 * Функция ждет начала движения объекта
	 * @param gob id объекта
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitStartMoveGob(int gob, int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(!jGob(gob).isMoving())
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}

	/**
	 * Функция ждет завершения движения объекта
	 * @param gob id объекта
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitEndMoveGob(int gob, int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(jGob(gob).isMoving())
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}

	/**
	 * Ждет начало, а затем завершение движения объекта
	 * @param gob id объекта
	 * @param timeout максимальное время ожидания
	 */
	public static void jWaitMoveGob(int gob, int timeout) {
		if (!jWaitStartMoveGob(gob, timeout)) return;
		jWaitEndMoveGob(gob, timeout);
	}
	/**
	 * Функция ждет появления указанного курсора
	 * @param name имя курсора
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitCursor(String name, int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(!JSBotUtils.getCursorName().equals(name))
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}

	/**
	 * Функция ждет появления указанного окна
	 * @param name имя ока
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitWindow(String name, int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(curr < timeout)
		{
			if(JSBotUtils.lastCreatedWindow != null &&
					JSBotUtils.lastCreatedWindow.cap.text != null &&
					JSBotUtils.lastCreatedWindow.cap.text.equals(name)){
				JSBotUtils.lastCreatedWindow = null;
				return true;
			}
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return false;
	}

	/**
	 * Функция ждет появления указанного окна и возвращает его, если оно появилось за время таймаута
	 * @param name name имя ока
	 * @param timeout максимальное время ожидания
	 * @return окно
	 */
	public static JSWindow jWaitNewWindow(String name, int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(curr < timeout) {
			if (JSBotUtils.lastCreatedWindow != null &&
				JSBotUtils.lastCreatedWindow.cap.text != null &&
				JSBotUtils.lastCreatedWindow.cap.text.equals(name)) {
				
				int rid = UI.instance.getId(JSBotUtils.lastCreatedWindow);
				if (!(UI.instance.getWidget(rid) instanceof Window)) continue;
				JSWindow ret = new JSWindow(rid);
				JSBotUtils.lastCreatedWindow = null;
				return ret;
			}
			if (!JSBot.Sleep(25)) return null;
			curr += 25;
		}
		return null;
	}

	/**
	 * Грязный хак, лучше это вызвать в начале скрипта, если будете работать с окнами в скрипте
	 */
	public static void jDropLastWindow() {
		JSBotUtils.lastCreatedWindow = null;
	}

	/**
	 * Функция ждет начала прогресса
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitStartProgress(int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(!JSBotUtils.hourGlass)
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}

	/**
	 * Ждет полный цикл прогресса
	 * @param timeout максимальное время ожидания
	 */
	public static void jWaitProgress(int timeout) {
		if (!jWaitStartProgress(timeout)) return;
		jWaitEndProgress(timeout);
	}

	/**
	 * Функция ждет завершения прогресса
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitEndProgress(int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(JSBotUtils.hourGlass)
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}

	/**
	 * Функция ждет появления вещи на курсоре (ждет пока что-то не возмем в руки)
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitDrag(int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(!JSBotUtils.isDragging())
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}

	/**
	 * Функция ждет исчезновения вещи с курсора (ждет пока что-то не выбросим из рук)
	 * @param timeout максимальное время ожидания
	 */
	public static boolean jWaitDrop(int timeout) {
		int curr = 0; if(timeout == 0) timeout = 10000;
		while(JSBotUtils.isDragging())
		{
			if(curr > timeout)
				return false;
			if (!JSBot.Sleep(25)) return false;
			curr += 25;
		}
		return true;
	}
	
	/***************************************************************
	 * Ниже следует пиздец связанный с картой
	 ***************************************************************/
	/**
	 * Возвращает массив объектов JSGob в указанном радиусе с
	 * оффсетом от игрока (в тайлах)
	 * 
	 * @param rad
	 *            радиус поиска объектов (в точках)
	 * @param offset
	 *            оффсет от игрока (в тайлах)
	 * @param mask
	 *            перечисление имен необходимых объектов (подстрока ресурса,
	 *            "!" в начале исключает объект из поиска)
	 * @return массив идентификаторов
	 */
	public static JSGob[] jGetObjects(int rad, Object offset, Object mask) {
		return JSBotUtils.objectIdList(rad, unWrapCoord(offset), unWrapStringArray(mask));
	}
	
	/**
	 * Возвращает массив объектов JSGob с указанным именем, BLOB'ом и в указанном прямоугольнике.
	 * @param abscrd абсолютные координаты начала (северо-западный угол) прямоугольника
	 * @param size размер прямоугольника
	 * @param blob BLOB объекта
	 * @param mask перечисление имен необходимых объектов (подстрока ресурса,
	 * "!" в начале исключает объект из поиска)
	 * @return массив идентификаторов
	 */
	public static JSGob[] jGetObjectsInRect(Object abscrd, Object size, int blob, Object mask) {
		return JSBotUtils.getObjectsInRect(unWrapCoord(abscrd), unWrapCoord(size), blob,
				unWrapStringArray(mask));
	}

	/**
	 * Возвращает тип тайла в оффсете от игрока (в тайлах)
	 * 
	 * @param offset
	 *            оффсет в тайлах
	 * @return тип тайла, -1 если не удалось получить тип тайла
	 */
	public static int jGetTileType(Object offset) {
		Coord c = unWrapCoord(offset);
		return JSBotUtils.tileType(c.x, c.y);
	}
	
	/**
	 * Возвращает тип тайла по абсолютным координатам
	 * @param absCoord абсолютные координаты
	 * @return тип тайла
	 */
	public static int jAbsTileType(Object absCoord) {
		Coord c = unWrapCoord(absCoord);
		return JSBotUtils.absTileType(c.x, c.y);
	}

	/**
	 * Отправляет на сервер щелчек мыши по объекту
	 * 
	 * @param objid
	 *            идентификатор объекта
	 * @param btn
	 *            кнопка мыши (1 - левая, 3 - правая)
	 * @param mod
	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
	 *            win)
	 */
	public static void jDoClick(int objid, int btn, int mod) {
		JSBotUtils.doClick(objid, btn, mod);
	}

	/**
	 * Двигаться на PF к указанной точке, координаты задавать абсолютные
	 * 
	 * @param point
	 *            координаты точки назначения
	 * @return количество кусков пути. 0, если путь не найден
	 */
	public static int jPFMove(Object point) {
		return UI.instance.mapview.map_pf_move(unWrapCoord(point));
	}

	/**
	 * Кликнуть по объекту правой кнопкой мыши, используя PF
	 * 
	 * @param id
	 *            идентификатор объекта
	 */
	public static int jPFClick(int id) {
		return UI.instance.mapview.map_pf_interact(id);
	}

	/**
	 * Отправляет на сервер щелчек по карте в указанные координаты
	 * относительно игрока (в тайлах)
	 * 
	 * @param coord
	 *            координаты
	 * @param btn
	 *            кнопка мыши (1 - левая, 3 - правая)
	 * @param mod
	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
	 *            win)
	 */
	public static void jOffsetClick(Object coord, int btn, int mod) {
		Coord c = unWrapCoord(coord);
		JSBotUtils.mapClick(c.x, c.y, btn, mod);
	}

	/**
	 * Отправляет на сервер щелчек по карте в указанные координаты
	 * (абсолютные, в точках карты)
	 * 
	 * @param coord
	 *            координаты
	 * @param btn
	 *            кнопка мыши (1 - левая, 3 - правая)
	 * @param mod
	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
	 *            win)
	 */
	public static void jAbsClick(Object coord, int btn, int mod) {
		Coord c = unWrapCoord(coord);
		JSBotUtils.mapAbsClick(c.x, c.y, btn, mod);
	}

	/**
	 * Передвигается на указанное количество тайлов относительно игрока
	 * 
	 * @param coord
	 *            координаты передвижения
	 */
	public static void jMoveStep(Object coord) {
		Coord c = unWrapCoord(coord);
		JSBotUtils.mapMoveStep(c.x, c.y);
	}

	/**
	 * Возвращает координаты игрока
	 * 
	 * @return координаты игрока
	 */
	public static Coord jMyCoords() {
		return JSBotUtils.MyCoord();
	}

	/**
	 * Взаимодествие предмета в руках (на курсоре) с точкой на карте (в
	 * тайлах) относительно игрока
	 * 
	 * @param coord
	 *            координаты оффсета
	 * @param mod
	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
	 *            win)
	 */
	public static void jInteractClick(Object coord, int mod) {
		Coord c = unWrapCoord(coord);
		JSBotUtils.mapInteractClick(c.x, c.y, mod);
	}

	/**
	 * Взаимодествие предмета в руках с точкой на карте (абсолютные, в
	 * точках карты)
	 * 
	 * @param coord
	 *            абсолютные координаты
	 * @param mod
	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
	 *            win)
	 */
	public static void jAbsInteractClick(Object coord, int mod) {
		Coord c = unWrapCoord(coord);
		JSBotUtils.mapAbsInteractClick(c.x, c.y, mod);
	}

	/**
	 * Поставить объект который хотим построить в указанные координаты (в
	 * тайлах) относительно игрока
	 * 
	 * @param coord
	 *            координаты оффсета от игрока
	 * @param btn
	 *            кнопка мыши (1 - левая, 3 - правая)
	 * @param mod
	 *            модификатор клавиатуры (1 - shift; 2 - ctrl; 4 - alt; 8 -
	 *            win)
	 */
	public static void jPlace(Object coord, int btn, int mod) {
		Coord c = unWrapCoord(coord);
		JSBotUtils.mapPlace(c.x, c.y, btn, mod);
	}

	/**
	 * Проверяет можно ли дойти до указанной точки напрямую
	 * 
	 * @param rc
	 *            Абсолютные координаты точки
	 * @return true если можно пройти напрямую
	 */
	public static boolean jIsPathFree(Object rc) {
		return APXUtils.isPathFree(unWrapCoord(rc));
	}

	/**
	 * 'Просит' игрока выбрать объект мышкой, пользователь должен щелкнуть
	 * на любой объект, тогда управление вернется в скрипт. По сути скрипт
	 * не продолжит выполнение, пока пользователь не выберет объект.
	 * 
	 * @param text
	 *            сообщение в игре (как в inGamePrint объекта JSHaven)
	 * @return объект JSGob или null
	 */
	public static JSGob jSelectObject(String text) {
		if (UI.instance.mapview == null)
			return null;
		JSBotUtils.slenPrint(text);
		UI.instance.mapview.objectSelecting = true;
		while (UI.instance.mapview.objectSelecting) {
			if (!JSBot.Sleep(100)) return null;
		}
		if (UI.instance.mapview.objectUnderMouse != null) {
			return new JSGob(UI.instance.mapview.objectUnderMouse.id);
		}
		return null;
	}

	/**
	 * Возвращает объект JSGob или null с указанным именем ресурса в
	 * указанном радиусе (тайлы) от игрока
	 * 
	 * @param name
	 *            имя ресурса
	 * @param rad
	 *            радиус от игрока (в тайлах)
	 * @return объект JSGob или null
	 */
	public static JSGob jFindObjectByName(String name, int rad) {
		return JSBotUtils.findObjectByName(name, rad);
	}

	/**
	 * Находит объект JSGob в указанном радиусе с оффсетом от игрока
	 * 
	 * @param name
	 *            имя ресурса
	 * @param rad
	 *            радиус поиска (в точках карты)
	 * @param coord
	 *            координаты оффсета от игрока
	 * @return ближайший объект JSGob или null
	 */
	public static JSGob jFindObjectWithOffset(String name, int rad, Object coord) {
		Coord c = unWrapCoord(coord);
		return JSBotUtils.findMapObject(name, rad, c.x, c.y);
	}

	/**
	 * Возвращает координаты 'центрирования' по тайлу
	 * 
	 * @param coord
	 *            передаваемые координаты
	 * @return 'центрированный тайл'
	 */
	public static Coord jTilify(Object coord) {
		Coord c = unWrapCoord(coord);
		return JSBotUtils.tilify(c);
	}
	
	/**
	 * Возвращает абсолютные координаты ближайшего тайла с указанным типом
	 * @param raduis радиус поиска в тайлах
	 * @param type тип тайла
	 * @return абсолютные координаты тайла
	 */
	public static Coord jGetNearestTileAbs(int raduis, int type) {
		return jMyCoords().add(jGetNearestTileCoord(raduis, type).mul(11));
	}
	
	/**
	 * Возвращает координаты оффсета в тайлах ближайшего тайла с указанным
	 * типом
	 * 
	 * @param raduis
	 *            радиус поиска тайла (в тайлах)
	 * @param type
	 *            тип тайла
	 * @return координаты оффсета от игрока в тайлах (P.S.: масло масло
	 *         масло.)
	 */
	public static Coord jGetNearestTileCoord(int raduis, int type) {
		double maxRealRad = 100000;
		Coord ret = null;
		for (int step = 1; step < raduis; step++) {
			for (int i = 0; i < step; i++) {
				int x, y;
				x = i - step;
				y = i;
				if (JSBotUtils.tileType(x, y) == type) {
					double realRad = Math.sqrt(x * x + y * y); // чтобы ты
																// сдох со
																// своей
																// явой, Арх
																// NOOOOO
																// Kerri я
																// нихотеть
																// -_-
					if (realRad < maxRealRad) {
						maxRealRad = realRad;
						ret = new Coord(x, y);
						raduis += 2;
						continue;
					}
				}
				x = i;
				y = step - i;
				if (JSBotUtils.tileType(x, y) == type) {
					double realRad = Math.sqrt(x * x + y * y);
					if (realRad < maxRealRad) {
						maxRealRad = realRad;
						ret = new Coord(x, y);
						raduis += 2;
						continue;
					}
				}
				x = step - i;
				y = -i;
				if (JSBotUtils.tileType(x, y) == type) {
					double realRad = Math.sqrt(x * x + y * y);
					if (realRad < maxRealRad) {
						maxRealRad = realRad;
						ret = new Coord(x, y);
						raduis += 2;
						continue;
					}
				}
				x = -i;
				y = i - step;
				if (JSBotUtils.tileType(x, y) == type) {
					double realRad = Math.sqrt(x * x + y * y);
					if (realRad < maxRealRad) {
						maxRealRad = realRad;
						ret = new Coord(x, y);
						raduis += 2;
						continue;
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Рисует прямоугольную область на земле. Штука чисто визуальная.
	 * Чтобы отключить рисование области, вызовите данную функцию, передав нулевые значения в размере.
	 * @param offset Оффсет в тайлах от игрока.
	 * @param size Размеры прямоугольника.
	 */
	public static void jDrawGroundRect(Object offset, Object size) {
		JSBotUtils.drawGroundRect(unWrapCoord(offset), unWrapCoord(size));
	}
	
	/**
	 * Создает окно
	 * @param position Позиция окна относительно экрана (в пикселях)
	 * @param size Размер экрана (в пикселях)
	 * @param caption Заголовок
	 * @return Указатель на окно
	 */
	public static JSGUI_Window jGUIWindow(Object position, Object size, String caption) {
		return JSGUI.createWindow(unWrapCoord(position), unWrapCoord(size), caption);
	}
	
	/**
	 * Создает текстовую метку
	 * @param parent Родительский элемент (окно)
	 * @param position Позиция внутри родителя (в пикселях)
	 * @param text Текст
	 * @return Указатель на метку
	 */
	public static JSGUI_Label jGUILabel(Object parent, Object position, String text) {
		return JSGUI.createLabel(JSGUI.unWrapGUI_Widget(parent), unWrapCoord(position), text);
	}
	
	/**
	 * Создает кнопку
	 * @param parent Родительский элемент (окно)
	 * @param position Позиция внутри родителя (в пикселях)
	 * @param size Ширина кнопки (в пикселях)
	 * @param caption Текст в кнопке
	 * @return Указатель на кнопку
	 */
	public static JSGUI_Button jGUIButton(Object parent, Object position, int size, String caption) {
		return JSGUI.createButton(JSGUI.unWrapGUI_Widget(parent), unWrapCoord(position), size, caption);
	}
	
	/**
	 * Создает поле ввода
	 * @param parent Родительский элемент (окно)
	 * @param position Позиция внутри родителя (в пикселях)
	 * @param size Размер поля ввода (в пикселях)
	 * @param deftext Текст в поле ввода по умолчанию
	 * @return Указатель на поле ввода
	 */
	public static JSGUI_TextEntry jGUIEntry(Object parent, Object position, Object size, String deftext) {
		return JSGUI.createEntry(JSGUI.unWrapGUI_Widget(parent), unWrapCoord(position),
				unWrapCoord(size), deftext);
	}
	
	/**
	 * Создает чекбокс
	 * @param parent Родительский элемент (окно)
	 * @param position Позиция внутри родителя (в пикселях)
	 * @param text Текст чекбокса
	 * @return Указатель на чекбокс
	 */
	public static JSGUI_CheckBox jGUICbox(Object parent, Object position, String text) {
		return JSGUI.createBox(JSGUI.unWrapGUI_Widget(parent), unWrapCoord(position), text);
	}
	
	
	// Конец гуя!
	
	/**
	 * Функция для работы с "инветарями" лодок/картов/вагонов, возвращает ресурс объекта из такого инвентаря
	 * @param window имя окна
	 * @param pos позиция в окне (как они приходят с сервера - хз, возможно что слева направа и сверху вниз)
	 * @return полное имя ресурса
	 */
	public static String jImgName(String window, int pos) {
		return JSBotUtils.getWindowImg(window, pos);
	}
	
	/**
	 * Возвращает общее количество слотов в лодках/...
	 * @param window имя окна
	 * @return количество слотов
	 */
	public static int jImgSlotsCount(String window) {
		return JSBotUtils.windowImgs(window, true);
	}
	
	/**
	 * Возвращает количество пустых слотов в лодках/...
	 * @param window имя окна
	 * @return количество пустых слотов
	 */
	public static int jImgFreeSlots(String window) {
		return (JSBotUtils.windowImgs(window, true) - JSBotUtils.windowImgs(window, false));
	}
	
	/**
	 * Посылает клик в окне лодки/... в указанный слот с заданным идентификатором
	 * @param window имя окна
	 * @param pos позиция
	 * @param btn кнопка мыши
	 * @param mod модификатор клавиатуры
	 */
	public static void jImgClick(String window, int pos, int btn, int mod) {
		JSBotUtils.imgClick(window, pos, btn, mod);
	}
	
	/**
	 * Установить строчку в поле для ХСа
	 * @param hs Строка ХСа
	 */
	public static void jBuddySetHS(String hs) {
		BuddyWnd.instance.setHSText(hs);
	}
	
	/**
	 * Возвращает имя текущего выделенного кина 
	 * @return имя кина
	 */
	public static String jBuddyDumpCurrentName() {
		return BuddyWnd.instance.dumpCurrentSelectedName();
	}
	
	/**
	 * Возвращает информацию об одежде для текущего кина
	 * @return Текст с одеждой для данного кина
	 */
	public static String jBuddyDumpCurrentInfo() {
		return BuddyWnd.instance.dumpCurrentSelectedInfo();
	}
	
	/**
	 * Проверяет, изменилась ли информация в кинах (в случае изменения флаг сбрасывается)
	 * @return true - если информация изменилась
	 */
	public static boolean jBuddyInfoChanged() {
		return BuddyWnd.instance.buddyInfoChanged();
	}
	
	/**
	 * Забыть выделенного кина
	 */
	public static void jBuddyForgetCurrent() {
		BuddyWnd.instance.forgetCur();
	}
	
	/**
	 * Переключает режим показа предупреждений для устаревших функций
	 * @param show Отключает предупреждения для false
	 */
	public static void jShowWarnings(boolean show) {
		JSBotUtils.sWarnings = show;
	}
	
	/**
	 * Возвращает массив кинов из кинлиста
	 * @return Массив кинов
	 */
	public static String[] jBuddyList() {
		return BuddyWnd.instance.allBuddies();
	}
	
	/**
	 * Возвращает JSGob по его идентификатору (врап для устаревших функций)
	 * @param gob ID объекта
	 * @return JSGob для этого объекта
	 */
	public static JSGob jGob(int gob) {
		return new JSGob(gob);
	}
	
	/**
	 * Показывает состоит ли игрок в пати с другими игроками
	 * @return true - если состоит в пати
	 */
	public static boolean jHaveParty() {
		return JSBotUtils.haveParty();
	}
	
	/**
	 * Отправляет клик по портрету в пати
	 * @param index Порядковый номер портрета
	 * @param btn Кнопка
	 */
	public static void jPartyClick(int index, int btn) {
		if (Partyview.instace != null) {
			Partyview.instace.click(index, btn);
		}
	}
	
	public static boolean jYouAggroedId(int id) {
		if (UI.instance.fight != null) {
			return UI.instance.fight.haveID(id);
		}
		return false;
	}
	
	/**
	 * Возвращает версию клиента
	 * @return версия клиента
	 */
	public static String jGetVersion() {
		return MainFrame.hhVersion;
	}
	
	/**
	 * Возвращает объект в указанном радиусе от абсолютных координат
	 * @param coord абсолютные координаты
	 * @param radius радиус поиска объекта от абсолютных координат (в точках)
	 * @param name имя объекта
	 * @return
	 */
	public static JSGob jFindMapObjectNearAbs(Object coord, int radius, String name) {
		return JSBotUtils.findMapObjectAbs(name, radius, unWrapCoord(coord));
	}
	
	/**
	 * Выходит из пати, если оно было
	 */
	public static void jLeaveParty() {
		if (jHaveParty())
			JSBotUtils.leaveParty();
	}
	
	/**
	 * Предлагает пользователю выбрать область на карте мышью (как линейка по ctrl+l)
	 * При подтверждении выбора функция возвращает массив из четырех координат:
	 * 1 - Абсолютные координаты левого верхнего угла области
	 * 2 - Абсолютные координаты правого нижнего угла области
	 * 3 - Оффсет от игрока в тайлах (до левого верхнего угла)
	 * 4 - Размер области в тайлах
	 * @param wndpos - координаты позоции окна на экране
	 * @return массив координат
	 */
	public static Coord[] jAreaSelector(Object wndpos) {
		return JSBotUtils.areaSelector(unWrapCoord(wndpos));
	}
	
}//Static haven