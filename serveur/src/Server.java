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
		
		while (grids.size() < turns) {
			grids.add(dices.generateGrid());
		}
		
		for (int t = 0; t < turns; t++) {
			
			// Phase de recherche
			
			// Phase de vérification
			
			// Phase de résultat
		}
		
		listener.close();
		
		/*
		• Phase de vérification : Le serveur vérifie que les propositions sont correctes, c’est-à-dire que les trajectoires
		sont correctes et que le mot proposé existe dans son dictionnaire. S’il y a un même mot proposé par au moins
		deux joueurs différents, les deux propositions sont considérées comme invalides. Par la suite, il y aura une
		variante de comptage qui prend en compte le mot proposé en premier. On passe ensuite à la phase de résultat où
		les scores sont calculés.
		*/
		
		/*
		• Phase de résultat: Les résultats du tour (score de tous les joueurs) sont diffusés aux participants ainsi que les
		mots proposés par chacun. Après un délai très court (10s), un nouveau tour commence avec un nouveau tirage
		de lettres.
		*/
	}
}
