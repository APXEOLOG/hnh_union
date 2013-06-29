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

import haven.CharWnd.Study;

import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

import union.JSBotUtils;

public class UI {
	
	static public UI instance;
	public RootWidget root;
	public SlenHud slenhud;
	public MenuGrid menugrid;
	public Speedget speedget;
	public Study wnd_study;
	public WikiBrowser wiki;
	public MapView mapview;
	public MinimapPanel minimappanel;
	public MiniMap minimap = null;
	public FlowerMenu popupMenu = null;
	public Equipory equip;
	public Makewindow make_window;
	public CharWnd wnd_char;
	public Fightview fight;
	
	private Widget keygrab, mousegrab;
	public Map<Integer, Widget> widgets = new TreeMap<Integer, Widget>();
	public Map<Widget, Integer> rwidgets = new HashMap<Widget, Integer>();
	private Receiver rcvr;
	public Coord mc = new Coord(0, 0), lcc = Coord.z; // There was an epic bug with uninitialized "mc"
	public Session sess;
	public IHWindowParent chat;
	
	public MapView mainview; // Ender landwindow FCK FCK FCK
	
	public boolean modshift, modctrl, modmeta, modsuper;
	long lastevent = System.currentTimeMillis();
	public Widget mouseon;
	public FSMan fsm;
	public Console cons = new WidgetConsole();
	private Collection<AfterDraw> afterdraws = null;

	public interface Receiver {
		public void rcvmsg(int widget, String msg, Object... args);
	}

	public interface AfterDraw {
		public void draw(GOut g);
	}

	private class WidgetConsole extends Console {
		{
			setcmd("q", new Command() {
				public void run(Console cons, String[] args) {
					HackThread.tg().interrupt();
				}
			});
			setcmd("lo", new Command() {
				public void run(Console cons, String[] args) {
					sess.close();
				}
			});
			setcmd("fs", new Command() {
				public void run(Console cons, String[] args) {
					if ((args.length >= 2) && (fsm != null)) {
						if (Utils.atoi(args[1]) != 0)
							fsm.setfs();
						else
							fsm.setwnd();
					}
				}
			});
		}

		private void findcmds(Map<String, Command> map, Widget wdg) {
			if (wdg instanceof Directory) {
				Map<String, Command> cmds = ((Directory) wdg).findcmds();
				synchronized (cmds) {
					map.putAll(cmds);
				}
			}
			for (Widget ch = wdg.child; ch != null; ch = ch.next)
				findcmds(map, ch);
		}

		public Map<String, Command> findcmds() {
			Map<String, Command> ret = super.findcmds();
			findcmds(ret, root);
			return (ret);
		}
	}

	@SuppressWarnings("serial")
	public static class UIException extends RuntimeException {
		public String mname;
		public Object[] args;

		public UIException(String message, String mname, Object... args) {
			super(message);
			this.mname = mname;
			this.args = args;
		}
	}

	public UI(Coord sz, Session sess) {
		instance = this;
		root = new RootWidget(this, sz);
		widgets.put(0, root);
		rwidgets.put(root, 0);
		this.sess = sess;
	}

	public void setreceiver(Receiver rcvr) {
		this.rcvr = rcvr;
	}

	public void bind(Widget w, int id) {
		widgets.put(id, w);
		rwidgets.put(w, id);
	}

	public void drawafter(AfterDraw ad) {
		synchronized (afterdraws) {
			afterdraws.add(ad);
		}
	}

	public void draw(GOut g) {
		afterdraws = new LinkedList<AfterDraw>();
		root.draw(g);
		synchronized (afterdraws) {
			for (AfterDraw ad : afterdraws) {
				ad.draw(g);
			}
		}
		afterdraws = null;
	}

	public void newwidget(int id, String type, Coord c, int parent,
			Object... args) throws InterruptedException {
		WidgetFactory f;
		if (type.indexOf('/') >= 0) {
			int ver = -1, p;
			if ((p = type.indexOf(':')) > 0) {
				ver = Integer.parseInt(type.substring(p + 1));
				type = type.substring(0, p);
			}
			Resource res = Resource.load(type, ver);
			res.loadwaitint();
			f = res.layer(Resource.CodeEntry.class).get(WidgetFactory.class);
		} else {
			f = Widget.gettype(type);
		}
		synchronized (this) {
			Widget pwdg = widgets.get(parent);
			if (pwdg == null)
				throw (new UIException("Null parent widget " + parent + " for "
						+ id, type, args));
			
			Widget wdg = f.create(c, pwdg, args);
			
			JSBotUtils.OnWidgetRecieve(wdg, id, type);
			bind(wdg, id);
			wdg.binded();
			if (wdg instanceof MapView) {
				mainview = mapview = (MapView) wdg;
			} 
		}
	}

	public void grabmouse(Widget wdg) {
		mousegrab = wdg;
	}

	public void grabkeys(Widget wdg) {
		keygrab = wdg;
	}

	private void removeid(Widget wdg) {
		if (rwidgets.containsKey(wdg)) {
			int id = rwidgets.get(wdg);
			widgets.remove(id);
			rwidgets.remove(wdg);
		}
		for (Widget child = wdg.child; child != null; child = child.next)
			removeid(child);
	}

	public void destroy(Widget wdg) {
		if ((mousegrab != null) && mousegrab.hasparent(wdg))
			mousegrab = null;
		if ((keygrab != null) && keygrab.hasparent(wdg))
			keygrab = null;
		wdg.destroy();
		wdg.unlink();
		removeid(wdg);
	}
	
	public Widget getWidget(int id) {
		synchronized (this) {
			if (widgets.containsKey(id)) {
				return widgets.get(id);
			}
		}
		return null;
	}
	
	public int getId(Widget wdg) {
		synchronized (this) {
			if (rwidgets.containsKey(wdg)) {
				return rwidgets.get(wdg);
			}
		}
		return -1;
	}

	public void destroy(int id) {
		synchronized (this) {
			if (widgets.containsKey(id)) {
				Widget wdg = widgets.get(id);
				JSBotUtils.OnWidgetRemove(wdg, id);
				destroy(wdg);
			}
		}
	}

	public void wdgmsg(Widget sender, String msg, Object... args) {
		int id;
		synchronized (this) {
			if (!rwidgets.containsKey(sender)) return;
			id = rwidgets.get(sender);
		}
		if (rcvr != null)
			rcvr.rcvmsg(id, msg, args);
	}

	public void uimsg(int id, String msg, Object... args) {
		Widget wdg;
		synchronized (this) {
			wdg = widgets.get(id);
		}
		if (wdg != null) {
			wdg.uimsg(msg.intern(), args);
			JSBotUtils.OnWidgetUpdate(wdg, id, msg.intern(), args);
		}
		else
			throw (new UIException("Uimsg to non-existent widget " + id, msg,
					args));
	}

	private void setmods(InputEvent ev) {
		int mod = ev.getModifiersEx();
		modshift = (mod & InputEvent.SHIFT_DOWN_MASK) != 0;
		modctrl = (mod & InputEvent.CTRL_DOWN_MASK) != 0;
		modmeta = (mod & (InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK)) != 0;
		/*
		 * modsuper = (mod & InputEvent.SUPER_DOWN_MASK) != 0;
		 */
	}

	public void type(KeyEvent ev) {
		setmods(ev);
		if (keygrab == null) {
			if (!root.type(ev.getKeyChar(), ev))
				root.globtype(ev.getKeyChar(), ev);
		} else {
			keygrab.type(ev.getKeyChar(), ev);
		}
	}

	public void keydown(KeyEvent ev) {
		setmods(ev);
		if (keygrab == null) {
			if (!root.keydown(ev))
				root.globtype((char) 0, ev);
		} else {
			keygrab.keydown(ev);
		}
	}

	public void keyup(KeyEvent ev) {
		setmods(ev);
		if (keygrab == null)
			root.keyup(ev);
		else
			keygrab.keyup(ev);
	}

	private Coord wdgxlate(Coord c, Widget wdg) {
		return (c.add(wdg.c.inv()).add(wdg.parent.rootpos().inv()));
	}

	public boolean dropthing(Widget w, Coord c, Object thing) {
		if (w instanceof DropTarget) {
			if (((DropTarget) w).dropthing(c, thing))
				return (true);
		}
		for (Widget wdg = w.lchild; wdg != null; wdg = wdg.prev) {
			Coord cc = w.xlate(wdg.c, true);
			if (c.isect(cc, wdg.sz)) {
				if (dropthing(wdg, c.add(cc.inv()), thing))
					return (true);
			}
		}
		return (false);
	}

	long last_newwidget_time = 0;

	public void update(long dt) {
		if (mapview == null)
			last_newwidget_time = System.currentTimeMillis();
		root.update(dt);
	}

	public void mousedown(MouseEvent ev, Coord c, int button) {
		setmods(ev);
		lcc = mc = c;
		if (mousegrab == null)
			root.mousedown(c, button);
		else
			mousegrab.mousedown(wdgxlate(c, mousegrab), button);
	}

	public void mouseup(MouseEvent ev, Coord c, int button) {
		setmods(ev);
		mc = c;
		if (mousegrab == null)
			root.mouseup(c, button);
		else
			mousegrab.mouseup(wdgxlate(c, mousegrab), button);
	}

	public void mousemove(MouseEvent ev, Coord c) {
		setmods(ev);
		mc = c;
		if (mousegrab == null)
			root.mousemove(c);
		else
			mousegrab.mousemove(wdgxlate(c, mousegrab));
	}

	public void mousewheel(MouseEvent ev, Coord c, int amount) {
		setmods(ev);
		lcc = mc = c;
		if (mousegrab == null)
			root.mousewheel(c, amount);
		else
			mousegrab.mousewheel(wdgxlate(c, mousegrab), amount);
	}

	public int modflags() {
		return ((modshift ? 1 : 0) | (modctrl ? 2 : 0) | (modmeta ? 4 : 0) | (modsuper ? 8
				: 0));
	}
}
