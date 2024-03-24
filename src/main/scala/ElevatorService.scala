import ElevatorDirection.*
class ElevatorService(numberOfElevators: Int, lowestFloor: Int, topFloor: Int) {
  private val availableElevators: Seq[Elevator] = Seq.fill(numberOfElevators)(Elevator(lowestFloor))
  def requestElevator(request: ElevatorRequest): Unit = 
   availableElevators.minBy(_.howManyStopsToPickUp(request)).addRequestToQueue(request)

  def step(): Unit = availableElevators.foreach(_.proceed())

  def status(): Unit = {
    println(s"Elevator\tCurrent floor\tStatus\t\tDirection\tDoors\tStops queue:")
    availableElevators.zipWithIndex.foreach((elevator, index) => elevator.printCurrentStatus(index))
  }
}
