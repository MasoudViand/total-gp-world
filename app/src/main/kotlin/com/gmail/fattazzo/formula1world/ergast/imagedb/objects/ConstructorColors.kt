/*
 * Project: total-gp-world
 * File: ConstructorColors.kt
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

package com.gmail.fattazzo.formula1world.ergast.imagedb.objects

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * @author fattazzo
 *
 *
 * date: 13/07/17
 */
@Table(name = "constructorColors")
class ConstructorColors : Model() {

    @Column
    var hex: String? = null

    @Column
    var year: Int = 0

    @Column(name = "constructorId")
    var constructor: Constructor? = null

    @Column(name = "driverId")
    var driver: Driver? = null

    @Column
    internal var rgbRed: Int = 0

    @Column
    internal var rgbGreen: Int = 0

    @Column
    internal var rgbBlue: Int = 0

    override fun toString(): String {
        return "ConstructorColors{" +
                "year=" + year +
                ", constructor=" + constructor +
                ", driver=" + driver +
                ", rgbRed=" + rgbRed +
                ", rgbGreen=" + rgbGreen +
                ", rgbBlue=" + rgbBlue +
                ", hex='" + hex + '\'' +
                '}'
    }
}
