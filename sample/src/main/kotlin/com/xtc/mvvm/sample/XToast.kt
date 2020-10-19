/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm.sample

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.DrawableRes
import com.xtc.mvvm.IToast
import com.xtc.mvvm.Mvvm.log
import com.xtc.mvvm.appContext
import com.xtc.mvvm.sample.databinding.ToastBinding
import com.xtc.mvvm.sample.databinding.ToastWithImgBinding
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

class XToast : IToast {
    private var currentToast: Toast? = null

    override fun toastWithImg(msg: String, @DrawableRes imageResId: Int): Flowable<Unit> =
        Flowable.just(appContext)
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                currentToast?.cancel()
                val binding = ToastWithImgBinding.inflate(LayoutInflater.from(it))
                binding.msg = msg
                binding.imageResId = imageResId
                log.i("[UI] [显示 toast] $msg")
                Toast(it).run {
                    view = binding.root
                    setGravity(Gravity.CENTER, 0, 0)
                    show()
                    currentToast = this
                }
            }.map { Unit }

    override fun toast(msg: String): Flowable<Unit> =
        Flowable.just(appContext)
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                currentToast?.cancel()
                val binding = ToastBinding.inflate(LayoutInflater.from(it))
                binding.msg = msg
                log.i("[UI] [显示 toast] $msg")
                Toast.makeText(it, msg, Toast.LENGTH_SHORT).run {
                    view = binding.root
                    setGravity(Gravity.CENTER, 0, 0)
                    show()
                    currentToast = this
                }
            }.map { Unit }
}
