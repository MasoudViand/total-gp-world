/*
 * Project: total-gp-world
 * File: ResultsRaceFragment.kt
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

package com.gmail.fattazzo.formula1world.fragments.current.races.detail.pages.results

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.widget.TableLayout
import com.gmail.fattazzo.formula1world.R
import com.gmail.fattazzo.formula1world.domain.F1Race
import com.gmail.fattazzo.formula1world.domain.F1Result
import com.gmail.fattazzo.formula1world.fragments.ITitledFragment
import com.gmail.fattazzo.formula1world.service.DataService
import org.androidannotations.annotations.*

@EFragment(R.layout.fragment_race_results)
open class ResultsRaceFragment : Fragment(), ITitledFragment, SwipeRefreshLayout.OnRefreshListener {
    override val titleResId: Int
        get() = R.string.detail_race_results_tab_title

    @FragmentArg
    lateinit internal var race: F1Race

    @ViewById(R.id.results_race_table_layout)
    lateinit internal var tableLayout: TableLayout

    @ViewById
    lateinit internal var swipe_refresh_layout: SwipeRefreshLayout

    @Bean
    lateinit internal var dataService: DataService

    private var raceResults: List<F1Result>? = null

    @AfterViews
    internal fun init() {
        tableLayout!!.removeViews(1, tableLayout!!.childCount - 1)

        swipe_refresh_layout!!.setOnRefreshListener(this)

        load()
    }

    @UiThread
    internal open fun startLoad() {
        if (swipe_refresh_layout != null) {
            swipe_refresh_layout!!.isRefreshing = true
        }
    }

    @Background
    internal open fun load() {
        startLoad()

        raceResults = dataService!!.loadRaceResult(race!!)
        updateUI()
    }

    @UiThread
    internal open fun updateUI() {
        try {
            if (tableLayout != null) {
                tableLayout!!.removeViews(1, tableLayout!!.childCount - 1)

                var rowNumber = 1
                for (result in raceResults!!) {
                    val row = ResultsRaceItemView_.build(activity, result, rowNumber)
                    tableLayout!!.addView(row, TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT))
                    rowNumber++
                }
            }
        } finally {
            if (swipe_refresh_layout != null) {
                swipe_refresh_layout!!.isRefreshing = false
            }
        }
    }

    override fun onRefresh() {
        dataService!!.clearRaceResultsCache(race!!)
        load()
    }

    companion object {

        fun newInstance(race: F1Race): ResultsRaceFragment {
            val resultsFragment = ResultsRaceFragment_()
            val args = Bundle()
            args.putSerializable("race", race)
            resultsFragment.arguments = args
            return resultsFragment
        }
    }
}