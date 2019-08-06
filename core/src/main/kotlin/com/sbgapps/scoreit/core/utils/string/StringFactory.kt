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

package com.sbgapps.scoreit.core.utils.string

sealed class StringFactory

data class Pure(val s: String) : StringFactory()
data class FromResources(val id: Int, val args: List<StringFactory>) : StringFactory()
data class FromQuantityResources(val id: Int, val quantity: Int, val args: List<StringFactory>) : StringFactory()
data class Concat(val parts: List<StringFactory>) : StringFactory()
