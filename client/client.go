package main
import (
	"fmt"
	"os"
	"bufio"
	"strings"
	"io/ioutil"
    "net"
)
 
func printGrid (grid string) {
	for i = 0; i < 4; i++ 
		fmt.Println(grid[4*i:4*(i+1)])
}

func printBilan (motsProposes string, scores string){	
	// afficher : score : user : mot1, mot2.....
	fmt.Println(motsProposes)
	fmt.Println(scores)

	wordsOfUsers := strings.Split(motsProposes, ",")
	scoresOfUsers := strings.Split(scores, "*")
	usersScores := []int{}
	
	for _, i := range scoresOfUsers {
		j, _ := strconv.Atoi(i)
		t2 = append(usersScores, j)
	}
	 	
	sort.Ints(usersScores)

	for score : scoresOfUsers{
		fmt.Print(score + " : ")
		for i = 0; i < wordsOfUsers; i++ {
		words := strings.Split(wordsOfUser, "*")
		fmt.Print (words[0] + " : ")
			for i=1; i < len (words); i++
				fmt.Print(words [i] + "," +)
		}
		fmt.Print ("\n")
	}
}

func stdinToChan (in chan string) {
	reader := bufio.NewReader(os.Stdin)
	for {
		s, _:= reader.ReadString('\n')
		in <- s
	}
}

func receiver (gridsCh chan<- string) {
	message := make([]byte, 1024);
    line, _ := conn.Read(message);
	args := strings.Split(line, "/")
	lineLen = len(args)
	if lineLen >= 1 {
		switch args[0] {
		case "BIENVENUE" :

		case "CONNECTE" && lineLen >= 2 :
			fmt.Println("User : " + args[1] + "joined the game.")
		case "DECONNEXION" && lineLen >= 2 :
			fmt.Println("user : " + args[1] + "leaved the game" )
		case "TOUR" && lineLen >= 2:
			fmt.Println ("New round begun : ")
			gridsCh <- args[1]
		case "MVALIDE" && lineLen >= 2:
			fmt.Println ("Valid word : " + args[1])
		case "MINVALIDE" && lineLen >= 2:
			fmt.Println ("Invalid word, reason : " + args[1] )
		case "RFIN" :
			fmt.Println ("End of round")
		case "BILANMOTS" && lineLen >= 2:
			printBilan(line)
		case "RECEPTION" && lineLen >= 2:
			fmt.Println ("you received a message : " + args[1])
		case "PRECEPTION" && lineLen >= 2:				
			fmt.Println (args[1] + "sent you a new message : " +args[2] )
		}		
	}
}

func sender (gridsCh <-chan string, conn net.Conn) {
	stdin := bufio.NewReader (os.Stdin)
	fmt.Print("Choose user name : ")
	user, _ := stdin.ReadString('\n')
	    fmt.Fprintf(conn,"CONNEXION/" + user + "/"+ "\n")

	for grid := range gridsCh {
		for {
			printGrid (grid)
			fmt.Println("Enter a trajectory : ")
			select {
			case /*stdin*/:

			case /*RFIN*/:
				// continue sur la boucle grid
			}
		}
	}
}

func main() {
	fmt.Println("hello world")
	conn, _ := net.Dial("tcp", "127.0.0.1:8081")
	gridsCh := make (chan, string)
	go receiver (gridsCh)
	go sender (gridsCh, conn)
}
