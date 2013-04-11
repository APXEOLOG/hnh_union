package union;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class INIFile {
	public static class Pair<T, K> {
		public T fst;
		public K snd;

		public Pair(T v1, K v2) {
			fst = v1;
			snd = v2;
		}

	}
	
	HashMap<String, String> map = new HashMap<String, String>();

	public INIFile(String fname) throws IOException {
		loadFile(fname);
	}

	private void loadFile(String fname) throws IOException {
		FileInputStream fs = new FileInputStream(fname);

		try {
			String section = "";
			String line;
			boolean ended = false;

			int c;
			List<Integer> buf = new ArrayList<Integer>();
			while (!ended) {
				buf.clear();
				while (true) {
					c = fs.read();

					if (c == -1) {
						ended = true;
						break;
					}
					if (c == 13 || c == 10)
						break;
					else
						buf.add(c);
				}
				if (buf.size() < 1)
					continue;

				byte[] arr = new byte[buf.size()];
				for (int i = 0; i < buf.size(); i++)
					arr[i] = buf.get(i).byteValue();
				line = new String(arr, "utf-8");

				if (line.startsWith(";"))
					continue;
				if (line.startsWith("[")) {
					section = line.substring(1, line.lastIndexOf("]")).trim();
					continue;
				}
				if (line.length() < 1)
					continue;
				if (section.length() > 0)
					addProperty(section, line);
			}
		} finally {
			fs.close();
		}
	}
	
	public void saveFile(String filename) {
		try {
			List<String> sections_list = new ArrayList<String>();
			for (String key : map.keySet()) {
				String section = key.split("\\.")[0];
			    if (!sections_list.contains(section)) sections_list.add(section);
			}
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
			for (String sect : sections_list) {
				writer.write("[" + sect + "]\n");
				for (String key : map.keySet()) {
					String[] keys = key.split("\\.");
					if (sect.equals(keys[0])) {
						writer.write(keys[1] + "=" + map.get(key) + '\n');
					}
				}
			}
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error while saving ini file: " + filename);
		}
	}
	
	public void addProperty(String section, String line) {
		int equalIndex = line.indexOf("=");

		if (equalIndex > 0) {
			String name = section + '.' + line.substring(0, equalIndex).trim();
			String value = line.substring(equalIndex + 1).trim();
			if (map.containsKey(name)) map.remove(name);
			map.put(name, value);
		}
	}

	public String getProperty(String section, String var, String def) {
		String s = map.get(section + '.' + var);
		if (s == null)
			return def;
		else
			return s;
	}
	
	public ArrayList<Pair<String, Color>> getSectionColors(String section, String pref) {
		ArrayList<Pair<String, Color>> buf = new ArrayList<Pair<String, Color>>();
		for (String j : map.keySet()) {
			String[] val = j.split("\\.");
			if (val[0].equals(section)) {
				buf.add(new Pair<String, Color>(pref + val[1], Color.decode(map.get(j))));
			}
		}
		return buf;
	}
	
	public void deleteProperty(String section, String var) {
		if (map.containsKey(section + '.' + var)) map.remove(section + '.' + var);
	}

	public int getProperty(String section, String var, int def) {
		String sval = getProperty(section, var, Integer.toString(def));

		return Integer.decode(sval).intValue();
	}

	public boolean getProperty(String section, String var, boolean def) {
		String sval = getProperty(section, var, def ? "True" : "False");

		return sval.equalsIgnoreCase("Yes") || sval.equalsIgnoreCase("True");
	}
}