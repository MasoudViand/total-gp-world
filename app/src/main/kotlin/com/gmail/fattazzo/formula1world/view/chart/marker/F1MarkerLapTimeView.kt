/*
 * Project: total-gp-world
 * File: F1MarkerLapTimeView.kt
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

package com.gmail.fattazzo.formula1world.view.chart.marker

import android.content.Context
import android.widget.TextView

import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.gmail.fattazzo.formula1world.R
import com.gmail.fattazzo.formula1world.domain.F1LapTime

class F1MarkerLapTimeView(context: Context) : MarkerView(context, R.layout.chart_marker_view_large) {

    private val tvContent: TextView
    private val tvContent2: TextView

    init {

        tvContent = findViewById<TextView>(R.id.tvContent)
        tvContent2 = findViewById<TextView>(R.id.tvContent2)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {

        if (e!!.data is F1LapTime) {
            val lapTime = e.data as F1LapTime

            tvContent.text = lapTime.driver!!.fullName
            tvContent2.text = lapTime.time
        } else {
            tvContent.text = ""
            tvContent2.text = ""
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}
