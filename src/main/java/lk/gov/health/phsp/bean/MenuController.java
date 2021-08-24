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
import javax.inject.Inject;
import lk.gov.health.phsp.bean.util.JsfUtil;
import lk.gov.health.phsp.entity.UserPrivilege;
import lk.gov.health.phsp.enums.Privilege;

/**
 *
 * @author buddhika
 */
@Named
@SessionScoped
public class MenuController implements Serializable {

    @Inject
    private WebUserController webUserController;
    
    /**
     * Creates a new instance of MenuController
     */
    public MenuController() {
    }
    
    public String toReportsIndex() {
        switch (webUserController.getLoggedUser().getWebUserRoleLevel()) {
            case Regional:
                return "/regional/reports_index";
            case National:
                return "/national/reports_index";
            case Hospital:
                return "/hospital/reports_index";
            case Lab:
                return "/lab/reports_index";
            case National_Lab:
                return "/national/lab_reports_index";
            case Moh:
                return "/moh/reports_index";
            case Provincial:
                return "/provincial/reports_index";
            default:
                return "";
        }
    }
    
    public String toAdministrationIndex() {
        boolean privileged = false;
        for(UserPrivilege up:webUserController.loggedUserPrivileges){
            if(up.getPrivilege()==Privilege.Institution_Administration){
                privileged=true;
            }
            if(up.getPrivilege()==Privilege.System_Administration){
                privileged=true;
            }
        }
        if(!privileged){
            JsfUtil.addErrorMessage("You are NOT autherized");
            return "";
        }
        switch (webUserController.getLoggedUser().getWebUserRoleLevel()) {
            case Regional:
                return "/regional/admin/index";
            case National:
                return "/national/admin/index";
            case Hospital:
                return "/hospital/admin/index";
            case Lab:
                return "/lab/admin/index";
            case National_Lab:
                return "/national/admin/index";
            case Moh:
                return "/moh/admin/index";
            case Provincial:
                return "/provincial/admin/index";
            default:
                return "";
        }
    }
    
}
