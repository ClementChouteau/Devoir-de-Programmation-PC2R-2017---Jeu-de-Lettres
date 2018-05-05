import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Accepter implements Runnable {

	private ServerSocket listener;
	private List<Player> accepted;
	private BlockingQueue<Job> jobs;

	Accepter(ServerSocket listener, List<Player> accepted, BlockingQueue<Job> jobs) {
		this.listener = listener;
		this.accepted = accepted;
		this.jobs = jobs;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = listener.accept();
				
				synchronized (accepted) { //TODO vérifier les autres accès à accepted
					accepted.add(new Player(socket, jobs));
				}
			} catch (IOException e) {
			}
		}
	}
}
