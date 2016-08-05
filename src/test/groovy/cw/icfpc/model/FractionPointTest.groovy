package cw.icfpc.model

import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge
import static cw.icfpc.utils.PolyFormat.getFractionPoint

class FractionPointTest extends Specification {
  def "Flip"() {
    given:
      def p = getFractionPoint("0,0")
      def mirror1 = getEdge("3,0 3,3")
    expect:
      def actual = p.flip(mirror1)
      actual == getFractionPoint("6,0")
  }
}
