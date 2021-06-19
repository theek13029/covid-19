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
import lk.gov.health.phsp.entity.ClientEncounterComponentItem;
import lk.gov.health.phsp.entity.Encounter;
import lk.gov.health.phsp.entity.Item;
import lk.gov.health.phsp.entity.Numbers;
import lk.gov.health.phsp.enums.EncounterType;
import lk.gov.health.phsp.facade.EncounterFacade;
import lk.gov.health.phsp.facade.NumbersFacade;
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

    @Inject
    private EncounterController encounterController;
    @Inject
    ItemController itemController;

    private Date fromDate;
    private Date toDate;
    private List<InstitutionCount> ics;
    List<Encounter> encounters;
    List<Numbers> numbersList;

    public String toCalculateNumbers() {
        return "/systemAdmin/calculate_numbers";
    }

    public void calculateNumbers() {
        numbersList = new ArrayList<>();
        calculateNumbersByOrderedTest();
        calculateCasesAndTestNumbers();
    }

    public void calculateNumbersByOrderedTest() {
        ClientEncounterComponentItem i = new ClientEncounterComponentItem();
        i.getItemEncounter();
        i.getItem();
        i.getItemValue();
        List<Item> items = new ArrayList<>();
        items.add(itemController.findItemByCode("test_type"));
        items.add(itemController.findItemByCode("covid_19_test_ordering_context_category"));
        Item pcr = itemController.findItemByCode("covid19_pcr_test");
        Item rat = itemController.findItemByCode("covid19_rat");

        ics = new ArrayList<>();
        String j = "select  new lk.gov.health.phsp.pojcs.InstitutionCount(count(e), e.institution, e.encounterDate, e.encounterType, i.item, i.itemValue)  "
                + " from  ClientEncounterComponentItem i join i.itemEncounter e"
                + " where e.retired=false "
                + " and e.encounterDate between :fd and :td "
                + " and i.item in :items "
                + " and e.encounterType=:test"
                + " group by e.encounterDate, e.institution, e.encounterType, i.item, i.itemValue";
        Map m = new HashMap();
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("items", items);
        m.put("test", EncounterType.Test_Enrollment);
        List<Object> os = encounterFacade.findObjectByJpql(j, m, TemporalType.DATE);
        for (Object o : os) {
            if (o instanceof InstitutionCount) {
                InstitutionCount ic = (InstitutionCount) o;
                String name = "";

                if (ic.getEncounerType() == null) {
                    continue;
                }
                switch (ic.getEncounerType()) {
                    case Test_Enrollment:
                        if (ic.getItemValue() == null) {
                            continue;
                        }
                        if (ic.getItemValue().equals(rat)) {
                            name = "rat orders";
                        } else if (ic.getItemValue().equals(pcr)) {
                            name = "pcr orders";
                        } else {
                            name = "";
                        }
                        break;
                    default:
                        name = "";
                }

                if (!name.trim().equals("")) {
                    Numbers n = new Numbers();
                    n.setInstitution(ic.getInstitution());
                    n.setNumberDate(ic.getDate());
                    n.setName(name);
                    n.setCount(ic.getCount());
                    Numbers nn = save(n);
                    if (nn != null) {
                        numbersList.add(nn);
                    }
                }
            }
        }
    }

    public void calculateCasesAndTestNumbers() {
        String j = "select  new lk.gov.health.phsp.pojcs.InstitutionCount(count(e), e.institution, e.encounterDate, e.encounterType)  "
                + " from Encounter e "
                + " where e.retired=false "
                + " and e.encounterDate between :fd and :td "
                + " group by e.encounterDate, e.institution, e.encounterType";
        Map m = new HashMap();
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        List<Object> os = encounterFacade.findObjectByJpql(j, m, TemporalType.DATE);
        for (Object o : os) {
            if (o instanceof InstitutionCount) {
                InstitutionCount ic = (InstitutionCount) o;
                String name = "";
                switch (ic.getEncounerType()) {
                    case Case_Enrollment:
                        name = "cases";
                        break;
                    case Death:
                        name = "deaths";
                        break;
                    case Test_Enrollment:
                        name = "test orders";
                        break;
                    default:
                        name = "";
                }

                if (!name.trim().equals("")) {
                    Numbers n = new Numbers();
                    n.setInstitution(ic.getInstitution());
                    n.setNumberDate(ic.getDate());
                    n.setName(name);
                    n.setCount(ic.getCount());
                    Numbers nn = save(n);
                    if (nn != null) {
                        numbersList.add(nn);
                    }
                }
            }
        }
    }

    /**
     * Creates a new instance of DashboardController
     */
    public DashboardController() {
    }

    public NumbersFacade getNumbersFacade() {
        return numbersFacade;
    }

    public Numbers save(Numbers numbers) {
        if (numbers == null) {
            return null;
        }
        if (numbers.getId() != null) {
            numbersFacade.edit(numbers);
            return numbers;
        }
        Numbers databaseNumbers;
        Map m = new HashMap();
        String j = "select n "
                + " from Numbers n "
                + " where n.numberDate=:d "
                + " and n.name=:name ";
        m.put("d", numbers.getNumberDate());
        m.put("name", numbers.getName());
        if (numbers.getInstitution() != null) {
            j += " and n.institution=:ins ";
            m.put("ins", numbers.getInstitution());
        } else if (numbers.getArea() != null) {
            j += " and n.area=:area ";
            m.put("area", numbers.getArea());
        } else {
            return null;
        }
        j += " order by n.id desc";
        databaseNumbers = numbersFacade.findFirstByJpql(j, m);
        if (databaseNumbers == null) {
            numbersFacade.create(numbers);
            return numbers;
        } else {
            databaseNumbers.setCount(numbers.getCount());
            numbersFacade.edit(databaseNumbers);
            return databaseNumbers;
        }
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

    public List<Numbers> getNumbersList() {
        return numbersList;
    }

    public void setNumbersList(List<Numbers> numbersList) {
        this.numbersList = numbersList;
    }

}
