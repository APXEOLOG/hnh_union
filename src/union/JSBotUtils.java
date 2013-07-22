package union;

import static haven.MCache.tileSize;
import haven.*;
import haven.BuddyWnd.Buddy;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import union.jsbot.*;

public class JSBotUtils {
	// variables here
	public static Glob glob = null;

	static int use_pf = 0;
	public static Coord rectOffset;
	public static Coord rectSize;
	public static boolean sWarnings = true;

	/* Some Variables for scripts */
	public static boolean hourGlass = false;
	public static int playerID = -1;
	public static int playerStamina = 0;
	public static int playerSHP = 0;
	public static int playerHHP = 0;
	public static int playerMHP = 0;
	public static int playerHungry = 0;
	public static int playerVillageAuthCurrent = 0;
	public static int playerVillageAuthMax = 0;
	public static int playerHappy = 0;
	public static int playerTowards = 0;
	public static boolean haveAggro = false;
	/* End of vars for scripts */
	public static boolean showDebugToConsole = false; // for debugging, toggles
														// by Shift+F11

	public static Window lastCreatedWindow = null;

	/* Remote Widget Handler */
	public static void OnWidgetRecieve(Widget wdg, int id, String type) {
		if (wdg instanceof IMeter) { // Stamina / SHP / HHP / Hungry
			currentMeters.add((IMeter) wdg);
		}
		if (wdg instanceof Img) { // Hourgalss on
			if (((Img) wdg).textureName.contains("gfx/hud/prog/"))
				hourGlass = true;
		}
		if (wdg instanceof Window) { // Last created window
			lastCreatedWindow = (Window) wdg;
			if (((Window)wdg).cap.text.equals("Change Name")) {
				@SuppressWarnings("unused")
				Button btnGen = new Button(new Coord(210, 20), 35, wdg, "Gen") {
					@SuppressWarnings("deprecation")
					@Override
					public void click() {
						parent.findchild(TextEntry.class).settext(APXUtils.generateNickname());
					}
				};
			}
		}
		if (wdg instanceof Fightview) { // Aggro
			haveAggro = true;
		}
		if (showDebugToConsole) {
			System.out.println("Widget: " + wdg.getClass().toString()
					+ " Type: " + type + " Parent: "
					+ wdg.parent.getClass().getName());
		}
	}

	public static void OnWidgetRemove(Widget wdg, int id) {
		if (wdg instanceof Img) { // Hourgalss off
			if (((Img) wdg).textureName.contains("gfx/hud/prog/"))
				hourGlass = false;
		}
		if (wdg instanceof Fightview) { // Aggro
			haveAggro = false;
		}
		if(wdg instanceof Window) {
			lastCreatedWindow = null;
		}
	}

	public static void OnWidgetUpdate(Widget wdg, int id, String msg,
			Object... args) {
		if (wdg instanceof IMeter) { // Stamina / SHP / HHP / Hungry
			updateMeters();
		}
	}

	/* End of remote widget handlers */

	/* Meter Handlers */
	public static ArrayList<IMeter> currentMeters = new ArrayList<IMeter>();

	public static void updateMeters() {
		for (IMeter meter : currentMeters) {
			if (meter.tooltip == null)
				continue;
			String meter_info = ((String) meter.tooltip).replaceAll(
					"[^\\d/%]+", "");
			if (meter.name.contains("nrj")) {
				playerStamina = Integer.parseInt(meter_info.replace("%", ""));
				continue;
			}
			if (meter.name.contains("hp")) {
				String[] hpbuf = meter_info.split("/");
				playerSHP = Integer.parseInt(hpbuf[0]);
				playerHHP = Integer.parseInt(hpbuf[1]);
				playerMHP = Integer.parseInt(hpbuf[2]);
				continue;
			}
			if (meter.name.contains("hngr")) {
				String[] buf = meter_info.split("%");
				playerHungry = Integer.parseInt(buf[1]);
				continue;
			}
			if (meter.name.contains("happy")) {
				String[] buf = meter_info.split("%");
				if (buf.length > 1) {
					playerHappy = Integer.parseInt(buf[0]);
					playerTowards = Integer.parseInt(buf[1]);
				} else {
					playerHappy = 0;
					playerTowards = Integer.parseInt(buf[0]);
				}
				continue;
			}
			if (meter.name.contains("auth")) {
				String[] buf = meter_info.split("/");
				playerVillageAuthCurrent = Integer.parseInt(buf[0]);
				playerVillageAuthMax = Integer.parseInt(buf[1]);
				continue;
			}
		}
	}

	/* End of meter handlers */

	// APX's new functions
	public static JSWindow getWindow(String name) {
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Window)
				if (((Window) wdg).cap.text.equals(name)) {
					return new JSWindow(UI.instance.getId(wdg));
				}
		}
		return null;
	}

	public static JSWindow[] getWindows(String name) {
		ArrayList<JSWindow> windows = new ArrayList<JSWindow>();
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Window)
				if (((Window) wdg).cap.text.equals(name)) {
					windows.add(new JSWindow(UI.instance.getId(wdg)));
				}
		}
		JSWindow[] a = new JSWindow[windows.size()];
		for (int i = 0; i < windows.size(); i++)
			a[i] = windows.get(i);
		return a;
	}

	public static JSChat[] getChats() {
		ArrayList<JSChat> chats = new ArrayList<JSChat>();
		Widget root = (Widget) UI.instance.chat;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof ChatHW)
				chats.add(new JSChat(UI.instance.getId(wdg)));
		}
		JSChat[] a = new JSChat[chats.size()];
		for (int i = 0; i < chats.size(); i++)
			a[i] = chats.get(i);
		return a;
	}

	// Kerrigan's block
	// You may change it, but you will die.
	
	// leaves party
	public static void leaveParty() {
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Partyview) {
				((Partyview)wdg).leaveParty();
				return;
			}
		}
	}

	// buddy
	public static boolean buddyAct(String charname, String action) {
		Map<Integer, Buddy> bIDMap = BuddyWnd.instance.idmap;
		if (bIDMap == null)
			return false;
		Set<Integer> keys = bIDMap.keySet();
		for (Integer k : keys) {
			Buddy b = bIDMap.get(k);
			if (b != null)
				if (b.name.text.equals(charname)) {
					BuddyWnd.instance.wdgmsg(action, k.intValue());
					break;
				}
		}
		return true;
	}

	// rendering
	public static void setRenderMode(boolean val) {
		Config.render_enable = val;
	}

	// return centre of screen
	public static Coord getCenterScreenCoord() {
		Coord sc, sz;
		if (UI.instance.mapview != null) {
			sz = UI.instance.mapview.sz;
			sc = new Coord((int) Math.round(Math.random() * 200 + sz.x / 2
					- 100), (int) Math.round(Math.random() * 200 + sz.y / 2
					- 100));
			return sc;
		} else
			return new Coord(400, 400);
	}

	public static JSBuff[] getBuffs() {
		Set<Integer> buffs = UI.instance.sess.glob.buffs.keySet();
		Object[] b = buffs.toArray();
		JSBuff[] a = new JSBuff[b.length];
		for (int i = 0; i < b.length; i++) {
			Integer id = (Integer) b[i];
			a[i] = new JSBuff(id.intValue());
		}
		return a;
	}

	// logouts character from game
	public static void logoutChar() {
		UI.instance.sess.close();
	}

	// return tile type at given coords
	public static int tileType(int x, int y) {
		Coord mC = MyCoord();
		mC = mC.div(tileSize);
		Coord tile = new Coord(x, y);
		mC = mC.add(tile);
		return UI.instance.mapview.getTileFix(mC);
	}
	
	public static int absTileType(int x, int y) {
		return UI.instance.mapview.getTileFix(new Coord(x, y));
	}

	// clicks at object with modifier
	public static void doClick(int obj_id, int btn, int modflags) {
		Coord sc, sz, oc;
		Gob o = glob.oc.getgob(obj_id);
		if (o == null)
			return;
		if (UI.instance.mapview != null) {
			sz = UI.instance.mapview.sz;
			sc = new Coord((int) Math.round(Math.random() * 200 + sz.x / 2
					- 100), (int) Math.round(Math.random() * 200 + sz.y / 2
					- 100));
			oc = o.position();
			UI.instance.mapview.wdgmsg("click", sc, oc, btn, modflags, obj_id,
					oc);
		}
	}

	// clicks on the map by given btn with given modifer
	public static void mapClick(int x, int y, int btn, int mod) {
		if (UI.instance.mapview != null)
			UI.instance.mapview.map_click(x, y, btn, mod);
	}

	// moves with a given offset
	public static void mapMoveStep(int x, int y) {
		if (UI.instance.mapview != null)
			UI.instance.mapview.map_move_step(x, y);
	}

	// absolute coords
	public static void mapAbsClick(int x, int y, int btn, int mod) {
		if (UI.instance.mapview != null)
			UI.instance.mapview.map_abs_click(x, y, btn, mod);
	}

	// moves to object with given offset from object
	public static void mapMove(int objid, int x, int y) {
		if (UI.instance.mapview != null)
			UI.instance.mapview.map_move(objid, new Coord(x, y));
	}

	// RMB at 'ground' with KB modifier
	public static void mapInteractClick(int x, int y, int mod) {
		if (UI.instance.mapview != null)
			UI.instance.mapview.map_interact_click(x, y, mod);
	}

	// same shit, but clicks at object
	public static void mapInteractClick(int objid, int mod) {
		if (UI.instance.mapview != null)
			UI.instance.mapview.map_interact_click(objid, mod);
	}

	// absolute
	public static void mapAbsInteractClick(int x, int y, int mod) {
		if (UI.instance.mapview != null)
			UI.instance.mapview.map_abs_interact_click(x, y, mod);
	}

	// places bUI.instanceld at given coords (tiles)
	public static void mapPlace(int x, int y, int btn, int mod) {
		if (UI.instance.mapview != null)
			UI.instance.mapview.map_place(x, y, btn, mod);
	}

	// block operates with players coords
	public static Coord MyCoord() {
		Gob pl;
		if (((pl = glob.oc.getgob(playerID)) != null)) {
			return pl.position();
		} else {
			return new Coord(0, 0);
		}
	}

	public static int myCoordX() {
		return MyCoord().x;
	}

	public static int myCoordY() {
		return MyCoord().y;
	}

	// block operates with popup menu
	public static void selectPopupMenuOpt(String OptName) {
		if (!havePopupMenu())
			return;
		UI.instance.popupMenu.SelectOpt(OptName);
	}

	public static boolean havePopupMenu() {
		return (UI.instance.popupMenu != null);
	}

	public static boolean popupBtn(String txt) {
		if (!havePopupMenu())
			return false;
		return UI.instance.popupMenu.haveOpt(txt);
	}
	
	public static void closePopup() {
		if (havePopupMenu())
			UI.instance.popupMenu.closeMenu();
	}

	// sends action
	public static void sendAction(String act_name) {
		if (UI.instance.menugrid != null) {
			UI.instance.menugrid.wdgmsg("act", act_name);
		}
	}

	// duble action
	public static void sendAction(String act_name, String act_name2) {
		if (UI.instance.menugrid != null) {
			UI.instance.menugrid.wdgmsg("act", new Object[] { act_name,
					act_name2 });
		}
	}

	// error messages
	public static void slenPrint(String msg) {
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof SlenHud)
				((SlenHud) wdg).error(msg);
		}
	}

	// opens different windows
	public static void openInventory() {
		UI.instance.root.wdgmsg("gk", 9);
	}

	public static void openEquipment() {
		UI.instance.root.wdgmsg("gk", 5);
	}

	public static void openSheet() {
		UI.instance.root.wdgmsg("gk", 20);
	}

	// true if window exist
	public static boolean haveWindow(String name) {
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Window)
				if (((Window) wdg).cap != null
						&& ((Window) wdg).cap.text != null)
					if (((Window) wdg).cap.text.equals(name)) {
						return true;
					}
		}// for
		return false;
	}

	// true if current cursor name == given name
	public static boolean isCursorName(String cur) {
		String[] sl = UI.instance.root.cursor.name.split("/");
		if (sl[sl.length - 1].equals(cur))
			return true;
		else
			return false;
	}

	// returns cursor name
	public static String getCursorName() {
		String[] sl = UI.instance.root.cursor.name.split("/");
		return sl[sl.length - 1]; // name
	}

	// returns full object name
	public static String objectResName(int objId) {
		String name = "";
		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				if (gob.id == objId) {
					name = gob.resname();
					break;
				}
			}
		}
		return name;
	}

	// returns coords of given obj
	public static Coord objCoords(int objId) {
		Coord c = new Coord();
		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				if (gob.id == objId) {
					c = gob.position();
					break;
				}
			}
		}
		return c;
	}

	public static boolean objIsPlayer(int objId) {
		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				if (gob.id == objId) {
					Avatar ava = gob.getattr(Avatar.class);
					if (ava != null) {
						return ava.isPlayer();
					}
				}
			}
		}
		return false;
	}

	public static boolean objIsSitting(int objId) {
		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				if (gob.id == objId) {
					Layered lay = gob.getattr(Layered.class);
					if (lay != null) {
						for (Entry<Indir<Resource>, Sprite> s : lay.sprites
								.entrySet()) {
							if (s.getValue().res.name.contains("gfx/borka/body/sitting/")) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean objIsCarrying(int objId) {
		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				if (gob.id == objId) {
					Layered lay = gob.getattr(Layered.class);
					if (lay != null) {
						for (Entry<Indir<Resource>, Sprite> s : lay.sprites
								.entrySet()) {
							if(s != null)
							if (s.getValue().res.name.contains("gfx/borka/body/") && s.getValue().res.name.contains("/arm/banzai/")) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	// finds object at the map with offset
	public static JSGob findMapObject(String name, int radius, int x, int y) {
		Coord my = MyCoord();
		my = MapView.tilify(my);
		Coord offset = new Coord(x, y).mul(tileSize);
		my = my.add(offset);
		double min = radius;
		Gob min_gob = null;

		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				double len = gob.position().dist(my);
				boolean m = ((name.length() > 0 && gob.resname().contains(name)) || name
						.length() < 1);
				if ((m) && (len < min)) {
					min = len;
					min_gob = gob;
				}
			}
		}
		if (min_gob != null)
			return new JSGob(min_gob.id);
		else
			return null;
	}
	
	public static JSGob findMapObjectAbs(String name, int radius, Coord abs) {
		double min = radius;
		Gob min_gob = null;

		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				double len = gob.position().dist(abs);
				boolean m = ((name.length() > 0 && gob.resname().contains(name)) || name
						.length() < 1);
				if ((m) && (len < min)) {
					min = len;
					min_gob = gob;
				}
			}
		}
		if (min_gob != null)
			return new JSGob(min_gob.id);
		else
			return null;
	}
	
	// finds object at tile
	public static int objectAtTile(String name, Coord tile) {
		Coord my = MapView.tilify(tile);
		double min = 5;
		Gob min_gob = null;

		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				double len = gob.position().dist(my);
				boolean m = ((name.length() > 0 && gob.resname().contains(name)) || name
						.length() < 1);
				if ((m) && (len < min)) {
					min = len;
					min_gob = gob;
				}
			}
		}
		if (min_gob != null)
			return min_gob.id;
		else
			return 0;
	}

	// finds object at the map with offset
	public static int findNearestMapObject(int radius) {
		Coord my = MyCoord();
		double min = radius;
		Gob min_gob = null;

		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				if (gob.id == playerID)
					continue;
				double len = gob.position().dist(my);
				if (len < min) {
					min = len;
					min_gob = gob;
				}
			}
		}
		if (min_gob != null)
			return min_gob.id;
		else
			return 0;
	}

	// finds object by name
	public static JSGob findObjectByName(String name, int radius) {
		return findMapObject(name, radius * 11, 0, 0);
	}

	public static JSGob[] objectIdList(int radius, Coord offset,
			String... objects) {
		ArrayList<Integer> objids = new ArrayList<Integer>();
		Coord myCurrentCoord = MyCoord();
		myCurrentCoord = MapView.tilify(myCurrentCoord);
		Coord objectOffset = new Coord(offset).mul(tileSize);
		myCurrentCoord = myCurrentCoord.add(objectOffset); // making offset
															// point
		double minRadius = radius; // find rad
		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				double objectDistance = gob.position().dist(myCurrentCoord) / 11; // currently
																					// with
																					// offset
				// System.out.println(objectDistance);
				for (String objName : objects) {
					if (objectDistance <= minRadius
							&& gob.resname().contains(objName)) {
						if (objName.startsWith("!"))
							continue;
						objids.add((Integer) gob.id);
					}// object at finding radius
				}// for bjects
			}// GOBs
		}// sync
		JSGob[] ret = new JSGob[objids.size()];
		for (int i = 0; i < objids.size(); i++)
			ret[i] = new JSGob(objids.get(i));
		return ret;
	}

	// drops object from hand
	public static void dropObj(int m) {
		UI.instance.mapview.drop_thing(m);
	}

	// at hands
	public static JSItem getItemDrag() {
		for (Widget wdg = UI.instance.root.child; wdg != null; wdg = wdg.next) {
			if ((wdg instanceof Item) && (((Item) wdg).isDragging))
				return (new JSItem(UI.instance.getId(wdg)));
		}
		return null;
	}

	// true if smth is dragging
	public static boolean isDragging() {
		for (Widget wdg = UI.instance.root.child; wdg != null; wdg = wdg.next) {
			if ((wdg instanceof Item) && (((Item) wdg).isDragging))
				return true;
		}
		return false;
	}
	
	public static JSInventory getStudy() {
		//if (CharWnd.Study.getInventory() != null)
		for (Widget wdg = CharWnd.study.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Inventory)
				return new JSInventory(UI.instance.getId(wdg));
		}
		return null;
	}

	// ISbox (build window) tooltip
	public static String getISBoxValue(String wnd, int pos, int type) {
		// 0 tooltip
		// 1 res name
		// 2 values
		if (type < 0 || type > 2)
			return "";
		int boxPos = 0;
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Window)
				if (((Window) wdg).cap.text.equals(wnd))
					for (Widget isb = wdg.child; isb != null; isb = isb.next)
						if (isb instanceof ISBox) {
							ISBox box = (ISBox) isb;
							if (box != null) {
								boxPos++;
								if (boxPos == pos) {
									if (type == 0)
										return box.toolTip;
									else if (type == 1)
										return box.resName;
									else if (type == 2)
										return box.boxValues;
									else
										return "";
								}
							}
						}
		}
		return "";
	}
	
	public static String getWindowImg(String wnd, int pos) {
		if (pos < 1)
			pos = 1;
		ArrayList<Img> empty = new ArrayList<Img>();
		ArrayList<Img> full  = new ArrayList<Img>();
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Window)
				if (((Window) wdg).cap.text.equals(wnd))
					for (Widget img = wdg.child; img != null; img = img.next)
						if (img instanceof Img) {
							Img i = (Img) img;
							if (i != null) {
								if(i.textureName.contains("/invsq"))
									empty.add(i);
								else
									full.add(i);
							}
						}
		}
		if (full.size() == 0)
			return "";
		if (pos - 1 > empty.size())
			return "";
		Img eimg = empty.get(pos-1);
		Coord epos = eimg.c;
		Coord esize = eimg.sz;
		for (int i = 0; i < full.size(); ++i) {
			Img timg = full.get(i);
			if(timg.c.isect(epos, esize))
				return timg.textureName;
		}
		return "";
	}
	
	public static int windowImgs(String wnd, boolean free) {
		int cnt = 0;
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Window)
				if (((Window) wdg).cap.text.equals(wnd))
					for (Widget img = wdg.child; img != null; img = img.next)
						if (img instanceof Img) {
							Img i = (Img) img;
							if (i != null) {
								if(free){
									if(i.textureName.contains("/invsq")) cnt++;
								}
								else
									if(!i.textureName.contains("/invsq")) cnt++;
										
							}
						}
		}
		return cnt;
	}
	
	public static void imgClick(String wnd, int pos, int button, int mod) {
		if (pos < 1)
			pos = 1;
		ArrayList<Img> empty = new ArrayList<Img>();
		ArrayList<Img> full  = new ArrayList<Img>();
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Window)
				if (((Window) wdg).cap.text.equals(wnd))
					for (Widget img = wdg.child; img != null; img = img.next)
						if (img instanceof Img) {
							Img i = (Img) img;
							if (i != null) {
								if(i.textureName.contains("/invsq"))
									empty.add(i);
								else
									full.add(i);
							}
						}
		}
		Img eimg = empty.get(pos-1);
		Coord epos = eimg.c;
		Coord esize = eimg.sz;
		for (int i = 0; i < full.size(); ++i) {
			Img timg = full.get(i);
			if(timg.c.isect(epos, esize)) {
				timg.wdgmsg("click", new Coord(1, 1), button, mod);
				return;
			}
		}
		eimg.wdgmsg("click", new Coord(1, 1), button, mod);
	}

	// not a boolean type
	// takes or transfer item from given wnd and given box
	public static void isBoxAct(String wnd, int pos, int type) {
		// 0 take
		// 1 transfer
		if (type != 0 && type != 1)
			return;
		int bPos = 0;
		Widget root = UI.instance.root;
		for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
			if (wdg instanceof Window)
				if (((Window) wdg).cap.text.equals(wnd))
					for (Widget isb = wdg.child; isb != null; isb = isb.next)
						if (isb instanceof ISBox) {
							ISBox box = (ISBox) isb;
							if (box != null) {
								bPos++;
								if (bPos == pos) {
									if (type == 0)
										box.takeOne();
									else if (type == 1)
										box.transferOne();
									else
										return;
								}// box that u need by count
							}// box isnt null
						}
		}// for
	}

	// true if given craft wnd is ready
	public static boolean checkCraft(String wnd) {
		if (UI.instance.make_window != null)
			return (UI.instance.make_window.is_ready && UI.instance.make_window.craft_name
					.equals(wnd)) ? true : false;
		else
			return false;
	}

	// crafts item
	public static void craftItem(int all) {
		if (UI.instance.make_window != null)
			UI.instance.wdgmsg(UI.instance.make_window, "make", all);
	}

	// returns object binary data
	public static int getObjectBlob(int id, int index) {
		int r = 0;
		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				if (gob.id == id) {
					r = gob.GetBlob(index);
					break;
				}
			}
		}
		return r;
	}

	// operates with speedget
	public static int getSpeed() {
		try {
			return UI.instance.speedget.getspeed();
		} catch (Exception e) {
			return 0;
		}
	}

	public static int setSpeed(int speed) {
		try {
			UI.instance.speedget.setspeed(speed);
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}

	// true if player moving
	public static boolean isMoving() {
		Gob me = UI.instance.mapview.glob.oc.getgob(playerID);
		Moving m = me.getattr(Moving.class);
		if (m == null)
			return false;
		else
			return true;
	}

	// true if moving
	public static boolean isMoving(int gob) {
		Gob me = UI.instance.mapview.glob.oc.getgob(gob);
		Moving m = me.getattr(Moving.class);
		if (m == null)
			return false;
		else
			return true;
	}

	// dragging at hand
	public static boolean haveDragItem() {
		for (Widget wdg = UI.instance.root.child; wdg != null; wdg = wdg.next) {
			if ((wdg instanceof Item) && (((Item) wdg).isDragging))
				return true;
		}
		return false;
	}

	// true if craft wnd is ready
	public static boolean isCraftReady() {
		if (UI.instance.make_window != null)
			return UI.instance.make_window.is_ready ? true : false;
		return false;
	}

	// returns object health
	public static int getObjectHealth(int objid) {
		synchronized (glob.oc) {
			for (Gob gob : glob.oc)
				if (gob.id == objid)
					return gob.getHealth();
		}
		return -1;
	}

	/* Other */
	public static int get_belief(String name) {
		if (UI.instance.wnd_char.beliefs == null)
			return -255;
		if (UI.instance.wnd_char.beliefs.get(name) != null) {
			CharWnd.Belief b = UI.instance.wnd_char.beliefs.get(name);
			return b.getval();
		} else
			return -255;
	}

	public static boolean buy_belief(String name, int val) {
		if (UI.instance.wnd_char.beliefs == null)
			return false;
		if (UI.instance.wnd_char.beliefs.get(name) != null) {
			CharWnd.Belief b = UI.instance.wnd_char.beliefs.get(name);
			b.buy(val);
			return true;
		} else
			return false;
	}

	public static Gob getPlayerSelf() {
		return glob.oc.getgob(playerID);
	}

	public static boolean inTheHouse() {
		boolean inhouse = false;
		if (tileType(0, 0) == 21)
			inhouse = true;
		if (findObjectByName("stairs-cellar", 20) != null)
			inhouse = true;
		return inhouse;
	}
	
	public static void drawGroundRect(Coord offset, Coord size) {
		rectOffset = offset;
		rectSize = size;
	}
	
	public static boolean haveCharlist() {
		if(Charlist.instance != null)
			return true;
		else
			return false;
	}
	
	public static Coord m2s(Coord c) {
		return (new Coord((c.x * 2) - (c.y * 2), c.x + c.y));
	}

	public static Coord s2m(Coord c) {
		return (new Coord((c.x / 4) + (c.y / 2), (c.y / 2) - (c.x / 4)));
	}
	
	public static Coord tilify(Coord c) {
		c = c.div(tileSize);
		c = c.mul(tileSize);
		c = c.add(tileSize.div(2));
		return (c);
	}
	
	public static Coord[] areaSelector(Coord wpos) {
		final Coord [] ret = new Coord[4];
		new MapMod(wpos, UI.instance.root){
			// I like Java for this shit 
			public void wdgmsg(Widget sender, String msg, Object... args) {
				if (sender == accept) {
					ret[0] = tilify(c1.mul(tileSize));
					ret[1] = tilify(c2.mul(tileSize));
					ret[2] = tilify(MyCoord()).sub(tilify(c1.mul(tileSize))).div(tileSize).inv();
					ret[3] = selectedSize;
					ui.destroy(this);
				}
				if (sender == btn) {
					ui.destroy(this);
				}
			}
		};
		return ret;
	}
	
	public static JSGob[] getObjectsInRect(Coord abs, Coord size, int blob, String... names) {
		abs = tilify(abs);
		if(blob < 0) blob = 0;
		ArrayList<Integer> objects = new ArrayList<Integer>();
		Coord bottomRight = new Coord(abs.add(size.mul(11)));
		synchronized (glob.oc) {
			for (Gob gob : glob.oc) {
				if(gob.GetBlob(0) == blob){
					Coord gc = gob.position();
					if(gc.x >= abs.x && gc.y >= abs.y &&
					   gc.x <= bottomRight.x && gc.y <= bottomRight.y)
						for (String objName : names) {
							if (gob.resname().contains(objName)) {
								if(objName.startsWith("!")) continue;
								if(!objects.contains((Integer) gob.id))
									objects.add((Integer) gob.id);
							}
						}
				}//blob
			}// GOBs
		}//sync
		JSGob[] ret = new JSGob[objects.size()];
		for (int i = 0; i < objects.size(); i++)
			ret[i] = new JSGob(objects.get(i));
		return ret;
	}
	
	public static boolean haveParty() {
		return glob.party.memb.size() > 1;
	}
	
	public static String[] getFepList() {
		return CharWnd.foodm.getElsNames();
	}
	
	public static double getFepByName(String id) {
		return CharWnd.foodm.getFepValue(id);
	}
	
	public static int getMaxStatValue() {
		return CharWnd.getMaxFepValue();
	}
	
	public static String getMaxStatName() {
		return CharWnd.getMaxFepName();
	}
	
	public static int getStat(String n) {
		return CharWnd.getStat(n);
	}
	
	public static double getCurrentFepCap() {
		return ((double) CharWnd.foodm.getCap() / 10);
	}
}
