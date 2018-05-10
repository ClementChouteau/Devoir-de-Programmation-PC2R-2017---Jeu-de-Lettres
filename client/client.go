package main

import (
	"bufio"
	"flag"
	"fmt"
	"net"
	"os"
	"strings"
	"sync"
)

func printGrid(grid string) {
	for i := 0; i < 4; i++ {
		fmt.Println("|" + grid[4*i:4*(i+1)] + "|")
	}
}

func printBilan(motsProposes string, scores string) {
	// afficher : score : user : mot1, mot2.....
	fmt.Println("Words : " + motsProposes) // PAS FINI ICI
	fmt.Println("Scores : " + scores)      // PAS FINI ICI
}

func printVainqueur(vainqueur string) {
	fmt.Println("Vainqueur : " + vainqueur) // PAS FINI ICI
}

func stdinToChan(in chan string) {
	stdinReader := bufio.NewReader(os.Stdin)
	for {
		s, _ := stdinReader.ReadString('\n')
		in <- strings.Trim(s, "\n")
	}
}

func receiver(wg *sync.WaitGroup, rfin chan<- struct{}, gridsCh chan<- string, conn net.Conn) {
	defer wg.Done()

	buffer := make([]byte, 1024)

	for {
		n, _ := conn.Read(buffer)
		line := string(buffer[:n])

		args := strings.Split(line, "/")
		if len(args) >= 1 {
			switch args[0] {
			case "BIENVENUE": // BIENVENUE/tirage/scores/
				if len(args) >= 3 {
					fmt.Println("Scores: " + args[2] + ".")
					gridsCh <- args[1]
				}
			case "CONNECTE": // CONNECTE/user/
				if len(args) >= 2 {
					fmt.Println("User : " + args[1] + " joined the game.")
				}
			case "DECONNEXION": // DECONNEXION/user/
				if len(args) >= 2 {
					fmt.Println("User : " + args[1] + " leaved the game.")
				}
			case "SESSION": // SESSION/
				fmt.Println("Beginning of a game session !")
				//gridsCh <- "" // QUAND SESSION SERA REGLE COTE SERVEUR

			case "VAINQUEUR": // VAINQUEUR/bilan/
				if len(args) >= 2 {
					printVainqueur(args[1])
				}
				return
			case "TOUR": // TOUR/tirage/
				if len(args) >= 2 {
					fmt.Println("Beginning of a new round !")
					gridsCh <- args[1]
				}
			case "MVALIDE": // MVALIDE/mot/
				if len(args) >= 2 {
					fmt.Println("Valid word : " + args[1] + ".")
				}
			case "MINVALIDE": // MINVALIDE/raison/
				if len(args) >= 2 {
					fmt.Println("Invalid word (reason : " + args[1] + ").")
				}
			case "RFIN":
				fmt.Println("End of round.")
				rfin <- struct{}{}

			case "BILANMOTS": // BILANMOTS/motsproposes/scores/
				if len(args) >= 3 {
					printBilan(args[1], args[2])
				}
			case "RECEPTION": // RECEPTION/message/
				if len(args) >= 2 {
					fmt.Println(">> " + args[1])
				}
			case "PRECEPTION": // PRECEPTION/message/user/
				if len(args) >= 3 {
					fmt.Println(args[2] + " >> " + args[1])
				}
			default:
				fmt.Println("Unrecognized HEADER: " + line)
			}
		}
	}
}

func wordOfTrajectory(grid string, trajectory string) string {
	var word string

	for i := 0; i+1 < len(trajectory); i += 2 {
		var y int
		switch trajectory[i] {
		case 'A':
			y = 0
		case 'B':
			y = 1
		case 'C':
			y = 2
		case 'D':
			y = 3
		default:
			y = 0
		}

		var x int
		switch trajectory[i+1] {
		case '1':
			x = 0
		case '2':
			x = 1
		case '3':
			x = 2
		case '4':
			x = 3
		default:
			x = 0
		}

		word += string(grid[y*4+x])
	}

	return word
}

func sender(wg *sync.WaitGroup, rfin <-chan struct{}, gridsCh <-chan string, conn net.Conn) {
	defer wg.Done()

	//<-gridsCh // QUAND SESSION SERA REGLE COTE SERVEUR

	stdinCh := make(chan string)
	go stdinToChan(stdinCh)
	defer close(stdinCh)

	fmt.Print("Choose user name : ")
	user := <-stdinCh

	conn.Write([]byte("CONNEXION/" + user + "/" + "\n"))

ROUND:
	for grid := range gridsCh {
		printGrid(grid)
		for {
			select {
			case line := <-stdinCh:
				args := strings.Fields(line)

				if len(args) >= 1 {
					switch args[0] {
					case "exit":
						conn.Write([]byte("SORT/" + user + "/" + "\n"))
						return
					case "msg":
						if len(args) >= 2 {
							message := args[1]
							conn.Write([]byte("ENVOI/" + message + "/" + "\n"))
						}
					case "pmsg":
						if len(args) >= 3 {
							toUser := args[1]
							message := args[2]
							conn.Write([]byte("PENVOI/" + toUser + "/" + message + "/" + "\n"))
						}
					default:
						conn.Write([]byte("TROUVE/" + wordOfTrajectory(grid, line) + "/" + line + "/" + "\n"))
					}
				}

			case <-rfin:
				continue ROUND
			}
		}
	}
}

func main() {
	hostname := flag.String("hostname", "localhost", "address used by server")
	port := flag.String("port", "2018", "port used by server")

	conn, err := net.Dial("tcp", *hostname+":"+*port)
	if err != nil {
		fmt.Println("Can't connect to server")
		return
	}
	defer conn.Close()

	gridsCh := make(chan string) // closed to indicate end of game

	rfin := make(chan struct{})
	defer close(rfin)

	var wg sync.WaitGroup
	wg.Add(1)
	go receiver(&wg, rfin, gridsCh, conn)
	wg.Add(1)
	go sender(&wg, rfin, gridsCh, conn)
	wg.Wait()
}
