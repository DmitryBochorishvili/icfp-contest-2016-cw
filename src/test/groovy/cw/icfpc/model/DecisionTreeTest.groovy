package cw.icfpc.model

import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getFractionPointList

class DecisionTreeTestSpec extends Specification
{
    def 'check if generateDecisionNodes() is advancing to solution'()
    {
        given:
            def poly1 = new AtomicPolygon(getFractionPointList('0,0 1,0 1/2,1/2')) // two triangles
            def poly2 = new AtomicPolygon(getFractionPointList('0,0 1/2,1/2 0,1'))
            def state = new State([poly1, poly2])

            def expectedPoly = new AtomicPolygon(getFractionPointList('0,0 1,0 1,1 0,1'))
            def expectedState = new State([expectedPoly])
        expect:
            List<State> decisions = DecisionTree.generateDecisionNodes(state)
            decisions.contains(expectedState)

    }

}
