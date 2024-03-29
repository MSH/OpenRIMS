package org.msh.pharmadex2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.HijrahEra;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.validator.UrlValidator;
import org.junit.jupiter.api.Test;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.ResolverService;
import org.msh.pharmadex2.service.r2.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.InternetAddressEditor;

import com.github.binodnme.dateconverter.converter.DateConverter;
import com.github.binodnme.dateconverter.utils.DateBS;
import com.github.eloyzone.jalalicalendar.DateConverterPers;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;

/**
 * Sometimes it is easily to run test then search for docs
 * 
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class ElStupido {
	
	  @Autowired 
	  private BoilerService boilerServ;
	 
	@Test
	public void formatIt() {
		System.out.println(String.format("Welcome to %d!", 12));
	}

	@Test
	public void emptyContains() {
		assertTrue("gdsdgsdgshdgshd".contains(""));
	}

	@Test
	public void primitivesForPropertyUtils()
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		PrimitiveClass prim = new PrimitiveClass();
		Field[] flds = PrimitiveClass.class.getDeclaredFields();
		for (Field fld : flds) {
			Object obj = PropertyUtils.getProperty(prim, fld.getName());
			System.out.println(obj.getClass().getName());
		}
	}

	@Test
	public void mailAddressComponents() throws AddressException {
		String a1 = "test@theta.com.ua";
		String a2 = "test/test@test.com";
		String a3 = "Google Calendar <calendar-notification@google.com>";

		InternetAddress ia = new InternetAddress(a3, true);
		System.out.println(ia.getAddress());
		ia = new InternetAddress(a2, true);
		System.out.println(ia.getAddress());
		ia = new InternetAddress(a1, true);
		System.out.println(ia.getAddress());
		ia = new InternetAddress("elStupido", true);
		System.out.println(ia.getAddress());
	}

	@Test
	public void localDate() {
		LocalDate ld;
		try {
			ld = LocalDate.parse("");
		} catch (Exception e) {
			ld = LocalDate.now();
		}
		System.out.println(ld);
	}

	@Test
	public void exploreThing() {
		List<Field> all = FieldUtils.getAllFieldsList(ThingDTO.class);
		for (Field fld : all) {
			Type typ = fld.getGenericType();
			// System.out.println(typ.getTypeName());
			if (typ.getTypeName().startsWith("java.util.Map<java.lang.String, org.msh.pharmadex2.dto.")) {
				System.out.println(fld.getName());
			}
		}
	}

	@Test
	public void nepaliDates() {
		Date date = new Date(); // default java Date object
		DateBS dateBS = DateConverter.convertADToBS(date); // returns corresponding DateBS
		System.out.println(dateBS.toString());
	}
	
	 @Test
	    public void convert() {
	    	//final LocalDate date =  LocalDate.of(2124, 02, 23); 
	    	final LocalDate date=LocalDate.now();
	  	  System.out.println(String.format("%s <- Original date.", date));
	  	  	
	  	DateConverterPers dateConverter = new DateConverterPers();
	        JalaliDate jalaliDate = dateConverter.gregorianToJalali(date.getYear(), date.getMonth(), date.getDayOfMonth());
	        System.out.println(String.format("%s <- Converted to Pers.", jalaliDate));
	        //JalaliDate(jalaliDate.getYear(), jalaliDate.getMonthPersian().getValue(), jalaliDate.getDay())
	        System.out.println("WeekDay Pers - "+jalaliDate.getDayOfWeek().getStringInPersian());
	        System.out.println("WeekDay Eng - "+jalaliDate.getDayOfWeek().getStringInEnglish());
	        System.out.println("Current date - "+ dateConverter.nowAsGregorian());
	        System.out.println("MonthPers date - "+ jalaliDate.getMonthPersian().getStringInPersian());
	        System.out.println("MonthEng date - "+ jalaliDate.getMonthPersian().getStringInEnglish());
	        String[][] persianTestCases = {
	                {"yyyy/mm/dd", "١٣٧٠/١١/٢٨"},
	                {"yyyy/M/dd", "٢٨/بهمن/١٣٧٠"},
	                {"yyyy/ M dd", "٢٨ بهمن /١٣٧٠"},
	                {"yyyy- M dd", "٢٨ بهمن -١٣٧٠"},
	                {"yyyy M dd", "٢٨ بهمن ١٣٧٠"},
	                {"yyyyMdd", "٢٨بهمن١٣٧٠"},
	        };
	        for (String[] strings : persianTestCases) {
	        System.out.println(String.format(jalaliDate.format(new JalaliDateFormatter(strings[0], JalaliDateFormatter.FORMAT_IN_PERSIAN))));
	        }
	        System.out.println("год "+ jalaliDate.format(new JalaliDateFormatter("yyyy", JalaliDateFormatter.FORMAT_IN_PERSIAN)));
	        System.out.println("год "+ jalaliDate.format(new JalaliDateFormatter("yyyy", JalaliDateFormatter.FORMAT_IN_ENGLISH)));

	        Long retLong= 1234567890l;
			//Locale locale = LocaleContextHolder.getLocale();
			Locale locale = new Locale("ps","AF");
			String str = String.format(locale,"%,d", retLong);
			str=boilerServ.numberToJalali(str);
			System.out.println("number "+ str);
			
			LocalDate ld = LocalDate.of(1964, 03, 11);
			int years = ResolverService.fullYears(ld);
			Locale locale1 = LocaleContextHolder.getLocale();
			String str1 = String.format(locale,"%,d", new Long(years));
			System.out.println("year "+ str1);
			str1=boilerServ.numberToJalali(str1);
			System.out.println("yearJ "+ str1);
			int str2=boilerServ.fullYearsJalali(ld);
			System.out.println("gi "+ str2);
	 }
	 
	@Test
	public void monthBetween() {
		long monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(LocalDate.parse("2016-12-01")),
				YearMonth.from(LocalDate.parse("2016-11-03")));
		System.out.println(monthsBetween);
	}

	@Test
	public void genericClass() {
		FormFieldDTO<Long> ffld = new FormFieldDTO<Long>();
		System.out.println(ffld.getValue());
	}

	@Test
	public void javaConst() {
		System.out.println(SystemService.ACTORS_NMRA);
		System.out.println(SystemService.ACTORS_AUTHENTICATED);
		System.out.println(SystemService.ACTORS_ALL);
	}

	@Test
	public void validURL() {
		String URL_REGEX = "[A-Za-z0-9]+((\\.)?[A-Za-z0-9]+)*";
		Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
		Matcher urlMatcher = URL_PATTERN.matcher("");
		assertFalse(urlMatcher.matches());
		urlMatcher = URL_PATTERN.matcher("aba");
		assertTrue(urlMatcher.matches());
		urlMatcher = URL_PATTERN.matcher("aba.");
		assertFalse(urlMatcher.matches());
		urlMatcher = URL_PATTERN.matcher("aba.jabba");
		assertTrue(urlMatcher.matches());
		urlMatcher = URL_PATTERN.matcher("aba..");
		assertFalse(urlMatcher.matches());
		urlMatcher = URL_PATTERN.matcher("aba.jaba.baba");
		assertTrue(urlMatcher.matches());

	}
}
