import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
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
		List<Player> accepted = new LinkedList<>();
		Accepter accepter = new Accepter(listener, accepted, game);
		Thread accepter_thread = new Thread(accepter);

		accepter_thread.start();

		for (int t = 0; t < turns; t++) {
			accepter.broadcast("SESSION/");
			// début du tour
			accepter.broadcast("TOUR/" + game.turnGrid() + "/");
			// Phase de recherche
			TimeUnit.SECONDS.sleep(3*60);

			accepter.broadcast("RFIN/");
			
			// Phase de vérification
			// Phase de résultat
			synchronized (game) {
				
				
				
				
				for (Player player : accepted) {
					
					//TODO BILANMOTS/motsproposes/scores/
					//(S -> C) Bilan du tour,ensemble des mots proposés et validés associés à leur client,
					//scores de tous les joueurs.					
					
					//TODO envoyer les scores
					//VAINQUEUR/bilan/
					//(S -> C) Fin de la session courante, scores finaux de la session.
				}
				
				game.nextTurn();

				TimeUnit.SECONDS.sleep(10); //TODO lancer plutôt le timer au début du synchronized et attendre le temps restant ici
			}
		}
		
		//TODO stopper le accepter ???
		listener.close();
	}	
}
