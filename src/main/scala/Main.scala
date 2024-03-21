import scala.annotation.tailrec
import scala.io.StdIn.readLine

def printMenu(): Unit =
  println("Menu:\nSimulate one step:\t\t\tstep or s\n" +
    "Print current status:\t\tprint or p\n" +
    "Add a new elevator request:\trequest or r\n" +
    "Display controls:\t\t\tmenu or m\n" +
    "Exit the simulation:\t\texit or esc")

def processUserInputToRequest(input: String): Array[(Int, Int)] =
  input.split(",")
    .map(_.split(" ")
      .filter(_.nonEmpty)
    ) collect {
    case Array(Int(a), Int(b)) if a != b => (a, b)
  }

@tailrec
def requestDataFromUser(message: String = ""): Array[(Int, Int)] = {
  println(s"${message}Enter current floor and the target floor separated with space, i.e. -2 10.\nYou can add multiple combinations, use `,` to separate them.")
  val combinations: Array[(Int, Int)] = processUserInputToRequest(readLine())
  if (combinations.isEmpty)
    requestDataFromUser("Please provide correct values.\n")
  else
    println(s"Processing requests: ${combinations.mkString(" ")}")
    combinations
}

@main def interface(): Unit =
  println("Welcome to the elevator simulation!\nPlease enter number of elevators you would like to simulate")
  val numberOfElevators = readLine().toInt
  println("Please enter lowest floor in the simulation")
  val lowestFloor = readLine().toInt
  println("Please enter top floor in the simulation")
  val topFloor = readLine().toInt
  val elevatorService: ElevatorService = new ElevatorService(numberOfElevators, lowestFloor, topFloor)
  var simulation: Boolean = true
  printMenu()
  while (simulation) {
    readLine() match {
      case "step" | "s" => elevatorService.step()
      case "print" | "p" => elevatorService.status()
      case "request" | "r" =>
        val requestsToAdd = requestDataFromUser()
        requestsToAdd.foreach((currentFloor, targetFloor) => elevatorService.requestElevator(currentFloor, targetFloor))
      case "menu" | "m" =>
      case "exit" | "esc" => simulation = false
      case _ => println("Sorry, wrong command")
    }
  }
