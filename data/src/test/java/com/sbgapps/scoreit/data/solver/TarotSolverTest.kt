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

import com.sbgapps.scoreit.data.model.*
import org.junit.Assert
import org.junit.Test
import kotlin.math.abs

class TarotSolverTest {

    private val solver: TarotSolver = TarotSolver()

    // Trois joueurs
    @Test
    fun `trois joueurs - joueur un fait une prise et remporte le contrat avec aucun bout`() {
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_1,
            partner = PLAYER_NONE,
            bid = BID_PRISE,
            oudlers = OUDLER_NONE,
            points = 58
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (58 - 56 + 25)
        Assert.assertEquals(points * 2, results[PLAYER_1])
        Assert.assertEquals(-points, results[PLAYER_2])
        Assert.assertEquals(-points, results[PLAYER_3])
    }

    @Test
    fun `trois joueurs - joueur un fait une prise et chute le contrat avec aucun bout`() {
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_1,
            partner = PLAYER_NONE,
            bid = BID_PRISE,
            oudlers = OUDLER_NONE,
            points = 50
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val points = abs(50 - 56) + 25
        Assert.assertEquals(-points * 2, results[PLAYER_1])
        Assert.assertEquals(points, results[PLAYER_2])
        Assert.assertEquals(points, results[PLAYER_3])
    }

    @Test
    fun `trois joueurs - joueur un fait une garde et remporte le contrat avec un bout`() {
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_1,
            partner = PLAYER_NONE,
            bid = BID_GARDE,
            oudlers = OUDLER_21_MSK,
            points = 58
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (58 - 51 + 25) * 2
        Assert.assertEquals(points * 2, results[PLAYER_1])
        Assert.assertEquals(-points, results[PLAYER_2])
        Assert.assertEquals(-points, results[PLAYER_3])
    }

    @Test
    fun `trois joueurs - joueur deux fait une garde sans et remporte le contrat avec deux bouts`() {
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_2,
            partner = PLAYER_NONE,
            bid = BID_GARDE_SANS,
            oudlers = OUDLER_PETIT_MSK or OUDLER_EXCUSE_MSK,
            points = 56
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (56 - 41 + 25) * 4
        Assert.assertEquals(-points, results[PLAYER_1])
        Assert.assertEquals(points * 2, results[PLAYER_2])
        Assert.assertEquals(-points, results[PLAYER_3])
    }

    @Test
    fun `trois joueurs - joueur trois fait une garde contre et remporte le contrat avec trois bouts`() {
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_3,
            partner = PLAYER_NONE,
            bid = BID_GARDE_CONTRE,
            oudlers = OUDLER_EXCUSE_MSK or OUDLER_21_MSK or OUDLER_PETIT_MSK,
            points = 64
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (64 - 36 + 25) * 6
        Assert.assertEquals(-points, results[PLAYER_1])
        Assert.assertEquals(-points, results[PLAYER_2])
        Assert.assertEquals(points * 2, results[PLAYER_3])
    }


    // Quatre joueurs
    @Test
    fun `quatre joueurs - joueur un fait une prise et remporte le contrat avec aucun bout`() {
        val lap = TarotLapData(
            playerCount = 4,
            taker = PLAYER_1,
            partner = PLAYER_NONE,
            bid = BID_PRISE,
            oudlers = OUDLER_NONE,
            points = 58
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (58 - 56 + 25)
        Assert.assertEquals(points * 3, results[PLAYER_1])
        Assert.assertEquals(-points, results[PLAYER_2])
        Assert.assertEquals(-points, results[PLAYER_3])
        Assert.assertEquals(-points, results[PLAYER_4])
    }

    @Test
    fun `quatre joueurs - joueur deux fait une prise et chute le contrat avec deux bouts`() {
        val lap = TarotLapData(
            playerCount = 4,
            taker = PLAYER_2,
            partner = PLAYER_NONE,
            bid = BID_PRISE,
            oudlers = OUDLER_21_MSK or OUDLER_EXCUSE_MSK,
            points = 38
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val points = abs(38 - 41) + 25
        Assert.assertEquals(points, results[PLAYER_1])
        Assert.assertEquals(-points * 3, results[PLAYER_2])
        Assert.assertEquals(points, results[PLAYER_3])
        Assert.assertEquals(points, results[PLAYER_4])
    }

    @Test
    fun `quatre joueurs - joueur deux fait une garde et remporte le contrat avec un bout`() {
        val lap = TarotLapData(
            playerCount = 4,
            taker = PLAYER_2,
            partner = PLAYER_NONE,
            bid = BID_GARDE,
            oudlers = OUDLER_PETIT_MSK,
            points = 58
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (58 - 51 + 25) * 2
        Assert.assertEquals(-points, results[PLAYER_1])
        Assert.assertEquals(points * 3, results[PLAYER_2])
        Assert.assertEquals(-points, results[PLAYER_3])
        Assert.assertEquals(-points, results[PLAYER_4])
    }

    @Test
    fun `quatre joueurs - joueur trois fait une garde sans et remporte le contrat avec deux bouts`() {
        val lap = TarotLapData(
            playerCount = 4,
            taker = PLAYER_3,
            partner = PLAYER_NONE,
            bid = BID_GARDE_SANS,
            oudlers = OUDLER_PETIT_MSK or OUDLER_21_MSK,
            points = 56
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (56 - 41 + 25) * 4
        Assert.assertEquals(-points, results[PLAYER_1])
        Assert.assertEquals(-points, results[PLAYER_2])
        Assert.assertEquals(points * 3, results[PLAYER_3])
        Assert.assertEquals(-points, results[PLAYER_4])
    }

    @Test
    fun `quatre joueurs - joueur quatre fait une garde contre et remporte le contrat avec trois bouts`() {
        val lap = TarotLapData(
            playerCount = 4,
            taker = PLAYER_4,
            partner = PLAYER_NONE,
            bid = BID_GARDE_CONTRE,
            oudlers = OUDLER_EXCUSE_MSK or OUDLER_21_MSK or OUDLER_PETIT_MSK,
            points = 64
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (64 - 36 + 25) * 6
        Assert.assertEquals(-points, results[PLAYER_1])
        Assert.assertEquals(-points, results[PLAYER_2])
        Assert.assertEquals(-points, results[PLAYER_3])
        Assert.assertEquals(points * 3, results[PLAYER_4])
    }


    // Cinq joueurs
    @Test
    fun `cinq joueurs - joueur un fait une prise avec joueur deux et remporte le contrat avec aucun bout`() {
        val lap = TarotLapData(
            playerCount = 5,
            taker = PLAYER_1,
            partner = PLAYER_2,
            bid = BID_PRISE,
            oudlers = OUDLER_NONE,
            points = 58
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (58 - 56 + 25)
        Assert.assertEquals(points * 2, results[PLAYER_1])
        Assert.assertEquals(points, results[PLAYER_2])
        Assert.assertEquals(-points, results[PLAYER_3])
        Assert.assertEquals(-points, results[PLAYER_4])
        Assert.assertEquals(-points, results[PLAYER_5])
    }

    @Test
    fun `cinq joueurs - joueur trois fait une garde avec joueur quatre et chute le contrat avec un bout`() {
        val lap = TarotLapData(
            playerCount = 5,
            taker = PLAYER_3,
            partner = PLAYER_4,
            bid = BID_GARDE,
            oudlers = OUDLER_21_MSK,
            points = 46
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val points = (abs(46 - 51) + 25) * 2
        Assert.assertEquals(points, results[PLAYER_1])
        Assert.assertEquals(points, results[PLAYER_2])
        Assert.assertEquals(-points * 2, results[PLAYER_3])
        Assert.assertEquals(-points, results[PLAYER_4])
        Assert.assertEquals(points, results[PLAYER_5])
    }

    @Test
    fun `cinq joueurs - joueur cinq fait une prise seul quatre et remporte le contrat avec deux bout`() {
        val lap = TarotLapData(
            playerCount = 5,
            taker = PLAYER_3,
            partner = PLAYER_3,
            bid = BID_PRISE,
            oudlers = OUDLER_21_MSK or OUDLER_PETIT_MSK,
            points = 46
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = abs(46 - 41) + 25
        Assert.assertEquals(-points, results[PLAYER_1])
        Assert.assertEquals(-points, results[PLAYER_2])
        Assert.assertEquals(points * 4, results[PLAYER_3])
        Assert.assertEquals(-points, results[PLAYER_4])
        Assert.assertEquals(-points, results[PLAYER_5])
    }

    // Petit au bout
    @Test
    fun `joueur un fait une garde et remporte le contrat avec un bout et le petit au bout`() {
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_1,
            partner = PLAYER_NONE,
            bid = BID_GARDE,
            oudlers = OUDLER_PETIT_MSK,
            points = 58,
            bonuses = listOf(PLAYER_1 to BONUS_PETIT_AU_BOUT)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val points = (58 - 51 + 25 + 10) * 2
        Assert.assertEquals(points * 2, results[PLAYER_1])
        Assert.assertEquals(-points, results[PLAYER_2])
        Assert.assertEquals(-points, results[PLAYER_3])
    }
}
