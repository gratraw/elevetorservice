

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html

class InterfaceTest extends munit.FunSuite {
  test("test request parser all wrong values") {
    assert(processUserInputToRequest("something random, 12 aasd, 11, 3asd") sameElements Array[(Int, Int)]())
  }
  test("test request empty input") {
    assert(processUserInputToRequest("") sameElements Array[(Int, Int)]())
  }
  test("test request parser all values correct with spaces") {
    assert(processUserInputToRequest("20 0, 11 21, 12, 0 12") sameElements Array[(Int, Int)]((20, 0), (11, 21), (0, 12)))
  }
  test("test request parser all correct") {
    assert(processUserInputToRequest("0 5,1 21,7 3,1 12") sameElements Array[(Int, Int)]((0, 5), (1, 21), (7, 3), (1, 12)))
  }
  test("test request parser single request") {
    assert(processUserInputToRequest("0 5") sameElements Array[(Int, Int)]((0, 5)))
  }
}
