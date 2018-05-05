import java.util.regex.Pattern;

public class Parser {
	public static String[] parse(String line) {
		//TODO vérifier les requêtes, retourner null si la requête est pas valide
		return line.split(Pattern.quote("/"));
	}
}
