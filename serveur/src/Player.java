import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class Player implements Runnable {
	
	private final Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	private String user;
	
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
				
				if (args.length >= 2 && args[0].equals("CONNEXION")) {
					user = args[1];
					//TODO ajouter un joueur
					out.print("BIENVENUE/" + game.turnGrid() + "/" + game.scores()  + "/n");
					out.flush();
				}

				if (args.length >= 2 && args[0].equals("SORT")) {
					//TODO envoyer DECONNEXION à tous les utilisateurs sauf celui ci
					socket.close();
				}

				if (args.length >= 3 && args[0].equals("TROUVE")) {
					//String mot = args[2]; //TODO inutile ???
					String trajectoire = args[2];
					game.giveWord(line, trajectory)
				}
				
				(C -> S) Annonce d’un mot et de sa trajectoire par un joueur.
				
				//TODO gérer les autres messages que l'on peut recevoir
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}																																				
	
																																																																																													
}																																																															