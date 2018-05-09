/*
 * Project: total-gp-world
 * File: RssNewsReader.kt
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

package com.gmail.fattazzo.formula1world.news.reader

import android.util.Log
import com.gmail.fattazzo.formula1world.news.NewsSource
import com.gmail.fattazzo.formula1world.news.objects.Channel
import com.gmail.fattazzo.formula1world.news.objects.News
import org.apache.commons.collections4.ListUtils
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author fattazzo
 *
 *
 * date: 20/07/17
 */
open class RssNewsReader : NewsReader {

    protected val channelElementTag: String
        get() = "channel"

    protected val newsElementTag: String
        get() = "item"

    protected val datePattern: String
        get() = "EEE, dd MMM yyyy HH:mm:ss Z"

    override fun loadNews(source: NewsSource): List<News> {

        val news = ArrayList<News>()

        try {
            val document = Jsoup.connect(source.url).parser(Parser.xmlParser()).get()

            // channel
            var channel: Channel? = null
            val channelElement = document.select(channelElementTag).first()
            if (channelElement != null) {
                channel = parseChannelElement(channelElement)
            }

            for (item in document.select(newsElementTag)) {
                val newsEntry = parseNewsElement(item, channel)
                news.add(newsEntry)
            }


        } catch (e: Exception) {
            Log.e(TAG, "Error", e)
        }

        return ListUtils.emptyIfNull(news)
    }

    protected fun parseChannelElement(element: Element): Channel {
        val title = element.getElementsByTag("title").first().text()
        val link = element.getElementsByTag("link").first().text()
        val description = element.getElementsByTag("description").first().text()
        val image = getChannelImage(element)

        val channel = Channel()
        channel.title = title
        channel.description = description
        channel.link = link
        channel.image = image
        return channel
    }

    protected open fun getChannelImage(channelElement: Element): String {
        return channelElement.getElementsByTag("image").first().select("url").text()
    }

    protected fun parseNewsElement(element: Element, channel: Channel?): News {
        val title = element.getElementsByTag("title").first().text()
        val description = getNewsDescription(element)
        val link = element.getElementsByTag("link").first().text()
        val image = getNewsImage(element)
        val pubDateStr = element.getElementsByTag("pubDate").first().text()
        var pubDate: Date? = null
        if (StringUtils.isNotBlank(pubDateStr)) {
            try {
                pubDate = SimpleDateFormat(datePattern, Locale.ENGLISH).parse(pubDateStr)
            } catch (e: ParseException) {
                pubDate = null
            }

        }

        val news = News()
        news.channel = channel
        news.title = title
        news.description = description
        news.link = link
        news.date = pubDate
        news.image = image
        return news
    }

    protected open fun getNewsImage(newsElement: Element): String? {
        val imgElement = newsElement.getElementsByTag("enclosure")
        return if (imgElement != null && imgElement.size > 0) {
            imgElement.first().text()
        } else null
    }

    protected open fun getNewsDescription(newsElement: Element): String {
        return newsElement.getElementsByTag("description").first().text()
    }

    companion object {

        private val TAG = RssNewsReader::class.java.simpleName
    }
}
