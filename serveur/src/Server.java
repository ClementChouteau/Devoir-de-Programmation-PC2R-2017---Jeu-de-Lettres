import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Server {
	
	private static int port = 0;
	private static String hostname = null;
	
	private static GameState game;

	public static void main(String[] args) throws UnknownHostException, IOException {
		int turns = 1;
		ArrayList<String> givenGrids = new ArrayList<>();
		
		// parser les arguments de la ligne de commande
		for (int i = 0; i < args.length; i++) {
			
			if (args[i].compareToIgnoreCase("-port") == 0 && i+1 < args.length)
				port = Integer.parseInt(args[++i]);
			if (args[i].compareToIgnoreCase("-hostname") == 0 && i+1 < args.length)
				hostname = args[++i];

			if (args[i].compareToIgnoreCase("-tours") == 0 && i+1 < args.length)
				turns = Integer.parseInt(args[++i]);
			
			if (args[i].compareToIgnoreCase("-grilles") == 0) {				
				while (++i < args.length && args[i].charAt(0) != '-') {
					givenGrids.add(args[i]);
				}
			}
			
			game = new GameState(givenGrids, turns);
		}
		
		ServerSocket listener = new ServerSocket(port, 0, InetAddress.getByName(hostname));
		List<Socket> accepted = new LinkedList<>();

		Thread accepter = new Thread(new Accepter(listener, accepted));

		accepter.start();

		for (int t = 0; t < turns; t++) {
			// début du tour
			// Phase de recherche
			TimeUnit.SECONDS.sleep(3*60);

			//TODO envoyer RFIN/			(S -> C) Expiration du delai imparti a la reflexion.
			
			// Phase de vérification
			// Phase de résultat
			synchronized (game) {
				for (Socket client : accepted) {
					//TODO envoyer les scores
				}
				
				game.nextTurn();

				TimeUnit.SECONDS.sleep(10); //TODO lancer le timer au début du synchronized et attendre le temps restant
			}
		}
		
		//TODO stopper le accepter ???
		listener.close();
	}
}
