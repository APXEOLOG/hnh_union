package union;

import static haven.MCache.tileSize;
import haven.Config;
import haven.Coord;
import haven.GOut;
import haven.Gob;
import haven.IButton;
import haven.Resource;
import haven.UI;
import haven.INIFile.Pair;
import java.awt.Color;
import java.util.ArrayList;


public class KerriUtils {
	
	public static ArrayList<Integer> iObj = new ArrayList<Integer>(); //ignored objects list
	
	//clears object ignoring lis
	public static void clearOL() {
		iObj.clear();
	}
	
	//adds new object for ignoring
	public static void addOL(Gob obj) {
		if(obj != null) { 
			Integer newId = Integer.valueOf(obj.id);
			if(!iObj.contains(newId))
				iObj.add(newId);
			}
	}
	
	//minimap panel button curio
	public static void makeButtonSC() {
		new IButton(new Coord(140, 8), UI.instance.minimappanel,
				Resource.loadimg("gfx/hud/buttons/curiosityu"), Resource.loadimg("gfx/hud/buttons/curiosityd")) {
			//Kerrigan
			//я в душе не ибу правильно ли это, я в яву не могу
			//оверрайд так через жопу подстанавливается автоматически, не ругайте меня, это хотябы вроде работает
			@Override
			public void setPushed(boolean pushed) {
				super.setPushed(Config.show_minimap_profits);
			}
			public void click() {
				Config.show_minimap_profits = !Config.show_minimap_profits;
				setPushed(Config.show_minimap_profits);
				UI.instance.slenhud.error("Show objects on minimap: "+Boolean.toString(Config.show_minimap_profits));
				Config.saveOptions();
			}
		}.tooltip = "Show different shit on minimap";
	}

	//minimap panel button other players
	public static void makeButtonOP() {
		new IButton(new Coord(160, 8), UI.instance.minimappanel, Resource.loadimg("gfx/hud/buttons/humanu"),
				Resource.loadimg("gfx/hud/buttons/humand")) {
			@Override
			public void setPushed(boolean pushed) {
				super.setPushed(Config.show_minimap_players);
			}
			public void click() {
				Config.show_minimap_players = !Config.show_minimap_players;
				setPushed(Config.show_minimap_players);
				UI.instance.slenhud.error("Show hearthlings on minimap: "+Boolean.toString(Config.show_minimap_players));
				Config.saveOptions();
			}
		}.tooltip = "Show other players on minimap";
	}
	
	//minimap panel button vision radius
	public static void makeButtonVR() {
		new IButton(new Coord(180, 8), UI.instance.minimappanel, Resource.loadimg("gfx/hud/buttons/radiusu"),
				Resource.loadimg("gfx/hud/buttons/radiusd")) {
			@Override
			public void setPushed(boolean pushed) {
				super.setPushed(Config.show_minimap_radius);
			}
			public void click() {

				Config.show_minimap_radius = !Config.show_minimap_radius;
				setPushed(Config.show_minimap_radius);
				UI.instance.slenhud.error("Show vision rect: "+Boolean.toString(Config.show_minimap_radius));
				Config.saveOptions();
			}
		}.tooltip = "Show vision radius on minimap";
	}
	
	//minimap panel buton object health
	public static void makeButtonOH() {
		new IButton(new Coord(200, 8), UI.instance.minimappanel, Resource.loadimg("gfx/hud/buttons/healthu"),
				Resource.loadimg("gfx/hud/buttons/healthd")) {
			@Override
			public void setPushed(boolean pushed) {
				super.setPushed(Config.show_gob_health);
			}
			public void click() {
				Config.show_gob_health = !Config.show_gob_health;
				setPushed(Config.show_gob_health);
				UI.instance.slenhud.error("Show objects health: "+Boolean.toString(Config.show_gob_health));
				Config.saveOptions();
			}
		}.tooltip = "Show objects health";
	}
	
	//draws players at minimap
	public static void drawPlayersAtMinimap(GOut g, Coord tc, Coord hsz) {
		if(UI.instance.minimap == null) return;
		synchronized (UI.instance.minimap.players) {
			for (Pair<Coord, Color> arg : UI.instance.minimap.players) {
				Coord ptc = arg.fst.div(tileSize).add(tc.inv())
						.add(hsz.div(2));
				g.chcolor(Color.BLACK);
				g.ftriangle(ptc, 11);
				g.chcolor(arg.snd);
				g.ftriangle(ptc, 8);
				g.chcolor();
			}
		}
	}
	
	//draw profits at minimap
	public static void drawProfitMinimap(GOut g, Coord tc, Coord hsz) {
		if(UI.instance.minimap == null) return;
		synchronized (UI.instance.minimap.profits) {
			for (Pair<Coord, Color> arg : UI.instance.minimap.profits) {
				Coord ptc = arg.fst.div(tileSize).add(tc.inv())
						.add(hsz.div(2));
				g.chcolor(Color.BLACK);
				g.fellipse(ptc, new Coord(5, 5));
				g.chcolor(arg.snd);
				g.fellipse(ptc, new Coord(3, 3));
				g.chcolor();
			}
		}
	}
	
	//draw herbs at minimap
		public static void drawHerbsMinimap(GOut g, Coord tc, Coord hsz) {
			if(UI.instance.minimap == null) return;
			synchronized (UI.instance.minimap.hherbs) {
				for (Pair<Coord, String> arg : UI.instance.minimap.hherbs) {
					Coord ptc = arg.fst.div(tileSize).add(tc.inv())
							.add(hsz.div(2));
					g.chcolor(Color.GRAY);
					g.fellipse(ptc, new Coord(10, 10));
					g.chcolor();
					//drawing icon
					String resn = arg.snd;
					Resource res = Resource.load(resn);
					res.loadwait();
					g.image(res.layer(Resource.imgc).tex(), ptc.sub(new Coord(10, 10)), new Coord(20, 20));
				}
			}
		}
	
	//draw vision square
	public static void drawVisSquare(GOut g, Coord tc, Coord hsz) {
		if(JSBotUtils.playerID == -1)
			return;
		if(UI.instance.minimap == null) return;
		Coord current; 
		if(JSBotUtils.getPlayerSelf() == null)
			current = new Coord(0, 0);
		else {
			if(JSBotUtils.getPlayerSelf() == null)
				current = new Coord(0, 0);
			else
				current = JSBotUtils.getPlayerSelf().position();
		}
		Coord ptc = current.div(tileSize).add(tc.inv()).add(hsz.div(2));
		g.chcolor(255, 255, 255, 64);
		g.frect(ptc.sub(42, 42), new Coord(85, 85));
		g.chcolor();
	}
	
	//
	public static boolean isTresspass(int s) {
		if(s==52465)
			return true;
		return false;
	}
	//
	public static boolean isTheft(int s) {
		if(s==53185)
			return true;
		return false;
	}
	//
	public static boolean isVandalism(int s) {
		if(s==62977)
			return true;
		return false;
	}
	//
	public static boolean isAssault(int s) {
		if(s==53057)
			return true;
		return false;
	}
	//
	public static boolean isBattery(int s) {
		if(s==62529)
			return true;
		return false;
	}
	//
	public static boolean isMurder(int s) {
		if(s==32961)
			return true;
		return false;
	}

}
