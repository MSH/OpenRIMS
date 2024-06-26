package com.github.eloyzone.jalalicalendar;

import org.junit.Test;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.*;


/**
 * Unit test for jeloy calendar
 * This unit test tried to cover all possible test-cases.
 *
 * @author Eloy (Elyas Hadizadeh Tasbiti)
 */
public class LibTest
{
    @Test
    public void jalaliToGregorian()
    {
        DateConverterPers dateConverter = new DateConverterPers();

        String errorMessage = "Jalali to Gregorian not converted properly";

        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1370, 11, 28), LocalDate.of(1992, 2, 17));
        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1397, 8, 12), LocalDate.of(2018, 11, 3));
        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1397, 12, 29), LocalDate.of(2019, 03, 20));
        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1386, 12, 29), LocalDate.of(2008, 03, 19));
        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1386, MonthPersian.ESFAND, 29), LocalDate.of(2008, 03, 19));
        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1388, 7, 18), LocalDate.of(2009, 10, 10));
        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1390, 9, 24), LocalDate.of(2011, 12, 15));
        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1390, MonthPersian.AZAR, 24), LocalDate.of(2011, 12, 15));
        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1357, 5, 5), LocalDate.of(1978, 7, 27));
        assertEquals(errorMessage, dateConverter.jalaliToGregorian(1357, MonthPersian.MORDAD, 5), LocalDate.of(1978, 7, 27));
    }

    @Test
    public void gregorianToJalali()
    {
        DateConverterPers dateConverter = new DateConverterPers();

        String errorMessage = "Gregorian to Jalali not converted properly";

        assertEquals(errorMessage, new JalaliDate(1370, 11, 28), dateConverter.gregorianToJalali(1992, 2, 17));
        assertEquals(errorMessage, new JalaliDate(1397, 12, 29), dateConverter.gregorianToJalali(2019, 3, 20));
        assertEquals(errorMessage, new JalaliDate(1397, 8, 12), dateConverter.gregorianToJalali(2018, 11, 3));
        assertEquals(errorMessage, new JalaliDate(1386, 12, 29), dateConverter.gregorianToJalali(2008, 3, 19));
        assertEquals(errorMessage, new JalaliDate(1388, 7, 18), dateConverter.gregorianToJalali(2009, 10, 10));
        assertEquals(errorMessage, new JalaliDate(1390, 9, 24), dateConverter.gregorianToJalali(2011, 12, 15));
        assertEquals(errorMessage, new JalaliDate(1357, 5, 5), dateConverter.gregorianToJalali(1978, 7, 27));
        assertEquals(errorMessage, new JalaliDate(1341, 6, 6), dateConverter.gregorianToJalali(1962, 8, 28));
    }

    @Test
    public void checkLeapYear()
    {
        String errorMessageLeapYear = "Testing leap year failed";
        String errorMessageNonLeapYear = "Testing non-leap year failed";

        // testing 33-years-period
        for (int i = 1280; i <= 1308; i = i + 4)
            assertTrue(errorMessageLeapYear, new JalaliDate(i, 11, 28).isLeapYear());

        // testing 33-years-period
        for (int i = 1313; i <= 1341; i = i + 4)
            assertTrue(errorMessageLeapYear, new JalaliDate(i, 11, 28).isLeapYear());

        // testing 29-years-period
        for (int i = 1346; i <= 1370; i = i + 4)
            assertTrue(errorMessageLeapYear, new JalaliDate(i, 11, 28).isLeapYear());

        // testing 33-years-period
        for (int i = 1375; i <= 1403; i = i + 4)
            assertTrue(errorMessageLeapYear, new JalaliDate(i, 11, 28).isLeapYear());

        // testing some random non-leap-years
        assertFalse(errorMessageNonLeapYear, new JalaliDate(1371, 11, 28).isLeapYear());
        assertFalse(errorMessageNonLeapYear, new JalaliDate(1376, 11, 28).isLeapYear());
        assertFalse(errorMessageNonLeapYear, new JalaliDate(1342, 11, 28).isLeapYear());
        assertFalse(errorMessageNonLeapYear, new JalaliDate(1344, 11, 28).isLeapYear());
        assertFalse(errorMessageNonLeapYear, new JalaliDate(1281, 11, 28).isLeapYear());
    }

    @Test
    public void dayOfWeekPersian()
    {
        String errorMessage = "Day of week not calculated properly";

        assertEquals(errorMessage, new JalaliDate(1370, 11, 28).getDayOfWeek().getStringInPersian(), DayOfWeekPersian.Doshanbeh.getStringInPersian());
        assertEquals(errorMessage, new JalaliDate(1370, 11, 28).getDayOfWeek().getStringInPersian(), DayOfWeekPersian.Doshanbeh.getStringInPersian());
        assertEquals(errorMessage, new JalaliDate(1397, 8, 12).getDayOfWeek().getStringInPersian(), DayOfWeekPersian.Shanbeh.getStringInPersian());
        assertEquals(errorMessage, new JalaliDate(1397, 12, 29).getDayOfWeek().getStringInPersian(), DayOfWeekPersian.Chaharshanbeh.getStringInPersian());
        assertEquals(errorMessage, new JalaliDate(1386, 12, 29).getDayOfWeek().getStringInEnglish(), DayOfWeekPersian.Chaharshanbeh.getStringInEnglish());
        assertEquals(errorMessage, new JalaliDate(1388, 7, 18).getDayOfWeek().getStringInEnglish(), DayOfWeekPersian.Shanbeh.getStringInEnglish());
        assertEquals(errorMessage, new JalaliDate(1390, 9, 24).getDayOfWeek().getStringInEnglish(), DayOfWeekPersian.Panjshanbeh.getStringInEnglish());
    }

    @Test
    public void nowAsGregorian()
    {
        String errorMessage = "current created gregorian date is wrong";
        assertEquals(errorMessage, new DateConverterPers().nowAsGregorian(), LocalDate.now());
    }


    //@Test
    public void jalaliFormatter()
    {
        String errorMessage = "Jalali date formatter does not work properly";

        String[][] persianTestCases = {
                {"yyyy/mm/dd", "١٣٧٠/١١/٢٨"},
                {"yyyy/M/dd", "٢٨/بهمن/١٣٧٠"},
                {"yyyy/ M dd", "٢٨ بهمن /١٣٧٠"},
                {"yyyy- M dd", "٢٨ بهمن -١٣٧٠"},
                {"yyyy M dd", "٢٨ بهمن ١٣٧٠"},
                {"yyyyMdd", "٢٨بهمن١٣٧٠"},
        };

        String[][] englishTestCases = {
                {"yyyy M dd", "1370 Bahmand 28"},
                {"yyyy/mm/dd", "1370/11/28"},
        };

        DateConverterPers dateConverter = new DateConverterPers();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(1992, Month.FEBRUARY, 17);

        for (String[] strings : persianTestCases) {
            assertEquals(errorMessage, jalaliDate.format(new JalaliDateFormatter(strings[0], JalaliDateFormatter.FORMAT_IN_PERSIAN)), strings[1]);
        System.out.println(String.format(jalaliDate.format(new JalaliDateFormatter(strings[0], JalaliDateFormatter.FORMAT_IN_PERSIAN))));
        System.out.println(strings[1]);
    }
        for (String[] strings : englishTestCases) {
            assertEquals(errorMessage, jalaliDate.format(new JalaliDateFormatter(strings[0])), strings[1]);
            System.out.println(String.format(jalaliDate.format(new JalaliDateFormatter(strings[0]))));
        }
        new JalaliDate();
    }

    @Test
    public void convert() {
    	final LocalDate date =  LocalDate.of(2024, 02, 23); 
  	  System.out.println(String.format("%s <- Original date.", date));
  	  	
    	DateConverterPers dateConverter = new DateConverterPers();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(date.getYear(), date.getMonth(), date.getDayOfMonth());
        System.out.println(String.format("%s <- Converted to Pers.", jalaliDate));
    }
    // --------------------------------- Test exceptions ------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void jalaliDateIllegalArgumentOfMonth()
    {
        new JalaliDate(1342, -1, 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void jalaliDateIllegalArgumentOfDay()
    {
        new JalaliDate(1342, 1, 35);
    }

    @Test(expected = IllegalArgumentException.class)
    public void jalaliDateIllegalArgumentOfYear()
    {
        new JalaliDate(-1342, 1, 31);
    }

    @Test(expected = IllegalArgumentException.class)
    public void jalaliDateIllegalArgumentOfDayInSecondHalfOfYear()
    {
        new JalaliDate(-1342, 7, 31);
    }

    @Test(expected = IllegalArgumentException.class)
    public void jalaliDateIllegalArgumentOfDayOfLastMonthInLeapYear()
    {
        // just in leap year the last month becomes 30
        new JalaliDate(1371, 12, 30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dateConvertorJalaliToGregorianIllegalArgumentOfMonth()
    {
        new DateConverterPers().gregorianToJalali(1342, -1, 6);
    }



}