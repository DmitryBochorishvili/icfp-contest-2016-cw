package cw.icfpc.model

import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge
import static cw.icfpc.utils.PolyFormat.getFractionPointList

class AtomicPolygonTest extends Specification {
  def "equals and hashCode"() {
    given:
      def vertices = getFractionPointList("0,0 0,1 1,1")
      def a = new AtomicPolygon(vertices)
      vertices = getFractionPointList("0,1 1,1 0,0")
      def b = new AtomicPolygon(vertices)
    expect:
      a.equals(b)
      a.hashCode() == b.hashCode()
  }
  
  def "flip"() {
    given:
      def vertices = getFractionPointList("0,0 0,1 1,1")
      def a = new AtomicPolygon(vertices)
      def mirror1 = getEdge("3,0 3,3")
    expect:
      def actual = a.flip(mirror1)
      actual == new AtomicPolygon(getFractionPointList("6,0 6,1 5,1"))
  }
}
