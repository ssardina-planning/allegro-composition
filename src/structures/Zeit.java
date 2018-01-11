package structures;

/**
 * Modelliert einen Zeitpunkt.
 * Erstellt: 08.03.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class Zeit {
    private Datum tag;
    private Uhrzeit zeit;
    
    public Zeit (Datum datum) {
        if (datum == null) throw new IllegalArgumentException("Kein Datum obergeben!");
        this.tag = datum;
        this.zeit = new Uhrzeit();
    }
    
    public Zeit (Datum datum, Uhrzeit zeit) {
        if (datum == null) throw new IllegalArgumentException("Kein Datum obergeben!");
        if (zeit == null) throw new IllegalArgumentException("Keine Uhrzeit obergeben!");
        this.tag = datum;
        this.zeit = zeit;
    }
    
    public Zeit (int tag, int monat, int jahr, boolean bc) {
        this.tag = new Datum(tag, monat, jahr, bc);
        this.zeit = new Uhrzeit();
    }
    
    public Zeit (int stunde, int minute, int sekunde) {
        this.tag = new Datum(0,0,-1);
        this.zeit = new Uhrzeit(stunde, minute, sekunde);
    }
    
    public Datum getDatum () {
        return this.tag;
    }
    
    public Uhrzeit getUhrzeit () {
        return this.zeit;
    }
    
    public void setDatum (Datum datum) {
        if (datum == null) throw new NullPointerException("Kein Datum obergeben!");
        this.tag = datum;
    }
    
    public void setUhzeit (Uhrzeit zeit) {
        if (zeit == null) throw new NullPointerException("Keine Uhrzeit obergeben!");
        this.zeit = zeit;
    }
    
    public boolean kleiner (Zeit zeit) {
        if (zeit == null) throw new NullPointerException("Keine Zeit obergeben!");
        if (this.tag.kleiner(zeit.tag)) return true;
        if (zeit.tag.kleiner(this.tag)) return false;
        if (this.zeit.kleiner(zeit.zeit)) return true;
        return false;
    }
    
    /**
     * Berechnet die Distanz zwischen zwei Zeitpunkten in Sekunden.
     * @param zeit Zeit, bis zu der die Distanz gemessen werden soll.
     * @return Zeitliche Distanz zwischen den zwei Zeitpunkten in Sekunden.
     */
    public int distanz (Zeit zeit) {
        if (zeit == null) throw new NullPointerException("Keine Zeit obergeben!");
        if (zeit.kleiner(this)) return -zeit.distanz(this);
        if (this.tag.equals(zeit.tag)) return this.zeit.distanz(zeit.zeit);
        int days = this.tag.distanz(zeit.tag);
        if (zeit.zeit.kleiner(this.zeit)) days--;
        return days * 24 * 60 * 60 + this.zeit.distanz(zeit.zeit);
    }
    
    public void verschiebeZeit (int jahre, int monate, int tage, int stunden, int minuten, int sekunden) {
        this.tag.changeJahr(jahre);
        this.tag.changeMonat(monate);
        this.tag.changeTag(tage);
        this.changeStunde(stunden);
        this.changeMinute(minuten);
        this.changeSekunde(sekunden);
    }
    
    public void changeJahr (int jahre) {
        this.tag.changeJahr(jahre);
    }
    
    public void changeMonat (int monate) {
        this.tag.changeMonat(monate);
    }
    
    public void changeTag (int tage) {
        this.tag.changeTag(tage);
    }
    
    public void changeStunde (int stunden) {
        int stunde = this.zeit.getStunde();
        stunde += stunden;
        while (stunde < 0) {
            stunde += 24;
            this.tag.changeTag(-1);
        }
        while (stunde > 23) {
            stunde -= 24;
            this.tag.changeTag(1);
        }
        this.zeit.setStunde(stunde);
    }
    
    public void changeMinute (int minuten) {
        int minute = this.zeit.getMinute();
        minute += minuten;
        while (minute < 0) {
            minute += 60;
            this.changeStunde(-1);
        }
        while (minute > 59) {
            minute -= 60;
            this.changeStunde(1);
        }
        this.zeit.setMinute(minute);
    }

    public void changeSekunde (int sekunden) {
        int sekunde = this.zeit.getSekunde();
        sekunde += sekunden;
        while (sekunde < 0) {
            sekunde += 60;
            this.changeMinute(-1);
        }
        while (sekunde > 59) {
            sekunde -= 60;
            this.changeMinute(1);
        }
        this.zeit.setSekunde(sekunde);
    }
    
    public String toString () {
        return tag.toString() + ". " + zeit.toString() + " Uhr.";
    }
    
    public boolean equals (Object o) {
        if (!(o instanceof Zeit)) return false;
        return gleich((Zeit)o);
    }
    
    private boolean gleich (Zeit zeit) {
        return (this.tag.equals(zeit.tag) && this.zeit.equals(zeit.zeit));
    }
}