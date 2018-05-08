import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

public class Worker implements Runnable {

	private GameState game;
	private List<Player> accepted;
	private BlockingQueue<Job> jobs;
	
	Worker(GameState game, List<Player> accepted, BlockingQueue<Job> jobs) {
		this.game = game;
		this.accepted = accepted;
		this.jobs = jobs;		
	}

	@Override
	public void run() {
		while (true) {
			try {
				Job job = jobs.take();
				
				switch (job.type) {
				case SORT:
				{
					String user = job.args[0];
					
					if (accepted.stream().anyMatch(p -> p.user == user)) {
						//TODO arrêter le thread player
						accepted.stream().filter(p -> p.user != user);
						broadcast ("DECONNEXION/" + user + "/");						
					}					
					break;
				}
				
				case CONNEXION:
				{
					String user = job.args[1];
					//TODO ajouter un joueur
					job.out.print("BIENVENUE/" + game.turnGrid() + "/" + game.scores()  + "/\n");
					job.out.flush();
					broadcast_if("CONNECTE/" + user + "/", player -> player.user != job.player.user); 
					break;
				}
				case TROUVE :
				{	
					String word = job.args[1];
					String trajectory = job.args[2];

					String reason = game.giveWord(job.player.user, trajectory);

					if(!word.equals(game.turnGrid().wordOfTrajectory(trajectory)))
						reason = "POS/word doesn't match trajectory";

					//TODO reason doit commencer par 'POS' en cas de problème de position et 'DIC' en cas de mot absent du dictionnaire
					//TODO reason doit commencer par 'PRI' si le mot est déjà donné par un autre joueur

					if (reason == null)
						job.player.out.print("MVALIDE/" + word + "/\n");
						
					else
						job.player.out.print("MINVALIDE/" + reason + "/\n");						
					break;
				}
				case ENVOI:
				{
					String msg = job.args [1];
					broadcast_if("RECEPTION/" + msg + "/", player -> player.user != job.player.user); 
					break;
				}
				case PENVOI:
				{	
					String user = job.args[1];
					String msg = job.args[2];
					broadcast_if("PRECEPTION/" + msg + "/" + job.player.user + "/", player -> player.user == user); 
					break;
				}
				case SESSION:
				{
					broadcast("SESSION/");
					break;
				}
				case TOUR:
				{
					broadcast("TOUR/" + game.turnGrid() + "/");
					break;
				}
				case RFIN:
				{
					broadcast("RFIN/");
					break;
					
				}
				case BILANMOTS:
				{
					//TODO pas fini ici
					broadcast("BILANMOTS/" + motsproposes + "/" + game.turnScores() + "/");
						//(S -> C) Bilan du tour,ensemble des mots proposés et validés associés à leur client,
						//TODO envoyer seulement le score du tour
				}
				case VAINQUEUR:
				{
					broadcast("VAINQUEUR/" + game.scores() + "/");
					//TODO quitter ce thread
				}
				}
			}
			 catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	public void broadcast(String msg) throws InterruptedException {	
		broadcast_if(msg, player -> true);
	}

	public void broadcast_if(String msg, Predicate<Player> condition) throws InterruptedException {		
		synchronized (accepted) {
			for (Player player : accepted) {
				if (condition.test(player)) {
					try {
						PrintWriter out = player.out;
						out.println(msg);
						out.flush();
					} catch (Exception e) {
						jobs.offer(new Job (Job.JobType.SORT, new String[]{player.user}));
					}
				}
			}
		}
	}
}
