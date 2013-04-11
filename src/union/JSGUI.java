package union;

import java.util.TreeMap;

import union.jsbot.JSGUI_Button;
import union.jsbot.JSGUI_CheckBox;
import union.jsbot.JSGUI_Label;
import union.jsbot.JSGUI_TextEntry;
import union.jsbot.JSGUI_Window;

import haven.*;

public class JSGUI {
	private static TreeMap<Integer, Widget> local_widgets = new TreeMap<Integer, Widget>();
	private static int local_index = 0;
	
	public static abstract class JSGUI_Widget {
		protected int wdgid;
		
		public JSGUI_Widget(int id) {
			wdgid = id;
		}
		
		protected Widget wdg() {
			Object wdg = local_widgets.get(wdgid);
			if (wdg instanceof Widget) {
				return (Widget) wdg;
			} else
				return null;
		}
		
		public boolean isActual() {
			return wdg() != null;
		}
		
		public void destroy() {
			wdg().destroyAll();
			local_widgets.remove(wdgid);
		}
	}
	
	public static JSGUI_Window createWindow(Coord pos, Coord size, String caption) {
		local_index++;
		Window wnd = new Window(pos, size, UI.instance.root, caption);
		local_widgets.put(local_index, wnd);
		return new JSGUI_Window(local_index);	
	}
	
	public static JSGUI_Button createButton(JSGUI_Widget parent, Coord pos, int width, String text) {
		local_index++;
		Button btn = new Button(pos, width, parent.wdg(), text);
		local_widgets.put(local_index, btn);
		return new JSGUI_Button(local_index);	
	}
	
	public static JSGUI_Label createLabel(JSGUI_Widget parent, Coord pos, String text) {
		local_index++;
		Label lbl = new Label(pos, parent.wdg(), text);
		local_widgets.put(local_index, lbl);
		return new JSGUI_Label(local_index);	
	}
	
	public static JSGUI_TextEntry createEntry(JSGUI_Widget parent, Coord pos, Coord size, String deftext) {
		local_index++;
		TextEntry entry = new TextEntry(pos, size, parent.wdg(), deftext);
		local_widgets.put(local_index, entry);
		return new JSGUI_TextEntry(local_index);
	}
	
	public static JSGUI_CheckBox createBox(JSGUI_Widget parent, Coord pos, String text) {
		local_index++;
		CheckBox cbox = new CheckBox(pos, parent.wdg(), text);
		local_widgets.put(local_index, cbox);
		return new JSGUI_CheckBox(local_index);
	}
	
	public static JSGUI_Widget unWrapGUI_Widget(Object obj) {
		if (obj instanceof org.mozilla.javascript.Wrapper) {
			Object temp = ((org.mozilla.javascript.Wrapper)obj).unwrap();
			if (temp instanceof JSGUI_Widget)
				return (JSGUI_Widget) temp;
		}
		return null;
	}
}
