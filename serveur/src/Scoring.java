import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Scoring {
	private final Set<String> dictionnary = new HashSet<String>(); //TODO attention aux majuscules !
	private Map<String, String> owner = new HashMap<String, String>(); //TODO récupérer les mots pour chaque joueur et construire le résultat du tour
	private Map<String, Integer> scores = new HashMap<String, Integer>();
	private Map<String, Integer> prevScores = new HashMap<String, Integer>();

	public Scoring(String dictionnaryFile) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(dictionnaryFile))) {
			stream.forEach(line -> dictionnary.add(line));
		}
	}
	
	//TODO ATTENTION IL FAUT PRENDRE EN COMPTE CETTE REMARQUE
//	Une lettre
//	de la grille ne peut être utilisée qu’une seule fois dans la construction d’un mot.
// DONC ajouter un tableau de bool qui indique si la lettre est utilisée ou non
	
	// retourne null si le mot et valide, et la raison sinon
	private String isValid(Grid grid, String trajectory) {
		String word = grid.wordOfTrajectory (trajectory);
		
		if (word == null)
			return "not a trajectory";
		
		if (word.length() < 3)
			return "too short";
		
		if (!dictionnary.contains(word))
			return "not in dictionnary";

		return null;
	}

	// doit être appelé après isValid()
	public String giveWord(Grid grid, String user, String trajectory) {
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
			return "already used";
		}

		owner.put(trajectory, user);

		Integer prev = scores.get(user);
		scores.put(user, ((prev==null) ? 0 : prev) + score(trajectory));
		return null;
	}

	public String scores() {
		StringWriter sw = new StringWriter();

		sw.write(scores.size());
		for (Map.Entry<String, Integer> entry : scores.entrySet()) {
			sw.write('*');
			sw.write(entry.getKey());
			sw.write('*');
			sw.write(entry.getValue());
		}

		return sw.toString();
	}

	public String turnScores() {
		StringWriter sw = new StringWriter();
		
		//TODO ajouter n (numéro du tour) au début de la réponse
		
		sw.write(scores.size());
		for (Map.Entry<String, Integer> entry : scores.entrySet()) {
			sw.write('*');
			sw.write(entry.getKey());
			sw.write('*');
			
			Integer prev = prevScores.get(entry.getKey());
			sw.write((prev != null) ? (prev - entry.getValue()) : entry.getValue());
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
		prevScores = new HashMap<>(scores); //TODO attention cela doit etre une copie profonde
	}
	
	
}
