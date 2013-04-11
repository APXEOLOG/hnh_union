package union;

import haven.Glob;
import haven.Resource;
import haven.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CustomMenu {
	public static final String menu_prefix = "_u_";
	public static HashMap<String, Resource> pagina_cache = new HashMap<String, Resource>();
	
	public static abstract class MenuElemetUseListener {
		public Object info;
		public MenuElemetUseListener(Object nfo) {
			info = nfo;
		}
		public abstract void use(int button);
	}
	
	public static class MenuElement {
		// Tree Node Structures
		public List<MenuElement> children = new LinkedList<MenuElement>();
		public MenuElement parent;
		
		// Resource data
		public String[] action;	// Actions
		public Resource res;	// Final Resource
		public String uniq_id;	// uniq resource id
		
	    public List<MenuElement> getChildren() {
	        if (this.children == null) {
	            return new ArrayList<MenuElement>();
	        }
	        return this.children;
	    }
	 
	    public void setChildren(List<MenuElement> children) {
	        this.children = children;
	    }
	 
	    public int getNumberOfChildren() {
	        if (children == null) {
	            return 0;
	        }
	        return children.size();
	    }
	    
	    public MenuElement addChild(String[] act, Resource icon, String name, String tooltip, char hotkey, String uniq) {
	    	MenuElement element = new MenuElement(res, act, icon, name, tooltip, hotkey, uniq);
	    	element.parent = this;
	    	children.add(element);
	    	return element;
	    }
	    
		public MenuElement(Resource parent, String[] act, Resource icon, String name, String tooltip, char hotkey, String uniq) {
			action = act;
			uniq_id = menu_prefix + uniq;
			res = new Resource(uniq_id);
			res.addLayer(icon);
			res.removePagina();
			res.addLayer(res.new AButton(name, parent, action, hotkey),
						 res.new Pagina(tooltip));
			pagina_cache.put(uniq_id, res);
			AddToMenu();
		}
		
		public void AddToMenu() {
			if (Glob.instance != null && Glob.instance.paginae != null)
				if (!Glob.instance.paginae.contains(res)) Glob.instance.paginae.add(res);
		}
		
		public void RemoveFromMenu() {
			if (Glob.instance != null && Glob.instance.paginae != null) Glob.instance.paginae.remove(res);
			RemoveListener();
		}
		
		public void SetListener(MenuElemetUseListener lst) {
			if (UI.instance != null && UI.instance.menugrid != null && action.length > 1) UI.instance.menugrid.listeners.put(action[1], lst);
		}
		
		public void RemoveListener() {
			if (UI.instance != null && UI.instance.menugrid != null && action.length > 1) UI.instance.menugrid.listeners.remove(action[1]);
		}
	}
	
	public static class MenuTree {
	 
	    private MenuElement rootElement;
	     
	    public MenuTree() {
	        super();
	    }
	 
	    /**
	     * Return the root Node of the tree.
	     * @return the root element.
	     */
	    public MenuElement getRootElement() {
	        return this.rootElement;
	    }
	 
	    /**
	     * Set the root Element for the tree.
	     * @param rootElement the root element to set.
	     */
	    public void setRootElement(MenuElement rootElement) {
	        this.rootElement = rootElement;
	    }
	     
	    /**
	     * Returns the Tree<T> as a List of Node<T> objects. The elements of the
	     * List are generated from a pre-order traversal of the tree.
	     * @return a List<Node<T>>.
	     */
	    public List<MenuElement> toList() {
	        List<MenuElement> list = new ArrayList<MenuElement>();
	        walk(rootElement, list);
	        return list;
	    }
	     
	    /**
	     * Returns a String representation of the Tree. The elements are generated
	     * from a pre-order traversal of the Tree.
	     * @return the String representation of the Tree.
	     */
	    public String toString() {
	        return toList().toString();
	    }
	     
	    /**
	     * Walks the Tree in pre-order style. This is a recursive method, and is
	     * called from the toList() method with the root element as the first
	     * argument. It appends to the second argument, which is passed by reference     * as it recurses down the tree.
	     * @param element the starting element.
	     * @param list the output of the walk.
	     */
	    private void walk(MenuElement element, List<MenuElement> list) {
	        list.add(element);
	        for (MenuElement data : element.getChildren()) {
	            walk(data, list);
	        }
	    }
	}
}
