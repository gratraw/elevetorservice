import scala.collection.mutable.ArrayBuffer

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html

class ElevatorTest extends munit.FunSuite {
  test("test stopsQueue") {
    val elevator = new Elevator(-1)
    elevator.addMultipleStops(Seq((-1, 2), (4, 5), (6, 0), (8, 3)))
    assert(elevator.getQueue == ArrayBuffer[Int](-1, 0, 2, 3, 4, 5, 6, 8))
    assert(elevator.getTargetFloor == -1)
    assert(elevator.getDirection == ElevatorDirection.GoingDown)
    assert(elevator.getStatus == ElevatorStatus.Stopped)
  }
  
  test("test Elevator routes and steps") {
    val elevator = new Elevator(0)
    println(s"Elevator\tCurrent floor\tStatus\t\tDirection\tDoors\tStops queue:")
    elevator.addStop(0,5)
    elevator.addStop(6,3)
    elevator.addStop(4,2)
    elevator.addStop(1,6)
    while (elevator.getQueue.nonEmpty) {
      elevator.printCurrentStatus(0)
      elevator.proceed()
    }
  }
}
