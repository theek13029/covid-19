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

import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lk.gov.health.phsp.entity.Client;
import lk.gov.health.phsp.facade.ClientFacade;
import lk.gov.health.phsp.pojcs.SlNic;
import lk.gov.health.phsp.pojcs.YearMonthDay;

/**
 *
 * @author buddhika
 */
@Named
@ApplicationScoped
public class ClientApplicationController {

    @Inject
    ItemApplicationController itemApplicationController;

    @EJB
    ClientFacade clientFacade;

    /**
     * Creates a new instance of ClientApplicationController
     */
    public ClientApplicationController() {
    }
    
    public void saveClient(Client c){
        if(c==null){
            return;
        }
        if(c.getId()==null){
            clientFacade.create(c);
        }else{
            clientFacade.edit(c);
        }
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
        Long count = clientFacade.countByJpql(jpql, m);
        return !(count == null || count == 0l);
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
        Long count = clientFacade.countByJpql(jpql, m);
        return !(count == null || count == 0l);
    }

    public void ageAndSexFromNic(Client client) {
        if (client == null || client.getPerson() == null || client.getPerson().getNic() == null) {
            return;
        }
        String nic = client.getPerson().getNic();
        SlNic n = new SlNic();
        n.setNic(nic);
        if (n.getDateOfBirth() != null) {
            client.getPerson().setDateOfBirth(n.getDateOfBirth());
        }
        if (n.getSex() != null) {
            if (n.getSex().equalsIgnoreCase("male")) {
                client.getPerson().setSex(itemApplicationController.getMale());
            } else {
                client.getPerson().setSex(itemApplicationController.getFemale());
            }
        }
        client.getPerson().getAgeYears();
        client.getPerson().getAgeMonths();
        client.getPerson().getAgeDays();
        client.getPerson().setDobIsAnApproximation(false);
    }

}
