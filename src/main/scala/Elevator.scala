import ElevatorDirection.*
import ElevatorStatus.*

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

class Elevator(lowestFloor: Int, topFloor: Int) {
  private val floorsRange: (Int, Int) = (lowestFloor, topFloor)
  private var stopsQueue: ArrayBuffer[ArrayBuffer[Int]] = ArrayBuffer.fill(3)(ArrayBuffer.empty)
  private var currentFloor: Int = if (lowestFloor < 0) 0 else lowestFloor
  private var targetFloor: Int = currentFloor
  private var doorClosed: Boolean = true
  private var direction: ElevatorDirection = Idle
  private var status: ElevatorStatus = Stopped

  def getAllQueues: ArrayBuffer[ArrayBuffer[Int]] = stopsQueue

  def getCurrentFloor: Int = this.currentFloor

  def isDoorClosed: Boolean = this.doorClosed

  def getStatus: ElevatorStatus = this.status

  private def setStatus(status: ElevatorStatus): Unit = this.status = status

  def getTargetFloor: Int = this.targetFloor

  /**
   *
   * @param request - potential request which ElevatorService needs a score for.
   * @return number of stops that are required to pickup the caller.
   */
  def howManyStopsToPickUp(request: ElevatorRequest): Int = {
    val sameDirection: Boolean = this.direction == request.requestDirection
    val canPickupInSameQueue: Boolean = sameDirection && ((this.direction == GoingUp && this.currentFloor <= request.pickup) || (this.direction == GoingDown && this.currentFloor >= request.pickup))
    val floorPassed = sameDirection && !canPickupInSameQueue

    val (queueIndex, steps) = request.requestDirection match {
      case GoingUp | GoingDown if canPickupInSameQueue => (0, 0)
      case GoingUp | GoingDown if floorPassed => (2, (stopsQueue(0) ++ stopsQueue(1)).size)
      case GoingUp | GoingDown => (1, stopsQueue(0).size)
      case _ => return stopsQueue.flatten.length
    }
    steps + stepsForPotentialRequest(request, if (request.requestDirection == GoingUp) orderUp else orderDown, queueIndex)
  }

  /**
   * Method that calculates number of stops it needs to take in order to fulfil the pickup request
   *
   * @param request  - potential elevator request
   * @param ordering - function that distinct and order queue to provide the potential place in the queue
   * @param index    - index of the queue in general stops queue
   * @return Number of steps that are necessary to stop for pickup request
   */
  private def stepsForPotentialRequest(request: ElevatorRequest, ordering: ArrayBuffer[Int] => ArrayBuffer[Int], index: Int): Int =
    ordering(stopsQueue(index) :+ request.pickup).indexOf(request.pickup) + 1

  def addMultipleStops(stopCollection: Seq[ElevatorRequest]): Unit = stopCollection.foreach(processRequest)

  def processRequest(request: ElevatorRequest): Boolean = {
    if (isWithinRange(lowestFloor to topFloor, request.bothFloors))
      addRequestToQueue(request)
    else
      false
  }

  private def addRequestToQueue(request: ElevatorRequest): Boolean = {
    request.requestDirection match {
      case _ if direction == Idle =>
        setDirection(if request.pickup < this.currentFloor then GoingDown else GoingUp)
        val isSameDirection = getDirection == request.requestDirection
        getDirection match {
          case GoingUp => if (isSameDirection) {
            stopsQueue(0) = orderUp(stopsQueue(0) ++ request.bothFloors)
          } else {
            stopsQueue(0) += request.pickup
            stopsQueue(1) += request.target
          }
          case GoingDown => if (isSameDirection) {
            stopsQueue(0) = orderDown(stopsQueue(0) ++ request.bothFloors)
          } else {
            stopsQueue(0) += request.pickup
            stopsQueue(1) += request.target
          }
          case _ => return false
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
        case _ => return false
      }
    }
    updateTarget()
    true
  }

  /**
   * Updates a target floor after adding a new request to the queue
   */
  private def updateTarget(): Unit =
    this.targetFloor = getDirection match {
      case GoingUp => getCurrentQueue.maxOption.getOrElse(currentFloor)
      case GoingDown => getCurrentQueue.minOption.getOrElse(currentFloor)
      case Idle => currentFloor
    }

  def getCurrentQueue: ArrayBuffer[Int] = this.stopsQueue.headOption match
    case Some(currentQueue) => currentQueue
    case _ => ArrayBuffer.empty

  def getDirection: ElevatorDirection = this.direction

  private def setDirection(direction: ElevatorDirection): Unit = this.direction = direction

  def printCurrentStatus(elevatorNumber: Int = 0): Unit = {
    println(
      s"$elevatorNumber\t\t\t${this.currentFloor}\t\t\t\t${this.targetFloor}\t\t\t\t${this.status}\t\t${this.direction}\t\t${if (this.doorClosed) "closed" else "open"}\t${this.stopsQueue.map(_.mkString("[", ", ", "]")).mkString("[", ", ", "]")}")
  }

  def proceed(): Unit = {
    direction match
      case Idle => updateStatus()
      case GoingUp if doorClosed && !isFloorStop => changeFloor(1) //Elevator goes up
      case GoingDown if doorClosed && !isFloorStop => changeFloor(-1) //Elevator goes down
      case GoingUp | GoingDown if isFloorStop && doorClosed && status == Moving => //Elevator reaches a stop, opens door
        status = Stopped
        openDoor()
      case GoingUp | GoingDown if status == Stopped => //Elevator opens door or prepares to move further
        if (doorClosed)
          openDoor()
        else
          removeStopFromCurrentQueue(this.currentFloor)
          updateStatus()
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
    } else if (getCurrentQueue.isEmpty) {
      rearrangeQueue()
      updateStatus()
    } else {
      getCurrentQueue.headOption match {
        case Some(nextStop: Int) if nextStop != getCurrentFloor =>
          setDirection(if getCurrentFloor < nextStop then GoingUp else GoingDown)
          this.status = Moving
          closeDoor()
        case Some(nextStop: Int) if nextStop == getCurrentFloor && !doorClosed =>
          removeStopFromCurrentQueue(nextStop)
          updateStatus()
        case _ =>
      }
    }
  }

  private def rearrangeQueue(): Unit = {
    this.stopsQueue = this.stopsQueue.tail :+ ArrayBuffer.empty
    if this.stopsQueue(0).isEmpty then
      this.stopsQueue(1).headOption match {
        case Some(firstStop: Int) => this.stopsQueue(0) += firstStop
        case _ => goIdle()
      }
  }

  private def openDoor(): Unit = this.doorClosed = false

  private def goIdle(): Unit = {
    closeDoor()
    setDirection(Idle)
    setStatus(Stopped)
  }
}
