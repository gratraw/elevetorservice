import ElevatorDirection.*
import ElevatorStatus.*

import scala.collection.mutable.ArrayBuffer
// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html

class ElevatorTest extends munit.FunSuite {
  test("test stopsQueue") {
    val elevator = new Elevator(-1)
    elevator.addMultipleStops(Seq(ElevatorRequest(-1, 2), ElevatorRequest(4, 5), ElevatorRequest(6, 0), ElevatorRequest(8, 3)))
    assert(elevator.getAllQueues.flatten == ArrayBuffer[Int](-1, 2, 4, 5, 8, 6, 3, 0))
    assert(elevator.getTargetFloor == -1)
    assert(elevator.getDirection == ElevatorDirection.GoingDown)
    assert(elevator.getStatus == ElevatorStatus.Stopped)
  }

  test("test Elevator routes and steps") {
    val elevator = new Elevator(-1)
    elevator.addMultipleStops(Seq(ElevatorRequest(0, 5),ElevatorRequest(6, 3), ElevatorRequest(4, 2), ElevatorRequest(1, 6)))
    val allStopsInOrder: ArrayBuffer[Int] = ArrayBuffer.empty
    while (elevator.getCurrentQueue.nonEmpty) {
      if (elevator.getStatus == Stopped && (elevator.getDirection == GoingUp || elevator.getDirection == GoingDown) && !elevator.isDoorClosed) allStopsInOrder += elevator.getCurrentFloor
      if (elevator.getCurrentFloor == 5 && elevator.getStatus == ElevatorStatus.Stopped) {
        elevator.addMultipleStops(Seq(ElevatorRequest(2, 4),ElevatorRequest(8, 3)))
      }
      elevator.proceed()
    }
    elevator.addMultipleStops(Seq(ElevatorRequest(7, 3),ElevatorRequest(5, -1),ElevatorRequest(-1, 2),ElevatorRequest(1, 6)))
    while (elevator.getCurrentQueue.nonEmpty) {
      if (elevator.getStatus == Stopped && (elevator.getDirection == GoingUp || elevator.getDirection == GoingDown) && !elevator.isDoorClosed) allStopsInOrder += elevator.getCurrentFloor
      elevator.proceed()
    }
    assert(allStopsInOrder == ArrayBuffer(0, 1, 5, 6, 8, 6, 4, 3, 2, 4, 7, 5, 3, -1, 1, 2, 6))
  }

  test("test stepsToPickup") {
    val elevator = new Elevator(-1)
    elevator.addMultipleStops(Seq(ElevatorRequest(-1, 2), ElevatorRequest(4, 5), ElevatorRequest(6, 0), ElevatorRequest(8, 3)))
    assert(elevator.howManyStopsToPickUp(ElevatorRequest(7,4)) == 6)
  }

  test("test stepsToPickup emptyQueue") {
    val elevator = new Elevator(-1)
    assert(elevator.howManyStopsToPickUp(ElevatorRequest(7, 4)) == 1)
  }
}
