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
import com.sbgapps.scoreit.data.model.BeloteBonusData
import com.sbgapps.scoreit.data.model.BeloteLapData
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.source.DataStore
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test


class BeloteSolverTest {

    private val mockDataStore = mockk<DataStore>()
    private val solver: BeloteSolver = BeloteSolver(mockDataStore)

    @Test
    fun `equipe une remporte son contrat car a plus de 81 points`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.ONE,
            points = 82,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.computeResults(lap)

        assertTrue(isWon)
        assertEquals(82, results[PlayerPosition.ONE.index])
        assertEquals(80, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `equipe deux remporte son contrat car a plus de 81 points`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.TWO,
            points = 82,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.computeResults(lap)

        assertTrue(isWon)
        assertEquals(80, results[PlayerPosition.ONE.index])
        assertEquals(82, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `equipe une perd son contrat car elle est dedans`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.ONE,
            points = 80,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.computeResults(lap)

        assertFalse(isWon)
        assertEquals(0, results[PlayerPosition.ONE.index])
        assertEquals(162, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `equipe deux perd son contrat car elle est dedans`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.TWO,
            points = 80,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.computeResults(lap)

        assertFalse(isWon)
        assertEquals(162, results[PlayerPosition.ONE.index])
        assertEquals(0, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `equipe une perd car elle est capot`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.TWO,
            points = 252,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.computeResults(lap)

        assertTrue(isWon)
        assertEquals(0, results[PlayerPosition.ONE.index])
        assertEquals(252, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `equipe deux perd car elle est capot`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.ONE,
            points = 252,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.computeResults(lap)

        assertTrue(isWon)
        assertEquals(252, results[PlayerPosition.ONE.index])
        assertEquals(0, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `equipe une remporte son contrat avec la belote et moins de 81 points`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.ONE,
            points = 72,
            bonuses = listOf(BeloteBonusData(PlayerPosition.ONE, BeloteBonus.BELOTE))
        )

        val (results, isWon) = solver.computeResults(lap)

        assertTrue(isWon)
        assertEquals(92, results[PlayerPosition.ONE.index])
        assertEquals(90, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `equipe deux remporte son contrat avec la belote et moins de 81 points`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.TWO,
            points = 72,
            bonuses = listOf(BeloteBonusData(PlayerPosition.TWO, BeloteBonus.BELOTE))
        )

        val (results, isWon) = solver.computeResults(lap)

        assertTrue(isWon)
        assertEquals(90, results[PlayerPosition.ONE.index])
        assertEquals(92, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `equipe une perd son contrat avec la belote`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.ONE,
            points = 70,
            bonuses = listOf(BeloteBonusData(PlayerPosition.ONE, BeloteBonus.BELOTE))
        )

        val (results, isWon) = solver.computeResults(lap)

        assertFalse(isWon)
        assertEquals(20, results[PlayerPosition.ONE.index])
        assertEquals(162, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `equipe deux perd son contrat avec la belote`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.TWO,
            points = 70,
            bonuses = listOf(BeloteBonusData(PlayerPosition.TWO, BeloteBonus.BELOTE))
        )

        val (results, isWon) = solver.computeResults(lap)

        assertFalse(isWon)
        assertEquals(162, results[PlayerPosition.ONE.index])
        assertEquals(20, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `litige sans belote`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.ONE,
            points = 81
        )

        val (results, isWon) = solver.computeResults(lap)

        assertTrue(isWon)
        assertEquals(81, results[PlayerPosition.ONE.index])
        assertEquals(81, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `litige avec belote`() {
        val lap = BeloteLapData(
            scorer = PlayerPosition.ONE,
            points = 71,
            bonuses = listOf(BeloteBonusData(PlayerPosition.ONE, BeloteBonus.BELOTE))
        )

        val (results, isWon) = solver.computeResults(lap)

        assertTrue(isWon)
        assertEquals(91, results[PlayerPosition.ONE.index])
        assertEquals(91, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `calcule le score sans arrondi et sans litige`() {
        every { mockDataStore.isBeloteScoreRounded() } returns false
        val laps = listOf(
            BeloteLapData(PlayerPosition.ONE, 116),
            BeloteLapData(PlayerPosition.TWO, 90),
            BeloteLapData(PlayerPosition.TWO, 50),
            BeloteLapData(PlayerPosition.ONE, 60),
            BeloteLapData(PlayerPosition.ONE, 252),
            BeloteLapData(PlayerPosition.TWO, 143)
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
        every { mockDataStore.isBeloteScoreRounded() } returns true
        val laps = listOf(
            BeloteLapData(PlayerPosition.ONE, 116),
            BeloteLapData(PlayerPosition.TWO, 90),
            BeloteLapData(PlayerPosition.TWO, 50),
            BeloteLapData(PlayerPosition.ONE, 60),
            BeloteLapData(PlayerPosition.ONE, 252),
            BeloteLapData(PlayerPosition.TWO, 143)
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
            BeloteLapData(PlayerPosition.ONE, 116),
            BeloteLapData(PlayerPosition.TWO, 81),
            BeloteLapData(PlayerPosition.TWO, 50),
            BeloteLapData(PlayerPosition.ONE, 60),
            BeloteLapData(PlayerPosition.ONE, 252),
            BeloteLapData(PlayerPosition.TWO, 143),
            BeloteLapData(PlayerPosition.TWO, 71, listOf(BeloteBonusData(PlayerPosition.TWO, BeloteBonus.BELOTE))),
            BeloteLapData(PlayerPosition.TWO, 120)
        )
        val scores: List<Int> = solver.computeScores(laps)

        val expected = listOf(
            116 + 81 + (81 + 162) + 0 + 252 + 19 + 91 + 42,
            46 + 0 + 0 + 162 + 0 + 143 + 0 + (91 + 120)
        )
        assertEquals(expected, scores)
    }
}
