package cw.icfpc.model

import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge
import static cw.icfpc.utils.PolyFormat.getFractionPoint
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

  def 'get adjucent edge'()
  {
    given:
        def p1 = new AtomicPolygon(getFractionPointList('0,0 0,1 1,1'))
        def p2 = new AtomicPolygon(getFractionPointList('0,0 0,-1 1,1'))

    expect:
        getEdge('0,0 1,1') == p1.getAdjacentEdge(p2)
  }
  
  def 'contains' () {
    given:
      def p1 = new AtomicPolygon(getFractionPointList('0,0 1,0 1/2,1'))
      def p2 = new AtomicPolygon(getFractionPointList('0,0 -1,0 -1,-1 0,-1'))
    expect:
      !p1.contains(getFractionPoint('0,0'))
      !p1.contains(getFractionPoint('1,0'))
      !p1.contains(getFractionPoint('1/2,1'))

      !p1.contains(getFractionPoint('0,1/2'))
      // FIXME: Might be a real problem: if a point lies exactly on a polygon side.
      //!p1.contains(getFractionPoint('1/2,0'))
    
      !p1.contains(getFractionPoint('0,2'))
      p1.contains(getFractionPoint('1/5,1/5'))
      !p1.contains(getFractionPoint('2,0'))
      p2.contains(getFractionPoint('-1/2,-1/2'))
      !p2.contains(getFractionPoint('-1/2,1/2'))
      !p2.contains(getFractionPoint('1/2,-1/2'))
  }

  def 'get area'()
  {
    expect:
        1.0d == new AtomicPolygon(getFractionPointList('0,0 0,1 1,1 1,0')).getArea()
        0.25d == new AtomicPolygon(getFractionPointList('1/2,1/2 0,0 1,0')).getArea()
  }
  
  def 'get center' ()
  {
    given:
      def p1 = new AtomicPolygon(getFractionPointList('1/4,1/4 1/8,1/4 1/8,1/8 1/4,1/8'))
      def p2 = new AtomicPolygon(getFractionPointList('1,1 1,-1 -2,-2 -1,1'))
    expect:
      p1.getCenter() == getFractionPoint('3/16,3/16')
      p2.getCenter() == getFractionPoint('-1/4,-1/4')
  }

  def 'test overlapping' () {
    given:
    def p1 = new AtomicPolygon(getFractionPointList('1/4,1/4 1/8,1/4 1/8,1/8 1/4,1/8'))
    def p2 = new AtomicPolygon(getFractionPointList('1/8,1/8 1/8,1/4 1/4,1/4 1/4,1/8'))
    def p3 = new AtomicPolygon(getFractionPointList('1/8,1/8 1/8,1/4 1/4,1/4'))
    def p4 = new AtomicPolygon(getFractionPointList('1/8,1/8 0,0 0,1/8'))

    expect:
      p1.overlaps(p2)
      p1.overlaps(p3)
      p3.overlaps(p1)
      !p1.overlaps(p4)
  }

}
