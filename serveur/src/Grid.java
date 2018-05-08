import java.util.ArrayList;
import java.util.Collections;

public class Grid {
	private String grid;
	private ArrayList<Boolean> isUsed ;
	
	Grid (String grid) {
		this.grid = grid;
		isUsed = new ArrayList<Boolean>(Collections.nCopies(grid.length(), false));
	}

	public static ArrayList<Integer> trajectoryOfString(String trajectory) {
		ArrayList<Integer> t = new ArrayList<>();
		for (int i = 0; 2*i+1 < trajectory.length(); i+=2) {

			int y = 0;
			switch (trajectory.charAt(2*i)) {
			case 'A': y = 0; break;
			case 'B': y = 1; break;
			case 'C': y = 2; break;
			case 'D': y = 3; break;
			default: return null;
			}

			int x = 0;
			switch (trajectory.charAt(2*i+1)) {
			case '1': x = 0; break;
			case '2': x = 1; break;
			case '3': x = 2; break;
			case '4': x = 3; break;
			default: return null;
			}

			t.add(4*y + x);
		}
		
		return t;
	}
	
	private boolean checkTrajectory (ArrayList<Integer> t) {
		ArrayList<Boolean> path = new ArrayList<Boolean>(Collections.nCopies(grid.length(), false));
		Integer prev = null;
		for (Integer i : t ) {
			if (path.get(i))
				return false;
			
			path.set(i, true);

			// on vérifie que i et son précédent sont adjacents
			if (prev != null) {
				if (
						! (i == prev-1 || i == prev+1
						|| i == prev-4 || i == prev+4
						|| i == prev-3 || i == prev+3
						|| i == prev-5 || i == prev+5)
					)
					return false;
			}
			
			prev = i;
		}
			
		return true;
	}
	
	public boolean isUsedLetter (ArrayList<Integer> t) {
		for (Integer i : t)
			if (isUsed.get(i))
				return false;		
		return true;
	}
	
	public String wordOfTrajectory  (String trajectory) {

		if (trajectory.length()%2 != 0)
			return null;
		
		ArrayList<Integer> t = trajectoryOfString(trajectory);
		if (t == null)
			return null;
		
		if (!checkTrajectory(t))
			return null;

		char[] word = new char[trajectory.length()/2];
		for (int i = 0; 2*i+1 < trajectory.length(); i+=2)
			word[i] = grid.charAt(t.get(i));	
		
		return new String (word);
	}
	
	public void giveWord (ArrayList<Integer> t) {
		for (Integer i : t)
			isUsed.set(i, true);
	}
	
	
}
