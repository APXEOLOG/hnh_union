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

import haven.INIFile.Pair;

import java.awt.Color;
import java.util.*;

import union.APXUtils;

public class Gob implements Sprite.Owner {
	public Coord rc, sc;
	int clprio = 0;
	public int id, frame, initdelay = (int) (Math.random() * 3000);
	public final Glob glob;
	public Map<Class<? extends GAttrib>, GAttrib> attr = new HashMap<Class<? extends GAttrib>, GAttrib>();
	public Collection<Overlay> ols = new LinkedList<Overlay>();
	public boolean hide;
	HlFx highlight = null;
	private boolean drawOlay = false;
	
	public void setDrawOlay(boolean b) {
		drawOlay = b;
	}
	
	public boolean getDrawOlay() {
		return drawOlay;
	}

	public static class Overlay {
		public Indir<Resource> res;
		public Message sdt;
		public Sprite spr;
		public int id;
		public boolean delign = false;

		public Overlay(int id, Indir<Resource> res, Message sdt) {
			this.id = id;
			this.res = res;
			this.sdt = sdt;
			spr = null;
		}

		public static interface CDel {
			public void delete();
		}
	}

	public static class HlFx implements Sprite.Part.Effect {
		public long time;

		public HlFx(long t) {
			time = t;
		}

		public GOut apply(GOut in) {
			return (new GOut(in) {
				{
					chcolor();
				}

				public void chcolor(Color col) {
					double k = (System.currentTimeMillis() - time) / 1000.0;
					k = 1 + Math.cos(10 * k);
					super.chcolor(Utils.blendcol(col, new Color(64, 255, 64,
							(int) (111 * k))));
				}
			});
		}
	}

	public Gob(Glob glob, Coord c, int id, int frame) {
		this.glob = glob;
		this.rc = c;
		this.id = id;
		this.frame = frame;
	}

	public Gob(Glob glob, Coord c) {
		this(glob, c, 0, 0);
	}

	public static interface ANotif<T extends GAttrib> {
		public void ch(T n);
	}

	public void ctick(int dt) {
		int dt2 = dt + initdelay;
		initdelay = 0;
		for (GAttrib a : attr.values()) {
			if (a instanceof Drawable)
				a.ctick(dt2);
			else
				a.ctick(dt);
		}
		for (Iterator<Overlay> i = ols.iterator(); i.hasNext();) {
			Overlay ol = i.next();
			if (ol.spr == null) {
				if (((getattr(Drawable.class) == null) || (getneg() != null))
						&& (ol.res.get() != null))
					ol.spr = Sprite.create(this, ol.res.get(), ol.sdt);
			} else {
				boolean done = ol.spr.tick(dt);
				if ((!ol.delign || (ol.spr instanceof Overlay.CDel)) && done)
					i.remove();
			}
		}
	}
	
	public void dumpOls() {
		System.out.println("<resource>");
		for (Iterator<Overlay> i = ols.iterator(); i.hasNext();) {
			Overlay ol = i.next();
			System.out.println("<overlay>");
			if (ol.res.get() != null) {
				Resource r = ol.res.get();
				r.loadwait();
				r.dumpLayers();
			}
			System.out.println("</overlay>");
		}
		System.out.println("</resource>");
	}
	
	public Overlay findol(int id) {
		for (Overlay ol : ols) {
			if (ol.id == id)
				return (ol);
		}
		return (null);
	}
	
	public int getHealth() {
		GobHealth hlt = getattr(GobHealth.class);
		if (hlt != null) {
			return (int) (hlt.asfloat() * 100);
		}
		return -1;
	}

	public byte GetBlob(int index) {
		Drawable d = getattr(Drawable.class);
		ResDrawable dw = getattr(ResDrawable.class);
		if (dw != null && d != null) {
			if (index < dw.sdt.blob.length && index >= 0)
				return dw.sdt.blob[index];
		}
		return 0;
	}
	
	public int[] getBlob() {
		Drawable d = getattr(Drawable.class);
		ResDrawable dw = getattr(ResDrawable.class);
		if (dw != null && d != null) {
			int[] ret = new int[dw.sdt.blob.length];
			for (int i = 0; i < ret.length; i++)
				ret[i] = dw.sdt.blob[i];
			return ret;
		}
		return new int[] { 0 };
	}

	public void tick() {
		for (GAttrib a : attr.values())
			a.tick();
	}

	public void move(Coord c) {
		Moving m = getattr(Moving.class);
		if (m != null)
			m.move(c);
		this.rc = c;
	}

	/**
	 * 
	 * @return Real-coord position of this Gob
	 */
	public Coord position() {
		Moving m = getattr(Moving.class);
		if (m != null)
			return (m.getc());
		else
			return (rc);
	}

	private Class<? extends GAttrib> attrclass(Class<? extends GAttrib> cl) {
		while (true) {
			Class<?> p = cl.getSuperclass();
			if (p == GAttrib.class)
				return (cl);
			cl = p.asSubclass(GAttrib.class);
		}
	}

	public void setattr(GAttrib a) {
		Class<? extends GAttrib> ac = attrclass(a.getClass());
		attr.put(ac, a);
	}

	public <C extends GAttrib> C getattr(Class<C> c) {
		GAttrib attr = this.attr.get(attrclass(c));
		if (!c.isInstance(attr))
			return (null);
		return (c.cast(attr));
	}

	public void delattr(Class<? extends GAttrib> c) {
		attr.remove(attrclass(c));
	}

	public Coord drawoff() {
		Coord ret = Coord.z;
		DrawOffset dro = getattr(DrawOffset.class);
		if (dro != null)
			ret = ret.add(dro.off);
		Following flw = getattr(Following.class);
		if (flw != null)
			ret = ret.add(flw.doff);
		return (ret);
	}

	public void checkhide() {

	}

	public void drawsetup(Sprite.Drawer drawer, Coord dc, Coord sz) {
		Resource res = getres();
		hide = (res != null) && res.hide && res.once && !res.skiphide
				&& Config.hide;
		if (Config.hide_all) return;
		
		if (hide) return;
		if (res != null)
			res.once = true;

		Drawable d = getattr(Drawable.class);
		Coord dro = drawoff();
		for (Overlay ol : ols) {
			if (ol.spr != null) {
				ol.spr.setup(drawer, dc, dro);
			}
		}

		if (d != null) {
			d.setup(drawer, dc, dro);
		}
	}

	public Resource getres() {
		Resource res = null;
		ResDrawable dw = getattr(ResDrawable.class);
		if (dw != null) {
			res = dw.res.get();
		} else {
			Layered ld = getattr(Layered.class);
			if ((ld != null) && (ld.layers.size() > 0)) {
				res = ld.layers.get(0).get();
			}
		}
		return res;
	}

	public String resname() {
		Resource res = getres();
		String name = "";
		if (res != null) {
			name = res.name;
		}
		return name;
	}
	
	public String[] resnames(){
		List<String> names = new ArrayList<String>();
		ResDrawable dw = getattr(ResDrawable.class);
		Resource res;
		if(dw != null){
			res = dw.res.get();
			if(res != null){
				names.add(res.name);
			}
		} else {
			Layered ld = getattr(Layered.class);
			if(ld != null){
				for (Indir<Resource> ir : ld.layers){
					res = ir.get();
					if(res != null){
						names.add(res.name);
					}
				}
			}
		}
		return names.toArray(new String[names.size()]);
	}

	public Random mkrandoom() {
		if (id < 0)
			return (MCache.mkrandoom(rc));
		else
			return (new Random(id));
	}

	public Resource.Neg getneg() {
		Resource r = getres();
		if (r != null) {
			return (r.layer(Resource.negc));
		}
		return (null);
	}
	
	public boolean isPlayer() {
		Avatar ava = getattr(Avatar.class);
		if (ava != null && ava.isPlayer()) {
			return true;
		}
		return false;
	}
	
	public boolean isKin() {
		KinInfo k = getattr(KinInfo.class);
		if (k != null) {
			if (k.group == 0 && k.name.length() == 0)
				return false;
			if (k.group >= 1)
				return true;
		}
		return false;
	}
	
	public boolean isInYourVillage() {
		KinInfo k = getattr(KinInfo.class);
		if (k != null && (k.type & 2) != 0)
				return true;
		return false;
	}
	
	public boolean isGroupKin(int group) {
		KinInfo k = getattr(KinInfo.class);
		if (k != null && k.group != group)
			return true;
		return false;
	}
	
	public int getKinGroup() {
		KinInfo k = getattr(KinInfo.class);
		if (k != null) {
			return k.group;
		}
		return -1;
	}
	
	public int getKinType() {
		KinInfo k = getattr(KinInfo.class);
		if (k != null) {
			return k.type;
		}
		return -1;
	}
}
