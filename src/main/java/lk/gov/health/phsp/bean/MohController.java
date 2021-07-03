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

// <editor-fold defaultstate="collapsed" desc="Import">
import java.io.Serializable;
import lk.gov.health.phsp.entity.Client;
import lk.gov.health.phsp.bean.util.JsfUtil;
import lk.gov.health.phsp.facade.ClientFacade;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import lk.gov.health.phsp.entity.Area;
import lk.gov.health.phsp.entity.Encounter;
import lk.gov.health.phsp.entity.Institution;
import lk.gov.health.phsp.enums.EncounterType;
import lk.gov.health.phsp.facade.EncounterFacade;
import lk.gov.health.phsp.facade.SmsFacade;
import javax.inject.Named;
import lk.gov.health.phsp.entity.Item;
import lk.gov.health.phsp.enums.InstitutionType;
// </editor-fold>

/**
 *
 * @author buddhika
 */
@Named(value = "mohController")
@SessionScoped
public class MohController implements Serializable {
// <editor-fold defaultstate="collapsed" desc="EJBs">

    @EJB
    private ClientFacade clientFacade;
    @EJB
    private EncounterFacade encounterFacade;
    @EJB
    private SmsFacade smsFacade;

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="Controllers">
    @Inject
    ClientApplicationController clientApplicationController;
    @Inject
    ApplicationController applicationController;
    @Inject
    ClientController clientController;
    @Inject
    private AreaApplicationController areaApplicationController;
    @Inject
    SessionController sessionController;
    @Inject
    private InstitutionApplicationController institutionApplicationController;
    @Inject
    private WebUserController webUserController;
    @Inject
    private EncounterController encounterController;
    @Inject
    private ItemController itemController;
    @Inject
    private ItemApplicationController itemApplicationController;
    @Inject
    private InstitutionController institutionController;
    @Inject
    private CommonController commonController;
    @Inject
    private AreaController areaController;
    @Inject
    private UserTransactionController userTransactionController;
    @Inject
    private PreferenceController preferenceController;
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Variables">  
    private Encounter rat;
    private Encounter pcr;
    private Encounter covidCase;
    private Encounter test;
    private Encounter deleting;

    private List<Encounter> tests;
    private Date fromDate;
    private Date toDate;

    private Item orderingCategory;
    private Item result;
    private Item testType;
    private Institution lab;
// </editor-fold>    
// <editor-fold defaultstate="collapsed" desc="Constructors">

    public MohController() {
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Functions">
    public void deleteTest() {
        deleting.setRetired(true);
        deleting.setRetiredAt(new Date());
        deleting.setRetiredBy(webUserController.getLoggedUser());
        encounterFacade.edit(deleting);
    }

    public String toListOfTests(){
        return "/moh/list_of_tests";
    }
    
    public void toDeleteTestFromLastPcrList() {
        deleteTest();
        sessionController.getPcrs().remove(deleting.getId());
    }

    public void toDeleteTestFromLastRatList() {
        deleteTest();
        sessionController.getRats().remove(deleting.getId());
    }

    public String toDeleteTestFromTestList() {
        deleteTest();
        return toTestList();
    }
    
    public String toRatView() {
        if (rat == null) {
            JsfUtil.addErrorMessage("No RAT");
            return "";
        }
        if (rat.getClient() == null) {
            JsfUtil.addErrorMessage("No Client");
            return "";
        }
        return "/moh/rat_view";
    }

    public String toRatOrderView() {
        if (rat == null) {
            JsfUtil.addErrorMessage("No RAT");
            return "";
        }
        if (rat.getClient() == null) {
            JsfUtil.addErrorMessage("No Client");
            return "";
        }
        return "/moh/rat_order_view";
    }

    
    public String toPcrView() {
        if (pcr == null) {
            JsfUtil.addErrorMessage("No RAT");
            return "";
        }
        if (pcr.getClient() == null) {
            JsfUtil.addErrorMessage("No Client");
            return "";
        }
        return "/moh/pcr_view";
    }

    public String toEditTest() {
        if (test == null) {
            JsfUtil.addErrorMessage("No Test");
            return "";
        }
        if (test.getPcrTestType().equals(itemApplicationController.getRat())) {
            rat = test;
            return toRatEdit();
        } else if (test.getPcrTestType().equals(itemApplicationController.getPcr())) {
            pcr = test;
            return toPcrEdit();
        } else {
            //TODO: add to edit test when test type not given
            JsfUtil.addErrorMessage("Not a RAT or PCR.");
            return "";
        }
    }

    public String toRatEdit() {
        if (rat == null) {
            JsfUtil.addErrorMessage("No RAT");
            return "";
        }
        if (!rat.getPcrTestType().equals(itemApplicationController.getRat())) {
            JsfUtil.addErrorMessage("Not a RAT");
            return "";
        }
        return "/moh/rat";
    }
    
    public String toRatOrderEdit() {
        if (rat == null) {
            JsfUtil.addErrorMessage("No RAT");
            return "";
        }
        if (!rat.getPcrTestType().equals(itemApplicationController.getRat())) {
            JsfUtil.addErrorMessage("Not a RAT");
            return "";
        }
        return "/moh/rat_order";
    }

    public String toPcrEdit() {
        if (pcr == null) {
            JsfUtil.addErrorMessage("No PCR");
            return "";
        }
        if (!pcr.getPcrTestType().equals(itemApplicationController.getPcr())) {
            JsfUtil.addErrorMessage("Not a PCR");
            return "";
        }
        return "/moh/pcr";
    }

    public List<Area> completeDistricts(String qry) {
        return areaController.completeDistricts(qry);
    }

    public List<Area> completeMohAreas(String qry) {
        return areaController.completeMoh(qry);
    }

    public List<Item> getCitizenships() {
        return itemApplicationController.getCitizenships();
    }

    public List<Item> getSexes() {
        return itemApplicationController.getSexes();
    }

    public String toAddNewRatWithNewClient() {
        rat = new Encounter();
        Date d = new Date();
        Client c = new Client();
        c.getPerson().setDistrict(webUserController.getLoggedUser().getInstitution().getDistrict());
        c.getPerson().setMohArea(webUserController.getLoggedUser().getInstitution().getMohArea());
        rat.setPcrTestType(itemApplicationController.getRat());
        rat.setPcrOrderingCategory(sessionController.getLastRatOrderingCategory());
        rat.setClient(c);
        rat.setInstitution(webUserController.getLoggedUser().getInstitution());
        rat.setEncounterType(EncounterType.Test_Enrollment);
        rat.setEncounterDate(d);
        rat.setEncounterFrom(d);
        rat.setEncounterMonth(CommonController.getMonth(d));
        rat.setEncounterQuarter(CommonController.getQuarter(d));
        rat.setEncounterYear(CommonController.getYear(d));

        rat.setSampled(true);
        rat.setSampledAt(new Date());
        rat.setSampledBy(webUserController.getLoggedUser());

        rat.setReferalInstitution(webUserController.getLoggedUser().getInstitution());

        rat.setResultConfirmed(Boolean.TRUE);
        rat.setResultConfirmedAt(d);
        rat.setResultConfirmedBy(webUserController.getLoggedUser());

        return "/moh/rat";
    }

    public String toAddNewRatOrderWithNewClient() {
        rat = new Encounter();
        Date d = new Date();
        Client c = new Client();
        c.getPerson().setDistrict(webUserController.getLoggedUser().getInstitution().getDistrict());
        c.getPerson().setMohArea(webUserController.getLoggedUser().getInstitution().getMohArea());
        rat.setPcrTestType(itemApplicationController.getRat());
        rat.setPcrOrderingCategory(sessionController.getLastRatOrderingCategory());
        rat.setClient(c);
        rat.setInstitution(webUserController.getLoggedUser().getInstitution());
        rat.setReferalInstitution(lab);
        rat.setEncounterType(EncounterType.Test_Enrollment);
        rat.setEncounterDate(d);
        rat.setEncounterFrom(d);
        rat.setEncounterMonth(CommonController.getMonth(d));
        rat.setEncounterQuarter(CommonController.getQuarter(d));
        rat.setEncounterYear(CommonController.getYear(d));
        rat.setSampled(true);
        rat.setSampledAt(new Date());
        rat.setSampledBy(webUserController.getLoggedUser());
        return "/moh/rat_order";
    }

    public String toAddNewPcrWithNewClient() {
        pcr = new Encounter();
        Date d = new Date();
        Client c = new Client();
        c.getPerson().setDistrict(webUserController.getLoggedUser().getInstitution().getDistrict());
        c.getPerson().setMohArea(webUserController.getLoggedUser().getInstitution().getMohArea());
        pcr.setPcrTestType(itemApplicationController.getPcr());
        pcr.setPcrOrderingCategory(sessionController.getLastPcrOrdringCategory());
        pcr.setClient(c);
        pcr.setInstitution(webUserController.getLoggedUser().getInstitution());
        pcr.setReferalInstitution(lab);
        pcr.setEncounterType(EncounterType.Test_Enrollment);
        pcr.setEncounterDate(d);
        pcr.setEncounterFrom(d);
        pcr.setEncounterMonth(CommonController.getMonth(d));
        pcr.setEncounterQuarter(CommonController.getQuarter(d));
        pcr.setEncounterYear(CommonController.getYear(d));
        pcr.setSampled(true);
        pcr.setSampledAt(new Date());
        pcr.setSampledBy(webUserController.getLoggedUser());
        return "/moh/pcr";
    }

    public String saveRatAndToNewRat() {
        if (saveRat() == null) {
            return "";
        }
        JsfUtil.addSuccessMessage("Ready to enter a new RAT");
        return toAddNewRatWithNewClient();
    }

    public String saveRatAndToNewRatOrder() {
        if (saveRat() == null) {
            return "";
        }
        JsfUtil.addSuccessMessage("Ready to enter a new RAT Order");
        return toAddNewRatOrderWithNewClient();
    }

    public String saveRatAndToRatView() {
        if (saveRat() != null) {
            return toRatView();
        } else {
            return "";
        }
    }

    public String saveRatAndToRatOrderView() {
        saveRat();
        return toRatView();
    }

    public String savePcrAndToNewPcr() {
        if (savePcr() != null) {
            return toAddNewPcrWithNewClient();
        } else {
            return "";
        }
    }

    public String savePcrAndToPcrView() {
        if (savePcr() != null) {
            return toPcrView();
        } else {
            return "";
        }
    }

    public String saveRat() {
        if (rat == null) {
            JsfUtil.addErrorMessage("No RAT to save");
            return "";
        }
        if (rat.getClient() == null) {
            JsfUtil.addErrorMessage("No Client to save");
            return "";
        }
        Institution createdIns = null;
        if (rat.getClient().getCreateInstitution() == null) {
            if (webUserController.getLoggedUser().getInstitution().getPoiInstitution() != null) {
                createdIns = webUserController.getLoggedUser().getInstitution().getPoiInstitution();
            } else {
                createdIns = webUserController.getLoggedUser().getInstitution();
            }
            rat.getClient().setCreateInstitution(createdIns);
        } else {
            createdIns = rat.getClient().getCreateInstitution();
        }

        if (createdIns == null || createdIns.getPoiNumber() == null || createdIns.getPoiNumber().trim().equals("")) {
            JsfUtil.addErrorMessage("The institution you logged has no POI. Can not generate a PHN.");
            return "";
        }

        if (rat.getClient().getPhn() == null || rat.getClient().getPhn().trim().equals("")) {
            String newPhn = applicationController.createNewPersonalHealthNumberformat(createdIns);

            int count = 0;
            while (clientApplicationController.checkPhnExists(newPhn, null)) {
                newPhn = applicationController.createNewPersonalHealthNumberformat(createdIns);
                count++;
                if (count > 100) {
                    JsfUtil.addErrorMessage("Generating New PHN Failed. Client NOT saved. Please contact System Administrator.");
                    return "";
                }
            }
            rat.getClient().setPhn(newPhn);
        }
        if (rat.getClient().getId() == null) {
            if (clientApplicationController.checkPhnExists(rat.getClient().getPhn(), null)) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (rat.getClient().getPerson().getNic() != null && !rat.getClient().getPerson().getNic().trim().equals("")) {
                if (clientApplicationController.checkNicExists(rat.getClient().getPerson().getNic(), null)) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        } else {
            if (clientApplicationController.checkPhnExists(rat.getClient().getPhn(), rat.getClient())) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (rat.getClient().getPerson().getNic() != null && !rat.getClient().getPerson().getNic().trim().equals("")) {
                if (clientApplicationController.checkNicExists(rat.getClient().getPerson().getNic(), rat.getClient())) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        }
        clientController.saveClient(rat.getClient());
        if (rat.getEncounterNumber() == null
                || rat.getEncounterNumber().trim().equals("")) {
            rat.setEncounterNumber(encounterController.createTestNumber(webUserController.getLoggedUser().getInstitution()));
        }

        encounterController.save(rat);

        sessionController.setLastRatOrderingCategory(rat.getPcrOrderingCategory());
        sessionController.setLastRat(rat);

        sessionController.getRats().put(rat.getId(), rat);

        JsfUtil.addSuccessMessage("Saved.");
        return "/moh/rat_view";
    }

    public String savePcr() {
        if (pcr == null) {
            JsfUtil.addErrorMessage("No pcr to save");
            return "";
        }
        if (pcr.getClient() == null) {
            JsfUtil.addErrorMessage("No Client to save");
            return "";
        }
        Institution createdIns = null;
        if (pcr.getClient().getCreateInstitution() == null) {
            if (webUserController.getLoggedUser().getInstitution().getPoiInstitution() != null) {
                createdIns = webUserController.getLoggedUser().getInstitution().getPoiInstitution();
            } else {
                createdIns = webUserController.getLoggedUser().getInstitution();
            }
            pcr.getClient().setCreateInstitution(createdIns);
        } else {
            createdIns = pcr.getClient().getCreateInstitution();
        }

        if (createdIns == null || createdIns.getPoiNumber() == null || createdIns.getPoiNumber().trim().equals("")) {
            JsfUtil.addErrorMessage("The institution you logged has no POI. Can not generate a PHN.");
            return "";
        }

        if (pcr.getClient().getPhn() == null || pcr.getClient().getPhn().trim().equals("")) {
            String newPhn = applicationController.createNewPersonalHealthNumberformat(createdIns);

            int count = 0;
            while (clientApplicationController.checkPhnExists(newPhn, null)) {
                newPhn = applicationController.createNewPersonalHealthNumberformat(createdIns);
                count++;
                if (count > 100) {
                    JsfUtil.addErrorMessage("Generating New PHN Failed. Client NOT saved. Please contact System Administrator.");
                    return "";
                }
            }
            pcr.getClient().setPhn(newPhn);
        }
        if (pcr.getClient().getId() == null) {
            if (clientApplicationController.checkPhnExists(pcr.getClient().getPhn(), null)) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (pcr.getClient().getPerson().getNic() != null && !pcr.getClient().getPerson().getNic().trim().equals("")) {
                if (clientApplicationController.checkNicExists(pcr.getClient().getPerson().getNic(), null)) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        } else {
            if (clientApplicationController.checkPhnExists(pcr.getClient().getPhn(), pcr.getClient())) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (pcr.getClient().getPerson().getNic() != null && !pcr.getClient().getPerson().getNic().trim().equals("")) {
                if (clientApplicationController.checkNicExists(pcr.getClient().getPerson().getNic(), pcr.getClient())) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        }
        clientController.saveClient(pcr.getClient());
        if (pcr.getEncounterNumber() == null
                || pcr.getEncounterNumber().trim().equals("")) {
            pcr.setEncounterNumber(encounterController.createTestNumber(webUserController.getLoggedUser().getInstitution()));
        }

        encounterController.save(pcr);

        sessionController.setLastPcrOrdringCategory(pcr.getPcrOrderingCategory());
        sessionController.setLastPcr(pcr);
        lab = pcr.getReferalInstitution();
        sessionController.getPcrs().put(pcr.getId(), pcr);

        JsfUtil.addSuccessMessage("PCR Saved.");
        return "/moh/pcr_view";
    }

    public void retrieveLastAddressForRat() {
        if (rat == null || rat.getClient() == null || sessionController.getLastRat() == null || sessionController.getLastRat().getClient() == null) {
            return;
        }
        rat.getClient().getPerson().setAddress(sessionController.getLastRat().getClient().getPerson().getAddress());
    }

    public void retrieveLastAddressForPcr() {
        if (pcr == null || pcr.getClient() == null
                || sessionController.getLastPcr() == null
                || sessionController.getLastPcr().getClient() == null) {
            return;
        }
        pcr.getClient().getPerson().setAddress(sessionController.getLastPcr().getClient().getPerson().getAddress());
    }

    public void retrieveLastPhoneForRat() {
        if (rat == null || rat.getClient() == null || sessionController.getLastRat() == null || sessionController.getLastRat().getClient() == null) {
            return;
        }
        rat.getClient().getPerson().setPhone1(sessionController.getLastRat().getClient().getPerson().getPhone1());
    }

    public void retrieveLastPhoneForPcr() {
        if (pcr == null
                || pcr.getClient() == null
                || sessionController.getLastPcr() == null
                || sessionController.getLastPcr().getClient() == null) {
            return;
        }
        pcr.getClient().getPerson().setPhone1(sessionController.getLastPcr().getClient().getPerson().getPhone1());
    }

    public void retrieveLastGnRat() {
        if (rat == null || rat.getClient() == null || sessionController.getLastRat() == null || sessionController.getLastRat().getClient() == null) {
            return;
        }
        rat.getClient().getPerson().setGnArea(sessionController.getLastRat().getClient().getPerson().getGnArea());
    }

    public void retrieveLastGnPcr() {
        if (pcr == null
                || pcr.getClient() == null
                || sessionController.getLastPcr() == null
                || sessionController.getLastPcr().getClient() == null) {
            return;
        }
        pcr.getClient().getPerson().setGnArea(sessionController.getLastPcr().getClient().getPerson().getGnArea());
    }

    public List<Area> completePhiAreasForRat(String qry) {
        List<Area> areas = new ArrayList<>();
        if (rat == null) {
            return areas;
        }
        if (rat.getClient() == null) {
            return areas;
        }
        if (rat.getClient().getPerson().getMohArea() == null) {
            return areaApplicationController.completePhiAreas(qry);
        } else {
            return areaApplicationController.completePhiAreasOfMoh(qry, rat.getClient().getPerson().getMohArea());
        }
    }

    public List<Area> completePhiAreasForPcr(String qry) {
        List<Area> areas = new ArrayList<>();
        if (pcr == null) {
            return areas;
        }
        if (pcr.getClient() == null) {
            return areas;
        }
        if (pcr.getClient().getPerson().getMohArea() == null) {
            return areaApplicationController.completePhiAreas(qry);
        } else {
            return areaApplicationController.completePhiAreasOfMoh(qry, pcr.getClient().getPerson().getMohArea());
        }
    }

    public List<Area> completeGnAreasForRat(String qry) {
        return completeGnAreas(qry, rat);
    }

    public List<Area> completeGnAreasForPcr(String qry) {
        return completeGnAreas(qry, pcr);
    }

    private List<Area> completeGnAreas(String qry, Encounter e) {
        List<Area> areas = new ArrayList<>();
        if (e == null) {
            return areas;
        }
        if (e.getClient() == null) {
            return areas;
        }
        if (e.getClient().getPerson().getDistrict() == null) {
            return areaApplicationController.completeGnAreas(qry);
        } else {
            return areaApplicationController.completeGnAreasOfDistrict(qry, e.getClient().getPerson().getDistrict());
        }
    }

    public String toTestList() {
        Map m = new HashMap();
        String j = "select c from Encounter c "
                + " where c.retired=false";
        j += " and c.institution =:ins ";
        m.put("ins", webUserController.getLoggedUser().getInstitution());

        if (testType != null) {
            j += " and c.pcrTestType=:tt ";
            m.put("tt", testType);
        }
        if (orderingCategory != null) {
            j += " and c.pcrOrderingCategory=:oc ";
            m.put("oc", orderingCategory);
        }
        if (result != null) {
            j += " and c.pcrResult=:tt ";
            m.put("result", result);
        }
        if (lab != null) {
            j += " and c.referalInstitution=:ri ";
            m.put("ri", lab);
        }

        j += " and c.encounterDate between :fd and :td "
                + " and c.encounterType=:t "
                + " order by c.id";
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("t", EncounterType.Test_Enrollment);
        tests = encounterFacade.findByJpql(j, m);
        return "/moh/list_of_tests";
    }
    
    public List<Item> getCovidTestOrderingCategories() {
        return itemApplicationController.getCovidTestOrderingCategories();
    }

    public List<Item> getCovidTestTypes() {
        return itemApplicationController.getCovidTestTypes();
    }
    
    public List<Item> getResultTypes() {
        return itemApplicationController.getPcrResults();
    }

    public List<Institution> completeLab(String qry) {
        List<InstitutionType> its = new ArrayList<>();
        its.add(InstitutionType.Lab);
        return institutionController.fillInstitutions(its, qry, null);
    }

// </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="Getters & Setters">  
// </editor-fold>  
    public Encounter getRat() {
        return rat;
    }

    public void setRat(Encounter rat) {
        this.rat = rat;
    }

    public Encounter getPcr() {
        return pcr;
    }

    public void setPcr(Encounter pcr) {
        this.pcr = pcr;
    }

    public Encounter getCovidCase() {
        return covidCase;
    }

    public void setCovidCase(Encounter covidCase) {
        this.covidCase = covidCase;
    }

    public List<Encounter> getTests() {
        return tests;
    }

    public void setTests(List<Encounter> tests) {
        this.tests = tests;
    }

    public Date getFromDate() {
        if (fromDate == null) {
            fromDate = CommonController.startOfTheDate();
        }
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        if (toDate == null) {
            toDate = commonController.endOfTheDay();
        }
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Encounter getTest() {
        return test;
    }

    public void setTest(Encounter test) {
        this.test = test;
    }

    public Item getOrderingCategory() {
        return orderingCategory;
    }

    public void setOrderingCategory(Item orderingCategory) {
        this.orderingCategory = orderingCategory;
    }

    public Item getResult() {
        return result;
    }

    public void setResult(Item result) {
        this.result = result;
    }

    public Item getTestType() {
        return testType;
    }

    public void setTestType(Item testType) {
        this.testType = testType;
    }

    public Institution getLab() {
        return lab;
    }

    public void setLab(Institution lab) {
        this.lab = lab;
    }

    public Encounter getDeleting() {
        return deleting;
    }

    public void setDeleting(Encounter deleting) {
        this.deleting = deleting;
    }

}
