import java.io.IOException;
import java.util.ArrayList;

public class GameState {
	private int turn = 0;
	private final ArrayList<Grid> grids = new ArrayList<>();
	private Scoring scoring;
	private boolean immediat;
	
	 public GameState(ArrayList<String> givenGrids, int turns, boolean immediat) throws IOException {
		for (String grid : givenGrids)
			grids.add(new Grid (grid));
		
		Dices dices = new Dices();		
		while (grids.size() < turns) {
			grids.add(new Grid (dices.generateGrid()));
		}
		
		scoring = new Scoring("dico.txt", immediat);
		this.immediat = immediat;
	}
	 
	 public void initializePlayer( String user ) {
		 scoring.initializePlayer(user);
	 }
	
	public Grid turnGrid() {
		return grids.get(turn);
	}
	
	// avance le jeu d'un tour
	public void nextTurn() {
		turn++;
		scoring.resetGivenWords();
		scoring.nextTurn();
	}
	
	// retourne null en cas de mot valide et accepté, retourne la raison sinon
	public String giveTrajectory(String user, String trajectory) {
		System.out.println("game.giveTrajectory");
		return scoring.giveTrajectory(turnGrid(), user, trajectory);
	}

	public String turnTrajectories() {
		return scoring.turnTrajectories();
	}

	public String scores() {
		return  scoring.scores();
	}
	
	public String turnScores() {
		return turn + '*' + scoring.turnScores();
	}
}
