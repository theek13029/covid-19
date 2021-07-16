/*
 * The MIT License
 *
 * Copyright 2019 Dr M H B Ariyaratne<buddhika.ari@gmail.com>.
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
package lk.gov.health.phsp.enums;

/**
 *
 * @author Dr M H B Ariyaratne
 */
public enum WebUserRole {
    System_Administrator("System Administrator"),
    Super_User("Super User"),
    User("User"),
    Pdhs("PDHS"),
    Rdhs("RDHS"),
    Re("Regional Epidemiologist"),
    Moh("MOH"),
    Amoh("AMOH"),
    Phi("PHI"),
    Phm("PHM"),
    Epidemiologist("Epidemiologist"),
    Nurse("Nurse"),
    ChiefEpidemiologist("Midwife"),
    Client("Client"),
    Hospital_Admin("Hospital User"),
    Hospital_User("Hospital Admin"),
    Lab_Consultant("Lab Consultant"),
    Lab_Mo("Lab Medical Officer"),
    Lab_Mlt("MLT"),
    Lab_User("Lab Assistant"),
    Lab_National("Lab National");
    private final String label;

    private WebUserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
