import ElevatorDirection.*
class ElevatorService(numberOfElevators: Int, lowestFloor: Int, topFloor: Int) {
  private val availableElevators: Seq[Elevator] = Seq.fill(numberOfElevators)(Elevator(lowestFloor))

  def requestElevator(currentFloor: Int, targetFloor: Int): Unit = {
    if currentFloor < targetFloor then
      val test1 = availableElevators.filter(elevator => elevator.getCurrentFloor <= currentFloor && Seq(GoingUp, Idle).contains(elevator.getDirection)).minBy(_.getCurrentFloor - currentFloor)
    else {
      val test2 = availableElevators.filter(elevator => elevator.getCurrentFloor >= currentFloor && Seq(GoingDown, Idle).contains(elevator.getDirection)).minBy(_.getCurrentFloor - currentFloor)
    }
    
    println("Requesting elevator")
  }

  def step(): Unit = availableElevators.foreach(_.proceed())

  def status(): Unit = {
    println(s"Elevator\tCurrent floor\tStatus\t\tDirection\tDoors\tStops queue:")
    availableElevators.zipWithIndex.foreach((elevator, index) => elevator.printCurrentStatus(index))
  }
}
