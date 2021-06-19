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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import lk.gov.health.phsp.entity.Area;
import lk.gov.health.phsp.entity.Institution;
import lk.gov.health.phsp.entity.Numbers;
import lk.gov.health.phsp.enums.EncounterType;
import lk.gov.health.phsp.facade.ClientEncounterComponentItemFacade;
import lk.gov.health.phsp.facade.ClientFacade;
import lk.gov.health.phsp.facade.EncounterFacade;
import lk.gov.health.phsp.facade.NumbersFacade;

/**
 *
 * @author buddhika
 */
@Named
@ApplicationScoped
public class DashboardApplicationController {

    @EJB
    ClientFacade clientFacade;
    @EJB
    EncounterFacade encounterFacade;
    @EJB
    ClientEncounterComponentItemFacade clientEncounterComponentItemFacade;
    @EJB
    NumbersFacade numbersFacade;

    private Long totalNumberOfOrdersYesterday;
    private Long totalNumberOfPendingResults;
    private Long totalNumberOfCasesYesterday;
    private Long totalNumberOfCasesToMark;

    /**
     * Creates a new instance of DashboardController
     */
    public DashboardApplicationController() {
    }

    public void prepareSystemDashboard() {

    }

    public Long findTotalNumberOfRegisteredClientsForAdmin() {
        return (countOfRegistedClients(null, null));
    }

    public Long findTotalNumberOfClinicEnrolmentsForAdmin() {
        return (countOfEncounters(null, EncounterType.Death));
    }

    public Long findTotalNumberOfClinicVisitsForAdmin() {
        return (countOfEncounters(null, EncounterType.Test_Enrollment));
    }

   
    
    public Long countOfRegistedClients(Institution ins, Area gn) {
        String j = "select count(c) from Client c "
                + " where c.retired=:ret ";
        Map m = new HashMap();
        m.put("ret", false);
        if (ins != null) {
            j += " and c.createInstitution=:ins ";
            m.put("ins", ins);
        }
        if (gn != null) {
            j += " and c.person.gnArea=:gn ";
            m.put("gn", gn);
        }
        return clientFacade.countByJpql(j, m);
    }

    public Long countOfEncounters(List<Institution> clinics, EncounterType ec) {
        String j = "select count(e) from Encounter e "
                + " where e.retired=:ret "
                + " and e.encounterType=:ec "
                + " and e.createdAt>:d";
        Map m = new HashMap();
        m.put("d", CommonController.startOfTheYear());
        m.put("ec", ec);
        m.put("ret", false);
        if (clinics != null && !clinics.isEmpty()) {
            m.put("ins", clinics);
            j += " and e.institution in :ins ";
        }
        return encounterFacade.findLongByJpql(j, m);
    }

    boolean bypass = true;

    public long findClientCountEncounterComponentItemMatchCount(
            List<Institution> ins,
            Date fromDate,
            Date toDate,
            String itemCode,
            List<String> valueStrings) {

        String j;
        Map m = new HashMap();

        j = "select count(f) "
                + " from ClientEncounterComponentItem f "
                + " where f.retired<>:ret ";
        j += " and f.item.code=:ic ";
        j += " and f.shortTextValue in :ivs";
        m.put("ic", itemCode);
        m.put("ret", true);
        m.put("ivs", valueStrings);
        if (fromDate != null && toDate != null) {
            m.put("fd", fromDate);
            m.put("td", toDate);
            j += " and f.createdAt between :fd and :td ";
        }
        return clientEncounterComponentItemFacade.findLongByJpql(j, m);
    }

    public Long getTotalNumberOfOrdersYesterday() {
        if (totalNumberOfOrdersYesterday == null) {
            prepareSystemDashboard();
        }
        return totalNumberOfOrdersYesterday;
    }

    public void setTotalNumberOfOrdersYesterday(Long totalNumberOfOrdersYesterday) {
        this.totalNumberOfOrdersYesterday = totalNumberOfOrdersYesterday;
    }

    public Long getTotalNumberOfPendingResults() {
        if (totalNumberOfPendingResults == null) {
            prepareSystemDashboard();
        }
        return totalNumberOfPendingResults;
    }

    public void setTotalNumberOfPendingResults(Long totalNumberOfPendingResults) {
        this.totalNumberOfPendingResults = totalNumberOfPendingResults;
    }

    public Long getTotalNumberOfCasesYesterday() {
        if (totalNumberOfCasesYesterday == null) {
            prepareSystemDashboard();
        }
        return totalNumberOfCasesYesterday;
    }

    public void setTotalNumberOfCasesYesterday(Long totalNumberOfCasesYesterday) {
        this.totalNumberOfCasesYesterday = totalNumberOfCasesYesterday;
    }

    public Long getTotalNumberOfCasesToMark() {
        if (totalNumberOfCasesToMark == null) {
            prepareSystemDashboard();
        }
        return totalNumberOfCasesToMark;
    }

    public void setTotalNumberOfCasesToMark(Long totalNumberOfCasesToMark) {
        this.totalNumberOfCasesToMark = totalNumberOfCasesToMark;
    }

}
