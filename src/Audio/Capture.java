package Audio;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import Startup.Starter;


public class Capture extends Thread {

	private boolean stopped = false;
	
	private TargetDataLine line;
		
	private OutputStream outputStream;
	
	public Capture(String ip, int port) {
		
		line = null;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, Starter.getFormat());
		
		if (!AudioSystem.isLineSupported(info)) {
		    System.out.println("This line is not supported");
		    System.exit(-1);
		}

		try {
		    line = (TargetDataLine) AudioSystem.getLine(info);
		    line.open(Starter.getFormat());
		} catch (LineUnavailableException e) {
			if(Starter.verbose) {
				e.printStackTrace();
			} else {
				System.out.println(Starter.error_non_verbose_msg);
			}
			System.exit(-1);
		}
		
		try {
			initConnection(ip, port);
		} catch (IOException e) {
			if(Starter.verbose) {
				e.printStackTrace();
			} else {
				System.out.println(Starter.error_non_verbose_msg);
			}
			System.exit(-1);
		}
		
		System.out.println("Initialized capturing");
	}
	
	public void endCapture() {
		this.stopped = true;
	}
	
	public void run() {
		int numBytesRead;
		byte[] data = new byte[line.getBufferSize() / 5];

		line.start();

		while (!stopped) {
		   numBytesRead =  line.read(data, 0, data.length);
		   try {
			   outputStream.write(data, 0, numBytesRead);
		   } catch (IOException e) {
			   if(Starter.verbose) {
					e.printStackTrace();
				} else {
					System.out.println(Starter.error_non_verbose_msg);
				}
				System.exit(-1);
		   }
		}
	}
	
	@SuppressWarnings("resource")
	public void initConnection(String ip, int port) throws IOException {
		Socket sock = new Socket(ip, port);
		outputStream = sock.getOutputStream();
	}
}
