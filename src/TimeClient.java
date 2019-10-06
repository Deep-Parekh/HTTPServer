import java.net.*; 
import java.io.*; 

public class TimeClient 
{ 
	private Socket socket = null;
	private DataInputStream input = null;
	private InetAddress address = null;
	
	public TimeClient(String server) {
		try {
			address = InetAddress.getByName(server);
			socket = new Socket(address, 13);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getDateTime() {
		String dateTime = null, line = null;
		try {
			input = new DataInputStream(socket.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			line = reader.readLine();
			line = reader.readLine();
			System.out.println(line);
			String[] time = line.split(" ", 4);
			dateTime = time[1] + "," + time[2];
		}catch(IOException e) {
			
		} 
		return dateTime;
	}
	
	public static void main(String[] args) {
		TimeClient tc = new TimeClient("time.nist.gov");
		tc.getDateTime();
	}
	
} 
