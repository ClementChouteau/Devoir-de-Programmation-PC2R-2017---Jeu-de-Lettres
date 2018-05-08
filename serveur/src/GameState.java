import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {
	private int turns = 1;
	private int turn = 0;
	private ArrayList<Grid> grids = new ArrayList<>();
	private Scoring scoring;
	private Map<String, Boolean> sync = new HashMap<String, Boolean>(); //TODO c quoi ce truc ?
	private boolean immediat;
	//TODO il manque des verrous pour les accès concurents ??
	
	public GameState(ArrayList<String> givenGrids, int turns, boolean immediat) throws IOException {
		for (String grid : givenGrids)
			grids.add(new Grid (grid));
		
		Dices dices = new Dices();		
		while (grids.size() < turns) {
			grids.add(new Grid (dices.generateGrid()));
		}
		
		scoring = new Scoring("dico.txt");
		this.immediat = immediat; //TODO prendre en compte cette option
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
	
	public Grid turnGrid() {
		return grids.get(turn);
	}
	
	// avance le jeu d'un tour, change de grille
	public boolean nextTurn() {
		turn++;
		scoring.resetGivenWords();
		//TODO mettre nouvelle grille, indiquer "nouvelle grille"
		//TODO ou bien la nouvelle grille à tout le monde directement
		return turn < turns;
	}
	
	// retourne null en cas de mot valide et accepté, retourne la raison sinon
	public String giveWord(String user, String trajectory) {
		return scoring.giveWord(turnGrid(), user, trajectory);
	}

	public String scores() {
		return scoring.scores();
	}
	
	public String turnScores() {
		return scoring.turnScores();
	}
}
