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
	
	private Map<String, Integer> scores;
	private String grid;
	
	Player(String grid, Map<String, Integer> scores, Socket socket) {
		this.socket = socket;
		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.scores = scores;
		this.grid = grid; 
	}

	@Override
	public void run() {
		while (true) {
			try {
				String line = in.readLine();
				String[] args = Parser.parse(line);
				
				if (args.length >= 2 && args[0].equals("CONNEXION")) {
					user = args[1];
					scores.put(user, 0);
					
					out.print("BIENVENUE/" + grid + "/" + "SCORE ICI"  + "/n"); //TODO écrire le score
					out.flush();
				}
				
				//TODO gérer les autres messages que l'on peut recevoir
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}																																				
	
																																																																																													
}																																																															