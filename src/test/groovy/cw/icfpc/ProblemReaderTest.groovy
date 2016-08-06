package cw.icfpc

import cw.icfpc.model.AtomicPolygon
import cw.icfpc.model.FractionPoint
import org.apache.commons.lang3.math.Fraction
import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge
import static cw.icfpc.utils.PolyFormat.getFractionPointList

class ProblemReaderTest extends Specification {
    def "atomizePolygon2" () {
        given:
            def normal = getFractionPointList('1/8,0 1/4,0 3/8,1/8 3/8,1/4 1/4,3/8 1/8,3/8 0,1/4 0,1/8')
            def hole = getFractionPointList('1/8,1/8 1/8,1/4 1/4,1/4 1/4,1/8')
            def edges = [
                    getEdge('1/4,0 3/8,1/8'),
                    getEdge('1/8,0 1/4,0'),
                    getEdge('0,1/8 0,1/4'),
                    getEdge('1/8,0 1/8,3/8'),
                    getEdge('1/4,0 1/4,3/8'),
                    getEdge('3/8,1/8 3/8,1/4'),
                    getEdge('1/8,0 0,1/8'),
                    getEdge('0,1/4 1/8,3/8'),
                    getEdge('0,1/8 3/8,1/8'),
                    getEdge('0,1/4 3/8,1/4'),
                    getEdge('3/8,1/4 1/4,3/8'),
                    getEdge('1/8,3/8 1/4,3/8')
            ]
            List<List<FractionPoint>> desc = [normal, hole]

        when:
            def atomizedPolygons = ProblemReader.atomizePolygons2(desc, edges)

        then:
            8 == atomizedPolygons.size()
    }
}
