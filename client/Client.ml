(* récupérer l'adresse et port du serveur sur la ligne de commande *)
#load "unix.cma";;
#load "threads.cma";;
let grille = "" in
let m_grille = Mutex.create () in

let my_sock_addr () = 
	let host = Unix.gethostbyname (Unix.gethostname()) in (** Sys.argv.(1) *)
	let h_addr = host.Unix.h_addr_list.(0) in
	let sock_addr = Unix.ADDR_INET(h_addr, int_of_string (Sys.argv.(2))) in
		sock_addr
in
let sock = Unix.socket Unix.PF_INET Unix.SOCK_STREAM 0 in
	Unix.connect sock (my_sock_addr ());
let user = read_line () in

let envoyer s =
	ignore (Unix.write_substring sock s 0 (String.length s))
in
envoyer ("CONNEXION/" ^ user ^ "/");

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
