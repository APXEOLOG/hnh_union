package union;

import haven.Coord;
import haven.Resource;
import haven.Tex;
import haven.TexI;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.HashMap;

import union.CustomMenu.MenuElement;
import union.CustomMenu.MenuElemetUseListener;

public class JSScriptInfo {
	/* Static */
	protected static Resource defaultIcon = Resource.load("paginae/union/scripts/script3");
	protected static HashMap<String, JSScriptInfo> script_list = new HashMap<String, JSScriptInfo>();
	
	public static void LoadAllSripts() {
		File scripts_dir = new File("scripts");
		if (scripts_dir.exists() && scripts_dir.isDirectory()) {
			String[] files = scripts_dir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".jbot");
				}
			});
			for (int i = 0; i < files.length; i++) {
				File script = new File(scripts_dir, files[i]);
				LoadScriptFromFile(script);
			}
		}
	}
	
	public static boolean hasUniq(String uniq) {
		for (JSScriptInfo info : script_list.values()) {
			if (info.scrUniq != null && info.scrUniq.equals(uniq)) return true;
		}
		return false;
	}
	
	public static JSScriptInfo LoadScriptFromFile(File file) {
		if (script_list.get(file.getName()) != null) {
			if (script_list.get(file.getName()).olderThen(file)) {
				script_list.get(file.getName()).LoadFromFile(file);
			}
		} else script_list.put(file.getName(), new JSScriptInfo(file));
		return script_list.get(file.getName());
	}
	
	public static void LoadAllSriptsToMenu() {
		for (JSScriptInfo info : script_list.values()) {
			if (!info.scrHide) info.AddStartMenuElement();
		}
	}
	
	public static void RemoveAllSriptsFromMenu() {
		for (JSScriptInfo info : script_list.values()) {
			if (!info.scrHide) info.RemoveStartMenuElement();
		}
	}
	/* End of static */
	
	public String scrName; // Название скрипта
	public String scrTooltip; // Тултип
	public String scrContent; // Содержимое скрипта
	public char scrHotkey; // Хоткей
	public Resource scrIcon; // Иконка в меню
	public String scrUniq; // Уникальный идентификатор скрипта
	public boolean scrHide; // dontshow

	// Массив всех токенов
	protected HashMap<String, String> tokens;
	// Время последнего изменения файла скрипта
	protected long lastModified = 0;
	// Имя файла
	protected String filename;
	// Элемент в меню запуска скрипта
	public MenuElement elementStart;
	// Элемент в меню остановки скрипта
	public MenuElement elementStop;
	// Поток
	public JSThread scrThread;

	public JSScriptInfo(File script) {
		LoadFromFile(script);
	}

	public void LoadFromFile(File script) {
		lastModified = script.lastModified();
		try {
			filename = script.getName();
			FileReader freader = new FileReader(script);
			BufferedReader reader = new BufferedReader(freader);
			StringBuilder builder = new StringBuilder();
			tokens = new HashMap<String, String>();
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				if (buffer.startsWith("//#!")) {
					if (buffer.contains("=")) {
						String[] token_buffer = buffer.substring(4).split("=");
						if (token_buffer.length == 2)
							tokens.put(token_buffer[0].trim(),
									token_buffer[1].trim());
					} else {
						tokens.put(buffer.substring(4).trim(), "");
					}
				}
				builder.append(buffer);
				builder.append('\n');
			}
			// Name
			if (tokens.containsKey("name"))
				scrName = tokens.get("name");
			else
				scrName = script.getName();
			// Tooltip
			if (tokens.containsKey("tooltip"))
				scrTooltip = tokens.get("tooltip");
			else
				scrTooltip = "";
			// Hotkey
			if (tokens.containsKey("hotkey"))
				scrHotkey = tokens.get("hotkey").charAt(0);
			else
				scrHotkey = '\0';
			// Icon
			if (tokens.containsKey("icon")) {
				scrIcon = Resource.load(tokens.get("icon"));
				scrIcon.loadwait();
				Resource.Image zimg = scrIcon.layer(Resource.imgc);
				Resource nr = new Resource(tokens.get("icon")+"_");
				if (zimg != null) {
					Tex btex = zimg.newTex();
					if (btex instanceof TexI) {
						Coord tSize = btex.sz();
						if(tSize.x > 30 || tSize.y > 30) {
							BufferedImage bim = APXUtils.scaleImage(((TexI)btex).back, 30, 30, new Color(0,0,0,0));
							nr.addLayer(scrIcon.new Image(zimg, bim));
							scrIcon = nr;
							//scrIcon.removeLayer(zimg);
							//scrIcon.addLayer(scrIcon.new Image(zimg, bim));
						}
					}
				}
			} else
				scrIcon = defaultIcon;
			// dontshow
			if (tokens.containsKey("dontshow"))
				scrHide = true;
			else
				scrHide = false;
			// Uniq
			if (scrUniq == null) { // Не генерировать уник заново для
									// перезагрузки скрипта
				if (tokens.containsKey("uniq") && !hasUniq(tokens.get("uniq")))
					scrUniq = tokens.get("uniq");
				else
					scrUniq = java.util.UUID.randomUUID().toString();
			}
			// Content
			scrContent = builder.toString();
			reader.close();
			freader.close();
		} catch (Exception e) {
			JSBot.JSError(e);
		}
	}

	public void AddStartMenuElement() {
		if (elementStart != null)
			return;
		elementStart = APXUtils.addResource("start_" + scrUniq, scrName,
				scrTooltip, scrHotkey, scrIcon, APXUtils.scriptRootNew,
				new MenuElemetUseListener(new String(filename)) {
					@Override
					public void use(int button) {
						if (info instanceof String) {
							JSScriptInfo script = script_list.get(info);
							script.Run();
						}
					}
				});
	}

	public void AddStopMenuElement() {
		if (elementStop != null)
			return;
		elementStop = APXUtils.addResource("stop_" + scrUniq, scrName,
				scrTooltip, scrHotkey, APXUtils.resScript4, APXUtils.scriptRootRem,
				new MenuElemetUseListener(new String(filename)) {
					@Override
					public void use(int button) {
						if (info instanceof String) {
							JSScriptInfo script = script_list.get(info);
							script.Stop();
						}
					}
				});
	}

	public void RemoveStopMenuElement() {
		if (elementStop != null) {
			elementStop.RemoveFromMenu();
			elementStop = null;
		}
	}
	
	public void RemoveStartMenuElement() {
		if (elementStart != null) {
			elementStart.RemoveFromMenu();
			elementStart = null;
		}
	}

	public void Run() {
		// Рестарт при повторном вызове. Зарпет на две копии одного скрипта при
		// одновременном выполнии
		if (isScriptRunning()) {
			Stop();
		}
		Update();
		scrThread = new JSThread(this) {

			@Override
			public void OnStart() {
				super.OnStart();
				AddStopMenuElement();
			}

			@Override
			public void OnStop() {
				super.OnStop();
				RemoveStopMenuElement();
			}
			
		};
		scrThread.start();
	}
	
	public void Stop() {
		if (scrThread != null) {
			scrThread.Stop();
			scrThread.interrupt();
		}
	}
	
	public boolean isScriptRunning() {
		return scrThread != null;
	}

	public boolean olderThen(File script) {
		return script.lastModified() > lastModified;
	}
	
	public void Update() {
		File scriptFile = new File("scripts", filename);
		if (scriptFile.exists()) {
			if (olderThen(scriptFile)) {
				LoadFromFile(scriptFile);
			}
		} else {
			RemoveStartMenuElement();
			script_list.remove(scriptFile.getName());
		}
	}
}
