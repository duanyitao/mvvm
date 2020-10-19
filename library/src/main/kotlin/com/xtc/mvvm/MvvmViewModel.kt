/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm

import android.content.ContentProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class MvvmViewModel : ViewModel() {
    private val _showLoading = MutableLiveData<Event<String>>()
    private val _dismissLoading = MutableLiveData<Event<Unit>>()
    private val _toastMsg = MutableLiveData<Event<String>>()
    private val _toastMsgWithImg = MutableLiveData<Event<Pair<String, Int>>>()

    val showLoading: LiveData<Event<String>> = _showLoading
    val dismissLoading: LiveData<Event<Unit>> = _dismissLoading
    val toastMsg: LiveData<Event<String>> = _toastMsg
    val toastMsgWithImg: LiveData<Event<Pair<String, Int>>> = _toastMsgWithImg

    protected fun showLoading(msg: String = appContext.getString(R.string.mvvm_loading)) {
        _showLoading.postEventValue(msg)
    }

    protected fun dismissLoading() {
        _dismissLoading.postEventValue(Unit)
    }

    protected fun toast(msg: String) {
        _toastMsg.postEventValue(msg)
    }

    protected fun toastWithImg(pair: Pair<String, Int>) {
        _toastMsgWithImg.postEventValue(pair)
    }
}
