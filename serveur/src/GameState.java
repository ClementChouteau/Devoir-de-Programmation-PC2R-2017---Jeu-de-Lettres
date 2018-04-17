import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {
	private int turns = 1;
	private int turn = 1;
	private ArrayList<String> grids = new ArrayList<>();
	private Scoring scoring;
	private Map<String, Boolean> sync = new HashMap<String, Boolean>();
	//TODO il manque des verrous pour les accès concurents ??
	
	public GameState(ArrayList<String> givenGrids, int turns) throws IOException {
		for (String grid : givenGrids)
			grids.add(grid);
		
		Dices dices = new Dices();		
		while (grids.size() < turns) {
			grids.add(dices.generateGrid());
		}
		
		scoring = new Scoring("dico.txt");
	}

	//TODO améliorer les noms
	//TODO remplacer toutes les méthodes posant une question par un isXXX()
	boolean isSynced(String user) {
		Boolean res = sync.get(user);
		return res!=null && res==Boolean.TRUE;
	}
	
	//TODO améliorer les noms
	void synced(String user) {
		sync.put(user, true);
	}

	
	public String turnGrid() {
		return grids.get(turn);
	}
	
	// avance le jeu d'un tour, change de grille
	public boolean nextTurn() {
		turn++;
		scoring.resetGivenWords();
		//TODO mettre nouvelle grille, indiquer "nouvelle grille"
		//TODO ou bien la nouvelle grille à tout le monde directement
		return turn <= turns;
	}
	
	// retourne null en cas de mot valide et accepté, retourne la raison sinon
	public String giveWord(String user, String trajectory) {
		String reason = scoring.isValid(turnGrid(), trajectory);

		if (reason == null)
			scoring.giveWord(user, trajectory);		
		
		return null;
	}
		
	public String scores() {
		return scoring.scores();
	}
}
