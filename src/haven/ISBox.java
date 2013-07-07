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

import haven.Resource.Tooltip;

public class ISBox extends Widget implements DTarget {
    static Tex bg = Resource.loadtex("gfx/hud/bosq");
    static Text.Foundry lf;
    private Resource res;
    private Text label;
    public String resName; //Kerri: name of resource at box
    public String toolTip; //Kerri: resource in game tooltip
    public String boxValues; //Kerri: values a/b/c in box
    static {
        lf = new Text.Foundry(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 18), java.awt.Color.WHITE);
        lf.aa = true;
    }
    
    static {
        Widget.addtype("isbox", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                return(new ISBox(c, parent, Resource.load((String)args[0]), (Integer)args[1], (Integer)args[2], (Integer)args[3]));
            }
        });
    }
    
    private void setlabel(int rem, int av, int bi) {
        label = lf.renderf("%d/%d/%d", rem, av, bi);
        boxValues = String.valueOf(rem)+"/"+String.valueOf(av)+"/"+String.valueOf(bi);
    }
    
    public ISBox(Coord c, Widget parent, Resource res, int rem, int av, int bi) {
        super(c, bg.sz(), parent);
        this.res = res;
        resName = res.name;
        boxValues = String.valueOf(rem)+"/"+String.valueOf(av)+"/"+String.valueOf(bi);
        setlabel(rem, av, bi);
    }
    
    public void draw(GOut g) {
        g.image(bg, Coord.z);
        if(!res.loading) {
            Tex t = res.layer(Resource.imgc).tex();
            Tooltip tt = res.layer(Resource.tooltip); // APX
            if (tt != null) toolTip = tt.t; //Kerri
            Coord dc = new Coord(6, (bg.sz().y / 2) - (t.sz().y / 2));
            g.image(t, dc);
        }
        g.image(label.tex(), new Coord(40, (bg.sz().y / 2) - (label.tex().sz().y / 2)));
    }
    
    public Object tooltip(Coord c, boolean again) {
	if(!res.loading && (res.layer(Resource.tooltip) != null))
	    return res.layer(Resource.tooltip).t;
	return(null);
    }
    
    //Kerri
    public void takeOne()
    {
    	wdgmsg("click");
    }
    
    //Kerri
    public void transferOne()
    {
    	wdgmsg("xfer");
    }
    
    public boolean mousedown(Coord c, int button) {
        if(button == 1) {
            if(ui.modshift)
                wdgmsg("xfer");
            else
                wdgmsg("click");
            return(true);
        }
        else if(button == 3 && ui.modshift) //Kerri
        {
        	if(boxValues != null)
    		{
        		String[] sList = boxValues.split("/");
        		int current = Integer.parseInt(sList[1]);
        		//big container may cause lag effect
        		//so 'i' now between 0 and 56
        		//56 - maximum inventory that u can have
        		if(current < 56)
        			for(int i = 0; i < current; i++)
        				wdgmsg("xfer");
        		else
        			for(int i = 0; i < 56; i++)
        				wdgmsg("xfer");
    		}
        }
        return(false);
    }
    
    
    public boolean mousewheel(Coord c, int amount) {
	if(amount < 0)
	    wdgmsg("xfer2", -1, ui.modflags());
	if(amount > 0)
	    wdgmsg("xfer2", 1, ui.modflags());
	return(true);
    }
    
    public boolean drop(Coord cc, Coord ul) {
        wdgmsg("drop");
        return(true);
    }
    
    public boolean iteminteract(Coord cc, Coord ul) {
        wdgmsg("iact");
        return(true);
    }
    
    public void uimsg(String msg, Object... args) {
        if(msg == "chnum") {
            setlabel((Integer)args[0], (Integer)args[1], (Integer)args[2]);
        } else {
            super.uimsg(msg, args);
        }
    }
}
