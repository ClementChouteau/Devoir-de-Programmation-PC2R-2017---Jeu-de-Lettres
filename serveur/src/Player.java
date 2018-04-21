import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Player implements Runnable {
	public String user;
	public final Socket socket;
	public PrintWriter out;
	
	private BufferedReader in;
		
	private GameState game;
	
	Player(GameState game, Socket socket) {
		this.socket = socket;
		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.game = game;
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
					//TODO ajouter un joueur
					out.print("BIENVENUE/" + game.turnGrid() + "/" + game.scores()  + "/\n");
					out.flush();
					
					//TODO envoyer CONNECTE/user/ aux autres joueurs
					//(S -> C) Signalement de la connexion de ’user’ aux autres clients.
				}

				// (C -> S) Déconnexion de ’user’.
				if (args.length >= 2 && args[0].equals("SORT")) {
					//TODO envoyer DECONNEXION à tous les utilisateurs sauf celui ci
					// dire à accepter de me supprimer
				}

				// (C -> S) Annonce d’un mot et de sa trajectoire par un joueur.
				if (args.length >= 3 && args[0].equals("TROUVE")) {
					String word = args[2];
					String trajectory = args[2];
					
					String reason = game.giveWord(user, trajectory);
					
					if(!word.equals(game.turnGrid().wordOfTrajectory(trajectory)))
							reason = "word doesn't match trajectory";
												
					if (reason == null)
						out.print("MVALIDE/" + word + "/\n");
					else
						out.print("MINVALIDE/" + reason + "/\n");						
				}
				
				
				
				//TODO gérer les autres messages que l'on peut recevoir
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}																																				
	
																																																																																													
}																																																															