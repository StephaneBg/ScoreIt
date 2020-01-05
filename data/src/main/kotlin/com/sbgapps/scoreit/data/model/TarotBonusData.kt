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

package com.sbgapps.scoreit.data.model

enum class TarotBonusData(val points: Int) {
    PETIT_AU_BOUT(10),
    POIGNEE_SIMPLE(20),
    POIGNEE_DOUBLE(30),
    POIGNEE_TRIPLE(40),
    CHELEM_NON_ANNONCE(200),
    CHELEM_ANNONCE_REALISE(400),
    CHELEM_ANNONCE_NON_REALISE(-200)
}
