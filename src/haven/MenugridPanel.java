package haven;

import java.awt.image.BufferedImage;

public class MenugridPanel extends Window{
	
	static final BufferedImage grip = Resource.loadimg("gfx/hud/gripbr");
    static final Coord gzsz = new Coord(16,17);
    static final Coord minsz = new Coord(150, 125);
    public MenuGrid menugrid;
    
	public MenugridPanel(Coord c, Coord sz, Widget parent) {
		super(new Coord(50, 50), sz, parent, "Menu");
		mrgn = Coord.z;
		fbtn.visible = true;
		cbtn.visible = false;
		new MenuGrid(new Coord(5, 5), new Coord(100, 100), this);
		System.out.println(parent);
		//menugrid = new MenuGrid(new Coord(0, 0), this);
		pack();
		//this.c = new Coord(MainFrame.getInnerSize().x - this.sz.x, 7);
	}
	
	protected void placecbtn() {
		fbtn.c = new Coord(wsz.x - 3 - Utils.imgsz(cbtni[0]).x, 3).add(mrgn.inv().add(wbox.tloff().inv()));
	}

	public void draw(GOut g) {
		super.draw(g);
		if(!folded)
			g.image(grip, sz.sub(gzsz));
	}

	public boolean mousedown(Coord c, int button) {
		if(folded) {
			return super.mousedown(c, button);
		}
		parent.setfocus(this);
		raise();
		if (button == 1) {
			ui.grabmouse(this);
			doff = c;
		}
		return super.mousedown(c, button);
	}
	
	public boolean mouseup(Coord c, int button) {
//		if(dm){
//			Config.setWindowOpt("minimap_pos", this.c.toString());
//		}
		super.mouseup(c, button);
		return (true);
	}
	
	public void mousemove(Coord c) {
			super.mousemove(c);
	}

	public boolean type(char key, java.awt.event.KeyEvent ev) {
		if(key == 27) {
			wdgmsg(fbtn, "click");
			return(true);
		}
		return(super.type(key, ev));
	}
}
