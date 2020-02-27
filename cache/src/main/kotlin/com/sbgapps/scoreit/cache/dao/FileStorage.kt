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

package com.sbgapps.scoreit.cache.dao

import android.content.Context
import com.snatik.storage.Storage

interface FileStorage {
    fun loadFile(directoryName: String, fileName: String): String
    fun saveFile(directoryName: String, fileName: String, data: String)
    fun createDirectory(directoryName: String)
    fun getSavedFiles(directoryName: String): List<Pair<String, Long>>
    fun removeFile(directoryName: String, fileName: String)
    fun renameFile(directoryName: String, oldFileName: String, newFileName: String)
}

class ScoreItFileStorage(context: Context) : FileStorage {

    private val storage: Storage = Storage(context)
    private val internalDirectory = storage.internalFilesDirectory

    override fun loadFile(directoryName: String, fileName: String): String =
        storage.readTextFile("$internalDirectory/$directoryName/$fileName")

    override fun saveFile(directoryName: String, fileName: String, data: String) {
        storage.createFile("$internalDirectory/$directoryName/$fileName", data)
    }

    override fun createDirectory(directoryName: String) {
        storage.createDirectory("$internalDirectory/$directoryName")
    }

    override fun getSavedFiles(directoryName: String): List<Pair<String, Long>> =
        storage.getFiles("$internalDirectory/$directoryName").map {
            it.name to it.lastModified()
        }

    override fun removeFile(directoryName: String, fileName: String) {
        storage.deleteFile("$internalDirectory/$directoryName/$fileName")
    }

    override fun renameFile(directoryName: String, oldFileName: String, newFileName: String) {
        storage.rename(
            "$internalDirectory/$directoryName/$oldFileName",
            "$internalDirectory/$directoryName/$newFileName"
        )
    }
}
