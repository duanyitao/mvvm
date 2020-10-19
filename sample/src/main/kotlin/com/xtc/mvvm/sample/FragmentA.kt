/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm.sample

import android.os.SystemClock
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxbinding3.view.clicks
import com.xtc.mvvm.sample.databinding.FragmentABinding
import com.xtc.mvvm.subscribeBy
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class FragmentA : BaseFragment<AViewModel, FragmentABinding>() {
    override val _viewModel: AViewModel by viewModels()

    override fun initArgs() {
    }

    override fun initBinding(): FragmentABinding = FragmentABinding.inflate(layoutInflater)

    override fun bindUiToData() {
        _binding.viewModel = _viewModel
    }

    override fun initView() {
    }

    override fun subscribeUi() {
        _binding.btnUpdate.clicks().applyThrottleFirstStrategy {
            it.doOnNext {
                _viewModel.tips.value = "welcome, now is ${System.currentTimeMillis()}"
            }
        }

        _binding.btnJumpB.clicks().applyThrottleFirstStrategy {
            it.doOnNext {
                _navController.navigate(FragmentADirections.actionAToB())
            }
        }

        _viewModel.apply {
            autoWelcome.observeData {
                tips.value = it
            }
        }
    }

    override fun loadData() {
        _viewModel.autoWelcome()
            .subscribeWithAutoDispose()
    }

    override fun clearData() {
    }

    override fun onBackPressed() {
        requireActivity().finish()
    }
}

class AViewModel(
    private val repository: ARepository = ARepository()
) : BaseViewModel() {
    var tips = MutableLiveData("welcome")

    private val _autoWelcome = MutableLiveData<String>()
    val autoWelcome: LiveData<String> = _autoWelcome

    fun autoWelcome(): Flowable<String> {
        return repository.autoWelcome()
            .doOnNext {
                toast("自动更新")
                _autoWelcome.postValue(it)
            }
    }
}

class ARepository {
    fun autoWelcome(): Flowable<String> {
        return Flowable.interval(0, 10, TimeUnit.SECONDS)
            .map { "welcome, now is ${System.currentTimeMillis()}" }
    }
}
