package lk.gov.health.phsp.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import lk.gov.health.phsp.entity.Institution;
import lk.gov.health.phsp.facade.PreferenceFacade;

@Named
@SessionScoped
public class PreferenceController implements Serializable {

    @EJB
    private PreferenceFacade ejbFacade;

    @Inject
    private WebUserController webUserController;
    @Inject
    private CommonController commonController;
    @Inject
    private UserTransactionController userTransactionController;

    /*
    Application Preferences
     */
    private String positiveRatSmsTemplate;
    private String negativePcrSmsTemplate;
    private String negativeRatSmsTemplate;
    private String positivePcrSmsTemplate;
    private String sentByErrorSmsTemplate;
    private String positiveSmsTemplate;
    private String negativeSmsTemplate;
    private String limsKey;
    private String pharmacyBaseUrl;
    private String pharmacyKey;

    /*
    Institution Preferences
     */
    private String labReportHtml;
    private String labApprovalSteps;

    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
    private PreferenceFacade getFacade() {
        return ejbFacade;
    }

    public String findPreferanceValue(String name) {
        Preference p = findPreferance(name);
        if (p != null) {
            return p.getLongTextValue();
        } else {
            return "";
        }
    }

    public String findPreferanceValue(String name, Institution ins) {
        Preference p = findPreferance(name, ins);
        if (p != null) {
            return p.getLongTextValue();
        } else {
            return "";
        }
    }

    public String toManagePreferences() {
        loadPreferences();
        return "/systemAdmin/preferences";
    }

    public String toManagePreferencesInstitution() {
        loadPreferencesInstitution();
        return "/insAdmin/preferences";
    }

    public void loadPreferences() {
        System.out.println("loadPreferences");
        positiveRatSmsTemplate = findPreferanceValue("positiveRatSmsTemplate");
        negativePcrSmsTemplate = findPreferanceValue("negativePcrSmsTemplate");
        negativeRatSmsTemplate = findPreferanceValue("negativeRatSmsTemplate");
        positivePcrSmsTemplate = findPreferanceValue("positivePcrSmsTemplate");
        sentByErrorSmsTemplate = findPreferanceValue("sentByErrorSmsTemplate");
        positiveSmsTemplate = findPreferanceValue("positiveSmsTemplate");
        negativeSmsTemplate = findPreferanceValue("negativeSmsTemplate");
        limsKey = findPreferanceValue("limsKey");
        pharmacyBaseUrl = findPreferanceValue("pharmacyBaseUrl");
        pharmacyKey = findPreferanceValue("pharmacyKey");
    }

    public void loadPreferencesInstitution() {
        labApprovalSteps = findPreferanceValue("labApprovalSteps", webUserController.getLoggedUser().getInstitution());
        labReportHtml = findPreferanceValue("labReportHeader", webUserController.getLoggedUser().getInstitution());
    }

    public void savePreferences() {
        savePreference("positiveRatSmsTemplate", positiveRatSmsTemplate);
        savePreference("negativePcrSmsTemplate", negativePcrSmsTemplate);
        savePreference("negativeRatSmsTemplate", negativeRatSmsTemplate);
        savePreference("positivePcrSmsTemplate", positivePcrSmsTemplate);
        savePreference("sentByErrorSmsTemplate", sentByErrorSmsTemplate);
        savePreference("positiveSmsTemplate", positiveSmsTemplate);
        savePreference("negativeSmsTemplate", negativeSmsTemplate);
        savePreference("limsKey", limsKey);
        savePreference("pharmacyBaseUrl", pharmacyBaseUrl);
        savePreference("pharmacyKey", pharmacyKey);
    }

    public void savePreferencesInstitution() {
        savePreference("labApprovalSteps", labApprovalSteps);
        savePreference("labReportHeader", labReportHtml);
    }

    public Preference findPreferance(String name) {
        if (name == null) {
            return null;
        }
        String j = "select p "
                + " from Preference p "
                + " where p.applicationPreferance=:ap "
                + " and p.name=:n";
        Map m = new HashMap();
        m.put("ap", true);
        m.put("n", name);
        Preference p = getFacade().findFirstByJpql(j, m);
        if (p == null) {
            p = new Preference();
            p.setApplicationPreferance(true);
            p.setName(name);
            savePreference(p);
        }
        return p;
    }

    public Preference findPreferance(String name, Institution ins) {
        if (name == null) {
            return null;
        }
        if (ins == null) {
            return null;
        }
        String j = "select p "
                + " from Preference p "
                + " where p.applicationPreferance=:ap "
                + " and p.name=:n "
                + " and p.institution=:ins ";
        Map m = new HashMap();
        m.put("ap", false);
        m.put("n", name);
        m.put("ins", ins);
        Preference p = getFacade().findFirstByJpql(j, m);
        if (p == null) {
            p = new Preference();
            p.setApplicationPreferance(false);
            p.setInstitution(ins);
            p.setName(name);
            savePreference(p);
        }
        return p;
    }

    public void savePreference(String name, String value) {
        Preference p = findPreferance(name);
        if (p != null) {
            p.setLongTextValue(value);
            savePreference(p);
        }
    }

    public void savePreference(String name, Institution ins, String value) {
        Preference p = findPreferance(name, ins);
        if (p != null) {
            p.setLongTextValue(value);
            savePreference(p);
        }
    }

    public void savePreference(Preference p) {
        if (p == null) {
            return;
        }
        if (p.getId() == null) {
            p.setCreatedAt(new Date());
            p.setCreatedBy(webUserController.getLoggedUser());
            getFacade().create(p);
        } else {
            p.setLastEditBy(webUserController.getLoggedUser());
            p.setLastEditeAt(new Date());
            getFacade().edit(p);
        }
    }

    public String getPositiveRatSmsTemplate() {
        if (positiveRatSmsTemplate == null) {
            loadPreferences();
        }
        return positiveRatSmsTemplate;
    }

    public void setPositiveRatSmsTemplate(String positiveRatSmsTemplate) {
        this.positiveRatSmsTemplate = positiveRatSmsTemplate;
    }

    public String getNegativePcrSmsTemplate() {
        if (negativePcrSmsTemplate == null) {
            loadPreferences();
        }
        return negativePcrSmsTemplate;
    }

    public void setNegativePcrSmsTemplate(String negativePcrSmsTemplate) {
        this.negativePcrSmsTemplate = negativePcrSmsTemplate;
    }

    public String getNegativeRatSmsTemplate() {
        if (negativeRatSmsTemplate == null) {
            loadPreferences();
        }
        return negativeRatSmsTemplate;
    }

    public void setNegativeRatSmsTemplate(String negativeRatSmsTemplate) {
        this.negativeRatSmsTemplate = negativeRatSmsTemplate;
    }

    public String getPositivePcrSmsTemplate() {
        if (positivePcrSmsTemplate == null) {
            loadPreferences();
        }
        return positivePcrSmsTemplate;
    }

    public void setPositivePcrSmsTemplate(String positivePcrSmsTemplate) {
        this.positivePcrSmsTemplate = positivePcrSmsTemplate;
    }

    public String getSentByErrorSmsTemplate() {
        if (sentByErrorSmsTemplate == null) {
            loadPreferences();
        }
        return sentByErrorSmsTemplate;
    }

    public void setSentByErrorSmsTemplate(String sentByErrorSmsTemplate) {
        this.sentByErrorSmsTemplate = sentByErrorSmsTemplate;
    }

    public String getPositiveSmsTemplate() {
        if (positiveSmsTemplate == null) {
            loadPreferences();
        }
        return positiveSmsTemplate;
    }

    public void setPositiveSmsTemplate(String positiveSmsTemplate) {
        this.positiveSmsTemplate = positiveSmsTemplate;
    }

    public String getNegativeSmsTemplate() {
        if (negativeSmsTemplate == null) {
            loadPreferences();
        }
        return negativeSmsTemplate;
    }

    public void setNegativeSmsTemplate(String negativeSmsTemplate) {
        this.negativeSmsTemplate = negativeSmsTemplate;
    }

    public String getLimsKey() {
        return limsKey;
    }

    public void setLimsKey(String limsKey) {
        this.limsKey = limsKey;
    }

    public String getPharmacyBaseUrl() {
        return pharmacyBaseUrl;
    }

    public void setPharmacyBaseUrl(String pharmacyBaseUrl) {
        this.pharmacyBaseUrl = pharmacyBaseUrl;
    }

    public String getPharmacyKey() {
        return pharmacyKey;
    }

    public void setPharmacyKey(String pharmacyKey) {
        this.pharmacyKey = pharmacyKey;
    }

    public PreferenceFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PreferenceFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public void setWebUserController(WebUserController webUserController) {
        this.webUserController = webUserController;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    public UserTransactionController getUserTransactionController() {
        return userTransactionController;
    }

    public void setUserTransactionController(UserTransactionController userTransactionController) {
        this.userTransactionController = userTransactionController;
    }

    public String getLabReportHtml() {
        if(labReportHtml==null){
            loadPreferencesInstitution();
        }
        return labReportHtml;
    }

    public void setLabReportHtml(String labReportHtml) {
        this.labReportHtml = labReportHtml;
    }

    public String getLabApprovalSteps() {
        if(labApprovalSteps==null){
            loadPreferencesInstitution();
        }
        return labApprovalSteps;
    }

    public void setLabApprovalSteps(String labApprovalSteps) {
        this.labApprovalSteps = labApprovalSteps;
    }

    
    
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Converters">
    @FacesConverter(forClass = Preference.class)
    public static class PreferenceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PreferenceController controller = (PreferenceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "preferenceController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            try {
                key = Long.valueOf(value);
            } catch (NumberFormatException e) {
                key = 0l;
            }
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
            if (object instanceof Preference) {
                Preference o = (Preference) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Preference.class.getName()});
                return null;
            }
        }

    }

    // </editor-fold>
}
