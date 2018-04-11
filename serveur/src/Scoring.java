
public class Scoring {
	
	public Scoring() {
		// prend un board en paramètre
	}
	
	boolean isValid(String grid, String word) {
		if (grid.length() != word.length())
			return false;
		
		
	}

	int score(String word) {
		switch (word.length()) {
		case 3:  return 1;
		case 2:  return 1;
		case 5:  return 2;
		case 6:  return 3;
		case 7:  return 5;
		default: return 11;
		}
	}
}
