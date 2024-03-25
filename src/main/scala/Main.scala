import scala.annotation.tailrec
import scala.io.StdIn.{readInt, readLine}

def printMenu(): Unit =
  println("Menu:\nSimulate one step:\t\t\tstep or s\n" +
            "Simulate multiple steps:\t\tnsteps or n\n" +
            "Print current status:\t\tprint or p\n" +
            "Add a new elevator request:\trequest or r\n" +
            "Display controls:\t\t\tmenu or m\n" +
            "Exit the simulation:\t\texit or esc")

@tailrec
def getValidInt(numberToCompare: Int)(implicit condition: (Int, Int) => Boolean): Int = {
  val potentialValue = getInt
  if (condition(potentialValue, numberToCompare)) potentialValue
  else {
    println("Enter a valid number")
    getValidInt(numberToCompare)
  }
}

def getInt: Int = {
  try
    readInt()
  catch
    case e: java.lang.NumberFormatException =>
      println("Enter a valid number")
      getInt
}

def initiateElevatorService: ElevatorService = {
  implicit val numberValidator: (Int, Int) => Boolean = (numberToCheck: Int, numberToCompare: Int) => numberToCheck > numberToCompare
  println("\nPlease enter number of elevators you would like to simulate")
  val numberOfElevators: Int = getValidInt(0)
  println("Please enter lowest floor in the simulation")
  val lowestFloor: Int = getInt
  println("Please enter top floor in the simulation")
  val topFloor: Int = getValidInt(lowestFloor)
  new ElevatorService(numberOfElevators, lowestFloor, topFloor)
}

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
  println("Welcome to the elevator simulation!")
  val elevatorService: ElevatorService = initiateElevatorService
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
      case "exit" | "esc" | "e" => simulation = false
      case _ => println("Sorry, wrong command")
    }
  }
  