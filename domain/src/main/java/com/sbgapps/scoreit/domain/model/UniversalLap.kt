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

package com.sbgapps.scoreit.domain.model


class UniversalLap(val id: Long?, private var points: MutableList<Int>) {

    var isWithTotal: Boolean = false

    fun setPoints(_points: MutableList<Int>) {
        points = if (isWithTotal) _points.dropLast(1).toMutableList() else _points
    }

    fun getPoints(): MutableList<Int> {
        return if (isWithTotal) {
            val _points = points.toMutableList()
            _points.add(points.sum())
            _points
        } else points
    }
}