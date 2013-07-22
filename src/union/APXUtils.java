package union;

import haven.*;
import haven.INIFile.Pair;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import union.AStar.Location;
import union.CustomMenu.MenuElement;
import union.CustomMenu.MenuElemetUseListener;
import union.CustomMenu.MenuTree;
import union.jsbot.JSHaven;

public class APXUtils {
	static {
		// Load Accounts Data
		accounts = new HashMap<String, AccountInfo>();
		_sa_load_data();
	}

	public static void HandleException(Exception e) {
		e.printStackTrace();
	}

	/*
	 * Path Finding Section
	 */
	public static final int PF_MAP_CELL_FREE = 1;
	public static final int PF_MAP_CELL_NOT_PASSABLE = 4;

	private static int[][] _pf_map; // CoefMap
	private static int _pf_map_rad = 40; // Radius of map
	private static int _pf_tilesize = 11; // Tilesize
	private static int _pf_dim = 0; // Dimension (full length of array)
	public static boolean _pf_draw_map = true;// Draw PF Map on screen
	private static Coord _pf_last_coord;
	private static boolean _pf_size_changed = false;

	public static int[][] PF_Map() {
		return _pf_map;
	}

	public static int PF_Dim() {
		return _pf_dim;
	}

	public static int PF_QSize() {
		return _pf_tilesize;
	}

	// Rebuild map array and setup size (final)
	private static void _pf_rebuild() {
		_pf_dim = _pf_map_rad * 2 + 1;
		_pf_map = new int[_pf_dim][_pf_dim];
	}

	// Clear map array from values (final)
	public static void _pf_clear() {
		if (_pf_map == null || _pf_size_changed)
			_pf_rebuild();
		for (int i = 0; i < _pf_dim; i++)
			for (int j = 0; j < _pf_dim; j++)
				_pf_map[i][j] = PF_MAP_CELL_FREE;
	}

	// Converts real coords to _pf_map coord (final)
	public static Coord _pf_real2map(Coord c) {
		Coord c_til = MapView.tilify(c);
		Coord l_til = MapView.tilify(UI.instance.mapview.myLastCoord);
		Coord new_c = c_til.sub(l_til).div(MCache.tileSize);
		return new_c.add(_pf_map_rad + 1, _pf_map_rad + 1);
	}

	// Converts _pf_map coord to real coord
	public static Coord _pf_map2real(Coord c) {
		Coord l_til = MapView.tilify(UI.instance.mapview.myLastCoord);
		return c.sub(_pf_map_rad + 1).mul(MCache.tileSize).add(l_til);
	}

	// Converts _pf_map coord to real coord
	public static Coord _pf_map2real_stored(Coord c) {
		Coord l_til = MapView.tilify(_pf_last_coord);
		return c.sub(_pf_map_rad + 1).mul(_pf_tilesize).add(l_til);
	}

	public static Coord _pf_get_ltmc() {
		Coord rel = UI.instance.mapview.myLastCoord.div(_pf_tilesize);
		return rel.sub(new Coord(_pf_map_rad + 1, _pf_map_rad + 1));
	}

	// If coord in array
	private static boolean _pf_in_array(Coord it) {
		if (_pf_map == null)
			return false;
		return it.x < _pf_map[0].length && it.y < _pf_map.length && it.x > 0
				&& it.y > 0;
	}

	// Mark array as non-passable (real coord input)
	public static void _pf_mark_area(Coord start, Coord end) {
		Coord s1 = start.sub(MapView.tilefy_ns(start));
		Coord s2 = end.sub(MapView.tilefy_ns(end));// .add(MCache.tileSize);
		// Coord s1 = new Coord(start.x, start.y);
		// Coord s2 = new Coord(end.x, end.y);
		// Coord st_trans = start.sub((start.div(11)).mul(11));
		// Coord en_trans = end.sub((end.div(11)).mul(11));
		if (s1.x > 6)
			start.x += 6;
		if (s1.y > 6)
			start.y += 6;

		if (s2.x < 4)
			end.x -= 6;
		if (s2.y < 4)
			end.y -= 6;

		// if (s1.x > s2.x) s2.x = s1.x + 1;
		// if (s1.y > s2.y) s2.y = s1.y + 1;

		Coord p1 = _pf_real2map(start);
		Coord p2 = _pf_real2map(end);

		if (!_pf_in_array(p1) || !_pf_in_array(p2))
			return;
		for (int i = p1.x; i <= p2.x; i++)
			for (int j = p1.y; j <= p2.y; j++)
				_pf_map[i][j] = AStar.Constants.DIAGONAL_NON_PASSABLE;
	}

	// Mark areay as non-passable (real coord input)
	public static void _pf_unmark_area(Coord start, Coord end) {
		Coord p1 = _pf_real2map(start);
		Coord p2 = _pf_real2map(end);

		if (!_pf_in_array(p1) || !_pf_in_array(p2))
			return;
		for (int i = p1.x; i <= p2.x; i++)
			for (int j = p1.y; j <= p2.y; j++)
				_pf_map[i][j] = PF_MAP_CELL_FREE;
	}

	// Mark areay as non-passable (real coord input)
	public static void _pf_mark_point(Coord start, int type) {
		int action = PF_MAP_CELL_FREE;
		if (type == 255 || type == 0)
			action = PF_MAP_CELL_NOT_PASSABLE;
		Coord p1 = _pf_real2map(start);
		if (!_pf_in_array(p1))
			return;
		_pf_map[p1.x][p1.y] = action;
	}

	// Print map into console
	public static void _pf_print_map() {
		for (int j = 0; j < _pf_map.length; j++) {
			for (int i = 0; i < _pf_map[0].length; i++)
				System.out.print(_pf_map[i][j]);
			System.out.println();
		}
	}

	// Get cell value
	public static int _pf_get_cell(Coord rc) {
		Coord it = _pf_real2map(rc);
		if (_pf_in_array(it))
			return _pf_map[it.x][it.y];
		else
			return -1;
	}

	// Find path
	@SuppressWarnings("rawtypes")
	public static ArrayList<Coord> _pf_find_path(Coord rc, int id_if_needed) {
		_pf_compute(id_if_needed);
		/*if (unmark_last_point)
			_pf_unmark_area(rc, new Coord(1, 1).add(rc));*/
		Coord pc = new Coord(_pf_map_rad + 1, _pf_map_rad + 1);
		Coord ec = _pf_real2map(rc);
		AStar a = new AStar(_pf_map, new Location(pc.x, pc.y), new Location(
				ec.x, ec.y));

		ArrayList<Coord> path = new ArrayList<Coord>();
		Vector v = a.AStarSearch(null);
		if (v == null)
			return new ArrayList<Coord>();
		for (Object obj : v) {
			path.add(new Coord(((AStar.Node) obj).location.x,
					((AStar.Node) obj).location.y));
		}
		ArrayList<Coord> ret = new ArrayList<Coord>();
		ret.add(path.get(0)); // Add 1st element
		for (int i = 1; i < path.size() - 1; i++) {
			Coord prev = path.get(i - 1);
			Coord curr = path.get(i);
			Coord next = path.get(i + 1);

			// down
			if (prev.x == curr.x && curr.x == prev.x && curr.y - prev.y == 1
					&& next.y - curr.y == 1) {
				continue;
			}
			// up
			if (prev.x == curr.x && curr.x == prev.x && curr.y - prev.y == -1
					&& next.y - curr.y == -1) {
				continue;
			}
			// right
			if (prev.y == curr.y && curr.y == prev.y && curr.x - prev.x == 1
					&& next.x - curr.x == 1) {
				continue;
			}
			// left
			if (prev.y == curr.y && curr.y == prev.y && curr.x - prev.x == -1
					&& next.x - curr.x == -1) {
				continue;
			}
			// up - right
			if (curr.x - prev.x == 1 && next.x - curr.x == 1
					&& curr.y - prev.y == 1 && next.y - curr.y == 1) {
				continue;
			}
			// down - right
			if (curr.x - prev.x == 1 && next.x - curr.x == 1
					&& curr.y - prev.y == -1 && next.y - curr.y == -1) {
				continue;
			}
			// up - left
			if (curr.x - prev.x == -1 && next.x - curr.x == -1
					&& curr.y - prev.y == 1 && next.y - curr.y == 1) {
				continue;
			}
			// down - left
			if (curr.x - prev.x == -1 && next.x - curr.x == -1
					&& curr.y - prev.y == -1 && next.y - curr.y == -1) {
				continue;
			}
			ret.add(curr);
		}
		ret.add(path.get(path.size() - 1)); // Add last element

		for (int i = 0; i < ret.size(); i++) {
			Coord cur = ret.get(i);
			Coord real = MapView.tilify(_pf_map2real(cur));
			ret.set(i, real);
		}
		// ret.add(rc);
		return ret;
	}

	// Fill _pf_map array
	public static void _pf_compute(int id_if_needed) {
		_pf_last_coord = UI.instance.mapview.myLastCoord;
		MapView mw = UI.instance.mapview;
		APXUtils._pf_clear();
		/* Tiles */
		for (int i = 0; i < _pf_dim; i++)
			for (int j = 0; j < _pf_dim; j++) {
				Coord cur = _pf_map2real(new Coord(i, j));
				int type = mw.getTileFix(cur.div(_pf_tilesize));
				_pf_map[i][j] = (type == 255 || type == 0) ? AStar.Constants.FULL_NON_PASSABLE
						: AStar.Constants.TERRAIN;
			}
		/* Objects */
		String name;
		synchronized (mw.glob.oc) {
			for (Gob gob : mw.glob.oc) {
				if (gob.id == JSBotUtils.playerID)
					continue;
				if (gob.id == id_if_needed)
					continue;
				name = gob.resname();
				if (name == null)
					continue;
				Drawable d = gob.getattr(Drawable.class);
				if (name.contains("gfx/tiles/")
						|| name.contains("gfx/terobjs/items/")
						|| name.contains("gfx/terobjs/plants/")
						|| name.contains("gfx/terobjs/herbs/")
						|| name.contains("gfx/terobjs/trees/log"))
					continue;
				if (name.contains("gfx/arch/sign")) {
					_pf_mark_area(gob.position(),
							gob.position().add(new Coord(2, 2)));
					continue;
				}
				Resource.Neg neg;
				if (d instanceof ResDrawable) {
					ResDrawable rd = (ResDrawable) d;
					if (rd.spr == null)
						continue;
					if (rd.spr.res == null)
						continue;
					neg = rd.spr.res.layer(Resource.negc);
				} else if (d instanceof Layered) {
					Layered lay = (Layered) d;
					if (lay.base.get() == null)
						continue;
					neg = lay.base.get().layer(Resource.negc);
				} else {
					continue;
				}
				if ((neg.bs.x > 0) && (neg.bs.y > 0)) {
					Coord c1 = gob.position().add(neg.bc);
					Coord c2 = c1.add(neg.bs);
					_pf_mark_area(c1, c2);
				} else {
					_pf_mark_area(gob.position(),
							gob.position().add(new Coord(2, 2)));
				}
			}
		}
	}

	public static void _pf_draw_screen_map() {
		for (int i = 0; i < _pf_dim; i++)
			for (int j = 0; j < _pf_dim; j++) {

			}
	}

	/* Logins save */

	public static class AccountInfo implements Serializable {
		private static final long serialVersionUID = -8211372962031061806L;
		public String login;
		public String password;
		public byte[] token;

		public AccountInfo(String l, String p, byte[] t) {
			login = l;
			password = p;
			token = t;
		}

		public AccountInfo(String l, String p) {
			this(l, p, null);
		}
	}

	public static HashMap<String, AccountInfo> accounts;

	public static void _sa_add_data(Object[] data) {
		// Object[] { user.text, pass.text, savepass.a }
		String login = (String) data[0];
		if (!accounts.containsKey(login)) {
			accounts.put(login, new AccountInfo(login, (String) data[1]));
			_sa_save_data();
		}
	}

	public static void _sa_save_data() {
		try {
			File file = new File("data.bin");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream files = new FileOutputStream(file);
			ObjectOutputStream sstream = new ObjectOutputStream(files);
			sstream.writeObject(accounts);
			sstream.flush();
			sstream.close();
		} catch (FileNotFoundException e) {
			HandleException(e);
		} catch (IOException e) {
			HandleException(e);
		}
	}

	public static void _sa_delete_account(String name) {
		accounts.remove(name);
		_sa_save_data();
	}

	@SuppressWarnings("unchecked")
	public static void _sa_load_data() {
		accounts.clear();
		try {
			FileInputStream file = new FileInputStream("data.bin");
			ObjectInputStream sstream = new ObjectInputStream(file);
			accounts = (HashMap<String, AccountInfo>) sstream.readObject();
			sstream.close();
		} catch (FileNotFoundException e) {
			// Just no save file
		} catch (IOException e) {
			// Some file error
		} catch (ClassNotFoundException e) {
			HandleException(e);
		}
	}

	/* Add elements to menugrid */
	public static MenuTree union_menu = new MenuTree();
	public static MenuElement scriptRootNew;
	public static MenuElement scriptRootRem;
	public static MenuElement unionRoot;
	public static MenuElement buddyElement;
	public static MenuElement equipElement;
	public static MenuElement charElement;
	public static MenuElement optElement;
	public static MenuElement invElement;

	public static Resource resUnion = Resource.load("paginae/union/union");
	public static Resource resScript1 = Resource
			.load("paginae/union/scripts/script1");
	public static Resource resScript2 = Resource
			.load("paginae/union/scripts/script2");
	public static Resource resScript3 = Resource
			.load("paginae/union/scripts/script3"); // add
	public static Resource resScript4 = Resource
			.load("paginae/union/scripts/script4"); // remove
	public static Resource resScript5 = Resource
			.load("paginae/union/scripts/script5"); // remove
	public static Resource buddyRes = Resource
			.load("paginae/union/buddy"); //buddy
	public static Resource equipRes = Resource
			.load("paginae/union/equip"); //equip
	public static Resource charRes = Resource
			.load("paginae/union/char"); //char
	public static Resource optRes = Resource
			.load("paginae/union/opt"); //options
	public static Resource invRes = Resource
			.load("paginae/union/inv"); //inventory

	public static void addMenu() {
		JSScriptInfo.RemoveAllSriptsFromMenu();
		unionRoot = new MenuElement(null,
				new String[] { CustomMenu.menu_prefix }, resUnion, "Union",
				"Union functions", 'U', "union_root");
		union_menu.setRootElement(unionRoot);
		/*Здесть насрал Керриган*/
		buddyElement = new MenuElement(null, new String[] {CustomMenu.menu_prefix, "buddy_listener"}, buddyRes,
				"Buddy window", "Toggles buddy window", 'd', "buddybtn");
		buddyElement.SetListener(new MenuElemetUseListener(null) {
					public void use(int button) {
						if (button == 1) {
							BuddyWnd.instance.visible = !BuddyWnd.instance.visible;
						}
					}
				});
		equipElement = new MenuElement(null, new String[] {CustomMenu.menu_prefix, "equip_listener"}, equipRes,
				"Equipment window", "Toggles equipment window", 'E', "equipbtn");
		equipElement.SetListener(new MenuElemetUseListener(null) {
					public void use(int button) {
						if (button == 1) {
							UI.instance.root.wdgmsg("gk", 5);
						}
					}
				});
		charElement = new MenuElement(null, new String[] {CustomMenu.menu_prefix, "char_listener"}, charRes,
				"Character window", "Toggles character sheet window", 'h', "charbtn");
		charElement.SetListener(new MenuElemetUseListener(null) {
					public void use(int button) {
						if (button == 1) {
							if (UI.instance.wnd_char != null) {
								UI.instance.wnd_char.toggle();
							}
						}
					}
				});
		optElement = new MenuElement(null, new String[] {CustomMenu.menu_prefix, "opt_listener"}, optRes,
				"Options window", "Toggles options window", 'O', "optbtn");
		optElement.SetListener(new MenuElemetUseListener(null) {
					public void use(int button) {
						if (button == 1) {
							UI.instance.slenhud.toggleopts();
						}
					}
				});
		invElement = new MenuElement(null, new String[] {CustomMenu.menu_prefix, "inv_listener"}, invRes,
				"Inventory window", "Toggles inventory window", 'I', "invbtn");
		invElement.SetListener(new MenuElemetUseListener(null) {
					public void use(int button) {
						if (button == 1) {
							UI.instance.root.wdgmsg("gk", 9);
						}
					}
				});
		
		scriptRootNew = addResource("script_start", "Run Scripts",
				"You can find all availiable scripts in this menu", 'R',
				resScript2, unionRoot, new MenuElemetUseListener(null) {
					public void use(int button) {
						if (button == 1) {
							JSScriptInfo.LoadAllSripts();
							JSScriptInfo.LoadAllSriptsToMenu();
						}
					}
				});
		
		scriptRootRem = addResource("script_stop", "Stop Scripts",
				"You can stop scripts from this menu", 'S', resScript1,
				unionRoot, null);

		addResource("toggle_draw_pf_map", "Toggle PF Map", "Do it lol!", 'T',
				resScript2, unionRoot,
				new MenuElemetUseListener(null) {
					public void use(int button) {
						UI.instance.mapview.toggle_draw_pf();
					}
				});

		addResource("stop_all_scripts", "Stop all scripts",
				"Stops all working scripts", 'a', resScript5, unionRoot,
				new MenuElemetUseListener(null) {
					public void use(int button) {
						JSBot.StopAllScripts();
					}
				});
		
		addResource("hide_all_objects", "Hide ALL Objects", "Do it lol!", 'H', resScript2, unionRoot,
				new MenuElemetUseListener(null) {
					public void use(int button) {
						Config.hide_all = !Config.hide_all;
					}
				});

		addResource("stop_self_moving", "Stop Movement",
				"Stops movement immidietly", 'm', resScript2, unionRoot,
				new MenuElemetUseListener(null) {
					public void use(int button) {
						_add_stop_movement();
					}
				});
		JSScriptInfo.LoadAllSriptsToMenu();
	}

	/**
	 * Добавить элемент в кастомное меню
	 * 
	 * @param uniq
	 *            Уникальначя строка (типа гуида). Уникальный идентификатор
	 *            кнопки. Нужен для того чтобы бинды этой иконки корректно
	 *            загружались при повторном запуске клиента. Есть перегрузка без
	 *            этого значения, такие бинды будут работать только до релога
	 * @param name
	 *            Отображаемое имя кнопки
	 * @param tooltip
	 *            Отображаемый тултип кнопки
	 * @param hotkey
	 *            Хоткей
	 * @param icon
	 *            Ресурс который будет использован в качестве иконки. Это должен
	 *            быть обычный графический ресурс (у него не должно быть лишних
	 *            слоев так что не берите ресы из пагины покачто)
	 * @param parent
	 *            Элемент который будет использован как родительский. unionRoot
	 *            - корень для всех юнион-фич. Если предок null то кнопка будет
	 *            в полном руте.
	 * @param listener
	 *            Лиснер который вызывается при клике на кнопку. Пример юза
	 *            можете глянуть в жботе. Если null то ничего не вызывается
	 * @return Элемент
	 */
	public static MenuElement addResource(String uniq, String name,
			String tooltip, char hotkey, Resource icon, MenuElement parent,
			MenuElemetUseListener listener) {
		String[] non_listener = new String[] { CustomMenu.menu_prefix };
		String[] with_listener = new String[] { CustomMenu.menu_prefix, uniq };
		MenuElement buf = parent.addChild(listener == null ? non_listener
				: with_listener, icon, name, tooltip, hotkey, uniq);
		buf.SetListener(listener);
		return buf;
	}

	public static MenuElement addResource(String name, String tooltip,
			char hotkey, Resource icon, MenuElement parent,
			MenuElemetUseListener listener) {
		return addResource(java.util.UUID.randomUUID().toString(), name,
				tooltip, hotkey, icon, parent, listener);
	}

	/* Additional functios */

	/**
	 * Немедленная остановка (клик под собой)
	 */
	public static void _add_stop_movement() {
		Coord playerCoord = JSBotUtils.MyCoord();
		if (UI.instance.mapview != null) {
			UI.instance.mapview.wdgmsg("click",
					JSBotUtils.getCenterScreenCoord(), playerCoord, 1, 0);
		}
	}

	public static String getHTML(String urlToRead) {
		URL url; // The URL to read
		HttpURLConnection conn; // The actual connection to the web page
		BufferedReader rd; // Used to read results from the web page
		String line; // An individual line of the web page HTML
		String result = ""; // A long string containing all the HTML
		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return result;
	}

	/* WASD movement */
	public static boolean wPressed = false;
	public static boolean aPressed = false;
	public static boolean sPressed = false;
	public static boolean dPressed = false;

	public static Coord updateWASD() {
		int x = 0;
		int y = 0;
		if (wPressed)
			y -= 1000;
		if (aPressed)
			x -= 1000;
		if (sPressed)
			y += 1000;
		if (dPressed)
			x += 1000;
		return new Coord(x, y);
	}

	/* MAC */
	public static String getMACAdress() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i],
						(i < mac.length - 1) ? "-" : ""));
			}
			return sb.toString();

		} catch (Exception e) {
			return null;
		}
	}

	public static String readFile(File f) {
		try {
			FileInputStream fstream = new FileInputStream(f);
			InputStreamReader in = new InputStreamReader(fstream);
			BufferedReader br = new BufferedReader(in);
			String strLine;
			StringBuilder sb = new StringBuilder();
			while ((strLine = br.readLine()) != null) {
				sb.append(strLine);
				sb.append('\n');
			}
			br.close();
			in.close();
			fstream.close();
			return sb.toString();
		} catch (Exception ex) {
			return "";
		}
	}

	/* Line Crossing */
	public static boolean isLinesCross(Coord line1_start, Coord line1_end,
			Coord line2_start, Coord line2_end) {
		double v1 = (line2_end.x - line2_start.x)
				* (line1_start.y - line2_start.y)
				- (line2_end.y - line2_start.y)
				* (line1_start.x - line2_start.x);
		double v2 = (line2_end.x - line2_start.x)
				* (line1_end.y - line2_start.y) - (line2_end.y - line2_start.y)
				* (line1_end.x - line2_start.x);
		double v3 = (line1_end.x - line1_start.x)
				* (line2_start.y - line1_start.y)
				- (line1_end.y - line1_start.y)
				* (line2_start.x - line1_start.x);
		double v4 = (line1_end.x - line1_start.x)
				* (line2_end.y - line1_start.y) - (line1_end.y - line1_start.y)
				* (line2_end.x - line1_start.x);
		return ((v1 * v2 <= 0) && (v3 * v4 <= 0));
	}

	/* Semi-PF-Checking */
	public static boolean isPathFree(Coord rc) {
		Coord my = UI.instance.mapview.myLastCoord;
		MapView mw = UI.instance.mapview;

		ArrayList<Pair<Coord, Coord>> diagonals = new ArrayList<Pair<Coord, Coord>>();
		/* Objects */
		String name;
		synchronized (mw.glob.oc) {
			for (Gob gob : mw.glob.oc) {
				if (gob.id == JSBotUtils.playerID)
					continue;
				name = gob.resname();
				if (name == null)
					continue;
				Drawable d = gob.getattr(Drawable.class);
				if (name.contains("gfx/tiles/")
						|| name.contains("gfx/terobjs/items/")
						|| name.contains("gfx/terobjs/plants/")
						|| name.contains("gfx/terobjs/herbs/")
						|| name.contains("gfx/terobjs/trees/log"))
					continue;
				if (name.contains("gfx/arch/sign")) {
					Coord c1 = gob.position().sub(_pf_tilesize / 2);
					Coord c2 = c1.add(_pf_tilesize, _pf_tilesize);
					diagonals.add(new Pair<Coord, Coord>(c1, c2));
					diagonals.add(new Pair<Coord, Coord>(new Coord(c2.x, c1.y),
							new Coord(c1.x, c2.y)));
					continue;
				}
				Resource.Neg neg;
				if (d instanceof ResDrawable) {
					ResDrawable rd = (ResDrawable) d;
					if (rd.spr == null)
						continue;
					if (rd.spr.res == null)
						continue;
					neg = rd.spr.res.layer(Resource.negc);
				} else if (d instanceof Layered) {
					Layered lay = (Layered) d;
					if (lay.base.get() == null)
						continue;
					neg = lay.base.get().layer(Resource.negc);
				} else {
					continue;
				}
				if ((neg.bs.x > 0) && (neg.bs.y > 0)) {
					Coord c1 = gob.position().add(neg.bc);
					Coord c2 = c1.add(neg.bs);
					diagonals.add(new Pair<Coord, Coord>(c1, c2));
					diagonals.add(new Pair<Coord, Coord>(new Coord(c2.x, c1.y),
							new Coord(c1.x, c2.y)));
				} else {
					Coord c1 = gob.position().sub(_pf_tilesize / 2);
					Coord c2 = c1.add(_pf_tilesize, _pf_tilesize);
					diagonals.add(new Pair<Coord, Coord>(c1, c2));
					diagonals.add(new Pair<Coord, Coord>(new Coord(c2.x, c1.y),
							new Coord(c1.x, c2.y)));
				}
			}
		}
		boolean ret = false;
		for (int i = 0; i < diagonals.size(); i++) {
			ret = ret 
					| isLinesCross(my.add(-2, -2), rc.add(-2, -2), diagonals.get(i).fst, diagonals.get(i).snd)
					| isLinesCross(my.add(-2, 2), rc.add(-2, 2), diagonals.get(i).fst, diagonals.get(i).snd)
					| isLinesCross(my.add(2, -2), rc.add(2, -2), diagonals.get(i).fst, diagonals.get(i).snd)
					| isLinesCross(my.add(2, 2), rc.add(2, 2), diagonals.get(i).fst, diagonals.get(i).snd);
		}
		return !ret;
	}
	
	public static ArrayList<String> params = new ArrayList<String>();
	
	static {
		params.add("java.version");
		params.add("os.name");
		params.add("os.arch");
		params.add("os.version");
		params.add("user.name");
	}
	
	/* Exception Handling */
	public static void handleException(Thread th, final Throwable ex) {
		if (th instanceof JSThread) return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuilder exinfo = new StringBuilder();
				for (String str : params) {
					exinfo.append(String.format("%s:%s\n", str, System.getProperty(str)));
				}
				exinfo.append(String.format("TITLE:%s\n", MainFrame.TITLE));
				exinfo.append(String.format("Exception:%s\n", ex.toString()));
				getHTML("http://unionclient.ru/exhandler.php?mac=" + getMACAdress() + "&info=" + exinfo.toString());
			}
		}).run();
	}
	
	public static BufferedImage scaleImage(BufferedImage img, int width, int height,
	        Color background) {
	    int imgWidth = img.getWidth();
	    int imgHeight = img.getHeight();
	    if (imgWidth*height < imgHeight*width) {
	        width = imgWidth*height/imgHeight;
	    } else {
	        height = imgHeight*width/imgWidth;
	    }
	    BufferedImage newImage = new BufferedImage(width, height,
	            BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = newImage.createGraphics();
	    try {
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        g.setBackground(background);
	        g.clearRect(0, 0, width, height);
	        g.drawImage(img, 0, 0, width, height, null);
	    } finally {
	        g.dispose();
	    }
	    return newImage;
	}
	
	public static void replaceGobAva(int gobid) {
		
	}
	
	static ArrayList<String> h_fnpre = new ArrayList<String>();
	static ArrayList<String> h_fnsuf = new ArrayList<String>();
	static ArrayList<String> h_lnpre = new ArrayList<String>();
	static ArrayList<String> h_lnsuf = new ArrayList<String>();
	static ArrayList<String> o_fnpre = new ArrayList<String>();
	static ArrayList<String> o_fnsuf = new ArrayList<String>();
	
	static {
		h_fnpre.add("Te");
		h_fnpre.add("Ni");
		h_fnpre.add("Nila");
		h_fnpre.add("Andro");
		h_fnpre.add("Androma");
		h_fnpre.add("Sha");
		h_fnpre.add("Ara");
		h_fnpre.add("Ma");
		h_fnpre.add("Mana");
		h_fnpre.add("La");
		h_fnpre.add("Landa");
		h_fnpre.add("Do");
		h_fnpre.add("Dori");
		h_fnpre.add("Pe");
		h_fnpre.add("Peri");
		h_fnpre.add("Conju");
		h_fnpre.add("Co");
		h_fnpre.add("Fo");
		h_fnpre.add("Fordre");
		h_fnpre.add("Da");
		h_fnpre.add("Dala");
		h_fnpre.add("Ke");
		h_fnpre.add("Kele");
		h_fnpre.add("Gra");
		h_fnpre.add("Grani");
		h_fnpre.add("Jo");
		h_fnpre.add("Sa");
		h_fnpre.add("Mala");
		h_fnpre.add("Ga");
		h_fnpre.add("Gavi");
		h_fnpre.add("Gavinra");
		h_fnpre.add("Mo");
		h_fnpre.add("Morlu");
		h_fnpre.add("Aga");
		h_fnpre.add("Agama");
		h_fnpre.add("Ba");
		h_fnpre.add("Balla");
		h_fnpre.add("Ballado");
		h_fnpre.add("Za");
		h_fnpre.add("Ari");
		h_fnpre.add("Ariu");
		h_fnpre.add("Au");
		h_fnpre.add("Auri");
		h_fnpre.add("Bra");
		h_fnpre.add("Ka");
		h_fnpre.add("Bu");
		h_fnpre.add("Buza");
		h_fnpre.add("Coi");
		h_fnpre.add("Bo");
		h_fnpre.add("Mu");
		h_fnpre.add("Muni");
		h_fnpre.add("Tho");
		h_fnpre.add("Thorga");
		h_fnpre.add("Ke");
		h_fnpre.add("Gri");
		h_fnpre.add("Bu");
		h_fnpre.add("Buri");
		h_fnpre.add("Hu");
		h_fnpre.add("Hugi");
		h_fnpre.add("Tho");
		h_fnpre.add("Thordi");
		h_fnpre.add("Ba");
		h_fnpre.add("Bandi");
		h_fnpre.add("Ga");
		h_fnpre.add("Bea");
		h_fnpre.add("Beaze");
		h_fnpre.add("Mo");
		h_fnpre.add("Modi");
		h_fnpre.add("Ma");
		h_fnpre.add("Malo");
		h_fnpre.add("Gholbi");
		h_fnpre.add("Gho");
		h_fnpre.add("Da");
		h_fnpre.add("Dagda");
		h_fnpre.add("Nua");
		h_fnpre.add("Nuada");
		h_fnpre.add("Oghma");
		h_fnpre.add("Ce");
		h_fnpre.add("Centri");
		h_fnpre.add("Cere");
		h_fnpre.add("Ce");
		h_fnpre.add("Ka");
		h_fnpre.add("Kathri");
		h_fnpre.add("Ado");
		h_fnpre.add("Adora");
		h_fnpre.add("Mora");
		h_fnpre.add("Mo");
		h_fnpre.add("Fe");
		h_fnpre.add("Felo");
		h_fnpre.add("Ana");
		h_fnpre.add("Anara");
		h_fnpre.add("Kera");
		h_fnpre.add("Mave");
		h_fnpre.add("Dela");
		h_fnpre.add("Mira");
		h_fnpre.add("Theta");
		h_fnpre.add("Tygra");
		h_fnpre.add("Adrie");
		h_fnpre.add("Diana");
		h_fnpre.add("Alsa");
		h_fnpre.add("Mari");
		h_fnpre.add("Shali");
		h_fnpre.add("Sira");
		h_fnpre.add("Sai");
		h_fnpre.add("Saithi");
		h_fnpre.add("Mala");
		h_fnpre.add("Kiri");
		h_fnpre.add("Ana");
		h_fnpre.add("Anaya");
		h_fnpre.add("Felha");
		h_fnpre.add("Drela");
		h_fnpre.add("Corda");
		h_fnpre.add("Nalme");
		h_fnpre.add("Na");
		h_fnpre.add("Um");
		h_fnpre.add("Ian");
		h_fnpre.add("Opi");
		h_fnpre.add("Lai");
		h_fnpre.add("Ygg");
		h_fnpre.add("Mne");
		h_fnpre.add("Ishn");
		h_fnpre.add("Kula");
		h_fnpre.add("Yuni");



		h_fnsuf.add("nn");
		h_fnsuf.add("las");
		h_fnsuf.add("math");
		h_fnsuf.add("th");
		h_fnsuf.add("ath");
		h_fnsuf.add("zar");
		h_fnsuf.add("ril");
		h_fnsuf.add("ris");
		h_fnsuf.add("rus");
		h_fnsuf.add("jurus");
		h_fnsuf.add("dred");
		h_fnsuf.add("rdred");
		h_fnsuf.add("lar");
		h_fnsuf.add("len");
		h_fnsuf.add("nis");
		h_fnsuf.add("rn");
		h_fnsuf.add("ge");
		h_fnsuf.add("lak");
		h_fnsuf.add("nrad");
		h_fnsuf.add("rad");
		h_fnsuf.add("lune");
		h_fnsuf.add("kus");
		h_fnsuf.add("mand");
		h_fnsuf.add("gamand");
		h_fnsuf.add("llador");
		h_fnsuf.add("dor");
		h_fnsuf.add("dar");
		h_fnsuf.add("nadar");
		h_fnsuf.add("rius");
		h_fnsuf.add("nius");
		h_fnsuf.add("zius");
		h_fnsuf.add("tius");
		h_fnsuf.add("sius");
		h_fnsuf.add("wield");
		h_fnsuf.add("helm");
		h_fnsuf.add("zan");
		h_fnsuf.add("tus");
		h_fnsuf.add("bor");
		h_fnsuf.add("nin");
		h_fnsuf.add("rgas");
		h_fnsuf.add("gas");
		h_fnsuf.add("lv");
		h_fnsuf.add("kelv");
		h_fnsuf.add("gelv");
		h_fnsuf.add("rim");
		h_fnsuf.add("sida");
		h_fnsuf.add("ginn");
		h_fnsuf.add("grinn");
		h_fnsuf.add("nn");
		h_fnsuf.add("huginn");
		h_fnsuf.add("rdin");
		h_fnsuf.add("ndis");
		h_fnsuf.add("bandis");
		h_fnsuf.add("gar");
		h_fnsuf.add("zel");
		h_fnsuf.add("di");
		h_fnsuf.add("ron");
		h_fnsuf.add("rne");
		h_fnsuf.add("lbine");
		h_fnsuf.add("gda");
		h_fnsuf.add("ghma");
		h_fnsuf.add("ntrius");
		h_fnsuf.add("dwyn");
		h_fnsuf.add("wyn");
		h_fnsuf.add("swyn");
		h_fnsuf.add("thris");
		h_fnsuf.add("dora");
		h_fnsuf.add("lore");
		h_fnsuf.add("nara");
		h_fnsuf.add("ra");
		h_fnsuf.add("las");
		h_fnsuf.add("gra");
		h_fnsuf.add("riel");
		h_fnsuf.add("lsa");
		h_fnsuf.add("rin");
		h_fnsuf.add("lis");
		h_fnsuf.add("this");
		h_fnsuf.add("lace");
		h_fnsuf.add("ri");
		h_fnsuf.add("naya");
		h_fnsuf.add("rana");
		h_fnsuf.add("lhala");
		h_fnsuf.add("lanim");
		h_fnsuf.add("rdana");
		h_fnsuf.add("lmeena");
		h_fnsuf.add("meena");
		h_fnsuf.add("fym");
		h_fnsuf.add("fyn");
		h_fnsuf.add("hara");

		h_lnpre.add("Flame");
		h_lnpre.add("Arcane");
		h_lnpre.add("Light");
		h_lnpre.add("Mage");
		h_lnpre.add("Spell");
		h_lnpre.add("Rex");
		h_lnpre.add("Dawn");
		h_lnpre.add("Dark");
		h_lnpre.add("Red");
		h_lnpre.add("Truth");
		h_lnpre.add("Might");
		h_lnpre.add("True");
		h_lnpre.add("Bright");
		h_lnpre.add("Pure");
		h_lnpre.add("Fearless");
		h_lnpre.add("Dire");
		h_lnpre.add("Blue");
		h_lnpre.add("White");
		h_lnpre.add("Black");
		h_lnpre.add("Rain");
		h_lnpre.add("Doom");
		h_lnpre.add("Rune");
		h_lnpre.add("Sword");
		h_lnpre.add("Force");
		h_lnpre.add("Axe");
		h_lnpre.add("Stone");
		h_lnpre.add("Iron");
		h_lnpre.add("Broad");
		h_lnpre.add("Stern");
		h_lnpre.add("Thunder");
		h_lnpre.add("Frost");
		h_lnpre.add("Rock");
		h_lnpre.add("Doom");
		h_lnpre.add("Blud");
		h_lnpre.add("Blood");
		h_lnpre.add("Stone");
		h_lnpre.add("Steel");
		h_lnpre.add("Golden");
		h_lnpre.add("Gold");
		h_lnpre.add("Silver");
		h_lnpre.add("White");
		h_lnpre.add("Black");
		h_lnpre.add("Gravel");
		h_lnpre.add("Sharp");
		h_lnpre.add("Star");
		h_lnpre.add("Night");
		h_lnpre.add("Moon");
		h_lnpre.add("Chill");
		h_lnpre.add("Whisper");
		h_lnpre.add("White");
		h_lnpre.add("Black");
		h_lnpre.add("Saber");
		h_lnpre.add("Snow");
		h_lnpre.add("Rain");
		h_lnpre.add("Dark");
		h_lnpre.add("Light");
		h_lnpre.add("Wind");
		h_lnpre.add("Iron");
		h_lnpre.add("Blade");
		h_lnpre.add("Shadow");
		h_lnpre.add("Flame");
		h_lnpre.add("Sin");
		h_lnpre.add("Pain");
		h_lnpre.add("Hell");
		h_lnpre.add("Wrath");
		h_lnpre.add("Rage");
		h_lnpre.add("Blood");
		h_lnpre.add("Terror");

		h_lnsuf.add("seeker");
		h_lnsuf.add("caster");
		h_lnsuf.add("binder");
		h_lnsuf.add("weaver");
		h_lnsuf.add("singer");
		h_lnsuf.add("font");
		h_lnsuf.add("hammer");
		h_lnsuf.add("redeemer");
		h_lnsuf.add("bearer");
		h_lnsuf.add("bringer");
		h_lnsuf.add("defender");
		h_lnsuf.add("conjuror");
		h_lnsuf.add("eye");
		h_lnsuf.add("staff");
		h_lnsuf.add("flame");
		h_lnsuf.add("fire");
		h_lnsuf.add("shaper");
		h_lnsuf.add("breaker");
		h_lnsuf.add("cliff");
		h_lnsuf.add("worm");
		h_lnsuf.add("hammer");
		h_lnsuf.add("brew");
		h_lnsuf.add("beard");
		h_lnsuf.add("fire");
		h_lnsuf.add("forge");
		h_lnsuf.add("stone");
		h_lnsuf.add("smith");
		h_lnsuf.add("fist");
		h_lnsuf.add("pick");
		h_lnsuf.add("skin");
		h_lnsuf.add("smasher");
		h_lnsuf.add("crusher");
		h_lnsuf.add("worker");
		h_lnsuf.add("shaper");
		h_lnsuf.add("song");
		h_lnsuf.add("shade");
		h_lnsuf.add("singer");
		h_lnsuf.add("ray");
		h_lnsuf.add("wind");
		h_lnsuf.add("fang");
		h_lnsuf.add("dragon");
		h_lnsuf.add("mane");
		h_lnsuf.add("scar");
		h_lnsuf.add("moon");
		h_lnsuf.add("wood");
		h_lnsuf.add("raven");
		h_lnsuf.add("wing");
		h_lnsuf.add("hunter");
		h_lnsuf.add("warden");
		h_lnsuf.add("stalker");
		h_lnsuf.add("grove");
		h_lnsuf.add("walker");
		h_lnsuf.add("master");
		h_lnsuf.add("blade");
		h_lnsuf.add("fury");
		h_lnsuf.add("weaver");
		h_lnsuf.add("terror");
		h_lnsuf.add("dweller");
		h_lnsuf.add("killer");
		h_lnsuf.add("seeker");
		h_lnsuf.add("bourne");
		h_lnsuf.add("bringer");
		h_lnsuf.add("runner");
		h_lnsuf.add("brand");
		h_lnsuf.add("wrath");

		o_fnpre.add("To");
		o_fnpre.add("Toja");
		o_fnpre.add("Ni");
		o_fnpre.add("Niko");
		o_fnpre.add("Ka");
		o_fnpre.add("Kaji");
		o_fnpre.add("Mi");
		o_fnpre.add("Mika");
		o_fnpre.add("Sa");
		o_fnpre.add("Samu");
		o_fnpre.add("Aki");
		o_fnpre.add("Akino");
		o_fnpre.add("Ma");
		o_fnpre.add("Mazu");
		o_fnpre.add("Yo");
		o_fnpre.add("Yozshu");
		o_fnpre.add("Da");
		o_fnpre.add("Dai");
		o_fnpre.add("Ki");
		o_fnpre.add("Kiga");
		o_fnpre.add("Ara");
		o_fnpre.add("Arashi");
		o_fnpre.add("Mo");
		o_fnpre.add("Moogu");
		o_fnpre.add("Ju");
		o_fnpre.add("Ga");
		o_fnpre.add("Garda");
		o_fnpre.add("Ne");
		o_fnpre.add("Ka");
		o_fnpre.add("Ma");
		o_fnpre.add("Ba");
		o_fnpre.add("Go");
		o_fnpre.add("Kaga");
		o_fnpre.add("Na");
		o_fnpre.add("Mo");
		o_fnpre.add("Kazra");
		o_fnpre.add("Kazi");
		o_fnpre.add("Fe");
		o_fnpre.add("Fenri");
		o_fnpre.add("Ma");
		o_fnpre.add("Tygo");
		o_fnpre.add("Ta");
		o_fnpre.add("Du");
		o_fnpre.add("Ka");
		o_fnpre.add("Ke");
		o_fnpre.add("Mu");
		o_fnpre.add("Gro");
		o_fnpre.add("Me");
		o_fnpre.add("Mala");
		o_fnpre.add("Tau");
		o_fnpre.add("Te");
		o_fnpre.add("Tu");
		o_fnpre.add("Mau");
		o_fnpre.add("Zu");
		o_fnpre.add("Zulki");
		o_fnpre.add("JoJo");
		o_fnpre.add("Sha");
		o_fnpre.add("Shaka");
		o_fnpre.add("Shakti");
		o_fnpre.add("Me");
		o_fnpre.add("Mezi");
		o_fnpre.add("Mezti");
		o_fnpre.add("Vo");
		o_fnpre.add("Do");
		o_fnpre.add("Du");
		o_fnpre.add("Di");
		o_fnpre.add("Vu");
		o_fnpre.add("Vi");
		o_fnpre.add("Dou");
		o_fnpre.add("Ga");
		o_fnpre.add("Gu");
		o_fnpre.add("Fae");
		o_fnpre.add("Fau");
		o_fnpre.add("Go");
		o_fnpre.add("Golti");
		o_fnpre.add("Vudo");
		o_fnpre.add("Voodoo");
		o_fnpre.add("Zolo");
		o_fnpre.add("Zulu");
		o_fnpre.add("Bra");
		o_fnpre.add("Net");


		o_fnsuf.add("jora");
		o_fnsuf.add("kora");
		o_fnsuf.add("jind");
		o_fnsuf.add("kasa");
		o_fnsuf.add("muro");
		o_fnsuf.add("nos");
		o_fnsuf.add("kinos");
		o_fnsuf.add("zuru");
		o_fnsuf.add("zshura");
		o_fnsuf.add("shura");
		o_fnsuf.add("ra");
		o_fnsuf.add("sho");
		o_fnsuf.add("gami");
		o_fnsuf.add("mi");
		o_fnsuf.add("shicage");
		o_fnsuf.add("cage");
		o_fnsuf.add("gul");
		o_fnsuf.add("bei");
		o_fnsuf.add("dal");
		o_fnsuf.add("gal");
		o_fnsuf.add("zil");
		o_fnsuf.add("gis");
		o_fnsuf.add("le");
		o_fnsuf.add("rr");
		o_fnsuf.add("gar");
		o_fnsuf.add("gor");
		o_fnsuf.add("grel");
		o_fnsuf.add("rg");
		o_fnsuf.add("gore");
		o_fnsuf.add("zragore");
		o_fnsuf.add("nris");
		o_fnsuf.add("sar");
		o_fnsuf.add("risar");
		o_fnsuf.add("rn");
		o_fnsuf.add("gore");
		o_fnsuf.add("m");
		o_fnsuf.add("rn");
		o_fnsuf.add("t");
		o_fnsuf.add("ll");
		o_fnsuf.add("k");
		o_fnsuf.add("lar");
		o_fnsuf.add("r");
		o_fnsuf.add("taur");
		o_fnsuf.add("taxe");
		o_fnsuf.add("lkis");
		o_fnsuf.add("labar");
		o_fnsuf.add("bar");
		o_fnsuf.add("jas");
		o_fnsuf.add("lrajas");
		o_fnsuf.add("lmaran");
		o_fnsuf.add("ran");
		o_fnsuf.add("kazahn");
		o_fnsuf.add("zahn");
		o_fnsuf.add("hn");
		o_fnsuf.add("lar");
		o_fnsuf.add("tilar");
		o_fnsuf.add("ktilar");
		o_fnsuf.add("zilkree");
		o_fnsuf.add("kree");
		o_fnsuf.add("lkree");
		o_fnsuf.add("jin");
		o_fnsuf.add("jinn");
		o_fnsuf.add("shakar");
		o_fnsuf.add("jar");
		o_fnsuf.add("ramar");
		o_fnsuf.add("kus");
		o_fnsuf.add("sida");
		o_fnsuf.add("Worm");
	}
	
	public static String generateNickname() {
		Random rnd = new Random(System.currentTimeMillis());
		int race_selection = rnd.nextInt(3);

		String thefirstname = "";
		String thelastname = "";
		String thefirstname1 = "";
		String thelastname1 = "";
		
		if (race_selection == 0) {
			double fnprefix1 = Math.floor(Math.random() * 122);
			double fnsuffix1 = Math.floor(Math.random() * 91);
			double lnprefix1 = Math.floor(Math.random() * 67);
			double lnsuffix1 = Math.floor(Math.random() * 64);
			
			thefirstname = h_fnpre.get((int) fnprefix1) + h_fnsuf.get((int) fnsuffix1);
			thelastname = h_lnpre.get((int) lnprefix1) + h_lnsuf.get((int) lnsuffix1);

			thefirstname1 = thefirstname.substring(0,1).toUpperCase();
			thefirstname = thefirstname1 + thefirstname.substring(1, thefirstname.length());

			thelastname1 = thelastname.substring(0,1).toUpperCase();
			thelastname = thelastname1 + thelastname.substring(1, thelastname.length());
			
		} else if (race_selection == 0) {
			double fnprefix1 = Math.floor(Math.random() * 80);
			double fnsuffix1 = Math.floor(Math.random() * 67);

			thefirstname = o_fnpre.get((int) fnprefix1) + o_fnsuf.get((int) fnsuffix1);
			thelastname = "";
			
		} else {
			double fnprefix1 = Math.floor(Math.random() * 122);
			double fnsuffix1 = Math.floor(Math.random() * 91);

			thefirstname = h_fnpre.get((int) fnprefix1) + h_fnsuf.get((int) fnsuffix1);
			
		}
		
		return thefirstname + " " + thelastname;
	}
}
