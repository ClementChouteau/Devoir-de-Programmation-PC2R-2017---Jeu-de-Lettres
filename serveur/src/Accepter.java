import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Accepter implements Runnable {

	private ServerSocket listener;
	private List<Player> accepted;
	private BlockingQueue<Job> jobs;
	private AtomicBoolean TROUVE_allowed ;

	Accepter(ServerSocket listener, List<Player> accepted, BlockingDeque<Job> jobs, AtomicBoolean TROUVE_allowed) {
		this.listener = listener;
		this.accepted = accepted;
		this.jobs = jobs;
		this.TROUVE_allowed = TROUVE_allowed;
	}
	
	@Override
	public void run() {		
		while (true) {
			try {
				Socket socket = listener.accept();
				
				synchronized (accepted) {
					Player player = new Player(socket, jobs, TROUVE_allowed);
					accepted.add(player);
					new Thread(player).start();
				}
			}
			catch (SocketException e) {
				break; // terminaison demandée par Server
			}
			catch (IOException e) {
				System.out.println("Problem with accept()");
			}
		}
		
		// stopper les Player
		for (Player player : accepted)
			try {
				player.socket.close();
			} catch (IOException e) {
			}		
	}
}
