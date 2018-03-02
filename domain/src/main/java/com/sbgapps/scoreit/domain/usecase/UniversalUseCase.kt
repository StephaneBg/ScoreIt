/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.domain.usecase

import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.domain.model.UniversalLap
import com.sbgapps.scoreit.domain.preference.PreferencesHelper
import com.sbgapps.scoreit.domain.repository.GameRepository


class UniversalUseCase(val universalRepo: GameRepository<UniversalLap>,
                       val prefsHelper: PreferencesHelper)
    : BaseUseCase() {

    private var gameId: Long? = null

    suspend fun deleteGame() {
        universalRepo.deleteGame(getGameId())
    }

    suspend fun getPlayers(): List<Player> {
        return asyncAwait {
            universalRepo.getPlayers(getGameId())
        }
    }

    suspend fun savePlayer(player: Player) {
        asyncAwait {
            universalRepo.savePlayer(getGameId(), player)
        }
    }

    suspend fun getLaps(): List<UniversalLap> {
        return asyncAwait {
            universalRepo.getLaps(getGameId())
        }
    }

    suspend fun saveLap(lap: UniversalLap) {
        asyncAwait {
            universalRepo.saveLap(getGameId(), lap)
        }
    }

    suspend fun deleteLap(lap: UniversalLap) {
        asyncAwait {
            universalRepo.deleteLap(getGameId(), lap)
        }
    }

    suspend fun clearLaps() {
        asyncAwait {
            universalRepo.clearLaps(getGameId())
        }
    }

    private suspend fun getGameId(): Long {
        return asyncAwait {
            gameId ?: run {
                val id = universalRepo.getGameId(prefsHelper.getUniversalGameName())
                gameId = id
                id
            }
        }
    }
}