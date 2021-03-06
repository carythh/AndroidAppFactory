package com.bihe0832.android.lib.file

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.bihe0832.android.lib.utils.encypt.MD5
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.NumberFormat


/**
 *
 * @author zixie code@bihe0832.com
 * Created on 2020-01-10.
 */
object FileUtils {

    const val SPACE_KB = 1024.0
    const val SPACE_MB = 1024 * SPACE_KB
    const val SPACE_GB = 1024 * SPACE_MB
    const val SPACE_TB = 1024 * SPACE_GB
    const val APK_FILE_SUFFIX = ".apk"

    fun checkFileExist(filePath: String): Boolean {
        return if (TextUtils.isEmpty(filePath)) {
            false
        } else {
            val file = File(filePath)
            file.length() > 0 && file.exists() && file.isFile
        }
    }

    fun checkAndCreateFolder(path: String): Boolean {
        try {
            File(path).let {
                return if (!it.exists()) {
                    it.mkdirs()
                } else {
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun checkStoragePermissions(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun checkFileExist(filePath: String, fileMD5: String): Boolean {
        return if (TextUtils.isEmpty(filePath)) {
            false
        } else {
            val file = File(filePath)
            if (TextUtils.isEmpty(fileMD5)) {
                file.length() > 0 && file.exists() && file.isFile
            } else {
                getFileMD5(filePath).equals(fileMD5, ignoreCase = true)
            }
        }
    }

    fun getFileLength(sizeInBytes: Long): String {
        val nf: NumberFormat = DecimalFormat().apply {
            maximumFractionDigits = 2
        }

        return try {
            when {
                sizeInBytes < SPACE_KB -> {
                    nf.format(sizeInBytes) + " B"
                }
                sizeInBytes < SPACE_MB -> {
                    nf.format(sizeInBytes / SPACE_KB) + " KB"
                }
                sizeInBytes < SPACE_GB -> {
                    nf.format(sizeInBytes / SPACE_MB) + " MB"
                }
                sizeInBytes < SPACE_TB -> {
                    nf.format(sizeInBytes / SPACE_GB) + " GB"
                }
                else -> {
                    nf.format(sizeInBytes / SPACE_TB) + " TB"
                }
            }
        } catch (e: java.lang.Exception) {
            "$sizeInBytes B"
        }
    }

    /**
     * 仅能打开 [ZixieFileProvider.getZixieFilePath] 对应目录下的文件
     */
    fun openFile(context: Context, filePath: String, fileType: String) {
        try { //设置intent的data和Type属性
            File(filePath).let { file ->
                val fileProvider = ZixieFileProvider.getZixieFileProvider(context, file)
                Intent(Intent.ACTION_VIEW).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    addCategory("android.intent.category.DEFAULT")
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        setDataAndType(Uri.fromFile(file), fileType)
                    } else {
                        setDataAndType(fileProvider, fileType)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }
                }.let {
                    context.startActivity(it)
                }
            }

        } catch (e: java.lang.Exception) { //当系统没有携带文件打开软件，提示
            e.printStackTrace()
        }
    }

    fun getFileMD5(filePath: String): String {
        return MD5.getFileMD5(filePath)
    }

    fun deleteDirectory(dir: File): Boolean {
        try {
            if (!dir.exists()) {
                return true
            } else {
                if (dir.isDirectory) {
                    val childrens: Array<String> = dir.list()
                    // 递归删除目录中的子目录下
                    for (child in childrens) {
                        val success: Boolean = deleteDirectory(File(dir, child))
                        if (!success) return false
                    }
                    return dir.delete()
                } else {
                    return deleteFile(dir.absolutePath)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun deleteFile(filePath: String): Boolean {
        try {
            return File(filePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    fun copyFile(source: File, dest: File) {
        var inputChannel: FileChannel? = null
        var outputChannel: FileChannel? = null
        try {
            inputChannel = FileInputStream(source).getChannel()
            outputChannel = FileOutputStream(dest).getChannel()
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputChannel?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                outputChannel?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun copyDirectory(src: File, dest: File) {
        try {
            if (src.isDirectory) {
                if (!dest.exists()) {
                    dest.mkdir()
                }
                val files = src.list()
                for (file in files) {
                    val srcFile = File(src, file)
                    val destFile = File(dest, file)
                    // 递归复制
                    copyDirectory(srcFile, destFile)
                }
            } else {
                copyFile(src, dest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getExtensionName(filename: String?): String {
        filename?.let {
            val dot = filename.lastIndexOf('.')
            if (dot > -1 && dot < filename.length - 1) {
                return filename.substring(dot + 1)
            }
        }
        return ""
    }

    fun getFileName(filePath: String?): String {
        filePath?.let {
            val split = filePath.lastIndexOf('/')
            return if (split > -1) {
                filePath.substring(split + 1)
            } else {
                filePath
            }
        }
        return ""
    }

    fun getFileNameWithoutEx(filename: String?): String {
        filename?.let {
            val dot = filename.lastIndexOf('.')
            val split = filename.lastIndexOf('/')
            if (split < dot) {
                if (dot > -1 && dot < filename.length) {
                    return if (split > -1) {
                        filename.substring(split + 1, dot)
                    } else {
                        filename.substring(0, dot)
                    }
                }
            }
        }
        return ""
    }

    fun getFileContent(filename: String?): String {
        var res = ""
        filename?.let { it ->
            if (checkFileExist(it)) {
                var fis: FileInputStream? = null
                try {
                    fis = FileInputStream(File(it))
                    val buffer = ByteArray(fis.available())
                    fis.read(buffer)
                    res = String(buffer, Charset.defaultCharset())
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        fis?.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return res
    }
}