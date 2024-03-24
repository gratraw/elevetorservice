class UtilsTest extends munit.FunSuite {
  test("test isWithinFloorRange true") {
    val elevatorRequest = ElevatorRequest(5, 10)
    assert(isWithinRange(0 to 10, elevatorRequest.bothFloors))
  }

  test("test isWithinFloorRange false") {
    val elevatorRequest = ElevatorRequest(-1, 3)
    assert(!isWithinRange(0 to 10, elevatorRequest.bothFloors))
  }
}