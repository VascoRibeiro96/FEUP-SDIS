package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.ThreadLocalRandom;

import peers.DataBase;
import peers.FileInfo;
import peers.Peer;
import utilities.Header;
import utilities.Message;

public class MdbChannel extends Channel{

    public MdbChannel(InetAddress addr, int port) throws IOException {
        super(addr, port);
        this.thread = new MdbThread();
    }
    
    public class MdbThread extends Thread {
        public void run() {
            System.out.println("Listening the MDB channel...");
            while(true){
                try{
                    socket.joinGroup(addr);
                    
                    //separate header and body from data
                    byte[] buf = new byte[64 * 1000];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String data = new String(packet.getData(), 0, packet.getLength());

                    //separate Header
                    String[] dataArray = data.split("\\r\\n\\r\\n");
                    Header header = getHeader(dataArray);
                    String message_type = header.getMessageType();
                    String sender_id = header.getSenderId();

                    //separate Body
                    int offsetOfBody = dataArray[0].length() + 4;
                    byte[] bodyByteArray = getArrayFromOffset(packet.getData(), offsetOfBody, packet.getLength());

                    
                    if(Peer.getPeer_id() != sender_id) {
						switch (message_type) {
						case "PUTCHUNK":
							System.out.println("PUTCHUNK");
							if (DataBase.repDegAchieved(header)) {
								System.out.println("ReplicationDeg achived");
								break;
							}
							McChannel.setReceivedPutchunk(true);
						
							//Handle
					    	
							// Check if the file was not backed up by this peer
							for (FileInfo fileInfo : Peer.getData().getBackedUpFiles().values()) 
							    if (fileInfo.getFileId().equals(header.getFileId()))
							    	return;
							
							//save chunk
							Peer.getData().saveChunk(header, bodyByteArray);
							
							//reply
							String version = header.getVersion();
							String peer_id = Peer.getPeer_id();
							String file_id = header.getFileId();
							int chunk_number = header.getChunkNo();
							
							Header replyHeader = new Header("STORED", version, peer_id, file_id, chunk_number, 0);
							Message reply = new Message(Peer.getMcChannel().getSocket(), Peer.getMcChannel().getAddr(), replyHeader, null);
							
							int timeout = ThreadLocalRandom.current().nextInt(0, 400);
							Thread.sleep(timeout);
							new Thread(reply).start();
							
							System.out.println("Replying...");
							
							break;
						}
					}
                    socket.leaveGroup(addr);
                }
                catch (IOException  | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

