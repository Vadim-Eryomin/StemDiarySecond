import java.net.*;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;

public class Test {
    public static void main(String[] args) throws UnknownHostException, SocketException {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);

            System.out.println(socket);
        }
    }
}
