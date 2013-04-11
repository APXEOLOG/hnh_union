package haven;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatHWPanel extends Widget implements IHWindowParent {

    public static ChatHWPanel instance;
    static BufferedImage[] fbtni = new BufferedImage[] {
	    Resource.loadimg("gfx/hud/fbtn"),
	    Resource.loadimg("gfx/hud/fbtnd"),
	    Resource.loadimg("gfx/hud/fbtnh") };
    static BufferedImage icon = Resource.loadimg("gfx/invobjs/parchment-written");
    static BufferedImage grip = Resource.loadimg("gfx/hud/griptr");
    static Coord isz = new Coord(30,30);
    static Coord gzsz = new Coord(16,17);
    static Tex cl = Resource.loadtex("gfx/hud/cleft");
    static Tex cm = Resource.loadtex("gfx/hud/cmain");
    static Tex cr = Resource.loadtex("gfx/hud/cright");
    static Text.Foundry cf = new Text.Foundry(new Font("Serif", Font.PLAIN, 12));
    static Text cap;
    
    static final int minbtnw = 90;
    static final int maxbtnw = 120;
    static final int sbtnw = 50;
    static final int btnh = 40;
    static final Coord minsz = new Coord(126, 126);
    public HWindow awnd;
    List<HWindow> wnds = new ArrayList<HWindow>();
    Map<HWindow, Button> btns = new HashMap<HWindow, Button>();
    Button sub, sdb;
    public static IButton fbtn;
    int urgency, woff = 0;
    boolean folded = false, dm = false, rsm = false, recalcsz = false;
    Coord btnc, doff, sc;

    static {
	cap = cf.render("Chat", Color.YELLOW);
    }
    
    public ChatHWPanel(Coord c, Coord sz, Widget parent) {
	super(c, sz, parent);
	ui.chat = instance = this;
	btnc = sz.sub(sz.x, btnh);
	sub = new Button(new Coord(300, 260), sbtnw, this,
		Resource.loadimg("gfx/hud/slen/sau")) {
	    public void click() {
		sup();
	    }
	};
	sdb = new Button(new Coord(300, 280), sbtnw, this,
		Resource.loadimg("gfx/hud/slen/sad")) {
	    public void click() {
		sdn();
	    }
	};
	sub.visible = sdb.visible = false;
	fbtn = new IButton(Coord.z, this, fbtni[0], fbtni[1], fbtni[2]);
	loadOpts();
    }
    
    private void loadOpts() {
	synchronized (Config.window_props) {
	    String str = Config.window_props.getProperty("chat_pos_folded", "null");
	    if(!str.equals("null")){
		sc = new Coord(str);
	    }
	    sz = new Coord(Config.window_props.getProperty("chat_size", sz.toString()));
	    c = new Coord(Config.window_props.getProperty("chat_pos", c.toString()));
	}	
    }
    
    private void sup() {
	woff--;
	updbtns();
    }

    private void sdn() {
	woff++;
	updbtns();
    }

    public void draw(GOut g) {
	if(recalcsz) {
	    recalcsz = false;
	    deltasz(Coord.z);
	}
	if (folded) {
	    if (SlenHud.urgcols[urgency] != null)
		g.chcolor(SlenHud.urgcols[urgency]);
	    g.image(icon, Coord.z.add(cl.sz()));
	    g.chcolor();
	    int w = cap.tex().sz().x;
	    int x0 = (isz.x / 2) - (w / 2) + cl.sz().x;
	    g.image(cl, new Coord(x0 - cl.sz().x, 0));
	    g.image(cm, new Coord(x0, 0), new Coord(w, cm.sz().y));
	    g.image(cr, new Coord(x0 + w, 0));
	    g.image(cap.tex(), new Coord(x0, 0));
	} else {
	    g.chcolor(230, 230, 255, 235);
	    g.frect(Coord.z, sz);
	    g.chcolor();
	    g.image(grip, new Coord(sz.x - gzsz.x, 0));
	    super.draw(g);
	    g.chcolor(64, 64, 64, 255);
	    g.rect(Coord.z, sz.add(new Coord(1, 1)));
	    g.chcolor();
	}
    }

    private void updbtns() {
	int k = Math.max((sz.x - sbtnw) / minbtnw, 1);
	if (k > wnds.size() / 2) {
	    k = Math.max(wnds.size() / 2, 1);
	    if ((wnds.size() % 2) != 0)
		k++;
	}
	int bw = Math.min((sz.x - sbtnw) / k, maxbtnw);
	int bpp = 2 * k;

	if (wnds.size() <= bpp) {
	    woff = 0;
	} else {
	    if (woff < 0)
		woff = 0;
	    if (woff > wnds.size() - bpp)
		woff = wnds.size() - bpp;
	}
	for (Button b : btns.values())
	    b.visible = false;
	sub.visible = sdb.visible = false;
	for (int i = 0; i < bpp; i++) {
	    int wi = i + woff;
	    if (wi >= wnds.size())
		continue;
	    if (woff > 0) {
		sub.visible = true;
		sub.c = btnc.add(sz.x - sbtnw, 0);
	    }
	    if (woff < wnds.size() - bpp) {
		sdb.visible = true;
		sdb.c = btnc.add(sz.x - sbtnw, 20);
	    }
	    HWindow w = wnds.get(wi);
	    Button b = btns.get(w);
	    w.sz = sz.sub(0, btnh);
	    b.change(w.title, w.visible ? Color.WHITE
		    : SlenHud.urgcols[w.urgent]);
	    b.visible = true;
	    b.sz.x = bw;
	    b.c = btnc.add(bw * (i % k), ((int) i / k) * 20);
	}
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
	if ((sender == fbtn)) {
	    setfold(true);
	} else {
	    super.wdgmsg(sender, msg, args);
	}
    }
    
    public void setfold(Boolean value) {
	if(folded != value) {
	    if(sc == null) 
		sc = c;
	    Coord tc = c;
	    c = sc;
	    sc = tc;
	}
	folded = value;
	fbtn.visible = !folded;
	if (awnd != null)
	    awnd.visible = !folded;
    }
    
    @Override
    public void addwnd(final HWindow wnd) {
	fbtn.raise();
	wnd.sz = sz.sub(0, btnh + gzsz.y);
	wnd.c = new Coord(0, gzsz.y);
	wnds.add(wnd);
	btns.put(wnd, new Button(new Coord(0, 260), 100, this, wnd.title) {
	    public void click() {
		setawnd(wnd, true);
	    }
	});
	if (!folded)
	    setawnd(wnd);
	else
	    wnd.visible = false;
	recalcsz = true;
    }

    @Override
    public void remwnd(HWindow wnd) {
	if (wnd == awnd) {
	    int i = wnds.indexOf(wnd);
	    if (wnds.size() == 1)
		setawnd(null);
	    else if (i < 0)
		setawnd(wnds.get(0));
	    else if (i >= wnds.size() - 1)
		setawnd(wnds.get(i - 1));
	    else
		setawnd(wnds.get(i + 1));
	}
	wnds.remove(wnd);
	ui.destroy(btns.get(wnd));
	btns.remove(wnd);
	updbtns();
    }

    @Override
    public void updurgency(HWindow wnd, int level) {
	if ((wnd == awnd) && !folded)
	    level = -1;
	if (level == -1) {
	    if (wnd.urgent == 0)
		return;
	    wnd.urgent = 0;
	} else {
	    if (wnd.urgent >= level)
		return;
	    wnd.urgent = level;
	}
	Button b = btns.get(wnd);
	b.change(wnd.title, SlenHud.urgcols[wnd.urgent]);
	int max = 0;
	for (HWindow w : wnds) {
	    if (w.urgent > max)
		max = w.urgent;
	}
	urgency = (level > 0) ? level : 0;
    }

    @Override
    public void setawnd(HWindow wnd) {
	setawnd(wnd, false);
    }

    @Override
    public void setawnd(HWindow wnd, boolean focus) {
	if (focus) {
	    setfold(false);
	}
	awnd = wnd;
	awnd.setfocus(awnd);
	for (HWindow w : wnds)
	    w.visible = false;
	if (wnd != null) {
	    wnd.visible = !folded;
	    updurgency(wnd, -1);
	}
	updbtns();
    }

    public boolean mousedown(Coord c, int button) {
	if(folded) {
	    if(!c.isect(Coord.z, isz.add(cl.sz())))
		return false;
	    if(c.isect(Coord.z.add(cl.sz()), isz))
		return true;
	}
	if(awnd != null)
	    awnd.setfocus(awnd);
	else
	    parent.setfocus(this);
	raise();
	if (super.mousedown(c, button))
	    return (true);
	if (!c.isect(Coord.z, sz))
	    return (false);
	if (button == 1) {
	    ui.grabmouse(this);
	    doff = c;
	    if(c.isect(new Coord(sz.x - gzsz.x, 0), gzsz))
		rsm = true;
	    else
		dm = true;
	}
	return (true);
    }

    public boolean mouseup(Coord c, int button) {
	if((folded)&&(c.isect(Coord.z.add(cl.sz()), isz))) {
	    setfold(false);
	    return true;
	}
	
	if (dm) {
	    ui.grabmouse(null);
	    dm = false;
	    Config.setWindowOpt("chat_pos"+(folded?"_folded":""), this.c.toString());
	} else if (rsm){
	    ui.grabmouse(null);
	    rsm = false;
	    Config.setWindowOpt("chat_size", this.sz.toString());
	} else {
	    super.mouseup(c, button);
	}
	return (true);
    }
    
    public void mousemove(Coord c) {
	if (dm) {
	    this.c = this.c.add(c.add(doff.inv()));
	} else if (rsm){
	    Coord d = c.sub(doff);
	    deltasz(d);
	    doff = c.add(0, d.y);
	} else {
	    super.mousemove(c);
	}
    }
    
    private void deltasz(Coord d) {
	d.y = -d.y;
	sz = sz.add(d);
	if(sz.x < minsz.x)
	    sz.x = minsz.x;
	if(sz.y < minsz.y)
	    sz.y = minsz.y;
	else
	    this.c.y -= d.y;
	btnc = sz.sub(sz.x, btnh);
	Coord s = sz.sub(0, btnh + gzsz.y);
	for (int i = 0; i < wnds.size(); i++) {
	    HWindow wnd = wnds.get(i);
	    wnd.setsz(s);
	}
	updbtns();
    }
    
    public boolean type(char key, KeyEvent ev) {
	if(!folded) {
	    if (key == KeyEvent.VK_ESCAPE) {
		setfold(true);
		return true;
	    }
//	    if ((key == KeyEvent.VK_ENTER)&&(awnd != null)) {
//		parent.setfocus(((ChatHW)awnd).in);
//		return true;
//	    }
	}
	return false;
    }

    @Override
    public HWindow getawnd() {
	return awnd;
    }
}
