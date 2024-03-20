class ElevatorService(numberOfElevators: Int, lowestFloor: Int, topFloor: Int) {
  private val availableElevators: Seq[Elevator] = Seq.fill(numberOfElevators)(Elevator())
  def requestElevator(currentFloor: Int, targetFloor: Int): Unit = ???
  def step(): Unit = ???
  def status(): Unit = availableElevators.zipWithIndex.foreach((elevator, index) => elevator.printCurrentStatus(index))
}
