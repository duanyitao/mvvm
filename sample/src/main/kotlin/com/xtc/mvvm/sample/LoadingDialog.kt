/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm.sample

import android.app.Dialog
import android.content.Context
import com.xtc.mvvm.ILoadingDialog
import kotlinx.android.synthetic.main.dialog_loading.*

class LoadingDialog(context: Context) : Dialog(context, R.style.LoadingDialog), ILoadingDialog {
    init {
        setContentView(R.layout.dialog_loading)
        setCanceledOnTouchOutside(false)
    }

    override fun show(msg: String) {
        tv_loading_msg.text = msg
        super.show()
    }
}
