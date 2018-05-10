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

(* Nouvelle connexion d’un client nomme ’user’ *)
(* CONNEXION/user/*)

let send s =
  let user = (my_input_line Unix.stdin) ^ "\n" in 
	ignore (Unix.write_substring sock user 0 (String.length s))
in
send ("CONNEXION/" ^ user ^ "/");

(* demander user *)
(* envoyer user au serveur *)


let readall socket = 
    let buffer = String.create 512 in
    let rec _readall accum = 
        try 
            let count = (recv socket buffer 0 512 []) in
                if count = 0 then accum else _readall ((String.sub buffer 0 count)::accum)
        with _ -> 
            accum
    in
        String.concat "" (List.rev (_readall []));


(** thread de réceptions serveur: attendre sur socket, bloquer un verrou, écrire sur stdout, débloquer verrou *)

(** thread d'interactions user: tant que ce n'est pas la fin des rounds *)
(* creer thread qui attend la grille *)
(* attendre réception grille -> lire de la socket client *)
(* afficher grille *)


(** Envoi de mot et trajectoire *)
(* creer thread qui attend la trajectoire + mot *)
(* demander une trajectoire *)
(* envoi mot trajectoire -> l'ecrire sur la socket cleint  *)

let afficher_grille g =
	for y=0 to 3 do
		for x=0 to 3 do
			print_char (String.get g (y*4+x))
		done;
		print_newline ()
	done;
in
afficher_grille "lidarejultneatng";

(** Chat avec les autres utilisateurs *)
(* Envoi message public -> ecrire ce message sur la socket client *)
(* ENVOI/message/ *)

(* PEnvoi message prive -> ecrire sur la socket en precisant le destinataire *)
(* PENVOI/user/message/ *)

(* Attention à tout moment lui aussi peut recevoir un message gérer ca *)
(* Message privé venant d'un utilisateur *)
(* Message public *)

(** DECONNEXION *)
(* SORT/user/ *)
(* DECONNEXION d'utilisateur USER *)
