import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;


public class Player implements Runnable {
	public String user;
	public final Socket socket;
	public PrintWriter out;
	
	private BufferedReader in;
		
	private BlockingQueue<Job> jobs;
	
	Player(Socket socket, BlockingQueue<Job> jobs) {
		this.socket = socket;
		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.jobs = jobs;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String line = in.readLine();
				String[] args = Parser.parse(line);
								
				// (C -> S) Nouvelle connexion d’un client nomme ’user’
				if (args.length >= 2 && args[0].equals("CONNEXION")) {
					user = args[1];
					jobs.put(new Job (Job.JobType.CONNEXION, args));
				}

				// (C -> S) Déconnexion de ’user’.
				else if (args.length >= 2 && args[0].equals("SORT")) {
					jobs.put(new Job (Job.JobType.SORT, args));
				}

				// (C -> S) Annonce d’un mot et de sa trajectoire par un joueur.
				else if (args.length >= 3 && args[0].equals("TROUVE")) {
					jobs.put(new Job (Job.JobType.TROUVE, args));					
				}
				
				else if (args.length >= 2 && args[0].equals("ENVOI")) {
					jobs.put(new Job (Job.JobType.ENVOI, args));					
				}
				
				else if (args.length >= 3 && args[0].equals("PENVOI")) {
					jobs.put(new Job (Job.JobType.PENVOI, args));
				}
				else {
					System.out.println("Unrecognized message HEADER");
				}
				
				//TODO mettre en place une map MESSAGEHEADER/ -> méthode à appeler
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}																																				
	
																																																																																													
}																																																															