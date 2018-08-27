package com.sbgapps.scoreit.ui.model

class Game(players: List<Player>, laps: List<UniversalLap>) {

    val achievement: List<Achievement>

    init {
        achievement = mutableListOf()
        players.forEachIndexed { indexPlayer, player ->
            val points = mutableListOf<Int>()
            points += 0
            laps.forEachIndexed { index, lap ->
                points += points[index] + lap.points[indexPlayer]
            }
            achievement.add(Pair(player, points))
        }
    }


    fun getScore(player: Player): Int = achievement.first { it.first == player }.second.last()
}

typealias Achievement = Pair<Player, List<Int>>