import ElevatorDirection.*
class ElevatorRequest (callingFloor: Int,targetFloor: Int){
  private def getRequestDirection: ElevatorDirection =  if (callingFloor < targetFloor) GoingUp else GoingDown
  val bothFloors: Array[Int] = Array(callingFloor, targetFloor)
  val requestDirection: ElevatorDirection = getRequestDirection
  val pickup: Int = callingFloor
  val target: Int = targetFloor
}