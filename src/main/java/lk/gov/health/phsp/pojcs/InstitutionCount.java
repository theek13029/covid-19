/*
 * The MIT License
 *
 * Copyright 2020 buddhika.
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
package lk.gov.health.phsp.pojcs;

import java.util.Date;
import lk.gov.health.phsp.entity.Area;
import lk.gov.health.phsp.entity.Institution;
import lk.gov.health.phsp.entity.Item;
import lk.gov.health.phsp.enums.EncounterType;

/**
 *
 * @author buddhika
 */
public class InstitutionCount {

    private Institution institution;
    private Area area;
    private Long count;
    private Date date;
    private EncounterType encounerType;
    private Item item;
    private Item itemValue;

    public InstitutionCount(Long count, Institution institution, Date date, EncounterType encounerType) {
        this.institution = institution;
        this.count = count;
        this.date = date;
        this.encounerType = encounerType;
    }

    public InstitutionCount(Long count, Institution institution, Date date, EncounterType encounerType, Item item, Item itemValue) {
        this.institution = institution;
        this.count = count;
        this.date = date;
        this.encounerType = encounerType;
        this.item = item;
        this.itemValue = itemValue;
    }

    
    
    public InstitutionCount(Long count, Area area, Date date, EncounterType encounerType) {
        this.area = area;
        this.count = count;
        this.date = date;
        this.encounerType = encounerType;
    }

    
    


    public InstitutionCount(Institution institution, Long count) {
        this.institution = institution;
        this.count = count;
    }
    

    public InstitutionCount() {
    }
    
    
    

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EncounterType getEncounerType() {
        return encounerType;
    }

    public void setEncounerType(EncounterType encounerType) {
        this.encounerType = encounerType;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItemValue() {
        return itemValue;
    }

    public void setItemValue(Item itemValue) {
        this.itemValue = itemValue;
    }
    
    
    
}
