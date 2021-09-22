package lk.gov.health.phsp.bean;

// <editor-fold defaultstate="collapsed" desc="Import">
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import lk.gov.health.phsp.entity.Client;
import lk.gov.health.phsp.bean.util.JsfUtil;
import lk.gov.health.phsp.bean.util.JsfUtil.PersistAction;
import lk.gov.health.phsp.facade.ClientFacade;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.TemporalType;
import lk.gov.health.phsp.entity.Area;
import lk.gov.health.phsp.entity.ClientEncounterComponentFormSet;
import lk.gov.health.phsp.entity.ClientEncounterComponentItem;
import lk.gov.health.phsp.entity.DesignComponentFormSet;
import lk.gov.health.phsp.entity.Encounter;
import lk.gov.health.phsp.entity.Institution;
import lk.gov.health.phsp.entity.Item;
import lk.gov.health.phsp.entity.Person;
import lk.gov.health.phsp.entity.Sms;
import lk.gov.health.phsp.enums.AreaType;
import lk.gov.health.phsp.enums.EncounterType;
import lk.gov.health.phsp.enums.InstitutionType;
import lk.gov.health.phsp.facade.EncounterFacade;
import lk.gov.health.phsp.facade.SmsFacade;
import lk.gov.health.phsp.pojcs.ClientBasicData;
import lk.gov.health.phsp.pojcs.ClientImport;
import lk.gov.health.phsp.pojcs.InstitutionCount;
import lk.gov.health.phsp.pojcs.ReportHolder;
import lk.gov.health.phsp.pojcs.SlNic;
import lk.gov.health.phsp.pojcs.YearMonthDay;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.runners.Parameterized;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.file.UploadedFile;

// </editor-fold>
@Named
@SessionScoped
public class ClientController implements Serializable {

    // <editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private lk.gov.health.phsp.facade.ClientFacade ejbFacade;
    @EJB
    private EncounterFacade encounterFacade;
    @EJB
    private SmsFacade smsFacade;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Controllers">
    @Inject
    ApplicationController applicationController;
    @Inject
    private AreaApplicationController areaApplicationController;
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
    private ClientEncounterComponentFormSetController clientEncounterComponentFormSetController;
    @Inject
    private UserTransactionController userTransactionController;
    @Inject
    private DesignComponentFormSetController designComponentFormSetController;
    @Inject
    private ClientEncounterComponentItemController clientEncounterComponentItemController;
    @Inject
    private PreferenceController preferenceController;

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Variables">
    private List<Client> items = null;
    private List<ClientImport> clientImports;
    private List<ClientImport> clientImportsSelected;
    private List<ClientBasicData> clients = null;
    private List<Client> selectedClients = null;
    private List<ClientBasicData> selectedClientsWithBasicData = null;
    private List<Client> importedClients = null;
    private String serialPrefix;
    private Long serialStart;
    String plateNo;

    private Item lastTestOrderingCategory;
    private Item lastTestPcrOrRat;

    private Encounter lastTest;

    private Area district;

    private String testNoCol;
    private String labNoCol;
    private String nameCol;
    private String ageColumn;
    private String sexCol;
    private String nicCol;
    private String phoneCol;
    private String addressCol;
    private String mohCol;
    private String districtCol;
    private String wardCol;
    private String bhtCol;
    private String resultCol;
    private String ct1Col;
    private String ct2Col;

    private Integer startRow = 1;

    private Item selectedTest;
    private Item orderingCategory;

    private List<ClientBasicData> selectedClientsBasic = null;

    private List<Encounter> institutionCaseEnrollments;
    private Map<Long, Encounter> institutionCaseEnrollmentMap;

    private List<Encounter> institutionTestEnrollments;
    private Map<Long, Encounter> institutionTestEnrollmentMap;

    private List<Encounter> listedToReceive;
    private List<Encounter> listedToEnterResults;
    private List<Encounter> listedToReviewResults;
    private List<Encounter> listedToConfirm;
    private List<Encounter> listedToDispatch;
    private List<Encounter> listedToDivert;
    private List<Encounter> listedToPrint;
    private List<Encounter> testList;
    private List<Encounter> caseList;

    private List<Encounter> listReceived;
    private List<Encounter> listReviewed;
    private List<Encounter> listConfirmed;
    private List<Encounter> listPositives;

    private List<Encounter> selectedToReceive;
    private List<Encounter> selectedToReview;
    private List<Encounter> selectedToConfirm;
    private List<Encounter> selectedToPrint;
    private List<Encounter> selectedToDispatch;
    private List<Encounter> selectedToDivert;

    private Client selected;
    private Long selectedId;
    private Long idFrom;
    private Long idTo;
    private Institution institution;
    private Institution referingInstitution;
    private Institution dispatchingLab;
    private Institution divertingLab;
    private List<Encounter> selectedClientsClinics;
    private List<Encounter> selectedClientEncounters;

//    Search queries
    private String searchingId;
    private String searchingPhn;
    private String searchingPassportNo;
    private String searchingDrivingLicenceNo;
    private String searchingNicNo;
    private String searchingName;
    private String searchingPhoneNumber;
    private String searchingLocalReferanceNo;
    private String searchingSsNumber;
    private String searchingTestNo;
    private String searchingBhtno;
    private BigInteger searchingTestId;
    private String searchingLabNo;

    private String uploadDetails;
    private String errorCode;
    private YearMonthDay yearMonthDay;
    private Institution selectedClinic;
    private int profileTabActiveIndex;
    private Integer numberOfPhnToReserve;
    private boolean goingToCaptureWebCamPhoto;
    private UploadedFile file;
    private Date clinicDate;
    private Date fromDate;
    private Date toDate;

    private Encounter selectedEncounterToMarkTest;
    private Encounter selectedEncounter;

    private Boolean nicExists;
    private Boolean phnExists;
    private Boolean emailExists;
    private Boolean phone1Exists;
    private Boolean passportExists;
    private Boolean dlExists;
    private Boolean localReferanceExists;
    private Boolean ssNumberExists;
    private String dateTimeFormat;
    private String dateFormat;
    private List<String> reservePhnList;
    private int intNo;

    private String bulkPrintReport;

    private List<InstitutionCount> labSummariesToReceive;
    private List<InstitutionCount> labSummariesReceived;
    private List<InstitutionCount> labSummariesEntered;
    private List<InstitutionCount> labSummariesReviewed;
    private List<InstitutionCount> labSummariesConfirmed;
    private List<InstitutionCount> labSummariesPositive;

    private Institution continuedLab;

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public ClientController() {
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Navigation">
    public String toSearchClientByName() {
        userTransactionController.recordTransaction("To Search Client By Name");
        return "/client/search_by_name";
    }

    public String toSearchClientById() {
        userTransactionController.recordTransaction("To Search Client By Id");
        return "/client/search_by_id";
    }

    public void toMarkAllNegative() {
        if (listedToEnterResults == null) {
            JsfUtil.addErrorMessage("Nothing to mark");
            return;
        }
        for (Encounter e : listedToEnterResults) {
            if (e.getPcrResult() == null) {
                e.setPcrResult(itemApplicationController.getPcrNegative());
                e.setPcrResultStr(preferenceController.getPcrNegativeTerm());
                e.setResultEntered(true);
                e.setResultEnteredAt(new Date());
                e.setResultEnteredBy(webUserController.getLoggedUser());
                getEncounterFacade().edit(e);
            }
        }
    }

    @Deprecated
    public String toEnterTestResults() {
        fillTestEnrollmentToMark();
        return "/client/mark_test_enrollments";
    }

    public void markNilReturnForCases() {
        Encounter nilCases = encounterController.getInstitutionTypeEncounter(webUserController.getLoggedInstitution(),
                EncounterType.No_Covid, new Date());
        JsfUtil.addSuccessMessage("Marked");
    }

    public void markNilReturnForTests() {
        Encounter nilCases = encounterController.getInstitutionTypeEncounter(webUserController.getLoggedInstitution(),
                EncounterType.No_test, new Date());
        JsfUtil.addSuccessMessage("Marked");
    }

    public void reverseNilReturnForTests() {
        Encounter nilCases = encounterController.getInstitutionTypeEncounter(webUserController.getLoggedInstitution(),
                EncounterType.No_test, new Date());
        nilCases.setRetired(true);
        nilCases.setRetiredBy(webUserController.getLoggedUser());
        nilCases.setRetiredAt(new Date());
        encounterController.save(nilCases);
        JsfUtil.addSuccessMessage("Reversed");
    }

    public void reverseNilReturnForCases() {
        Encounter nilCases = encounterController.getInstitutionTypeEncounter(webUserController.getLoggedInstitution(),
                EncounterType.No_Covid, new Date());
        nilCases.setRetired(true);
        nilCases.setRetiredBy(webUserController.getLoggedUser());
        nilCases.setRetiredAt(new Date());
        encounterController.save(nilCases);
        JsfUtil.addSuccessMessage("Reversed");
    }

    public String toSearchClientByDetails() {
        return "/client/search_by_details";

    }

    public String toSelectClient() {
        return "/client/select";
    }

    public String toSelectClientBasic() {
        return "/client/select_basic";
    }

    public String toClient() {
        return "/client/client";
    }

    public void markTestAsNotReceived() {
        if (selectedEncounterToMarkTest == null) {
            JsfUtil.addErrorMessage("Nothing to Mark");
            return;
        }
        selectedEncounterToMarkTest.setResultPositive(null);
        selectedEncounterToMarkTest.setResultDate(new Date());
        selectedEncounterToMarkTest.setResultDateTime(new Date());
        encounterFacade.edit(selectedEncounterToMarkTest);
        JsfUtil.addSuccessMessage("Marked as Not Received");
    }

    public void markTestAsPositive() {
        if (selectedEncounterToMarkTest == null) {
            JsfUtil.addErrorMessage("Nothing to Mark");
            return;
        }
        selectedEncounterToMarkTest.setResultPositive(true);
        selectedEncounterToMarkTest.setResultDate(new Date());
        selectedEncounterToMarkTest.setResultDateTime(new Date());
        encounterFacade.edit(selectedEncounterToMarkTest);
//        sendPositiveSms(selectedEncounterToMarkTest);
        JsfUtil.addSuccessMessage("Marked as Positive");
    }

    public void sendPositiveSms(Encounter e) {
        String number = "";
        if (e == null) {
            return;
        }
        if (e.getClient() == null) {
            return;
        }
        if (e.getClient().getPerson().getPhone1() != null && !e.getClient().getPerson().getPhone1().trim().equals("")) {
            number = e.getClient().getPerson().getPhone1().trim();
        } else if (e.getClient().getPerson().getPhone2() != null && !e.getClient().getPerson().getPhone2().trim().equals("")) {
            number = e.getClient().getPerson().getPhone2().trim();
        } else {
            // // System.out.println("No Phone number");
            return;
        }
        String smsType = "COVID-19 Positive SMS";
        Item item = itemController.findItemByCode("test_type");
        ClientEncounterComponentItem ceci = e.getClientEncounterComponentItem(item);
        String smsTemplate;
        if (ceci == null || ceci.getItemValue() == null) {
            smsTemplate = preferenceController.getPositiveSmsTemplate();
        } else if (ceci.getItemValue().equals(itemController.findItemByCode("covid19_pcr_test"))) {
            smsType = "Positive PCR SMS";
            smsTemplate = preferenceController.getPositivePcrSmsTemplate();
        } else if (ceci.getItemValue().equals(itemController.findItemByCode("covid19_rat"))) {
            smsTemplate = preferenceController.getPositiveRatSmsTemplate();
            smsType = "Positive RAT SMS";
        } else {
            smsTemplate = preferenceController.getPositiveSmsTemplate();
        }
        smsTemplate = "The PCR Test of #{name} done on #{reported_date} was Positive for COVID-19. Relevant Officers will contact you. #{institution}";
        Sms s = new Sms();
        s.setEncounter(e);
        s.setCreatedAt(new Date());
        s.setCreater(webUserController.getLoggedUser());
        s.setInstitution(webUserController.getLoggedInstitution());
        s.setReceipientNumber(number);
        smsTemplate = smsTemplate.replace("#{name}", s.getEncounter().getClient().getPerson().getName());
        smsTemplate = smsTemplate.replace("#{institution}", s.getEncounter().getInstitution().getName());
        smsTemplate = smsTemplate.replace("#{sampled_date}", CommonController.dateTimeToString(s.getEncounter().getEncounterDate()));
        smsTemplate = smsTemplate.replace("#{reported_date}", CommonController.dateTimeToString(s.getEncounter().getResultDate()));
        String messageBody = smsTemplate;
        s.setSendingMessage(messageBody);
        s.setSentSuccessfully(false);
        s.setAwaitingSending(true);
        s.setSendingFailed(false);
        s.setSmsType(smsType);
        getSmsFacade().create(s);
    }

    public void sendNegativeSms(Encounter e) {
        String number = "";
        if (e == null) {
            return;
        }
        if (e.getClient() == null) {
            return;
        }
        if (e.getClient().getPerson().getPhone1() != null && !e.getClient().getPerson().getPhone1().trim().equals("")) {
            number = e.getClient().getPerson().getPhone1().trim();
        } else if (e.getClient().getPerson().getPhone2() != null && !e.getClient().getPerson().getPhone2().trim().equals("")) {
            number = e.getClient().getPerson().getPhone2().trim();
        } else {
            // // System.out.println("No Phone number");
            return;
        }
        String smsType = "COVID-19 Negative SMS";
        Item item = itemController.findItemByCode("test_type");
        ClientEncounterComponentItem ceci = e.getClientEncounterComponentItem(item);
        String smsTemplate;
        if (ceci == null || ceci.getItemValue() == null) {
            smsTemplate = preferenceController.getNegativeSmsTemplate();
        } else if (ceci.getItemValue().equals(itemController.findItemByCode("covid19_pcr_test"))) {
            smsType = "Negative PCR SMS";
            smsTemplate = preferenceController.getNegativePcrSmsTemplate();
        } else if (ceci.getItemValue().equals(itemController.findItemByCode("covid19_rat"))) {
            smsTemplate = preferenceController.getNegativeRatSmsTemplate();
            smsType = "Negative RAT SMS";
        } else {
            smsTemplate = preferenceController.getNegativeSmsTemplate();
        }

        Sms s = new Sms();
        s.setEncounter(e);
        s.setCreatedAt(new Date());
        s.setCreater(webUserController.getLoggedUser());
        s.setInstitution(webUserController.getLoggedInstitution());
        s.setReceipientNumber(number);

        // // System.out.println("smsTemplate = " + smsTemplate);
        // // System.out.println("s = " + s);
        // // System.out.println("s.getEncounter() = " + s.getEncounter());
        // // System.out.println("s.getEncounter().getClient() = " + s.getEncounter().getClient());
        smsTemplate = smsTemplate.replace("#{name}", s.getEncounter().getClient().getPerson().getName());
        smsTemplate = smsTemplate.replace("#{institution}", s.getEncounter().getInstitution().getName());
        smsTemplate = smsTemplate.replace("#{sampled_date}", CommonController.dateTimeToString(s.getEncounter().getEncounterDate()));
        smsTemplate = smsTemplate.replace("#{reported_date}", CommonController.dateTimeToString(s.getEncounter().getResultDate()));
        String messageBody = smsTemplate;
        s.setSendingMessage(messageBody);
        s.setSentSuccessfully(false);
        s.setAwaitingSending(true);
        s.setSendingFailed(false);
        s.setSmsType(smsType);
        getSmsFacade().create(s);
    }

    public void markTestAsNegative() {
        if (selectedEncounterToMarkTest == null) {
            JsfUtil.addErrorMessage("Nothing to Mark");
            return;
        }
        selectedEncounterToMarkTest.setResultPositive(false);
        selectedEncounterToMarkTest.setResultDate(new Date());
        selectedEncounterToMarkTest.setResultDateTime(new Date());
        encounterFacade.edit(selectedEncounterToMarkTest);
//        sendNegativeSms(selectedEncounterToMarkTest);
        JsfUtil.addSuccessMessage("Marked as Negative");
    }

    public String toClientProfile() {
        selectedClientEncounters = null;
        userTransactionController.recordTransaction("To Client Profile");
        selectedClientEncounters = encounterController.getItems(selected);
        updateYearDateMonth();
        return "/client/profile";
    }

    public String toClientProfileForCaseEncounter() {
        userTransactionController.recordTransaction("To Client Profile");
        DesignComponentFormSet dfs = designComponentFormSetController.getFirstCaseEnrollmentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No Default Form Set");
            return "";
        }
        ClientEncounterComponentFormSet cefs;
        cefs = clientEncounterComponentFormSetController.findLastFormsetToDataEntry(dfs, selected);
        if (cefs == null) {
            cefs = clientEncounterComponentFormSetController.createNewCaseEnrollmentFormsetToDataEntry(dfs);
        }
        if (cefs == null) {
            JsfUtil.addErrorMessage("No Patient Form Set");
            return "";
        }
        clientEncounterComponentFormSetController.loadOldFormset(cefs);
        updateYearDateMonth();
        return "/client/profile_case_enrollment";
    }

    public String toClientProfileForTestEncounter() {
        selectedClientEncounters = null;
        userTransactionController.recordTransaction("To Client Profile");

        DesignComponentFormSet dfs = designComponentFormSetController.getFirstTestEnrollmentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No Default Form Set");
            return "";
        }
        ClientEncounterComponentFormSet cefs;
        cefs = clientEncounterComponentFormSetController.findLastFormsetToDataEntry(dfs, selected);
        if (cefs == null) {
            cefs = clientEncounterComponentFormSetController.createNewTestEnrollmentFormsetToDataEntry(dfs);
        }
        if (cefs == null) {
            JsfUtil.addErrorMessage("No Patient Form Set");
            return "";
        }
        clientEncounterComponentFormSetController.loadOldFormset(cefs);
        updateYearDateMonth();
        return "/client/profile_test_enrollment";
    }

    public String toClientProfileById() {
        selected = getFacade().find(selectedId);
        if (selected == null) {
            JsfUtil.addErrorMessage("No such client");
            return "";
        }
        selectedClientsClinics = null;
        selectedClientEncounters = null;
        userTransactionController.recordTransaction("To Client Profile");
        return "/client/profile";
    }

    public String toLabReceiveAll() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.referalInstitution=:rins "
                + " and c.sentToLab is not null "
                + " and (c.sampleRejectedAtLab is null or c.sampleRejectedAtLab=:rej) "
                + " and (c.sampleMissing is null or c.sampleMissing=:sm) "
                + " and c.receivedAtLab is null "
                + " group by c.institution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("rej", false);
        m.put("sm", false);
        m.put("rins", referingInstitution);
        // // System.out.println("j = " + j);
        // // System.out.println("m = " + m);
        // // System.out.println("getFromDate() = " + getFromDate());
        // // System.out.println("getToDate() = " + getToDate());
        // // System.out.println("referingInstitution = " + referingInstitution);
        labSummariesToReceive = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        // // System.out.println("obs = " + obs.size());
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesToReceive.add((InstitutionCount) o);
            }
        }
        return "/lab/receive_all";
    }

    public String toLabReceiveAll1() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.referalInstitution=:rins "
                + " and c.sentToLab is not null "
                + " and c.receivedAtLab is null "
                + " group by c.institution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("rins", referingInstitution);
        // // System.out.println("j = " + j);
        // // System.out.println("m = " + m);
        // // System.out.println("getFromDate() = " + getFromDate());
        // // System.out.println("getToDate() = " + getToDate());
        // // System.out.println("referingInstitution = " + referingInstitution);
        labSummariesToReceive = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        // // System.out.println("obs = " + obs.size());
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesToReceive.add((InstitutionCount) o);
            }
        }
        return "/lab/receive_all";
    }

    public String toLabSummarySamplesReceived() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.receivedAtLabAt between :fd and :td "
                + " and c.referalInstitution=:rins "
                + " group by c.institution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", CommonController.startOfTheDate(getFromDate()));
        m.put("td", CommonController.endOfTheDate(getToDate()));
        m.put("rins", referingInstitution);
        labSummariesReceived = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesReceived.add((InstitutionCount) o);
            }
        }
        return "/lab/summary_samples_received";
    }

    public String toLabSummaryResultsEntered() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.resultEnteredAt between :fd and :td "
                + " and c.referalInstitution=:rins "
                + " group by c.institution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", CommonController.startOfTheDate(getFromDate()));
        m.put("td", CommonController.endOfTheDate(getToDate()));
        m.put("rins", referingInstitution);
        labSummariesEntered = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesEntered.add((InstitutionCount) o);
            }
        }
        return "/lab/summary_samples_entered";
    }

    public String toLabSummarySamplesReviewed() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.resultReviewedAt between :fd and :td "
                + " and c.referalInstitution=:rins "
                + " group by c.institution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", CommonController.startOfTheDate(getFromDate()));
        m.put("td", CommonController.endOfTheDate(getToDate()));
        m.put("rins", referingInstitution);
        labSummariesReviewed = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesReviewed.add((InstitutionCount) o);
            }
        }
        return "/lab/summary_samples_reviewed";
    }

    public String toLabSummarySamplesConfirmed() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.resultConfirmedAt between :fd and :td "
                + " and c.referalInstitution=:rins "
                + " group by c.institution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", CommonController.startOfTheDate(getFromDate()));
        m.put("td", CommonController.endOfTheDate(getToDate()));
        m.put("rins", referingInstitution);
        labSummariesConfirmed = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesConfirmed.add((InstitutionCount) o);
            }
        }
        return "/lab/summary_samples_confirmed";
    }

    public String toSummaryByOrderedInstitutionVsLabToReceive() {
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, c.referalInstitution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type ";
        j += " and c.encounterDate between :fd and :td "
                + " and (c.receivedAtLab is null or c.receivedAtLab=:rl ) "
                + " and c.institution.rdhsArea=:rd ";
        j += " and c.sentToLab=:sl ";
        j += " group by c.institution, c.referalInstitution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("rl", false);
        m.put("sl", true);
        m.put("rd", webUserController.getLoggedInstitution().getRdhsArea());
        labSummariesToReceive = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        // // System.out.println("obs = " + obs.size());
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesToReceive.add((InstitutionCount) o);
            }
        }
        return "/moh/summary_lab_vs_ordered_to_receive";
    }

    public String toSummaryByOrderedInstitution() {
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " group by c.institution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        labSummariesToReceive = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        // // System.out.println("obs = " + obs.size());
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesToReceive.add((InstitutionCount) o);
            }
        }
        return "/moh/summary_lab_ordered";
    }

    public String toSummaryByOrderedInstitutionVsLabReceived() {
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, c.referalInstitution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.receivedAtLab is not null "
                + " group by c.institution, c.referalInstitution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        labSummariesToReceive = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        // // System.out.println("obs = " + obs.size());
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesToReceive.add((InstitutionCount) o);
            }
        }
        return "/moh/summary_lab_vs_ordered_received";
    }

    public String toSummaryByOrderedInstitutionVsLabConfirmed() {
        String j = "select new lk.gov.health.phsp.pojcs.InstitutionCount(c.institution, c.referalInstitution, count(c)) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.resultConfirmed is not null "
                + " group by c.institution, c.referalInstitution";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        labSummariesToReceive = new ArrayList<>();
        List<Object> obs = getFacade().findObjectByJpql(j, m, TemporalType.DATE);
        // // System.out.println("obs = " + obs.size());
        for (Object o : obs) {
            if (o instanceof InstitutionCount) {
                labSummariesToReceive.add((InstitutionCount) o);
            }
        }
        return "/moh/summary_lab_vs_ordered_results_available";
    }

    private Long insLabDateCount(Institution ins, Institution lab, Date date) {
        Long c = 0l;
        String j = "select count(c) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.receivedAtLabAt between :fd and :td "
                + " and c.referalInstitution=:rins "
                + " and c.institution=:ins ";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        Date fd = CommonController.startOfTheDate(date);
        Date td = CommonController.endOfTheDate(date);
        m.put("fd", fd);
        m.put("td", td);
        m.put("ins", ins);
        m.put("rins", lab);
        c = getEncounterFacade().findLongByJpql(j, m, TemporalType.TIMESTAMP);
        c++;
        return c;
    }

    private Long labDateCount(Institution lab, Date date) {
        Long c = 0l;
        String j = "select count(c) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.receivedAtLabAt=:edate "
                + " and c.referalInstitution=:rins ";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("edate", date);
        m.put("rins", lab);
        c = getEncounterFacade().findLongByJpql(j, m, TemporalType.DATE);
        c++;
        return c;
    }

    private Long labPeriodCount(Institution lab, Date from, Date to) {
        Long c = 0l;
        String j = "select count(c) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.receivedAtLabAt between :fdate and :tdate "
                + " and c.referalInstitution=:rins ";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fdate", from);
        m.put("tdate", to);
        m.put("rins", lab);
        c = getEncounterFacade().findLongByJpql(j, m, TemporalType.DATE);
        c++;
        return c;
    }

    private Long labCount(Institution lab) {
        Long c = 0l;
        String j = "select count(c) "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.referalInstitution=:rins "
                + " and c.receivedAtLab is not null";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("rins", lab);
        c = getEncounterFacade().findLongByJpql(j, m, TemporalType.DATE);
        c++;
        return c;
    }

    public String labReceiveAll() {
        String labPrefix;
        Long startCount;
        String dateString = CommonController.formatDate("ddMMyy");
        String monthString = CommonController.formatDate("MM/yy");
        String yearString = CommonController.formatDate("yy");
        switch (getPreferenceController().getLabNumberGeneration()) {
            case "InsLabDateCount":
                startCount = insLabDateCount(institution, webUserController.getLoggedInstitution(), new Date());
                labPrefix = institution.getCode()
                        + "/"
                        + webUserController.getLoggedInstitution().getCode()
                        + "/"
                        + dateString
                        + "/";
                break;
            case "InsDateCount":
                startCount = insLabDateCount(institution, webUserController.getLoggedInstitution(), new Date());
                labPrefix = institution.getCode()
                        + "/"
                        + dateString
                        + "/";
                break;
            case "LabDateCount":
                startCount = insLabDateCount(institution, webUserController.getLoggedInstitution(), new Date());
                labPrefix = institution.getCode()
                        + "/"
                        + dateString
                        + "/";
                break;
            case "DateCount":
                startCount = labDateCount(webUserController.getLoggedInstitution(), new Date());
                labPrefix
                        = dateString
                        + "/";
                break;
            case "Count":
                startCount = labCount(webUserController.getLoggedInstitution());
                Long add = 0l;
                try {
                    add = Long.parseLong(preferenceController.getStartingSerialCount());
                } catch (Exception e) {
                    add = 0l;
                }
                startCount += add;
                labPrefix
                        = webUserController.getLoggedInstitution().getCode();
                break;
            case "YearCount":
                startCount = labPeriodCount(webUserController.getLoggedInstitution(),
                        CommonController.startOfTheYear(),
                        CommonController.endOfYear());
                labPrefix
                        = yearString
                        + "/";
                break;
            case "MonthCount":
                startCount = labPeriodCount(webUserController.getLoggedInstitution(),
                        CommonController.startOfTheMonth(),
                        CommonController.endOfTheMonth());
                labPrefix
                        = monthString
                        + "/";
                break;
            case "CustomCount":
                if (serialStart == null) {
                    JsfUtil.addErrorMessage("Need a Starting Number");
                    return "";
                }
                if (serialPrefix == null) {
                    serialPrefix = "";
                }
                startCount = this.serialStart;
                labPrefix = this.serialPrefix;
                break;
            default:
                startCount = 1l;
                labPrefix = "NOTSET/";
        }

        if (startCount == null) {
            startCount = 0l;
        }
        if (labPrefix == null) {
            labPrefix = "";
        }

        String j = "select c "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.referalInstitution=:rins "
                + " and c.institution=:ins "
                + " and c.sentToLab is not null "
                + " and (c.sampleRejectedAtLab is null or c.sampleRejectedAtLab=:rej) "
                + " and (c.sampleMissing is null or c.sampleMissing=:sm) "
                + " and c.receivedAtLab is null "
                + " order by c.encounterNumber";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("rej", false);
        m.put("sm", false);
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("ins", institution);
        m.put("rins", webUserController.getLoggedInstitution());
        labSummariesToReceive = new ArrayList<>();
        List<Encounter> receivingSamplesTmp = encounterFacade.findByJpql(j, m);
        for (Encounter e : receivingSamplesTmp) {
            e.setPlateNumber(plateNo);
            e.setReceivedAtLab(true);
            e.setReceivedAtLabAt(new Date());
            e.setReceivedAtLabBy(webUserController.getLoggedUser());
            e.setLabNumber(labPrefix + startCount);
            startCount++;
            encounterFacade.edit(e);
        }
        serialStart = startCount;
        return toLabReceiveAll();
    }

    public String toLabReceiveSelected() {
        String j = "select c "
                + " from Encounter c "
                + " where c.retired=false "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and (c.sampleRejectedAtLab is null or c.sampleRejectedAtLab=:rej) "
                + " and (c.sampleMissing is null or c.sampleMissing=:sm) "
                + " and c.referalInstitution=:rins "
                + " and c.sentToLab is not null "
                + " and c.receivedAtLab is null";
        Map m = new HashMap();
        m.put("type", EncounterType.Test_Enrollment);
        m.put("rej", false);
        m.put("sm", false);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("rins", webUserController.getLoggedInstitution());
        if (institution != null) {
            j += " and c.institution=:ins ";
            m.put("ins", institution);
        }

        j += " order by c.encounterNumber";
        listedToReceive = encounterFacade.findByJpql(j, m);
        return "/lab/receive_orders";
    }

    public String toLabReports() {
        return "/lab/reports_index";
    }

    public String toLabOrderByReferringInstitution() {
        referingInstitution = webUserController.getLoggedInstitution();
        Map m = new HashMap();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired<>:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td ";
        if (institution != null) {
            j += " and c.institution=:ins ";
            m.put("ins", institution);
        }
        j += " and c.referalInstitution=:rins"
                + " order by c.encounterNumber";
        m.put("ret", true);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("rins", referingInstitution);
        testList = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/lab/order_list";
    }

    public String toLabListReceivedByInstitution() {
        referingInstitution = webUserController.getLoggedInstitution();
        Map m = new HashMap();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired<>:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.referalInstitution=:rins"
                + " order by c.id";
        m.put("ret", true);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("rins", referingInstitution);
        listReceived = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/lab/list_received";
    }

    public String markAllAsReceived() {
        String j = "select c "
                + " from Encounter c "
                + " where c.retired<>:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.institution=:ins "
                + " and c.referalInstitution=:rins"
                + " order by c.id";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("ins", institution);
        m.put("rins", referingInstitution);
        List<Encounter> receiving = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        for (Encounter e : receiving) {
            e.setReceivedAtLab(true);
            e.setReceivedAtLabAt(new Date());
            e.setReceivedAtLabBy(webUserController.getLoggedUser());
            encounterFacade.edit(e);
        }
        return "/lab/order_list";
    }

    public String toLabEnterResults() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired<>:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.referalInstitution=:rins"
                + " and c.receivedAtLab=:rec "
                + " and (c.resultEntered is null or c.resultEntered=:re ) "
                + " and (c.sampleRejectedAtLab is null or c.sampleRejectedAtLab=:rej) "
                + " and (c.sampleMissing is null or c.sampleMissing=:sm) ";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("re", false);
        m.put("rec", true);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("rej", false);
        m.put("sm", false);
        m.put("rins", referingInstitution);
        if (institution != null) {
            m.put("ins", institution);
            j += " and c.institution=:ins ";
        }
        if (plateNo != null && !plateNo.trim().equals("")) {
            m.put("pn", plateNo);
            j += " and c.plateNumber=:pn ";
        }
        j += " order by c.encounterNumber";
        listedToEnterResults = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/lab/enter_results";
    }

    public String toMohEnterResults() {
        institution = webUserController.getLoggedInstitution();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired<>:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.institution=:ins ";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("ins", institution);

        if (referingInstitution != null) {
            j += " and c.referalInstitution=:ri ";
            m.put("ri", referingInstitution);
        }

        if (selectedTest != null) {

        }

        j += " order by c.id";
        listedToEnterResults = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);

        return "/moh/enter_results";
    }

    public String toLabReviewResults() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired<>:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.referalInstitution=:rins"
                + " and c.resultEntered=:rec "
                + " and (c.sampleRejectedAtLab is null or c.sampleRejectedAtLab=:rej) "
                + " and c.resultReviewed is null ";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("rec", true);
        m.put("rej", false);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("rins", referingInstitution);
        if (institution != null) {
            j += " and c.institution=:ins ";
            m.put("ins", institution);
        }
        if (plateNo != null && !plateNo.trim().equals("")) {
            m.put("pn", plateNo);
            j += " and c.plateNumber=:pn ";
        }
        j += " order by c.encounterNumber";
        listedToReviewResults = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/lab/review_results";
    }

    public String toLabEditResults() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired<>:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.referalInstitution=:rins"
                + " and c.resultEntered=:rec "
                + " and (c.sampleRejectedAtLab is null or c.sampleRejectedAtLab=:rej) "
                + " and c.resultReviewed is null ";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("rec", true);
        m.put("rej", false);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("rins", referingInstitution);
        if (institution != null) {
            m.put("ins", institution);
            j += " and c.institution=:ins ";
        }
        if (plateNo != null && !plateNo.trim().equals("")) {
            m.put("pn", plateNo);
            j += " and c.plateNumber=:pn ";
        }
        j += " order by c.encounterNumber";
        listedToReviewResults = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/lab/edit_results";
    }

    public String toDispatchSamples() {
        String j = "select c "
                + " from Encounter c "
                + " where c.retired=:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.institution=:ins "
                + " and c.sentToLab is null "
                + " order by c.id";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("ins", institution);
        listedToDispatch = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/regional/dispatch_samples";
    }

    public String toDispatchSamplesByMohOrHospital() {
        String j = "select c "
                + " from Encounter c "
                + " where (c.retired is null or c.retired=:ret ) "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.institution=:ins "
                + " and (c.sentToLab is null or c.sentToLab=:stl) "
                + " order by c.id";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("stl", false);
        m.put("ins", webUserController.getLoggedInstitution());
        listedToDispatch = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/moh/dispatch_samples";
    }

    public String toDispatchSamplesWithReferringLab() {
        String j = "select c "
                + " from Encounter c "
                + " where c.retired=:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.institution=:ins "
                + " and c.referalInstitution=:rins "
                + " and c.sentToLab is null "
                + " order by c.id";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("ins", institution);
        m.put("rins", referingInstitution);
        listedToDispatch = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/moh/dispatch_samples";
    }

    public String toDivertSamples() {
        String j = "select c "
                + " from Encounter c "
                + " where c.retired=:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.institution=:ins "
                + " and c.referalInstitution=:rins "
                + " and (c.receivedAtLab is null or c.receivedAtLab=:rec) "
                + " order by c.id";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("rec", false);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("ins", institution);
        m.put("rins", referingInstitution);
        listedToDivert = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/moh/divert_samples";
    }

    public String toConfirmResults() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired=:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.referalInstitution=:rins"
                + " and c.resultReviewed=:rec "
                + " and (c.sampleRejectedAtLab is null or c.sampleRejectedAtLab=:rej) "
                + " and (c.resultConfirmed is null or c.resultConfirmed=:rc) ";

        Map m = new HashMap();
        m.put("ret", false);
        m.put("rc", false);
        m.put("rec", true);
        m.put("rej", false);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("rins", referingInstitution);
        if (institution != null) {
            m.put("ins", institution);
            j += " and c.institution=:ins ";
        }
        if (plateNo != null && !plateNo.trim().equals("")) {
            m.put("pn", plateNo);
            j += " and c.plateNumber=:pn ";
        }
        j += " order by c.encounterNumber";
        listedToConfirm = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/lab/confirm_results";
    }

    public String toLabToSelectForPrinting() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired=:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.referalInstitution=:rins"
                + " and c.resultConfirmed is not null "
                + " and (c.sampleRejectedAtLab is null or c.sampleRejectedAtLab=:rej) ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("rej", false);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());

        m.put("rins", referingInstitution);

        if (institution != null) {
            m.put("ins", institution);
            j += " and c.institution=:ins ";
        }
        if (plateNo != null && !plateNo.trim().equals("")) {
            m.put("pn", plateNo);
            j += " and c.plateNumber=:pn ";
        }
        j += " order by c.encounterNumber";
        listedToPrint = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/lab/print_results";
    }

    public String toMohToSelectForPrinting() {
        institution = webUserController.getLoggedInstitution();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired=:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.institution=:ins "
                + " and c.resultConfirmed is not null "
                + " order by c.encounterNumber";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("ins", institution);
        listedToPrint = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/moh/print_results";
    }

    public void saveEncounterResults(Encounter se) {
        System.out.println("saveEncounterResults");
        if (se == null) {
            return;
        }
        String labApprovalSteps = preferenceController.findPreferanceValue("labApprovalSteps", webUserController.getLoggedInstitution());

        switch (labApprovalSteps) {
            case "Entry":
                se.setResultConfirmed(true);
                se.setResultConfirmedAt(new Date());
                se.setResultConfirmedBy(webUserController.getLoggedUser());
            case "EntryConfirm":
                se.setResultReviewed(true);
                se.setResultReviewedAt(new Date());
                se.setResultReviewedBy(webUserController.getLoggedUser());
            case "EntryReviewConfirm":
                se.setResultEntered(true);
                se.setResultEnteredAt(new Date());
                se.setResultEnteredBy(webUserController.getLoggedUser());
                break;
            default:
                se.setResultConfirmed(true);
                se.setResultConfirmedAt(new Date());
                se.setResultConfirmedBy(webUserController.getLoggedUser());
        }

        if (se.getPcrResult() != null) {
            if (se.getPcrResult().equals(itemApplicationController.getPcrPositive())) {
                se.setPcrResultStr(preferenceController.getPcrPositiveTerm());
            } else if (se.getPcrResult().equals(itemApplicationController.getPcrInconclusive())) {
                se.setPcrResultStr(preferenceController.getPcrInconclusiveTerm());
            } else if (se.getPcrResult().equals(itemApplicationController.getPcrInvalid())) {
                se.setPcrResultStr(preferenceController.getPcrInvalidTerm());
            } else if (se.getPcrResult().equals(itemApplicationController.getPcrNegative())) {
                se.setPcrResultStr(preferenceController.getPcrNegativeTerm());
            } else {

            }
        } else {
            se.setPcrResultStr("");
        }
        encounterFacade.edit(se);
    }

    public void saveLabNo(Encounter se) {
        if (se == null) {
            return;
        }
        encounterFacade.edit(se);
    }

    public void saveEncounterResultsAtMohForConfirmedDate(Encounter se) {
        if (se == null) {
            return;
        }
        if (se.getResultConfirmedAt() != null) {
            se.setResultConfirmed(true);
            se.setResultConfirmedBy(webUserController.getLoggedUser());
            se.setResultReviewed(true);
            se.setResultReviewedAt(se.getResultConfirmedAt());
            se.setResultReviewedBy(webUserController.getLoggedUser());
            se.setResultEntered(true);
            se.setResultEnteredAt(se.getResultConfirmedAt());
            se.setResultEnteredBy(webUserController.getLoggedUser());
            se.setResultNoted(true);
            se.setResultNotedAt(se.getResultConfirmedAt());
            se.setResultNotedBy(webUserController.getLoggedUser());
        } else {
            se.setResultConfirmed(true);
            se.setResultConfirmedAt(new Date());
            se.setResultConfirmedBy(webUserController.getLoggedUser());
            se.setResultReviewed(true);
            se.setResultReviewedAt(new Date());
            se.setResultReviewedBy(webUserController.getLoggedUser());
            se.setResultEntered(true);
            se.setResultEnteredAt(new Date());
            se.setResultEnteredBy(webUserController.getLoggedUser());
            se.setResultNoted(true);
            se.setResultNotedAt(new Date());
            se.setResultNotedBy(webUserController.getLoggedUser());

        }
        encounterFacade.edit(se);
    }

    public void saveEncounterResultsAtMoh(Encounter se) {
        if (se == null) {
            return;
        }
        se.setResultConfirmed(true);
        se.setResultConfirmedAt(new Date());
        se.setResultConfirmedBy(webUserController.getLoggedUser());
        se.setResultReviewed(true);
        se.setResultReviewedAt(new Date());
        se.setResultReviewedBy(webUserController.getLoggedUser());
        se.setResultEntered(true);
        se.setResultEnteredAt(new Date());
        se.setResultEnteredBy(webUserController.getLoggedUser());
        se.setResultNoted(true);
        se.setResultNotedAt(new Date());
        se.setResultNotedBy(webUserController.getLoggedUser());
        encounterFacade.edit(se);
    }

    public String confirmSelectedResults() {
        for (Encounter e : selectedToConfirm) {
            e.setResultConfirmed(true);
            e.setResultConfirmedAt(new Date());
            e.setResultConfirmedBy(webUserController.getLoggedUser());
            //TODO : Remove try catch
            try {
                String labReport = generateLabReport(e);
                e.setResultPrintHtml(labReport);
            } catch (Exception ex) {
                // System.out.println("ex = " + ex);
            }
            encounterFacade.edit(e);
        }
        selectedToConfirm = null;
        return toConfirmResults();
    }

    public String dispatchSelectedSamples() {
        if (dispatchingLab == null) {
            JsfUtil.addErrorMessage("Please select a lab to send samples");
            return "";
        }
        for (Encounter e : selectedToDispatch) {
            e.setSentToLab(true);
            e.setSentToLabAt(new Date());
            e.setSentToLabBy(webUserController.getLoggedUser());
            e.setReferalInstitution(dispatchingLab);
            encounterFacade.edit(e);
        }
        selectedToDispatch = null;
        return toDispatchSamples();
    }

    public String dispatchSelectedSamplesAtMohOrHospital() {
        if (dispatchingLab == null) {
            JsfUtil.addErrorMessage("Please select a lab to send samples");
            return "";
        }
        for (Encounter e : selectedToDispatch) {
            e.setSentToLab(true);
            e.setSentToLabAt(new Date());
            e.setSentToLabBy(webUserController.getLoggedUser());
            e.setReferalInstitution(dispatchingLab);
            encounterFacade.edit(e);
        }
        selectedToDispatch = null;
        return toDispatchSamplesByMohOrHospital();
    }

    public String divertSelectedSamples() {
        if (divertingLab == null) {
            JsfUtil.addErrorMessage("Please select a lab to divert samples");
            return "";
        }
        for (Encounter e : selectedToDivert) {
            e.setSentToLab(true);
            e.setSentToLabAt(new Date());
            e.setSentToLabBy(webUserController.getLoggedUser());
            e.setReferalInstitution(divertingLab);
            encounterFacade.edit(e);
        }
        selectedToDivert = null;
        return toDivertSamples();
    }

    public String markSelectedAsReceivedResults() {
        String labPrefix;
        Long startCount;
        String dateString = CommonController.formatDate("ddMMyy");
        String monthString = CommonController.formatDate("MM/yy");
        String yearString = CommonController.formatDate("yy");
        String labNoGen = getPreferenceController().getLabNumberGeneration();
        // System.out.println("labNoGen = " + labNoGen);
        switch (labNoGen) {
            case "InsLabDateCount":
                startCount = insLabDateCount(institution, webUserController.getLoggedInstitution(), new Date());
                labPrefix = institution.getCode()
                        + "/"
                        + webUserController.getLoggedInstitution().getCode()
                        + "/"
                        + dateString
                        + "/";
                break;
            case "InsDateCount":
                startCount = insLabDateCount(institution, webUserController.getLoggedInstitution(), new Date());
                labPrefix = institution.getCode()
                        + "/"
                        + dateString
                        + "/";
                break;
            case "LabDateCount":
                startCount = insLabDateCount(institution, webUserController.getLoggedInstitution(), new Date());
                labPrefix = institution.getCode()
                        + "/"
                        + dateString
                        + "/";
                break;
            case "DateCount":
                startCount = labDateCount(webUserController.getLoggedInstitution(), new Date());
                labPrefix
                        = dateString
                        + "/";
                break;
            case "Count":
                startCount = labCount(webUserController.getLoggedInstitution());
                // System.out.println("startCount = " + startCount);
                Long add = 0l;
                try {
                    add = Long.parseLong(preferenceController.getStartingSerialCount());
                } catch (Exception e) {
                    add = 0l;
                }
                // System.out.println("add = " + add);
                startCount += add;
                // System.out.println("startCount = " + startCount);
                labPrefix
                        = webUserController.getLoggedInstitution().getCode();
                break;
            case "YearCount":
                startCount = labPeriodCount(webUserController.getLoggedInstitution(),
                        CommonController.startOfTheYear(),
                        CommonController.endOfYear());
                labPrefix
                        = yearString
                        + "/";
                break;
            case "MonthCount":
                startCount = labPeriodCount(webUserController.getLoggedInstitution(),
                        CommonController.startOfTheMonth(),
                        CommonController.endOfTheMonth());
                labPrefix
                        = monthString
                        + "/";
                break;
            case "CustomCount":
                if (serialStart == null) {
                    JsfUtil.addErrorMessage("Please add a start number");
                    return "";
                }
                if (serialPrefix == null) {
                    serialPrefix = "";
                }
                startCount = this.serialStart;
                labPrefix = this.serialPrefix;
                break;

            default:
                startCount = 1l;
                labPrefix = "NOTSET/";
        }

        for (Encounter e : selectedToReceive) {
            e.setReceivedAtLab(true);
            e.setReceivedAtLabAt(new Date());
            e.setReceivedAtLabBy(webUserController.getLoggedUser());
            e.setLabNumber(labPrefix + startCount);
            e.setPlateNumber(plateNo);
            startCount++;
            encounterFacade.edit(e);
        }
        selectedToReceive = null;
        return toLabReceiveAll();
    }

    public String markSelectedAsRejectAtReceive() {
        for (Encounter e : selectedToReceive) {
            e.setSampleRejectedAtLab(true);
            e.setSampleRejectedAtLabAt(new Date());
            e.setSampleRejectedAtLabBy(webUserController.getLoggedUser());
            encounterFacade.edit(e);
        }
        selectedToReceive = null;
        return toLabReceiveAll();
    }

    public String markSelectedAsRejectedAtDataEntry(Encounter e) {
        e.setSampleRejectedAtLab(true);
        e.setSampleRejectedAtLabAt(new Date());
        e.setSampleRejectedAtLabBy(webUserController.getLoggedUser());
        encounterFacade.edit(e);
        return toLabEnterResults();
    }

    public String markUnassigned() {
        for (Encounter e : selectedToReceive) {
            e.setReferalInstitution(null);
            e.setSentToLab(false);
            encounterFacade.edit(e);
        }
        selectedToReceive = null;
        return toLabReceiveAll();
    }

    public String markSampleMissing() {
        for (Encounter e : selectedToReceive) {
            e.setReferalInstitution(null);
            e.setSentToLab(false);
            encounterFacade.edit(e);
        }
        selectedToReceive = null;
        return toLabReceiveAll();
    }

    public String toLabPrintSelected() {
        for (Encounter e : selectedToPrint) {
            e.setResultPrinted(true);
            e.setResultPrintedAt(new Date());
            e.setResultPrintedBy(webUserController.getLoggedUser());
            encounterFacade.edit(e);
        }
//        selectedToPrint = null;
        return "/lab/print_preview";
    }

    public String toLabPrintSelectedBulk() {
        for (Encounter e : selectedToPrint) {
            e.setResultPrinted(true);
            e.setResultPrintedAt(new Date());
            e.setResultPrintedBy(webUserController.getLoggedUser());
            encounterFacade.edit(e);
        }
        bulkPrintReport = generateLabReportsBulk(selectedToPrint);
        selectedToPrint = null;
        return "/lab/printing_results_bulk";
    }

    public String toSelectedToEnterResults() {
        for (Encounter e : selectedToPrint) {
            e.setResultEntered(false);
            e.setResultConfirmed(false);
            e.setResultPrinted(false);
            e.setResultReviewed(false);
            encounterFacade.edit(e);
        }
        bulkPrintReport = generateLabReportsBulk(selectedToPrint);
        selectedToPrint = null;
        return "/lab/printing_results_bulk";
    }

    public String toMohPrintSelected() {
        for (Encounter e : selectedToPrint) {
            e.setResultPrinted(true);
            e.setResultPrintedAt(new Date());
            e.setResultPrintedBy(webUserController.getLoggedUser());
            encounterFacade.edit(e);
        }
        return "/moh/printing_results";
    }

    public String generateLabReport(Encounter e) {
        if (e == null) {
            return "No Encounter";
        }
        String html = getPreferenceController().findPreferanceValue("labReportHeader", webUserController.getLoggedInstitution());
        if (html == null || html.trim().equals("")) {
            return "No Report Format";
        }
        //Patient Properties
        html = html.replace("{name}", e.getClient().getPerson().getName());
        e.getClient().getPerson().calAgeFromDob();

        html = html.replace("{age}", e.getClient().getPerson().getAge());
        html = html.replace("{sex}", e.getClient().getPerson().getSex().getName());
        html = html.replace("{address}", e.getClient().getPerson().getAddress());
        if (e.getClient().getPerson().getPhone1() != null) {
            html = html.replace("{phone1}", e.getClient().getPerson().getPhone1());
        } else {
            html = html.replace("{phone1}", "");
        }
        if (e.getClient().getPerson().getPhone2() != null) {
            html = html.replace("{phone2}", e.getClient().getPerson().getPhone2());
        } else {
            html = html.replace("{phone2}", "");
        }
        if (e.getLabNumber() != null) {
            html = html.replace("{lab_no}", e.getLabNumber());
        } else {
            html = html.replace("{lab_no}", "");
        }
        if (e.getEncounterNumber() != null) {
            html = html.replace("{ref_no}", e.getEncounterNumber());
        } else {
            html = html.replace("{ref_no}", "");
        }
        if (e.getClient().getPerson().getGnArea() != null) {
            html = html.replace("{gn}", e.getClient().getPerson().getGnArea().getName());
        } else {
            html = html.replace("{gn}", "");
        }
        if (e.getClient().getPerson().getPhiArea() != null) {
            html = html.replace("{phi}", e.getClient().getPerson().getPhiArea().getName());
        } else {
            html = html.replace("{phi}", "");
        }

        //Institute Properties
        if (e.getInstitution() != null) {
            if (e.getInstitution().getName() != null) {
                html = html.replace("{institute}", e.getInstitution().getName());
                html = html.replace("{institute_name}", e.getInstitution().getName());
            } else {
                html = html.replace("{institute}", "");
                html = html.replace("{institute_name}", "");
            }
            if (e.getInstitution().getAddress() != null) {
                html = html.replace("{institute_address}", e.getInstitution().getAddress());
            } else {
                html = html.replace("{institute_address}", "");
            }
            if (e.getInstitution().getPhone() != null) {
                html = html.replace("{institute_phone}", e.getInstitution().getPhone());
            } else {
                html = html.replace("{institute_phone}", "");
            }
            if (e.getInstitution().getFax() != null) {
                html = html.replace("{institute_fax}", e.getInstitution().getFax());
            } else {
                html = html.replace("{institute_fax}", "");
            }
            if (e.getInstitution().getEmail() != null) {
                html = html.replace("{institute_email}", e.getInstitution().getEmail());
            } else {
                html = html.replace("{institute_email}", "");
            }
        } else {
            html = html.replace("{institute}", "");
            html = html.replace("{institute_name}", "");
            html = html.replace("{institute_address}", "");
            html = html.replace("{institute_phone}", "");
            html = html.replace("{institute_fax}", "");
            html = html.replace("{institute_email}", "");

        }

        //Institute Properties
        if (e.getReferalInstitution() != null) {
            if (e.getReferalInstitution().getName() != null) {
                html = html.replace("{lab}", e.getReferalInstitution().getName());
                html = html.replace("{lab_name}", e.getReferalInstitution().getName());
            } else {
                html = html.replace("{lab}", "");
                html = html.replace("{lab_name}", "");
            }
            if (e.getReferalInstitution().getAddress() != null) {
                html = html.replace("{lab_address}", e.getReferalInstitution().getAddress());
            } else {
                html = html.replace("{lab_address}", "");
            }
            if (e.getReferalInstitution().getPhone() != null) {
                html = html.replace("{lab_phone}", e.getReferalInstitution().getPhone());
            } else {
                html = html.replace("{lab_phone}", "");
            }
            if (e.getReferalInstitution().getFax() != null) {
                html = html.replace("{lab_fax}", e.getReferalInstitution().getFax());
            } else {
                html = html.replace("{lab_fax}", "");
            }
            if (e.getReferalInstitution().getEmail() != null) {
                html = html.replace("{lab_email}", e.getReferalInstitution().getEmail());
            } else {
                html = html.replace("{lab_email}", "");
            }
        } else {
            html = html.replace("{lab}", "");
            html = html.replace("{lab_name}", "");
            html = html.replace("{lab_address}", "");
            html = html.replace("{lab_phone}", "");
            html = html.replace("{lab_fax}", "");
            html = html.replace("{lab_email}", "");
        }

        if (e.getCreatedAt() != null) {
            html = html.replace("{requested_date}", CommonController.dateTimeToString(e.getCreatedAt()));
        } else {
            html = html.replace("{requested_date}", "");
        }

        if (e.getSampledAt() != null) {
            html = html.replace("{sampled_date}", CommonController.dateTimeToString(e.getSampledAt()));
        } else {
            html = html.replace("{sampled_date}", "");
        }

        if (e.getSentToLabAt() != null) {
            html = html.replace("{dispatched_date}", CommonController.dateTimeToString(e.getSentToLabAt()));
        } else {
            html = html.replace("{dispatched_date}", "");
        }

        if (e.getReceivedAtLabAt() != null) {
            html = html.replace("{received_date}", CommonController.dateTimeToString(e.getReceivedAtLabAt()));
        } else {
            html = html.replace("{received_date}", "");
        }

        if (e.getResultEnteredAt() != null) {
            html = html.replace("{entered_date}", CommonController.dateTimeToString(e.getResultEnteredAt()));
        } else {
            html = html.replace("{entered_date}", "");
        }

        if (e.getResultReviewedAt() != null) {
            html = html.replace("{reviewed_date}", CommonController.dateTimeToString(e.getResultReviewedAt()));
        } else {
            html = html.replace("{reviewed_date}", "");
        }

        if (e.getResultConfirmedAt() != null) {
            html = html.replace("{confirmed_date}", CommonController.dateTimeToString(e.getResultConfirmedAt()));
        } else {
            html = html.replace("{confirmed_date}", "");
        }

        if (e.getSampledAt() != null) {
            html = html.replace("{sampled_date}", CommonController.dateTimeToString(e.getResultConfirmedAt()));
        } else {
            html = html.replace("{sampled_date}", "");
        }

        if (e.getResultEnteredBy() != null) {
            html = html.replace("{entered_by}", e.getResultEnteredBy().getPerson().getName());
        } else {
            html = html.replace("{entered_by}", "");
        }

        if (e.getResultReviewedBy() != null) {
            html = html.replace("{reviewed_by}", e.getResultReviewedBy().getPerson().getName());
        } else {
            html = html.replace("{reviewed_by}", "");
        }

        if (e.getResultConfirmedBy() != null) {
            html = html.replace("{approved_by}", e.getResultConfirmedBy().getPerson().getName());
        } else {
            html = html.replace("{approved_by}", "");
        }

        if (e.getPcrTestType().getName() != null) {
            html = html.replace("{test}", e.getPcrTestType().getName());
        } else {
            html = html.replace("{test}", "");
        }

        if (e.getPcrResult() != null) {
            html = html.replace("{pcr_result}", e.getPcrResult().getName());
        } else {
            html = html.replace("{pcr_result}", "");
        }

        if (e.getCtValue() != null) {
            html = html.replace("{pcr_ct1}", e.getCtValue().toString());
        } else {
            html = html.replace("{pcr_ct1}", "");
        }
        if (e.getCtValue2() != null) {
            html = html.replace("{pcr_ct2}", e.getCtValue2().toString());
        } else {
            html = html.replace("{pcr_ct2}", "");
        }
        if (e.getResultComments() != null) {
            html = html.replace("{pcr_comments}", e.getResultComments());
        } else {
            html = html.replace("{pcr_comments}", "");
        }
        if (e.getUnitWard() != null) {
            html = html.replace("{unit_or_ward}", e.getUnitWard());
        } else {
            html = html.replace("{unit_or_ward}", "");
        }
        if (e.getBht() != null) {
            html = html.replace("{bht}", e.getBht());
        } else {
            html = html.replace("{bht}", "");
        }

        if (e.getPcrResult() != null) {
            if (e.getPcrResult().equals(itemApplicationController.getPcrPositive())) {
                html = html.replace("{pcr_result_string}", getPreferenceController().findPreferanceValue("pcrPositiveTerm", webUserController.getLoggedInstitution()));
                html = html.replace("{pcr_comment_string}", getPreferenceController().findPreferanceValue("pcrPositiveComment", webUserController.getLoggedInstitution()));
            } else if (e.getPcrResult().equals(itemApplicationController.getPcrNegative())) {
                html = html.replace("{pcr_result_string}", getPreferenceController().findPreferanceValue("pcrNegativeTerm", webUserController.getLoggedInstitution()));
                html = html.replace("{pcr_comment_string}", getPreferenceController().findPreferanceValue("pcrNegativeComment", webUserController.getLoggedInstitution()));
            } else if (e.getPcrResult().equals(itemApplicationController.getPcrInvalid())) {
                html = html.replace("{pcr_result_string}", getPreferenceController().findPreferanceValue("pcrInvalidTerm", webUserController.getLoggedInstitution()));
                html = html.replace("{pcr_comment_string}", getPreferenceController().findPreferanceValue("pcrInvalidComment", webUserController.getLoggedInstitution()));
            } else if (e.getPcrResult().equals(itemApplicationController.getPcrInconclusive())) {
                html = html.replace("{pcr_result_string}", getPreferenceController().findPreferanceValue("pcrInconclusiveTerm", webUserController.getLoggedInstitution()));
                html = html.replace("{pcr_comment_string}", getPreferenceController().findPreferanceValue("pcrInconclusiveComment", webUserController.getLoggedInstitution()));
            } else {
                html = html.replace("{pcr_result_string}", getPreferenceController().findPreferanceValue(""));
                html = html.replace("{pcr_comment_string}", getPreferenceController().findPreferanceValue(""));
            }
        } else {
            html = html.replace("{pcr_result_string}", getPreferenceController().findPreferanceValue(""));
            html = html.replace("{pcr_comment_string}", getPreferenceController().findPreferanceValue(""));
        }

        return html;
    }

    public String generateLabReportsBulk(List<Encounter> es) {
        Map<String, ReportHolder> rhs = new HashMap<>();
        List<ReportHolder> orhs = new ArrayList<>();
        for (Encounter e : es) {
            ReportHolder temRh = new ReportHolder();
            temRh.setIns(e.getInstitution());
            temRh.setDateOfCollection(e.getSampledAt());
            temRh.setDateOfReceipt(e.getSampleRejectedAtLabAt());
            temRh.setDateOfReport(e.getResultConfirmedAt());
            String tmpId = temRh.getId();
            ReportHolder savedRh = rhs.get(tmpId);
            if (savedRh == null) {
                savedRh = temRh;
            }
            rhs.put(savedRh.getId(), savedRh);
            savedRh.getEncounters().add(e);
        }
        Integer rowsPerPage;
        try {
            rowsPerPage = Integer.parseInt(preferenceController.getNumberOfRowsPerPage());
        } catch (Exception e) {
            rowsPerPage = 10;
        }
        for (ReportHolder r : rhs.values()) {
            if (r.getEncounters().size() > rowsPerPage) {
                int totalPages = (r.getEncounters().size() / rowsPerPage) + 1;
                ReportHolder nr = null;
                int counter = 0;
                int remaing = r.getEncounters().size();
                int pageNo = 0;
                for (Encounter e : r.getEncounters()) {
                    if (counter == 0) {
                        counter = 1;
                        pageNo++;
                        nr = new ReportHolder();
                        nr.setDateOfCollection(e.getSampledAt());
                        nr.setDateOfReceipt(e.getReceivedAtLabAt());
                        nr.setDateOfReport(e.getResultConfirmedAt());
                        nr.setIns(e.getInstitution());
                        nr.setPageNo(pageNo);
                        nr.setTotalPages(totalPages);
                        nr.getEncounters().add(e);
                        remaing--;
                        orhs.add(nr);
                    } else if (counter == rowsPerPage) {
                        pageNo++;
                        nr = new ReportHolder();
                        nr.setDateOfCollection(e.getSampledAt());
                        nr.setDateOfReceipt(e.getReceivedAtLabAt());
                        nr.setDateOfReport(e.getResultConfirmedAt());
                        nr.setIns(e.getInstitution());
                        nr.setPageNo(pageNo);
                        nr.setTotalPages(totalPages);
                        nr.getEncounters().add(e);
                        orhs.add(nr);
                        counter = 1;
                    } else {
                        nr.getEncounters().add(e);
                        remaing--;
                        counter++;
                    }
                }
            } else if (!r.getEncounters().isEmpty()) {
                orhs.add(r);
            }
        }
        String allReportHolders = "";
        for (ReportHolder rh : orhs) {
            allReportHolders += " <div class=\"main-page\">";
            allReportHolders += " <div class=\"sub-page\">";
            allReportHolders += generateLabReportBulk(rh);
            allReportHolders += " </div>  ";
            allReportHolders += " </div>  ";
        }
        return allReportHolders;
    }

    public String generateLabReportBulk(ReportHolder rh) {
        if (rh == null || rh.getEncounters().isEmpty()) {
            return "No Encounters";
        }
        String html = getPreferenceController().findPreferanceValue("labReportBulkHtml", webUserController.getLoggedInstitution());
        if (html == null || html.trim().equals("")) {
            return "No Report Format";
        }

        String tblHtml = "<table id=\"tbl\" >";
        tblHtml += "<tr>";
        tblHtml += "<th rowspan=\"2\">Lab No</th>";
        tblHtml += "<th rowspan=\"2\">Test No</th>";
        tblHtml += "<th rowspan=\"2\">Name</th>";
        tblHtml += "<th rowspan=\"2\">Age</th>";
        tblHtml += "<th rowspan=\"2\">Gender</th>";
        tblHtml += "<th rowspan=\"2\">Unit</th>";
        tblHtml += "<th rowspan=\"2\">Test Result<br/>SARS-CoV-2</th>";
        tblHtml += "<th >CT Values</th>";
        tblHtml += "</tr>";
        tblHtml += "<tr>";
        tblHtml += "<th>Target 1</th>";
        tblHtml += "<th>Target 2</th>";
        tblHtml += "</tr>";

        for (Encounter e : rh.getEncounters()) {
            e.getClient().getPerson().calAgeFromDob();
            String result;
            String ct1 = "";
            String ct2 = "";
            if (e.getCtValue() != null) {
                ct1 = e.getCtValue() + "";
            }
            if (e.getCtValue2() != null) {
                ct2 = e.getCtValue2() + "";
            }
            if (e.getPcrResultStr() != null && !e.getPcrResultStr().trim().equals("")) {
                result = e.getPcrResultStr();
            } else if (e.getPcrResult() != null) {
                result = e.getPcrResult().getName();
            } else {
                result = "";
            }

            if (e.getLabNumber() == null) {
                e.setLabNumber("");
            }
            if (e.getUnitWard() == null) {
                e.setUnitWard("");
            }
            if (e.getEncounterNumber() == null) {
                e.setEncounterNumber("");
            }
            tblHtml += "<tr>";
            tblHtml += "<td>" + e.getLabNumber() + "</td>";
            tblHtml += "<td>" + e.getEncounterNumber() + "</td>";
            tblHtml += "<td>" + e.getClient().getPerson().getName() + "</td>";
            tblHtml += "<td>" + e.getClient().getPerson().getAge() + "</td>";
            tblHtml += "<td>" + e.getClient().getPerson().getSex().getName() + "</td>";
            tblHtml += "<td>" + e.getUnitWard() + "</td>";
            tblHtml += "<td>" + result + "</td>";
            tblHtml += "<td>" + ct1 + "</td>";
            tblHtml += "<td>" + ct2 + "</td>";
            tblHtml += "</tr>";

            html = html.replace("{institute}", rh.getIns().getName());

            if (rh.getDateOfReceipt() != null) {
                html = html.replace("{received_date}", CommonController.dateTimeToString(rh.getDateOfReceipt(), "dd MMMM yyyy"));
            }

            if (rh.getDateOfCollection() != null) {
                html = html.replace("{sampled_date}", CommonController.dateTimeToString(rh.getDateOfCollection(), "dd MMMM yyyy"));
            }

            if (rh.getDateOfReport() != null) {
                html = html.replace("{confirmed_date}", CommonController.dateTimeToString(rh.getDateOfReport(), "dd MMMM yyyy"));
            }
        }
        tblHtml += "</table>";
        html = html.replace("{result_table}", tblHtml);
        return html;
    }

    public void reviewOkForSelectedResults() {
        for (Encounter e : selectedToReview) {
            e.setResultReviewed(true);
            e.setResultReviewedAt(new Date());
            e.setResultReviewedBy(webUserController.getLoggedUser());
            encounterFacade.edit(e);
        }
        selectedToReview = null;
        toLabReviewResults();
    }

    public String toLabOrderByReferringInstitutionToPrintResults() {
        referingInstitution = webUserController.getLoggedInstitution();
        String j = "select c "
                + " from Encounter c "
                + " where c.retired<>:ret "
                + " and c.encounterType=:type "
                + " and c.encounterDate between :fd and :td "
                + " and c.institution=:ins "
                + " and c.referalInstitution=:rins "
                + " and c.resultConfirmed=:con "
                + " order by c.encounterNumber";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("type", EncounterType.Test_Enrollment);
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("con", true);
        m.put("ins", institution);
        m.put("rins", referingInstitution);
        testList = getEncounterFacade().findByJpql(j, m, TemporalType.DATE);
        return "/lab/result_list";
    }

    public String toAddNewClientForCaseEnrollment() {
        setSelected(new Client());
        selected.setRetired(true);
        selected.getPerson().setDistrict(webUserController.getLoggedInstitution().getDistrict());
        selected.getPerson().setMohArea(webUserController.getLoggedInstitution().getMohArea());
        saveClient(selected);
        clearRegisterNewExistsValues();
        selectedClientsClinics = null;
        selectedClientEncounters = null;
        selectedClinic = null;
        yearMonthDay = new YearMonthDay();
        userTransactionController.recordTransaction("to add a new client for case");
        DesignComponentFormSet dfs = designComponentFormSetController.getFirstCaseEnrollmentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No Default Form Set");
            return "";
        }
        ClientEncounterComponentFormSet cefs = clientEncounterComponentFormSetController.createNewCaseEnrollmentFormsetToDataEntry(dfs);
        if (cefs == null) {
            JsfUtil.addErrorMessage("No Patient Form Set");
            return "";
        }
        clientEncounterComponentFormSetController.loadOldFormset(cefs);
        clientEncounterComponentFormSetController.getSelected().getEncounter().setReferalInstitution(continuedLab);
        return "/client/client_case_enrollment";
    }

    public String toSelectedForCaseEnrollment() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return "";
        }
        selected.getPerson().calAgeFromDob();
        clearRegisterNewExistsValues();
        selectedClientsClinics = null;
        selectedClientEncounters = null;
        selectedClinic = null;
        yearMonthDay = new YearMonthDay();
        userTransactionController.recordTransaction("to add a new client for case");
        DesignComponentFormSet dfs = designComponentFormSetController.getFirstCaseEnrollmentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No Default Form Set");
            return "";
        }
        ClientEncounterComponentFormSet cefs = clientEncounterComponentFormSetController.createNewCaseEnrollmentFormsetToDataEntry(dfs);
        if (cefs == null) {
            JsfUtil.addErrorMessage("No Patient Form Set");
            return "";
        }
        clientEncounterComponentFormSetController.loadOldFormset(cefs);

        return "/client/client_case_enrollment";
    }

    public String toNewCaseInvestigationFromTest() {
        if (selectedEncounter == null) {
            JsfUtil.addErrorMessage("No encounter");
            return "";
        }
        Encounter testEncounter = selectedEncounter;
        if (selectedEncounter.getClient() == null) {
            JsfUtil.addErrorMessage("No Client");
            return "";
        }
        setSelected(selectedEncounter.getClient());
        saveClient(selected);
        clearRegisterNewExistsValues();
        selectedClientsClinics = null;
        selectedClientEncounters = null;
        selectedClinic = null;
        yearMonthDay = new YearMonthDay();
        DesignComponentFormSet dfs = designComponentFormSetController.getFirstCaseEnrollmentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No Default Form Set");
            return "";
        }

        ClientEncounterComponentFormSet cefs = clientEncounterComponentFormSetController.createNewCaseInvestigationFromTest(dfs, testEncounter);

        selectedEncounter = cefs.getEncounter();

        if (cefs == null) {
            JsfUtil.addErrorMessage("No Patient Form Set");
            return "";
        }
//        clientEncounterComponentFormSetController.loadOldFormset(cefs);
        if (cefs.getEncounter() != null) {
            testEncounter.setReferenceCase(cefs.getEncounter());
            encounterFacade.edit(testEncounter);
            cefs.getEncounter().setReferenceTest(testEncounter);
            encounterFacade.edit(cefs.getEncounter());
        }
        updateYearDateMonth();
        return "/client/client_case_enrollment";
    }

    public String toViewOrEditCaseEnrollmentFromEncounter() {
        if (selectedEncounter == null) {
            JsfUtil.addErrorMessage("No encounter");
            return "";
        }
        Encounter testEncounter = selectedEncounter;
        if (selectedEncounter.getClient() == null) {
            JsfUtil.addErrorMessage("No Client");
            return "";
        }
        setSelected(selectedEncounter.getClient());
        saveClient(selected);
        clearRegisterNewExistsValues();
        selectedClientsClinics = null;
        selectedClientEncounters = null;
        selectedClinic = null;
        yearMonthDay = new YearMonthDay();

        DesignComponentFormSet dfs = designComponentFormSetController.getFirstCaseEnrollmentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No Default Form Set");
            return "";
        }
        ClientEncounterComponentFormSet cefs = clientEncounterComponentFormSetController.findFormsetFromEncounter(selectedEncounter);
        if (cefs == null) {
            JsfUtil.addErrorMessage("No Patient Form Set");
            return "";
        }
//    clientEncounterComponentFormSetController.loadOldFormset(cefs);
        if (cefs.getEncounter() != null) {
            testEncounter.setReferenceCase(cefs.getEncounter());
            encounterFacade.edit(testEncounter);
            cefs.getEncounter().setReferenceTest(testEncounter);
        }
        updateYearDateMonth();
        return "/client/client_case_enrollment";
    }

    public String toNewTestEnrollmentFromEncounter() {
        if (selectedEncounter == null) {
            JsfUtil.addErrorMessage("No encounter");
            return "";
        }
        if (selectedEncounter.getClient() == null) {
            JsfUtil.addErrorMessage("No Client");
            return "";
        }
        setSelected(selectedEncounter.getClient());
        saveClient(selected);
        clearRegisterNewExistsValues();
        selectedClientsClinics = null;
        selectedClientEncounters = null;
        selectedClinic = null;
        yearMonthDay = new YearMonthDay();
        userTransactionController.recordTransaction("to add a new client for case");
        DesignComponentFormSet dfs = designComponentFormSetController.getFirstTestEnrollmentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No Default Form Set");
            return "";
        }
        ClientEncounterComponentFormSet cefs = clientEncounterComponentFormSetController.createNewTestEnrollmentFormsetToDataEntry(dfs);
        if (cefs == null) {
            JsfUtil.addErrorMessage("No Patient Form Set");
            return "";
        }
        clientEncounterComponentFormSetController.loadOldFormset(cefs);
        updateYearDateMonth();
        return "/client/client_test_enrollment";
    }

    public String toDeleteTestEncounter() {
        if (selectedEncounter == null) {
            JsfUtil.addErrorMessage("No encounter");
            return "";
        }
        if (selectedEncounter.getClient() == null) {
            JsfUtil.addErrorMessage("No Client");
            return "";
        }
        selectedEncounter.setRetired(true);
        selectedEncounter.setRetiredAt(new Date());
        selectedEncounter.setRetiredBy(webUserController.getLoggedUser());
        encounterController.save(selectedEncounter);
        fillTestList();
        selectedEncounter = null;
        return "";
    }

    public String toDeleteCaseEncounter() {
        if (selectedEncounter == null) {
            JsfUtil.addErrorMessage("No encounter");
            return "";
        }
        if (selectedEncounter.getClient() == null) {
            JsfUtil.addErrorMessage("No Client");
            return "";
        }
        selectedEncounter.setRetired(true);
        selectedEncounter.setRetiredAt(new Date());
        selectedEncounter.setRetiredBy(webUserController.getLoggedUser());
        encounterController.save(selectedEncounter);
        fillCaseList();
        selectedEncounter = null;
        return "";
    }

    public void addLastAddress() {
        if (lastTest == null) {
            return;
        }
        if (selected == null) {
            return;
        }
        selected.getPerson().setAddress(lastTest.getClient().getPerson().getAddress());
    }

    public void addLastPhones() {
        if (lastTest == null) {
            return;
        }
        if (selected == null) {
            return;
        }
        selected.getPerson().setPhone1(lastTest.getClient().getPerson().getPhone1());
        selected.getPerson().setPhone2(lastTest.getClient().getPerson().getPhone2());
    }

    public void addLastGn() {
        if (lastTest == null) {
            return;
        }
        if (selected == null) {
            return;
        }
        selected.getPerson().setGnArea(lastTest.getClient().getPerson().getGnArea());
    }

    public void addLastPhi() {
        if (lastTest == null) {
            return;
        }
        if (selected == null) {
            return;
        }
        selected.getPerson().setPhiArea(lastTest.getClient().getPerson().getPhiArea());
    }

    public String toAddNewClientForTestEnrollment() {
        setSelected(new Client());
        selected.getPerson().setDistrict(webUserController.getLoggedInstitution().getDistrict());
        selected.getPerson().setMohArea(webUserController.getLoggedInstitution().getMohArea());

        selected.setRetired(true);
        saveClient(selected);
        clearRegisterNewExistsValues();
        selectedClientsClinics = null;
        selectedClientEncounters = null;
        selectedClinic = null;
        yearMonthDay = new YearMonthDay();
        userTransactionController.recordTransaction("to add a new client for test ordering");
        DesignComponentFormSet dfs = designComponentFormSetController.getFirstTestEnrollmentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No Default Form Set");
            return "";
        }
        ClientEncounterComponentFormSet cefs = clientEncounterComponentFormSetController.createNewTestEnrollmentFormsetToDataEntry(dfs);
        if (cefs == null) {
            JsfUtil.addErrorMessage("No Patient Form Set");
            return "";
        }
        clientEncounterComponentFormSetController.loadOldFormset(cefs);
        clientEncounterComponentFormSetController.getSelected().getEncounter().setReferalInstitution(continuedLab);
        clientEncounterComponentFormSetController.getSelected().getEncounter().setPcrOrderingCategory(lastTestOrderingCategory);
        clientEncounterComponentFormSetController.getSelected().getEncounter().setPcrTestType(lastTestPcrOrRat);
        return "/client/client_test_enrollment";
    }

    public String toSelectedForNewTestEnrollment() {
        if (selected == null) {
            JsfUtil.addErrorMessage("No Patient");
            return "";
        }
        clearRegisterNewExistsValues();
        selectedClientsClinics = null;
        selectedClientEncounters = null;
        selectedClinic = null;
        yearMonthDay = new YearMonthDay();
        userTransactionController.recordTransaction("to new test for selected client");
        DesignComponentFormSet dfs = designComponentFormSetController.getFirstTestEnrollmentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No Default Form Set");
            return "";
        }
        ClientEncounterComponentFormSet cefs = clientEncounterComponentFormSetController.createNewTestEnrollmentFormsetToDataEntry(dfs);
        if (cefs == null) {
            JsfUtil.addErrorMessage("No Patient Form Set");
            return "";
        }
//        clientEncounterComponentFormSetController.loadOldFormset(cefs);
        updateYearDateMonth();
        return "/client/client_test_enrollment";
    }

    public String toFromFromEncounter() {
        if (selectedEncounter == null) {
            JsfUtil.addErrorMessage("Nothing selected");
            return "";
        }

        setSelected(selectedEncounter.getClient());
        selected.getPerson().calAgeFromDob();

        clearRegisterNewExistsValues();
        selectedClientsClinics = null;
        selectedClientEncounters = null;
        selectedClinic = null;
        yearMonthDay = new YearMonthDay();
        userTransactionController.recordTransaction("to From from a Selected Encounter");

        ClientEncounterComponentFormSet cefs = clientEncounterComponentFormSetController.findFormsetFromEncounter(selectedEncounter);

        if (cefs == null) {
            JsfUtil.addErrorMessage("No such formset");
            return "";
        }

        DesignComponentFormSet dfs = cefs.getReferanceDesignComponentFormSet();
        if (dfs == null) {
            JsfUtil.addErrorMessage("No DFS");
            return "";
        }
        clientEncounterComponentFormSetController.loadOldFormset(cefs);
        if (dfs.isCaseEnrollmentForm()) {
            return "/client/client_test_enrollment";
        } else if (dfs.isTestEnrollmentForm()) {
            return "/client/client_test_enrollment";
        } else {
            JsfUtil.addErrorMessage("No Form");
            return "";
        }
    }

    public String toViewCorrectedDuplicates() {
        String j;
        j = "select c"
                + " from Client c "
                + " where c.reservedClient <> true and c.comments is not null";
        items = getFacade().findByJpql(j);
        userTransactionController.recordTransaction("To View Corrected Duplicates");
        return "/systemAdmin/clients_with_corrected_duplicate_phn";
    }

    public String toDetectPhnDuplicates() {
        String j;
        Map m = new HashMap();
        j = "SELECT c.phn "
                + " FROM Client c "
                + " "
                + " GROUP BY c.phn"
                + " HAVING COUNT(c.phn) > 1 ";
        List<String> duplicatedPhnNumbers = getFacade().findString(j);
        items = new ArrayList<>();
        for (String dupPhn : duplicatedPhnNumbers) {
            j = "select c"
                    + " from Client c "
                    + " where c.phn=:phn";
            m = new HashMap();
            m.put("phn", dupPhn);
            List<Client> temClients = getFacade().findByJpql(j, m);
            items.addAll(temClients);
        }
        userTransactionController.recordTransaction("To Detect PHN Duplicates");
        return "/systemAdmin/clients_with_phn_duplication";
    }

    public String correctPhnDuplicates() {
        String j;
        Map m = new HashMap();
        j = "SELECT c.phn "
                + " FROM Client c "
                + " "
                + " GROUP BY c.phn"
                + " HAVING COUNT(c.phn) > 1 ";
        List<String> duplicatedPhnNumbers = getFacade().findString(j, intNo);
        items = new ArrayList<>();
        for (String dupPhn : duplicatedPhnNumbers) {
            // // System.out.println("dupPhn = " + dupPhn);
            j = "select c"
                    + " from Client c "
                    + " where c.phn=:phn";
            m = new HashMap();
            m.put("phn", dupPhn);
            List<Client> temClients = getFacade().findByJpql(j, m);
            int n = 0;
            for (Client c : temClients) {
                if (n == 0) {

                } else {
                    if (c.getPerson().getLocalReferanceNo() == null || c.getPerson().getLocalReferanceNo().trim().equals("")) {
                        c.setComments("Duplicate PHN. Old PHN Stored as Local Ref");
                        // // System.out.println("Duplicate PHN. Old PHN Stored as Local Ref");
                        // // System.out.println("c.getPhn()");
                        c.getPerson().setLocalReferanceNo(c.getPhn());
                        // // System.out.println("c.getPerson().getLocalReferanceNo() = " + c.getPerson().getLocalReferanceNo());
                        c.setPhn(generateNewPhn(c.getCreateInstitution()));
                        // // System.out.println("c.getPhn()");
                    } else if (c.getPerson().getSsNumber() == null || c.getPerson().getSsNumber().trim().equals("")) {
                        c.setComments("Duplicate PHN. Old PHN Stored as SC No");
                        // // System.out.println("Duplicate PHN. Old PHN Stored as SC No");
                        // // System.out.println("c.getPhn()");
                        c.getPerson().setSsNumber(c.getPhn());
                        // // System.out.println("c.getPerson().getSsNumber() = " + c.getPerson().getSsNumber());
                        c.setPhn(generateNewPhn(c.getCreateInstitution()));
                        // // System.out.println("c.getPhn()");
                    } else {
                        // // System.out.println("No Space to Store Old PHN");
                    }
                    getFacade().edit(c);
                }
                n++;
            }
            items.addAll(temClients);
        }
        userTransactionController.recordTransaction("Correct PHN Duplicates");
        return "/systemAdmin/clients_with_corrected_duplicate_phn";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Functions">

//  This will allow a lab user to search for samples with test no
    public String searchByTestNo() {
        if (this.searchingTestNo == null || this.searchingTestNo.length() == 0) {
            JsfUtil.addErrorMessage("Please enter a valid test number");
            return "";
        }

        this.searchingBhtno = null;
        this.searchingLabNo = null;
        this.searchingTestId = null;
        this.searchingName = null;

        Map hashmap = new HashMap<>();
        String jpql = "select e from Encounter e where e.retired=:retired";

        jpql += " and e.referalInstitution=:ref";
        hashmap.put("retired", false);

        hashmap.put("ref", webUserController.getLoggedInstitution());

        jpql += " and e.encounterNumber=:eno";
        hashmap.put("eno", this.searchingTestNo.toUpperCase());

        jpql += " and e.encounterType=:etype";
        hashmap.put("etype", EncounterType.Test_Enrollment);

        jpql += " and e.encounterDate between :fd and :td";
        hashmap.put("fd", fromDate);
        hashmap.put("td", toDate);

        if (this.institution != null) {
            jpql += " and e.institution=:ins";
            hashmap.put("ins", this.institution);
        }

        jpql += " order by e.encounterNumber";

        testList = encounterFacade.findByJpql(jpql, hashmap, TemporalType.DATE);
        System.out.println(testList);
        return "/hospital/search";
     }


//   This will search for a patient according to a BHT number
     public String searchByBhtNo() {
        if (this.searchingBhtno == null || this.searchingBhtno.trim().length() == 0) {
            JsfUtil.addErrorMessage("Please enter a valid BHT number");
            return  "";
        }
        this.searchingLabNo = null;
        this.searchingTestId = null;
        this.searchingTestNo = null;
        this.searchingName = null;

        Map hashmap = new HashMap<>();
        String jpql = "select e from Encounter e where e.retired=:retired";
        hashmap.put("retired", false);

        jpql += " and e.referalInstitution=:ref";
        hashmap.put("ref", webUserController.getLoggedInstitution());

        jpql += " and e.encounterType=:etype";
        hashmap.put("etype", EncounterType.Test_Enrollment);

        if (this.institution != null) {
            jpql += " and e.institution=:ins";
            hashmap.put("ins", this.institution);
        }

        jpql += " and e.encounterDate between :fd and :td";
        hashmap.put("fd", this.fromDate);
        hashmap.put("td", this.toDate);

        jpql += " and lower(e.bht) like :bht";
        hashmap.put("bht", "%" + this.searchingBhtno.toLowerCase() + "%");

        jpql += " order by e.bht";

        testList = encounterFacade.findByJpql(jpql, hashmap, TemporalType.DATE);
        System.out.println(testList);
        return "/hospital/search";
    }

//    This will return a test according to it's ID
    public String searchByTestId() {
        if (this.searchingTestId == null) {
            JsfUtil.addErrorMessage("Please enter a valid test number");
            return  "";
        }

        this.searchingBhtno = null;
        this.searchingLabNo = null;
        this.searchingTestNo = null;
        this.searchingName = null;

        Map hashmap = new HashMap<>();
        String jpql = "select e from Encounter e where e.retired=:retired";
        hashmap.put("retired", false);

        jpql += " and e.referalInstitution=:ref";
        hashmap.put("ref", webUserController.getLoggedInstitution());

        jpql += " and e.encounterType=:etype";
        hashmap.put("etype", EncounterType.Test_Enrollment);

        jpql += " and e.encounterDate between :fd and :td";
        hashmap.put("fd", this.fromDate);
        hashmap.put("td", this.toDate);


        if (this.institution != null) {
            jpql += " and e.institution=:ins";
            hashmap.put("ins", this.institution);
        }

        jpql += " and e.id=:id";
        hashmap.put("id", this.searchingTestId);

        jpql += " order by e.id";

        testList = encounterFacade.findByJpql(jpql, hashmap, TemporalType.DATE);
        System.out.println(testList);
        return "/hospital/search";
    }

//    This function will search for a test under the test tube number
    	public String searchByLabNo() {
	        if (this.searchingLabNo == null || this.searchingLabNo.trim().length() == 0) {
	            JsfUtil.addErrorMessage("Please enter a valid lab number");
                return  "";
            }

            this.searchingBhtno = null;
            this.searchingTestId = null;
            this.searchingTestNo = null;
            this.searchingName = null;

            Map hashmap = new HashMap<>();
            String jpql = "select e from Encounter e where e.retired=:retired";
            hashmap.put("retired", false);

            jpql += " and e.referalInstitution=:ref";
            hashmap.put("ref", webUserController.getLoggedInstitution());

            jpql += " and e.encounterType=:etype";
            hashmap.put("etype", EncounterType.Test_Enrollment);


            if (this.institution != null) {
                jpql += " and e.institution=:ins";
                hashmap.put("ins", this.institution);
            }

            jpql += " and e.encounterDate between :fd and :td";
            hashmap.put("fd", this.fromDate);
            hashmap.put("td", this.toDate);

            jpql += " and lower(e.labNumber) like :labNo";
            hashmap.put("labNo", "%" + this.searchingLabNo.toLowerCase() + "%");

            jpql += " order by e.bht";

            testList = encounterFacade.findByJpql(jpql, hashmap, TemporalType.DATE);
            System.out.println(testList);
            return "/hospital/search";
    }

    public String toUploadOrders() {
        return "/lab/upload_orders";
    }

    public String toUploadResultsForHospitals() {
        return "/hospital/upload_results";
    }

// This will search patient by name
    public String searchByName() {
        if (searchingName == null && searchingName.trim().equals("")) {
            JsfUtil.addErrorMessage("Please enter a name to search");
            return "";
        }

        if (searchingName.length() < 5) {
            JsfUtil.addErrorMessage("Please enter at least 4 characters to serach");
            return "";
        }

        this.searchingBhtno = null;
        this.searchingLabNo = null;
        this.searchingTestId = null;
        this.searchingTestNo = null;

        Map hashmap = new HashMap<>();
        String jpql = "select e from Encounter e where e.retired=:retired";
        hashmap.put("retired", false);

        jpql += " and e.referalInstitution=:ref";
        hashmap.put("ref", webUserController.getLoggedInstitution());

        jpql += " and e.encounterType=:etype";
        hashmap.put("etype", EncounterType.Test_Enrollment);

        jpql += " and e.encounterDate between :fd and :td";
        hashmap.put("fd", this.fromDate);
        hashmap.put("td", this.toDate);

        if (this.institution != null) {
            jpql += " and e.institution=:ins";
            hashmap.put("ins", this.institution);
        }

        jpql += " and lower(e.client.person.name) like :name";
        hashmap.put("name", "%" + searchingName.toLowerCase() + "%");

        jpql += " order by e.id";

        testList = encounterFacade.findByJpql(jpql, hashmap, TemporalType.DATE);
        return "/hospital/search";

    }

    public String importOrdersAndResultsFromExcel() {
        if (file == null) {
            JsfUtil.addErrorMessage("File ?");
            return "";
        }

        district = institution.getDistrict();

        String strTestNo = null;
        String strLabNo = null;
        String strName = null;
        String strSex = null;
        String strPhone = null;
        String strAddress = null;
        String strNic = null;
        String strResult = null;
        Integer testNoColInt;
        Integer labNoColInt;
        Integer ageColInt;
        Integer nameColInt;
        Integer sexColInt;
        Integer nicColInt;
        Integer phoneColInt;
        Integer addressColInt;
        Integer resultColInt;
        Integer mohColInt;
        Integer districtColInt;
        Integer wardColInt;
        Integer bhtColInt;
        Integer ct1ColInt;
        Integer ct2ColInt;
        Item sex = null;
        Item result = null;

        int ageInYears = 0;

        nameColInt = CommonController.excelColFromHeader(nameCol);
        ageColInt = CommonController.excelColFromHeader(ageColumn);
        testNoColInt = CommonController.excelColFromHeader(testNoCol);
        labNoColInt = CommonController.excelColFromHeader(labNoCol);
        sexColInt = CommonController.excelColFromHeader(sexCol);
        phoneColInt = CommonController.excelColFromHeader(phoneCol);
        addressColInt = CommonController.excelColFromHeader(addressCol);
        nicColInt = CommonController.excelColFromHeader(nicCol);
        resultColInt = CommonController.excelColFromHeader(resultCol);
        mohColInt = CommonController.excelColFromHeader(mohCol);
        districtColInt = CommonController.excelColFromHeader(districtCol);
        wardColInt = CommonController.excelColFromHeader(wardCol);
        bhtColInt = CommonController.excelColFromHeader(bhtCol);
        ct2ColInt = CommonController.excelColFromHeader(ct2Col);
        ct1ColInt = CommonController.excelColFromHeader(ct1Col);

        JsfUtil.addSuccessMessage(file.getFileName());
        XSSFWorkbook myWorkBook;
        try {
            JsfUtil.addSuccessMessage(file.getFileName());
            myWorkBook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);
            Iterator<Row> rowIterator = mySheet.iterator();
            Long count = 0l;
            startRow--;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() < startRow) {
                    continue;
                }

                if (testNoColInt != null) {
                    strTestNo = cellValue(row.getCell(testNoColInt));
                }
                if (labNoColInt != null) {
                    strLabNo = cellValue(row.getCell(labNoColInt));
                }
                if (nicColInt != null) {
                    strNic = cellValue(row.getCell(nicColInt));
                }
                Client c = null;
                if (strNic != null && !strNic.trim().equals("") && strNic.trim().length() > 5) {
                    c = getClientByNic(strNic);
                }

                if (c == null) {
                    if (nameColInt != null) {
                        strName = cellValue(row.getCell(nameColInt));
                    }
                    if (sexColInt != null) {
                        strSex = cellValue(row.getCell(sexColInt));
                    }
                    if (phoneColInt != null) {
                        strPhone = cellValue(row.getCell(phoneColInt));
                    }
                    if (strPhone != null) {
                        strPhone = "'" + strPhone;
                    } else {
                        Double dbl = cellValueDouble(row.getCell(phoneColInt));
                        if (dbl != null) {
                            strPhone = "'" + dbl + "";
                        }
                    }
                    if (addressColInt != null) {
                        strAddress = cellValue(row.getCell(addressColInt));
                    }
                    if (nicColInt != null) {
                        strNic = cellValue(row.getCell(nicColInt));
                    }

                    if (ageColInt != null) {
                        ageInYears = cellValueInt(row.getCell(ageColInt));
                    }
                    if (strSex != null && strSex.toLowerCase().contains("f")) {
                        sex = itemApplicationController.getFemale();
                    } else {
                        sex = itemApplicationController.getMale();
                    }
                } else {
                    strName = c.getPerson().getName();
                    strPhone = c.getPerson().getPhone1();
                    strAddress = c.getPerson().getAddress();
                    sex = c.getPerson().getSex();
                    ageInYears = c.getPerson().getAgeYears();
                }

                if (resultColInt != null) {
                    strResult = cellValue(row.getCell(resultColInt));
                }
                if (strResult != null) {
                    if (strResult.toLowerCase().contains("pos")) {
                        result = itemApplicationController.getPcrPositive();
                    } else {
                        result = itemApplicationController.getPcrNegative();
                    }
                }

                Area ptDistrict = null;
                String districtName = null;
                if (districtColInt != null) {
                    districtName = cellValue(row.getCell(districtColInt));
                }
                if (districtName != null) {
                    ptDistrict = areaController.getAreaByCodeIfNotName(districtName, AreaType.District);
                }
                if (ptDistrict == null) {
                    ptDistrict = district;
                }

                Area ptMoh = null;
                String mohName = null;
                if (mohColInt != null) {
                    mohName = cellValue(row.getCell(mohColInt));
                }

                if (mohName != null) {
                    ptMoh = areaController.getAreaByCodeIfNotName(districtName, AreaType.MOH);
                }

                ClientImport ci = new ClientImport();
                ci.setClient(c);
                ci.setName(strName);
                ci.setTestNo(strTestNo);
                ci.setLabNo(strLabNo);
                ci.setAddress(strAddress);
                ci.setAgeInYears(ageInYears);
                ci.setSex(sex);
                ci.setNic(strNic);
                ci.setPhone(strPhone);
                ci.setId(count);
                ci.setResult(result);

                ci.setDistrict(ptDistrict);
                ci.setMoh(ptMoh);

                String ptWard = null;
                if (wardColInt != null) {
                    ptWard = cellValue(row.getCell(wardColInt));

                    if (ptWard != null) {
                        ci.setWardUnit(ptWard);
                    } else {
                        Double dbl = cellValueDouble(row.getCell(wardColInt));
                        if (dbl != null) {
                            ci.setWardUnit("'" + dbl + "");
                        }
                    }
                }

                String ptBht = null;
                if (bhtColInt != null) {
                    ptBht = cellValue(row.getCell(bhtColInt));

                    if (ptBht != null) {
                        ci.setBhtNo(ptBht);
                    } else {
                        Double dbl = cellValueDouble(row.getCell(bhtColInt));
                        if (dbl != null) {
                            ci.setBhtNo("'" + dbl + "");
                        }
                    }
                }

                Double ptCt1 = null;
                if (ct1ColInt != null) {
                    ptCt1 = cellValueDouble(row.getCell(ct1ColInt));
                    if (ptCt1 == null) {
                        String ct = cellValue(row.getCell(ct1ColInt));
                        if (ct != null && !ct.trim().equals("")) {
                            ptCt1 = CommonController.getDoubleValue(ct);
                        }
                    }
                }
                if (ptCt1 != null && ptCt1 < 1) {
                    ptCt1 = null;
                }

                Double ptCt2 = null;
                if (ct2ColInt != null) {
                    ptCt2 = cellValueDouble(row.getCell(ct2ColInt));
                    if (ptCt2 == null) {
                        String ct = cellValue(row.getCell(ct2ColInt));
                        if (ct != null && !ct.trim().equals("")) {
                            ptCt2 = CommonController.getDoubleValue(ct);
                        }
                    }
                }
                if (ptCt2 != null && ptCt2 < 1) {
                    ptCt2 = null;
                }

                if (ptCt1 != null && ptCt1 != 0.0) {
                    ci.setCt1(ptCt1);
                }
                if (ptCt2 != null && ptCt2 != 0.0) {
                    ci.setCt2(ptCt2);
                }

                count++;
                getClientImports().add(ci);

            }
            startRow++;
            return "/hospital/uploaded_results";
        } catch (IOException e) {
            JsfUtil.addErrorMessage(e.getMessage());
            return "";
        }

    }


    public String importOrdersFromExcel() {
        if (file == null) {
            JsfUtil.addErrorMessage("File ?");
            return "";
        }

        district = institution.getDistrict();

        String strTestNo = null;
        String strName = null;
        String strSex = null;
        String strPhone = null;
        String strAddress = null;
        String strNic = null;
        Integer testNoColInt;
        Integer ageColInt;
        Integer nameColInt;
        Integer sexColInt;
        Integer nicColInt;
        Integer phoneColInt;
        Integer addressColInt;
        Integer mohColInt;
        Integer districtColInt;
        Integer wardColInt;
        Integer bhtColInt;
        Item sex = null;

        int ageInYears = 0;

        nameColInt = CommonController.excelColFromHeader(nameCol);
        ageColInt = CommonController.excelColFromHeader(ageColumn);
        testNoColInt = CommonController.excelColFromHeader(testNoCol);
        sexColInt = CommonController.excelColFromHeader(sexCol);
        phoneColInt = CommonController.excelColFromHeader(phoneCol);
        addressColInt = CommonController.excelColFromHeader(addressCol);
        nicColInt = CommonController.excelColFromHeader(nicCol);
        mohColInt = CommonController.excelColFromHeader(mohCol);
        districtColInt = CommonController.excelColFromHeader(districtCol);
        wardColInt = CommonController.excelColFromHeader(wardCol);
        bhtColInt = CommonController.excelColFromHeader(bhtCol);

        JsfUtil.addSuccessMessage(file.getFileName());
        XSSFWorkbook myWorkBook;
        try {
            JsfUtil.addSuccessMessage(file.getFileName());
            myWorkBook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);
            Iterator<Row> rowIterator = mySheet.iterator();
            Long count = 0l;
            startRow--;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() < startRow) {
                    continue;
                }

                if (testNoColInt != null) {
                    strTestNo = cellValue(row.getCell(testNoColInt));
                }
                if (nicColInt != null) {
                    strNic = cellValue(row.getCell(nicColInt));
                }
                Client c = null;
                if (strNic != null && !strNic.trim().equals("") && strNic.trim().length() > 5) {
                    c = getClientByNic(strNic);
                }

                if (c == null) {
                    if (nameColInt != null) {
                        strName = cellValue(row.getCell(nameColInt));
                    }
                    if (sexColInt != null) {
                        strSex = cellValue(row.getCell(sexColInt));
                    }
                    if (phoneColInt != null) {
                        strPhone = cellValue(row.getCell(phoneColInt));
                    }
                    if (strPhone != null) {
                        strPhone = "'" + strPhone;
                    } else {
                        Double dbl = cellValueDouble(row.getCell(phoneColInt));
                        if (dbl != null) {
                            strPhone = "'" + dbl + "";
                        }
                    }
                    if (addressColInt != null) {
                        strAddress = cellValue(row.getCell(addressColInt));
                    }
                    if (nicColInt != null) {
                        strNic = cellValue(row.getCell(nicColInt));
                    }

                    if (ageColInt != null) {
                        ageInYears = cellValueInt(row.getCell(ageColInt));
                    }
                    if (strSex != null && strSex.toLowerCase().contains("f")) {
                        sex = itemApplicationController.getFemale();
                    } else {
                        sex = itemApplicationController.getMale();
                    }
                } else {
                    strName = c.getPerson().getName();
                    strPhone = c.getPerson().getPhone1();
                    strAddress = c.getPerson().getAddress();
                    sex = c.getPerson().getSex();
                    ageInYears = c.getPerson().getAgeYears();
                }
                Area ptDistrict = null;
                String districtName = null;
                if (districtColInt != null) {
                    districtName = cellValue(row.getCell(districtColInt));
                }
                if (districtName != null) {
                    ptDistrict = areaController.getAreaByCodeIfNotName(districtName, AreaType.District);
                }
                if (ptDistrict == null) {
                    ptDistrict = district;
                }

                Area ptMoh = null;
                String mohName = null;
                if (mohColInt != null) {
                    mohName = cellValue(row.getCell(mohColInt));
                }

                if (mohName != null) {
                    ptMoh = areaController.getAreaByCodeIfNotName(districtName, AreaType.MOH);
                }

                ClientImport ci = new ClientImport();
                ci.setClient(c);
                ci.setName(strName);
                ci.setTestNo(strTestNo);
                ci.setAddress(strAddress);
                ci.setAgeInYears(ageInYears);
                ci.setSex(sex);
                ci.setNic(strNic);
                ci.setPhone(strPhone);
                ci.setId(count);

                ci.setDistrict(ptDistrict);
                ci.setMoh(ptMoh);

                String ptWard = null;
                if (wardColInt != null) {
                    ptWard = cellValue(row.getCell(wardColInt));

                    if (ptWard != null) {
                        ci.setWardUnit(ptWard);
                    } else {
                        Double dbl = cellValueDouble(row.getCell(wardColInt));
                        if (dbl != null) {
                            ci.setWardUnit("'" + dbl + "");
                        }
                    }
                }

                String ptBht = null;
                if (bhtColInt != null) {
                    ptBht = cellValue(row.getCell(bhtColInt));

                    if (ptBht != null) {
                        ci.setBhtNo(ptBht);
                    } else {
                        Double dbl = cellValueDouble(row.getCell(bhtColInt));
                        if (dbl != null) {
                            ci.setBhtNo("'" + dbl + "");
                        }
                    }
                }

                count++;
                getClientImports().add(ci);

            }
            startRow++;
            return "/hospital/uploaded_orders";
        } catch (IOException e) {
            JsfUtil.addErrorMessage(e.getMessage());
            return "";
        }

    }


    public String importOrdersFromExcelOld() {
        if (institution == null) {
            JsfUtil.addErrorMessage("Institution ?");
            return "";
        }
        if (file == null) {
            JsfUtil.addErrorMessage("File ?");
            return "";
        }
        if (district == null) {
            district = institution.getDistrict();
        }
        if (district == null) {
            JsfUtil.addErrorMessage("District ?");
            return "";
        }
        String strTestNo;
        String strLabNo;
        String strName;

        String strSex;
        String strPhone;
        String strAddress;
        String strNic;

        int testNoColInt;
        int labNoColInt;
        int ageColInt;
        int nameColInt;
        int sexColInt;
        int nicColInt;
        int phoneColInt;
        int addressColInt;
        Item sex;
        int ageInYears;

        nameColInt = CommonController.excelColFromHeader(nameCol);
        ageColInt = CommonController.excelColFromHeader(ageColumn);
        testNoColInt = CommonController.excelColFromHeader(testNoCol);
        labNoColInt = CommonController.excelColFromHeader(labNoCol);
        sexColInt = CommonController.excelColFromHeader(sexCol);
        phoneColInt = CommonController.excelColFromHeader(phoneCol);
        addressColInt = CommonController.excelColFromHeader(addressCol);
        nicColInt = CommonController.excelColFromHeader(nicCol);

        JsfUtil.addSuccessMessage(file.getFileName());
        XSSFWorkbook myWorkBook;
        try {
            JsfUtil.addSuccessMessage(file.getFileName());
            myWorkBook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);
            Iterator<Row> rowIterator = mySheet.iterator();
            Long count = 0l;
            startRow++;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() < startRow) {
                    continue;
                }
                strTestNo = cellValue(row.getCell(testNoColInt));
                strLabNo = cellValue(row.getCell(labNoColInt));
                strNic = cellValue(row.getCell(nicColInt));
                Client c = null;
                if (strNic != null && !strNic.trim().equals("")) {
                    c = getClientByNic(strNic);
                }

                if (c == null) {
                    strName = cellValue(row.getCell(nameColInt));
                    strSex = cellValue(row.getCell(sexColInt));
                    strPhone = cellValue(row.getCell(phoneColInt));
                    strAddress = cellValue(row.getCell(addressColInt));
                    strNic = cellValue(row.getCell(nicColInt));
                    ageInYears = cellValueInt(row.getCell(ageColInt));
                    if (strSex.toLowerCase().contains("f")) {
                        sex = itemApplicationController.getFemale();
                    } else {
                        sex = itemApplicationController.getMale();
                    }
                } else {
                    strName = c.getPerson().getName();
                    strPhone = c.getPerson().getPhone1();
                    strAddress = c.getPerson().getAddress();
                    sex = c.getPerson().getSex();
                    ageInYears = c.getPerson().getAgeYears();
                }
                ClientImport ci = new ClientImport();
                ci.setClient(c);
                ci.setName(strName);
                ci.setTestNo(strTestNo);
                ci.setLabNo(strLabNo);
                ci.setAddress(strAddress);
                ci.setAgeInYears(ageInYears);
                ci.setSex(sex);
                ci.setNic(strNic);
                ci.setPhone(strPhone);
                ci.setId(count);
                count++;
                getClientImports().add(ci);

            }
            return "/lab/uploaded_orders";
        } catch (IOException e) {
            JsfUtil.addErrorMessage(e.getMessage());
            return "";
        }

    }

    public String saveUploadedOrders() {
        if (getClientImportsSelected().isEmpty()) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return "";
        }
        int count = 0;
        for (ClientImport ci : getClientImportsSelected()) {
            toAddNewPcrWithNewClient(ci);
            count++;
        }
        String msg = "" + count + " Orders Imported.";
        JsfUtil.addSuccessMessage(msg);
        return toUploadOrders();
    }

    public String saveUploadedOrdersPlusResults() {
        if (getClientImportsSelected().isEmpty()) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return "";
        }
        int count = 0;
        for (ClientImport ci : getClientImportsSelected()) {
            toAddNewPcrResultWithNewClient(ci);
            count++;
        }
        String msg = "" + count + " Orders Imported.";
        JsfUtil.addSuccessMessage(msg);
        clientImports = new ArrayList<>();
        clientImportsSelected = new ArrayList<>();
        clearUploadedData();
        return toUploadResultsForHospitals();
    }

    public String clearUploadedData() {
        clientImports = new ArrayList<>();
        clientImportsSelected = new ArrayList<>();
        return toUploadOrders();
    }

    private void toAddNewPcrWithNewClientOld(ClientImport ci) {
        Encounter pcr = new Encounter();
        Client c;
        if (ci.getClient() == null) {
            c = new Client();
            c.getPerson().setDistrict(district);
            c.getPerson().setName(ci.getName());
            c.getPerson().setAddress(ci.getAddress());
            c.getPerson().setPhone1(ci.getPhone());
            c.getPerson().setSex(ci.getSex());
            c.getPerson().setNic(ci.getNic());
            Calendar calDob = Calendar.getInstance();
            calDob.add(Calendar.YEAR, (0 - ci.getAgeInYears()));
            calDob.add(Calendar.MONTH, (0 - ci.getAgeInMonths()));
            calDob.add(Calendar.DAY_OF_YEAR, (0 - ci.getAgeInDays()));
            c.getPerson().setDateOfBirth(calDob.getTime());
        } else {
            c = ci.getClient();
        }

        pcr.setPcrTestType(lastTestPcrOrRat);
        pcr.setClient(c);

        pcr.setInstitution(institution);
        pcr.setCreatedInstitution(webUserController.getLoggedInstitution());
        pcr.setReferalInstitution(webUserController.getLoggedInstitution());

        pcr.setEncounterNumber(ci.getTestNo());
        pcr.setEncounterType(EncounterType.Test_Enrollment);
        pcr.setEncounterDate(fromDate);
        pcr.setEncounterFrom(new Date());
        pcr.setEncounterMonth(CommonController.getMonth(fromDate));
        pcr.setEncounterQuarter(CommonController.getQuarter(fromDate));
        pcr.setEncounterYear(CommonController.getYear(fromDate));

        pcr.setSampled(true);
        pcr.setSampledAt(fromDate);
        pcr.setSampledBy(webUserController.getLoggedUser());
        pcr.setCreatedAt(new Date());

        pcr.setSentToLab(true);
        pcr.setSentToLabAt(toDate);
        pcr.setSentToLabBy(webUserController.getLoggedUser());

        if (c.getId() == null) {
            getFacade().create(c);
        } else {
            getFacade().edit(c);
        }
        encounterFacade.create(pcr);
    }

     private void toAddNewPcrWithNewClient(ClientImport ci) {
        Encounter pcr = new Encounter();
        Client c;
        if (ci.getClient() == null) {
            c = new Client();

            c.getPerson().setName(ci.getName());
            c.getPerson().setAddress(ci.getAddress());
            c.getPerson().setPhone1(ci.getPhone());
            c.getPerson().setSex(ci.getSex());
            c.getPerson().setNic(ci.getNic());
            if (ci.getDistrict() != null) {
                c.getPerson().setDistrict(ci.getDistrict());
            } else {
                c.getPerson().setDistrict(district);
            }
            if (ci.getMoh() != null) {
                c.getPerson().setMohArea(ci.getMoh());
            }
            Calendar calDob = Calendar.getInstance();
            calDob.add(Calendar.YEAR, (0 - ci.getAgeInYears()));
            calDob.add(Calendar.MONTH, (0 - ci.getAgeInMonths()));
            calDob.add(Calendar.DAY_OF_YEAR, (0 - ci.getAgeInDays()));
            c.getPerson().setDateOfBirth(calDob.getTime());
        } else {
            c = ci.getClient();
        }

        pcr.setClient(c);

        pcr.setInstitution(institution);
        pcr.setCreatedInstitution(institution);
        pcr.setReferalInstitution(referingInstitution);
        pcr.setUnitWard(ci.getWardUnit());
        pcr.setBht(ci.getBhtNo());
        pcr.setPcrOrderingCategory(orderingCategory);

        pcr.setEncounterNumber(ci.getTestNo());
        pcr.setLabNumber(ci.getLabNo());
        pcr.setEncounterType(EncounterType.Test_Enrollment);
        pcr.setEncounterDate(fromDate);
        pcr.setEncounterFrom(new Date());
        pcr.setEncounterMonth(CommonController.getMonth(fromDate));
        pcr.setEncounterQuarter(CommonController.getQuarter(fromDate));
        pcr.setEncounterYear(CommonController.getYear(fromDate));

        pcr.setSampled(true);
        pcr.setSampledAt(fromDate);
        pcr.setSampledBy(webUserController.getLoggedUser());
        pcr.setCreatedAt(new Date());

        pcr.setSentToLab(true);
        pcr.setSentToLabAt(fromDate);
        pcr.setSentToLabBy(webUserController.getLoggedUser());

        pcr.setPcrTestType(lastTestPcrOrRat);

        if (c.getId() == null) {
            getFacade().create(c);
        } else {
            getFacade().edit(c);
        }
        encounterFacade.create(pcr);
    }


    private void toAddNewPcrResultWithNewClient(ClientImport ci) {
        Encounter pcr = new Encounter();
        Client c;
        if (ci.getClient() == null) {
            c = new Client();

            c.getPerson().setName(ci.getName());
            c.getPerson().setAddress(ci.getAddress());
            c.getPerson().setPhone1(ci.getPhone());
            c.getPerson().setSex(ci.getSex());
            c.getPerson().setNic(ci.getNic());
            if (ci.getDistrict() != null) {
                c.getPerson().setDistrict(ci.getDistrict());
            } else {
                c.getPerson().setDistrict(district);
            }
            if (ci.getMoh() != null) {
                c.getPerson().setMohArea(ci.getMoh());
            }
            Calendar calDob = Calendar.getInstance();
            calDob.add(Calendar.YEAR, (0 - ci.getAgeInYears()));
            calDob.add(Calendar.MONTH, (0 - ci.getAgeInMonths()));
            calDob.add(Calendar.DAY_OF_YEAR, (0 - ci.getAgeInDays()));
            c.getPerson().setDateOfBirth(calDob.getTime());
        } else {
            c = ci.getClient();
        }

        pcr.setClient(c);

        pcr.setInstitution(institution);
        pcr.setCreatedInstitution(institution);
        pcr.setReferalInstitution(referingInstitution);
        pcr.setUnitWard(ci.getWardUnit());
        pcr.setBht(ci.getBhtNo());
        pcr.setPcrOrderingCategory(orderingCategory);

        pcr.setEncounterNumber(ci.getTestNo());
        pcr.setLabNumber(ci.getLabNo());
        pcr.setEncounterType(EncounterType.Test_Enrollment);
        pcr.setEncounterDate(fromDate);
        pcr.setEncounterFrom(new Date());
        pcr.setEncounterMonth(CommonController.getMonth(fromDate));
        pcr.setEncounterQuarter(CommonController.getQuarter(fromDate));
        pcr.setEncounterYear(CommonController.getYear(fromDate));

        pcr.setSampled(true);
        pcr.setSampledAt(fromDate);
        pcr.setSampledBy(webUserController.getLoggedUser());
        pcr.setCreatedAt(new Date());

        pcr.setSentToLab(true);
        pcr.setSentToLabAt(fromDate);
        pcr.setSentToLabBy(webUserController.getLoggedUser());

        pcr.setReceivedAtLab(true);
        pcr.setReceivedAtLabAt(fromDate);
        pcr.setReceivedAtLabBy(webUserController.getLoggedUser());

        pcr.setResultReviewed(true);
        pcr.setResultReviewedAt(toDate);
        pcr.setResultReviewedBy(webUserController.getLoggedUser());

        pcr.setResultConfirmed(true);
        pcr.setResultConfirmedAt(toDate);
        pcr.setResultConfirmedBy(webUserController.getLoggedUser());

        pcr.setResultDate(toDate);

        pcr.setPcrResult(ci.getResult());
        if (ci.getCt1() != null && ci.getCt1() != 0.0) {
            pcr.setCtValue(ci.getCt1());
        }
        if (ci.getCt2() != null && ci.getCt2() != 0.0) {
            pcr.setCtValue2(ci.getCt2());
        }

        pcr.setPcrTestType(lastTestPcrOrRat);

        if (c.getId() == null) {
            getFacade().create(c);
        } else {
            getFacade().edit(c);
        }
        encounterFacade.create(pcr);
    }


    private String cellValue(Cell cell) {
        String str = "";
        if (cell == null) {
            return str;
        }
        if (cell.getCellType() == null) {
            return str;
        }
        if (null != cell.getCellType()) {
            switch (cell.getCellType()) {
                case STRING:
                    str = cell.getStringCellValue();
                    break;
                case BLANK:
                    break;
                case BOOLEAN:
                    boolean b = cell.getBooleanCellValue();
                    if (b) {
                        str = "true";
                    } else {
                        str = "false";
                    }
                    break;
                case FORMULA:
                    str = cell.getCellFormula();
                    break;
                case NUMERIC:
                    str = cell.getNumericCellValue() + "";
                    break;
                case _NONE:
                    break;
                default:
                    break;
            }
        }
        return str;
    }

    private int cellValueInt(Cell cell) {
        int intNum = 0;
        if (cell == null) {
            return intNum;
        }
        if (cell.getCellType() == null) {
            return intNum;
        }
        if (null != cell.getCellType()) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    Double d = cell.getNumericCellValue();
                    intNum = d.intValue();
                    break;
                default:
                    intNum = 0;
                    break;
            }
        }
        return intNum;
    }

    private Double cellValueDouble(Cell cell) {
        Double dblNum = null;
        if (cell == null) {
            return dblNum;
        }
        if (cell.getCellType() == null) {
            return dblNum;
        }
        if (null != cell.getCellType()) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    Double d = cell.getNumericCellValue();
                    dblNum = d;
                    break;
                default:
                    dblNum = 0.0;
                    break;
            }
        }
        return dblNum;
    }

    public List<Area> completeClientsGnArea(String qry) {
        List<Area> areas = new ArrayList<>();
        if (selected == null) {
            return areas;
        }
        if (selected.getPerson().getDistrict() == null) {
            return areaApplicationController.completeGnAreas(qry);
        } else {
            return areaApplicationController.completeGnAreasOfDistrict(qry, selected.getPerson().getDistrict());
        }
    }

    public List<Area> completeClientsMohArea(String qry) {
        List<Area> areas = new ArrayList<>();
        if (selected == null) {
            return areas;
        }
        if (selected.getPerson().getDistrict() == null) {
            return areaApplicationController.completeMohAreas(qry);
        } else {
            return areaApplicationController.completeGnAreasOfDistrict(qry, selected.getPerson().getDistrict());
        }
    }

    public List<Area> completeClientsPhiArea(String qry) {
        List<Area> areas = new ArrayList<>();
        if (selected == null) {
            return areas;
        }
        if (selected.getPerson().getMohArea() == null) {
            return areaApplicationController.completePhiAreas(qry);
        } else {
            return areaApplicationController.completePhiAreasOfMoh(qry, selected.getPerson().getMohArea());
        }
    }

    public void clearRegisterNewExistsValues() {
        phnExists = false;
        nicExists = false;
        emailExists = false;
        phone1Exists = false;
        ssNumberExists = false;
    }

    public void clearExistsValues() {
        phnExists = false;
        nicExists = false;
        passportExists = false;
        dlExists = false;
    }

    public void checkPhnExists() {
        phnExists = null;
        if (selected == null) {
            return;
        }
        if (selected.getPhn() == null) {
            return;
        }
        if (selected.getPhn().trim().equals("")) {
            return;
        }
        phnExists = checkPhnExists(selected.getPhn(), selected);
    }

    public Boolean checkPhnExists(String phn, Client c) {
        String jpql = "select count(c) from Client c "
                + " where c.retired=:ret "
                + " and c.phn=:phn ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("phn", phn);
        if (c != null && c.getId() != null) {
            jpql += " and c <> :client";
            m.put("client", c);
        }
        Long count = getFacade().countByJpql(jpql, m);
        if (count == null || count == 0l) {
            return false;
        } else {
            return true;
        }
    }

    public void checkNicExists() {
        nicExists = null;
        if (selected == null) {
            return;
        }
        if (selected.getPerson() == null) {
            return;
        }
        if (selected.getPerson().getNic() == null) {
            return;
        }
        if (selected.getPerson().getNic().trim().equals("")) {
            return;
        }
        nicExists = checkNicExists(selected.getPerson().getNic(), selected);
    }

    public void ageAndSexFromNic() {
        // // System.out.println("ageAndSexFromNic");
        if (getSelected().getPerson().getNic() != null) {
            SlNic n = new SlNic();
            n.setNic(getSelected().getPerson().getNic());
            if (n.getDateOfBirth() != null) {
                getSelected().getPerson().setDateOfBirth(n.getDateOfBirth());
            }
            if (n.getSex() != null) {
                if (n.getSex().equalsIgnoreCase("male")) {
                    getSelected().getPerson().setSex(itemApplicationController.getMale());
                } else {
                    getSelected().getPerson().setSex(itemApplicationController.getFemale());
                }
            }
        }
        updateYearDateMonth();
    }

    public Boolean checkNicExists(String nic, Client c) {
        String jpql = "select count(c) from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res "
                + " and c.person.nic=:nic ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        m.put("nic", nic);
        if (c != null && c.getPerson() != null && c.getPerson().getId() != null) {
            jpql += " and c.person <> :person";
            m.put("person", c.getPerson());
        }
        Long count = getFacade().countByJpql(jpql, m);
        if (count == null || count == 0l) {
            return false;
        } else {
            return true;
        }

    }

    public void addCreatedDateFromCreatedAt() {
        String j = "select c from Client c where c.createdOn is null";
        List<Client> cs = getFacade().findByJpql(j, 1000);
        // // System.out.println("cs.getSize() = " + cs.size());
        for (Client c : cs) {
            if (c.getCreatedOn() == null) {
                c.setCreatedOn(c.getCreatedAt());
                getFacade().edit(c);
            }
        }
    }

    public void checkEmailExists() {
        emailExists = null;
        if (selected == null) {
            return;
        }
        if (selected.getPerson() == null) {
            return;
        }
        if (selected.getPerson().getEmail() == null) {
            return;
        }
        if (selected.getPerson().getEmail().trim().equals("")) {
            return;
        }
        emailExists = checkEmailExists(selected.getPerson().getEmail(), selected);
    }

    public Boolean checkEmailExists(String email, Client c) {
        String jpql = "select count(c) from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res "
                + " and c.person.email=:email ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        m.put("email", email);
        if (c != null && c.getPerson() != null && c.getPerson().getId() != null) {
            jpql += " and c.person <> :person";
            m.put("person", c.getPerson());
        }
        Long count = getFacade().countByJpql(jpql, m);
        if (count == null || count == 0l) {
            return false;
        } else {
            return true;
        }

    }

    public void checkPhone1Exists() {
        phone1Exists = null;
        if (selected == null) {
            return;
        }
        if (selected.getPerson() == null) {
            return;
        }
        if (selected.getPerson().getPhone1() == null) {
            return;
        }
        if (selected.getPerson().getPhone1().trim().equals("")) {
            return;
        }
        phone1Exists = checkPhone1Exists(selected.getPerson().getPhone1(), selected);
    }

    public Boolean checkPhone1Exists(String phone1, Client c) {
        String jpql = "select count(c) from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res "
                + " and c.person.phone1=:phone1 ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        m.put("phone1", phone1);
        if (c != null && c.getPerson() != null && c.getPerson().getId() != null) {
            jpql += " and c.person <> :person";
            m.put("person", c.getPerson());
        }
        Long count = getFacade().countByJpql(jpql, m);
        if (count == null || count == 0l) {
            return false;
        } else {
            return true;
        }

    }

    public void checkSsNumberExists() {
        ssNumberExists = null;
        if (selected == null) {
            return;
        }
        if (selected.getPerson() == null) {
            return;
        }
        if (selected.getPerson().getSsNumber() == null) {
            return;
        }
        if (selected.getPerson().getSsNumber().trim().equals("")) {
            return;
        }
        ssNumberExists = checkSsNumberExists(selected.getPerson().getSsNumber(), selected);
    }

    public Boolean checkSsNumberExists(String ssNumber, Client c) {
        String jpql = "select count(c) from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res "
                + " and c.person.ssNumber=:ssNumber ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        m.put("ssNumber", ssNumber);
        if (c != null && c.getPerson() != null && c.getPerson().getId() != null) {
            jpql += " and c.person <> :person";
            m.put("person", c.getPerson());
        }
        Long count = getFacade().countByJpql(jpql, m);
        if (count == null || count == 0l) {
            return false;
        } else {
            return true;
        }

    }

    public void fixClientPersonCreatedAt() {
        String j = "select c from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res ";

        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        List<Client> cs = getFacade().findByJpql(j, m);
        for (Client c : cs) {

            if (c.getCreatedAt() == null && c.getPerson().getCreatedAt() != null) {
                c.setCreatedAt(c.getPerson().getCreatedAt());
                getFacade().edit(c);
            } else if (c.getCreatedAt() != null && c.getPerson().getCreatedAt() == null) {
                c.getPerson().setCreatedAt(c.getCreatedAt());
                getFacade().edit(c);
            } else if (c.getCreatedAt() == null && c.getPerson().getCreatedAt() == null) {
                c.getPerson().setCreatedAt(new Date());
                c.setCreatedAt(new Date());
                getFacade().edit(c);
            }

        }
        userTransactionController.recordTransaction("Fix Client Person Created At");
    }

    public void updateClientCreatedInstitution() {
        if (institution == null) {
            JsfUtil.addErrorMessage("Institution ?");
            return;
        }
        String j = "select c from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res "
                + " and c.id > :idf "
                + " and c.id < :idt ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        m.put("idf", idFrom);
        m.put("idt", idTo);
        List<Client> cs = getFacade().findByJpql(j, m);
        for (Client c : cs) {
            c.setCreateInstitution(institution);
            getFacade().edit(c);
        }
        userTransactionController.recordTransaction("Update Client Created Institution");
    }

    public void updateClientDateOfBirth() {
        String j = "select c from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res "
                + " and c.id > :idf "
                + " and c.id < :idt ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        m.put("idf", idFrom);
        m.put("idt", idTo);
        List<Client> cs = getFacade().findByJpql(j, m);
        for (Client c : cs) {
            Calendar cd = Calendar.getInstance();

            if (c.getPerson().getDateOfBirth() != null) {

                cd.setTime(c.getPerson().getDateOfBirth());

                int dobYear = cd.get(Calendar.YEAR);

                if (dobYear < 1800) {
                    cd.add(Calendar.YEAR, 2000);
                    c.getPerson().setDateOfBirth(cd.getTime());
                    getFacade().edit(c);
                }

            }
        }
        userTransactionController.recordTransaction("Update Client Date Of Birth");
    }

    public Long countOfRegistedClients(Institution ins, Area gn) {
        String j = "select count(c) from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        if (ins != null) {
            j += " and c.createInstitution=:ins ";
            m.put("ins", ins);
        }
        if (gn != null) {
            j += " and c.person.gnArea=:gn ";
            m.put("gn", gn);
        }
        return getFacade().countByJpql(j, m);
    }

    public String toRegisterdClientsDemo() {
        String j = "select c from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        if (webUserController.getLoggedInstitution() != null) {
            j += " and c.createInstitution=:ins ";
            m.put("ins", webUserController.getLoggedInstitution());
        } else {
            items = new ArrayList<>();
        }

        items = getFacade().findByJpql(j, m);
        return "/insAdmin/registered_clients";
    }

    public String toRegisterdClientsWithDates() {
        String j = "select c from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        if (webUserController.getLoggedInstitution() != null) {
            j += " and c.createInstitution=:ins ";
            m.put("ins", webUserController.getLoggedInstitution());
        }
        j = j + " and c.createdAt between :fd and :td ";
        j = j + " order by c.id desc";
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        items = getFacade().findByJpql(j, m, TemporalType.TIMESTAMP);
        return "/insAdmin/registered_clients";
    }

    public String toRegisterdClientsWithDatesForSystemAdmin() {
        userTransactionController.recordTransaction("To Registerd Clients With Dates For SystemAdmin");
        return "/systemAdmin/all_clients";
    }

    public void fillClients() {
        String j = "select new lk.gov.health.phsp.pojcs.ClientBasicData("
                + "c.id, "
                + "c.phn, "
                + "c.person.gnArea.name, "
                + "c.createInstitution.name, "
                + "c.person.dateOfBirth, "
                + "c.createdAt, "
                + "c.person.sex.name, "
                + "c.person.nic,"
                + "c.person.name "
                + ") "
                + "from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("res", true);
        j = j + " and c.createdAt between :fd and :td ";
        j = j + " order by c.id desc";
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        selectedClientsBasic = null;
        clients = getFacade().findByJpql(j, m, TemporalType.TIMESTAMP);
        userTransactionController.recordTransaction("Fill Clients - SysAdmin");
    }

    public void fillRegisterdClientsWithDatesForInstitution() {
        String j = "select c from Client c "
                + " where c.retired<>:ret "
                + " and c.reservedClient<>:res ";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("res", true);
        j = j + " and c.createdAt between :fd and :td ";

        if (institution != null) {
            j = j + " and c.createInstitution =:ins ";
            m.put("ins", institution);
        } else {
            j = j + " and c.createInstitution in :ins ";
            m.put("ins", webUserController.getLoggableInstitutions());
        }

        j = j + " order by c.id desc";
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        selectedClients = null;
        items = getFacade().findByJpql(j, m, TemporalType.TIMESTAMP);
    }

    public void saveSelectedImports() {
        if (institution == null) {
            JsfUtil.addErrorMessage("Institution ?");
            return;
        }
        for (Client c : selectedClients) {
            c.setCreateInstitution(institution);
            if (!checkPhnExists(c.getPhn(), null)) {
                c.setId(null);
                saveClient(c);
            }
        }
    }

    public void fillClientsWithWrongPhnLength() {
        String j = "select c from Client c where length(c.phn) <>11 and reservedClient<>true order by c.id";
        items = getFacade().findByJpql(j);
        userTransactionController.recordTransaction("Fill Clients With Wrong PHN Length");
    }

    public void fillRetiredClients() {
        String j = "select new lk.gov.health.phsp.pojcs.ClientBasicData("
                + "c.id, "
                + "c.phn, "
                + "c.person.gnArea.name, "
                + "c.createInstitution.name, "
                + "c.person.dateOfBirth, "
                + "c.createdAt, "
                + "c.person.sex.name, "
                + "c.person.nic,"
                + "c.person.name "
                + ") "
                + "from Client c "
                + " where c.retired=:ret "
                + " and c.reservedClient<>:res ";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("res", true);
        j = j + " and c.createdAt between :fd and :td ";
        j = j + " order by c.id desc";
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        selectedClientsBasic = null;
        clients = getFacade().findByJpql(j, m, TemporalType.TIMESTAMP);
        userTransactionController.recordTransaction("Fill Retired Clients - SysAdmin");
    }

    public String retireSelectedClients() {
        for (ClientBasicData cb : selectedClientsBasic) {

            Client c = getFacade().find(cb.getId());

            if (c == null) {
                continue;
            };

            c.setRetired(true);
            c.setRetireComments("Bulk Delete");
            c.setRetiredAt(new Date());
            c.setRetiredBy(webUserController.getLoggedUser());

            c.getPerson().setRetired(true);
            c.getPerson().setRetireComments("Bulk Delete");
            c.getPerson().setRetiredAt(new Date());
            c.getPerson().setRetiredBy(webUserController.getLoggedUser());

            getFacade().edit(c);
        }
        selectedClients = null;
        userTransactionController.recordTransaction("Retire Selected Clients - SysAdmin");
        return toRegisterdClientsWithDatesForSystemAdmin();
    }

    public String unretireSelectedClients() {
        for (ClientBasicData cb : selectedClientsBasic) {

            Client c = getFacade().find(cb.getId());

            if (c == null) {
                continue;
            };
            c.setRetired(false);
            c.setRetireComments("Bulk Un Delete");
            c.setLastEditBy(webUserController.getLoggedUser());
            c.setLastEditeAt(new Date());

            c.getPerson().setRetired(false);
            c.getPerson().setRetireComments("Bulk Un Delete");
            c.getPerson().setEditedAt(new Date());
            c.getPerson().setEditer(webUserController.getLoggedUser());

            getFacade().edit(c);
        }
        selectedClients = null;
        userTransactionController.recordTransaction("Unretire Selected Clients - SysAdmin");
        return toRegisterdClientsWithDatesForSystemAdmin();
    }

    public void retireSelectedClient() {
        Client c = selected;
        if (c != null) {
            c.setRetired(true);
            c.setRetiredBy(webUserController.getLoggedUser());
            c.setRetiredAt(new Date());

            c.getPerson().setRetired(true);
            c.getPerson().setRetiredBy(webUserController.getLoggedUser());
            c.getPerson().setRetiredAt(new Date());

            getFacade().edit(c);
        }
    }

    public void saveAllImports() {
        if (institution == null) {
            JsfUtil.addErrorMessage("Institution ?");
            return;
        }
        for (Client c : importedClients) {
            c.setCreateInstitution(institution);
            if (!checkPhnExists(c.getPhn(), null)) {
                c.setId(null);
                saveClient(c);
            }
        }
    }

//    public boolean phnExists(String phn) {
//        String j = "select c fromDate Client c where c.retired=:ret "
//                + " and c.phn=:phn";
//        Map m = new HashMap();
//        m.put("ret", false);
//        m.put("phn", phn);
//        Client c = getFacade().findFirstByJpql(j, m);
//        if (c == null) {
//            return false;
//        }
//        return true;
//    }
    public void prepareToCapturePhotoWithWebCam() {
        goingToCaptureWebCamPhoto = true;
    }

    public void finishCapturingPhotoWithWebCam() {
        goingToCaptureWebCamPhoto = false;
    }

    public void onTabChange(TabChangeEvent event) {

        // ////// // System.out.println("profileTabActiveIndex = " + profileTabActiveIndex);
        TabView tabView = (TabView) event.getComponent();

        profileTabActiveIndex = tabView.getChildren().indexOf(event.getTab());

    }

    public List<Encounter> fillEncounters(Client client,
            EncounterType encType, Integer maxRecordCount) {
        // ////// // System.out.println("fillEncounters");
        String j = "select e from Encounter e where e.retired=false ";
        Map m = new HashMap();
        if (client != null) {
            j += " and e.client=:c ";
            m.put("c", client);
        }
        if (maxRecordCount == null) {
            return encounterFacade.findByJpql(j, m);
        } else {
            return encounterFacade.findByJpql(j, m, maxRecordCount);
        }

    }

    public List<Encounter> fillEncounters(Client client, InstitutionType insType, EncounterType encType, boolean excludeCompleted, Integer maxRecordCount) {
        // ////// // System.out.println("fillEncounters");
        String j = "select e from Encounter e where e.retired=false ";
        Map m = new HashMap();
        if (client != null) {
            j += " and e.client=:c ";
            m.put("c", client);
        }
        if (insType != null) {
            j += " and e.institution.institutionType=:it ";
            m.put("it", insType);
        }
        if (insType != null) {
            j += " and e.encounterType=:et ";
            m.put("et", encType);
        }
        if (excludeCompleted) {
            j += " and e.completed=:com ";
            m.put("com", false);
        }
        if (maxRecordCount == null) {
            return encounterFacade.findByJpql(j, m);
        } else {
            return encounterFacade.findByJpql(j, m, maxRecordCount);
        }

    }

    public List<Encounter> fillEncounters(Client client, InstitutionType insType, EncounterType encType, boolean excludeCompleted) {
        return fillEncounters(client, insType, encType, true, null);
    }

    public void enrollInClinic() {
        if (selectedClinic == null) {
            JsfUtil.addErrorMessage("Please select an clinic to enroll.");
            return;
        }
        if (selected == null) {
            JsfUtil.addErrorMessage("Please select a client to enroll.");
            return;
        }
        if (selectedClinic.getId() == null) {
            JsfUtil.addErrorMessage("Please select a valid clinic to enroll.");
            return;
        }
        if (selected.getId() == null) {
            JsfUtil.addErrorMessage("Please save the client first before enrolling.");
            return;
        }
        if (encounterController.clinicEnrolmentExists(selectedClinic, selected)) {
            JsfUtil.addErrorMessage("This client is already enrolled.");
            return;
        }
        Encounter encounter = new Encounter();
        encounter.setClient(selected);
        encounter.setEncounterType(EncounterType.Death);
        encounter.setCreatedAt(new Date());
        encounter.setCreatedBy(webUserController.getLoggedUser());
        encounter.setInstitution(selectedClinic);
        encounter.setCreatedInstitution(webUserController.getLoggedInstitution());
        if (clinicDate != null) {
            encounter.setEncounterDate(clinicDate);
        } else {
            encounter.setEncounterDate(new Date());
        }
        encounter.setEncounterNumber(encounterController.createClinicEnrollNumber(selectedClinic));
        encounter.setCompleted(false);
        encounterFacade.create(encounter);
        JsfUtil.addSuccessMessage(selected.getPerson().getNameWithTitle() + " was Successfully Enrolled in " + selectedClinic.getName() + "\nThe Clinic number is " + encounter.getEncounterNumber());
        selectedClientsClinics = null;
    }

    public void generateAndAssignNewPhn() {
        if (selected == null) {
            return;
        }
        Institution poiIns;
        if (webUserController.getLoggedInstitution() == null) {
            JsfUtil.addErrorMessage("You do not have an Institution. Please contact support.");
            return;
        }
        //// // System.out.println("webUserController.getLoggedInstitution() = " + webUserController.getLoggedInstitution().getLastHin());
        if (webUserController.getLoggedInstitution().getPoiInstitution() != null) {
            poiIns = webUserController.getLoggedInstitution().getPoiInstitution();
        } else {
            poiIns = webUserController.getLoggedInstitution();
        }
        if (poiIns.getPoiNumber() == null || poiIns.getPoiNumber().trim().equals("")) {
            JsfUtil.addErrorMessage("A Point of Issue is NOT assigned to your Institution. Please discuss with the System Administrator.");
            return;
        }
        selected.setPhn(applicationController.createNewPersonalHealthNumberformat(poiIns));
    }

    public String generateNewPhn(Institution ins) {
        Institution poiIns;
        if (ins == null) {
            // // System.out.println("Ins is null");
            return null;
        }
        if (ins.getPoiInstitution() != null) {
            poiIns = ins.getPoiInstitution();
        } else {
            poiIns = ins;
        }
        if (poiIns.getPoiNumber() == null || poiIns.getPoiNumber().trim().equals("")) {
            // // System.out.println("A Point of Issue is NOT assigned to the Institution. Please discuss with the System Administrator.");
            return null;
        }
        return applicationController.createNewPersonalHealthNumberformat(poiIns);
    }

    public void gnAreaChanged() {
        if (selected == null) {
            return;
        }
        if (selected.getPerson().getGnArea() != null) {
            selected.getPerson().setDsArea(selected.getPerson().getGnArea().getDsd());
            selected.getPerson().setMohArea(selected.getPerson().getGnArea().getMoh());
            selected.getPerson().setPhmArea(selected.getPerson().getGnArea().getPhm());
            selected.getPerson().setDistrict(selected.getPerson().getGnArea().getDistrict());
            selected.getPerson().setProvince(selected.getPerson().getGnArea().getProvince());
        }
    }

    public void selectedClientChanged() {
        clientEncounterComponentFormSetController.setLastFiveClinicVisits(null);
    }

    public void updateYearDateMonth() {
        getYearMonthDay();
        if (selected != null) {
            yearMonthDay.setYear(selected.getPerson().getAgeYears() + "");
            yearMonthDay.setMonth(selected.getPerson().getAgeMonths() + "");
            yearMonthDay.setDay(selected.getPerson().getAgeDays() + "");
            selected.getPerson().setDobIsAnApproximation(false);
        } else {
            yearMonthDay = new YearMonthDay();
        }
    }

    public void yearMonthDateChanged() {
        if (selected == null) {
            return;
        }
        selected.getPerson().setDobIsAnApproximation(true);
        selected.getPerson().setDateOfBirth(guessDob(yearMonthDay));
    }

    public Date guessDob(YearMonthDay yearMonthDay) {
        int years = 0;
        int month = 0;
        int day = 0;
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("IST"));
        try {
            if (yearMonthDay.getYear() != null && !yearMonthDay.getYear().isEmpty()) {
                years = Integer.valueOf(yearMonthDay.getYear());
                now.add(Calendar.YEAR, -years);
            }

            if (yearMonthDay.getMonth() != null && !yearMonthDay.getMonth().isEmpty()) {
                month = Integer.valueOf(yearMonthDay.getMonth());
                now.add(Calendar.MONTH, -month);
            }

            if (yearMonthDay.getDay() != null && !yearMonthDay.getDay().isEmpty()) {
                day = Integer.valueOf(yearMonthDay.getDay());
                now.add(Calendar.DATE, -day);
            }

            return now.getTime();
        } catch (Exception e) {
            ////// ////// // System.out.println("Error is " + e.getMessage());
            return new Date();

        }
    }

    public void addNewPhnNumberToSelectedClient() {
        if (selected == null) {
            JsfUtil.addErrorMessage("No Client is Selected");
            return;
        }
        if (webUserController.getLoggedInstitution().getPoiNumber().trim().equals("")) {
            JsfUtil.addErrorMessage("No POI is configured for your institution. Please contact support.");
            return;
        }
        selected.setPhn(applicationController.createNewPersonalHealthNumber(webUserController.getLoggedInstitution()));
    }

    public String searchByPhn() {
        selectedClients = listPatientsByPhn(searchingPhn);
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            clearSearchById();
            if (selected.isReservedClient()) {
                return "/client/client";
            }
            return toClientProfile();
        } else {
            selected = null;
            clearSearchById();
            return toSelectClient();
        }
    }

    public String searchByNic() {
        selectedClients = listPatientsByNic(searchingNicNo);
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            clearSearchById();
            return toClientProfile();
        } else {
            selected = null;
            clearSearchById();
            return toSelectClient();
        }
    }

    public String searchByPhoneNumber() {
        selectedClients = listPatientsByPhone(searchingPhoneNumber);
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            clearSearchById();
            return toClientProfile();
        } else {
            selected = null;
            clearSearchById();
            return toSelectClient();
        }
    }

    public String searchByPassportNo() {
        selectedClients = listPatientsByPassportNo(searchingPassportNo);
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            clearSearchById();
            return toClientProfile();
        } else {
            selected = null;
            clearSearchById();
            return toSelectClient();
        }
    }

    public String searchByDrivingLicenseNo() {
        selectedClients = listPatientsByDrivingLicenseNo(searchingDrivingLicenceNo);
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            clearSearchById();
            return toClientProfile();
        } else {
            selected = null;
            clearSearchById();
            return toSelectClient();
        }
    }

    public String searchByLocalReferanceNo() {
        if (webUserController.getLoggedUser().isSystemAdministrator()) {
            selectedClients = listPatientsByLocalReferanceNoForSystemAdmin(searchingLocalReferanceNo);
        } else {
            selectedClients = listPatientsByLocalReferanceNo(searchingLocalReferanceNo);
        }
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            clearSearchById();
            return toClientProfile();
        } else {
            selected = null;
            clearSearchById();
            return toSelectClient();
        }
    }

    public String searchBySsNo() {
        selectedClients = listPatientsBySsNo(searchingSsNumber);
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            clearSearchById();
            return toClientProfile();
        } else {
            selected = null;
            clearSearchById();
            return toSelectClient();
        }
    }

    public String searchByAllId() {
        selectedClients = new ArrayList<>();
        if (searchingPhn != null && !searchingPhn.trim().equals("")) {
            selectedClients.addAll(listPatientsByPhn(searchingPhn));
        }
        if (searchingNicNo != null && !searchingNicNo.trim().equals("")) {
            selectedClients.addAll(listPatientsByNic(searchingNicNo));
        }
        if (searchingPhoneNumber != null && !searchingPhoneNumber.trim().equals("")) {
            selectedClients.addAll(listPatientsByPhone(searchingPhoneNumber));
        }
        if (searchingPassportNo != null && !searchingPassportNo.trim().equals("")) {
            selectedClients.addAll(listPatientsByPassportNo(searchingPassportNo));
        }
        if (searchingDrivingLicenceNo != null && !searchingDrivingLicenceNo.trim().equals("")) {
            selectedClients.addAll(listPatientsByDrivingLicenseNo(searchingDrivingLicenceNo));
        }
        if (searchingLocalReferanceNo != null && !searchingLocalReferanceNo.trim().equals("")) {
            selectedClients.addAll(listPatientsByLocalReferanceNo(searchingLocalReferanceNo));
        }
        if (searchingSsNumber != null && !searchingSsNumber.trim().equals("")) {
            selectedClients.addAll(listPatientsBySsNo(searchingSsNumber));
        }

        if (selectedClients == null || selectedClients.isEmpty()) {
            JsfUtil.addErrorMessage("No Results Found. Try different search criteria.");
            return "";
        }
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            clearSearchById();
            return toClientProfile();
        } else {
            selected = null;
            clearSearchById();
            return toSelectClient();
        }
    }

    public String searchById() {
        clearExistsValues();
        if (searchingPhn != null && !searchingPhn.trim().equals("")) {
            selectedClients = listPatientsByPhn(searchingPhn);
        } else if (searchingNicNo != null && !searchingNicNo.trim().equals("")) {
            selectedClients = listPatientsByNic(searchingNicNo);
        } else if (searchingPhoneNumber != null && !searchingPhoneNumber.trim().equals("")) {
            selectedClients = listPatientsByPhone(searchingPhoneNumber);
        } else if (searchingPassportNo != null && !searchingPassportNo.trim().equals("")) {
            selectedClients = listPatientsByPassportNo(searchingPassportNo);
        } else if (searchingDrivingLicenceNo != null && !searchingDrivingLicenceNo.trim().equals("")) {
            selectedClients = listPatientsByDrivingLicenseNo(searchingDrivingLicenceNo);
        } else if (searchingLocalReferanceNo != null && !searchingLocalReferanceNo.trim().equals("")) {
            selectedClients = listPatientsByLocalReferanceNo(searchingLocalReferanceNo);
        } else if (searchingSsNumber != null && !searchingSsNumber.trim().equals("")) {
            selectedClients = listPatientsBySsNo(searchingSsNumber);
        }
        if (selectedClients == null || selectedClients.isEmpty()) {
            JsfUtil.addErrorMessage("No Results Found. Try different search criteria.");
            return "";
        }
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            clearSearchById();
            return toClientProfile();
        } else {
            selected = null;
            clearSearchById();
            return toSelectClient();
        }
    }

    public String searchByAnyIdWithBasicData() {
        userTransactionController.recordTransaction("Search By Any Id");
        clearExistsValues();
        if (searchingId == null) {
            searchingId = "";
        }

        selectedClientsWithBasicData = listPatientsByIDsStepviceWithBasicData(searchingId.trim().toUpperCase());

        if (selectedClientsWithBasicData == null || selectedClientsWithBasicData.isEmpty()) {
            JsfUtil.addErrorMessage("No Results Found. Try different search criteria.");
            userTransactionController.recordTransaction("Search By Any Id Failed as no match");
            return "/client/search_by_id";
        }
        if (selectedClientsWithBasicData.size() == 1) {
            selected = getFacade().find(selectedClientsWithBasicData.get(0).getId());
            selectedClients = null;
            searchingId = "";
            userTransactionController.recordTransaction("Search By Any Id returend single match");
            return toClientProfile();
        } else {
            selected = null;
            searchingId = "";
            userTransactionController.recordTransaction("Search By Any Id returned multiple matches");
            return toSelectClientBasic();
        }
    }

    public String searchByPhnWithBasicData() {
        // // System.out.println("searchByPhnWithBasicData");
        userTransactionController.recordTransaction("Search By PHN");
        clearExistsValues();
        if (searchingId == null) {
            searchingId = "";
        }

        selectedClientsWithBasicData = listPatientsByPhnWithBasicData(searchingId.trim().toUpperCase());

        if (selectedClientsWithBasicData == null || selectedClientsWithBasicData.isEmpty()) {
            JsfUtil.addErrorMessage("No Results Found. Try different search criteria.");
            userTransactionController.recordTransaction("Search By Any Id Failed as no match");
            return "/client/search_by_id";
        }
        if (selectedClientsWithBasicData.size() == 1) {
            selected = getFacade().find(selectedClientsWithBasicData.get(0).getId());
            selectedClients = null;
            searchingId = "";
            userTransactionController.recordTransaction("Search By Any Id returend single match");
            return toClientProfile();
        } else {
            selected = null;
            searchingId = "";
            userTransactionController.recordTransaction("Search By Any Id returned multiple matches");
            return toSelectClientBasic();
        }
    }

    public String searchByAnyId() {
        clearExistsValues();
        if (searchingId == null) {
            searchingId = "";
        }

        selectedClients = listPatientsByIDsStepvice(searchingId.trim().toUpperCase());

        if (selectedClients == null || selectedClients.isEmpty()) {
            JsfUtil.addErrorMessage("No Results Found. Try different search criteria.");
            userTransactionController.recordTransaction("Search By Any Id");
            return "/client/search_by_id";
        }
        if (selectedClients.size() == 1) {
            setSelected(selectedClients.get(0));
            selectedClients = null;
            searchingId = "";
            userTransactionController.recordTransaction("Search By Any Id");
            return toClientProfile();
        } else {
            selected = null;
            searchingId = "";
            userTransactionController.recordTransaction("Search By Any Id");
            return toSelectClient();
        }
    }

    public void clearSearchById() {
        searchingId = "";
        searchingPhn = "";
        searchingPassportNo = "";
        searchingDrivingLicenceNo = "";
        searchingNicNo = "";
        searchingName = "";
        searchingPhoneNumber = "";
        searchingLocalReferanceNo = "";
        searchingSsNumber = "";
    }

    public List<Client> listPatientsByPhn(String phn) {
        String j = "select c from Client c where c.retired=false and upper(c.phn)=:q order by c.phn";
        Map m = new HashMap();
        m.put("q", phn.trim().toUpperCase());
        return getFacade().findByJpql(j, m);
    }

    public List<Client> listPatientsByNic(String phn) {
        String j = "select c from Client c where c.retired=false and c.reservedClient<>:res and upper(c.person.nic)=:q order by c.phn";
        Map m = new HashMap();
        m.put("res", true);
        m.put("q", phn.trim().toUpperCase());
        return getFacade().findByJpql(j, m);
    }

    public List<Client> listPatientsByPhone(String phn) {
        String j = "select c from Client c where c.retired=false and c.reservedClient<>:res and (upper(c.person.phone1)=:q or upper(c.person.phone2)=:q) order by c.phn";
        Map m = new HashMap();
        m.put("res", true);
        m.put("q", phn.trim().toUpperCase());
        return getFacade().findByJpql(j, m);
    }

    public List<Client> listPatientsByLocalReferanceNo(String refNo) {
        String j = "select c from Client c "
                + " where c.retired=false "
                + " and c.reservedClient<>:res "
                + " and lower(c.person.localReferanceNo)=:q "
                + " and c.createInstitution=:ins "
                + " order by c.phn";
        Map m = new HashMap();
        m.put("res", true);
        m.put("q", refNo.trim().toLowerCase());
        m.put("ins", webUserController.getLoggedInstitution());
        return getFacade().findByJpql(j, m);
    }

    public List<Client> listPatientsByLocalReferanceNoForSystemAdmin(String refNo) {
        String j = "select c from Client c "
                + " where c.retired=false "
                + " and c.reservedClient<>:res "
                + " and lower(c.person.localReferanceNo)=:q "
                + " order by c.phn";
        Map m = new HashMap();
        m.put("res", true);
        m.put("q", refNo.trim().toLowerCase());
        return getFacade().findByJpql(j, m);
    }

    public List<Client> listPatientsBySsNo(String ssNo) {
        String j = "select c from Client c "
                + " where c.retired=false "
                + " and c.reservedClient<>:res "
                + " and lower(c.person.ssNumber)=:q "
                + " order by c.phn";
        Map m = new HashMap();
        m.put("res", true);
        m.put("q", ssNo.trim().toLowerCase());
        return getFacade().findByJpql(j, m);
    }

    public List<Client> listPatientsByDrivingLicenseNo(String dlNo) {
        String j = "select c from Client c "
                + " where c.retired=false "
                + " and c.reservedClient<>:res "
                + " and lower(c.person.drivingLicenseNumber)=:q "
                + " order by c.phn";
        Map m = new HashMap();
        m.put("res", true);
        m.put("q", dlNo.trim().toLowerCase());
        return getFacade().findByJpql(j, m);
    }

    public List<Client> listPatientsByPassportNo(String passportNo) {
        String j = "select c from Client c "
                + " where c.retired=false "
                + " and c.reservedClient<>:res "
                + " and lower(c.person.passportNumber)=:q "
                + " order by c.phn";
        Map m = new HashMap();
        m.put("res", true);
        m.put("q", passportNo.trim().toLowerCase());
        return getFacade().findByJpql(j, m);
    }

    public List<Client> listPatientsByIDsStepvice(String ids) {
        //// // System.out.println("ids = " + ids);
        if (ids == null || ids.trim().equals("")) {
            return null;
        }
        List<Client> cs;
        if (ids == null || ids.trim().equals("")) {
            cs = new ArrayList<>();
            return cs;
        }
        String j;
        Map m;
        m = new HashMap();
        j = "select c from Client c "
                + " where c.retired=false "
                + " and upper(c.phn)=:q "
                + " order by c.phn";
        m.put("q", ids.trim().toUpperCase());
        //// // System.out.println("m = " + m);
        //// // System.out.println("j = " + j);
        cs = getFacade().findByJpql(j, m);

        if (cs != null && !cs.isEmpty()) {
            //// // System.out.println("cs.size() = " + cs.size());
            return cs;
        }

        j = "select c from Client c "
                + " where c.retired=false "
                + " and ("
                + " upper(c.person.phone1)=:q "
                + " or "
                + " upper(c.person.phone2)=:q "
                + " or "
                + " upper(c.person.nic)=:q "
                + " ) "
                + " order by c.phn";
        cs = getFacade().findByJpql(j, m);
        //// // System.out.println("m = " + m);
        //// // System.out.println("j = " + j);
        if (cs != null && !cs.isEmpty()) {
            //// // System.out.println("cs.size() = " + cs.size());
            return cs;
        }

        j = "select c from Client c "
                + " where c.retired=false "
                + " and ("
                + " c.person.localReferanceNo=:q "
                + " or "
                + " c.person.ssNumber=:q "
                + " ) "
                + " order by c.phn";

        return getFacade().findByJpql(j, m);
    }

    public List<ClientBasicData> listPatientsByIDsStepviceWithBasicData(String ids) {
        if (ids == null || ids.trim().equals("")) {
            return null;
        }
        List<ClientBasicData> cs;
        List<Object> objs;
        if (ids.trim().equals("")) {
            cs = new ArrayList<>();
            return cs;
        }
        String j;
        Map m;
        m = new HashMap();
        j = "select new lk.gov.health.phsp.pojcs.ClientBasicData("
                + "c.id, "
                + "c.phn, "
                + "c.person.name, "
                + "c.person.nic, "
                + "c.person.phone1, "
                + "c.person.address "
                + ") ";
        j += " from Client c "
                + " where c.retired=false "
                + " and c.phn=:q "
                + " order by c.phn";
        m.put("q", ids.trim().toUpperCase());
        //// // System.out.println("m = " + m);
        //// // System.out.println("j = " + j);
        objs = getFacade().findByJpql(j, m);

        if (objs != null && !objs.isEmpty()) {
            cs = objectsToClientBasicDataObjects(objs);
            return cs;
        }

        j = "select new lk.gov.health.phsp.pojcs.ClientBasicData("
                + "c.id, "
                + "c.phn, "
                + "c.person.name, "
                + "c.person.nic, "
                + "c.person.phone1, "
                + "c.person.address "
                + ") "
                + " from Client c "
                + " where c.retired=false "
                + " and ("
                + " c.person.phone1=:q "
                + " or "
                + " c.person.phone2=:q "
                + " or "
                + " c.person.nic=:q "
                + " ) "
                + " order by c.phn";
        objs = getFacade().findByJpql(j, m);
        //// // System.out.println("m = " + m);
        //// // System.out.println("j = " + j);
        if (objs != null && !objs.isEmpty()) {
            cs = objectsToClientBasicDataObjects(objs);
            return cs;
        }

        j = "select new lk.gov.health.phsp.pojcs.ClientBasicData("
                + "c.id, "
                + "c.phn, "
                + "c.person.name, "
                + "c.person.nic, "
                + "c.person.phone1, "
                + "c.person.address "
                + ") "
                + " from Client c "
                + " where c.retired=false "
                + " and ("
                + " c.person.localReferanceNo=:q "
                + " or "
                + " c.person.ssNumber=:q "
                + " ) "
                + " order by c.phn";

        objs = getFacade().findByJpql(j, m);
        if (objs != null && !objs.isEmpty()) {
            cs = objectsToClientBasicDataObjects(objs);
            return cs;
        }

        cs = new ArrayList<>();
        return cs;
    }

    public List<ClientBasicData> listPatientsByPhnWithBasicData(String ids) {
        if (ids == null || ids.trim().equals("")) {
            return null;
        }
        List<ClientBasicData> cs;
        List<Object> objs;
        if (ids.trim().equals("")) {
            cs = new ArrayList<>();
            return cs;
        }
        String j;
        Map m;
        m = new HashMap();
        j = "select new lk.gov.health.phsp.pojcs.ClientBasicData("
                + "c.id, "
                + "c.phn, "
                + "c.person.name, "
                + "c.person.sex.name,"
                + "c.person.nic, "
                + "c.person.phone1, "
                + "c.person.address "
                + ") ";
        j += " from Client c "
                + " where c.retired=false "
                + " and upper(c.phn)=:q "
                + " order by c.phn";
        m.put("q", ids.trim().toUpperCase());
        objs = getFacade().findByJpql(j, m);

        if (objs != null && !objs.isEmpty()) {
            cs = objectsToClientBasicDataObjects(objs);
            return cs;
        }
        cs = new ArrayList<>();
        return cs;
    }

    public List<ClientBasicData> objectsToClientBasicDataObjects(List<Object> objs) {
        List<ClientBasicData> cbds = new ArrayList<>();
        if (objs == null || objs.isEmpty()) {
            return cbds;
        }
        for (Object o : objs) {
            if (o instanceof ClientBasicData) {
                ClientBasicData c = (ClientBasicData) o;
                cbds.add(c);
            }
        }
        return cbds;
    }

    public List<Client> listPatientsByIDs(String ids) {
        if (ids == null || ids.trim().equals("")) {
            return null;
        }
        String j = "select c from Client c "
                + " where c.retired=false "
                + " and c.reservedClient<>:res "
                + " and ("
                + " upper(c.person.phone1)=:q "
                + " or "
                + " upper(c.person.phone2)=:q "
                + " or "
                + " upper(c.person.nic)=:q "
                + " or "
                + " upper(c.phn)=:q "
                + " or "
                + " c.person.localReferanceNo=:q "
                + " or "
                + " c.person.ssNumber=:q "
                + " ) "
                + " order by c.phn";
        Map m = new HashMap();
        m.put("res", true);
        m.put("q", ids.trim().toUpperCase());
        return getFacade().findByJpql(j, m);
    }

    public Client prepareCreate() {
        selected = new Client();
        return selected;
    }

    public String saveClientAndCaseEnrollment() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return "";
        }

        continuedLab = clientEncounterComponentFormSetController.getSelected().getEncounter().getReferalInstitution();
        Institution createdIns = null;
        selected.setRetired(false);
        if (selected.getCreateInstitution() == null) {
            if (webUserController.getLoggedInstitution().getPoiInstitution() != null) {
                createdIns = webUserController.getLoggedInstitution().getPoiInstitution();
            } else {
                createdIns = webUserController.getLoggedInstitution();
            }
            selected.setCreateInstitution(createdIns);
        } else {
            createdIns = selected.getCreateInstitution();
        }

        if (createdIns == null || createdIns.getPoiNumber() == null || createdIns.getPoiNumber().trim().equals("")) {
            JsfUtil.addErrorMessage("The institution you logged has no POI. Can not generate a PHN.");
            return "";
        }

        if (selected.getPhn() == null || selected.getPhn().trim().equals("")) {
            String newPhn = applicationController.createNewPersonalHealthNumberformat(createdIns);

            int count = 0;
            while (checkPhnExists(newPhn, null)) {
                newPhn = applicationController.createNewPersonalHealthNumberformat(createdIns);
                count++;
                if (count > 100) {
                    JsfUtil.addErrorMessage("Generating New PHN Failed. Client NOT saved. Please contact System Administrator.");
                    return "";
                }
            }
            selected.setPhn(newPhn);
        }

        if (selected.getId() == null) {
            if (checkPhnExists(selected.getPhn(), null)) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (selected.getPerson().getNic() != null && !selected.getPerson().getNic().trim().equals("")) {

                if (checkNicExists(selected.getPerson().getNic(), null)) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        } else {
            if (checkPhnExists(selected.getPhn(), selected)) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (selected.getPerson().getNic() != null && !selected.getPerson().getNic().trim().equals("")) {
                if (checkNicExists(selected.getPerson().getNic(), selected)) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        }
        selected.setReservedClient(false);

        saveClient(selected);

        if (clientEncounterComponentFormSetController.getSelected().getEncounter() != null) {

            clientEncounterComponentFormSetController.getSelected().getEncounter().setEncounterNumber(encounterController.createCaseNumber(webUserController.getLoggedInstitution()));

            clientEncounterComponentFormSetController.getSelected().getEncounter().setRetired(false);
            encounterFacade.edit(clientEncounterComponentFormSetController.getSelected().getEncounter());
        }
        getInstitutionCaseEnrollmentMap().put(selected.getId(), clientEncounterComponentFormSetController.getSelected().getEncounter());

        JsfUtil.addSuccessMessage("Saved.");
        return "/client/profile_case_enrollment";
    }

    public String saveSelectedClient() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return "";
        }
        if (selected.getId() == null) {
            if (checkPhnExists(selected.getPhn(), null)) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (selected.getPerson().getNic() != null && !selected.getPerson().getNic().trim().equals("")) {

                if (checkNicExists(selected.getPerson().getNic(), null)) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        } else {
            if (checkPhnExists(selected.getPhn(), selected)) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (selected.getPerson().getNic() != null && !selected.getPerson().getNic().trim().equals("")) {
                if (checkNicExists(selected.getPerson().getNic(), selected)) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        }
        selected.setReservedClient(false);
        saveClient(selected);
        JsfUtil.addSuccessMessage("Saved.");
        return "/client/profile_case_enrollment";
    }

    public String saveClientAndTestEnrollment() {
        // // System.out.println("saveClientAndTestEnrollment");
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return "";
        }
        continuedLab = clientEncounterComponentFormSetController.getSelected().getEncounter().getReferalInstitution();
        Institution createdIns = null;
        selected.setRetired(false);
        if (selected.getCreateInstitution() == null) {
            if (webUserController.getLoggedInstitution().getPoiInstitution() != null) {
                createdIns = webUserController.getLoggedInstitution().getPoiInstitution();
            } else {
                createdIns = webUserController.getLoggedInstitution();
            }
            selected.setCreateInstitution(createdIns);
        } else {
            createdIns = selected.getCreateInstitution();
        }

        if (createdIns == null || createdIns.getPoiNumber() == null || createdIns.getPoiNumber().trim().equals("")) {
            JsfUtil.addErrorMessage("The institution you logged has no POI. Can not generate a PHN.");
            return "";
        }

        if (selected.getPhn() == null || selected.getPhn().trim().equals("")) {
            String newPhn = applicationController.createNewPersonalHealthNumberformat(createdIns);

            int count = 0;
            while (checkPhnExists(newPhn, null)) {
                newPhn = applicationController.createNewPersonalHealthNumberformat(createdIns);
                count++;
                if (count > 100) {
                    JsfUtil.addErrorMessage("Generating New PHN Failed. Client NOT saved. Please contact System Administrator.");
                    return "";
                }
            }
            selected.setPhn(newPhn);
        }

        if (selected.getId() == null) {
            if (checkPhnExists(selected.getPhn(), null)) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (selected.getPerson().getNic() != null && !selected.getPerson().getNic().trim().equals("")) {

                if (checkNicExists(selected.getPerson().getNic(), null)) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        } else {
            if (checkPhnExists(selected.getPhn(), selected)) {
                JsfUtil.addErrorMessage("PHN already exists.");
                return null;
            }
            if (selected.getPerson().getNic() != null && !selected.getPerson().getNic().trim().equals("")) {
                if (checkNicExists(selected.getPerson().getNic(), selected)) {
                    JsfUtil.addErrorMessage("NIC already exists.");
                    return null;
                }
            }
        }

        saveClient(selected);

        if (clientEncounterComponentFormSetController.getSelected().getEncounter() != null) {

            if (clientEncounterComponentFormSetController.getSelected().getEncounter().getEncounterNumber() == null
                    || clientEncounterComponentFormSetController.getSelected().getEncounter().getEncounterNumber().trim().equals("")) {
                clientEncounterComponentFormSetController.getSelected().getEncounter().setEncounterNumber(encounterController.createTestNumber(webUserController.getLoggedInstitution()));
            }
            Encounter te
                    = clientEncounterComponentFormSetController.getSelected().getEncounter();
            te.setRetired(false);
            te.setSampled(true);
            te.setSampledAt(new Date());
            te.setSampledBy(webUserController.getLoggedUser());
//            te.setSentToLab(true);
//            te.setSentToLabAt(new Date());
//            te.setSentToLabBy(webUserController.getLoggedUser());
            encounterFacade.edit(te);
            lastTestOrderingCategory = te.getPcrOrderingCategory();
            lastTestPcrOrRat = te.getPcrTestType();
            lastTest = te;
        }

        // clientEncounterComponentFormSetController.completeFormsetForTestEnrollment();
        getInstitutionTestEnrollmentMap().put(selected.getId(), clientEncounterComponentFormSetController.getSelected().getEncounter());

        JsfUtil.addSuccessMessage("Saved.");
        return "/client/profile_test_enrollment";
    }

    public void reserverPhn() {
        Institution createdIns;
        int i = 0;

        if (webUserController.getLoggedInstitution().getPoiInstitution() != null) {
            createdIns = webUserController.getLoggedInstitution().getPoiInstitution();
        } else {
            createdIns = webUserController.getLoggedInstitution();
        }

        if (createdIns == null) {
            JsfUtil.addErrorMessage("No POI");
            return;
        }

        if (numberOfPhnToReserve == null) {
            JsfUtil.addErrorMessage("No Numner of PHN to add");
            return;
        }

        if (numberOfPhnToReserve > 100) {
            JsfUtil.addErrorMessage("Only upto 100 PHNs can reserve at a time.");
            return;
        }
        reservePhnList = new ArrayList<>();

        while (i < numberOfPhnToReserve) {
            String newPhn = generateNewPhn(createdIns);

            if (!checkPhnExists(newPhn, null)) {
                reservePhnList.add(newPhn);

                Client rc = new Client();

                rc.setPhn(newPhn);
                rc.setCreatedBy(webUserController.getLoggedUser());
                rc.setCreatedAt(new Date());
                rc.setCreatedOn(new Date());
                rc.setCreateInstitution(createdIns);
                if (rc.getPerson().getCreatedAt() == null) {
                    rc.getPerson().setCreatedAt(new Date());
                }
                if (rc.getPerson().getCreatedBy() == null) {
                    rc.getPerson().setCreatedBy(webUserController.getLoggedUser());
                }
                rc.setReservedClient(true);

                getFacade().create(rc);
                i = i + 1;
            }
        }
    }

    public void saveClient(Client c) {
        if (c == null) {
            JsfUtil.addErrorMessage("No Client Selected to save.");
            return;
        }
        if (c.getId() == null) {
            c.setCreatedBy(webUserController.getLoggedUser());
            if (c.getCreatedAt() == null) {
                c.setCreatedAt(new Date());
            }
            if (c.getCreatedOn() == null) {
                c.setCreatedOn(new Date());
            }
            if (c.getCreateInstitution() == null) {
                if (webUserController.getLoggedInstitution().getPoiInstitution() != null) {
                    c.setCreateInstitution(webUserController.getLoggedInstitution().getPoiInstitution());
                } else if (webUserController.getLoggedInstitution() != null) {
                    c.setCreateInstitution(webUserController.getLoggedInstitution());
                }
            }
            if (c.getPerson().getCreatedAt() == null) {
                c.getPerson().setCreatedAt(new Date());
            }
            if (c.getPerson().getCreatedBy() == null) {
                c.getPerson().setCreatedBy(webUserController.getLoggedUser());
            }
            getFacade().create(c);
        } else {
            c.setLastEditBy(webUserController.getLoggedUser());
            c.setLastEditeAt(new Date());
            getFacade().edit(c);
        }
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleClinical").getString("ClientCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items toDate trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleClinical").getString("ClientUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleClinical").getString("ClientDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items toDate trigger re-query.
        }
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleClinical").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleClinical").getString("PersistenceErrorOccured"));
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Functions - Temporary">
    public void convertFormsetDataInToEncounterDate() {
        String j = "select e "
                + " from Encounter e "
                + " where "
                + " e.pcrTestType is null "
                + " and e.retired=false "
                + " and e.institution is not null "
                + " and e.encounterDate is not null"
                + " and e.encounterType=:t";
        Map m = new HashMap();
        m.put("t", EncounterType.Test_Enrollment);
        if (idFrom == null) {
            idFrom = 1000l;
        }
        List<Encounter> cs = encounterFacade.findByJpql(j, m, idFrom.intValue());
        errorCode = "";
        for (Encounter e : cs) {

            if (e.getInstitution() == null) {
                errorCode += "No Institution";
                continue;
            }
            if (e.getEncounterDate() == null) {
                continue;
            }
            if (e.getClient() == null || e.getClient().getPerson() == null) {
                continue;
            }

            errorCode += "\n Institution = " + e.getInstitution().getName() + "\n Date : " + e.getEncounterDate()
                    + "\n Patient = " + e.getClient().getPerson().getName();
            ClientEncounterComponentItem eTestType = e.getClientEncounterComponentItemByCode("test_type");

            if (eTestType == null || eTestType.getItemValue() == null) {
                e.setPcrTestType(itemApplicationController.getPcr());
                errorCode += "PCR Type Not Found";
            } else {
                e.setPcrTestType(eTestType.getItemValue());
            }

            eTestType = e.getClientEncounterComponentItemByCode("covid_19_test_ordering_context_category");

            if (eTestType == null || eTestType.getItemValue() == null) {
                e.setPcrOrderingCategory(itemApplicationController.getPcr());
                errorCode += "Ordering Category Not Found";
            } else {
                e.setPcrOrderingCategory(eTestType.getItemValue());
            }

            encounterFacade.edit(e);

        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    public String getSearchingBhtno() {
        return searchingBhtno;
    }

    public void setSearchingBhtno(String searchingBhtno) {
        this.searchingBhtno = searchingBhtno;
    }

    public String getSearchingId() {
        return searchingId;
    }

    public void setSearchingId(String searchingId) {
        this.searchingId = searchingId;
    }

    public String getSearchingPhn() {
        return searchingPhn;
    }

    public void setSearchingPhn(String searchingPhn) {
        this.searchingPhn = searchingPhn;
    }

    public String getSearchingPassportNo() {
        return searchingPassportNo;
    }

    public void setSearchingPassportNo(String searchingPassportNo) {
        this.searchingPassportNo = searchingPassportNo;
    }

    public String getSearchingDrivingLicenceNo() {
        return searchingDrivingLicenceNo;
    }

    public void setSearchingDrivingLicenceNo(String searchingDrivingLicenceNo) {
        this.searchingDrivingLicenceNo = searchingDrivingLicenceNo;
    }

    public String getSearchingNicNo() {
        return searchingNicNo;
    }

    public void setSearchingNicNo(String searchingNicNo) {
        this.searchingNicNo = searchingNicNo;
    }

    public String getSearchingName() {
        return searchingName;
    }

    public void setSearchingName(String searchingName) {
        this.searchingName = searchingName;
    }

    public ClientFacade getEjbFacade() {
        return ejbFacade;
    }

    public ApplicationController getApplicationController() {
        return applicationController;
    }

    public Client getSelected() {
        return selected;
    }

    public void setSelected(Client selected) {
        this.selected = selected;
        updateYearDateMonth();
        selectedClientChanged();
        selectedClientsClinics = null;
        selectedClientEncounters = null;
    }

    public String getSearchingTestNo() {
        return searchingTestNo;
    }

    public void setSearchingTestNo(String searchingTestNo) {
        this.searchingTestNo = searchingTestNo;
    }

    private ClientFacade getFacade() {
        return ejbFacade;
    }

    public List<Client> getItems() {
//        if (items == null) {
//            items = getFacade().findAll();
//        }
        return items;
    }

    public List<Client> getItems(String jpql, Map m) {
        return getFacade().findByJpql(jpql, m);
    }

    public Client getClient(java.lang.Long id) {
        return getFacade().find(id);
    }

    public Client getClientByNic(String nic) {
        String jpql = "select c from Client c "
                + " where c.retired=:ret "
                + " and c.person.nic=:nic ";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("nic", nic);
        return getFacade().findFirstByJpql(jpql, m);
    }

    public List<Client> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Client> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public String getSearchingPhoneNumber() {
        return searchingPhoneNumber;
    }

    public void setSearchingPhoneNumber(String searchingPhoneNumber) {
        this.searchingPhoneNumber = searchingPhoneNumber;
    }

    public List<Client> getSelectedClients() {
        return selectedClients;
    }

    public void setSelectedClients(List<Client> selectedClients) {
        this.selectedClients = selectedClients;
    }

    public YearMonthDay getYearMonthDay() {
        if (yearMonthDay == null) {
            yearMonthDay = new YearMonthDay();
        }
        return yearMonthDay;
    }

    public void setYearMonthDay(YearMonthDay yearMonthDay) {
        this.yearMonthDay = yearMonthDay;
    }

    public Institution getSelectedClinic() {
        return selectedClinic;
    }

    public void setSelectedClinic(Institution selectedClinic) {
        this.selectedClinic = selectedClinic;
    }

    public List<Encounter> getSelectedClientsClinics() {
        if (selectedClientsClinics == null) {
            selectedClientsClinics = fillEncounters(selected, InstitutionType.Clinic, EncounterType.Death, true);
        }
        return selectedClientsClinics;
    }

    public void setSelectedClientsClinics(List<Encounter> selectedClientsClinics) {
        this.selectedClientsClinics = selectedClientsClinics;
    }

    public int getProfileTabActiveIndex() {
        return profileTabActiveIndex;
    }

    public void setProfileTabActiveIndex(int profileTabActiveIndex) {
        this.profileTabActiveIndex = profileTabActiveIndex;
    }

    public EncounterFacade getEncounterFacade() {
        return encounterFacade;
    }

    public EncounterController getEncounterController() {
        return encounterController;
    }

    public boolean isGoingToCaptureWebCamPhoto() {
        return goingToCaptureWebCamPhoto;
    }

    public void setGoingToCaptureWebCamPhoto(boolean goingToCaptureWebCamPhoto) {
        this.goingToCaptureWebCamPhoto = goingToCaptureWebCamPhoto;
    }

    public String getUploadDetails() {
        if (uploadDetails == null || uploadDetails.trim().equals("")) {
            uploadDetails
                    = "client_phn_number" + "\n"
                    + "client_nic_number" + "\n"
                    + "client_title" + "\n"
                    + "client_name" + "\n"
                    + "client_sex" + "\n"
                    + "client_data_of_birth" + "\n"
                    + "client_citizenship" + "\n"
                    + "client_ethnic_group" + "\n"
                    + "client_religion" + "\n"
                    + "client_marital_status" + "\n"
                    + "client_permanent_address" + "\n"
                    + "client_gn_area_name" + "\n"
                    + "client_gn_area_code" + "\n"
                    + "client_mobile_number" + "\n"
                    + "client_home_number" + "\n"
                    + "client_email" + "\n"
                    + "client_registered_at" + "\n";
        }

        return uploadDetails;
    }

    public void setUploadDetails(String uploadDetails) {
        this.uploadDetails = uploadDetails;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<Client> getImportedClients() {
        return importedClients;
    }

    public void setImportedClients(List<Client> importedClients) {
        this.importedClients = importedClients;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public ItemController getItemController() {
        return itemController;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public AreaController getAreaController() {
        return areaController;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Long getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(Long idFrom) {
        this.idFrom = idFrom;
    }

    public Long getIdTo() {
        return idTo;
    }

    public void setIdTo(Long idTo) {
        this.idTo = idTo;
    }

    public Date getClinicDate() {
        return clinicDate;
    }

    public void setClinicDate(Date clinicDate) {
        this.clinicDate = clinicDate;
    }

    public Boolean getNicExists() {
        return nicExists;
    }

    public void setNicExists(Boolean nicExists) {
        this.nicExists = nicExists;
    }

    public Boolean getPhnExists() {
        return phnExists;
    }

    public void setPhnExists(Boolean phnExists) {
        this.phnExists = phnExists;
    }

    public Boolean getPassportExists() {
        return passportExists;
    }

    public void setPassportExists(Boolean passportExists) {
        this.passportExists = passportExists;
    }

    public Boolean getDlExists() {
        return dlExists;
    }

    public void setDlExists(Boolean dlExists) {
        this.dlExists = dlExists;
    }

    public InstitutionController getInstitutionController() {
        return institutionController;
    }

    public void setInstitutionController(InstitutionController institutionController) {
        this.institutionController = institutionController;
    }

    public Date getFromDate() {
        if (fromDate == null) {
            fromDate = commonController.startOfTheDay();
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

    public String getDateTimeFormat() {
        if (dateTimeFormat == null) {
            dateTimeFormat = "yyyy-MM-dd hh:mm:ss";
        }
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getDateFormat() {
        if (dateFormat == null) {
            dateFormat = "yyyy/MM/dd";
        }
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getSearchingLocalReferanceNo() {
        return searchingLocalReferanceNo;
    }

    public void setSearchingLocalReferanceNo(String searchingLocalReferanceNo) {
        this.searchingLocalReferanceNo = searchingLocalReferanceNo;
    }

    public String getSearchingSsNumber() {
        return searchingSsNumber;
    }

    public void setSearchingSsNumber(String searchingSsNumber) {
        this.searchingSsNumber = searchingSsNumber;
    }

    public Boolean getLocalReferanceExists() {
        return localReferanceExists;
    }

    public void setLocalReferanceExists(Boolean localReferanceExists) {
        this.localReferanceExists = localReferanceExists;
    }

    public Boolean getSsNumberExists() {
        return ssNumberExists;
    }

    public void setSsNumberExists(Boolean ssNumberExists) {
        this.ssNumberExists = ssNumberExists;
    }

    public ClientEncounterComponentFormSetController getClientEncounterComponentFormSetController() {
        return clientEncounterComponentFormSetController;
    }

    public void setClientEncounterComponentFormSetController(ClientEncounterComponentFormSetController clientEncounterComponentFormSetController) {
        this.clientEncounterComponentFormSetController = clientEncounterComponentFormSetController;
    }

    public Long getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(Long selectedId) {
        selected = getFacade().find(selectedId);
        this.selectedId = selectedId;
    }

    public List<ClientBasicData> getClients() {
        return clients;
    }

    public void setClients(List<ClientBasicData> clients) {
        this.clients = clients;
    }

    public List<ClientBasicData> getSelectedClientsBasic() {
        return selectedClientsBasic;
    }

    public void setSelectedClientsBasic(List<ClientBasicData> selectedClientsBasic) {
        this.selectedClientsBasic = selectedClientsBasic;
    }

    public int getIntNo() {
        return intNo;
    }

    public void setIntNo(int intNo) {
        this.intNo = intNo;
    }

    public List<Encounter> getSelectedClientEncounters() {
        if (selectedClientEncounters == null) {
            selectedClientEncounters = fillEncounters(selected, EncounterType.Test_Enrollment, 5);

        }
        return selectedClientEncounters;
    }

    public void setSelectedClientEncounters(List<Encounter> selectedClientEncounters) {
        this.selectedClientEncounters = selectedClientEncounters;
    }

    public Boolean getEmailExists() {
        return emailExists;
    }

    public void setEmailExists(Boolean emailExists) {
        this.emailExists = emailExists;
    }

    public Boolean getPhone1Exists() {
        return phone1Exists;
    }

    public void setPhone1Exists(Boolean phone1Exists) {
        this.phone1Exists = phone1Exists;
    }

    public Integer getNumberOfPhnToReserve() {
        return numberOfPhnToReserve;
    }

    public void setNumberOfPhnToReserve(Integer numberOfPhnToReserve) {
        this.numberOfPhnToReserve = numberOfPhnToReserve;
    }

    public List<ClientBasicData> getSelectedClientsWithBasicData() {
        return selectedClientsWithBasicData;
    }

    public void setSelectedClientsWithBasicData(List<ClientBasicData> selectedClientsWithBasicData) {
        this.selectedClientsWithBasicData = selectedClientsWithBasicData;
    }

    public List<String> getReservePhnList() {
        return reservePhnList;
    }

    public void setReservePhnList(List<String> reservePhnList) {
        this.reservePhnList = reservePhnList;
    }

    public List<Encounter> getInstitutionCaseEnrollments() {
        institutionCaseEnrollments = new ArrayList<>(getInstitutionCaseEnrollmentMap().values());
        return institutionCaseEnrollments;
    }

    public void setInstitutionCaseEnrollments(List<Encounter> institutionCaseEnrollments) {
        this.institutionCaseEnrollments = institutionCaseEnrollments;
    }

    public List<Encounter> getInstitutionTestEnrollments() {
        institutionTestEnrollments = new ArrayList<>(getInstitutionTestEnrollmentMap().values());
        return institutionTestEnrollments;
    }

    public void setInstitutionTestEnrollments(List<Encounter> institutionTestEnrollments) {
        this.institutionTestEnrollments = institutionTestEnrollments;
    }

    private Map<Long, Encounter> findTodaysInstitutionCaseEnrollmentEncounters() {
        String j = "select c from Encounter c "
                + " where c.retired=false"
                + " and c.institution=:ins "
                + " and c.encounterDate=:d "
                + " and c.encounterType=:t "
                + " order by c.id desc";
        Map m = new HashMap();
        m.put("ins", webUserController.getLoggedInstitution());
        m.put("d", new Date());
        m.put("t", EncounterType.Case_Enrollment);
        List<Encounter> cs = getEncounterFacade().findByJpql(j, m);
        Map<Long, Encounter> tm = new HashMap<>();
        if (cs != null) {
            for (Encounter c : cs) {
                tm.put(c.getId(), c);
            }
        }
        return tm;
    }

    private Map<Long, Encounter> findTodaysInstitutionTestEnrollmentEncounters() {
        String j = "select c from Encounter c "
                + " where c.retired=false "
                + " and c.institution=:ins "
                + " and c.encounterDate=:d "
                + " and c.encounterType=:t "
                + " order by c.id desc";
        Map m = new HashMap();
        m.put("ins", webUserController.getLoggedInstitution());
        m.put("d", new Date());
        m.put("t", EncounterType.Test_Enrollment);
        List<Encounter> cs = getEncounterFacade().findByJpql(j, m);
        Map<Long, Encounter> tm = new HashMap<>();
        if (cs != null) {
            for (Encounter c : cs) {
                tm.put(c.getId(), c);
            }
        }
        return tm;
    }

    public String toListCases() {
        return "/client/case_list";
    }

    public String toListTests() {
        return "/client/test_list";
    }

    public void fillTestEnrollmentToMark() {
        String j = "select c from Encounter c "
                + " where c.retired=false"
                + " and c.institution=:ins "
                + " and c.encounterDate between :fd and :td "
                + " and c.encounterType=:t "
                + " order by c.id";
        Map m = new HashMap();
        Institution ins = webUserController.getLoggedInstitution();
        m.put("ins", ins);
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("t", EncounterType.Test_Enrollment);
        listedToReceive = getEncounterFacade().findByJpql(j, m);
    }

    public void fillTestList() {
        Map m = new HashMap();
        String j = "select c from Encounter c "
                + " where c.retired=false";
        j += " and c.institution in :inss ";
        m.put("inss", webUserController.getLoggableInstitutions());
        j += " and c.encounterDate between :fd and :td "
                + " and c.encounterType=:t "
                + " order by c.id";
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("t", EncounterType.Test_Enrollment);
        testList = getEncounterFacade().findByJpql(j, m);
    }

    public void fillCaseList() {
        Map m = new HashMap();
        String j = "select c from Encounter c "
                + " where c.retired=false";

        j += " and c.institution=:ins ";
        m.put("ins", webUserController.getLoggedInstitution());

        j += " and c.encounterDate between :fd and :td "
                + " and c.encounterType=:t "
                + " order by c.id";

        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("t", EncounterType.Case_Enrollment);
        caseList = getEncounterFacade().findByJpql(j, m);
    }

    public void fillInvestigatedListForMoh() {
        Map m = new HashMap();
        String j = "select c from Encounter c "
                + " where c.retired=false";
        j += " and c.institution=:ins ";
        m.put("ins", webUserController.getLoggedInstitution());
        j += " and c.encounterDate between :fd and :td "
                + " and c.encounterType=:t "
                + " order by c.id";
        m.put("fd", getFromDate());
        m.put("td", getToDate());
        m.put("t", EncounterType.Case_Enrollment);
        caseList = getEncounterFacade().findByJpql(j, m);
    }

    public Map<Long, Encounter> getInstitutionCaseEnrollmentMap() {
        if (institutionCaseEnrollmentMap == null) {
            institutionCaseEnrollmentMap = findTodaysInstitutionCaseEnrollmentEncounters();
        }
        return institutionCaseEnrollmentMap;
    }

    public void setInstitutionCaseEnrollmentMap(Map<Long, Encounter> institutionCaseEnrollmentMap) {
        this.institutionCaseEnrollmentMap = institutionCaseEnrollmentMap;
    }

    public Map<Long, Encounter> getInstitutionTestEnrollmentMap() {
        if (institutionTestEnrollmentMap == null) {
            institutionTestEnrollmentMap = findTodaysInstitutionTestEnrollmentEncounters();
        }
        return institutionTestEnrollmentMap;
    }

    public void setInstitutionTestEnrollmentMap(Map<Long, Encounter> institutionTestEnrollmentMap) {
        this.institutionTestEnrollmentMap = institutionTestEnrollmentMap;
    }

    public Encounter getSelectedEncounterToMarkTest() {
        return selectedEncounterToMarkTest;
    }

    public void setSelectedEncounterToMarkTest(Encounter selectedEncounterToMarkTest) {
        this.selectedEncounterToMarkTest = selectedEncounterToMarkTest;
    }

    public List<Encounter> getListedToReceive() {
        return listedToReceive;
    }

    public void setListedToReceive(List<Encounter> listedToReceive) {
        this.listedToReceive = listedToReceive;
    }

    public List<Encounter> getTestList() {
        return testList;
    }

    public void setTestList(List<Encounter> testList) {
        this.testList = testList;
    }

    public List<Encounter> getCaseList() {
        return caseList;
    }

    public void setCaseList(List<Encounter> caseList) {
        this.caseList = caseList;
    }

    public Encounter getSelectedEncounter() {
        return selectedEncounter;
    }

    public void setSelectedEncounter(Encounter selectedEncounter) {
        this.selectedEncounter = selectedEncounter;
    }

    public SmsFacade getSmsFacade() {
        return smsFacade;
    }

    public void setSmsFacade(SmsFacade smsFacade) {
        this.smsFacade = smsFacade;
    }

    public List<InstitutionCount> getLabSummariesToReceive() {
        return labSummariesToReceive;
    }

    public void setLabSummariesToReceive(List<InstitutionCount> labSummariesToReceive) {
        this.labSummariesToReceive = labSummariesToReceive;
    }

    public Institution getReferingInstitution() {
        return referingInstitution;
    }

    public void setReferingInstitution(Institution referingInstitution) {
        this.referingInstitution = referingInstitution;
    }

    public AreaApplicationController getAreaApplicationController() {
        return areaApplicationController;
    }

    public void setAreaApplicationController(AreaApplicationController areaApplicationController) {
        this.areaApplicationController = areaApplicationController;
    }

    public InstitutionApplicationController getInstitutionApplicationController() {
        return institutionApplicationController;
    }

    public void setInstitutionApplicationController(InstitutionApplicationController institutionApplicationController) {
        this.institutionApplicationController = institutionApplicationController;
    }

    public ItemApplicationController getItemApplicationController() {
        return itemApplicationController;
    }

    public void setItemApplicationController(ItemApplicationController itemApplicationController) {
        this.itemApplicationController = itemApplicationController;
    }

    public UserTransactionController getUserTransactionController() {
        return userTransactionController;
    }

    public void setUserTransactionController(UserTransactionController userTransactionController) {
        this.userTransactionController = userTransactionController;
    }

    public DesignComponentFormSetController getDesignComponentFormSetController() {
        return designComponentFormSetController;
    }

    public void setDesignComponentFormSetController(DesignComponentFormSetController designComponentFormSetController) {
        this.designComponentFormSetController = designComponentFormSetController;
    }

    public ClientEncounterComponentItemController getClientEncounterComponentItemController() {
        return clientEncounterComponentItemController;
    }

    public void setClientEncounterComponentItemController(ClientEncounterComponentItemController clientEncounterComponentItemController) {
        this.clientEncounterComponentItemController = clientEncounterComponentItemController;
    }

    public PreferenceController getPreferenceController() {
        return preferenceController;
    }

    public void setPreferenceController(PreferenceController preferenceController) {
        this.preferenceController = preferenceController;
    }

    public Institution getContinuedLab() {
        return continuedLab;
    }

    public void setContinuedLab(Institution continuedLab) {
        this.continuedLab = continuedLab;
    }

    public List<Encounter> getSelectedToReceive() {
        return selectedToReceive;
    }

    public void setSelectedToReceive(List<Encounter> selectedToReceive) {
        this.selectedToReceive = selectedToReceive;
    }

    public List<Encounter> getSelectedToConfirm() {
        return selectedToConfirm;
    }

    public void setSelectedToConfirm(List<Encounter> selectedToConfirm) {
        this.selectedToConfirm = selectedToConfirm;
    }

    public List<Encounter> getListedToConfirm() {
        return listedToConfirm;
    }

    public void setListedToConfirm(List<Encounter> listedToConfirm) {
        this.listedToConfirm = listedToConfirm;
    }

    public List<Encounter> getListedToEnterResults() {
        return listedToEnterResults;
    }

    public void setListedToEnterResults(List<Encounter> listedToEnterResults) {
        this.listedToEnterResults = listedToEnterResults;
    }

    public List<Encounter> getListedToReviewResults() {
        return listedToReviewResults;
    }

    public void setListedToReviewResults(List<Encounter> listedToReviewResults) {
        this.listedToReviewResults = listedToReviewResults;
    }

    public List<Encounter> getSelectedToReview() {
        return selectedToReview;
    }

    public void setSelectedToReview(List<Encounter> selectedToReview) {
        this.selectedToReview = selectedToReview;
    }

    public List<Encounter> getListedToPrint() {
        return listedToPrint;
    }

    public void setListedToPrint(List<Encounter> listedToPrint) {
        this.listedToPrint = listedToPrint;
    }

    public List<Encounter> getSelectedToPrint() {
        return selectedToPrint;
    }

    public void setSelectedToPrint(List<Encounter> selectedToPrint) {
        this.selectedToPrint = selectedToPrint;
    }

    public Encounter getLastTest() {
        return lastTest;
    }

    public void setLastTest(Encounter lastTest) {
        this.lastTest = lastTest;
    }

    public Item getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(Item selectedTest) {
        this.selectedTest = selectedTest;
    }

    public List<Encounter> getListedToDispatch() {
        return listedToDispatch;
    }

    public void setListedToDispatch(List<Encounter> listedToDispatch) {
        this.listedToDispatch = listedToDispatch;
    }

    public List<Encounter> getListedToDivert() {
        return listedToDivert;
    }

    public void setListedToDivert(List<Encounter> listedToDivert) {
        this.listedToDivert = listedToDivert;
    }

    public List<Encounter> getSelectedToDispatch() {
        return selectedToDispatch;
    }

    public void setSelectedToDispatch(List<Encounter> selectedToDispatch) {
        this.selectedToDispatch = selectedToDispatch;
    }

    public List<Encounter> getSelectedToDivert() {
        return selectedToDivert;
    }

    public void setSelectedToDivert(List<Encounter> selectedToDivert) {
        this.selectedToDivert = selectedToDivert;
    }

    public Institution getDivertingLab() {
        return divertingLab;
    }

    public void setDivertingLab(Institution divertingLab) {
        this.divertingLab = divertingLab;
    }

    public Institution getDispatchingLab() {
        return dispatchingLab;
    }

    public void setDispatchingLab(Institution dispatchingLab) {
        this.dispatchingLab = dispatchingLab;
    }

    public String getNameCol() {
        return nameCol;
    }

    public void setNameCol(String nameCol) {
        this.nameCol = nameCol;
    }

    public String getTestNoCol() {
        return testNoCol;
    }

    public void setTestNoCol(String testNoCol) {
        this.testNoCol = testNoCol;
    }

    public String getAgeColumn() {
        return ageColumn;
    }

    public void setAgeColumn(String ageColumn) {
        this.ageColumn = ageColumn;
    }

    public String getSexCol() {
        return sexCol;
    }

    public void setSexCol(String sexCol) {
        this.sexCol = sexCol;
    }

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public Item getLastTestOrderingCategory() {
        return lastTestOrderingCategory;
    }

    public void setLastTestOrderingCategory(Item lastTestOrderingCategory) {
        this.lastTestOrderingCategory = lastTestOrderingCategory;
    }

    public Item getLastTestPcrOrRat() {
        return lastTestPcrOrRat;
    }

    public void setLastTestPcrOrRat(Item lastTestPcrOrRat) {
        this.lastTestPcrOrRat = lastTestPcrOrRat;
    }

    public List<InstitutionCount> getLabSummariesReceived() {
        return labSummariesReceived;
    }

    public void setLabSummariesReceived(List<InstitutionCount> labSummariesReceived) {
        this.labSummariesReceived = labSummariesReceived;
    }

    public List<InstitutionCount> getLabSummariesReviewed() {
        return labSummariesReviewed;
    }

    public void setLabSummariesReviewed(List<InstitutionCount> labSummariesReviewed) {
        this.labSummariesReviewed = labSummariesReviewed;
    }

    public List<InstitutionCount> getLabSummariesConfirmed() {
        return labSummariesConfirmed;
    }

    public void setLabSummariesConfirmed(List<InstitutionCount> labSummariesConfirmed) {
        this.labSummariesConfirmed = labSummariesConfirmed;
    }

    public List<InstitutionCount> getLabSummariesPositive() {
        return labSummariesPositive;
    }

    public void setLabSummariesPositive(List<InstitutionCount> labSummariesPositive) {
        this.labSummariesPositive = labSummariesPositive;
    }

    public List<Encounter> getListReceived() {
        return listReceived;
    }

    public void setListReceived(List<Encounter> listReceived) {
        this.listReceived = listReceived;
    }

    public List<Encounter> getListReviewed() {
        return listReviewed;
    }

    public void setListReviewed(List<Encounter> listReviewed) {
        this.listReviewed = listReviewed;
    }

    public List<Encounter> getListConfirmed() {
        return listConfirmed;
    }

    public void setListConfirmed(List<Encounter> listConfirmed) {
        this.listConfirmed = listConfirmed;
    }

    public List<Encounter> getListPositives() {
        return listPositives;
    }

    public void setListPositives(List<Encounter> listPositives) {
        this.listPositives = listPositives;
    }

    public List<InstitutionCount> getLabSummariesEntered() {
        return labSummariesEntered;
    }

    public void setLabSummariesEntered(List<InstitutionCount> labSummariesEntered) {
        this.labSummariesEntered = labSummariesEntered;
    }

    public String getSerialPrefix() {
        return serialPrefix;
    }

    public void setSerialPrefix(String serialPrefix) {
        this.serialPrefix = serialPrefix;
    }

    public Long getSerialStart() {
        return serialStart;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public void setSerialStart(Long serialStart) {
        this.serialStart = serialStart;
    }

    public String getBulkPrintReport() {
        return bulkPrintReport;
    }

    public void setBulkPrintReport(String bulkPrintReport) {
        this.bulkPrintReport = bulkPrintReport;
    }

    public String getPhoneCol() {
        return phoneCol;
    }

    public void setPhoneCol(String phoneCol) {
        this.phoneCol = phoneCol;
    }

    public String getAddressCol() {
        return addressCol;
    }

    public void setAddressCol(String addressCol) {
        this.addressCol = addressCol;
    }

    public List<ClientImport> getClientImports() {
        if (clientImports == null) {
            clientImports = new ArrayList<>();
        }
        return clientImports;
    }

    public void setClientImports(List<ClientImport> clientImports) {
        this.clientImports = clientImports;
    }

    public Area getDistrict() {
        return district;
    }

    public void setDistrict(Area district) {
        this.district = district;
    }

    public List<ClientImport> getClientImportsSelected() {
        if (clientImportsSelected == null) {
            clientImportsSelected = new ArrayList<>();
        }
        return clientImportsSelected;
    }

    public void setClientImportsSelected(List<ClientImport> clientImportsSelected) {
        this.clientImportsSelected = clientImportsSelected;
    }

    public String getNicCol() {
        return nicCol;
    }

    public void setNicCol(String nicCol) {
        this.nicCol = nicCol;
    }

    public String getResultCol() {
        return resultCol;
    }

    public void setResultCol(String resultCol) {
        this.resultCol = resultCol;
    }

    public String getMohCol() {
        return mohCol;
    }

    public void setMohCol(String mohCol) {
        this.mohCol = mohCol;
    }

    public String getDistrictCol() {
        return districtCol;
    }

    public void setDistrictCol(String districtCol) {
        this.districtCol = districtCol;
    }

    public String getWardCol() {
        return wardCol;
    }

    public void setWardCol(String wardCol) {
        this.wardCol = wardCol;
    }

    public String getBhtCol() {
        return bhtCol;
    }

    public void setBhtCol(String bhtCol) {
        this.bhtCol = bhtCol;
    }

    public String getCt1Col() {
        return ct1Col;
    }

    public void setCt1Col(String ct1Col) {
        this.ct1Col = ct1Col;
    }

    public String getCt2Col() {
        return ct2Col;
    }

    public void setCt2Col(String ct2Col) {
        this.ct2Col = ct2Col;
    }

    public String getLabNoCol() {
        return labNoCol;
    }

    public void setLabNoCol(String labNoCol) {
        this.labNoCol = labNoCol;
    }

    public Item getOrderingCategory() {
        return orderingCategory;
    }

    public void setOrderingCategory(Item orderingCategory) {
        this.orderingCategory = orderingCategory;
    }



    /**
	 * @return the searchingTestId
	 */
	public BigInteger getSearchingTestId() {
		return searchingTestId;
	}

	/**
	 * @param searchingTestId the searchingTestId to set
	 */
	public void setSearchingTestId(BigInteger searchingTestId) {
		this.searchingTestId = searchingTestId;
	}





	/**
	 * @return the searchingLabNo
	 */
	public String getSearchingLabNo() {
		return searchingLabNo;
	}

	/**
	 * @param searchingLabNo the searchingLabNo to set
	 */
	public void setSearchingLabNo(String searchingLabNo) {
		this.searchingLabNo = searchingLabNo;
	}





	// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Inner Classes">
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Converters">
    @FacesConverter(forClass = Client.class)
    public static class ClientControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ClientController controller = (ClientController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "clientController");
            return controller.getClient(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Client) {
                Client o = (Client) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Client.class.getName()});
                return null;
            }
        }

    }

// </editor-fold>
}
