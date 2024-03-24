import scala.collection.mutable.ArrayBuffer
def orderUp(upQueue: ArrayBuffer[Int]): ArrayBuffer[Int] = upQueue.distinct.sorted

def orderDown(downQueue: ArrayBuffer[Int]): ArrayBuffer[Int] = downQueue.distinct.sorted.reverse

def isWithinRange(floorRange: Range, elemsToCheck: Seq[Int]): Boolean = elemsToCheck.forall(floorRange.contains)