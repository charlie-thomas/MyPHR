package com.csbgroup.myphr;

import com.csbgroup.myphr.Calendar.CalendarDay;

import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.*;

public class UnitTests {

    @Test
    public void isOtherDayMethodTest() throws ParseException {
        assertTrue(CalendarDay.isOtherDay("01/01/2000", "03/01/2000"));
    }

    @Test
    public void isOtherDayMethodFailTest() throws ParseException {
        assertFalse(CalendarDay.isOtherDay("01/01/2000", "02/01/2000"));
    }
}