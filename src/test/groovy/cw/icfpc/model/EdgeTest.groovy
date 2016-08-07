package cw.icfpc.model

import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge

class EdgeTest extends Specification {
  def "equals and hashCode"() {
    given:
      def a = getEdge("0,0 1,1")
      def b = getEdge("1,1 0,0")
    expect:
      a.equals(b)
      a.hashCode() == b.hashCode()
  }
  
  def "flip"() {
    given:
      def e = getEdge("0,0 1,1")
      def mirror = getEdge("2,0 0,2")
    expect:
      e.flip(mirror) == getEdge("1,1 2,2")
  }

  def "canBeMergedWith"() {
    given:
      def e1 = getEdge("0,0 1/2,1/2")
      def e2 = getEdge("1,1 1/2,1/2")

      def a1 = getEdge("0,0 1/2,1/2")
      def a2 = getEdge("1/2,1/2 1/2,1")

      def b1 = getEdge("0,0 1,0")
      def b2 = getEdge("0,0 0,1")
    expect:
      null != e1.mergeWith(e2)
      null == a1.mergeWith(a2)
      null == b1.mergeWith(b2)
  }
}
