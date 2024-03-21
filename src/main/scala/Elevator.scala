import ElevatorDirection.*
import ElevatorStatus.*

import scala.collection.mutable.ArrayBuffer

class Elevator(lowestFloor: Int) {
  private var stopsQueue: ArrayBuffer[Int] = ArrayBuffer.empty[Int]
  private var currentFloor: Int = if (lowestFloor < 0) 0 else lowestFloor
  private var targetFloor: Int = 0
  private var doorClosed: Boolean = true
  private var direction: ElevatorDirection = Idle
  private var status: ElevatorStatus = Stopped

  def getQueue: ArrayBuffer[Int] = this.stopsQueue

  def getCurrentFloor: Int = this.currentFloor
  def getDirection: ElevatorDirection = this.direction

  def getStatus: ElevatorStatus = this.status

  def getTargetFloor: Int = this.targetFloor

  def addMultipleStops(stopCollection: Seq[(Int,Int)]): Unit = stopCollection.foreach(addStop)

  def addStop(callingFloor: Int, floorToAdd: Int): Unit = {
    if (!isFloorInQueue(floorToAdd) && !isFloorInQueue(callingFloor)) {
      direction match
        case Idle =>
          this.targetFloor = floorToAdd
          if this.currentFloor != callingFloor then
            if this.currentFloor < callingFloor then
              this.direction = GoingUp
              this.stopsQueue +: Array(callingFloor, floorToAdd)
            else
              this.direction = GoingDown
            this.stopsQueue +: Array(callingFloor, floorToAdd)
          else {
            this.stopsQueue += floorToAdd
            prepareElevator()
          }
        case GoingUp => addStopToQueue(floorToAdd, orderUp, orderDown, floorToAdd > targetFloor, (floor: Int) => floor > currentFloor)
        case GoingDown => addStopToQueue(floorToAdd, orderDown, orderUp, floorToAdd < targetFloor, (floor: Int) => floor < currentFloor)
    }
  }

  private def orderUp(upQueue: ArrayBuffer[Int]): ArrayBuffer[Int] = upQueue.sorted

  private def orderDown(downQueue: ArrayBuffer[Int]): ArrayBuffer[Int] = downQueue.sorted.reverse

  private def addStopToQueue(stopToAdd: Int,
                             orderingNow: ArrayBuffer[Int] => ArrayBuffer[Int],
                             orderingNext: ArrayBuffer[Int] => ArrayBuffer[Int],
                             newTargetFloor: Boolean,
                             partitionRule: Int => Boolean): Unit = {
    val (stopsNow, stopsNext) = stopsQueue.partition(partitionRule)
    stopsQueue = if (stopToAdd > currentFloor)
      stopsNow ++ orderingNext(stopsNext :+ stopToAdd)
    else if (newTargetFloor) {
      targetFloor = stopToAdd
      (stopsNow :+ stopToAdd) ++ stopsNext
    }
    else orderingNow(stopsNow :+ stopToAdd) ++ stopsNext
  }

  private def isFloorInQueue(floor: Int): Boolean = stopsQueue.contains(floor)

  private def prepareElevator(): Unit =
    openDoor()
    if (this.currentFloor < this.stopsQueue.head)
      this.direction = GoingUp
    else this.direction = GoingDown
    this.status = Stopped

  private def openDoor(): Unit = this.doorClosed = false

  def printCurrentStatus(elevatorNumber: Int = 0): Unit = {
    println(
      s"$elevatorNumber\t\t\t${this.currentFloor}\t\t\t\t${this.status}\t\t${this.direction}\t\t${if (this.doorClosed) "closed" else "open"}\t${this.stopsQueue.mkString("[", ", ", "]")}")
  }

  def proceed(): Unit = {
    direction match
      case Idle => updateStatus()
      case GoingUp if doorClosed && !isFloorStop => changeFloor(1) //Elevator goes up
      case GoingDown if doorClosed && !isFloorStop => changeFloor(-1) //Elevator goes down
      case _ if isFloorStop && doorClosed && status == Moving => //Elevator reaches a stop, opens door
        status = Stopped
        openDoor()
      case _ if !doorClosed && status == Stopped => //Elevator prepares to move further
        removeStopFromQueue(this.currentFloor)
        closeDoor()
        updateStatus()
      case _ => println("An unexpected error occurred")
  }

  private def closeDoor(): Unit = this.doorClosed = true

  private def changeFloor(direction: Int): Unit = this.currentFloor += direction

  private def isFloorStop: Boolean = this.currentFloor == this.stopsQueue.head

  private def updateStatus(): Unit =
    if (this.stopsQueue.isEmpty)
      this.direction = Idle
      this.status = Stopped
    else if (this.currentFloor < this.stopsQueue.head)
      this.direction = GoingUp
      this.status = Moving
    else
      this.direction = GoingDown
      this.status = Moving

  private def removeStopFromQueue(stop: Int): Unit = this.stopsQueue -= stop
}
