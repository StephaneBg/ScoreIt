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

import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.BeloteBonusValue
import com.sbgapps.scoreit.data.model.BeloteLap
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_CAPOT
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.source.DataStore
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test


class BeloteSolverTest {

    private val mockDataStore = mockk<DataStore> {
        every { isBeloteScoreRounded() } returns true
    }
    private val solver: BeloteSolver = BeloteSolver(mockDataStore)

    @Test
    fun `Le total des points est de 162`() {
        assertEquals(162, POINTS_TOTAL)
    }

    @Test
    fun `Le total des points pour le capot est de 252`() {
        assertEquals(252, POINTS_CAPOT)
    }

    @Test
    fun `équipe une remporte son contrat car a plus de 81 points`() {
        val points = 82
        val lap = BeloteLap(
            taker = PlayerPosition.ONE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(points, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux remporte son contrat car a plus de 81 points`() {
        val points = 82
        val lap = BeloteLap(
            taker = PlayerPosition.TWO,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.ONE.index])
        assertEquals(points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe une perd son contrat car elle est dedans`() {
        val points = 80
        val lap = BeloteLap(
            taker = PlayerPosition.ONE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals(0, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux perd son contrat car elle est dedans`() {
        val points = 80
        val lap = BeloteLap(
            taker = PlayerPosition.TWO,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals(POINTS_TOTAL, results[PlayerPosition.ONE.index])
        assertEquals(0, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe une perd car elle est capot`() {
        val lap = BeloteLap(
            taker = PlayerPosition.TWO,
            points = POINTS_TOTAL,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(0, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_CAPOT, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux perd car elle est capot`() {
        val lap = BeloteLap(
            taker = PlayerPosition.ONE,
            points = POINTS_TOTAL,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(POINTS_CAPOT, results[PlayerPosition.ONE.index])
        assertEquals(0, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `Le bonus accordé pour la belote est de 20 points`() {
        assertEquals(20, BeloteBonusValue.BELOTE.points)
    }

    @Test
    fun `équipe une remporte son contrat avec la belote et moins de 81 points`() {
        val points = 72
        val lap = BeloteLap(
            taker = PlayerPosition.ONE,
            points = points,
            bonuses = listOf(BeloteBonus(PlayerPosition.ONE, BeloteBonusValue.BELOTE))
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(points + BeloteBonusValue.BELOTE.points, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux remporte son contrat avec la belote et moins de 81 points`() {
        val points = 72
        val lap = BeloteLap(
            taker = PlayerPosition.TWO,
            points = points,
            bonuses = listOf(BeloteBonus(PlayerPosition.TWO, BeloteBonusValue.BELOTE))
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.ONE.index])
        assertEquals(points + BeloteBonusValue.BELOTE.points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe une perd son contrat avec la belote qui est imprenable`() {
        val lap = BeloteLap(
            taker = PlayerPosition.ONE,
            points = 70,
            bonuses = listOf(BeloteBonus(PlayerPosition.ONE, BeloteBonusValue.BELOTE))
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals(BeloteBonusValue.BELOTE.points, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe une perd son contrat avec une tierce`() {
        val lap = BeloteLap(
            taker = PlayerPosition.ONE,
            points = 70,
            bonuses = listOf(BeloteBonus(PlayerPosition.ONE, BeloteBonusValue.RUN_3))
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals(0, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL + BeloteBonusValue.RUN_3.points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux perd son contrat avec la belote qui est imprenable`() {
        val lap = BeloteLap(
            taker = PlayerPosition.TWO,
            points = 70,
            bonuses = listOf(BeloteBonus(PlayerPosition.TWO, BeloteBonusValue.BELOTE))
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals(POINTS_TOTAL, results[PlayerPosition.ONE.index])
        assertEquals(BeloteBonusValue.BELOTE.points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux perd son contrat et l'équipe une a une tierce`() {
        val lap = BeloteLap(
            taker = PlayerPosition.TWO,
            points = 70,
            bonuses = listOf(BeloteBonus(PlayerPosition.ONE, BeloteBonusValue.RUN_3))
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals(0, results[PlayerPosition.TWO.index])
        assertEquals(POINTS_TOTAL + BeloteBonusValue.RUN_3.points, results[PlayerPosition.ONE.index])
    }

    @Test
    fun `litige sans belote`() {
        val points = 81
        val lap = BeloteLap(
            taker = PlayerPosition.ONE,
            points = points
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(points, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `litige avec belote`() {
        val points = 71
        val lap = BeloteLap(
            taker = PlayerPosition.ONE,
            points = points,
            bonuses = listOf(BeloteBonus(PlayerPosition.ONE, BeloteBonusValue.BELOTE))
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(points + BeloteBonusValue.BELOTE.points, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `calcule le score sans arrondi et sans litige`() {
        every { mockDataStore.isBeloteScoreRounded() } returns false
        val laps = listOf(
            BeloteLap(PlayerPosition.ONE, 116),
            BeloteLap(PlayerPosition.TWO, 90),
            BeloteLap(PlayerPosition.TWO, 50),
            BeloteLap(PlayerPosition.ONE, 60),
            BeloteLap(PlayerPosition.ONE, 252),
            BeloteLap(PlayerPosition.TWO, 143)
        )
        val scores: List<Int> = solver.computeScores(laps)

        val expected = listOf(
            116 + 72 + 162 + 0 + 252 + 19,
            46 + 90 + 0 + 162 + 0 + 143
        )
        assertEquals(expected, scores)
    }

    @Test
    fun `calcule le score avec arrondi et sans litige`() {
        val laps = listOf(
            BeloteLap(PlayerPosition.ONE, 116),
            BeloteLap(PlayerPosition.TWO, 90),
            BeloteLap(PlayerPosition.TWO, 50),
            BeloteLap(PlayerPosition.ONE, 60),
            BeloteLap(PlayerPosition.ONE, 252),
            BeloteLap(PlayerPosition.TWO, 143)
        )
        val scores: List<Int> = solver.computeScores(laps)

        val expected = listOf(
            120 + 70 + 160 + 0 + 250 + 20,
            50 + 90 + 0 + 160 + 0 + 140
        )
        assertEquals(expected, scores)
    }

    @Test
    fun `calcule le score sans arrondi et avec litige`() {
        every { mockDataStore.isBeloteScoreRounded() } returns false
        val laps = listOf(
            BeloteLap(PlayerPosition.ONE, 116),
            BeloteLap(PlayerPosition.TWO, 81),
            BeloteLap(PlayerPosition.TWO, 50),
            BeloteLap(PlayerPosition.ONE, 60),
            BeloteLap(PlayerPosition.ONE, 252),
            BeloteLap(PlayerPosition.TWO, 143),
            BeloteLap(PlayerPosition.TWO, 71, listOf(BeloteBonus(PlayerPosition.TWO, BeloteBonusValue.BELOTE))),
            BeloteLap(PlayerPosition.TWO, 120)
        )
        val scores: List<Int> = solver.computeScores(laps)

        val expected = listOf(
            116 + 81 + (81 + 162) + 0 + 252 + 19 + 91 + 42,
            46 + 0 + 0 + 162 + 0 + 143 + 0 + (91 + 120)
        )
        assertEquals(expected, scores)
    }
}
