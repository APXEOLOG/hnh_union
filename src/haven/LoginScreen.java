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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import union.APXUtils;
import union.APXUtils.AccountInfo;

public class LoginScreen extends Widget {
	Login cur;
	Text error;
	IButton btn;
	static Text.Foundry textf, textfs;
	Tex bg = Resource.loadtex("gfx/loginscr");
	Tex logo = Resource.loadtex("gfx/logo");
	Text progress = null;

	static {
		textf = new Text.Foundry(new java.awt.Font("Sans", java.awt.Font.PLAIN,
				16));
		textfs = new Text.Foundry(new java.awt.Font("Sans",
				java.awt.Font.PLAIN, 14));
	}

	/* Login saving */
	static LoginScreen instance;
	public static ArrayList<Button> login_btns = new ArrayList<Button>();
	public static ArrayList<Button> del_btns = new ArrayList<Button>();
	
	static Comparator<AccountInfo> comparator = new Comparator<AccountInfo>() {
	    public int compare(AccountInfo c1, AccountInfo c2) {
	        return c1.login.compareToIgnoreCase(c2.login);
	    }
	};

	
	public static void spawnLoginButtons() {
		for (Button btn : login_btns) {
			btn.unlink();
			btn.destroy();
			btn = null;
		}
		login_btns.clear();
		for (Button btn : del_btns) {
			btn.unlink();
			btn.destroy();
			btn = null;
		}
		del_btns.clear();

		int i = 0;
		int j = 0;
		
		Collection<AccountInfo> accColl = APXUtils.accounts.values();
		List<AccountInfo> accList = new ArrayList<AccountInfo>(accColl);
		Collections.sort(accList, comparator);
		
		Iterator<APXUtils.AccountInfo> iterator = accList.iterator();
		while (iterator.hasNext()) {
			if(j == 20) j = 0;
			APXUtils.AccountInfo info = iterator.next();
			Button btn = new Button(Coord.z.add(0 + 140 * (i/20), j * 30), 100, instance,
					info.login) {
				@Override
				public void click() {
					APXUtils.AccountInfo info = (APXUtils.AccountInfo) Info;
					// fromWidget = true;
					instance.wdgmsg("forget");
					instance.wdgmsg(instance, "login", new Object[] {
							info.login, info.password, false });
				}
			};
			btn.Info = info;
			login_btns.add(btn);
			Button btn_del = new Button(Coord.z.add(105 + 140 * (i/20), j * 30), 15, instance,
					"X") {
				public void click() {
					APXUtils.AccountInfo info = (APXUtils.AccountInfo) Info;
					APXUtils._sa_delete_account(info.login);
					LoginScreen.spawnLoginButtons();
				}
			};
			btn_del.Info = info;
			del_btns.add(btn_del);
			i++;
			j++;
		}
	}

	/* End of login saving */

	public LoginScreen(Widget parent) {
		super(Coord.z, new Coord(800, 600), parent);
		setfocustab(true);
		parent.setfocus(this);
		new Img(Coord.z, bg, this);
		new Img(new Coord(420, 215).add(logo.sz().div(2).inv()), logo, this);
		instance = this;
		spawnLoginButtons();
	}
	
	//Kerri: stoled from APX
	//may fails.
	public static boolean login(String charname) {
		if (instance == null) return false;
		Iterator<APXUtils.AccountInfo> iterator = APXUtils.accounts.values().iterator();
		while (iterator.hasNext()) {
			APXUtils.AccountInfo info = iterator.next();
			if (info.login.equals(charname)) {
				instance.wdgmsg("forget");
				instance.wdgmsg(instance, "login", new Object[] { info.login, info.password, false });
				return true;
			}
		}
		return false;
	}

	private static abstract class Login extends Widget {
		private Login(Coord c, Coord sz, Widget parent) {
			super(c, sz, parent);
		}

		abstract Object[] data();

		abstract boolean enter();
	}

	private class Pwbox extends Login {
		TextEntry user, pass;
		CheckBox savepass;

		private Pwbox(String username, boolean save) {
			super(new Coord(620, 310), new Coord(150, 150), LoginScreen.this);
			setfocustab(true);
			new Label(new Coord(0, 0), this, "User name", textf);
			user = new TextEntry(new Coord(0, 20), new Coord(150, 20), this,
					username);
			new Label(new Coord(0, 60), this, "Password", textf);
			pass = new TextEntry(new Coord(0, 80), new Coord(150, 20), this, "");
			pass.pw = true;
			savepass = new CheckBox(new Coord(0, 110), this, "Remember me");
			savepass.a = save;
			if (user.text.equals(""))
				setfocus(user);
			else
				setfocus(pass);
		}

		public void wdgmsg(Widget sender, String name, Object... args) {
		}

		Object[] data() {
			Object[] data = new Object[] { user.text, pass.text, savepass.a };
			APXUtils._sa_add_data(data);
			return data;
		}

		boolean enter() {
			if (user.text.equals("")) {
				setfocus(user);
				return (false);
			} else if (pass.text.equals("")) {
				setfocus(pass);
				return (false);
			} else {
				return (true);
			}
		}
	}

	private class Tokenbox extends Login {
		Text label;
		Button btn;

		private Tokenbox(String username) {
			super(new Coord(295, 310), new Coord(250, 100), LoginScreen.this);
			label = textfs.render("Identity is saved for " + username,
					java.awt.Color.WHITE);
			btn = new Button(new Coord(75, 30), 100, this, "Forget me");
		}

		Object[] data() {
			return (new Object[0]);
		}

		boolean enter() {
			return (true);
		}

		public void wdgmsg(Widget sender, String name, Object... args) {
			if (sender == btn) {
				LoginScreen.this.wdgmsg("forget");
				return;
			}
			super.wdgmsg(sender, name, args);
		}

		public void draw(GOut g) {
			g.image(label.tex(), new Coord((sz.x / 2) - (label.sz().x / 2), 0));
			super.draw(g);
		}
	}

	private void mklogin() {
		synchronized (ui) {
			btn = new IButton(new Coord(680, 460), this,
					Resource.loadimg("gfx/hud/buttons/loginu"),
					Resource.loadimg("gfx/hud/buttons/logind"));
			progress(null);
		}
	}

	private void error(String error) {
		synchronized (ui) {
			if (this.error != null)
				this.error = null;
			if (error != null)
				this.error = textf.render(error, java.awt.Color.RED);
		}
	}

	private void progress(String p) {
		synchronized (ui) {
			if (progress != null)
				progress = null;
			if (p != null)
				progress = textf.render(p, java.awt.Color.WHITE);
		}
	}

	private void clear() {
		if (cur != null) {
			ui.destroy(cur);
			cur = null;
			ui.destroy(btn);
			btn = null;
		}
		progress(null);
	}

	public void wdgmsg(Widget sender, String msg, Object... args) {
		if (sender == btn) {
			if (cur.enter())
				super.wdgmsg("login", cur.data());
			return;
		}
		super.wdgmsg(sender, msg, args);
	}

	public void uimsg(String msg, Object... args) {
		synchronized (ui) {
			if (msg == "passwd") {
				clear();
				cur = new Pwbox((String) args[0], (Boolean) args[1]);
				mklogin();
			} else if (msg == "token") {
				clear();
				cur = new Tokenbox((String) args[0]);
				mklogin();
			} else if (msg == "error") {
				error((String) args[0]);
			} else if (msg == "prg") {
				error(null);
				clear();
				progress((String) args[0]);
			}
		}
	}

	public void draw(GOut g) {
		c = MainFrame.getCenterPoint().sub(400, 300);
		super.draw(g);
		if (error != null)
			g.image(error.tex(), new Coord(420 - (error.sz().x / 2), 500));
		if (progress != null)
			g.image(progress.tex(), new Coord(420 - (progress.sz().x / 2), 350));
	}

	public boolean type(char k, java.awt.event.KeyEvent ev) {
		if (k == 10) {
			if ((cur != null) && cur.enter())
				wdgmsg("login", cur.data());
			return (true);
		}
		return (super.type(k, ev));
	}
}
