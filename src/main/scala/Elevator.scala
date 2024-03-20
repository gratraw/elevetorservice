import scala.collection.mutable.ArrayBuffer
import ElevatorStatus.*
import ElevatorDirection.*

class Elevator {
  private var stopsQueue: ArrayBuffer[Int] = ArrayBuffer.empty[Int]
  private var currentFloor: Int = 0
  private var targetFloor: Int = 0
  private var doorClosed: Boolean = true
  private var direction: ElevatorDirection = Idle
  private var status: ElevatorStatus = Stopped
  private def closeDoor(): Unit = this.doorClosed = true
  private def openDoor(): Unit = this.doorClosed = false
  private def changeFloor(direction: Int): Unit = this.currentFloor += direction
  private def orderUp(upQueue: ArrayBuffer[Int]): ArrayBuffer[Int] = upQueue.sorted
  private def orderDown(downQueue: ArrayBuffer[Int]): ArrayBuffer[Int] = downQueue.sorted.reverse
  private def isFloorStop: Boolean = currentFloor == stopsQueue.head
  private def addStopToQueue(stopToAdd: Int, orderingNow: ArrayBuffer[Int] => ArrayBuffer[Int],
                             orderingNext: ArrayBuffer[Int] => ArrayBuffer[Int], newTargetFloor: Boolean): Unit = {
    val (stopsNow, stopsNext) = stopsQueue.splitAt(stopsQueue.indexOf(targetFloor))
    stopsQueue = if (stopToAdd < currentFloor) stopsNow ++ orderingNext(stopsNext :+ stopToAdd)
    else if (newTargetFloor) {
      targetFloor = stopToAdd
      (stopsNow :+ stopToAdd) ++ stopsNext
    }
    else orderingNow(stopsNow :+ stopToAdd) ++ stopsNext
  }

  def isFloorInQueue(floor: Int): Boolean = stopsQueue.contains(floor)
  def addStop(floorToAdd: Int): Unit = {
    direction match
      case Idle => stopsQueue += floorToAdd
      case GoingUp => addStopToQueue(floorToAdd, orderUp, orderDown, floorToAdd > targetFloor)
      case GoingDown => addStopToQueue(floorToAdd, orderDown, orderUp, floorToAdd > targetFloor)
    updateStatus()
  }
  def printCurrentStatus(elevatorNumber: Int): Unit = {
    println(s"""Elevator ${elevatorNumber}:\n
      \tCurrent floor: ${this.currentFloor}
      \tStops queue: ${this.stopsQueue.mkString("[", ", ", "]")}
      \tDoors direction: ${if (this.doorClosed) "closed" else "open"}""")
  }
  private def updateStatus(): Unit =
    if (stopsQueue.isEmpty)
      direction = Idle
      status = Stopped
    else if (currentFloor < stopsQueue.head)
      direction = GoingUp
      status = Moving
    else
      direction = GoingDown
      status = Moving

  private def removeStopFromQueue(stop: Int): Unit = stopsQueue -= stop
  def proceed(): Unit = {
    direction match
      case Idle => updateStatus()
      case GoingUp if doorClosed && !isFloorStop => changeFloor(1) //Elevator goes up
      case GoingDown if doorClosed && !isFloorStop => changeFloor(-1) //Elevator goes down
      case _ if isFloorStop && doorClosed && status == Moving => //Elevator reaches a stop, opens door
        status = Stopped
        openDoor()
      case _ if isFloorStop && !doorClosed && status == Stopped => //Elevator prepares to move further
        removeStopFromQueue(currentFloor)
        closeDoor()
        updateStatus()
      case _ => println("An unexpected error occurred")
  }
}
