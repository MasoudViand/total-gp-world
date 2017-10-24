package com.gmail.fattazzo.formula1world.dbimage

import android.util.SparseIntArray

import com.activeandroid.query.Select
import com.gmail.fattazzo.formula1world.CustomRobolectricRunner
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.Race

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/**
 * @author fattazzo
 *
 *
 * date: 16/06/17
 */
@RunWith(CustomRobolectricRunner::class)
class DBRacesTest : BaseDBTest() {

    private var racesData: SparseIntArray? = null

    @Before
    override fun initDB() {
        super.initDB()

        racesData = SparseIntArray()
        racesData!!.put(1950, 7)
        racesData!!.put(1951, 8)
        racesData!!.put(1952, 8)
        racesData!!.put(1953, 9)
        racesData!!.put(1954, 9)
        racesData!!.put(1955, 7)
        racesData!!.put(1956, 8)
        racesData!!.put(1957, 8)
        racesData!!.put(1958, 11)
        racesData!!.put(1959, 9)
        racesData!!.put(1960, 10)
        racesData!!.put(1961, 8)
        racesData!!.put(1962, 9)
        racesData!!.put(1963, 10)
        racesData!!.put(1964, 10)
        racesData!!.put(1965, 10)
        racesData!!.put(1966, 9)
        racesData!!.put(1967, 11)
        racesData!!.put(1968, 12)
        racesData!!.put(1969, 11)
        racesData!!.put(1970, 13)
        racesData!!.put(1971, 11)
        racesData!!.put(1972, 12)
        racesData!!.put(1973, 15)
        racesData!!.put(1974, 15)
        racesData!!.put(1975, 14)
        racesData!!.put(1976, 16)
        racesData!!.put(1977, 17)
        racesData!!.put(1978, 16)
        racesData!!.put(1979, 15)
        racesData!!.put(1980, 14)
        racesData!!.put(1981, 15)
        racesData!!.put(1982, 16)
        racesData!!.put(1983, 15)
        racesData!!.put(1984, 16)
        racesData!!.put(1985, 16)
        racesData!!.put(1986, 16)
        racesData!!.put(1987, 16)
        racesData!!.put(1988, 16)
        racesData!!.put(1989, 16)
        racesData!!.put(1990, 16)
        racesData!!.put(1991, 16)
        racesData!!.put(1992, 16)
        racesData!!.put(1993, 16)
        racesData!!.put(1994, 16)
        racesData!!.put(1995, 17)
        racesData!!.put(1996, 16)
        racesData!!.put(1997, 17)
        racesData!!.put(1998, 16)
        racesData!!.put(1999, 16)
        racesData!!.put(2000, 17)
        racesData!!.put(2001, 17)
        racesData!!.put(2002, 17)
        racesData!!.put(2003, 16)
        racesData!!.put(2004, 18)
        racesData!!.put(2005, 19)
        racesData!!.put(2006, 18)
        racesData!!.put(2007, 17)
        racesData!!.put(2008, 18)
        racesData!!.put(2009, 17)
        racesData!!.put(2010, 19)
        racesData!!.put(2011, 19)
        racesData!!.put(2012, 20)
        racesData!!.put(2013, 19)
        racesData!!.put(2014, 19)
        racesData!!.put(2015, 19)
        racesData!!.put(2016, 21)
        racesData!!.put(2017, 20)
    }

    @Test
    fun testCount() {
        val total = Select().from(Race::class.java).count()
        assertTrue("Race count < 976", total >= 976)
        println(total.toString() + " Race found")
    }

    @Test
    fun testCountByYear() {

        for (i in 0 until racesData!!.size()) {
            val year = racesData!!.keyAt(i)
            val count = racesData!!.get(year)

            val countDB = Select().from(Race::class.java).where("year = ?", year).count()
            assertEquals("Wrong Race for year " + year, count.toLong(), countDB.toLong())
            println(countDB.toString() + " Race for year " + year)
        }
    }
}
