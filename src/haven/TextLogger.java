package haven;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TextLogger {
	private Calendar cal;
	private SimpleDateFormat datestampformat;
	private SimpleDateFormat timestampformat;
	private String datestamp;
	private String timestamp;
	
	private boolean use_timestamp = true;

	private File dir;
	private File file;
	private BufferedWriter writer;

	public TextLogger(String filename) {
		datestampformat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		timestampformat = new SimpleDateFormat("'['H':'mm':'ss'] '");
		cal = Calendar.getInstance();
		datestamp = datestampformat.format(cal.getTime());

		try {
			if (writer != null)
				writer.close();
				
			file = new File(filename);
			dir = file.getParentFile();
			if (!dir.exists())
				dir.mkdirs();
			
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write("-");
			writer.newLine();
			writer.write(datestamp);
			writer.newLine();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}	

	public void Log (String str) {
		if (str.length() > 0) {
			try {
				cal = Calendar.getInstance();
				timestamp = timestampformat.format(cal.getTime());
				writer.write(timestamp);
				writer.write(str);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}