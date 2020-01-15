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
import com.sbgapps.scoreit.data.model.Coinche
import com.sbgapps.scoreit.data.model.CoincheLapData
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.POINTS_CAPOT
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.source.DataStore
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test


class CoincheSolverTest {

    private val mockDataStore = mockk<DataStore> {
        every { isBeloteScoreRounded() } returns true
    }
    private val solver: CoincheSolver = CoincheSolver(mockDataStore)

    @Test
    fun `Le total des points est de 162`() {
        assertEquals(162, POINTS_TOTAL)
    }

    @Test
    fun `Le total des points pour le capot est de 252`() {
        assertEquals(252, POINTS_CAPOT)
    }

    @Test
    fun `équipe une enchérit 120, réalise 130 points et remporte son contrat`() {
        val bid = 120
        val points = 130
        val lap = CoincheLapData(
            taker = PlayerPosition.ONE,
            bid = bid,
            coinche = Coinche.NONE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(bid + points, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux enchérit 100, réalise 120 points et remporte son contrat`() {
        val bid = 100
        val points = 120
        val lap = CoincheLapData(
            taker = PlayerPosition.TWO,
            bid = bid,
            coinche = Coinche.NONE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.ONE.index])
        assertEquals(bid + points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux enchérit 120, réalise 110 points, annonce la belote et remporte son contrat`() {
        val bid = 120
        val points = 110
        val lap = CoincheLapData(
            taker = PlayerPosition.TWO,
            bid = bid,
            coinche = Coinche.NONE,
            points = points,
            bonuses = listOf(BeloteBonusData(PlayerPosition.TWO, BeloteBonus.BELOTE))
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.ONE.index])
        assertEquals(bid + points + BeloteBonus.BELOTE.points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe une enchérit 120, réalise 110 points et chute son contrat`() {
        val bid = 120
        val points = 110
        val lap = CoincheLapData(
            taker = PlayerPosition.ONE,
            bid = bid,
            coinche = Coinche.NONE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals(0, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL + bid, results[PlayerPosition.TWO.index])
    }


    @Test
    fun `équipe une enchérit 140, réalise 110 points, annonce la belote et chute son contrat`() {
        val bid = 140
        val points = 110
        val lap = CoincheLapData(
            taker = PlayerPosition.ONE,
            bid = bid,
            coinche = Coinche.NONE,
            points = points,
            bonuses = listOf(BeloteBonusData(PlayerPosition.ONE, BeloteBonus.BELOTE))
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals(BeloteBonus.BELOTE.points, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL + bid, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux enchérit 130, réalise 100 points et chute son contrat`() {
        val bid = 130
        val points = 100
        val lap = CoincheLapData(
            taker = PlayerPosition.TWO,
            bid = bid,
            coinche = Coinche.NONE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals(POINTS_TOTAL + bid, results[PlayerPosition.ONE.index])
        assertEquals(0, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe une enchérit 140, réalise un capot et remporte son contrat`() {
        val bid = 140
        val points = POINTS_TOTAL
        val lap = CoincheLapData(
            taker = PlayerPosition.ONE,
            bid = bid,
            coinche = Coinche.NONE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(POINTS_CAPOT + bid, results[PlayerPosition.ONE.index])
        assertEquals(0, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux enchérit 130, réalise un capot et remporte son contrat`() {
        val bid = 130
        val points = POINTS_TOTAL
        val lap = CoincheLapData(
            taker = PlayerPosition.TWO,
            bid = bid,
            coinche = Coinche.NONE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals(0, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_CAPOT + bid, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `Le coefficient de la coinche est de 2`() {
        assertEquals(2, Coinche.COINCHE.coefficient)
    }

    @Test
    fun `Le coefficient de la surcoinche est de 4`() {
        assertEquals(4, Coinche.SURCOINCHE.coefficient)
    }

    @Test
    fun `équipe une enchérit 120, réalise 130 points et remporte son contrat alors qu'elle est coinchée`() {
        val bid = 120
        val points = 130
        val lap = CoincheLapData(
            taker = PlayerPosition.ONE,
            bid = bid,
            coinche = Coinche.COINCHE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals((bid + points) * Coinche.COINCHE.coefficient, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe une enchérit 120, réalise 130 points et remporte son contrat alors qu'elle a surcoinché`() {
        val bid = 120
        val points = 130
        val lap = CoincheLapData(
            taker = PlayerPosition.ONE,
            bid = bid,
            coinche = Coinche.SURCOINCHE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertTrue(isWon)
        assertEquals((bid + points) * Coinche.SURCOINCHE.coefficient, results[PlayerPosition.ONE.index])
        assertEquals(POINTS_TOTAL - points, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux enchérit 120, réalise 110 points et chute son contrat alors qu'elle est coinchée`() {
        val bid = 120
        val points = 110
        val lap = CoincheLapData(
            taker = PlayerPosition.TWO,
            bid = bid,
            coinche = Coinche.COINCHE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals((POINTS_TOTAL + bid) * Coinche.COINCHE.coefficient, results[PlayerPosition.ONE.index])
        assertEquals(0, results[PlayerPosition.TWO.index])
    }

    @Test
    fun `équipe deux enchérit 120, réalise 110 points et chute son contrat alors qu'elle a surcoinché`() {
        val bid = 120
        val points = 110
        val lap = CoincheLapData(
            taker = PlayerPosition.TWO,
            bid = bid,
            coinche = Coinche.SURCOINCHE,
            points = points,
            bonuses = emptyList()
        )

        val (results, isWon) = solver.getResults(lap)

        assertFalse(isWon)
        assertEquals((POINTS_TOTAL + bid) * Coinche.SURCOINCHE.coefficient, results[PlayerPosition.ONE.index])
        assertEquals(0, results[PlayerPosition.TWO.index])
    }

}
