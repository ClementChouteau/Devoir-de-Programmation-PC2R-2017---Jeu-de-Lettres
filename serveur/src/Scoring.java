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
	private final Set<String> dictionnary = new HashSet<String>();
	private Map<String, String> owner = new HashMap<String, String>();
	private Map<String, Integer> scores = new HashMap<String, Integer>();


	public Scoring(String dictionnaryFile) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(dictionnaryFile))) {
			stream.forEach(line -> dictionnary.add(line));
		}
	}
	
	public boolean isValid(String grid, String trajectory) {
		if (trajectory.length() < 3)
			return false;
		
		if (trajectory.length()%2 != 0)
			return false;
		
		char[] word = new char[trajectory.length()/2];
		for (int i = 0; 2*i+1 < trajectory.length(); i++) {

			int y = 0;
			switch (trajectory.charAt(2*i)) {
			case 'A': y = 0; break;
			case 'B': y = 1; break;
			case 'C': y = 2; break;
			case 'D': y = 3; break;
			default: return false;			
			}

			int x = 0;
			switch (trajectory.charAt(2*i+1)) {
			case '1': x = 0; break;
			case '2': x = 1; break;
			case '3': x = 2; break;
			case '4': x = 3; break;
			default: return false;			
			}

			
			word[i] = grid.charAt(4*y + x); //TODO créer une classe Grid permettant l'accès selon une position (char, char)
		}
		
		if (!dictionnary.contains(new String(word)))
			return false;
		
		return owner.get(new String(word)) == null;
	}

	public void giveWord(String user, String trajectory) {
		//TODO ATTENTION ON DOIT METTRE LE MOT PAS LA TRAJECTOIRE
		//TODO PEUT ETRE D'AUTRES SOUCIS COMME CA AUTRE PART
		owner.put(trajectory, user);
		
		//TODO incrémenter score
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
}
