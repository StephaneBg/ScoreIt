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
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.OUDLER_21_MSK
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.OUDLER_EXCUSE_MSK
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.OUDLER_NONE
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.OUDLER_PETIT_MSK
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.POINTS_CONTRACT
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.POINTS_WITH_NO_OUDLER
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.POINTS_WITH_ONE_OUDLER
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.POINTS_WITH_THREE_OUDLERS
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.POINTS_WITH_TWO_OUDLERS
import org.junit.Assert
import org.junit.Test
import kotlin.math.abs

class TarotSolverTest {

    private val solver: TarotSolver = TarotSolver()

    @Test
    fun `verification des constantes`() {

        Assert.assertEquals(10, TarotBonusData.PETIT_AU_BOUT.points)
        Assert.assertEquals(20, TarotBonusData.POIGNEE_SIMPLE.points)
        Assert.assertEquals(30, TarotBonusData.POIGNEE_DOUBLE.points)
        Assert.assertEquals(40, TarotBonusData.POIGNEE_TRIPLE.points)

        Assert.assertEquals(200, TarotBonusData.CHELEM_NON_ANNONCE.points)
        Assert.assertEquals(400, TarotBonusData.CHELEM_ANNONCE_REALISE.points)
        Assert.assertEquals(-200, TarotBonusData.CHELEM_ANNONCE_NON_REALISE.points)

        Assert.assertEquals(25, POINTS_CONTRACT)
        Assert.assertEquals(56, POINTS_WITH_NO_OUDLER)
        Assert.assertEquals(51, POINTS_WITH_ONE_OUDLER)
        Assert.assertEquals(41, POINTS_WITH_TWO_OUDLERS)
        Assert.assertEquals(36, POINTS_WITH_THREE_OUDLERS)
    }

    // Trois joueurs
    @Test
    fun `trois joueurs - joueur un fait une prise et remporte le contrat avec aucun bout`() {
        val points = 58
        val lap = TarotLapData(
                playerCount = 3,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_NONE,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_NO_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(score * 2, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `trois joueurs - joueur un fait une prise et chute le contrat avec aucun bout`() {
        val points = 50
        val lap = TarotLapData(
                playerCount = 3,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_NONE,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val score = (abs(points - POINTS_WITH_NO_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(-score * 2, results[PLAYER_1])
        Assert.assertEquals(score, results[PLAYER_2])
        Assert.assertEquals(score, results[PLAYER_3])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `trois joueurs - joueur un fait une garde et remporte le contrat avec un bout`() {
        val points = 58
        val lap = TarotLapData(
                playerCount = 3,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.GUARD,
                oudlers = OUDLER_21_MSK,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(score * 2, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `trois joueurs - joueur deux fait une garde sans et remporte le contrat avec deux bouts`() {
        val points = 56
        val lap = TarotLapData(
                playerCount = 3,
                taker = PLAYER_2,
                partner = PLAYER_NONE,
                bid = TarotBidData.GUARD_WITHOUT_KITTY,
                oudlers = OUDLER_PETIT_MSK or OUDLER_EXCUSE_MSK,
                points = 56
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(score * 2, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `trois joueurs - joueur trois fait une garde contre et remporte le contrat avec trois bouts`() {
        val points = 64
        val lap = TarotLapData(
                playerCount = 3,
                taker = PLAYER_3,
                partner = PLAYER_NONE,
                bid = TarotBidData.GUARD_AGAINST_KITTY,
                oudlers = OUDLER_EXCUSE_MSK or OUDLER_21_MSK or OUDLER_PETIT_MSK,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_THREE_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(score * 2, results[PLAYER_3])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }


    // Quatre joueurs
    @Test
    fun `quatre joueurs - joueur un fait une prise et remporte le contrat avec aucun bout`() {
        val points = 62
        val lap = TarotLapData(
                playerCount = 4,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_NONE,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_NO_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(score * 3, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `quatre joueurs - joueur deux fait une prise et chute le contrat avec deux bouts`() {
        val points = 38
        val lap = TarotLapData(
                playerCount = 4,
                taker = PLAYER_2,
                partner = PLAYER_NONE,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_21_MSK or OUDLER_EXCUSE_MSK,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(score, results[PLAYER_1])
        Assert.assertEquals(-score * 3, results[PLAYER_2])
        Assert.assertEquals(score, results[PLAYER_3])
        Assert.assertEquals(score, results[PLAYER_4])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `quatre joueurs - joueur deux fait une garde et remporte le contrat avec un bout`() {
        val points = 57
        val lap = TarotLapData(
                playerCount = 4,
                taker = PLAYER_2,
                partner = PLAYER_NONE,
                bid = TarotBidData.GUARD,
                oudlers = OUDLER_PETIT_MSK,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(score * 3, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `quatre joueurs - joueur trois fait une garde sans et remporte le contrat avec deux bouts`() {
        val points = 56
        val lap = TarotLapData(
                playerCount = 4,
                taker = PLAYER_3,
                partner = PLAYER_NONE,
                bid = TarotBidData.GUARD_WITHOUT_KITTY,
                oudlers = OUDLER_PETIT_MSK or OUDLER_21_MSK,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(score * 3, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `quatre joueurs - joueur quatre fait une garde contre et remporte le contrat avec trois bouts`() {
        val points = 66
        val lap = TarotLapData(
                playerCount = 4,
                taker = PLAYER_4,
                partner = PLAYER_NONE,
                bid = TarotBidData.GUARD_AGAINST_KITTY,
                oudlers = OUDLER_EXCUSE_MSK or OUDLER_21_MSK or OUDLER_PETIT_MSK,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_THREE_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(score * 3, results[PLAYER_4])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }


    // Cinq joueurs
    @Test
    fun `cinq joueurs - joueur un fait une prise avec joueur deux et remporte le contrat avec aucun bout`() {
        val points = 59
        val lap = TarotLapData(
                playerCount = 5,
                taker = PLAYER_1,
                partner = PLAYER_2,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_NONE,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_NO_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(score * 2, results[PLAYER_1])
        Assert.assertEquals(score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(-score, results[PLAYER_5])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `cinq joueurs - joueur trois fait une garde avec joueur quatre et chute le contrat avec un bout`() {
        val points = 46
        val lap = TarotLapData(
                playerCount = 5,
                taker = PLAYER_3,
                partner = PLAYER_4,
                bid = TarotBidData.GUARD,
                oudlers = OUDLER_21_MSK,
                points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val score = (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(score, results[PLAYER_1])
        Assert.assertEquals(score, results[PLAYER_2])
        Assert.assertEquals(-score * 2, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(score, results[PLAYER_5])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `cinq joueurs - joueur cinq fait une prise seul quatre et remporte le contrat avec deux bouts`() {
        val points = 46
        val lap = TarotLapData(
            playerCount = 5,
            taker = PLAYER_3,
            partner = PLAYER_3,
            bid = TarotBidData.SMALL,
            oudlers = OUDLER_21_MSK or OUDLER_PETIT_MSK,
            points = points
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(score * 4, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(-score, results[PLAYER_5])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    // Petit au bout
    @Test
    fun `joueur un fait une garde et remporte le contrat avec un bout avec le petit au bout`() {
        val points = 58
        val lap = TarotLapData(
                playerCount = 3,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.GUARD,
                oudlers = OUDLER_PETIT_MSK,
                points = points,
                bonuses = listOf(PLAYER_1 to TarotBonusData.PETIT_AU_BOUT)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score =
                (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT + TarotBonusData.PETIT_AU_BOUT.points) * lap.bid.coefficient
        Assert.assertEquals(score * 2, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `joueur un fait une prise, remporte le contrat avec aucun bout et perd le petit au bout`() {
        val points = 60
        val lap = TarotLapData(
                playerCount = 3,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_NONE,
                points = points,
                bonuses = listOf(PLAYER_2 to TarotBonusData.PETIT_AU_BOUT)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score =
                (abs(points - POINTS_WITH_NO_OUDLER) + POINTS_CONTRACT - TarotBonusData.PETIT_AU_BOUT.points) * lap.bid.coefficient
        Assert.assertEquals(score * 2, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `joueur deux fait une garde, chute le contrat avec un bout et perd le petit au bout`() {
        val points = 48
        val lap = TarotLapData(
                playerCount = 3,
                taker = PLAYER_2,
                partner = PLAYER_NONE,
                bid = TarotBidData.GUARD,
                oudlers = OUDLER_EXCUSE_MSK,
                points = points,
                bonuses = listOf(PLAYER_3 to TarotBonusData.PETIT_AU_BOUT)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val score =
                (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT + TarotBonusData.PETIT_AU_BOUT.points) * lap.bid.coefficient
        Assert.assertEquals(score, results[PLAYER_1])
        Assert.assertEquals(-score * 2, results[PLAYER_2])
        Assert.assertEquals(score, results[PLAYER_3])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    // Chelem
    @Test
    fun `joueur un fait une garde et remporte le contrat avec un chelem`() {
        val points = 91
        val lap = TarotLapData(
                playerCount = 4,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_21_MSK or OUDLER_PETIT_MSK or OUDLER_EXCUSE_MSK,
                points = points,
                bonuses = listOf(PLAYER_1 to TarotBonusData.CHELEM_ANNONCE_REALISE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_THREE_OUDLERS) + POINTS_CONTRACT + TarotBonusData.CHELEM_ANNONCE_REALISE.points) * lap.bid.coefficient
        Assert.assertEquals(score * 3, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `joueur un fait une garde et remporte le contrat avec un chelem et sans l'excuse`() {
        val points = 87
        val lap = TarotLapData(
                playerCount = 4,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_21_MSK or OUDLER_PETIT_MSK,
                points = points,
                bonuses = listOf(PLAYER_1 to TarotBonusData.CHELEM_ANNONCE_REALISE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT + TarotBonusData.CHELEM_ANNONCE_REALISE.points) * lap.bid.coefficient
        Assert.assertEquals(score * 3, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `joueur un fait une garde et remporte le contrat avec un chelem pas annonce`() {
        val points = 91
        val lap = TarotLapData(
                playerCount = 4,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_21_MSK or OUDLER_PETIT_MSK,
                points = points,
                bonuses = listOf(PLAYER_1 to TarotBonusData.CHELEM_NON_ANNONCE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT + TarotBonusData.CHELEM_NON_ANNONCE.points) * lap.bid.coefficient
        Assert.assertEquals(score * 3, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }

    @Test
    fun `joueur un fait une garde et remporte le contrat avec un chelem pas reussi`() {
        val points = 91
        val lap = TarotLapData(
                playerCount = 4,
                taker = PLAYER_1,
                partner = PLAYER_NONE,
                bid = TarotBidData.SMALL,
                oudlers = OUDLER_21_MSK or OUDLER_PETIT_MSK,
                points = points,
                bonuses = listOf(PLAYER_1 to TarotBonusData.CHELEM_ANNONCE_NON_REALISE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT + TarotBonusData.CHELEM_ANNONCE_NON_REALISE.points) * lap.bid.coefficient
        Assert.assertEquals(score * 3, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(-score, results[PLAYER_4])
        Assert.assertEquals(0, results.reduce { acc, i -> acc + i })
    }
}
