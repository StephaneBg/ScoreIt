package com.sbgapps.scoreit.ui.model

import com.sbgapps.scoreit.domain.model.PlayerEntity
import com.sbgapps.scoreit.domain.model.UniversalLapEntity

fun PlayerEntity.mapFromDomain(score: Int) = Player(id, name, color, score)

fun Player.mapToDomain() = PlayerEntity(id, name, color)

fun UniversalLapEntity.mapFromDomain() = UniversalLap(id, points)

fun UniversalLap.mapToDomain() = UniversalLapEntity(id, points)