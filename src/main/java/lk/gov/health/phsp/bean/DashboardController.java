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
package lk.gov.health.phsp.bean;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.TemporalType;
import lk.gov.health.phsp.ejb.CovidDataHolder;
import lk.gov.health.phsp.entity.ClientEncounterComponentItem;
import lk.gov.health.phsp.entity.Encounter;
import lk.gov.health.phsp.entity.Item;
import lk.gov.health.phsp.entity.Numbers;
import lk.gov.health.phsp.enums.EncounterType;
import lk.gov.health.phsp.facade.EncounterFacade;
import lk.gov.health.phsp.facade.NumbersFacade;
import lk.gov.health.phsp.pojcs.CovidData;
import lk.gov.health.phsp.pojcs.InstitutionCount;

/**
 *
 * @author buddhika
 */
@Named
@SessionScoped
public class DashboardController implements Serializable {

    @EJB
    private NumbersFacade numbersFacade;
    @EJB
    private EncounterFacade encounterFacade;
    @EJB
    CovidDataHolder covidDataHolder;

    @Inject
    private EncounterController encounterController;
    @Inject
    ItemController itemController;
    @Inject
    DashboardApplicationController dashboardApplicationController;

    private Date fromDate;
    private Date toDate;
    private List<InstitutionCount> ics;

    private List<CovidData> covidDatasForMohs;
    private List<CovidData> covidDatasForAreas;
    private List<CovidData> covidDatasForLabs;
    private List<CovidData> covidDatasForHospitals;
    private List<CovidData> covidDatasForRdhs;
    private List<CovidData> covidDatasForPdhs;
    private List<CovidData> covidDatasForCountry;

    public String toCalculateNumbers() {
        return "/systemAdmin/calculate_numbers";
    }

    /**
     * Creates a new instance of DashboardController
     */
    public DashboardController() {
    }

    public void calculateNumbers() {
        dashboardApplicationController.calculateNumbers(fromDate, toDate);
    }

    public NumbersFacade getNumbersFacade() {
        return numbersFacade;
    }

    public void setNumbersFacade(NumbersFacade numbersFacade) {
        this.numbersFacade = numbersFacade;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public EncounterFacade getEncounterFacade() {
        return encounterFacade;
    }

    public void setEncounterFacade(EncounterFacade encounterFacade) {
        this.encounterFacade = encounterFacade;
    }

    public EncounterController getEncounterController() {
        return encounterController;
    }

    public void setEncounterController(EncounterController encounterController) {
        this.encounterController = encounterController;
    }

    public List<InstitutionCount> getIcs() {
        return ics;
    }

    public void setIcs(List<InstitutionCount> ics) {
        this.ics = ics;
    }

    public List<CovidData> getCovidDatasForMohs() {
        covidDatasForMohs = covidDataHolder.getCovidDatasForMohs();
        return covidDatasForMohs;
    }

    public List<CovidData> getCovidDatasForAreas() {
        return covidDatasForAreas;
    }

    public List<CovidData> getCovidDatasForLabs() {
        return covidDatasForLabs;
    }

    public List<CovidData> getCovidDatasForHospitals() {
        return covidDatasForHospitals;
    }

    public List<CovidData> getCovidDatasForRdhs() {
        return covidDatasForRdhs;
    }

    public List<CovidData> getCovidDatasForPdhs() {
        return covidDatasForPdhs;
    }

    public List<CovidData> getCovidDatasForCountry() {
        return covidDatasForCountry;
    }

}
