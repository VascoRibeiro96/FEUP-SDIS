package channels;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class MdrChannel extends Channel{

    public MdrChannel(InetAddress addr, int port) throws IOException {
        super(addr, port);
    }

}