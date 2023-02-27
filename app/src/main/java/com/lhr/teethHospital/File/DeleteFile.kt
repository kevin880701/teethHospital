package com.lhr.teethHospital.File

import java.io.File

class DeleteFile(filePath: String) {

    init {
        val file = File(filePath)
        file.delete()
    }
}