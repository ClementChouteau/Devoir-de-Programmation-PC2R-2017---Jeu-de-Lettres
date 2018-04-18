(* récupérer l'adresse et port du serveur sur la ligne de commande *)

(* demander user *)
(* envoyer user au serveur *)

(** thread de réceptions serveur: attendre sur socket, bloquer un verrou, écrire sur stdout, débloquer verrou *)

(** thread d'interactions user: tant que ce n'est pas la fin des rounds *)
(* attendre réception grille *)
(* afficher grille *)

(* demander une trajectoire *)
(* envoi mot trajectoire *)
(*  *)

let afficher_grille g =
	for y=0 to 3 do
		for x=0 to 3 do
			print_char (String.get g (y*4+x))
		done;
		print_newline ()
	done;
in
	afficher_grille "lidarejultneatng";
