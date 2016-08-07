package cw.icfpc.utils

import static cw.icfpc.StateTestUtils.*
import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge
import static cw.icfpc.utils.PolyFormat.getFractionPointList

class FacetSplitterSpec extends Specification {
  
  def "AddEdgeToCycle"() 
  {
    given:
      def axe = new FacetSplitter([
              getEdge('0,0 0,1'), getEdge('0,1 1,1'), getEdge('1,1 1,0'), getEdge('1,0 0,0'),
              getEdge('0,0 1,1'), getEdge('0,1 1,0'),
      ])
    expect:
      axe.intoFacets() != null
  }

  def "GetCycleEnd"() 
  {

  }

  def "FindCycle"() 
  {

  }

  def "IntoFacets"() 
  {

  }
}
