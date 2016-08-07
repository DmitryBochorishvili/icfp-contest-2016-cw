package cw.icfpc

import cw.icfpc.model.AtomicPolygon
import cw.icfpc.model.FractionPoint
import cw.icfpc.model.State
import cw.icfpc.utils.PolyFormat

class StateTestUtils {
  public static State state(String... poly) {
      State s = State.createNew(
              poly.collect{
                  new AtomicPolygon(PolyFormat.getFractionPointList(it))})
      int destId = 0
      s.atomicPolygons[0].vertices.forEach{it.destId = destId++}
      s
  }

  public static FractionPoint p(String s) {
      PolyFormat.getFractionPoint(s)
  }
}
