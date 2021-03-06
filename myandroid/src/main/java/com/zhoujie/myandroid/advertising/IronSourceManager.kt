package com.zhoujie.myandroid.advertising

import android.app.Activity
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.ironsource.mediationsdk.sdk.InterstitialListener
import com.ironsource.mediationsdk.sdk.RewardedVideoListener
import java.util.*

class IronSourceManager private constructor() : RewardedVideoListener, InterstitialListener {

    private var showRewardedVideo = false
    private var showInterstitial = true

    private var withCustomData = false
    private var key: String? = null
    private var value: String? = null
    private lateinit var mListener: Listener

    inner class Listener {
        var onLoading: (() -> Unit)? = null
        var onShow: (() -> Unit)? = null

        fun onLoading(operation: () -> Unit) {
            onLoading = operation
        }

        fun onShow(operation: () -> Unit) {
            onShow = operation
        }
    }

    companion object {
        private var instance: IronSourceManager? = null
        fun getInstance(): IronSourceManager =
            instance ?: synchronized(this) {
                instance ?: IronSourceManager().also { instance = it }
            }
    }

    fun init(activity: Activity, appKey: String, vararg adUnits: IronSource.AD_UNIT) {
        IronSource.init(activity, appKey, *adUnits)
    }

    fun showRewardedVideo(listener: Listener.() -> Unit) {
        mListener = Listener().also(listener)
        showRewardedVideo = true
        if (IronSource.isRewardedVideoAvailable()) {
            IronSource.showRewardedVideo()
        } else {
            mListener.onLoading?.invoke()
        }
    }

    fun showRewardedVideo() {
        showRewardedVideo = true
        if (IronSource.isRewardedVideoAvailable()) {
            IronSource.showRewardedVideo()
        }
    }

    fun showInterstitial(listener: Listener.() -> Unit) {
        mListener = Listener().also(listener)
        showInterstitial = true
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial()
        } else {
            mListener.onLoading?.invoke()
        }
    }

    fun showInterstitial() {
        showInterstitial = true
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial()
        }
    }

    fun showRewardedVideoWithCustomData(key: String, value: String) {
        showRewardedVideo = true
        withCustomData = true
        this.key = key
        this.value = value
    }

    init {
        IronSource.setRewardedVideoListener(this)
        IronSource.setInterstitialListener(this)
    }

    override fun onRewardedVideoAdOpened() {

    }

    override fun onRewardedVideoAdClosed() {

    }

    override fun onRewardedVideoAvailabilityChanged(available: Boolean) {
        if (available && showRewardedVideo) {
            if (key != null && value != null && withCustomData) {
                val map = HashMap<String, String>()
                map["data"] = value!!
                IronSource.setRewardedVideoServerParameters(map)
                withCustomData = false
            }
            IronSource.showRewardedVideo()
            if (::mListener.isInitialized) {
                mListener.onShow?.invoke()
            }
            showRewardedVideo = false
        }
        if (showRewardedVideo && !available) {
            if (::mListener.isInitialized) {
                mListener.onLoading?.invoke()
            }
        }
    }

    override fun onRewardedVideoAdStarted() {

    }

    override fun onRewardedVideoAdEnded() {

    }

    override fun onRewardedVideoAdRewarded(placement: Placement?) {

    }

    override fun onRewardedVideoAdShowFailed(error: IronSourceError?) {

    }

    override fun onRewardedVideoAdClicked(placement: Placement?) {

    }

    override fun onInterstitialAdReady() {
        if (showInterstitial) {
            IronSource.showInterstitial()
            showInterstitial = false
        }
    }

    override fun onInterstitialAdLoadFailed(error: IronSourceError?) {

    }

    override fun onInterstitialAdOpened() {

    }

    override fun onInterstitialAdClosed() {

    }

    override fun onInterstitialAdShowSucceeded() {
        if (::mListener.isInitialized) {
            mListener.onShow?.invoke()
            IronSource.loadInterstitial()
        }
    }

    override fun onInterstitialAdShowFailed(error: IronSourceError?) {

    }

    override fun onInterstitialAdClicked() {

    }
}