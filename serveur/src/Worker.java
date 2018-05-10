import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

public class Worker implements Runnable {

	private GameState game;
	private List<Player> accepted;
	private BlockingQueue<Job> jobs;
	
	Worker(GameState game, List<Player> accepted, BlockingDeque<Job> jobs) {
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
					String user = job.args[1];
					
					synchronized (accepted) {
						try {
							job.player.socket.close();
						} catch (IOException e) {
						}
						accepted.remove(job.player);
					}
					broadcast ("DECONNEXION/" + user + "/");
					break;
				}
				
				case CONNEXION:
				{
					
					String user = job.args[1];
					game.initializePlayer(user);
					
					job.player.out.print("BIENVENUE/" + game.turnGrid().grid +  "/" + game.scores()  + "/\n");
					job.player.out.flush();
					broadcast_if("CONNECTE/" + user + "/", player -> player.user != job.player.user); 
					break;
				}
				case TROUVE:
				{	
					
					String word = job.args[1];
					String trajectory = job.args[2];
										
					String reason;
					if(!word.equals(game.turnGrid().wordOfTrajectory(trajectory)))
						reason = "POS/word doesn't match trajectory";
					else
						reason = game.giveTrajectory(job.player.user, trajectory);
					
					if (reason == null) {
						job.player.out.print("MVALIDE/" + word + "/\n");
						job.player.out.flush();
					}
					else {
						job.player.out.print("MINVALIDE/" + reason + "/\n");
					    job.player.out.flush();
					}
					break;
				}
				case ENVOI:
				{
					String msg = job.args [1];
					broadcast_if("RECEPTION/" + msg + "/", player -> ! player.user.equals ( job.player.user)); 
					break;
				}
				case PENVOI:
				{	
					String user = job.args[1];
					String msg = job.args[2];
					broadcast_if("PRECEPTION/" + msg + "/" + job.player.user + "/", player -> player.user .equals (user)); 
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
					broadcast("BILANMOTS/" + game.turnTrajectories() + "/" + game.turnScores() + "/");					
					game.nextTurn();
				}
				case VAINQUEUR:
				{
					broadcast("VAINQUEUR/" + game.scores() + "/");
					return;
				}
				}
			}
			 catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	private void broadcast(String msg) throws InterruptedException {	
		broadcast_if(msg, player -> true);
	}

	private void broadcast_if(String msg, Predicate<Player> condition) throws InterruptedException {		
		synchronized (accepted) {
			for (ListIterator<Player>it = accepted.listIterator(); it.hasNext();) {
				Player player = it.next();
				
				if (condition.test(player)) {

					synchronized (player) {
						if (! player.user.equals("")) {
							try {
								PrintWriter out = player.out;
								out.println(msg);
								out.flush();
							} catch (Exception e) {
								try {
									player.socket.close();
								} catch (IOException e1) {
								}
								jobs.put(new Job (Job.JobType.SORT, new String[]{player.user}, player));
								it.remove();
							}							
						}
					}
				}
			}
		}
	}
}
