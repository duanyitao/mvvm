/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm

import android.content.Context
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.xtc.mvvm.Mvvm.log
import com.uber.autodispose.FlowableSubscribeProxy
import io.reactivex.Flowable

object Mvvm {
    const val TAG = "mvvm"
    var log: IMvvmLog = object : IMvvmLog {
        override fun d(message: String, vararg args: Any?) {
            Log.d(TAG, message.format(*args))
        }

        override fun i(message: String, vararg args: Any?) {
            Log.i(TAG, message.format(*args))
        }

        override fun w(message: String, vararg args: Any?) {
            Log.w(TAG, message.format(*args))
        }

        override fun e(message: String, vararg args: Any?) {
            Log.e(TAG, message.format(*args))
        }
    }
}

interface IMvvmLog {
    fun d(message: String, vararg args: Any?)
    fun i(message: String, vararg args: Any?)
    fun w(message: String, vararg args: Any?)
    fun e(message: String, vararg args: Any?)
}

interface IToast {
    fun toast(msg: String): Flowable<Unit>
    fun toastWithImg(msg: String, @DrawableRes imageResId: Int): Flowable<Unit>
}

interface ILoadingDialog {
    fun show(msg: String)
    fun dismiss()
}

fun <T> MutableLiveData<Event<T>>.postEventValue(value: T) {
    postValue(Event(value))
}

fun <T> FlowableSubscribeProxy<T>.subscribeBy(
    onNext: ((T) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null
) {
    this.subscribe(
        { onNext?.invoke(it) },
        {
            log.e(it.toString())
            onError?.invoke(it)
        },
        { onComplete?.invoke() }
    )
}
