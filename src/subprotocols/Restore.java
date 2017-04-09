package subprotocols;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import peers.FileInfo;
import peers.Peer;
import utilities.Header;
import utilities.Message;

public class Restore extends Thread{

	private static String file_name;
	private static FileOutputStream new_output;
	private static byte[] file;
	private static Header header;
	private static int numOfChunks = 0;
	private static Message message;

	public Restore(String file_name){
		this.file_name = file_name;
		
		//create output
		try{
			new_output = new FileOutputStream("../res/" + "restore_" + file_name);
		}catch(FileNotFoundException e){
			System.out.println("Can't creat new output");
		}
		//Initiator for file
		file = new byte[0];
	}
	
	public void run(){
		if (!Peer.getData().getBackedUpFiles().containsKey(file_name)) {
			System.out.println("This file '" + file_name + "' was not backed up yet");
			return;
		}
		
		Peer.getData();
		FileInfo fileInfo = Peer.getData().getBackedUpFiles().get(file_name);
		header = new Header("GETCHUNK", "1.0", Peer.getPeer_id(), fileInfo.getFileId(), 0, 0);
		
		Peer.getMdrChannel().setWaitingChunks(true);
		sendChunk();
		
		
	}

	public static String getFile_name() {
		return file_name;
	}

	public static void setFile_name(String file_name) {
		Restore.file_name = file_name;
	}

	public static int getNumOfChunks(){ 
		return numOfChunks;
	}
	
	public static void loadDefaults() {
		file = new byte[0];
		numOfChunks = 0;
	}

	public static FileOutputStream getNew_output() {
		return new_output;
	}

	public static void incNumOfChunks() {
		numOfChunks++;
	}

	public static void sendChunk() {
		header.setChunkNo(numOfChunks);
		message = new Message(Peer.getMcChannel().getSocket(), Peer.getMcChannel().getAddr(), header, null);
		new Thread(message).start();
		
	}
	
	


}
