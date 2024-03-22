import scala.collection.mutable.ArrayBuffer

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html

class ElevatorTest extends munit.FunSuite {
  test("test stopsQueue") {
    val elevator = new Elevator(-1)
    elevator.addMultipleStops(Seq(ElevatorRequest(-1, 2), ElevatorRequest(4, 5), ElevatorRequest(6, 0), ElevatorRequest(8, 3)))
    elevator.printCurrentStatus()
    assert(elevator.getAllQueues.flatten == ArrayBuffer[Int](-1, 2, 4, 5, 8, 6, 3, 0))
    assert(elevator.getTargetFloor == -1)
    assert(elevator.getDirection == ElevatorDirection.GoingDown)
    assert(elevator.getStatus == ElevatorStatus.Stopped)
  }
  
  test("test Elevator routes and steps") {
    val elevator = new Elevator(0)
    println(s"Elevator\tCurrent floor\tStatus\t\tDirection\tDoors\tStops queue:")
    elevator.addRequestToQueue(ElevatorRequest(0,5))
    elevator.addRequestToQueue(ElevatorRequest(6,3))
    elevator.addRequestToQueue(ElevatorRequest(4,2))
    elevator.addRequestToQueue(ElevatorRequest(1,6))
    while (elevator.getCurrentQueue.nonEmpty) {
      elevator.printCurrentStatus(0)
      elevator.proceed()
    }
  }
}
