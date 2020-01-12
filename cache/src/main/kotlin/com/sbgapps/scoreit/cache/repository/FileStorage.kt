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

import android.content.Context
import com.snatik.storage.Storage

interface FileStorage {
    fun loadFile(directoryName: String, fileName: String): String
    fun saveFile(directoryName: String, fileName: String, data: String)
    fun createDirectory(directoryName: String)
    fun getSavedFiles(directoryName: String): List<Pair<String, Long>>
}

class ScoreItFileStorage(context: Context) : FileStorage {

    private val storage: Storage = Storage(context)
    private val internalStorageDirectory = storage.internalFilesDirectory

    override fun loadFile(directoryName: String, fileName: String): String =
        storage.readTextFile("$internalStorageDirectory/$directoryName/$fileName")

    override fun saveFile(directoryName: String, fileName: String, data: String) {
        storage.createFile("$internalStorageDirectory/$directoryName/$fileName", data)
    }

    override fun createDirectory(directoryName: String) {
        storage.createDirectory("$internalStorageDirectory/$directoryName")
    }

    override fun getSavedFiles(directoryName: String): List<Pair<String, Long>> =
        storage.getFiles("$internalStorageDirectory/$directoryName").map {
            it.name to it.lastModified()
        }
}
