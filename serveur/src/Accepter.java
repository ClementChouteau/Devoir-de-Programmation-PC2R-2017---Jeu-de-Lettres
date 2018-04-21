import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Accepter implements Runnable {

	private ServerSocket listener;
	private List<Player> accepted;
	private GameState game;

	Accepter(ServerSocket listener, List<Player> accepted, GameState game) {
		this.listener = listener;
		this.accepted = accepted;
		this.game = game;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = listener.accept();
				
				synchronized (accepted) {
					accepted.add(new Player(game, socket));
				}
			} catch (IOException e) {
			}
		}
	}
	
	public void broadcast(String msg) {
		ArrayList<String> disconnected = new ArrayList<>();
		
		synchronized (accepted) {
			for (Player player : accepted) {
				try {
					PrintWriter out = player.out;
					out.println(msg);
					out.flush();
				} catch (Exception e) {
					disconnected.add(player.user);
					//TODO arrêter le thread player
					accepted.remove(player);
				}
			}
		}
		if (!disconnected.isEmpty()) {
			disconnect(disconnected);
		}
	}
	
	public void disconnect(ArrayList<String> disconnected) {
		for (String user : disconnected) {
			broadcast("DECONNEXION/" + user + "/");
		}
	}
}
