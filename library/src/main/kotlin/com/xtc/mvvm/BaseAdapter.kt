/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.xtc.mvvm.Mvvm.log
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SuperViewHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

open class BaseAdapter<T>(
    private val itemLayout: Int = 0,
    private val itemDataBRId: Int,
    private val listenerMap: Map<Int, (Flowable<Triple<Int, T, View>>) -> Flowable<*>> = emptyMap()
) : RecyclerView.Adapter<SuperViewHolder>() {
    var list: MutableList<T> = mutableListOf()

    open fun getItemLayout(item: T): Int {
        return itemLayout
    }

    open fun notifyDataSetChanged(newList: List<T>) {
        val temp = mutableListOf<T>()
        temp.addAll(newList)
        this.list.clear()
        this.list.addAll(temp)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return getItemLayout(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            viewType, parent,
            false
        )
        return SuperViewHolder(binding)
    }

    @SuppressLint("CheckResult")
    protected fun <T> Observable<in T>.applyDebounceStrategy(
        next: ((Flowable<in T>) -> Flowable<in T>)
    ) {
        this.toFlowable(BackpressureStrategy.DROP)
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Flowable.just(it)
                    .mergeWith(next.invoke(Flowable.just(it)))
                    .doOnError { e -> log.e(e.toString()) }
                    .onErrorResumeNext(Flowable.empty())
            }
            .subscribe({}, { log.e(it.toString()) })
    }

    override fun onBindViewHolder(holder: SuperViewHolder, position: Int) {
        holder.binding.setVariable(itemDataBRId, list[position])
        listenerMap.forEach { e ->
            val byId: View? = holder.binding.root.findViewById(e.key)
            byId?.clicks()?.applyDebounceStrategy {
                it.flatMap {
                    e.value.invoke(Flowable.just(Triple(position, list[position], byId)))
                }
            }
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
