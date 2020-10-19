/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm.sample

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.xtc.mvvm.Event
import com.xtc.mvvm.appContext
import com.xtc.mvvm.sample.databinding.ActivityMainBinding
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        this.setContentView(binding.root)
        binding.srContent.setOnRefreshListener {
            binding.viewModel?.refresh()?.subscribeWithAutoDispose()
        }

        binding.viewModel?.apply {
            tips.observeEventData {
                Toast.makeText(appContext,it,Toast.LENGTH_LONG).show()
            }
            isRefreshing.observeEventData {
                binding.srContent.isRefreshing = it
            }
        }
    }
}

class MainActivityViewModel(
    private val repository: MainActivityRepository = MainActivityRepository()
) : BaseViewModel() {
    var isRefreshing: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val tips: MutableLiveData<Event<String>> = MutableLiveData()

    fun refresh(): Flowable<Unit> {
        return repository.refresh()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                tips.postValue(Event("加载中"))
            }
            .flatMap { return@flatMap Single.timer(5, TimeUnit.SECONDS) }
            .toFlowable()
            .doOnNext {
                tips.postValue(Event("加载成功"))
            }
            .doOnNext { isRefreshing.postValue(Event(false)) }
            .map { Unit }
    }
}

class MainActivityRepository {
    fun refresh(): Single<Unit> {
        return Single.just(Unit)
    }
}
