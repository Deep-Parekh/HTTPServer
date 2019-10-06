import java.net.*;
import java.io.*; 
import java.text.*;
import java.time.*;
import java.util.*;

public class Server 
{
	//initialize socket and input stream 
	static Socket socket = null;
	static ServerSocket server = null; 
	static DataInputStream in	 = null;
	static DataOutputStream out = null;
	
	public static void main(String args[])
	{
		try{
			server = new ServerSocket(5000);
			System.out.println("Listening for connection on port 5000");
		} catch (Exception e){
			System.out.println("Connection on port 5000 failed");
		}

		while (true) {
			try {
				socket = server.accept();
				in = new DataInputStream(socket.getInputStream());
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = reader.readLine();
				//System.out.println(line);
				String query = getHeader(line);
				out = new DataOutputStream(socket.getOutputStream());
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
				if (query == "<html><title>Output from Server</title><body><p>INVALID REQUEST</p></body></html>")
					writeToBrowser(writer, query);
				else {
					TimeClient tc = new TimeClient("time.nist.gov");
					String dateTime = tc.getDateTime();
					String output = convert(dateTime, query);
					writeToBrowser(writer, output);
				}
				reader.close();
				writer.close();
				socket.close();
			} catch (Exception e) {
				
			}
		}
		
	}
	
	private static String getHeader(String header) {
		String[] getHeader = header.split(" ");
		getHeader[1] = getHeader[1].replace('/', ' ').trim();
		switch (getHeader[1]) {
		case "time?x=pst":
			return "PST";
		case "time?x=est":
			return "EST5EDT";
		case "time?x=all":
		case "time":
			return "all";
		default:
			return "<html><title>Output from Server</title><body><p>INVALID REQUEST</p></body></html>";
		}
	}
	
	private static String convert(String dateTime, String query) {
		Calendar calendar = Calendar.getInstance();
		try {
			SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd,hh:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date dateAndTime = format.parse(dateTime);
			calendar.setTime(dateAndTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yy hh:mm aa");
		outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		if (query == "all") {
			String returnString = "<html><title>Output from Server</title><body><p><p>GMT Date/Time: " + outputFormat.format(calendar.getTime()) + "</p>";
			outputFormat.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
			returnString += "<p>EST Date/Time: " + outputFormat.format(calendar.getTime()) + "</p>";
			outputFormat.setTimeZone(TimeZone.getTimeZone("PST"));
			returnString += "<p>PST Date/Time: " + outputFormat.format(calendar.getTime()) + "</p></body></html>";
			return returnString;
		}
		outputFormat.setTimeZone(TimeZone.getTimeZone(query));
		if (query == "EST5EDT")
			query = "EST";
		return "<html><title>Output from Server</title><body><p>"+ query + " Date/Time: " + outputFormat.format(calendar.getTime()) + "</p>";
	}
	
	private static void writeToBrowser(BufferedWriter writer, String toWrite) {
		String httpHeader = "HTTP/1.1 200 OK\r\nContent-type: text/html\r\nContent-Length: " + toWrite.length() + "\r\n\r\n";
		//System.out.println("Printing Response Header: " + httpHeader);
		try {
			writer.write(httpHeader);
			writer.write(toWrite);
			writer.flush();
		} catch(IOException e) {
			
		}
	}
	
} 
