import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class Player implements Runnable {
	public String user = null;
	public final Socket socket;
	public PrintWriter out;
	
	private BufferedReader in;
		
	private BlockingQueue<Job> jobs;
	private AtomicBoolean TROUVE_allowed;
	
	Player(Socket socket, BlockingQueue<Job> jobs, AtomicBoolean TROUVE_allowed) {
		this.socket = socket;
		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.jobs = jobs;
		this.TROUVE_allowed = TROUVE_allowed;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String line = in.readLine();
				System.out.println(line);
				String[] args = Parser.parse(line);
				// (C -> S) Nouvelle connexion d’un client nomme ’user’
				if (args.length >= 2 && args[0].equals("CONNEXION")) {
					user = args[1];
					jobs.put(new Job (Job.JobType.CONNEXION, args, this));
				}
				else if (user != null) {					
					// (C -> S) Déconnexion de ’user’.
					if (args.length >= 2 && args[0].equals("SORT")) {
						synchronized (this) {
							if (user != "") {
								user = "";
								jobs.put(new Job (Job.JobType.SORT, args, this));								
							}
						}
						break; // terminer le thread
					}
					// (C -> S) Annonce d’un mot et de sa trajectoire par un joueur.
					else if (args.length >= 3 && args[0].equals("TROUVE")) {
						synchronized (jobs) {
							if (!TROUVE_allowed.get())
								continue;
						
							args[1] = args[1].toUpperCase();
							args[2] = args[2].toUpperCase();
							jobs.put(new Job (Job.JobType.TROUVE, args,this));
						}
					}
					// (C -> S) Envoi (public) d’une chaine de caractere "message" à tous les joueurs.
					else if (args.length >= 2 && args[0].equals("ENVOI")) {
						jobs.put(new Job (Job.JobType.ENVOI, args, this));					
					}
					
					// (C -> S) Envoi (privé) d’une chaine de caractere "message" au joueur "user" uniquement.
					else if (args.length >= 3 && args[0].equals("PENVOI")) {
						jobs.put(new Job (Job.JobType.PENVOI, args, this));
					}
					else {
						System.out.println("Unrecognized message HEADER");
					}
				}
				
			} catch (Exception e) {
				break; // arrêter le thread
			}
		}
	}																																				
	
																																																																																													
}																																																															