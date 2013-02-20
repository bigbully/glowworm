package userJavabean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class PersonForDate {

    private Date date;
    private Timestamp timestamp;
    private Time time;
    private Inet4Address inet4Address;
    private BigDecimal bd;
    private BigInteger bi;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Inet4Address getInet4Address() {
        return inet4Address;
    }

    public void setInet4Address(Inet4Address inet4Address) {
        this.inet4Address = inet4Address;
    }

    public BigDecimal getBd() {
        return bd;
    }

    public void setBd(BigDecimal bd) {
        this.bd = bd;
    }

    public BigInteger getBi() {
        return bi;
    }

    public void setBi(BigInteger bi) {
        this.bi = bi;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
