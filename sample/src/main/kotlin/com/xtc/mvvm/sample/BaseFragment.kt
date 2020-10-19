/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm.sample

import androidx.databinding.ViewDataBinding
import com.xtc.mvvm.ILoadingDialog
import com.xtc.mvvm.IToast
import com.xtc.mvvm.MvvmDialogFragment
import com.xtc.mvvm.MvvmFragment
import com.xtc.mvvm.MvvmViewModel

abstract class BaseFragment<VM : MvvmViewModel, Binding : ViewDataBinding> : MvvmFragment<VM, Binding>() {
    override val _toast: IToast by lazy {
        XToast()
    }

    override val _loadingDialog: ILoadingDialog by lazy {
        LoadingDialog(requireContext())
    }

    override fun initViewInBase() {
    }
}

abstract class BaseDialogFragment<VM : MvvmViewModel, Binding : ViewDataBinding> : MvvmDialogFragment<VM, Binding>() {
    override val _toast: IToast by lazy {
        XToast()
    }

    override val _loadingDialog: ILoadingDialog by lazy {
        LoadingDialog(requireContext())
    }

    override fun initViewInBase() {
    }
}
