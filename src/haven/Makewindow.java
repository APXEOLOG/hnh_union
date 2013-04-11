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

import java.util.*;
import java.awt.Color;
import java.awt.Font;

public class Makewindow extends HWindow {
	public boolean is_ready = false;
	public String craft_name = "";
	Widget obtn, cbtn;
	List<Widget> inputs;
	List<Widget> outputs;
	static Coord boff = new Coord(7, 9);
	public static final Text.Foundry nmf = new Text.Foundry(new Font("Serif",
			Font.PLAIN, 20));

	static {
		Widget.addtype("make", new WidgetFactory() {
			public Widget create(Coord c, Widget parent, Object[] args) {
				return (new Makewindow(parent, (String) args[0]));
			}
		});
	}

	public Makewindow(Widget parent, String rcpnm) {
		super(parent, "Crafting", true);
		ui.make_window = this;
		Label nm = new Label(new Coord(10, 10), this, rcpnm, nmf);
		nm.setcolor(Color.black); //Kerri
		craft_name = rcpnm;
		nm.c = new Coord(sz.x - 10 - nm.sz.x, 10);
		Label iLabel = new Label(new Coord(10, 18), this, "Input:");
		Label rLabel = new Label(new Coord(10, 73), this, "Result:");
		iLabel.setcolor(Color.black); //Kerri
		rLabel.setcolor(Color.black); //Kerri
		obtn = new Button(new Coord(290, 71), 60, this, "Craft");
		cbtn = new Button(new Coord(360, 71), 60, this, "Craft All");
	}
	
	@Override
	public void unlink(){
		super.unlink();
		UI.instance.make_window = null;
	}

	public void uimsg(String msg, Object... args) {
		if (msg == "pop") {
			is_ready = true;
			final int xoff = 50;
			if (inputs != null) {
				for (Widget w : inputs)
					w.unlink();
				for (Widget w : outputs)
					w.unlink();
			}
			inputs = new LinkedList<Widget>();
			outputs = new LinkedList<Widget>();
			int i;
			Coord c = new Coord(xoff, 10);
			for (i = 0; (Integer) args[i] >= 0; i += 2) {
				Widget box = new Inventory(c, new Coord(1, 1), this);
				inputs.add(box);
				c = c.add(new Coord(31, 0));
				new Item(Coord.z, (Integer) args[i], -1, box, null,
						(Integer) args[i + 1]);
			}
			c = new Coord(xoff, 65);
			for (i++; (i < args.length) && ((Integer) args[i] >= 0); i += 2) {
				Widget box = new Inventory(c, new Coord(1, 1), this);
				outputs.add(box);
				c = c.add(new Coord(31, 0));
				new Item(Coord.z, (Integer) args[i], -1, box, null,
						(Integer) args[i + 1]);
			}
		}
	}

	public void wdgmsg(Widget sender, String msg, Object... args) {
		if (sender == obtn) {
			if (msg == "activate")
				wdgmsg("make", 0);
			return;
		}
		if (sender == cbtn) {
			if (msg == "activate")
				wdgmsg("make", 1);
			return;
		}
		if (sender instanceof Item)
			return;
		if (sender instanceof Inventory)
			return;
		super.wdgmsg(sender, msg, args);
	}

	public boolean globtype(char ch, java.awt.event.KeyEvent ev) {
		if (ch == '\n') {
			wdgmsg("make", ui.modctrl ? 1 : 0);
			return (true);
		}
		return (super.globtype(ch, ev));
	}
}
