
# Devoir-de-Programmation-PC2R-2017---Jeu-de-Lettres
Le but de ce projet est d'implémenter un jeude Boggle composé de deux parties client/serveur.
Nous avons choisi les langages:
+ Java pour le serveur.
+ Go pour le client.

## Utilisation du serveur  (Java)
Le serveur supporte les options de ligne de commande suivantes:
+ `-port 2018` spécifie le numéro de port du serveur (2018 par défaut, 0 pour choisir un port automatiquement).
+ `-hostname 127.0.0.1` spécifie le nom (adresse ?) du serveur
+ `-tours T`indique le nombre de tours de jeu.
+ `-grilles g1 ... gn` donne les grilles de jeu à utiliser (complétées par des grilles aléatoires si $n<T$ .
+ `-immediat` option vérification immédiate (non implémentée).
+ `-turnTime n` temps de réflexion par tour de jeu (3 min par défaut).
+ `-bilanTime n`temps de bilan entre les tours (10 s par défaut).

## Présentation du serveur (Java)
La gestion du jeu est facilitée par des classes utilitaires de rôle:
+ `Dices`: génération aléatoire d'une grille à partir de dés.
+ `Grid` stocke une grille, permet de convertir une trajectoire.
+ `Scoring` gère le dictionnaire, stocke les mots proposés par les joueurs, leurs scores.
+ `GameState` interface de haut niveau pour agir sur le jeu et obtenir des informations.

Gestion des requêtes clients selon un modèle $n$ producteurs pour $1$ consommateur, la communication se fait en utilisant une 
`BlockingDeque<Job>`, le type de requête étant `JobType`.
Les producteurs sont:
+ `Server` qui envoie des `Job` lors de l'avancement du jeu.
+ `Player` qui parse un message de son client (comme un ajout de mot).

Le consommateur est:
+ `Worker` qui récupère les `Job` et effectue les modifications nécessaires sur les structures de données.
```java
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
	
	Job(JobType type, String[] args);
}
```
Le producteur étant le seul à modifier les structures de jeu `GameState`, nous n'avons pas de problèmes de concurrence à ce niveau là.

Nous avons dû ajouter un mécanisme pour empêcher les joueurs de donner des mots pendant la phase de résultats, nous effectuons de façon atomique: l'interdiction de donner des mots (mots donnés ignorés), la déclaration de fin de round.
Tout cela n'empêche pas les threads `Player` de traiter les autres requêtes comme les messages de chat.
```java
for (int t = 0; t < turns; t++) {			
	// début du tour
	synchronized (jobs) {
		jobs.put(new Job(Job.JobType.TOUR, new String[0]));
		TROUVE_allowed.set(true);
	}
			
	// Phase de recherche
	TimeUnit.SECONDS.sleep(turnTime);

	// Phase de vérification et de résultat
	synchronized (jobs) {
		TROUVE_allowed.set(false);
		jobs.put(new Job(Job.JobType.RFIN, new String[0]));
		jobs.put(new Job(Job.JobType.BILANMOTS, new String[0]));
	}
		
	TimeUnit.SECONDS.sleep(bilanTime);			
}
```

Un joueur peut se retrouver déconnecté (donc broadcast d'un message `DECONNEXION`) de deux façons différentes:
+ Il envoie lui même `SORT`.
+ Une erreur lors d'un envoi vers le client le déconnecte.

Mais avec notre système de producteur/consommateur, il y a le risque d'envoyer plusieurs fois le message indiquant la déconnexion du joueur aux autres joueurs, nous utilisons la valeur spéciale `""` pour indiquer que le joueur va être déconnecté.

La liste des joueurs `List<Player> accepted` est accédée simultanément par `Accepter` pour ajouter un joueur, et par `Worker` pour itérer dessus, on *synchronize* l'objet pour éviter les problèmes.
```java
Socket socket = listener.accept();

synchronized (accepted) {
	Player player = new Player(socket, jobs, TROUVE_allowed);
	accepted.add(player);
	new Thread(player).start();
}
```
L'arrêt du jeu est effectué par `Server` en fermant les sockets, les threads détectent alors une erreur et s'arrêtent.

Dans les mécanismes de jeu nous considérons la notion de *trajectoire*, on a interpété l'énoncé comme parlant de trajectoire quand il parle de mot, à l'exception du dictionnaire.

## Utilisation du client (Go)
Nécessite l'installation du paquet go, le lancement du programme se fait avec `go run client.go`

Le client supporte les options de ligne de commande suivantes:
+ `-hostname 127.0.0.1` donne l'adresse du serveur Boggle.
+ `-port 2018` donne le port du serveur Boggle.

Voici un exemple de session de jeu:
```
Choose user name : clement
Scores: 1*clement*0.
|LIDA|
|REJU|
|LTNE|
|ATNG|
C2B1A2A3B2C3D2
Valid word : TRIDENT.
User : kahina joined the game.
>> bonjour tout le monde !
msg salut a tous !
pmsg kahina cc
End of round.
Words : clement*C2B1A2A3B2C3D2,
Scores : 0*kahina*0*clement*5
Vainqueur : 2*kahina*0*clement*5
```
On aperçoit les commandes :
+ `msg [message]` permet d'envoyer un message de chat global.
+ `pmsg [joueur] [message]` permet d'envoyer un message privé à un joueur.
+ `exit` indique au serveur la déconnexion du client et quitte le programme.
+ `C2B1A2A3B2C3D2` tout autre message est interprété comme une trajectoire.

## Présentation du client (Go)
Le client go utilise les threads:
+ Le thread `receiver` qui reçoit les messages envoyés par le serveur et affiche ce qu'il faut sur l'entrée standard.
+ Le thread `sender` qui attend une entrée (message ou trajectoire) sur stdin et effectue les envois au serveur.

La communication entre ces threads se fait avec des canaux synchrones go, on n'utilise pas de mémoire partagée.
Par exemple quand une grille est récupérée par `receiver`, il la place dans le canal `gridsCh`.
Quand une fin de tour est récupérée, une valeur arbitraire est passée dans un autre canal `rfin`.
```go
...
case "TOUR": // TOUR/tirage/
	if len(args) >= 2 {
		fmt.Println("Beginning of a new round !")
		gridsCh <- args[1]
	}
...
case "RFIN": // RFIN/
	fmt.Println("End of round.")
	rfin <- struct{}{}
...
```
Le thread `sender` attend une grille pour commencer un tour et attend simultanément  avec `select`:
+ Une entrée sur l'entrée standard `line :=  <-stdinCh`.
+ Une indication de fin de round `<-rfin`.

La fin d'une session de jeu est indiquée par la fermeture du canal `gridsCh`, ce qui entraîne la sortie de la boucle `for grid := range gridsCh`.
```go
...
ROUND:
	for grid := range gridsCh {
		printGrid(grid)
		for {
			select {
			case line := <-stdinCh:
				...
			case <-rfin:
				continue ROUND
			}
		}
	}
...
```

