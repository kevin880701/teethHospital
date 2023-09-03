package com.lhr.teethHospital.file

import java.io.File

class DeleteFile(filePath: String) {

    init {
        val file = File(filePath)
        file.delete()
    }
}