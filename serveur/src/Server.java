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
	private static int turnTime = 3*60;
	private static int bilanTime = 10;
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		int turns = 1;
		ArrayList<String> givenGrids = new ArrayList<>();
		boolean immediat = false;
		
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
			
			if (args[i].compareToIgnoreCase("-immediat") == 0)
				immediat = true;
			
			if (args[i].compareToIgnoreCase("-turnTime") == 0 && i+1 < args.length)
				turnTime = Integer.parseInt(args[++i]);

			if (args[i].compareToIgnoreCase("-bilanTime") == 0 && i+1 < args.length)
				bilanTime = Integer.parseInt(args[++i]);

			game = new GameState(givenGrids, turns, immediat);
		}
		
		ServerSocket listener = new ServerSocket(port, 0, InetAddress.getByName(hostname)); //TODO vérifier hostname
		List<Player> accepted = new LinkedList<>();
		BlockingQueue<Job> jobs = new LinkedBlockingQueue<Job>();
		Accepter accepter = new Accepter(listener, accepted, jobs);
		Thread accepter_thread = new Thread(accepter);		
		Thread worker_thread = new Thread(new Worker(game, accepted, jobs));

		// afficher les valeurs des options par défaut		
		if (port == 2018)
			System.out.println("Default port: 2018");
		else if (port == 0) {
			System.out.print("Automatically assigned port: ");
			System.out.println(listener.getLocalPort());
		}
		
		if (hostname == null)
			System.out.println("Default host name");
		
		if (turnTime == 3*60)
			System.out.println("Turn time default value: 3 min");
		if (bilanTime == 10)
			System.out.println("Bilan time default value: 10 s");
				
		accepter_thread.start();

		for (int t = 0; t < turns; t++) {
			jobs.put(new Job(Job.JobType.SESSION, new String[0]));
			
			// début du tour
			jobs.put(new Job(Job.JobType.TOUR, new String[0]));
			
			// Phase de recherche
			TimeUnit.SECONDS.sleep(turnTime);

			jobs.put(new Job(Job.JobType.RFIN, new String[0]));
			
			// Phase de vérification et de résultat
			synchronized (game) {
				//TODO attention à ces 2 lignes !!!!
				//TODO mettre un job changement de tour ????
				jobs.put(new Job(Job.JobType.BILANMOTS, new String[0]));				
				game.nextTurn();

				TimeUnit.SECONDS.sleep(bilanTime);
			}
		}

		//TODO ajouter comme plus urgent
		jobs.put(new Job(Job.JobType.VAINQUEUR, new String[0]));

		//TODO attendre la fin de Worker (arrêté par VAINQUEUR
		//TODO arrêter le thread Accepter
		
		//TODO et Player on les arrête quand ?
		
		listener.close();
	}	
}
