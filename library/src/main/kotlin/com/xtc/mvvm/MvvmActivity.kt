/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.xtc.mvvm.Mvvm.log
import com.uber.autodispose.android.lifecycle.autoDispose
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

abstract class MvvmActivity : AppCompatActivity() ,IAutoDispose{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.i("[UI] [onCreate] ${this.javaClass.simpleName}")
    }

    override fun onResume() {
        super.onResume()
        log.i("[UI] [进入] ${this.javaClass.simpleName}")
    }

    override fun onPause() {
        super.onPause()
        log.i("[UI] [离开] ${this.javaClass.simpleName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        log.i("[UI] [onDestroy] ${this.javaClass.simpleName}")
    }

    override fun <T> Single<T>.autoDispose() = this.autoDispose(this@MvvmActivity)
    override fun <T> Observable<T>.autoDispose() = this.autoDispose(this@MvvmActivity)
    override fun <T> Flowable<T>.autoDispose() = this.autoDispose(this@MvvmActivity)

    fun <T> LiveData<T>.observeData(action: (T) -> Unit) {
        this.observe(this@MvvmActivity, Observer { action.invoke(it) })
    }

    fun <T> LiveData<Event<T>>.observeEventData(action: (T) -> Unit) {
        this.observe(this@MvvmActivity, Observer { event ->
            event.getContentIfNotHandled()?.let { it ->
                action.invoke(it)
            }
        })
    }

    fun <T> MutableLiveData<T>.observeData(action: (T) -> Unit) {
        this.observe(this@MvvmActivity, Observer { action.invoke(it) })
    }

    fun <T> MutableLiveData<Event<T>>.observeEventData(action: (T) -> Unit) {
        this.observe(this@MvvmActivity, Observer { event ->
            event.getContentIfNotHandled()?.let { it ->
                action.invoke(it)
            }
        })
    }

}
