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
    private CovidDataHolder covidDataHolder;

    @Inject
    private EncounterController encounterController;
    @Inject
    private ItemController itemController;
    @Inject
    private DashboardApplicationController dashboardApplicationController;
    @Inject
    private WebUserController webUserController;
    @Inject
    private ItemApplicationController itemApplicationController;

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

    private Long samplesToReceive;
    private Long samplesReceived;
    private Long samplesRejected;
    private Long samplesResultEntered;
    private Long samplesResultReviewed;
    private Long samplesResultsConfirmed;
    private Long samplesPositive;

    public void prepareLabDashboard() {
        String j;
        Map m;

        j = "select count(e) "
                + " from Encounter e "
                + " where e.retired=false "
                + " and e.sentToLabAt between :fd and :td "
                + " and e.referalInstitution=:lab";
        m = new HashMap();
        m.put("fd", CommonController.startOfTheDate(fromDate));
        m.put("td", CommonController.endOfTheDate(toDate));
        m.put("lab", webUserController.getLoggedUser().getInstitution());
        samplesToReceive = encounterFacade.countByJpql(j, m, TemporalType.TIMESTAMP);
        
        j = "select count(e) "
                + " from Encounter e "
                + " where e.retired=false "
                + " and e.sampledAt between :fd and :td "
                + " and e.referalInstitution=:lab";
        m = new HashMap();
        m.put("fd", CommonController.startOfTheDate(fromDate));
        m.put("td", CommonController.endOfTheDate(toDate));
        m.put("lab", webUserController.getLoggedUser().getInstitution());
        samplesReceived = encounterFacade.countByJpql(j, m, TemporalType.TIMESTAMP);

        j = "select count(e) "
                + " from Encounter e "
                + " where e.retired=false "
                + " and e.resultEnteredAt between :fd and :td "
                + " and e.referalInstitution=:lab";
        m = new HashMap();
        m.put("fd", CommonController.startOfTheDate(fromDate));
        m.put("td", CommonController.endOfTheDate(toDate));
        m.put("lab", webUserController.getLoggedUser().getInstitution());
        samplesResultEntered = encounterFacade.countByJpql(j, m, TemporalType.TIMESTAMP);

        j = "select count(e) "
                + " from Encounter e "
                + " where e.retired=false "
                + " and e.resultEnteredAt between :fd and :td "
                + " and e.referalInstitution=:lab";
        m = new HashMap();
        m.put("fd", CommonController.startOfTheDate(fromDate));
        m.put("td", CommonController.endOfTheDate(toDate));
        m.put("lab", webUserController.getLoggedUser().getInstitution());
        samplesResultEntered = encounterFacade.countByJpql(j, m, TemporalType.TIMESTAMP);

        j = "select count(e) "
                + " from Encounter e "
                + " where e.retired=false "
                + " and e.resultReviewedAt between :fd and :td "
                + " and e.referalInstitution=:lab";
        m = new HashMap();
        m.put("fd", CommonController.startOfTheDate(fromDate));
        m.put("td", CommonController.endOfTheDate(toDate));
        m.put("lab", webUserController.getLoggedUser().getInstitution());
        samplesResultReviewed = encounterFacade.countByJpql(j, m, TemporalType.TIMESTAMP);

        j = "select count(e) "
                + " from Encounter e "
                + " where e.retired=false "
                + " and e.resultConfirmedAt between :fd and :td "
                + " and e.referalInstitution=:lab";
        m = new HashMap();
        m.put("fd", CommonController.startOfTheDate(fromDate));
        m.put("td", CommonController.endOfTheDate(toDate));
        m.put("lab", webUserController.getLoggedUser().getInstitution());
        samplesResultsConfirmed = encounterFacade.countByJpql(j, m, TemporalType.TIMESTAMP);

        j = "select count(e) "
                + " from Encounter e "
                + " where e.retired=false "
                + " and e.resultConfirmedAt between :fd and :td "
                + " and e.referalInstitution=:lab "
                + " and e.pcrResult=:pos";
        m = new HashMap();
        m.put("fd", CommonController.startOfTheDate(fromDate));
        m.put("td", CommonController.endOfTheDate(toDate));
        m.put("lab", webUserController.getLoggedUser().getInstitution());
        m.put("pos", itemApplicationController.getPcrPositive());
        samplesPositive = encounterFacade.countByJpql(j, m, TemporalType.TIMESTAMP);

    }

    public String toCalculateNumbers() {
        return "/systemAdmin/calculate_numbers";
    }

    /**
     * Creates a new instance of DashboardController
     */
    public DashboardController() {
    }

    public void calculateNumbers() {
        covidDataHolder.calculateNumbers(fromDate, toDate);
    }

    public void updateDashboard() {
        dashboardApplicationController.updateDashboard();
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

    public CovidDataHolder getCovidDataHolder() {
        return covidDataHolder;
    }

    public void setCovidDataHolder(CovidDataHolder covidDataHolder) {
        this.covidDataHolder = covidDataHolder;
    }

    public ItemController getItemController() {
        return itemController;
    }

    public void setItemController(ItemController itemController) {
        this.itemController = itemController;
    }

    public DashboardApplicationController getDashboardApplicationController() {
        return dashboardApplicationController;
    }

    public void setDashboardApplicationController(DashboardApplicationController dashboardApplicationController) {
        this.dashboardApplicationController = dashboardApplicationController;
    }

    public Long getSamplesReceived() {
        return samplesReceived;
    }

    public void setSamplesReceived(Long samplesReceived) {
        this.samplesReceived = samplesReceived;
    }

    public Long getSamplesRejected() {
        return samplesRejected;
    }

    public void setSamplesRejected(Long samplesRejected) {
        this.samplesRejected = samplesRejected;
    }

    public Long getSamplesResultEntered() {
        return samplesResultEntered;
    }

    public void setSamplesResultEntered(Long samplesResultEntered) {
        this.samplesResultEntered = samplesResultEntered;
    }

    public Long getSamplesResultReviewed() {
        return samplesResultReviewed;
    }

    public void setSamplesResultReviewed(Long samplesResultReviewed) {
        this.samplesResultReviewed = samplesResultReviewed;
    }

    public Long getSamplesResultsConfirmed() {
        return samplesResultsConfirmed;
    }

    public void setSamplesResultsConfirmed(Long samplesResultsConfirmed) {
        this.samplesResultsConfirmed = samplesResultsConfirmed;
    }
    
    

    public Long getSamplesPositive() {
        return samplesPositive;
    }

    public void setSamplesPositive(Long samplesPositive) {
        this.samplesPositive = samplesPositive;
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public void setWebUserController(WebUserController webUserController) {
        this.webUserController = webUserController;
    }

    public ItemApplicationController getItemApplicationController() {
        return itemApplicationController;
    }

    public void setItemApplicationController(ItemApplicationController itemApplicationController) {
        this.itemApplicationController = itemApplicationController;
    }

    public Long getSamplesToReceive() {
        return samplesToReceive;
    }

    public void setSamplesToReceive(Long samplesToReceive) {
        this.samplesToReceive = samplesToReceive;
    }

}
