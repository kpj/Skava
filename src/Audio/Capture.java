package Audio;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import Startup.Starter;


public class Capture extends Thread {

	private boolean stopped = false;
	
	private TargetDataLine line;
		
	private OutputStream outputStream;
	
	public Capture(String ip, int port) {
		
		line = null;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, Starter.getFormat());
		
		System.out.println();
		System.out.println("Possible mixer (choose by entering their index number)");
		
		Mixer.Info[] availMixer = AudioSystem.getMixerInfo();
		int c = 0;
		
		for(Mixer.Info mi : availMixer) {
			System.out.println("{" + c + "} - " + mi);
			System.out.println("Source lines:");
			for(Line.Info li : AudioSystem.getMixer(mi).getSourceLineInfo()) {
				System.out.println("\t"+li);
			}
			System.out.println("Target lines:");
			for(Line.Info li : AudioSystem.getMixer(mi).getTargetLineInfo()) {
				System.out.println("\t"+li);
			}
			System.out.println();
			c++;
		}
		c = Starter.readInt();
		
		if (!AudioSystem.isLineSupported(info)) {
		    System.out.println("This line is not supported");
		    System.exit(-1);
		}

		try {
			line = (TargetDataLine) AudioSystem.getMixer(availMixer[c]).getLine(info);
			line.open(Starter.getFormat());
		} catch (LineUnavailableException e) {
			if(Starter.verbose) {
				e.printStackTrace();
			} else {
				System.out.println(Starter.error_non_verbose_msg);
			}
			System.exit(-1);
		}
		
		boolean connected = false;
		System.out.println("Connecting");
		while(!connected) {
			try {
				initConnection(ip, port);
				connected = true;
			} catch (IOException e) {
				System.out.print(".");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println();
		
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
		   
		   for(int i=0; i<numBytesRead; i+=1) {
			   for(int j=0; j<=Integer.parseInt(Byte.toString(data[i])); j++) {
				   System.out.print(".");
			   }
			   System.out.println();
		   }
		   
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
