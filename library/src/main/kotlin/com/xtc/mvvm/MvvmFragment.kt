/*
 * Copyright 2015-2020 colin.
 */

@file:Suppress("PropertyName")

package com.xtc.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

abstract class MvvmFragment<VM : MvvmViewModel, Binding : ViewDataBinding> : Fragment(),
    IMvvmFragment<VM, Binding> {
    lateinit var _binding: Binding
    override lateinit var _lifecycleOwner: LifecycleOwner
    override lateinit var _activity: FragmentActivity
    val _navController: NavController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity()
        runInOnCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _lifecycleOwner = viewLifecycleOwner
        _binding = initBinding().apply {
            lifecycleOwner = this@MvvmFragment._lifecycleOwner
        }
        return _binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        runInOnActivityCreated()
    }

    override fun onResume() {
        super.onResume()
        runInOnResume()
    }

    override fun onPause() {
        super.onPause()
        runInOnPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        runInOnDestroy()
    }

    override fun onBackPressed() {
        _navController.navigateUp()
    }
}

abstract class MvvmDialogFragment<VM : MvvmViewModel, Binding : ViewDataBinding> : DialogFragment(),
    IMvvmFragment<VM, Binding> {
    lateinit var _binding: Binding
    override lateinit var _lifecycleOwner: LifecycleOwner
    override lateinit var _activity: FragmentActivity
    val _navController: NavController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _lifecycleOwner = this
        _activity = requireActivity()
        runInOnCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _lifecycleOwner = viewLifecycleOwner
        _binding = initBinding().apply {
            lifecycleOwner = this@MvvmDialogFragment._lifecycleOwner
        }
        return _binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        runInOnActivityCreated()
    }

    override fun onResume() {
        super.onResume()
        runInOnResume()
    }

    override fun onPause() {
        super.onPause()
        runInOnPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        runInOnDestroy()
    }

    override fun onBackPressed() {
        _navController.navigateUp()
    }
}
