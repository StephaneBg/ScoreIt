package com.sbgapps.scoreit.cache.model

import com.sbgapps.scoreit.domain.model.PlayerEntity
import com.sbgapps.scoreit.domain.model.UniversalLapEntity

fun PlayerData.mapFromCache() = PlayerEntity(id!!, name, color)

fun PlayerEntity.mapToCache(gameId: Long) = PlayerData(id, gameId, name, color)

fun UniversalLapData.mapFromCache() = UniversalLapEntity(id!!, points)

fun UniversalLapEntity.mapToCache(gameId: Long) = UniversalLapData(id, gameId, points)