/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import haven.Resource.AButton;
import java.util.*;

import javax.imageio.ImageIO;

import union.APXUtils;
import union.CustomMenu;
import union.JSBotUtils;
import union.CustomMenu.MenuElemetUseListener;

public class MenuGrid extends Window {
	public final static Tex bg = Resource.loadtex("gfx/hud/invsq");
	public final static Coord bgsz = bg.sz().add(-1, -1); //grid spacing
	public final static Resource menu_next_tab = Resource.load("gfx/hud/sc-next");
	public final static Resource menu_back_to_previous = Resource.load("gfx/hud/sc-back");
	public final static RichText.Foundry ttfnd = new RichText.Foundry(
			TextAttribute.FAMILY, "SansSerif", TextAttribute.SIZE, 10);
	private static Coord gsz = new Coord(4, 4);//grid size (slots: columns, rows)
	private Resource menu_current_parent_resource, pressed, dragging,
			layout[][] = new Resource[200][200];
	private int menu_current_layer_elements_offset = 0;
	private Map<Character, Resource> hotmap = new TreeMap<Character, Resource>();
	public ArrayList<ToolbarWnd> toolbars = new ArrayList<ToolbarWnd>();
	public ToolbarWnd digitbar;
	public ToolbarWnd functionbar;
	public ToolbarWnd numpadbar;
	public HashMap<String, MenuElemetUseListener> listeners = new HashMap<String, MenuElemetUseListener>();
	static final BufferedImage grip = Resource.loadimg("gfx/hud/gripbr");
	static final Coord gzsz = new Coord(16, 17);
	boolean rsm = false;
	static final Coord minsz = new Coord(bgsz.x * 4 + 30, bgsz.y * 4 + 28);

	static {
		Widget.addtype("scm", new WidgetFactory() {
			public Widget create(Coord c, Widget parent, Object[] args) {
				Coord size = new Coord(bgsz.x * (gsz.x - 1) + 30, bgsz.y * (gsz.y - 1) + 15);
				MenuGrid ret = new MenuGrid(c, size, parent);
				return ret;
			}
		});
	}

	public class PaginaException extends RuntimeException {
		public Resource res;

		public PaginaException(Resource r) {
			super("Invalid pagina: " + r.name);
			res = r;
		}
	}

	private Resource[] getSubResources(Resource p) {
		Resource[] cp = new Resource[0];
		Resource[] all;
		{
			Collection<Resource> ta = new HashSet<Resource>();
			Collection<Resource> open;
			synchronized (ui.sess.glob.paginae) {
				open = new HashSet<Resource>(ui.sess.glob.paginae);
			}
			while (!open.isEmpty()) {
				for (Resource r : open.toArray(cp)) {
					if (!r.loading) {
						AButton ad = r.layer(Resource.action);
						if (ad == null)
							throw (new PaginaException(r));
						if ((ad.parent != null) && !ta.contains(ad.parent))
							open.add(ad.parent);
						ta.add(r);
						open.remove(r);
					}
				}
			}
			all = ta.toArray(cp);
		}
		Collection<Resource> tobe = new HashSet<Resource>();
		for (Resource r : all) {
			if (r.layer(Resource.action).parent == p)
				tobe.add(r);
		}
		return (tobe.toArray(cp));
	}

	public MenuGrid(Coord c, Coord size, Widget parent) {
		super(c, size, UI.instance.root, "Menu");
		getSubResources(null);
		ui.menugrid = this;
		APXUtils.addMenu();
		ToolbarWnd.loadBelts();
		fbtn.visible = true;
		cbtn.visible = false;
		loadpos();
		oldSize = this.sz;
		recalcSize(this.sz);
		fbtn.c.x = this.sz.x - 38;
		if(Config.toggleCA)
			JSBotUtils.sendAction("crime");
		/* Load Toolbars */
		try {
			Properties tbprops = new Properties();
			tbprops.load(new FileInputStream("toolbars.conf"));
			int count = Integer.parseInt(tbprops.getProperty("toolbars_count"));
			for (int i = 0; i < count; i++) {
				String name = "toolbar" + (i + 1);
				String barinfo = tbprops.getProperty(name);
				String[] tbinfo = barinfo.split("\\|");
				toolbars.add(new ToolbarWnd(new Coord(0, i * 100), ui.root, name, tbinfo));
			}
		} catch (Exception e) {
			e.printStackTrace();
			toolbars.clear();
			digitbar = new ToolbarWnd(new Coord(0, 300), ui.root, "toolbar1");
			functionbar = new ToolbarWnd(new Coord(50, 300), ui.root, "toolbar2",
					2, KeyEvent.VK_F1, 12, new Coord(4, 10));
			numpadbar = new ToolbarWnd(new Coord(100, 300), ui.root, "toolbar3",
					10, KeyEvent.VK_NUMPAD0) {
				protected void nextBelt() {
					loadBelt((belt + 1) % 5 + 10);
				}

				protected void prevBelt() {
					loadBelt((belt - 1) % 5 + 10);
				}
			};
		}
	}

	private static Comparator<Resource> sorter = new Comparator<Resource>() {
		public int compare(Resource a, Resource b) {
			AButton aa = a.layer(Resource.action), ab = b
					.layer(Resource.action);
			if ((aa.ad.length == 0) && (ab.ad.length > 0))
				return (-1);
			if ((aa.ad.length > 0) && (ab.ad.length == 0))
				return (1);
			return (aa.name.compareTo(ab.name));
		}
	};

	private void updlayout() {
		Resource[] cur = getSubResources(this.menu_current_parent_resource);
		Arrays.sort(cur, sorter);
		int i;
		hotmap.clear();
		for (i = 0; i < cur.length; i++) {
			Resource.AButton ad = cur[i].layer(Resource.action);
			if (ad.hk != 0)
				hotmap.put(Character.toUpperCase(ad.hk), cur[i]);
		}
		i = menu_current_layer_elements_offset;
		for (int y = 0; y < gsz.y; y++) {
			for (int x = 0; x < gsz.x; x++) {
				Resource btn = null;
				if ((this.menu_current_parent_resource != null) && (x == gsz.x - 1) && (y == gsz.y - 1)) {
					btn = menu_back_to_previous;
				} else if ((cur.length > ((gsz.x * gsz.y) - 1))
						&& (x == gsz.x - 2) && (y == gsz.y - 1)) {
					btn = menu_next_tab;
				} else if (i < cur.length) {
					btn = cur[i++];
				}
				layout[x][y] = btn;
			}
		}
	}

	private static Text rendertt(Resource res, boolean withpg) {
		Resource.AButton ad = res.layer(Resource.action);
		Resource.Pagina pg = res.layer(Resource.pagina);
		String tt = ad.name;
		int pos = tt.toUpperCase().indexOf(Character.toUpperCase(ad.hk));
		if (pos >= 0)
			tt = tt.substring(0, pos) + "$col[255,255,0]{" + tt.charAt(pos)
					+ "}" + tt.substring(pos + 1);
		else if (ad.hk != 0)
			tt += " [$col[255,255,0]{" + ad.hk + "}]";
		if (withpg && (pg != null)) {
			tt += "\n\n" + pg.text;
		}
		return (ttfnd.render(tt, 0));
	}

	public void draw(GOut g) {
		super.draw(g);
		if(folded) return;
		updlayout();
		for (int y = 0; y < gsz.y; y++) {
			for (int x = 0; x < gsz.x; x++) {
				Coord p = bgsz.mul(new Coord(x, y));
				p = p.add(10, 15);
				g.image(bg, p);
				Resource btn = layout[x][y];
				if (btn != null) {
					Tex btex = btn.layer(Resource.imgc).tex();
					g.image(btex, p.add(1, 1));
					if (btn == pressed) {
						g.chcolor(new Color(0, 0, 0, 128));
						g.frect(p.add(1, 1), btex.sz());
						g.chcolor();
					}
				}
			}
		}
		if (dragging != null) {
			final Tex dt = dragging.layer(Resource.imgc).tex();
			ui.drawafter(new UI.AfterDraw() {
				public void draw(GOut g) {
					g.image(dt, ui.mc.add(dt.sz().div(2).inv()));
				}
			});
		}
		if(!folded)
		    g.image(grip, sz.sub(gzsz));
	}

	private Resource curttr = null;
	private boolean curttl = false;
	private Text curtt = null;
	private long hoverstart;

	public Object tooltip(Coord c, boolean again) {
		Resource res = bhit(c);
		long now = System.currentTimeMillis();
		if ((res != null) && (res.layer(Resource.action) != null)) {
			if (!again)
				hoverstart = now;
			boolean ttl = (now - hoverstart) > 500;
			if ((res != curttr) || (ttl != curttl)) {
				curtt = rendertt(res, ttl);
				curttr = res;
				curttl = ttl;
			}
			return (curtt);
		} else {
			hoverstart = now;
			return ("");
		}
	}

	private Resource bhit(Coord c) {
		Coord bc = c.add(-10, -15).div(bgsz);
		if ((bc.x >= 0) && (bc.y >= 0) && (bc.x < gsz.x) && (bc.y < gsz.y))
			return (layout[bc.x][bc.y]);
		else
			return (null);
	}
	
	Resource right_click_pressed = null;
	public boolean mousedown(Coord c, int button) {
		if(folded) {
			//recalcSize(this.sz);
		    return super.mousedown(c, button);
		}
		parent.setfocus(this);
		raise();
		Resource h = bhit(c);
		if(button == 1){
			if(h != null) {
				pressed = h;
				ui.grabmouse(this);
				return true;
			}
			else {
				ui.grabmouse(this);
				doff = c;
				if(c.isect(sz.sub(gzsz), gzsz)){
					rsm = true;
					return true;
				}
			}
		} else if ((button == 3) && (h != null)) {
			right_click_pressed = h;
			return true;
		}
		return super.mousedown(c, button);
	}

	public void mousemove(Coord c) {
		if (rsm){
			Coord d = c.sub(doff);
			this.sz = this.sz.add(d);
			this.sz.x = Math.max(minsz.x, this.sz.x);
			this.sz.y = Math.max(minsz.y, this.sz.y);
			doff = c;
			recalcSize(this.sz);
			oldSize = this.sz;
			fbtn.c.x = this.sz.x - 38;
			gsz.x = (this.sz.x - 30)/(bgsz.x - 1);
			gsz.y = (this.sz.y - 30)/(bgsz.y - 1);
		} else {
			if ((dragging == null) && (pressed != null)) {
				Resource h = bhit(c);
				if (h != pressed)
					dragging = pressed;
				return;
			}else
				super.mousemove(c);
		}
	}

	public void use(Resource r) {
		if (r != null) {
			AButton btn = r.layer(Resource.action);
			if (btn != null) {
				String[] actts = btn.ad;
				if ((actts != null) && (actts.length > 0)
						&& actts[0].equals(CustomMenu.menu_prefix)) {
					if (actts.length > 1) {
						String id = actts[1];
						MenuElemetUseListener listener = listeners.get(id);
						if (listener != null) {
							listener.use(1);
						}
					}
				}
			}
		}
		if (getSubResources(r).length > 0) {
			menu_current_parent_resource = r;
			menu_current_layer_elements_offset = 0;
		} else if (r == menu_back_to_previous) {
			menu_current_parent_resource = menu_current_parent_resource.layer(Resource.action).parent;
			menu_current_layer_elements_offset = 0;
		} else if (r == menu_next_tab) {
			if ((menu_current_layer_elements_offset + (gsz.x*gsz.y - 2) ) >= getSubResources(menu_current_parent_resource).length)
				menu_current_layer_elements_offset = 0;
			else
				menu_current_layer_elements_offset += (gsz.x*gsz.y - 2);
		} else {
			AButton abtn = r.layer(Resource.action);
			if (abtn == null) return;
			String[] actions = abtn.ad;
			if (actions[0].equals("@")) {
				usecustom(actions);
				// } else if (ad[0].equals("declaim")){
				// new DeclaimVerification(ui.root, ad);
			} else {
				int k = 0;
				if (actions[0].equals("crime")) {
					k = -1;
				}
				if (actions[0].equals("tracking")) {
					k = -2;
				}
				if (actions[0].equals("swim")) {
					k = -3;
				}
				if (k < 0) {
					synchronized (ui.sess.glob.buffs) {
						if (ui.sess.glob.buffs.containsKey(k)) {
							ui.sess.glob.buffs.remove(k);
						} else {
							Buff buff = new Buff(k, r.indir());
							buff.major = true;
							ui.sess.glob.buffs.put(k, buff);
						}
					}
				}
				//Kerri: debugging actions
				if(Config.mgridDebug) {
					String[] ss = r.layer(Resource.action).ad;
					String s = "";
					for (int i=0; i<ss.length; i++)
						s = s+ss[i]+", ";
					System.out.println("Act click: "+s);
				}
				//
				wdgmsg("act", (Object[]) actions);
			}
		}
	}

	private void usecustom(String[] list) {
		if (list[1].equals("radius")) {
			Config.showRadius = !Config.showRadius;
			String str = "Radius highlight is turned "
					+ ((Config.showRadius) ? "ON" : "OFF");
			ui.cons.out.println(str);
			ui.slenhud.error(str);
			Config.saveOptions();
		} else if (list[1].equals("hidden")) {
			Config.showHidden = !Config.showHidden;
			String str = "Hidden object highlight is turned "
					+ ((Config.showHidden) ? "ON" : "OFF");
			ui.cons.out.println(str);
			ui.slenhud.error(str);
			Config.saveOptions();
		} else if (list[1].equals("hide")) {
			for (int i = 2; i < list.length; i++) {
				String item = list[i];
				if (Config.hideObjectList.contains(item)) {
					Config.remhide(item);
				} else {
					Config.addhide(item);
				}
			}
		} else if (list[1].equals("simple plants")) {
			Config.simple_plants = !Config.simple_plants;
			String str = "Simplified plants is turned "
					+ ((Config.simple_plants) ? "ON" : "OFF");
			ui.cons.out.println(str);
			ui.slenhud.error(str);
			Config.saveOptions();
		} else if (list[1].equals("timers")) {
			TimerPanel.toggle();
		} else if (list[1].equals("animal")) {
			Config.showBeast = !Config.showBeast;
			String str = "Animal highlight is turned "
					+ ((Config.showBeast) ? "ON" : "OFF");
			ui.cons.out.println(str);
			ui.slenhud.error(str);
			Config.saveOptions();
		} else if (list[1].equals("study")) {
			if (ui.wnd_study != null)
				ui.wnd_study.toggle();
		} else if (list[1].equals("globalchat")) {
			ui.root.wdgmsg("gk", 3);
		} else if (list[1].equals("wiki")) {
			if (ui.wiki == null) {
				new WikiBrowser(MainFrame.getCenterPoint().sub(115, 75),
						Coord.z, ui.root);
			} else {
				ui.wiki.wdgmsg(ui.wiki.cbtn, "click");
			}
		}
		use(null);
	}
	
	private void loadpos(){
		synchronized (Config.window_props) {
			c = new Coord(Config.window_props.getProperty("menugrid_pos", c.toString()));
			this.sz = new Coord(Config.window_props.getProperty("menugrid_sz", this.sz.toString()));
			gsz.x = (this.sz.x - 30)/(bgsz.x - 1);
			gsz.y = (this.sz.y - 30)/(bgsz.y - 1);
		}
	}

	public boolean mouseup(Coord c, int button) {
		if(dm){
		    Config.setWindowOpt("menugrid_pos", this.c.toString());
		}
		if (rsm){
		    ui.grabmouse(null);
		    rsm = false;
		    Config.setWindowOpt("menugrid_sz", this.sz.toString());
		}else{
		Resource h = bhit(c);
		if (button == 1) {
			if (dragging != null) {
				ui.dropthing(ui.root, ui.mc, dragging);
				dragging = pressed = null;
			} else if (pressed != null) {
				if (pressed == h)
					use(h);
				pressed = null;
			}
			ui.grabmouse(null);
			//return true;
		} else if (button == 3) {
			if (h == null) return true;
			if (right_click_pressed == h) {
				if (right_click_pressed.layer(Resource.action) != null) {
					String[] actions = right_click_pressed.layer(Resource.action).ad;
					if (actions != null && actions.length > 0 && actions[0].equals(CustomMenu.menu_prefix)) {
						if (actions.length > 1) {
							String id = actions[1];
							MenuElemetUseListener listener = listeners.get(id);
							if (listener != null) {
								listener.use(button);
							}
						}
					}
				}
			}
			return true;
		}
		updlayout();}
		return super.mouseup(c, button);
	}

	public void uimsg(String msg, Object... args) {
		if (msg == "goto") {
			String res = (String) args[0];
			if (res.equals(""))
				menu_current_parent_resource = null;
			else
				menu_current_parent_resource = Resource.load(res);
			menu_current_layer_elements_offset = 0;
		}
	}

	public boolean globtype(char k, KeyEvent ev) {
		if ((k == 27) && (this.menu_current_parent_resource != null)) {
			this.menu_current_parent_resource = null;
			menu_current_layer_elements_offset = 0;
			updlayout();
			return (true);
		} else if ((k == 'N') && (layout[gsz.x - 2][gsz.y - 1] == menu_next_tab)) {
			use(menu_next_tab);
			return (true);
		}
		Resource r = hotmap.get(Character.toUpperCase(k));
		if (r != null) {
			use(r);
			return (true);
		}
		return (false);
	}
}
