class ElevatorService(numberOfElevators: Int, lowestFloor: Int, topFloor: Int) {
  private val availableElevators: Seq[Elevator] = Seq.fill(numberOfElevators)(Elevator(lowestFloor, topFloor))
  val floorRange: Range = lowestFloor to topFloor
  def requestElevator(request: ElevatorRequest): Boolean = 
    availableElevators.minBy(_.howManyStopsToPickUp(request)).processRequest(request)

  def step(): Unit = availableElevators.foreach(_.proceed())

  def status(): Unit = {
    println(s"Elevator\tCurrent floor\tTarget floor\tStatus\t\tDirection\tDoors\tStops queue")
    availableElevators.zipWithIndex.foreach((elevator, index) => elevator.printCurrentStatus(index))
  }
}