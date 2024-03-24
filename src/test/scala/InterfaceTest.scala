
class InterfaceTest extends munit.FunSuite {
  test("test request parser all wrong values") {
    assert(processUserInputToRequest("something random, 12 aasd, 11, 3asd") sameElements Array[ElevatorRequest]())
  }
  test("test request empty input") {
    assert(processUserInputToRequest("") sameElements Array[(Int, Int)]())
  }
  test("test request parser all values correct with spaces") {
    assert(processUserInputToRequest("20 0, 11 21, 12, 0 12").flatMap(_.bothFloors) sameElements Array(ElevatorRequest(20, 0), ElevatorRequest(11, 21), ElevatorRequest(0, 12)).flatMap(_.bothFloors))
  }
  test("test request parser all correct") {
    assert(processUserInputToRequest("0 5,1 21,7 3,1 12").flatMap(_.bothFloors) sameElements Array(ElevatorRequest(0, 5), ElevatorRequest(1, 21), ElevatorRequest(7, 3), ElevatorRequest(1, 12)).flatMap(_.bothFloors))
  }
  test("test request parser single request") {
    assert(processUserInputToRequest("0 5").flatMap(_.bothFloors) sameElements Array(ElevatorRequest(0, 5)).flatMap(_.bothFloors))
  }
}
