/*
 * Copyright 2015-2020 colin.
 */

package com.xtc.mvvm

import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.uber.autodispose.FlowableSubscribeProxy
import com.uber.autodispose.android.lifecycle.autoDispose
import com.xtc.mvvm.Mvvm.log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

@Suppress("PropertyName")
interface IMvvmFragment<VM : MvvmViewModel, Binding : ViewDataBinding>:IAutoDispose {
    /**
     * 使用委托的方式初始化.
     * ```
     * override val _viewModel: VM by viewModels()
     * ```
     */
    val _viewModel: VM

    /**
     * 实现 IToast 接口的 toast 控件.
     * ```
     * override val _toast = SampleToast()
     * ```
     */
    val _toast: IToast

    /**
     * 实现 ILoadingDialog 接口的 dialog 控件.
     * ```
     * override val _loadingDialog = SampleLoadingDialog()
     * ```
     */
    val _loadingDialog: ILoadingDialog

    /**
     * 使用 viewLifecycleOwner 赋值，以避免生命周期问题.
     * ```
     * override lateinit var _lifecycleOwner
     *
     * override fun onCreateView() {
     *     _lifecycleOwner = viewLifecycleOwner
     * }
     * ```
     */
    var _lifecycleOwner: LifecycleOwner

    /**
     * 使用 requireActivity() 赋值.
     * ```
     * override lateinit var _activity
     *
     * override fun onCreate(
     *     _activity = requireActivity()
     * }
     * ```
     */
    var _activity: FragmentActivity

    // onCreate
    // -------------------------------------------------------------------------------------------------

    /**
     * 取出 Navigation 传递的参数保存到 [_viewModel] 内.
     * ```
     * override fun initArgs() {
     *     viewModel.args = SampleFragmentArgs.fromBundle(requireArguments())
     * }
     * ```
     * Run in [Fragment.onCreate].
     */
    fun initArgs()

    // onCreateView
    // -------------------------------------------------------------------------------------------------

    /**
     * 初始化 [ViewDataBinding] 对象.
     * ```
     * override fun initBinding() = FragmentSampleBinding.inflate(layoutInflater)
     * ```
     * Run in [Fragment.onCreateView].
     */
    fun initBinding(): Binding

    // onActivityCreated
    // -------------------------------------------------------------------------------------------------

    /**
     * 通过 DataBinding 将视图绑定到数据源 [_viewModel] 上.
     * ```
     * override fun bindUiToData() {
     *     binding.viewModel = viewModel
     * }
     * ```
     * Run in [Fragment.onActivityCreated].
     */
    fun bindUiToData()

    /**
     * 初始化通用视图相关功能，例如 toolbar.
     *
     * Run in [Fragment.onActivityCreated].
     */
    fun initViewInBase()

    /**
     * 初始化视图相关功能.
     *
     * Run in [Fragment.onActivityCreated].
     */
    fun initView()

    /**
     * UI 订阅（观察）数据变化.
     * ```
     * override fun subscribeUi() {
     *     viewModel.sample.observeData {
     *
     *     }
     * }
     * ```
     * Run in [Fragment.onActivityCreated].
     */
    fun subscribeUi()

    // onResume
    // -------------------------------------------------------------------------------------------------

    /** [Fragment.onResume()]. */
    fun loadData()

    // onPause
    // -------------------------------------------------------------------------------------------------

    /** [Fragment.onPause()]. */
    fun clearData()

    // Run in callback.
    // -------------------------------------------------------------------------------------------------

    /** do not use it directly in your codes. */
    fun runInOnCreate() {
        log.i("[UI] [onCreate] ${this.javaClass.simpleName}")
        initArgs()
    }

    /** do not use it directly in your codes. */
    fun runInOnActivityCreated() {
        _activity.onBackPressedDispatcher.addCallback(_lifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }
        )
        bindUiToData()
        initViewInBase()
        initView()
        subscribeUiInBase()
        subscribeUi()
    }

    /** do not use it directly in your codes. */
    fun runInOnResume() {
        log.i("[UI] [进入] ${this.javaClass.simpleName}")
        loadData()
    }

    /** do not use it directly in your codes. */
    fun runInOnPause() {
        log.i("[UI] [离开] ${this.javaClass.simpleName}")
        clearData()
    }

    /** do not use it directly in your codes. */
    fun runInOnDestroy() {
        log.i("[UI] [onDestroy] ${this.javaClass.simpleName}")
    }

    // -------------------------------------------------------------------------------------------------

    fun toast(msg: String) {
        _toast.toast(msg)
            .subscribeWithAutoDispose()
    }

    fun toastWithImg(msg: String, @DrawableRes imageResource: Int) {
        _toast.toastWithImg(msg, imageResource)
            .subscribeWithAutoDispose()
    }

    fun showLoading(msg: String = appContext.getString(R.string.mvvm_loading)) {
        log.i("[UI] [显示加载中] $msg")
        _loadingDialog.show(msg)
    }

    fun dismissLoading() {
        log.i("[UI] [隐藏加载中]")
        _loadingDialog.dismiss()
    }

    fun onBackPressed()

    fun <T> LiveData<T>.observeData(action: (T) -> Unit) {
        this.observe(_lifecycleOwner, Observer { action.invoke(it) })
    }

    fun <T> LiveData<Event<T>>.observeEventData(action: (T) -> Unit) {
        this.observe(_lifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { it ->
                action.invoke(it)
            }
        })
    }

    fun <T> MutableLiveData<T>.observeData(action: (T) -> Unit) {
        this.observe(_lifecycleOwner, Observer { action.invoke(it) })
    }

    fun <T> MutableLiveData<Event<T>>.observeEventData(action: (T) -> Unit) {
        this.observe(_lifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { it ->
                action.invoke(it)
            }
        })
    }

    override fun <T> Single<T>.autoDispose() = this.autoDispose(_lifecycleOwner)
    override fun <T> Observable<T>.autoDispose() = this.autoDispose(_lifecycleOwner)
    override fun <T> Flowable<T>.autoDispose() = this.autoDispose(_lifecycleOwner)

    /** 在固定时间内只发射第一次事件，事件发射后，经过固定时间重新计时，适合防止按钮重复点击. */
    fun <T> Flowable<T>.applyThrottleFirstStrategy(block: (Flowable<T>) -> Flowable<*>) {
        this.throttleFirst(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                block.invoke(Flowable.just(it))
                    .doOnError { e -> log.e(e.toString()) }
                    .onErrorResumeNext(Flowable.empty())
            }
            .subscribeWithAutoDispose()
    }

    /** 在固定时间内只发射第一次事件，事件发射后，经过固定时间重新计时，适合防止按钮重复点击. */
    fun <T> Observable<T>.applyThrottleFirstStrategy(block: (Flowable<T>) -> Flowable<*>) {
        this.toFlowable(BackpressureStrategy.DROP)
            .applyThrottleFirstStrategy(block)
    }

    /** 经过固定时间后发射最后唯一一次事件，如果一个时间间隔内出现多次事件，则以最后一次事件发生的时间起重新计时，适合防止搜索框输入过快导致大量搜索. */
    fun <T> Flowable<T>.applyDebounceStrategy(block: (Flowable<T>) -> Flowable<*>) {
        this.debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                block.invoke(Flowable.just(it))
                    .doOnError { e -> log.e(e.toString()) }
                    .onErrorResumeNext(Flowable.empty())
            }
            .subscribeWithAutoDispose()
    }

    /** 经过固定时间后发射最后唯一一次事件，如果一个时间间隔内出现多次事件，则以最后一次事件发生的时间起重新计时，适合防止搜索框输入过快导致大量搜索. */
    fun <T> Observable<T>.applyDebounceStrategy(block: (Flowable<T>) -> Flowable<*>) {
        this.toFlowable(BackpressureStrategy.DROP)
            .applyDebounceStrategy(block)
    }

    private fun subscribeUiInBase() {
        _viewModel.apply {
            showLoading.observeEventData { showLoading(it) }
            dismissLoading.observeEventData { dismissLoading() }
            toastMsg.observeEventData { toast(it) }
            toastMsgWithImg.observeEventData { toastWithImg(it.first, it.second) }
        }
    }
}
