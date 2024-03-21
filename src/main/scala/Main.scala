import scala.annotation.tailrec
import scala.io.StdIn.readLine
def printMenu():Unit = println("Menu:\nSimulate one step:\t\t\tstep or s\nPrint current status:\t\tprint or p\nAdd a new elevator request:\trequest or r\nDisplay controls:\t\t\tmenu or m\nExit the simulation:\t\texit or esc")

@tailrec
def requestDataFromUser(message: String = ""): Array[(Int, Int)] = {
  println(s"${message}\nEnter current floor and the target floor separated with space, i.e. -2 10.\nYou can add multiple combinations, use `,` to separate them.")
  var combinations : Array[(Int, Int)] = readLine()
      .split(",")
      .map(_.split(" ")) collect {
        case Array(a: String, b: String) if (a != b) && a.nonEmpty && b.nonEmpty => (a.toInt, b.toInt)
      }

  if (combinations.isEmpty)
    requestDataFromUser("Please provide correct values.")
  else
    println(combinations.mkString(" "))
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
  while(simulation){
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
