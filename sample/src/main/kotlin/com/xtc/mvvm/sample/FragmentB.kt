/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm.sample

import androidx.fragment.app.viewModels
import com.xtc.mvvm.sample.databinding.FragmentBBinding
import com.jakewharton.rxbinding3.view.clicks

class FragmentB : BaseFragment<BViewModel, FragmentBBinding>() {
    override val _viewModel: BViewModel by viewModels()

    override fun initArgs() {
    }

    override fun initBinding(): FragmentBBinding = FragmentBBinding.inflate(layoutInflater)

    override fun bindUiToData() {
    }

    override fun initView() {
    }

    override fun subscribeUi() {
        _binding.btnBack.clicks().applyThrottleFirstStrategy {
            it.doOnNext {
                _navController.navigateUp()
            }
        }
    }

    override fun loadData() {
    }

    override fun clearData() {
    }
}

class BViewModel(
    private val repository: BRepository = BRepository()
) : BaseViewModel()

class BRepository
