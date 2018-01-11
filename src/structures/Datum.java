package structures;

/**
 * Modelliert ein Datum.
 * Erstellt: 28.02.2005
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class Datum {
    private int tag;
    private int monat;
    private int jahr;
    private boolean vChr;
    
    public Datum (int jahr) {
        if (pruefeDatum(0, 0, jahr)) {
            this.tag = 0;
            this.monat = 0;
            this.jahr = jahr;
            vChr = false;
        } else throw new IllegalArgumentException();
    }
    
    public Datum (int jahr, boolean vChr) {
        if (pruefeDatum(0, 0, jahr)) {
            this.tag = 0;
            this.monat = 0;
            this.jahr = jahr;
            this.vChr = vChr;
        } else throw new IllegalArgumentException();
    }
    
    public Datum (int tag, int monat, int jahr) {
        if (pruefeDatum(tag, monat, jahr)) {
            this.tag = tag;
            this.monat = monat;
            this.jahr = jahr;
            vChr = false;
        } else throw new IllegalArgumentException();
    }
    
    public Datum (int tag, int monat, int jahr, boolean vChr) {
        if (pruefeDatum(tag, monat, jahr)) {
            this.tag = tag;
            this.monat = monat;
            this.jahr = jahr;
            this.vChr = vChr;
        } else throw new IllegalArgumentException();
    }

    public Datum (int monat, int jahr) {
        if (pruefeDatum(0,monat,jahr)) {
            this.jahr = jahr;
            this.tag = 0;
            this.monat = monat;
            vChr = false;
        } else throw new IllegalArgumentException();
    }
    
    public Datum (int monat, int jahr, boolean vChr) {
        if (pruefeDatum(0,monat,jahr)) {
            this.jahr = jahr;
            this.tag = 0;
            this.monat = monat;
            this.vChr = vChr;
        } else throw new IllegalArgumentException();
    }
    
    public int getTag () {
        return this.tag;
    }
    
    public void setTag (int tag) {
        if (pruefeDatum(tag, monat, jahr)) {
            this.tag = tag;
        } else throw new IllegalArgumentException();
    }
    
    public int getMonat () {
        return this.monat;
    }
    
    public void setMonat (int monat) {
        if (pruefeDatum(tag, monat, jahr)) {
            this.monat = monat;
        } else throw new IllegalArgumentException();
    }
    
    public int getJahr () {
        return this.jahr;
    }
    
    public void setJahr (int jahr) {
        if (pruefeDatum(tag, monat, jahr)) {
            this.jahr = jahr;
        } else throw new IllegalArgumentException();
    }
    
    public void changeJahr (int jahre) {
        if (this.jahr == -1) throw new IllegalStateException("Unbekanntes Jahr kann nicht verschoben werden!");
        if (this.vChr) jahre = -jahre;
        this.jahr += jahre;
        if (this.jahr < 0) {
            this.jahr = -this.jahr;
            this.vChr = !this.vChr;
        }
        else if (this.jahr == 0) this.vChr = false;
    }

    public void changeMonat (int monate) {
        if (this.monat == 0) throw new IllegalStateException("Unbekannter Monat kann nicht verschoben werden!");
        this.monat += monate;
        while (this.monat < 1) {
            this.monat += 12;
            if (this.jahr != -1) this.changeJahr(-1);
        }
        while (this.monat > 12) {
            this.monat -= 12;
            if (this.jahr != -1) this.changeJahr(1);
        }
        if (!pruefeDatum(this.tag, this.monat, this.jahr)) {
            if (this.monat == 2) {
                if (this.schaltjahr()) this.tag -= 29;
                else this.tag -= 28;
            } else {
                this.tag = 1;
            }
            this.monat++;
        }
    }

    public void changeTag (int tage) {
        if (this.tag == 0) throw new IllegalStateException("Unbekannter Tag kann nicht verschoben werden!");
        if (tage < 0) {
            tage = -tage;
            while (this.tag <= tage) {
                tage -= this.tag;
                switch (this.monat) {
                    case 5: case 7: case 10: case 12:
                        this.tag = 30;
                        break;
                    case 3:
                        if (schaltjahr(this.jahr)) this.tag = 29;
                        else this.tag = 28;
                        break;
                    default:
                        this.tag = 31;
                }
                this.changeMonat(-1);
            }
            this.tag -= tage;
        } else {
            this.tag += tage;
            while (!pruefeDatum(this.tag,this.monat,this.jahr)) {
                switch (this.monat) {
                    case 4: case 6: case 9: case 11:
                        this.tag -= 30;
                        break;
                    case 2:
                        if (schaltjahr(this.jahr)) this.tag -= 29;
                        else this.tag -= 28;
                        break;
                    default:
                        this.tag -= 31;
                }
                this.changeMonat(1);
            }
        }
    }
    
    public boolean getBC () {
        return this.vChr;
    }
    
    public void setBC (boolean vChr) {
        this.vChr = vChr;
    }
    
    private static boolean pruefeDatum(int tag, int monat, int jahr) {
        if (tag < 0 || tag > 31 || monat < 0 || monat > 12 || jahr < -1 ||
            (monat == 0 && tag != 0) ||
       
       ((monat == 4 || monat == 6 || monat == 9 || monat == 11) &&
             tag == 31) ||
            (monat == 2 && tag > 29) ||
            (monat == 2 && !schaltjahr(jahr) && tag == 29)) {
            return false;
        } else return true;
    }
    
    public static boolean schaltjahr (int jahr) {
        if (jahr % 4 == 0 && jahr % 100 != 0 || jahr % 400 == 0) {
            return true;
        } else return false;
    }
    
    public boolean schaltjahr () {
        return schaltjahr(this.jahr);
    }
    
    public boolean unbekannt () {
        return (tag == 0 && monat == 0 && jahr == -1);
    }
    
    /**
     * Gibt zurock, ob das aktuelle Datum kleiner als das obergebene ist.
     */
    public boolean kleiner (Datum datum) {
        if (datum == null) throw new NullPointerException();
        if (this.jahr == -1 || datum.jahr == -1) throw new IllegalArgumentException();
        int jahr1 = this.jahr, jahr2 = datum.jahr;
        if (this.vChr) jahr1 = -jahr1;
        if (datum.vChr) jahr2 = -jahr2;
        if (jahr1 < jahr2) return true;
        if (jahr1 > jahr2) return false;
        if (this.monat < datum.monat) return true;
        if (this.monat > datum.monat) return false;
        if (this.tag < datum.tag) return true;
        return false;
    }
    
    /**
     * Gibt die Anzahl an Tagen vom aktuellen Datum bis zum obergebenen Datum zurock.
     */
    public int distanz (Datum datum) {
        if (datum == null) throw new NullPointerException();
        if (this.tag == 0 || datum.tag == 0) throw new IllegalArgumentException();
        if (this.equals(datum)) return 0;
        int erg = 0, day = this.tag, mon = this.monat;
        if (this.jahr == -1 || datum.jahr == -1) {
            while (!datum.equals(new Datum(day,mon,datum.jahr,datum.vChr))) {
                if (pruefeDatum(day + 1, mon, -1)) day++;
                else if (pruefeDatum(1, mon + 1, -1)) {
                    day = 1;
                    mon++;
                } else {
                    day = 1;
                    mon = 1;
                }
                erg++;
            }
        } else {
            if (!this.kleiner(datum)) erg = -datum.distanz(this);
            else {
                int year = this.jahr;
                boolean bc = this.vChr;
                while (!datum.equals(new Datum(day,mon,year,bc))) {
                    if (pruefeDatum(day + 1, mon, year)) day++;
                    else if (pruefeDatum(1, mon + 1, year)) {
                        day = 1;
                        mon++;
                    } else if (bc) {
                        if (year == 1) bc = false;
                        day = 1;
                        mon = 1;
                        year--;
                    } else {
                        day = 1;
                        mon = 1;
                        year++;
                    }
                    erg++;
                }
            }
        }
        return erg;
    }
    
    public String toString () {
        if (this.unbekannt()) return "unbekannt";
        String erg = "";
        if (tag != 0) {
            erg += tag + ". ";
        }
        switch(monat) {
            case 1:
                erg += "Januar ";
                break;
            case 2:
                erg += "Februar ";
                break;
            case 3:
                erg += "Morz ";
                break;
            case 4:
                erg += "April ";
                break;
            case 5:
                erg += "Mai ";
                break;
            case 6:
                erg += "Juni ";
                break;
            case 7:
                erg += "Juli ";
                break;
            case 8:
                erg += "August ";
                break;
            case 9:
                erg += "September ";
                break;
            case 10:
                erg += "Oktober ";
                break;
            case 11:
                erg += "November ";
                break;
            case 12:
                erg += "Dezember ";
                break;
            default:
        }
        if (jahr != -1) {
            erg += jahr;
            if (vChr) {
                erg += " v.Chr.";
            }
        }
        return erg;
    }
    
    public boolean equals (Object o) {
        if (o instanceof Datum) return gleich((Datum)o);
        return false;
    }
    
    private boolean gleich (Datum datum) {
        return (this.tag == datum.tag && this.monat == datum.monat && this.jahr == datum.jahr && this.vChr == datum.vChr);
    }
    
    public Datum copy() {
        return new Datum(this.tag, this.monat, this.jahr, this.vChr);
    }
    
}