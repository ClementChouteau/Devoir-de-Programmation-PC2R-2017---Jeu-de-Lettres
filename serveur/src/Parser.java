import java.util.regex.Pattern;

public class Parser {
	public static String[] parse(String line) {
		return line.split(Pattern.quote("/"));
	}
}
