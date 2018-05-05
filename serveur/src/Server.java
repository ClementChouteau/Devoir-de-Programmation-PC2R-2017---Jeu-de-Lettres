import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Server {

	private static int port = 2018;
	private static String hostname = null;
	private static GameState game;

	// 1 thread qui accepte les connexions
	// 1 thread main qui gère les timers ??
	// n threads pour les clients (pool de thread !!) qui récupère les entrées et fais qq sorties faciles
	// 1 thread qui récupère les requêtes des thread clients et fais les envois (notamment les brodcast)
	
	
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
			
			//TODO récupérer en options des timers pour le temps de recherche / temps bilan du tour
			
			//TODO ajouter option -immediat pour les mots en double interdits
			
			game = new GameState(givenGrids, turns);
		}
		
		ServerSocket listener = new ServerSocket(port, 0, InetAddress.getByName(hostname)); //TODO vérifier hostname
		List<Player> accepted = new LinkedList<>();
		BlockingQueue<Job> jobs = new LinkedBlockingQueue<Job>();
		Accepter accepter = new Accepter(listener, accepted, jobs);
		Thread accepter_thread = new Thread(accepter);		
		Thread worker_thread = new Thread(new Worker(game, accepted, jobs));

		//TODO si port==0 alors afficher le port choisi
		
		//TODO si un paramètre par défaut est utilisé alors l'afficher
		
		accepter_thread.start();

		for (int t = 0; t < turns; t++) {
			jobs.put(new Job(Job.JobType.SESSION, new String[0]));
			
			// début du tour
			jobs.put(new Job(Job.JobType.TOUR, new String[0]));
			
			// Phase de recherche
			TimeUnit.SECONDS.sleep(3*60);

			jobs.put(new Job(Job.JobType.RFIN, new String[0]));
			
			
			// Phase de vérification
			// Phase de résultat
			synchronized (game) {
				jobs.put(new Job(Job.JobType.BILANMOTS, new String[0]));
				
				game.nextTurn();

				TimeUnit.SECONDS.sleep(10);
			}
		}

		jobs.put(new Job(Job.JobType.VAINQUEUR, new String[0]));

		
		//TODO stopper les threads proprement
		listener.close();
	}	
}
