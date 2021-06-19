/*
 * The MIT License
 *
 * Copyright 2021 buddhika.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lk.gov.health.phsp.pojcs;

import lk.gov.health.phsp.entity.Area;
import lk.gov.health.phsp.entity.Institution;

/**
 *
 * @author buddhika
 */
public class CovidData {

    private Institution institution;
    private Area area;

    private Long todaysCases;
    private Long yesterdaysCases;
    private Long lastSevenDaysCases;
    private Long lastFourteenToSevenDaysCases;
    private Long thisMonthCases;
    private Long lastMonthCases;
    private Long thisYearCases;
    
     private Long todaysTests;
    private Long yesterdaysTests;
    private Long lastSevenDaysTests;
    private Long lastFourteenToSevenDaysTests;
    private Long thisMonthTests;
    private Long lastMonthTests;
    private Long thisYearTests;

    private Long todaysPcrs;
    private Long yesterdaysPcrs;
    private Long lastSevenDaysPcrs;
    private Long lastFourteenToSevenDaysPcrs;
    private Long thisMonthPcrs;
    private Long lastMonthPcrs;
    private Long thisYearPcrs;

    private Long todaysRats;
    private Long yesterdaysRats;
    private Long lastSevenDaysRats;
    private Long lastFourteenToSevenDaysRats;
    private Long thisMonthRats;
    private Long lastMonthRats;
    private Long thisYearRats;

    public Long getTodaysCases() {
        return todaysCases;
    }

    public void setTodaysCases(Long todaysCases) {
        this.todaysCases = todaysCases;
    }

    public Long getYesterdaysCases() {
        return yesterdaysCases;
    }

    public void setYesterdaysCases(Long yesterdaysCases) {
        this.yesterdaysCases = yesterdaysCases;
    }

    public Long getLastSevenDaysCases() {
        return lastSevenDaysCases;
    }

    public void setLastSevenDaysCases(Long lastSevenDaysCases) {
        this.lastSevenDaysCases = lastSevenDaysCases;
    }

    public Long getLastFourteenToSevenDaysCases() {
        return lastFourteenToSevenDaysCases;
    }

    
    
    public void setLastFourteenToSevenDaysCases(Long lastFourteenToSevenDaysCases) {
        this.lastFourteenToSevenDaysCases = lastFourteenToSevenDaysCases;
    }

    public Long getThisMonthCases() {
        return thisMonthCases;
    }

    public void setThisMonthCases(Long thisMonthCases) {
        this.thisMonthCases = thisMonthCases;
    }

    public Long getLastMonthCases() {
        return lastMonthCases;
    }

    public void setLastMonthCases(Long lastMonthCases) {
        this.lastMonthCases = lastMonthCases;
    }

    public Long getThisYearCases() {
        return thisYearCases;
    }

    public void setThisYearCases(Long thisYearCases) {
        this.thisYearCases = thisYearCases;
    }

    public Long getTodaysPcrs() {
        return todaysPcrs;
    }

    public void setTodaysPcrs(Long todaysPcrs) {
        this.todaysPcrs = todaysPcrs;
    }

    public Long getYesterdaysPcrs() {
        return yesterdaysPcrs;
    }

    public void setYesterdaysPcrs(Long yesterdaysPcrs) {
        this.yesterdaysPcrs = yesterdaysPcrs;
    }

    public Long getLastSevenDaysPcrs() {
        return lastSevenDaysPcrs;
    }

    public void setLastSevenDaysPcrs(Long lastSevenDaysPcrs) {
        this.lastSevenDaysPcrs = lastSevenDaysPcrs;
    }

    public Long getLastFourteenToSevenDaysPcrs() {
        return lastFourteenToSevenDaysPcrs;
    }

    public void setLastFourteenToSevenDaysPcrs(Long lastFourteenToSevenDaysPcrs) {
        this.lastFourteenToSevenDaysPcrs = lastFourteenToSevenDaysPcrs;
    }

    public Long getThisMonthPcrs() {
        return thisMonthPcrs;
    }

    public void setThisMonthPcrs(Long thisMonthPcrs) {
        this.thisMonthPcrs = thisMonthPcrs;
    }

    public Long getLastMonthPcrs() {
        return lastMonthPcrs;
    }

    public void setLastMonthPcrs(Long lastMonthPcrs) {
        this.lastMonthPcrs = lastMonthPcrs;
    }

    public Long getThisYearPcrs() {
        return thisYearPcrs;
    }

    public void setThisYearPcrs(Long thisYearPcrs) {
        this.thisYearPcrs = thisYearPcrs;
    }

    public Long getTodaysRats() {
        return todaysRats;
    }

    public void setTodaysRats(Long todaysRats) {
        this.todaysRats = todaysRats;
    }

    public Long getYesterdaysRats() {
        return yesterdaysRats;
    }

    public void setYesterdaysRats(Long yesterdaysRats) {
        this.yesterdaysRats = yesterdaysRats;
    }

    public Long getLastSevenDaysRats() {
        return lastSevenDaysRats;
    }

    public void setLastSevenDaysRats(Long lastSevenDaysRats) {
        this.lastSevenDaysRats = lastSevenDaysRats;
    }

    public Long getLastFourteenToSevenDaysRats() {
        return lastFourteenToSevenDaysRats;
    }

    public void setLastFourteenToSevenDaysRats(Long lastFourteenToSevenDaysRats) {
        this.lastFourteenToSevenDaysRats = lastFourteenToSevenDaysRats;
    }

    public Long getThisMonthRats() {
        return thisMonthRats;
    }

    public void setThisMonthRats(Long thisMonthRats) {
        this.thisMonthRats = thisMonthRats;
    }

    public Long getLastMonthRats() {
        return lastMonthRats;
    }

    public void setLastMonthRats(Long lastMonthRats) {
        this.lastMonthRats = lastMonthRats;
    }

    public Long getThisYearRats() {
        return thisYearRats;
    }

    public void setThisYearRats(Long thisYearRats) {
        this.thisYearRats = thisYearRats;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Long getTodaysTests() {
        return todaysTests;
    }

    public void setTodaysTests(Long todaysTests) {
        this.todaysTests = todaysTests;
    }

    public Long getYesterdaysTests() {
        return yesterdaysTests;
    }

    public void setYesterdaysTests(Long yesterdaysTests) {
        this.yesterdaysTests = yesterdaysTests;
    }

    public Long getLastSevenDaysTests() {
        return lastSevenDaysTests;
    }

    public void setLastSevenDaysTests(Long lastSevenDaysTests) {
        this.lastSevenDaysTests = lastSevenDaysTests;
    }

    public Long getLastFourteenToSevenDaysTests() {
        return lastFourteenToSevenDaysTests;
    }

    public void setLastFourteenToSevenDaysTests(Long lastFourteenToSevenDaysTests) {
        this.lastFourteenToSevenDaysTests = lastFourteenToSevenDaysTests;
    }

    public Long getThisMonthTests() {
        return thisMonthTests;
    }

    public void setThisMonthTests(Long thisMonthTests) {
        this.thisMonthTests = thisMonthTests;
    }

    public Long getLastMonthTests() {
        return lastMonthTests;
    }

    public void setLastMonthTests(Long lastMonthTests) {
        this.lastMonthTests = lastMonthTests;
    }

    public Long getThisYearTests() {
        return thisYearTests;
    }

    public void setThisYearTests(Long thisYearTests) {
        this.thisYearTests = thisYearTests;
    }

    
    
}
