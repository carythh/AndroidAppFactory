package com.bihe0832.android.common.test.base

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.bihe0832.android.common.test.R
import com.bihe0832.android.common.test.item.TestItemData
import com.bihe0832.android.common.webview.WebPageActivity
import com.bihe0832.android.framework.router.RouterAction
import com.bihe0832.android.framework.router.RouterConstants
import com.bihe0832.android.framework.ui.list.CommonListLiveData
import com.bihe0832.android.framework.ui.list.easyrefresh.CommonListFragment
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.debug.DebugTools
import com.bihe0832.android.lib.debug.InputDialogCompletedCallback
import com.bihe0832.android.lib.http.common.HttpBasicRequest
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.text.TextFactoryUtils
import com.bihe0832.android.lib.thread.ThreadManager
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

open class BaseTestFragment : CommonListFragment() {

    open fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(TestItemData("test"))
        }
    }

    private val mTestDataLiveData by lazy {
        object : TestListLiveData() {
            override fun fetchData() {
                postValue(getDataList())
            }
        }
    }

    override fun getResID(): Int {
        return R.layout.fragment_test_tab
    }

    override fun getDataLiveData(): CommonListLiveData {
        return mTestDataLiveData
    }

    protected fun sendInfo(title: String, content: String) {
        DebugTools.sendInfo(context, title, content, false)
    }


    protected fun showInfo(title: String, content: String) {
        DebugTools.showInfo(context, title, content, "发送到第三方应用")
    }

    fun showInputDialog(titleName: String, msg: String, defaultValue: String, listener: InputDialogCompletedCallback) {
        DebugTools.showInputDialog(context, titleName, msg, defaultValue, listener)
    }


    protected fun startActivity(cls: Class<*>) {
        val intent = Intent(context, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    protected fun showResult(s: String?) {
        s?.let {
            ThreadManager.getInstance().runOnUIThread {
                ZLog.d(HttpBasicRequest.LOG_TAG, "showResult:$s")
                view?.findViewById<TextView>(R.id.test_tips)?.apply {
                    this.text = TextFactoryUtils.getSpannedTextByHtml("<B>提示信息</b>:<BR> $s")
                    visibility = View.VISIBLE
                }

            }
        }
    }


    fun openWeb(url: String) {
        val map = HashMap<String, String>()
        map[RouterConstants.INTENT_EXTRA_KEY_WEB_URL] = Uri.encode(url)
        RouterAction.openPageByRouter(WebPageActivity.MODULE_NAME_WEB_PAGE, map)
    }
}