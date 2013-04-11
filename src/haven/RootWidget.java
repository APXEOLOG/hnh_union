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

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GLException;

import union.JSBotUtils;
import union.KerriUtils;

import com.sun.opengl.util.Screenshot;

public class RootWidget extends ConsoleHost {
	public static Resource defcurs = Resource.load("gfx/hud/curs/arw");
	Logout logout = null;
	Profile gprof;
	boolean afk = false;
	public static boolean screenshot = false;
	public static boolean names_ready = false;

	public RootWidget(UI ui, Coord sz) {
		super(ui, new Coord(0, 0), sz);
		setfocusctl(true);
		cursor = defcurs;
	}

	public boolean globtype(char key, KeyEvent ev) {
		if (!super.globtype(key, ev)) {
			int code = ev.getKeyCode();
			boolean ctrl = ev.isControlDown();
			boolean alt = ev.isAltDown();
			if (Config.profile && (key == '`')) {
				new Profwnd(ui.slenhud, ui.mapview.prof, "MV prof");
			} else if (code == KeyEvent.VK_ENTER) {
				JSBotUtils.doClick(JSBotUtils.findNearestMapObject(110), 3, 0);
			} else if (Config.profile && (key == '~')) {
				new Profwnd(ui.slenhud, gprof, "Glob prof");
			} else if (Config.profile && (key == '!')) {
				new Profwnd(ui.slenhud, ui.mapview.mask.prof, "ILM prof");
			} else if ((code == KeyEvent.VK_Y) && ctrl) {
				Config.render_enable = !Config.render_enable;
			} else if ((code == KeyEvent.VK_Z) && ctrl) {
				Config.assign_to_tile = !Config.assign_to_tile;
			} else if ((code == KeyEvent.VK_N) && ctrl) {
				Config.nightvision = !Config.nightvision;
			} else if ((code == KeyEvent.VK_X) && ctrl) {
				Config.xray = !Config.xray;
			} else if ((code == KeyEvent.VK_D) && ctrl) {
				Config.showDebug = !Config.showDebug;
			} else if ((code == KeyEvent.VK_H) && ctrl) {
				Config.hide = !Config.hide;
			} else if ((code == KeyEvent.VK_F) && ctrl) {
				UI.instance.mapview.resetcam();
			} else if ((code == KeyEvent.VK_Q) && alt) {
				ui.speedget.setspeed(0, true);
			} else if ((code == KeyEvent.VK_W) && alt) {
				ui.speedget.setspeed(1, true);
			} else if ((code == KeyEvent.VK_E) && alt) {
				ui.speedget.setspeed(2, true);
			} else if ((code == KeyEvent.VK_F12) && ui.modshift) {
				Config.mgridDebug = !Config.mgridDebug;
				System.out.println("Action debugging: "+Boolean.toString(Config.mgridDebug));
			} else if ((code == KeyEvent.VK_F11) && ui.modshift) {
				JSBotUtils.showDebugToConsole = !JSBotUtils.showDebugToConsole;
				System.out.println("Debugging: "+Boolean.toString(JSBotUtils.showDebugToConsole));
			} else if ((code == KeyEvent.VK_R) && alt) {
				ui.speedget.setspeed(3, true);
			} else if ((code == KeyEvent.VK_F9) && ui.modshift) {
					KerriUtils.clearOL();
			} else if ((code == KeyEvent.VK_M) && ctrl) {
				ui.minimappanel.fbtn.click();
			} else if ((code == KeyEvent.VK_L) && alt) {
				JSBotUtils.logoutChar();
			} else if ((code == KeyEvent.VK_A) && ctrl) {
				Config.showMyAvatar = !Config.showMyAvatar;
			} else if ((code == KeyEvent.VK_G) && ctrl) {
				Config.grid = !Config.grid;
			} else if (((int) key == 2) & ctrl) {// CTRL-B have code of 02
				BuddyWnd.instance.visible = !BuddyWnd.instance.visible;
			} else if (((int) key == 20) & ctrl) {// CTRL-T have code of 20
				if (UI.instance.wnd_char != null) {
					UI.instance.wnd_char.toggle();
				}
			} else if (code == KeyEvent.VK_HOME) {
				ui.mapview.resetcam();
			} else if (code == KeyEvent.VK_END) {
				screenshot = true;
			} else if (key == ':') {
				entercmd();
			} else if (key != 0) {
				wdgmsg("gk", (int) key);
			}
		}
		return (true);
	}

	public void draw(GOut g) {
		if (screenshot && Config.sshot_noui) {
			visible = false;
		}
		super.draw(g);
		drawcmd(g, new Coord(20, 580));
		if (screenshot && (!Config.sshot_nonames || names_ready)) {
			visible = true;
			screenshot = false;
			try {
				Coord s = MainFrame.getInnerSize();
				String stamp = Utils.sessdate(System.currentTimeMillis());
				String ext = Config.sshot_compress ? ".jpg" : ".png";
				File f = new File("screenshots/SS_" + stamp + ext);
				f.mkdirs();
				Screenshot.writeToFile(f, s.x, s.y);
			} catch (GLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// if(!afk && (System.currentTimeMillis() - ui.lastevent > 300000)) {
		// afk = true;
		// Widget slen = findchild(SlenHud.class);
		// if(slen != null)
		// slen.wdgmsg("afk");
		// } else if(afk && (System.currentTimeMillis() - ui.lastevent <
		// 300000)) {
		// afk = false;
		// }
	}

	public void error(String msg) {
	}
}
