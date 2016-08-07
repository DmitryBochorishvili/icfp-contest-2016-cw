package cw.icfpc.model

import spock.lang.Ignore
import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getFractionPointList

class StateSpec extends Specification
{
    private State state(String... poly) {
        State s = State.createNew(
                poly.collect{
                    new AtomicPolygon(getFractionPointList(it))})
        int destId = 0
        s.atomicPolygons[0].vertices.forEach{it.destId = destId++}
        s
    }

    def 'final state: true for initial square'()
    {
        expect:
            state('0,0 1,0 1,1 0,1').isFinalState()
    }

    def 'final state: true for moved square'()
    {
        expect:
            state('1/2,0 3/2,0 3/2,1 1/2,1').isFinalState()
    }

    def 'final state: false for not a square'()
    {
        expect:
            !state('0,0 1,1 2,1 1,0').isFinalState()
    }

    def 'final state: adjacent polygons'()
    {
        given:
            def p1 = new AtomicPolygon(getFractionPointList('0,0 0,1 1/2,1 1,1/2 1,0'))
            def p2 = new AtomicPolygon(getFractionPointList('1/2,1 1,1/2 1,1'))

        expect:
            State.createNew([p1, p2]).isFinalState() == true
    }

    def 'toSolution - just a square'()
    {
        expect:
            state('0,0 0,1 1,1 1,0').toSolution() == """4
1,0
0,0
1,1
0,1
1
4 1 3 2 0
1,0
0,0
1,1
0,1
""" 
    }
    
    def 'toSolution - problem #1'()
    {
        expect:
            state('0,0 1,0 1,1 0,1').toSolution() == """4
1,0
0,0
1,1
0,1
1
4 1 0 2 3
1,0
0,0
1,1
0,1
""" 
    }
    
    @Ignore
    def 'toSolution - one unfold'()
    {
        given:
            def currentState = state('0,0 1/2,0 1/2,1 0,1')
            def decisions = DecisionTree.generateDecisionNodes(currentState)
            def solution = decisions.find{it.isFinalState()} 
        
        expect:
            solution != null
            solution.toSolution() == """6
0,0
0,1
1,1
1,0
1/2,0
1/2,1
1
4 0 1 2 3
0,0
0,1
0,1
0,0
1/2,0
1/2,1
"""
    }

    // To consider, do not delete this test
//    def 'calculate adjacent atomics correctly'()
//    {
//        given:
//            def p1 = new AtomicPolygon(getFractionPointList('0,1 0,0 1/2,1/2'))
//            def p2 = new AtomicPolygon(getFractionPointList('1,0 1,1 0,0'))
//            def p3 = new AtomicPolygon(getFractionPointList('0,1 1,1 1/2,1/2'))
//
//        when:
//            def state = new State([p1, p2, p3]);
//
//        then:
//            state.getAdjacentPolygons(p1).size() == 2
//            state.getAdjacentPolygons(p2).size() == 2
//            state.getAdjacentPolygons(p3).size() == 2
//    }
}
