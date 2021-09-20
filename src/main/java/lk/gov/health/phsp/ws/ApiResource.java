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
package lk.gov.health.phsp.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import lk.gov.health.phsp.bean.AnalysisController;
import lk.gov.health.phsp.bean.ApiRequestApplicationController;
import lk.gov.health.phsp.bean.ApplicationController;
import lk.gov.health.phsp.bean.AreaApplicationController;
import lk.gov.health.phsp.bean.CommonController;
import lk.gov.health.phsp.bean.InstitutionApplicationController;
import lk.gov.health.phsp.bean.ItemApplicationController;
import lk.gov.health.phsp.bean.StoredQueryResultController;
import lk.gov.health.phsp.bean.WebUserApplicationController;
import lk.gov.health.phsp.bean.WebUserController;
import lk.gov.health.phsp.entity.ApiRequest;
import lk.gov.health.phsp.entity.Area;
import lk.gov.health.phsp.entity.Client;
import lk.gov.health.phsp.entity.ClientEncounterComponentItem;
import lk.gov.health.phsp.entity.Encounter;
import lk.gov.health.phsp.entity.Institution;
import lk.gov.health.phsp.entity.Item;
import lk.gov.health.phsp.entity.QueryComponent;
import lk.gov.health.phsp.entity.Relationship;
import lk.gov.health.phsp.entity.WebUser;
import lk.gov.health.phsp.enums.AreaType;
import lk.gov.health.phsp.enums.EncounterType;
import lk.gov.health.phsp.enums.InstitutionType;
import lk.gov.health.phsp.enums.RelationshipType;
import lk.gov.health.phsp.enums.WebUserRole;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author buddhika
 */
@Path("")
@Dependent
public class ApiResource {

    @Context
    private UriInfo context;

    @Inject
    AreaApplicationController areaApplicationController;
    @Inject
    InstitutionApplicationController institutionApplicationController;
    @Inject
    AnalysisController analysisController;
    @Inject
    ItemApplicationController itemApplicationController;
    @Inject
    ApplicationController applicationController;
    @Inject
    StoredQueryResultController storedQueryResultController;
    @Inject
    WebUserController webUserController;
    @Inject
    ApiRequestApplicationController apiRequestApplicationController;
    @Inject
    WebUserApplicationController webUserApplicationController;

    /**
     * Creates a new instance of GenericResource
     */
    public ApiResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("name") String name,
            @QueryParam("year") String year,
            @QueryParam("month") String month,
            @QueryParam("institute_id") String instituteId,
            @QueryParam("id") String id,
            @QueryParam("username") String username,
            @QueryParam("password") String password,
            @QueryParam("test_number") String test_number,
            @QueryParam("referring_lab_id") String referring_lab_id,
            @QueryParam("client_name") String client_name,
            @QueryParam("client_address") String client_address,
            @QueryParam("client_phone_number") String client_phone_number,
            @QueryParam("client_nic") String client_nic,
            @QueryParam("client_passport_number") String client_passport_number,
            @QueryParam("client_age_in_years") String client_age_in_years,
            @QueryParam("client_age_in_months") String client_age_in_months,
            @QueryParam("client_age_in_days") String client_age_in_days,
            @QueryParam("client_gender") String client_gender,
            @QueryParam("client_citizenship") String client_citizenship,
            @QueryParam("ordering_category_id") String ordering_category_id,
            @QueryParam("moh_id") String moh_id,
            @QueryParam("district_id") String district_id,
            @QueryParam("gn_area_id") String gn_area_id,
            @QueryParam("phi_area_id") String phi_area_id,
            @QueryParam("comments") String comments,
            @Context HttpServletRequest requestContext,
            @Context SecurityContext context) {

        String ipadd = requestContext.getHeader("X-FORWARDED-FOR");

        JSONObject jSONObjectOut;
        if (name == null || name.trim().equals("")) {
            jSONObjectOut = errorMessageInstruction();
        } else {
            switch (name) {
                case "get_lab_list":
                    jSONObjectOut = labList();
                    break;
                case "get_moh_list":
                    jSONObjectOut = mohList();
                    break;
                case "get_ordering_category_list":
                    jSONObjectOut = orderingCategoryList();
                    break;
                case "get_province_list":
                    jSONObjectOut = provinceList();
                    break;
                case "get_district_list":
                    jSONObjectOut = districtList();
                    break;
                case "get_institutes_list":
                case "get_institute_list":
                    jSONObjectOut = instituteList();
                    break;
                case "get_gender_list":
                    jSONObjectOut = genderList();
                    break;
                case "get_citizenship_list":
                    jSONObjectOut = citizenshipList();
                    break;
                case "get_gn_area_list":
                    jSONObjectOut = gnAreaList();
                    break;
                case "get_institutes_registered_list":
                    jSONObjectOut = phiAreaList();
                    break;
                case "get_vaccination_statuses":
                    jSONObjectOut = vaccinationStatusesList();
                    break;
                case "get_symptomatic_statuses":
                    jSONObjectOut = symptomaticStatusesList();
                    break;
                case "submit_pcr_request":
                    jSONObjectOut = submitPcrRequest(username,
                            password,
                            test_number,
                            referring_lab_id,
                            client_name,
                            client_address,
                            client_phone_number,
                            client_nic,
                            client_passport_number,
                            client_age_in_years,
                            client_age_in_months,
                            client_age_in_days,
                            client_gender,
                            client_citizenship,
                            ordering_category_id,
                            moh_id,
                            district_id,
                            gn_area_id,
                            phi_area_id,
                            comments);
                    break;
                case "request_test_result":
                    jSONObjectOut = requestTestResult();
                    break;
                case "submit_pcr_result":
                    jSONObjectOut = submitPcrResult();
                    break;
                case "submit_rat_result":
                    jSONObjectOut = submitRatResult();
                    break;

                default:
                    jSONObjectOut = errorMessage();
            }
        }

        String json = jSONObjectOut.toString();
        return json;
    }

    private JSONObject submitPcrRequest(String username,
            String password,
            String test_number,
            String referring_lab_id,
            String client_name,
            String client_address,
            String client_phone_number,
            String client_nic,
            String client_passport_number,
            String client_age_in_years,
            String client_age_in_months,
            String client_age_in_days,
            String client_gender,
            String client_citizenship,
            String ordering_category_id,
            String moh_id,
            String district_id,
            String gn_area_id,
            String phi_area_id,
            String comments) {

        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();

        Integer intAgeInYears = 0;
        Integer intAgeInMonths = 0;
        Integer intAgeInDays = 0;
        Area cDistrict;
        Area cMoh;
        Area cGn;
        Area cPhi;
        
        WebUser wu;

        Item cGender=null;
        Item cCitizenship = null;
        Item eOrderingCategory = null;
        
        wu = webUserApplicationController.getWebUser(username, password);
        
        if(wu==null){
            return errorMessageLogin();
        }
        
        if(wu.getI)
        
        cDistrict = areaApplicationController.getArea(CommonController.getLongValue(district_id));
        cMoh = areaApplicationController.getArea(CommonController.getLongValue(moh_id));
        cGn = areaApplicationController.getArea(CommonController.getLongValue(gn_area_id));
        cPhi = areaApplicationController.getArea(CommonController.getLongValue(phi_area_id));
        eOrderingCategory = itemApplicationController.findItemById(CommonController.getLongValue(ordering_category_id));
        
        if (client_name == null || client_name.trim().equals("")) {
            return errorMessageNoClientName();
        }
        
        if(client_gender==null || client_gender.trim().equals("")){
            return errorMessageNoGender();
        }else{
            if(client_gender.toLowerCase().contains("f")){
               cGender = itemApplicationController.getFemale();
            }else{
               cGender = itemApplicationController.getMale();
            }
        }
        
        if(client_citizenship!=null ){
            if(client_citizenship.toLowerCase().contains("f")){
                cCitizenship = itemApplicationController.findItemByCode("citizenship_foreign");
            }else{
                cCitizenship = itemApplicationController.findItemByCode("citizenship_local");
            }
        }
        
        if (client_age_in_years != null || !client_name.trim().equals("")) {
            intAgeInYears = CommonController.getIntegerValue(client_age_in_years);
        } else if (client_age_in_months != null || !client_name.trim().equals("")) {
            intAgeInMonths = CommonController.getIntegerValue(client_age_in_months);
        } else if (client_age_in_days != null || !client_name.trim().equals("")) {
            intAgeInDays = CommonController.getIntegerValue(client_age_in_days);
        } else {
            return errorMessageNoAge();
        }
        if (intAgeInYears == null || intAgeInYears < 1) {
            if (intAgeInMonths == null || intAgeInMonths < 1) {
                if (intAgeInDays == null || intAgeInDays < 1) {
                    return errorMessageNoAge();
                }
            }
        }
        
        

        Client c = new Client();
        c.getPerson().setName(client_name);
        c.getPerson().setAgeYears(intAgeInYears);
        c.getPerson().setAgeMonths(intAgeInMonths);
        c.getPerson().setAgeDays(intAgeInDays);
        c.getPerson().setPhone1(client_phone_number);
        c.getPerson().setAddress(client_address);
        c.getPerson().setDistrict(cDistrict);
        c.getPerson().setMohArea(cMoh);
        c.getPerson().setGnArea(cGn);
        c.getPerson().setPhiArea(cPhi);
        c.getPerson().setSex(cGender);
        c.getPerson().setCitizenship(cCitizenship);
        if (client_nic == null) {
            client_nic = "";
        } else {
            client_nic = client_nic.trim().toUpperCase();
        }
        if (client_passport_number == null) {
            client_passport_number = "";
        } else {
            client_passport_number = client_passport_number.trim().toUpperCase();
        }

        if (!client_nic.equals("") && !client_passport_number.equals("")) {
            c.getPerson().setNic(client_nic);
            c.getPerson().setPassportNumber(client_passport_number);
        } else if (client_nic.equals("") && !client_passport_number.equals("")) {
            c.getPerson().setNic(client_passport_number);
            c.getPerson().setPassportNumber(client_passport_number);
        } else if (!client_nic.equals("") && client_passport_number.equals("")) {
            c.getPerson().setNic(client_nic);
            c.getPerson().setPassportNumber(client_nic);
        }
        
        Encounter e = new Encounter();
        e.setPcrTestType(itemApplicationController.getPcr());
        e.setClient(c);
        e.setComments(comments);
        e.setCreatedAt(new Date());
        e.setPcrOrderingCategory(eOrderingCategory);
        

        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject requestTestResult() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();

        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject submitPcrResult() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();

        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject submitRatResult() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();

        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject districtList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Area> ds = areaApplicationController.getAllAreas(AreaType.District);
        for (Area a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("district_id", a.getId());
            ja.put("district_code", a.getCode());
            ja.put("district_name", a.getName());
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject instituteList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Institution> ds = institutionApplicationController.getHospitals();
        for (Institution a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("institute_id", a.getId());
            ja.put("institute_code", a.getCode());
            ja.put("name", a.getName());
            ja.put("hin", a.getPoiNumber());
            ja.put("latitude", a.getCoordinate().getLatitude());
            ja.put("longitude", a.getCoordinate().getLongitude());
            ja.put("address", a.getAddress());
            ja.put("type", a.getInstitutionType());
            ja.put("type_label", a.getInstitutionType().getLabel());
            if (a.getEditedAt() != null) {
                ja.put("edited_at", a.getEditedAt());
            } else {
                ja.put("edited_at", a.getCreatedAt());
            }
            if (a.getProvince() != null) {
                ja.put("province_id", a.getProvince().getId());
            }
            if (a.getDistrict() != null) {
                ja.put("district_id", a.getDistrict().getId());
            }
            ja.put("child_institutions", Get_Child_Institutions(a));
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject genderList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Item> ds = itemApplicationController.getSexes();
        for (Item a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("gender_id", a.getId());
            ja.put("gender_name", a.getCode());
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject vaccinationStatusesList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Item> ds = itemApplicationController.getVaccinationStatuses();
        for (Item a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("vaccination_status_id", a.getId());
            ja.put("vaccination_status_name", a.getCode());
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject symptomaticStatusesList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Item> ds = itemApplicationController.getSymptomaticStatuses();
        for (Item a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("symptomatic_status_id", a.getId());
            ja.put("symptomatic_status_name", a.getCode());
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject gnAreaList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Area> ds = areaApplicationController.getAllAreas(AreaType.GN);
        for (Area a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("gn_id", a.getId());
            ja.put("gn_code", a.getCode());
            ja.put("gn_name", a.getCode());
            if (a.getDistrict() != null) {
                ja.put("district_name", a.getDistrict().getName());
                ja.put("district_id", a.getDistrict().getId());
            }
            if (a.getDsd() != null) {
                ja.put("dsd_name", a.getDsd().getName());
                ja.put("dsd_id", a.getDsd().getId());
            }
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject phiAreaList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Area> ds = areaApplicationController.getAllAreas(AreaType.GN);
        for (Area a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("phi_id", a.getId());
            ja.put("phi_name", a.getCode());
            if (a.getDistrict() != null) {
                ja.put("district_name", a.getDistrict().getName());
                ja.put("district_id", a.getDistrict().getId());
            }
            if (a.getMoh() != null) {
                ja.put("moh_name", a.getMoh().getName());
                ja.put("moh_id", a.getMoh().getId());
            }
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject citizenshipList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Item> ds = itemApplicationController.getCitizenships();
        for (Item a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("citizenship_id", a.getId());
            ja.put("citizenship_name", a.getName());
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject provinceList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Area> ds = areaApplicationController.getAllAreas(AreaType.Province);
        for (Area a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("province_id", a.getId());
            ja.put("province_code", a.getCode());
            ja.put("province_name", a.getName());
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject labList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<InstitutionType> its = new ArrayList<>();
        its.add(InstitutionType.Lab);
        List<Institution> ds = institutionApplicationController.findInstitutions(its);
        for (Institution a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("lab_id", a.getId());
            ja.put("lab_name", a.getName());
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject orderingCategoryList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Item> ds = itemApplicationController.getCovidTestOrderingCategories();
        for (Item a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("ordering_category_id", a.getId());
            ja.put("ordering_category_name", a.getName());
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject mohList() {
        JSONObject jSONObjectOut = new JSONObject();
        JSONArray array = new JSONArray();
        List<Area> ds = areaApplicationController.getAllAreas(AreaType.MOH);
        for (Area a : ds) {
            JSONObject ja = new JSONObject();
            ja.put("moh_id", a.getId());
            ja.put("moh_name", a.getName());
            if (a.getDistrict() != null) {
                ja.put("district_name", a.getDistrict().getName());
                ja.put("district_id", a.getDistrict().getName());
            }
            array.put(ja);
        }
        jSONObjectOut.put("data", array);
        jSONObjectOut.put("status", successMessage());
        return jSONObjectOut;
    }

    private JSONObject successMessage() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 200);
        jSONObjectOut.put("type", "success");
        return jSONObjectOut;
    }

    private JSONObject errorMessage() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 400);
        jSONObjectOut.put("type", "error");
        String e = "Parameter name is not recognized.";
        jSONObjectOut.put("message", "Parameter name is not recognized.");
        return jSONObjectOut;
    }

    private JSONObject errorMessageNoClientName() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 400);
        jSONObjectOut.put("type", "error");
        String e = "A client name is required.";
        jSONObjectOut.put("message", e);
        return jSONObjectOut;
    }

    private JSONObject errorMessageNoAge() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 400);
        jSONObjectOut.put("type", "error");
        String e = "An age is required.";
        jSONObjectOut.put("message", e);
        return jSONObjectOut;
    }

    private JSONObject errorMessageNoIndicator() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 411);
        jSONObjectOut.put("type", "error");
        jSONObjectOut.put("message", "Indicator NOT recognized.");
        return jSONObjectOut;
    }
    
    private JSONObject errorMessageLogin() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 411);
        jSONObjectOut.put("type", "error");
        jSONObjectOut.put("message", "Your username and password combination is wrong.");
        return jSONObjectOut;
    }

    private JSONObject errorMessageNoGender() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 402);
        jSONObjectOut.put("type", "error");
        jSONObjectOut.put("message", "Gender is not provided.");
        return jSONObjectOut;
    }

    private JSONObject errorMessageNoInstituteId() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 402);
        jSONObjectOut.put("type", "error");
        jSONObjectOut.put("message", "Parameter institute_id is not provided or not recognized.");
        return jSONObjectOut;
    }

    private JSONObject errorMessageNoInstituteFound() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 403);
        jSONObjectOut.put("type", "error");
        jSONObjectOut.put("message", "Parameter institute_id is not found.");
        return jSONObjectOut;
    }

    private JSONObject errorMessageInstruction() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 400);
        jSONObjectOut.put("type", "error");
        jSONObjectOut.put("message", "You must provide a value for the parameter name.");
        return jSONObjectOut;
    }

    private JSONObject errorMessageNoId() {
        JSONObject jSONObjectOut = new JSONObject();
        jSONObjectOut.put("code", 410);
        jSONObjectOut.put("type", "error");
        jSONObjectOut.put("message", "The ID provided is not found.");
        return jSONObjectOut;
    }

    private String Get_Child_Institutions(Institution institution) {
        String childInstitions = null;

        if (institution != null) {
            List<Institution> instList = institutionApplicationController.findChildrenInstitutions(institution);

            for (Institution i_ : instList) {
                if (childInstitions == null) {
                    childInstitions = institution.getCode() + ":" + i_.getCode();
                } else {
                    childInstitions += "^" + institution.getCode() + ":" + i_.getCode();
                }
            }
        }
        return childInstitions;
    }
}
