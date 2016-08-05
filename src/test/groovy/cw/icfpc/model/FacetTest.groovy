package cw.icfpc.model

import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getFacet

class FacetTest extends Specification {
  def "equals and hashCode"() {
    given:
      def a = getFacet("0,0 1,1")
      def b = getFacet("1,1 0,0")
    expect:
      a.equals(b)
      a.hashCode() == b.hashCode()
  }
}
