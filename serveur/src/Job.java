import java.io.PrintWriter;


public class Job {
	
	public enum JobType {
		SORT,
		CONNEXION,
		TROUVE,
		ENVOI,
		PENVOI,
		SESSION,
		TOUR,
		RFIN,
		BILANMOTS,
		VAINQUEUR
		;
	}
	
	public JobType type;
	public String[] args;
	public PrintWriter out;
	public Player player;
	
	Job(JobType type, String[] args) {
		this.type = type;
		this.args = args;		
	}
	

	Job(JobType type, String[] args, Player player) {
		this(type, args);
		this.player = player;
	}
}