import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

/**
 * Created by Francisca on 29/03/17.
 */
public class TestApp {
    private static String peer_ap;
    private static String remote_obj_name;

    public static void main(String[] args) throws IOException {
        if(!checkArguments(args)){
            return;
        }

        Services service;
        Registry registry = LocateRegistry.getRegistry("localhost");

        try {
            service = (Services) registry.lookup(peer_ap);
            switch(remote_obj_name) {
                case "BACKUP":
                    service.backup();
                    break;
                case "RESTORE":
                    service.restore();
                    break;
                case "DELETE":
                    service.delete();
                    break;
                case "MANAGE":
                    service.manage();
                    break;
                case "STATE":
                    service.state();
                    break;
                default:
                    System.out.println("Invalid Request");
                    System.exit(-1);
            }
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkArguments(String[] args) throws UnknownHostException {
        if(args.length < 2 || args.length > 4){
            System.out.println("Invalid number of arguments! Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
            return false;
        }

        setPeer_ap(args[0]);
        setRemote_obj_name(args[1]);

        return true;
    }

    //GETTERS AND SETTERS
    public static String getPeer_ap() {
        return peer_ap;
    }

    public static void setPeer_ap(String peer_ap) {
        TestApp.peer_ap = peer_ap;
    }

    public static String getRemote_obj_name() {
        return remote_obj_name;
    }

    public static void setRemote_obj_name(String remote_obj_name) {
        TestApp.remote_obj_name = remote_obj_name;
    }
}
