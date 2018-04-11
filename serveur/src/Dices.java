import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dices {
	
	private static char[][] dices =
		{
				{'E', 'T', 'U', 'K', 'N', 'O'},
				{'E', 'V', 'G', 'T', 'I', 'N'},
				{'D', 'E', 'C', 'A', 'M', 'P'},
				{'I', 'E', 'L', 'R', 'U', 'W'},
				{'E', 'H', 'I', 'F', 'S', 'E'},
				{'R', 'E', 'C', 'A', 'L', 'S'},
				{'E', 'N', 'T', 'D', 'O', 'S'},
				{'O', 'F', 'X', 'R', 'I', 'A'},
				{'N', 'A', 'V', 'E', 'D', 'Z'},
				{'E', 'I', 'O', 'A', 'T', 'A'},
				{'G', 'L', 'E', 'N', 'Y', 'U'},
				{'B', 'M', 'A', 'Q', 'J', 'O'},
				{'T', 'L', 'I', 'B', 'R', 'A'},
				{'S', 'P', 'U', 'L', 'T', 'E'},
				{'A', 'I', 'M', 'S', 'O', 'R'},
				{'E', 'N', 'H', 'R', 'I', 'S'}				
		};
	
	private final Random randomGenerator = new Random();
	
	Dices() {
	}

	private  ArrayList<Integer> randomPermutation(int n) {
		ArrayList<Integer> permutation = new ArrayList<Integer>();
		for (int i = 0; i < n; i++)
		    permutation.add(i);
		
		Collections.shuffle(permutation);
		return permutation;
	}
	
	String generateGrid() {
		char[] grid = new char [dices.length];
		ArrayList<Integer> permutation = randomPermutation(dices.length);
		
		for (int i = 0; i < dices.length; i++) {
		      int j = randomGenerator.nextInt(100);
		      grid[i] = dices[permutation.get(i)][j];
		}

		return new String(grid);
	}
	
}
