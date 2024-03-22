import ElevatorDirection.*
import ElevatorStatus.*

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

class Elevator(lowestFloor: Int) {
  private var stopsQueue: ArrayBuffer[ArrayBuffer[Int]] = ArrayBuffer.fill(3)(ArrayBuffer.empty)
  private var currentFloor: Int = if (lowestFloor < 0) 0 else lowestFloor
  private var targetFloor: Int = currentFloor
  private var doorClosed: Boolean = true
  private var direction: ElevatorDirection = Idle
  private var status: ElevatorStatus = Stopped

  def getAllQueues: ArrayBuffer[ArrayBuffer[Int]] = stopsQueue

  def getCurrentFloor: Int = this.currentFloor

  def getStatus: ElevatorStatus = this.status

  def getTargetFloor: Int = this.targetFloor

  private def updateTarget(): Unit = {
    this.targetFloor = getDirection match {
      case GoingUp => getCurrentQueue.maxOption.getOrElse(currentFloor)
      case GoingDown => getCurrentQueue.minOption.getOrElse(currentFloor)
      case Idle => currentFloor
    }
  }

  def howManyStopsToPickUp(request: ElevatorRequest): Int =
    request.requestDirection match {
      case GoingUp if this.direction == GoingUp && this.currentFloor <= request.pickup => //Can be pickedUp in the same queue
        stepsForPotentialRequest(request, orderUp, 0)
      case GoingDown if this.direction == GoingDown && this.currentFloor >= request.pickup => //Can be pickedUp in the same queue
        stepsForPotentialRequest(request, orderDown, 0)
      case GoingUp if this.direction == GoingUp => //Floor passed, need to take whole tour up and down
        (stopsQueue(0) ++ stopsQueue(1)).size + stepsForPotentialRequest(request, orderUp, 2)
      case GoingDown if this.direction == GoingDown => //Floor passed, need to take whole tour down and up
        (stopsQueue(0) ++ stopsQueue(1)).size + stepsForPotentialRequest(request, orderDown, 2)
      case GoingUp => stopsQueue(0).size + stepsForPotentialRequest(request, orderUp, 1) // Will be picked up after direction change
      case GoingDown => stopsQueue(0).size + stepsForPotentialRequest(request, orderDown, 1) // Will be picked up after direction change
      case _ => stopsQueue.flatten.length //Unpredicted case, get all stops as score
    }

  private def stepsForPotentialRequest(request: ElevatorRequest, ordering: ArrayBuffer[Int] => ArrayBuffer[Int], index: Int): Int =
    ordering(stopsQueue(index) :+ request.pickup).indexOf(request.pickup) + 1

  def addMultipleStops(stopCollection: Seq[ElevatorRequest]): Unit = stopCollection.foreach(addRequestToQueue)

  def addRequestToQueue(request: ElevatorRequest): Unit = {
    request.requestDirection match {
      case _ if direction == Idle =>
        if request.pickup < this.currentFloor then setDirection(GoingDown) else setDirection(GoingUp)
        getDirection match {
          case GoingUp => if (request.requestDirection == GoingUp) {
            stopsQueue(0) = orderUp(stopsQueue(0) ++ request.bothFloors)
          } else {
            stopsQueue(0) += request.pickup
            stopsQueue(1) += request.target
          }
          case GoingDown => if (request.requestDirection == GoingDown) {
            stopsQueue(0) = orderDown(stopsQueue(0) ++ request.bothFloors)
          } else {
            stopsQueue(0) += request.pickup
            stopsQueue(1) += request.target
          }
          case _ =>
        }
      case GoingUp if direction == GoingUp =>
        val queueIndex: Int = if this.currentFloor <= request.pickup then 0 else 2
        stopsQueue(queueIndex) = orderUp(stopsQueue(queueIndex) ++ request.bothFloors)
      case GoingDown if this.direction == GoingDown =>
        val queueIndex: Int = if this.currentFloor >= request.pickup then 0 else 2
        stopsQueue(queueIndex) = orderDown(stopsQueue(queueIndex) ++ request.bothFloors)
      case _ => getDirection match {
        case GoingUp => stopsQueue(1) = orderDown(stopsQueue(1) ++ request.bothFloors)
        case GoingDown => stopsQueue(1) = orderUp(stopsQueue(1) ++ request.bothFloors)
        case _ =>
      }
    }
    updateTarget()
  }

  private def orderUp(upQueue: ArrayBuffer[Int]): ArrayBuffer[Int] = upQueue.sorted

  private def orderDown(downQueue: ArrayBuffer[Int]): ArrayBuffer[Int] = downQueue.sorted.reverse

  private def setDirection(direction: ElevatorDirection): Unit = this.direction = direction

  def getDirection: ElevatorDirection = this.direction

  def printCurrentStatus(elevatorNumber: Int = 0): Unit = {
    println(
      s"$elevatorNumber\t\t\t${this.currentFloor}\t\t\t\t${this.status}\t\t${this.direction}\t\t${if (this.doorClosed) "closed" else "open"}\t${this.stopsQueue.map(_.mkString("[", ", ", "]")).mkString("[", ", ", "]")}")
  }

  //TODO: fix
  def proceed(): Unit = {
    direction match
      case Idle => updateStatus()
      case GoingUp if doorClosed && !isFloorStop => changeFloor(1) //Elevator goes up
      case GoingDown if doorClosed && !isFloorStop => changeFloor(-1) //Elevator goes down
      case _ if isFloorStop && doorClosed && status == Moving => //Elevator reaches a stop, opens door
        status = Stopped
        openDoor()
      case _ if !doorClosed && status == Stopped => //Elevator prepares to move further
        removeStopFromCurrentQueue(this.currentFloor)
        closeDoor()
        updateStatus()
      case _ if doorClosed && status == Stopped => openDoor()
      case _ => println("An unexpected error occurred")
  }

  private def closeDoor(): Unit = this.doorClosed = true

  private def changeFloor(direction: Int): Unit = this.currentFloor += direction

  private def removeStopFromCurrentQueue(floorToRemove: Int): Unit = this.stopsQueue(0) -= floorToRemove

  private def isFloorStop: Boolean = getCurrentQueue.headOption.contains(currentFloor)

  @tailrec
  private def updateStatus(): Unit = {
    if (this.stopsQueue.flatten.isEmpty) {
      goIdle()
    }
    if (getCurrentQueue.isEmpty) {
      rearrangeQueue()
      updateStatus()
    } else {
      getCurrentQueue.headOption match {
        case Some(nextStop: Int) =>
          if this.currentFloor < nextStop then setDirection(GoingUp) else setDirection(GoingDown)
          this.status = Moving
        case _ => goIdle()
      }
    }
  }

  private def rearrangeQueue(): Unit = {
    this.stopsQueue = this.stopsQueue.tail
    if this.stopsQueue(0).isEmpty then
      this.stopsQueue(1).headOption match {
        case Some(firstStop: Int) => this.stopsQueue(0) += firstStop
        case _ => goIdle()
      }
  }

  private def prepareElevator(): Unit = {
    openDoor()
    getCurrentQueue.headOption match {
      case Some(nextStop: Int) =>
        if (this.currentFloor < nextStop) setDirection(GoingUp) else setDirection(GoingDown)
        setStatus(Stopped)
      case _ =>
        goIdle()
    }
  }

  private def openDoor(): Unit = this.doorClosed = false
  private def goIdle(): Unit =
    setDirection(Idle)
    setStatus(Stopped)
  private def setStatus(status: ElevatorStatus): Unit = this.status = status
  def getCurrentQueue: ArrayBuffer[Int] = this.stopsQueue.head
}
