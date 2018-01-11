package structures;

/**
 * Modelliert eine Uhrzeit.
 * Erstellt: 08.03.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class Uhrzeit {
    private int stunde;
    private int minute;
    private int sekunde;
    
    public Uhrzeit () {
        this.stunde = 0;
        this.minute = 0;
        this.sekunde = 0;
    }
    
    public Uhrzeit (int stunde) {
        if (!pruefeZeit(stunde, 0, 0)) throw new IllegalArgumentException();
        this.stunde = stunde;
        this.minute = 0;
        this.sekunde = 0;
    }
    
    public Uhrzeit (int stunde, int minute) {
        if (!pruefeZeit(stunde, minute, 0)) throw new IllegalArgumentException();
        this.stunde = stunde;
        this.minute = minute;
        this.sekunde = 0;
    }
    
    public Uhrzeit (int stunde, int minute, int sekunde) {
        if (!pruefeZeit(stunde, minute, sekunde)) throw new IllegalArgumentException();
        this.stunde = stunde;
        this.minute = minute;
        this.sekunde = sekunde;
    }

    public int getStunde () {
        return this.stunde;
    }
    
    public void setStunde (int stunde) {
        if (stunde < 0 || stunde > 23) throw new IllegalArgumentException("Keine goltige Stunde!");
        this.stunde = stunde;
    }
    
    public int getMinute () {
        return this.minute;
    }
    
    public void setMinute (int minute) {
        if (minute < 0 || minute > 59) throw new IllegalArgumentException("Keine goltige Minute!");
        this.minute = minute;
    }

    public int getSekunde () {
        return this.sekunde;
    }

    public void setSekunde (int sekunde) {
        if (sekunde < 0 || sekunde > 59) throw new IllegalArgumentException("Keine goltige Sekunde!");
        this.sekunde = sekunde;
    }
    
    public void changeStunde (int stunden) {
        this.stunde += stunden;
        while (this.stunde < 0) this.stunde += 24;
        while (this.stunde > 23) this.stunde -= 24;
    }

    public void changeMinute (int minuten) {
        this.minute += minuten;
        while (this.minute < 0) {
            this.minute += 60;
            this.changeStunde(-1);
        }
        while (this.minute > 59) {
            this.minute -= 60;
            this.changeStunde(1);
        }
    }

    public void changeSekunde (int sekunden) {
        this.sekunde += sekunden;
        while (this.sekunde < 0) {
            this.sekunde += 60;
            this.changeMinute(-1);
        }
        while (this.sekunde > 59) {
            this.sekunde -= 60;
            this.changeMinute(1);
        }
    }
    
    private static boolean pruefeZeit (int stunde, int minute, int sekunde) {
        return (stunde >= 0 && stunde < 24 && minute >= 0 && minute < 60 && sekunde >= 0 && sekunde < 60);
    }
    
    public boolean kleiner (Uhrzeit zeit) {
        if (zeit == null) throw new NullPointerException("Keine Uhrzeit obergeben!");
        if (this.stunde < zeit.stunde) return true;
        if (this.stunde > zeit.stunde) return false;
        if (this.minute < zeit.minute) return true;
        if (this.minute > zeit.minute) return false;
        if (this.sekunde < zeit.sekunde) return true;
        return false;
    }
    
    public int distanz (Uhrzeit zeit) {
        if (zeit == null) throw new NullPointerException("Keine Uhrzeit obergeben!");
        int hours = zeit.stunde - this.stunde, mins = zeit.minute - this.minute, secs = zeit.sekunde - this.sekunde;
        if (secs < 0) {
            secs += 60;
            mins--;
        }
        if (mins < 0) {
            mins += 60;
            hours--;
        }
        if (hours < 0) hours += 24;
        mins += hours * 60;
        secs += mins * 60;
        return secs;
    }
    
    public String toString () {
        String erg = "", stunde = "", minute = "", sekunde = "";
        if (this.stunde < 10) stunde += "0";
        if (this.minute < 10) minute += "0";
        if (this.sekunde < 10) sekunde += "0";
        stunde += this.stunde;
        minute += this.minute;
        sekunde += this.sekunde;
        erg += stunde + ":" + minute + ":" + sekunde;
        return erg;
    }
    
    public boolean equals (Object o) {
        if (o instanceof Uhrzeit) return gleich((Uhrzeit)o);
        return false;
    }
    
    private boolean gleich (Uhrzeit zeit) {
        return (this.stunde == zeit.stunde && this.minute == zeit.minute && this.sekunde == zeit.sekunde);
    }
}