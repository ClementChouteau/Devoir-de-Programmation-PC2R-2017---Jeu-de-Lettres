import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Scoring {
	private final Set<String> dictionnary = new HashSet<String>();
	private Map<String, String> owner = new HashMap<String, String>();
	private Map<String, Integer> scores = new HashMap<String, Integer>();
	private Map<String, Integer> prevScores = new HashMap<String, Integer>();

	//TODO prendre en compte l'option immediat
	public Scoring(String dictionnaryFile, boolean immediat) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(dictionnaryFile))) {
			stream.forEach(line -> dictionnary.add(line.toUpperCase()));
		}
	}
	
	// retourne null si le mot est valide, la raison sinon
	private String isValid(Grid grid, String trajectory) {
		String word = grid.wordOfTrajectory (trajectory);
		
		ArrayList<Integer> t = Grid.trajectoryOfString(trajectory);
		if (! grid.isUsedLetter(t))
		return "POS/letter already used";
		
		if (word == null)
			return "not a trajectory";
		
		if (word.length() < 3)
			return "too short";
		
		if (!dictionnary.contains(word))
			return "DIC/not in dictionnary";

		return null;
	}
	 public void initializePlayer( String user ) {
		 if (scores.get(user) == null)
			 scores.put(user, 0);			 
	 }
	

	// doit être appelé après isValid()
	public String giveTrajectory(Grid grid, String user, String trajectory) {
		String reason = isValid(grid, trajectory);
		if (reason != null)
			return reason;
				
		if (owner.get(trajectory) != null) {
			// le précédent  utilisateur perd les points de ce mot
			if(! owner.get(trajectory).equals("")) {
				 Integer ownerScore = scores.get(owner.get(trajectory));
				 scores.put(owner.get(trajectory), ownerScore - score (trajectory));
				 owner.put(trajectory, "");
			}
			return "PRI/word already used";
		}
		else {
			Integer prev = scores.get(user);
			scores.put(user, ((prev==null) ? 0 : prev) + score(trajectory));
		}

		owner.put(trajectory, user);

		return null;
	}

	public String scores() {
		StringWriter sw = new StringWriter();
		sw.write(new Integer (scores.size()).toString());

		for (Map.Entry<String, Integer> entry : scores.entrySet()) {
			sw.write('*');
			sw.write(entry.getKey());
			sw.write('*');
			String score = entry.getValue().toString();
			sw.write(score);
		}

		return sw.toString();
	}

	public String turnTrajectories() {
		StringWriter sw = new StringWriter();		
		// créer la liste d'association utilisateur -> mots proposés
		Map<String, List<String>> trajectoriesOfUsers = new HashMap<String, List<String>> ();
		for (Map.Entry<String, String> entry : owner.entrySet()) {
			String trajectory = entry.getKey();
			String user = entry.getValue();
			List<String> trajectories = trajectoriesOfUsers.get(user);
			if (trajectories == null)
				trajectories = new LinkedList<>();
			trajectories.add(trajectory);
			trajectoriesOfUsers.put(user, trajectories);
		}

		// user1*mot1*...*motN,user2:...
		for (Map.Entry<String, List<String>> entry : trajectoriesOfUsers.entrySet()) {
			String user = entry.getKey();
			List<String> words = entry.getValue();
			
			sw.write(user);
			for (String word : words) {
				sw.write('*');
				sw.write(word);
		
			}
			
			sw.write(','); //TODO virgule en trop à la fin
		}
		
		return sw.toString();
	}
	
	public String turnScores() {
		StringWriter sw = new StringWriter();
				
		for (Map.Entry<String, Integer> entry : scores.entrySet()) {
			sw.write('*');
			sw.write(entry.getKey());
			sw.write('*');
			
			Integer prev = prevScores.get(entry.getKey());
			Integer score = (prev != null) ? (prev - entry.getValue()) : entry.getValue();
			sw.write(score.toString());
		}
		
		return sw.toString();
		
	}
	
	private int score(String trajectory) {
		switch (trajectory.length()/2) {
		case 3:  return 1;
		case 2:  return 1;
		case 5:  return 2;
		case 6:  return 3;
		case 7:  return 5;
		default: return 11;
		}
	}
	
	void resetGivenWords() {
		owner.clear();
	}
	
	public void nextTurn () {
		owner.clear();
		prevScores = new HashMap<>(scores);
	}
	
	
}
	
