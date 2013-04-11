package union;

import haven.UI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import union.JSBot.JSDeprecatedHaven;
import union.JSUtils.StoppableContext;
import union.JSUtils.StoppedExecutionException;
import union.jsbot.JSHaven;

@SuppressWarnings("deprecation")
public class JSThread extends Thread {
	/* Static */
	public static HashMap<String, JSThread> scriptThreads = new HashMap<String, JSThread>();
	/* End of static */
	public JSScriptInfo scriptInfo;
	public StoppableContext jsContext;
	public ScriptableObject jsScope;

	public JSThread(JSScriptInfo info) {
		scriptInfo = info;
	}

	@Override
	public synchronized void run() {
		// For commits
		jsContext = new StoppableContext();
		jsContext.setGeneratingDebug(true);
		jsContext.setGeneratingSource(true);
		synchronized (scriptThreads) {
			scriptThreads.put(scriptInfo.scrUniq, this);
		}
		try {
			jsScope = jsContext.initStandardObjects();
			loadStaticClass(jsScope, JSHaven.class);
			Object jsOut = Context.javaToJS(System.out, jsScope);
			ScriptableObject.putProperty(jsScope, "out", jsOut);
			try {
				// Define classes here
				ScriptableObject.defineClass(jsScope, JSDeprecatedHaven.class);
				
			} catch (IllegalAccessException e) {
				JSBot.JSError(e);
			} catch (InstantiationException e) {
				JSBot.JSError(e);
			} catch (InvocationTargetException e) {
				JSBot.JSError(e);
			}
			OnStart();
			jsContext.evaluateString(jsScope, scriptInfo.scrContent,
					scriptInfo.scrName, 1, null);
		} catch (StoppedExecutionException e) {
			// do nothing, script stops.
		} catch (Exception ex) {
			JSBot.JSError(ex);
		} finally {
			OnStop();
			Context.exit();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void loadStaticClass(ScriptableObject scope, Class cl) {
		Method methods[] = cl.getDeclaredMethods();
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < methods.length; i++) {
			if (Modifier.isStatic(methods[i].getModifiers()) && Modifier.isPublic(methods[i].getModifiers())) {
				names.add(methods[i].getName());
			}
		}
		scope.defineFunctionProperties(names.toArray(new String[names.size()]), cl, ScriptableObject.DONTENUM);
	}

	@Override
	public String toString() {
		return String.format("Script: %s\tThread: %d", scriptInfo.scrName,
				getId());
	}
	
	public void Stop() {
		jsContext.Stop();
	}
	
	public void OnStart() {
		System.out.printf("%s Started.\n", toString());
		UI.instance.slenhud.error(scriptInfo.scrName + " started.");
		UI.instance.cons.out.println(scriptInfo.scrName + " started.");
	}
	
	public void OnStop() {
		scriptInfo.scrThread = null;
		System.out.printf("%s Finished.\n", toString());
		UI.instance.slenhud.error(scriptInfo.scrName + " finished.");
		UI.instance.cons.out.println(scriptInfo.scrName + " finished.");
	}
}
