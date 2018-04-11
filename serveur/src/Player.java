import java.net.Socket;

public class Player extends Thread {
	
	private final Socket socket;

	Player(Socket socket) {
		this.socket = socket;
	}

}
