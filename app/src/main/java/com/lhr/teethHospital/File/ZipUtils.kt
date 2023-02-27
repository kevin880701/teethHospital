package com.lhr.teethHospital.File

import com.lhr.teethHospital.Model.Model.Companion.TEETH_DIR
import java.io.*
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry

class ZipUtils(out:OutputStream) {
    var filesList: ArrayList<String>  = ArrayList()
    var databasesList: ArrayList<String>  = ArrayList()
    val buffer = ByteArray(1024)
    var zos = ZipOutputStream(out)

    init {
        try {
            generateFilesList(File(TEETH_DIR))
//            generateFilesList(File(APP_FILES_PATH + File.separator + csvBackUp))
//            generateDatabaseList(File(DATABASES_PATH + File.separator))
            var `in`: FileInputStream? = null
            for (file in filesList!!) {
                println("File Added : $file")
//                val ze = ZipEntry(source + File.separator + file)  //新增上層資料夾
                val ze = ZipEntry(file)
                zos.putNextEntry(ze)
                try {
                    `in` = FileInputStream(TEETH_DIR + file)
                    var len = 0
                    while (`in`.read(buffer).also({ len = it }) > 0) {
                        zos.write(buffer, 0, len)
                    }
                } finally {
                    (`in` as FileInputStream).close()
                }
            }
            // 跑SQL資料夾
//            for (file in databasesList!!) {
//                println("File Added : $file")
//                val ze = ZipEntry(databasesSource + File.separator + file)
//                zos.putNextEntry(ze)
//                try {
//                    `in` = FileInputStream(DATABASES_PATH + File.separator + file)
//                    var len = 0
//                    while (`in`.read(buffer).also({ len = it }) > 0) {
//                        zos.write(buffer, 0, len)
//                    }
//                } finally {
//                    (`in` as FileInputStream).close()
//                }
//            }
            zos.closeEntry()
        } catch (ex: IOException) {
            ex.printStackTrace()
        } finally {
            try {
                zos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun generateFilesList(node: File) { // add file only
        if (node.isFile) {
            filesList.add(generateZipEntry(node.toString()) as String)
        }
        if (node.isDirectory) {
            val subNote = node.list()
            for (filename in subNote) {
                generateFilesList(File(node, filename))
            }
        }
    }

    private fun generateZipEntry(file: String): String? {
        return file.substring(TEETH_DIR.length, file.length)
    }

    private fun databasesGenerateZipEntry(file: String): String? {
        return file.substring(TEETH_DIR.length, file.length)
    }
}