import scala.annotation.tailrec
import scala.io.StdIn.readLine

def printMenu(): Unit =
  println("Menu:\nSimulate one step:\t\t\tstep or s\n" +
            "Simulate multiple steps:\t\tnsteps or n\n" +
            "Print current status:\t\tprint or p\n" +
            "Add a new elevator request:\trequest or r\n" +
            "Display controls:\t\t\tmenu or m\n" +
            "Exit the simulation:\t\texit or esc")

def processUserInputToRequest(input: String): Array[ElevatorRequest] =
  input.split(",")
    .map(_.split(" ")
      .filter(_.nonEmpty)
    ) collect {
    case Array(Int(a), Int(b)) if a != b => ElevatorRequest(a, b)
  }

def performNumberOfSteps(): (Int, Boolean) = {
  print("Enter number of steps to simulate: ")
  val numberOfLines: Int = readLine().toIntOption.getOrElse(0)
  println("Do you want to print each step? y/n")
  val printSteps: Boolean = Seq("y", "yes").contains(readLine().toLowerCase)
  (numberOfLines, printSteps)
}
def printRequestError(elevatorRequest: ElevatorRequest): Unit =
  println(s"Unfortunately, the request to call an elevator from floor ${elevatorRequest.pickup} to ${elevatorRequest.target} please try again")

@tailrec
def requestDataFromUser(message: String = ""): Array[ElevatorRequest] = {
  println(s"${message}Enter current floor and the target floor separated with space, i.e. -2 10.\nYou can add multiple combinations, use `,` to separate them.")
  val combinations: Array[ElevatorRequest] = processUserInputToRequest(readLine())
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
    print("Enter command: ")
    readLine().toLowerCase match {
      case "step" | "s" => elevatorService.step()
      case "nsteps" | "n" => val (steps, printStatus) = performNumberOfSteps()
        elevatorService.performSimulationNTimes(steps, printStatus)
      case "print" | "p" => elevatorService.status()
      case "request" | "r" =>
        val requestsToAdd = requestDataFromUser()
        requestsToAdd collect (elevatorRequest => {
          if !elevatorService.requestElevator(elevatorRequest) then
            printRequestError(elevatorRequest)
        })
      case "menu" | "m" => printMenu()
      case "exit" | "esc" => simulation = false
      case _ => println("Sorry, wrong command")
    }
  }
