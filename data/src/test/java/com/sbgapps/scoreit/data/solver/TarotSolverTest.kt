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
    fun `la base des points du contrat est 25`() {
        Assert.assertEquals(25, POINTS_CONTRACT)
    }

    @Test
    fun `le nombre de points à réaliser avec aucun oudler est 56`() {
        Assert.assertEquals(56, POINTS_WITH_NO_OUDLER)
    }

    @Test
    fun `le nombre de points à réaliser avec un oudler est 51`() {
        Assert.assertEquals(51, POINTS_WITH_ONE_OUDLER)
    }

    @Test
    fun `le nombre de points à réaliser avec deux oudlers est 41`() {
        Assert.assertEquals(41, POINTS_WITH_TWO_OUDLERS)
    }

    @Test
    fun `le nombre de points à réaliser avec trois oudlers est 36`() {
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
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
        Assert.assertEquals(0, results.sum())
    }

    // Le Petit au Bout
    // Si le Petit fait partie de la dernière levée, on dit qu'il est au Bout. Le camp qui réalise la
    // dernière levée, à condition que cette levée comprenne le Petit, bénéficie d'une prime de 10
    // points, multipliable selon le contrat, quel que soit le résultat de la donne.

    @Test
    fun `le petit au bout vaut 10 points`() {
        Assert.assertEquals(10, TarotBonusData.PETIT_AU_BOUT.points)
    }

    @Test
    fun `joueur un fait une garde et remporte le contrat avec le petit au bout`() {
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
        val score = (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT + TarotBonusData.PETIT_AU_BOUT.points) *
                lap.bid.coefficient
        Assert.assertEquals(score * 2, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
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
        val score = (abs(points - POINTS_WITH_NO_OUDLER) + POINTS_CONTRACT - TarotBonusData.PETIT_AU_BOUT.points) *
                lap.bid.coefficient
        Assert.assertEquals(score * 2, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
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
        val score = (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT + TarotBonusData.PETIT_AU_BOUT.points) *
                lap.bid.coefficient
        Assert.assertEquals(score, results[PLAYER_1])
        Assert.assertEquals(-score * 2, results[PLAYER_2])
        Assert.assertEquals(score, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }

    // La Poignée (10, 13 ou 15 Atouts)
    // Un joueur possédant une Poignée peut, s'il le désire, l'annoncer et la présenter, les
    // Atouts classés dans l'ordre décroissant, complète et en une seule fois, juste avant de jouer
    // sa première carte. L'Excuse peut remplacer un Atout manquant mais la présentation de
    // l’Excuse dans la Poignée implique que l'annonceur n'a pas d'autre Atout.
    // La Poignée doit comprendre effectivement DIX, TREIZE, QUINZE Atouts. Lorsqu'un
    // joueur possède ONZE, DOUZE, QUATORZE, SEIZE Atouts, il doit en cacher un ou plus de
    // son choix. Lorsque le preneur possède 4 Rois et 15 atouts, l’atout écarté doit être remontré
    // avec la triple Poignée qui est alors comptabilisée.
    // - A la simple Poignée (10 Atouts) correspond une la prime de 20 points.
    // - A la double Poignée (13 Atouts) correspond une la prime de 30 points.
    // - A la triple Poignée (15 Atouts) correspond une la prime de 40 points.
    // Ces primes gardent la même valeur quel que soit le contrat. La prime est acquise
    // au camp vainqueur de la donne.

    @Test
    fun `la poignée simple vaut 20 points`() {
        Assert.assertEquals(20, TarotBonusData.POIGNEE_SIMPLE.points)
    }

    @Test
    fun `la poignée double vaut 30 points`() {
        Assert.assertEquals(30, TarotBonusData.POIGNEE_DOUBLE.points)
    }

    @Test
    fun `la poignée triple vaut 40 points`() {
        Assert.assertEquals(40, TarotBonusData.POIGNEE_TRIPLE.points)
    }

    @Test
    fun `joueur un fait une garde, remporte le contrat avec un bout et une poignée simple annoncée par l'attaque`() {
        val points = 54
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_1,
            partner = PLAYER_NONE,
            bid = TarotBidData.GUARD,
            oudlers = OUDLER_21_MSK,
            points = points,
            bonuses = listOf(PLAYER_1 to TarotBonusData.POIGNEE_SIMPLE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient +
                TarotBonusData.POIGNEE_SIMPLE.points
        Assert.assertEquals(score * 2, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }

    @Test
    fun `joueur deux fait une prise, remporte le contrat avec un bout et une poignée double annoncée par l'attaque`() {
        val points = 57
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_2,
            partner = PLAYER_NONE,
            bid = TarotBidData.SMALL,
            oudlers = OUDLER_PETIT_MSK,
            points = points,
            bonuses = listOf(PLAYER_2 to TarotBonusData.POIGNEE_DOUBLE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient +
                TarotBonusData.POIGNEE_DOUBLE.points
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(score * 2, results[PLAYER_2])
        Assert.assertEquals(-score, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }

    @Test
    fun `joueur trois fait une garde sans, remporte le contrat avec deux bouts et une poignée double annoncée par l'attaque`() {
        val points = 48
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_3,
            partner = PLAYER_NONE,
            bid = TarotBidData.GUARD_WITHOUT_KITTY,
            oudlers = OUDLER_PETIT_MSK or OUDLER_21_MSK,
            points = points,
            bonuses = listOf(PLAYER_3 to TarotBonusData.POIGNEE_TRIPLE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient +
                TarotBonusData.POIGNEE_TRIPLE.points
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(score * 2, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }

    @Test
    fun `joueur trois fait une garde sans, remporte le contrat avec deux bouts et une poignée double annoncée par la défense`() {
        val points = 48
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_3,
            partner = PLAYER_NONE,
            bid = TarotBidData.GUARD_WITHOUT_KITTY,
            oudlers = OUDLER_PETIT_MSK or OUDLER_21_MSK,
            points = points,
            bonuses = listOf(PLAYER_1 to TarotBonusData.POIGNEE_TRIPLE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient +
                TarotBonusData.POIGNEE_TRIPLE.points
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(score * 2, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }

    @Test
    fun `joueur un fait une garde, chute le contrat avec un bout et une poignée simple annoncée par l'attaque`() {
        val points = 47
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_1,
            partner = PLAYER_NONE,
            bid = TarotBidData.GUARD,
            oudlers = OUDLER_21_MSK,
            points = points,
            bonuses = listOf(PLAYER_1 to TarotBonusData.POIGNEE_SIMPLE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val score = (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient +
                TarotBonusData.POIGNEE_SIMPLE.points
        Assert.assertEquals(-score * 2, results[PLAYER_1])
        Assert.assertEquals(score, results[PLAYER_2])
        Assert.assertEquals(score, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }

    @Test
    fun `joueur un fait une garde, chute le contrat avec un bout et une poignée simple annoncée par la défense`() {
        val points = 47
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_1,
            partner = PLAYER_NONE,
            bid = TarotBidData.GUARD,
            oudlers = OUDLER_21_MSK,
            points = points,
            bonuses = listOf(PLAYER_2 to TarotBonusData.POIGNEE_SIMPLE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val score = (abs(points - POINTS_WITH_ONE_OUDLER) + POINTS_CONTRACT) * lap.bid.coefficient +
                TarotBonusData.POIGNEE_SIMPLE.points
        Assert.assertEquals(-score * 2, results[PLAYER_1])
        Assert.assertEquals(score, results[PLAYER_2])
        Assert.assertEquals(score, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }

    // Le chelem
    // Réussir le CHELEM, c'est gagner toutes les levées. Vous jouerez peut-être durant
    // des années sans jamais réaliser, ni subir, ce coup extrêmement rare. Lorsqu’un joueur
    // demande un CHELEM, il doit toujours appeler l’arbitre.
    // Le Chelem est demandé après l’annonce d’un contrat ; les points sont comptés en
    // fonction du contrat demandé et une prime (ou une pénalité) supplémentaire sanctionne la
    // réussite (ou l'échec) de ce Chelem :
    // - Chelem annoncé et réalisé : prime supplémentaire de 400 points.
    // - Chelem non annoncé mais réalisé : prime supplémentaire de 200 points.
    // - Chelem annoncé mais non réalisé : 200 points sont déduits du total.
    // En cas d'annonce du Chelem, l'entame revient de droit au joueur qui l’a demandé, quel
    // que soit le donneur.
    // En cas de Chelem réussi, le demandeur doit obligatoirement faire tous ses plis et s’il
    // détient l’Excuse, la jouer en dernier ; en conséquence, le Petit sera considéré au Bout s'il est
    // mené à l'avant dernier pli.
    // Paradoxalement, il arrive que la défense inflige un Chelem au déclarant. Dans ce cas,
    // chaque défenseur reçoit, en plus de la marque normale, une prime de 200 points.

    @Test
    fun `le chelem annoncé et réalisé vaut 400 points`() {
        Assert.assertEquals(400, TarotBonusData.CHELEM_ANNONCE_REALISE.points)
    }

    @Test
    fun `le chelem non annoncé mais réalisé vaut 200 points`() {
        Assert.assertEquals(200, TarotBonusData.CHELEM_NON_ANNONCE.points)
    }

    @Test
    fun `le chelem annoncé mais non réalisé vaut -200 points`() {
        Assert.assertEquals(-200, TarotBonusData.CHELEM_ANNONCE_NON_REALISE.points)
    }

    @Test
    fun `joueur trois fait une garde sans, annonce un chelem et remporte le contrat avec deux bouts`() {
        val points = 58
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_3,
            partner = PLAYER_NONE,
            bid = TarotBidData.GUARD_WITHOUT_KITTY,
            oudlers = OUDLER_PETIT_MSK or OUDLER_21_MSK,
            points = points,
            bonuses = listOf(PLAYER_3 to TarotBonusData.CHELEM_ANNONCE_REALISE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient +
                TarotBonusData.CHELEM_ANNONCE_REALISE.points
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(score * 2, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }

    @Test
    fun `joueur trois fait une garde sans et remporte le contrat avec un chelem non annoncé avec deux bouts`() {
        val points = 62
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_3,
            partner = PLAYER_NONE,
            bid = TarotBidData.GUARD_WITHOUT_KITTY,
            oudlers = OUDLER_PETIT_MSK or OUDLER_21_MSK,
            points = points,
            bonuses = listOf(PLAYER_3 to TarotBonusData.CHELEM_NON_ANNONCE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertTrue(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient +
                TarotBonusData.CHELEM_NON_ANNONCE.points
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(score * 2, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }

    @Test
    fun `joueur trois fait une garde sans et remporte le contrat avec un chelem annoncé mais non réalisé avec deux bouts`() {
        val points = 61
        val lap = TarotLapData(
            playerCount = 3,
            taker = PLAYER_3,
            partner = PLAYER_NONE,
            bid = TarotBidData.GUARD_WITHOUT_KITTY,
            oudlers = OUDLER_PETIT_MSK or OUDLER_21_MSK,
            points = points,
            bonuses = listOf(PLAYER_3 to TarotBonusData.CHELEM_ANNONCE_NON_REALISE)
        )

        val (results, isWon) = solver.computeResults(lap)

        Assert.assertFalse(isWon)
        val score = (abs(points - POINTS_WITH_TWO_OUDLERS) + POINTS_CONTRACT) * lap.bid.coefficient +
                TarotBonusData.CHELEM_ANNONCE_NON_REALISE.points
        Assert.assertEquals(-score, results[PLAYER_1])
        Assert.assertEquals(-score, results[PLAYER_2])
        Assert.assertEquals(score * 2, results[PLAYER_3])
        Assert.assertEquals(0, results.sum())
    }
}
