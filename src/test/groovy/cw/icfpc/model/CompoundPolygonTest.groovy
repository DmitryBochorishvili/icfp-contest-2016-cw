package cw.icfpc.model

import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge
import static cw.icfpc.utils.PolyFormat.getFractionPointList

class CompoundPolygonTest extends Specification {
  def "Flip"() {
    given:
      def a = new AtomicPolygon(getFractionPointList("0,0 0,1 1,1"))
      def b = new AtomicPolygon(getFractionPointList("2,2 3,3 0,8"))
      def c = new CompoundPolygon([a, b])
      def mirror1 = getEdge("0,0 -3,0")
    expect:
      def actual = c.flip(mirror1)
      actual == new CompoundPolygon([
          new AtomicPolygon(getFractionPointList("0,0 0,-1 1,-1")),
          new AtomicPolygon(getFractionPointList("2,-2 3,-3 0,-8"))
        ])
  }
}
