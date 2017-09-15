/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.webcohesion.ofx4j.io.DefaultStringConversion;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author roger
 */
public class BBOFXStringConversation extends DefaultStringConversion {

    private TimeZone LocalTimeZone = TimeZone.getTimeZone("BRT");

    @Override
    protected String formatTime(Time time) {
        // todo formtar de acordo com a regra do BB
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("BRT"));
        calendar.setTime(time);
        calendar.setTimeZone(TimeZone.getDefault());
        String result = String.format("%1$tH%1$tM%1$tS[%Z:%z]", calendar) + "[-3:BRT]";
        return result;
        // !!!OLD FORM - SEE ABOVE return String.format("%1$tH%1$tM%1$tS.%1$tL", calendar);
    }

    
    @Override
    protected String formatDate(Date date) {
        // todo formatar de acordo com a regras do BB

        /*
        System.out.println(" NZ Local Time: 2011-10-06 03:35:05");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localNZ = LocalDateTime.parse("2011-10-06 03:35:05", formatter);
        ZonedDateTime zonedNZ = ZonedDateTime.of(localNZ, ZoneId.of("+13:00"));
        LocalDateTime localUTC = zonedNZ.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        System.out.println("UTC Local Time: " + localUTC.format(formatter));
         */
        SimpleDateFormat sdf = new SimpleDateFormat("zzz");
        System.out.println(TimeZone.getDefault().getID());
        System.out.println(sdf.format(date));

        ZoneId z = ZoneId.of("America/Recife");
        ZoneOffset offsetInEffectNow = z.getRules().getOffset(Instant.now());
        System.out.println(offsetInEffectNow);

        //ZoneId z = ZoneId.of("BRT");
        //Instant iDate = date.toInstant();
        //iDate.        ZonedDateTime zdt = iDate.atZone(z);
        //System.out.println(zdt);
        GregorianCalendar calendar = new GregorianCalendar(this.LocalTimeZone);
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getDefault());
        String result = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", calendar) + "[-3:BRT]";
        return result;
    }

}
