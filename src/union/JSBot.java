package union;

import haven.*;

import java.io.File;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

import union.jsbot.*;

public class JSBot {
	static {
		Console.setscmd("jbot", new Console.Command() {
			public void run(Console cons, String[] args) {
				try {
					runFile(new File("scripts", args[1] + ".jbot"));
				} catch (Exception e) {
					JSError(e);
				}
			}
		});
	}
	
	public static void runFile(File file) {
		JSScriptInfo info = JSScriptInfo.LoadScriptFromFile(file);
		if (info != null) {
			info.Run();
		}
	}
	
	public static void JSError(Exception e) {
		if (e instanceof WrappedException) {
			WrappedException ex = (WrappedException) e;
			System.err.printf("[JSBot Runtime Error] %s\n", ex.getMessage());
		} else if (e instanceof EcmaError) {
			EcmaError ex = (EcmaError) e;
			System.err.printf("[JSBot ScriptSyntax Error] %s\n", ex.getMessage());
		} else e.printStackTrace();
	}
	
	public static void StopAllScripts() {
		synchronized (JSThread.scriptThreads) {
			for (JSThread jsThread : JSThread.scriptThreads.values()) {
				jsThread.Stop();
				jsThread.interrupt();
			}
		}
	}

	public static boolean Sleep(int timeout) {
		try {
			Thread.sleep(timeout);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}
	
	public static void Stop() {
		if (Thread.currentThread() instanceof JSThread) {
			JSThread current = (JSThread) Thread.currentThread();
			current.jsContext.Stop();
		}
	}

	public static void JSInit() {
		JSScriptInfo.LoadAllSripts();
	}

	@Deprecated
	public static class JSDeprecatedHaven extends ScriptableObject {
		private static final long serialVersionUID = -8850370958392090478L;

		@Override
		public String getClassName() {
			return "JSHaven";
		}
		
		public JSDeprecatedHaven() {
			
		}
		
		void deprecated() {
			if(!JSBotUtils.sWarnings) return;
			java.lang.Exception e = new java.lang.Exception();
			StackTraceElement ste[] = e.getStackTrace();
			String oldmethod = ste[1].getMethodName();
			String newmethod = oldmethod.replace("jsFunction_", "");
			Character c = Character.toUpperCase(newmethod.charAt(0));
			newmethod = c + newmethod.substring(1);
			System.out.println("WARN: " + oldmethod + " is deprecated. Use static j" + newmethod + " instead.");
		}
		
		//dirty hack; getting Coord type;
		/**
		 * Возвращает переменную типа Coord с заданными координатами
		 * @param x координата x
		 * @param y координата y
		 * @return переменная типа Coord
		 */
		public Coord jsFunction_makeCoord(int x, int y){
			System.out.println("WARN: makeCoord is deprecated. Use static jCoord instead.");
			Coord nc = new Coord(x, y);
			return nc;
		}
		
		/**
		 * Функция создает окно "ввода". Используется для ввода текстового или числового значения.
		 * @param x координата x для отрисовки окна на экране
		 * @param y координата y для отрисовки окна на экране
		 * @param header заголовок окна
		 * @param label текст лейбла над полем ввода
		 * @return объект JSInputWidget
		 */
		public JSInputWidget jsFunction_getInputWidget(int x, int y, String header, String label){
			deprecated();
			return new JSInputWidget(new Coord(x, y), header, label);
		}
		
		/**
		 * Приостанавливает текущий поток в котором выполняется бот
		 * @param timeout Время на которое бот приостанавливается (в милисекундах)
		 */
		public void jsFunction_Sleep(int timeout) {
			deprecated();
			Sleep(timeout);
		}
		
		/**
		 * Выводит сообщение в консоль (системную)
		 * Функция также позволяет выводить числа и другие объекты без дополнительных преобразований
		 * @param str Объект который нужно вывести в консоль
		 */
		public void jsFunction_Print(String str) {
			deprecated();
			System.out.println(str);
		}
		
		/**
		 * Выводит сообщение в игровую консоль (Вкладка Messages)
		 * @param str строка сообщения
		 */
		public void jsFunction_toConsole(String str) {
			deprecated();
			if(UI.instance.cons != null) UI.instance.cons.out.println(str);
		}
		
		/**
		 * Возвращет объект класса JSWindow указанного окна
		 * Работа с окнами в соответствующей документации
		 * @param name имя окна
		 * @return окно
		 */
		public JSWindow jsFunction_getWindow(String name) {
			deprecated();
			return JSBotUtils.getWindow(name);
		}
		
		/**
		 * Посылает запрос на пати указанному игроку
		 * @param charname имя персонажа
		 * @return true в случае успеха
		 */
		public boolean jsFunction_sendParty(String charname) {
			deprecated();
			return JSBotUtils.buddyAct(charname, "inv"); 
		}
		
		/**
		 * Открывает личную переписку с указанным персонажем из кин листа
		 * @param charname имя персонажа
		 * @return true в случае успеха
		 */
		public boolean jsFunction_privateChat(String charname) {
			deprecated();
			return JSBotUtils.buddyAct(charname, "chat"); 
		}
		
		/**
		 * Дискрайбнуть (передать кин) указанного чара
		 * @param charname имя персонажа
		 * @return true в случае успеха
		 */
		public boolean jsFunction_describeChar(String charname) {
			deprecated();
			return JSBotUtils.buddyAct(charname, "desc"); 
		}
		
		/**
		 * Выгнать чара из деревни (доступно лауспикеру) указанного чара
		 * @param charname имя персонажа
		 * @return true в случае успеха
		 */
		public boolean jsFunction_exileChar(String charname) {
			deprecated();
			return JSBotUtils.buddyAct(charname, "exile"); 
		}
		
		/**
		 * Закончить анальную "дружбу" с персонажем из кин листа, работает как "End kinship".
		 * Повторный вызов команды сработает как "Forget".
		 * @param charname имя персонажа
		 * @return true в случае успеха
		 */
		public boolean jsFunction_endKinshipForget(String charname) {
			deprecated();
			return JSBotUtils.buddyAct(charname, "rm"); 
		}
		
		/**
		 * Возвращает масив окон с указанным именем (удобно для Seedbag)
		 * @param name имя окна
		 * @return массив окон
		 */
		public JSWindow[] jsFunction_getWindows(String name) {
			deprecated();
			return JSBotUtils.getWindows(name);
		}
		
		/**
		 * Завершает выполнение скрипта
		 */
		public void jsFunction_Exit() {
			deprecated();
			Stop();
		}
		
		/**
		 * Завершает текущую сессию персонажа (логаут)
		 */
		public void jsFunction_Logout() {
			deprecated();
			JSBotUtils.logoutChar();
		}
		
		/**
		 * Залогиниться за персонажа с указанным логином
		 * @param acc логин персонажа
		 * @return true, если удалось залогиниться
		 */
		public boolean jsFunction_Login(String acc) {
			deprecated();
			return LoginScreen.login(acc);
		}
		
		/**
		 * Выбрать чара с указанным именем
		 * @param charname имя чара
		 * @return true, если выбрать чара удалось
		 */
		public boolean jsFunction_SelectChar(String charname) {
			deprecated();
		    	return Charlist.choose_player(charname);
		}
		
		/**
		 * Проверяет, находится ли персонаж под аггро
		 * @return true, если под аггро
		 */
		public boolean jsFunction_haveAggro() {
			deprecated();
			return JSBotUtils.haveAggro;
		}
		
		/**
		 * Функция получает значение счетчика верования для определенного параметра
		 * Окно белифсов должно быть открыто
		 * @param name Имя белифса. Допустимые значения: life, night, civil, nature, martial, change
		 * @return Возвращает значение -5...5 в случае успеха или -255 в случае ошибки.
		 */
		public int jsFunction_getBelief(String name) {
			deprecated();
			return JSBotUtils.get_belief(name);
		}
		
		/**
		 * Функция двигает ползунок белифсов. Окно белифсов должно быть открыто
		 * @param name Имя белифса. Допустимые значения: life, night, civil, nature, martial, change
		 * @param val Значение. Должно быть -1...1
		 * @return true в случае успеха, иначе false
		 */
		public boolean jsFunction_buyBelief(String name, int val) {
			deprecated();
			return JSBotUtils.buy_belief(name, val);
		}
		
		/**
		 * Возвращает список текущих чатов
		 * Работу с чатами смотреть в соответствующей документации
		 * @return Список чатов
		 */
		public JSChat[] jsFunction_getChats() {
			deprecated();
			return JSBotUtils.getChats();
		}
		
		/**
		 * Выбрать нужный пункт контекстного меню
		 * @param option_name имя пункта
		 */
		public void jsFunction_selectContextMenu(String option_name) {
			deprecated();
			JSBotUtils.selectPopupMenuOpt(option_name);
		}
		
		/**
		 * Послать действие на сервер
		 * @param name имя действия (например "mine")
		 */
		public void jsFunction_sendAction(String name) {
			deprecated();
			JSBotUtils.sendAction(name);
		}
		
		/**
		 * Послать действие на сервер, но с двумя параметрами
		 * @param name имя первого действия
		 * @param name2 имя второго действия
		 * Пример: sendDoubleAction("craft", "axe");
		 */
		public void jsFunction_sendDoubleAction(String name, String name2) {
			deprecated();
			JSBotUtils.sendAction(name, name2);
		}
		
		/**
		 * Выводит текст сообщения в игре (там где всякие "This land is owned by someone")
		 * @param message текст сообщения
		 */
		public void jsFunction_inGamePrint(String message) {
			deprecated();
			JSBotUtils.slenPrint(message);
		}
		
		/**
		 * Открывает/закрывает инвентарь игрока в зависимости от того закрыт/открыт ли он
		 */
		public void jsFunction_toggleInventory() {
			deprecated();
			JSBotUtils.openInventory();
		}
		
		/**
		 * Открывает/закрывает эквип (Equipment) игрока...
		 */
		public void jsFunction_toggleEquipment() {
			deprecated();
			JSBotUtils.openEquipment();
		}
		
		/**
		 * Открывает/закрывает окно статистики (Character Sheet) игрока...
		 */
		public void jsFunction_toggleSheet() {
			deprecated();
			JSBotUtils.openSheet();
		}
		
		/**
		 * Возвращает значение подсказки в указанном (по счету) блоке в окне постройки
		 * @param name имя окна
		 * @param pos позиция блока с ресурсами (нумерация с 1)
		 * @return текст подсказки
		 */
		public String jsFunction_getBuildToolTip(String name, int pos) {
			deprecated();
			return JSBotUtils.getISBoxValue(name, pos, 0);
		}
		
		/**
		 * Возвращает имя ресурса в указанном (по счету) блоке в окне постройки
		 * @param name имя окна
		 * @param pos позиция блока с ресурсами (нумерация с 1)
		 * @return имя ресурса
		 */
		public String jsFunction_getBuildResName(String name, int pos) {
			deprecated();
			return JSBotUtils.getISBoxValue(name, pos, 1);
		}
		
		/**
		 * Возвращает значения в указанном (по счету) блоке в окне постройки
		 * @param name имя окна
		 * @param pos позиция блока с ресурсами (нумерация с 1)
		 * @return значение в виде a/b/c
		 */
		public String jsFunction_getBuildValues(String name, int pos) {
			deprecated();
			return JSBotUtils.getISBoxValue(name, pos, 2);
		}
		
		/**
		 * Берет в руки вещь из окна постройки
		 * @param name имя окна постройки
		 * @param pos позиция блока с ресурсами
		 */
		public void jsFunction_takeBuildItem(String name, int pos) {
			deprecated();
			JSBotUtils.isBoxAct(name, pos, 0);
		}
		
		/**
		 * Перемещает в инвентарь игрока вещь (одну за один вызов функции) из окна постройки
		 * @param name имя окна постройки
		 * @param pos позиция блока с ресурсами
		 */
		public void jsFunction_transferBuildItem(String name, int pos) {
			deprecated();
			JSBotUtils.isBoxAct(name, pos, 1);
		}
		
		/**
		 * Проверяет открыто ли окно
		 * @param wnd имя окна
		 * @return true, если открыто
		 */
		public boolean jsFunction_haveWindow(String wnd) {
			deprecated();
			return JSBotUtils.haveWindow(wnd);
		}
		
		/**
		 * Проверяет имя текущего курсора с указанным
		 * @param cur имя курсора (можно посмотреть по ctrl+D)
		 * @return true, если именя совпадают
		 */
		public boolean jsFunction_isCursor(String cur) {
			deprecated();
			cur = cur.toLowerCase();
			return JSBotUtils.isCursorName(cur);
		}
		
		/**
		 * Возвращает имя текущего курсора
		 * @return имя курсора
		 */
		public String jsFunction_getCursor() {
			deprecated();
			return JSBotUtils.getCursorName();
		}
		
		/**
		 * Возвращает объект типа JSMap для последующей работы с ним. 
		 * Доступные методы смотри в соответствующей документации.
		 * @return объект типа JSMap
		 */
		public JSMap jsFunction_getJSMap(){
			deprecated();
			return new JSMap();
		}
		
		/**
		 * Возвращает объект типа JSEquip для работы с инвентарем. 
		 * Доступные методы в соответствующей документации. 
		 * @return объект типа JSEquip, ели откруто окно эквипа, иначе null
		 */
		public JSEquip jsFunction_getJSEquip(){
			deprecated();
			if(JSBotUtils.haveWindow("Equipment"))
				return new JSEquip();
			return null;
		}
		
		/**
		 * Выбросить на землю объект, который на курсоре
		 * @param mod модификатор клавиатуры
		 */
		public void jsFunction_dropObject(int mod) {
			deprecated();
			JSBotUtils.dropObj(mod);
		}
		
		/**
		 * Возвращает вещь на курсоре
		 * @return объект JSItem или null, если ничего не держим в руках
		 */
		public JSItem jsFunction_getDraggingItem() {
			deprecated();
			return JSBotUtils.getItemDrag();
		}
		
		/**
		 * Проверяет наличие окна крафта
		 * @param wnd название крафта
		 * @return true, если окно есть
		 */
		public boolean jsFunction_haveCraft(String wnd) {
			deprecated();
			return JSBotUtils.checkCraft(wnd);
		}
		
		/**
		 * Ждет появления окна крафта
		 * @param wnd название крафта
		 */
		public void jsFunction_waitCraft(String wnd, int timeout) {
			deprecated();
			int cur = 0;
			while (true) {
				if(cur > timeout)
					break;
	    		if (UI.instance.make_window != null)
	    			if ((UI.instance.make_window.is_ready) &&
	    					(UI.instance.make_window.craft_name.equals(wnd))) return;
	    		Sleep(25);
	    		cur += 25;
	    	}
		}
		
		/**
		 * Скрафтить вещь
		 * @param all если true, то крафтит все вещи, иначе крафтит одну
		 */
		public void jsFunction_craftItem(boolean all) {
			deprecated();
			if(all)
				JSBotUtils.craftItem(1);
			else
				JSBotUtils.craftItem(0);
		}
		
		/**
		 * Включает/отключает рендеринг
		 * @param b включает если true
		 */
		public void jsFunction_setRendering(boolean b) {
			deprecated();
			JSBotUtils.setRenderMode(b);
		}
		
		/**
		 * Возвращает все баффы, которые сейчас есть
		 * Работа с баффами в соответствующей документации
		 * @return массив баффов
		 */
		public JSBuff[] jsFunction_getBuffs(){
			deprecated();
			return JSBotUtils.getBuffs();
		}
		
		/**
		 * Проверяет наличие контекстного меню
		 * @return true, если меню открыто
		 */
		public boolean jsFunction_havePopup() {
			deprecated();
			return JSBotUtils.havePopupMenu();
		}
		
		/**
		 * Проверяет наличие пункта в контекстном меню
		 * @param opt имя пункта
		 * @return true, меню содержит указанный пункт
		 */
		public boolean jsFunction_havePopupOption(String opt) {
			deprecated();
			return JSBotUtils.popupBtn(opt);
		}
		
		/**
		 * Проверяет наличие прогресса (песочные часы)
		 * @return true, если они есть
		 */
		public boolean jsFunction_haveHourglass() {
			deprecated();
			return JSBotUtils.hourGlass;
		}
		
		/**
		 * Возвращает текущую скорость персонажа (0..3)
		 * @return скорость
		 */
		public int jsFunction_getSpeed() {
			deprecated();
			return JSBotUtils.getSpeed();
		}
		
		/**
		 * Установливает текущую скорость передвижения персонажа
		 * @param speed скорость персонажа (0..3)
		 */
		public void jsFunction_setSpeed(int speed) {
			deprecated();
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
		public int jsFunction_getHungry() {
			deprecated();
			return JSBotUtils.playerHungry;
		}
		
		/**
		 * Возвращает софтХП игрока
		 * @return софтХП
		 */
		public int jsFunction_getSHP() {
			deprecated();
			return JSBotUtils.playerSHP;
		}
		
		/**
		 * Возвращает хардХП игрока
		 * @return хардХП
		 */
		public int jsFunction_getHHP() {
			deprecated();
			return JSBotUtils.playerHHP;
		}
		
		/**
		 * Возвращает максимальное количество ХП игрока
		 * @return максХП
		 */
		public int jsFunction_getMHP() {
			deprecated();
			return JSBotUtils.playerMHP;
		}
		
		/**
		 * Возвращает усталость персонажа
		 * @return усталость
		 */
		public int jsFunction_getStamina() {
			deprecated();
			return JSBotUtils.playerStamina;
		}
		
		/**
		 * Возвращает идентификатор персонажа
		 * @return идентификатор
		 */
		public int jsFunction_getMyID() {
			deprecated();
			return JSBotUtils.playerID;
		}
		
		/**
		 * Проверяет двигается ли персонаж
		 * @return true, если двигается
		 */
		public boolean jsFunction_isMoving() {
			deprecated();
			return JSBotUtils.isMoving();
		}
		
		/**
		 * Проверяет двигается ли объект с указанным идетификатором
		 * @param gob идентификатор объекта
		 * @return true, если двигается
		 */
		public boolean jsFunction_isGobMoving(int gob) {
			deprecated();
			return JSBotUtils.isMoving(gob);
		}

		/**
		 * Проверяет наличие предмета в руках (на курсоре)
		 * @return true, если персонаж что-то держит
		 */
		public boolean jsFunction_isDragging() {
			deprecated();
			return JSBotUtils.isDragging();
		}
		
		/**
		 * Проверяет готово ли окно крафта
		 * @return true, если готово
		 */
		public boolean jsFunction_isCraftReady() {
			deprecated();
			return JSBotUtils.isCraftReady();
		}
		
		/**
		 * Возвращает здоровье объекта
		 * @param id идентификатор объекта
		 * @return здоровье
		 */
		public int jsFunction_getObjectHealth(int id) {
			deprecated();
			return JSBotUtils.getObjectHealth(id);
		}
		
		/**
		 * Проигрывает звук указанное количество миллесекунд, не прерывая
		 * выполнение скрипта
		 * @param msec время проигрывания
		 */
		public void jsFunction_playBeep(final int msec) {
			deprecated();
			new Thread(new Runnable() {
				@Override
				public void run() {
					Music.startPlayBeep();
					Sleep(msec);
					Music.stopPlayBeep();
				}
			}).run();
		}
		
		/**
		 * Функция ждет появления контекстного меню
		 */
		public boolean jsFunction_waitPopup(int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(!JSBotUtils.havePopupMenu())
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
		
		/**
		 * Функция ждет начала движения персонажа
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitStartMove(int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(!JSBotUtils.isMoving())
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
		
		/**
		 * Функция ждет конца завершения движения игрока
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitEndMove(int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(JSBotUtils.isMoving())
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
		
		/**
		 * Ждет начало, а затем завершение движения персонажа
		 * @param timeout максимальное время ожидания
		 */
		public void jsFunction_waitMove(int timeout) {
			deprecated();
			jsFunction_waitStartMove(timeout);
			jsFunction_waitEndMove(timeout);
		}
		
		/**
		 * Функция ждет начала движения объекта
		 * @param gob id объекта
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitStartMoveGob(int gob, int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(!JSBotUtils.isMoving(gob))
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
		
		/**
		 * Функция ждет завершения движения объекта
		 * @param gob id объекта
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitEndMoveGob(int gob, int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(JSBotUtils.isMoving(gob))
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
		
		/**
		 * Ждет начало, а затем завершение движения объекта
		 * @param gob id объекта
		 * @param timeout максимальное время ожидания
		 */
		public void jsFunction_waitMoveGob(int gob, int timeout) {
			deprecated();
			jsFunction_waitStartMoveGob(gob, timeout);
			jsFunction_waitEndMoveGob(gob, timeout);
		}
		/**
		 * Функция ждет появления указанного курсора
		 * @param name имя курсора
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitCursor(String name, int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(!JSBotUtils.getCursorName().equals(name))
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
		
		/**
		 * Функция ждет появления указанного окна
		 * @param name имя ока
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitWindow(String name, int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(curr < timeout)
			{
				if(JSBotUtils.lastCreatedWindow != null &&
						JSBotUtils.lastCreatedWindow.cap.text != null &&
						JSBotUtils.lastCreatedWindow.cap.text.equals(name)){
					JSBotUtils.lastCreatedWindow = null;
					return true;
				}
				Sleep(25);
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
		public JSWindow jsFunction_waitNewWindow(String name, int timeout) {
			deprecated();
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
				Sleep(25);
				curr += 25;
			}
			return null;
		}
		
		public void jsFunction_dropLastWindow() {
			deprecated();
			JSBotUtils.lastCreatedWindow = null;
		}
		
		/**
		 * Функция ждет начала прогресса
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitStartProgress(int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(!JSBotUtils.hourGlass)
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
		
		/**
		 * Ждет полный цикл прогресса
		 * @param timeout максимальное время ожидания
		 */
		public void jsFunction_waitProgress(int timeout) {
			deprecated();
			jsFunction_waitStartProgress(timeout);
			jsFunction_waitEndProgress(timeout);
		}
		
		/**
		 * Функция ждет завершения прогресса
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitEndProgress(int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(JSBotUtils.hourGlass)
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
		
		/**
		 * Функция ждет появления вещи на курсоре (ждет пока что-то не возмем в руки)
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitDrag(int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(!JSBotUtils.isDragging())
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
		
		/**
		 * Функция ждет исчезновения вещи с курсора (ждет пока что-то не выбросим из рук)
		 * @param timeout максимальное время ожидания
		 */
		public boolean jsFunction_waitDrop(int timeout) {
			deprecated();
			int curr = 0; if(timeout == 0) timeout = 10000;
			while(JSBotUtils.isDragging())
			{
				if(curr > timeout)
					return false;
				Sleep(25);
				curr += 25;
			}
			return true;
		}
	}// JSHaven
	

}
