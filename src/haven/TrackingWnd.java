package haven;

import java.util.ArrayList;
import java.util.List;

public class TrackingWnd extends Window {

    public static List<TrackingWnd>instances = new ArrayList<TrackingWnd>();
    public int a1, a2;
    public Coord pos = null;
    
    
    public TrackingWnd(int direction, int delta, int a1, int a2) {
	super(new Coord(100,100), Coord.z, UI.instance.root, "Direction");
	this.a1 = a1;
	this.a2 = a2;
	justclose = true;
	new Label(Coord.z, this, "Direction: "+direction+"o, delta: "+delta+"o");
	pack();
	Gob pl;
	if((ui.mapview.playergob >= 0) && ((pl = ui.sess.glob.oc.getgob(ui.mapview.playergob)) != null) && (pl.sc != null)) {
	    pos = pl.position();
	}
	instances.add(this);
    }
    
    public void destroy(){
	instances.remove(instances.indexOf(this));
	super.destroy();
    }
}
