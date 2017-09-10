/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.webcohesion.ofx4j.io.DefaultStringConversion;
import java.sql.Time;
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
        String result = String.format("%1$tH%1$tM%1$tS[%Z:%z]", calendar) + "[-3BRT]";
        return result;
        // !!!OLD FORM - SEE ABOVE return String.format("%1$tH%1$tM%1$tS.%1$tL", calendar);
    }

    @Override
    protected String formatDate(Date date) {
        // todo formatar de acordo com a regras do BB
        GregorianCalendar calendar = new GregorianCalendar(this.LocalTimeZone);
        calendar.setTime(date);
        String result = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", calendar) + "[-3BRT]";
        return result;
    }

}
