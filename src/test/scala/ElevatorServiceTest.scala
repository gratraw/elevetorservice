import ElevatorDirection.*
import ElevatorStatus.*

import scala.collection.mutable.ArrayBuffer
// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html

class ElevatorServiceTest extends munit.FunSuite {
  test("test elevator selector") {
    val elevatorService = ElevatorService(4, -2, 5)
    val requests = Seq(ElevatorRequest(0, 5),ElevatorRequest(4, 3), ElevatorRequest(4, 2), ElevatorRequest(1, 6),ElevatorRequest(0, 5),ElevatorRequest(5, 3), ElevatorRequest(4, 2), ElevatorRequest(1, 5), ElevatorRequest(-2,5))
    requests.foreach(elevatorService.requestElevator)
    for a <- 1 to 10 do {
      elevatorService.status()
      elevatorService.step()
    }
  }
}
