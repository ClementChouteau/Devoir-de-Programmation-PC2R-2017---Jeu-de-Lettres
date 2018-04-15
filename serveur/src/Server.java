import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Server {
	
	private static int port = 2018;
	private static String hostname = null;
	
	private static int turns = 1;
	private static ArrayList<String> grids = new ArrayList<>();

	public static void main(String[] args) throws UnknownHostException, IOException {
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
					grids.add(args[i]);
				}				
			}
		}
		
		
		ServerSocket listener = new ServerSocket(port); //TODO utiliser hostname
		List<Socket> accepted = new LinkedList<>();

		Thread accepter = new Thread(new Accepter(listener, accepted));
		
		Dices dices = new Dices();
		Map<String, Integer> scores = new HashMap<String, Integer>();
		
		accepter.start();
		
		while (grids.size() < turns) {
			grids.add(dices.generateGrid());
		}
		
		for (int t = 0; t < turns; t++) {
			// début du tour
			//TODO lancer timer 3 à 5 min
			
			// Phase de recherche
			//TODO attendre fin timer
			
			// Phase de vérification
			// Phase de résultat
			//TODO verrou global de toutes les données de jeu
			//TODO timer de 10s
			//TODO envoyer les scores
			//TODO reset les scores et mots proposés
			//TODO mettre nouvelle grille, indiquer "nouvelle grille"
			//TODO attendre fin timer
			//TODO  enlever verrou global
		}
		
		listener.close();
	}
}
