import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Accepter implements Runnable {
	
	private ServerSocket listener;
	private List<Socket> accepted;
	
	Accepter(ServerSocket listener, List<Socket> accepted) {
		this.listener = listener;
		this.accepted = accepted;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				accepted.add(listener.accept());
			} catch (IOException e) {
			}
		}
	}
}
