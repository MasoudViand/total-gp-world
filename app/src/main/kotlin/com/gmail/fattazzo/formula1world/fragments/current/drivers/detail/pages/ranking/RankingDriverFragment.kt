/*
 * Project: total-gp-world
 * File: RankingDriverFragment.kt
 *
 * Created by fattazzo
 * Copyright © 2018 Gianluca Fattarsi. All rights reserved.
 *
 * MIT License
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gmail.fattazzo.formula1world.fragments.current.drivers.detail.pages.ranking

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ProgressBar
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.gmail.fattazzo.formula1world.R
import com.gmail.fattazzo.formula1world.config.Config
import com.gmail.fattazzo.formula1world.domain.F1Driver
import com.gmail.fattazzo.formula1world.domain.F1Result
import com.gmail.fattazzo.formula1world.service.DataService
import com.gmail.fattazzo.formula1world.utils.ImageUtils
import org.androidannotations.annotations.*
import org.apache.commons.collections4.CollectionUtils
import java.util.*

/**
 * @author fattazzo
 *
 *
 * date: 21/04/17
 */
@EFragment(R.layout.fragment_driver_ranking)
open class RankingDriverFragment : Fragment() {

    @FragmentArg
    lateinit internal var driver: F1Driver

    @Bean
    lateinit internal var imageUtils: ImageUtils

    @Bean
    lateinit internal var chartManager: ChartManager

    @Bean
    lateinit internal var dataService: DataService

    @ViewById(R.id.ranking_driver_position_chart)
    lateinit internal var positionChart: LineChart

    @ViewById(R.id.ranking_driver_points_chart)
    lateinit internal var pointsChart: LineChart

    @ViewById
    lateinit internal var refreshFab: FloatingActionButton

    @ViewById
    lateinit internal var progressBar: ProgressBar

    private var raceResults: List<F1Result>? = null
    private var leaderRaceResults: List<F1Result>? = null

    @AfterViews
    internal fun init() {
        chartManager!!.configureChart(positionChart!!)
        chartManager!!.configureChart(pointsChart!!)

        loadData()
    }

    @Click
    internal fun refreshFabClicked() {
        dataService!!.clearDriverRaceResultsCache(driver!!)
        if (CollectionUtils.isNotEmpty(leaderRaceResults)) {
            dataService!!.clearDriverRaceResultsCache(leaderRaceResults!![0].driver!!)
        }
        loadData()
    }

    @UiThread
    internal open fun startLoad() {
        if (progressBar != null) {
            progressBar!!.visibility = View.VISIBLE
        }
    }

    @Background
    internal open fun loadData() {
        startLoad()

        raceResults = dataService!!.loadDriverRacesResult(driver!!)

        leaderRaceResults = ArrayList()
        val driverLeader = dataService!!.loadDriverLeader()
        if (driverLeader != null) {
            leaderRaceResults = dataService!!.loadDriverRacesResult(driverLeader.driver!!)
        }

        loadChartData()
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    internal open fun loadChartData() {
        try {
            if (positionChart != null && pointsChart != null) {
                if (raceResults!!.isNotEmpty()) {
                    chartManager!!.loadPositionsChartData(positionChart!!, raceResults!!)
                }
                if (leaderRaceResults!!.isNotEmpty()) {
                    chartManager!!.loadPointsChartData(pointsChart!!, raceResults!!, leaderRaceResults!!)
                }
                startChatsAnimation()
            }
        } finally {
            if (progressBar != null) {
                progressBar!!.visibility = View.INVISIBLE
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser && positionChart != null) {
            startChatsAnimation()
        }
    }

    private fun startChatsAnimation() {
        val animDuration = if (Config.animationEnabled) 1000 else 0

        positionChart!!.animateY(animDuration, Easing.EasingOption.EaseInOutSine)
        pointsChart!!.animateY(animDuration, Easing.EasingOption.EaseInOutSine)
    }

    companion object {

        fun newInstance(driver: F1Driver): RankingDriverFragment {
            val rankingDriverFragment = RankingDriverFragment_()
            val args = Bundle()
            args.putSerializable("driver", driver)
            rankingDriverFragment.arguments = args
            return rankingDriverFragment
        }
    }

}
