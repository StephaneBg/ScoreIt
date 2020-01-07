/*
 * Copyright 2019 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.data.solver

import com.sbgapps.scoreit.data.model.UniversalLapData
import junit.framework.Assert.assertEquals
import org.junit.Test

class UniversalSolverTest {

    private val solver: UniversalSolver = UniversalSolver()

    @Test
    fun `calcule les résultats sans totaux`() {
        val playerCount = 3
        val points = (1..playerCount).map { it * 10 }
        val lap = UniversalLapData(points)
        val (results, _) = solver.computeResults(lap, false)
        assertEquals(points, results)
    }

    @Test
    fun `calcule les résultats avec totaux`() {
        val playerCount = 3
        val points = (1..playerCount).map { it * 10 }
        val lap = UniversalLapData(points)
        val (results, _) = solver.computeResults(lap, true)
        assertEquals(points.toMutableList().apply { add(points.sum()) }, results)
    }

    @Test
    fun `calcule les scores sans totaux`() {
        val playerCount = 3
        val points = (1..playerCount).map { it * 10 }
        val laps = listOf(
            UniversalLapData(points),
            UniversalLapData(points)
        )
        assertEquals(points.map { it * 2 }, solver.computeScores(laps, playerCount, false))
    }

    @Test
    fun `calcule les scores avec totaux`() {
        val playerCount = 3
        val points = (1..playerCount).map { it * 10 }
        val laps = listOf(
            UniversalLapData(points),
            UniversalLapData(points)
        )
        assertEquals(
            points.map { it * 2 }.toMutableList().apply { add(sum()) },
            solver.computeScores(laps, playerCount, true)
        )
    }
}
