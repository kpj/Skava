package Audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import Startup.Starter;

public class Playback extends Thread {
	
	private ServerSocket servSock;
	private Socket sock;
	
	private int maxRecv = 65655;
	
	private boolean stopped = false;
		
	
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	
	
	public Playback(int port) {
	
		// Wup Wup
		
		try {
			servSock = new ServerSocket(port);
			
			System.out.println("Initialized playback");
		} catch (IOException e) {
			if(Starter.verbose) {
				e.printStackTrace();
			} else {
				System.out.println(Starter.error_non_verbose_msg);
			}
			System.exit(-1);	
		}
	}
	
	public void run() {
		try {
			handleIncomingData();
		} catch (IOException e) {
			if(Starter.verbose) {
				e.printStackTrace();
			} else {
				System.out.println(Starter.error_non_verbose_msg);
			}
			System.exit(-1);
		}
	}
	
	public void handleIncomingData() throws IOException {
		
		sock = servSock.accept();
		
		InputStream inputStream = sock.getInputStream();
		
		byte[] byteArr = new byte[maxRecv];
		byte[] data = new byte[0];
				
		int bytesRead = 0;
		
		while(!stopped) {
			bytesRead = inputStream.read(byteArr, 0, Math.min(maxRecv, inputStream.available()));
			data = Arrays.copyOf(byteArr, bytesRead);
			if(data.length == 0)
				continue;
			
			playByteArray(data);
		}
	}
	
	public void endPlayback() {
		this.stopped = true;
	}
	
	public void playByteArray(byte[] arr) {
		
		audioInputStream = new AudioInputStream(
				new ByteArrayInputStream(arr),
				Starter.getFormat(),
				arr.length / Starter.getFormat().getFrameSize()
			);
						
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, Starter.getFormat());
		
		try {
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
			
			sourceDataLine.open(Starter.getFormat());
		} catch (LineUnavailableException e) {
			if(Starter.verbose) {
				e.printStackTrace();
			} else {
				System.out.println(Starter.error_non_verbose_msg);
			}
			System.exit(-1);
		}
		
	    sourceDataLine.start();
	    
	    int bufferSize = (int) Starter.getFormat().getSampleRate() * Starter.getFormat().getFrameSize();
	    byte buffer[] = new byte[bufferSize];
	    
		try {
			int count;
            while ((count = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
            	if (count > 0) {
            		sourceDataLine.write(buffer, 0, count);
            	}
            }
            sourceDataLine.drain();
            sourceDataLine.close();
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
