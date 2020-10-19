/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm

import com.uber.autodispose.FlowableSubscribeProxy
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.SingleSubscribeProxy
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

interface IAutoDispose {


    fun <T> Single<T>.autoDispose():SingleSubscribeProxy<T>
    fun <T> Observable<T>.autoDispose():ObservableSubscribeProxy<T>
    fun <T> Flowable<T>.autoDispose():FlowableSubscribeProxy<T>


    fun <T> Single<T>.subscribeWithAutoDispose(){
        this.toFlowable().autoDispose().subscribeBy {  }
    }
    fun <T> Observable<T>.subscribeWithAutoDispose(){
        this.toFlowable(BackpressureStrategy.BUFFER).autoDispose().subscribeBy {  }
    }

    fun <T> Flowable<T>.subscribeWithAutoDispose(){
        this.autoDispose().subscribeBy {  }
    }

}