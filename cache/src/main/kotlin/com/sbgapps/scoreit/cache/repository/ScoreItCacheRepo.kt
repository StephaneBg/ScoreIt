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

package com.sbgapps.scoreit.cache.repository

import com.sbgapps.scoreit.cache.manager.GameManager
import com.sbgapps.scoreit.cache.model.Game
import com.sbgapps.scoreit.cache.model.Lap
import com.sbgapps.scoreit.cache.model.Player
import com.sbgapps.scoreit.cache.model.belote.BeloteBonusCache
import com.sbgapps.scoreit.cache.model.belote.BeloteGame
import com.sbgapps.scoreit.cache.model.belote.BeloteLap
import com.sbgapps.scoreit.cache.model.coinche.CoincheGame
import com.sbgapps.scoreit.cache.model.tarot.*
import com.sbgapps.scoreit.cache.model.universal.UniversalGame
import com.sbgapps.scoreit.cache.model.universal.UniversalLap
import com.sbgapps.scoreit.data.model.*
import com.sbgapps.scoreit.data.repository.CacheRepo

class ScoreItCacheRepo(private val gameManager: GameManager) : CacheRepo {

    override fun loadGame(): GameData = gameManager.loadGame().toGameData()

    override fun createGame(name: String): GameData = gameManager.createGame(name).toGameData()

    override fun saveGame(game: GameData) {
        val gameToSave = when (game) {
            is UniversalGameData -> UniversalGame(
                game.players.map { Player(it.name, it.color) },
                game.laps.map { UniversalLap(it.points.toIntArray()) }
            )

            is TarotGameData -> when (game.players.size) {
                3 -> {
                    TarotThreeGame(
                        game.players.map { Player(it.name, it.color) },
                        game.laps.map {
                            TarotThreeLap(
                                it.taker,
                                TarotBidCache(it.bid.ordinal),
                                it.points,
                                it.oudlers,
                                it.bonuses.map { (player, bonus) ->
                                    TarotBonusCache(
                                        bonus.ordinal,
                                        player
                                    )
                                }
                            )
                        }
                    )
                }
                4 -> {
                    TarotFourGame(
                        game.players.map { Player(it.name, it.color) },
                        game.laps.map {
                            TarotFourLap(
                                it.taker,
                                TarotBidCache(it.bid.ordinal),
                                it.points,
                                it.oudlers,
                                it.bonuses.map { (player, bonus) ->
                                    TarotBonusCache(
                                        bonus.ordinal,
                                        player
                                    )
                                }
                            )
                        }
                    )
                }
                5 -> {
                    TarotFiveGame(
                        game.players.map { Player(it.name, it.color) },
                        game.laps.map {
                            TarotFiveLap(
                                it.taker,
                                TarotBidCache(it.bid.ordinal),
                                it.points,
                                it.oudlers,
                                it.bonuses.map { (player, bonus) ->
                                    TarotBonusCache(
                                        bonus.ordinal,
                                        player
                                    )
                                },
                                it.partner
                            )
                        }
                    )
                }
                else -> error("Can't play Tarot with another player count")
            }

            is BeloteGameData -> BeloteGame(
                game.players.map { Player(it.name, it.color) },
                game.laps.map {
                    BeloteLap(
                        it.scorer,
                        it.points,
                        it.bonuses.map { bonus ->
                            BeloteBonusCache(
                                bonus.second.ordinal,
                                bonus.first
                            )
                        })
                }
            )

//            is CoincheGameData -> CoincheGame(
//                game.players.map { Player(it.name, it.color) },
//                game.laps.map { CoincheLap(it.points.toIntArray()) }
//            )
            else -> error("Unknown game")
        }
        gameManager.saveGame(gameToSave)
    }

    private fun Game<Lap>.toGameData(): GameData = when (this) {
        is UniversalGame -> UniversalGameData(
            players.map { it.toPlayerData() },
            laps.map {
                UniversalLapData(it.scores.toList())
            }
        )

        is TarotThreeGame -> TarotGameData(
            players.map { it.toPlayerData() },
            laps.map {
                TarotLapData(
                    TarotThreeGame.NB_PLAYERS,
                    it.taker,
                    PLAYER_NONE,
                    it.bid.fromCache(),
                    it.oudlers,
                    it.points,
                    it.bonuses.map { bonus -> bonus.player to bonus.fromCache() }
                )
            }
        )

        is TarotFourGame -> TarotGameData(
            players.map { it.toPlayerData() },
            laps.map {
                TarotLapData(
                    TarotFourGame.NB_PLAYERS,
                    it.taker,
                    PLAYER_NONE,
                    it.bid.fromCache(),
                    it.oudlers,
                    it.points,
                    it.bonuses.map { bonus -> bonus.player to bonus.fromCache() }
                )
            }
        )

        is TarotFiveGame -> TarotGameData(
            players.map { it.toPlayerData() },
            laps.map {
                TarotLapData(
                    TarotFiveGame.NB_PLAYERS,
                    it.taker,
                    it.partner,
                    it.bid.fromCache(),
                    it.oudlers,
                    it.points,
                    it.bonuses.map { bonus -> bonus.player to bonus.fromCache() }
                )
            }
        )

        is BeloteGame -> BeloteGameData(
            players.map { it.toPlayerData() },
            laps.map {
                BeloteLapData(
                    it.scorer,
                    it.points,
                    it.bonuses.map { bonus -> bonus.player to bonus.fromCache() }
                )
            }
        )

        is CoincheGame -> CoincheGameData(
            players.map { it.toPlayerData() },
            laps.map {
                CoincheLapData(
                    it.scorer,
                    it.bidder,
                    it.bid,
                    CoincheBidData.values()[it.coinche],
                    it.points,
                    it.bonuses.map { bonus -> bonus.player to bonus.fromCache() }
                )
            }
        )

        else -> error("Unknown game")
    }

    private fun TarotBidCache.fromCache(): TarotBidData = TarotBidData.values()[get()]
    private fun TarotBonusCache.fromCache(): TarotBonusData = TarotBonusData.values()[get()]
    private fun BeloteBonusCache.fromCache(): BeloteBonusData = BeloteBonusData.values()[get()]
}
