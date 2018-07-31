/*
 * Project: total-gp-world
 * File: DBUpdateActivity.kt
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

package com.gmail.fattazzo.formula1world.activity.dbupdate

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.activeandroid.ActiveAndroid
import com.activeandroid.util.ReflectionUtils
import com.gmail.fattazzo.formula1world.R
import com.gmail.fattazzo.formula1world.service.DataService
import com.gmail.fattazzo.formula1world.settings.ApplicationPreferenceManager
import com.gmail.fattazzo.formula1world.utils.Utils
import org.androidannotations.annotations.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream


/**
 * @author fattazzo
 *         <p/>
 *         date: 28/11/17
 */
@EActivity(R.layout.activity_dbupdate)
open class DBUpdateActivity : Activity() {

    @Extra
    @JvmField
    var versionToUpdate: Int = 0

    @Extra
    internal lateinit var currentVersione: String

    @Extra
    internal lateinit var onlineMinAppVersion: String

    @Bean
    internal lateinit var preferenceManager: ApplicationPreferenceManager

    @Bean
    internal lateinit var dataService: DataService

    @ViewById
    internal lateinit var downloadProgressBar: ProgressBar

    @ViewById
    internal lateinit var downloadProgressTV: TextView

    @ViewById
    internal lateinit var dbUpdateDescriptionTV: TextView

    @ViewById
    internal lateinit var minVersionTV: TextView

    @ViewById
    internal lateinit var cancelButton: Button

    private var downloadTask: DownloadTask? = null

    private var canClose: Boolean = true

    @AfterViews
    fun initViews() {
        dbUpdateDescriptionTV.text = Utils.getHtmlText(getString(R.string.dbupdate_message_text))

        minVersionTV.visibility = if (convertVersionToInt(currentVersione) < convertVersionToInt(onlineMinAppVersion)) View.VISIBLE else View.GONE
        var text = getString(R.string.dbupdate_versionecode_text)
        text = text.toString().replace("{0}", currentVersione).replace("{1}", onlineMinAppVersion)
        minVersionTV.text = Utils.getHtmlText(text)

        updateDBButtonClicked()
    }

    @Click
    fun updateDBButtonClicked() {
        if (convertVersionToInt(currentVersione) > convertVersionToInt(onlineMinAppVersion)) {
            downloadTask = DownloadTask(this)
            downloadTask!!.execute(DB_URL)
        }
    }

    private fun convertVersionToInt(version: String): Int {
        val stringSplit = version.split(".")
        return stringSplit[0].toInt() * 1000 + stringSplit[1].toInt()
    }

    @Click
    fun cancelButtonClicked() {
        if (downloadTask != null) {
            downloadTask!!.cancel(true)
        }
    }

    override fun onBackPressed() {
        if (canClose) {
            super.onBackPressed()
        }
    }

    private inner class DownloadTask(private val context: Context) : AsyncTask<String, Int, String?>() {

        var operation = "Download"

        override fun doInBackground(vararg sUrl: String): String? {
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            try {
                val url = URL(sUrl[0])
                connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return ("Server returned HTTP " + connection.responseCode + " " + connection.responseMessage)
                }

                val fileLength = connection.contentLength

                input = connection.inputStream

                val fileToDownload = File(DOWNLOAD_PATH + "f1db.zip")
                if(fileToDownload.exists()) {
                    fileToDownload.delete()
                }

                output = FileOutputStream(fileToDownload)

                val data = ByteArray(4096)
                var total: Long = 0

                while (true) {
                    val count = input!!.read(data)
                    if (count <= 0)
                        break

                    if (isCancelled) {
                        input.close()
                        canClose = true
                        return null
                    }
                    total += count.toLong()

                    if (fileLength > 0)
                        publishProgress((total * 100 / fileLength).toInt())
                    output.write(data, 0, count)
                }

                unzipDb(fileToDownload, DOWNLOAD_PATH)

                ActiveAndroid.dispose()

                val dbName = ReflectionUtils.getMetaData<String>(applicationContext, "AA_DB_NAME")
                deleteDatabase(dbName)

                File(DOWNLOAD_PATH + DOWNLOAD_FILE_NAME).renameTo(File(DOWNLOAD_PATH + dbName))
            } catch (e: Exception) {
                return e.toString()
            } finally {
                try {
                    if (output != null)
                        output.close()
                    if (input != null)
                        input.close()
                } catch (ignored: IOException) {
                }

                if (connection != null)
                    connection.disconnect()

                dataService.clearCache()
                ActiveAndroid.initialize(this@DBUpdateActivity)
            }
            return null
        }

        @Throws(IOException::class)
        private fun unzipDb(zipFilePath: File, destDirectory: String) {
            val zipIn = ZipInputStream(FileInputStream(zipFilePath))
            val entry = zipIn.nextEntry
            if (entry != null) {
                val filePath = destDirectory + File.separator + DOWNLOAD_FILE_NAME
                val bos = BufferedOutputStream(FileOutputStream(filePath))
                val bytesIn = ByteArray(4000)
                var read = 0
                while ({ read = zipIn.read(bytesIn); read }() != -1) {
                    bos.write(bytesIn, 0, read)
                }
                bos.close()
                zipIn.closeEntry()
            }
            zipIn.close()
        }

        override fun onPreExecute() {
            super.onPreExecute()
            canClose = false

            cancelButton.isEnabled = true

            downloadProgressTV.text = ""

            downloadProgressBar.max = 100
            downloadProgressBar.progress = 0
        }

        override fun onProgressUpdate(vararg progress: Int?) {
            super.onProgressUpdate(*progress)

            downloadProgressBar.isIndeterminate = false
            downloadProgressBar.max = 100
            downloadProgressBar.progress = progress[0]!!.or(0)

            downloadProgressTV.text = operation + ": " + downloadProgressBar.progress.toString() + "% / 100%"
        }

        override fun onPostExecute(result: String?) {
            canClose = true
            cancelButton.isEnabled = false
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show()
            else {
                Toast.makeText(context, "DB aggiornato alla versione " + versionToUpdate, Toast.LENGTH_SHORT).show()
                preferenceManager.prefs!!.sharedPreferences.edit().putInt("lastVersionDBFilesImported", versionToUpdate).apply()
            }

            this@DBUpdateActivity.finish()
        }
    }

    companion object {
        const val DB_URL = " https://raw.githubusercontent.com/fattazzo/total-gp-world/master/db/f1db.zip"

        const val DOWNLOAD_FILE_NAME = "f1dbnew"
        val DOWNLOAD_PATH = ActiveAndroid.getDatabase().path.removeSuffix("f1db")
    }
}