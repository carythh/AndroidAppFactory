package com.bihe0832.android.framework.log

import android.content.Context
import com.bihe0832.android.framework.R
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.utils.DateUtil
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


/**
 *
 * @author hardyshi code@bihe0832.com
 * Created on 2020-03-05.
 * Description: 用户处理特殊日志
 *
 */
object LoggerFile {
    private var mContext: Context? = null
    private var mCanSaveSpecialFile = false
    private val mLogFiles = HashMap<String, File?>()
    private val mBufferedWriters = HashMap<String, BufferedWriter?>()

    fun init(context: Context, isDebug: Boolean) {
        mContext = context
        mCanSaveSpecialFile = isDebug
        ZLog.setDebug(isDebug)
    }

    private fun reset(fileName: String) {
        if (mCanSaveSpecialFile) {
            if (mLogFiles[fileName] != null && mBufferedWriters[fileName] != null) {

            } else {
                try {
                    var file = File(getFilePathByName(fileName))
                    if (!FileUtils.checkFileExist(getFilePathByName(fileName))) {
                        file.createNewFile()
                    }
                    val bufferedWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(file), "UTF-8"))
                    mLogFiles[fileName] = file
                    mBufferedWriters[fileName] = bufferedWriter
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun bufferSave(fileName: String, msg: String?) {
        try {
            mBufferedWriters[fileName]?.write("${DateUtil.getDateEN()} $msg")
            mBufferedWriters[fileName]?.newLine()
            mBufferedWriters[fileName]?.flush()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getFilePathByName(module: String): String {
        return mContext?.getExternalFilesDir(mContext?.getString(R.string.lib_bihe0832_file_folder))?.absolutePath + "/${module}_${DateUtil.getDateENyyyyMMdd()}.txt"

    }

    fun log(module: String, msg: String) {
        ZLog.info(module, msg)
        try {
            if (mCanSaveSpecialFile) {
                reset(module)
                bufferSave(module, msg)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun openLog(module: String) {
        try { //设置intent的data和Type属性
            mContext?.let {
                FileUtils.openFile(it, getFilePathByName(module), "text/plain")
            }
        } catch (e: java.lang.Exception) { //当系统没有携带文件打开软件，提示
            e.printStackTrace()
        }
    }
}