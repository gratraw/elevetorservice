class ElevatorService(numberOfElevators: Int, lowestFloor: Int, topFloor: Int) {
  val floorRange: Range = lowestFloor to topFloor
  private val availableElevators: Seq[Elevator] = Seq.fill(numberOfElevators)(Elevator(lowestFloor, topFloor))

  def requestElevator(request: ElevatorRequest): Boolean =
    availableElevators.minBy(_.howManyStopsToPickUp(request)).processRequest(request)

  def performSimulationNTimes(numberOfSteps: Int, printStatus: Boolean): Unit =
    if (numberOfSteps != 0) {
      if printStatus then status()
      step()
      performSimulationNTimes(numberOfSteps - 1, printStatus)
    }

  def step(): Unit = availableElevators.foreach(_.proceed())

  def status(): Unit = {
    println(s"Elevator\tCurrent floor\tTarget floor\tStatus\t\tDirection\tDoors\tStops queue")
    availableElevators.zipWithIndex.foreach((elevator, index) => elevator.printCurrentStatus(index))
  }
}