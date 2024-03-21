class ElevatorService(numberOfElevators: Int, lowestFloor: Int, topFloor: Int) {
  private val availableElevators: Seq[Elevator] = Seq.fill(numberOfElevators)(Elevator(lowestFloor))

  def requestElevator(currentFloor: Int, targetFloor: Int): Unit = {
    println("Requesting elevator")
  }

  def step(): Unit = availableElevators.foreach(_.proceed())

  def status(): Unit = {
    println(s"Elevator\tCurrent floor\tStatus\t\tDirection\tDoors\tStops queue:")
    availableElevators.zipWithIndex.foreach((elevator, index) => elevator.printCurrentStatus(index))
  }
}
