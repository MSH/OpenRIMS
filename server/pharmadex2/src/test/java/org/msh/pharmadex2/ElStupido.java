package org.msh.pharmadex2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.r2.SystemService;
import org.springframework.mail.javamail.InternetAddressEditor;

import com.github.binodnme.dateconverter.converter.DateConverter;
import com.github.binodnme.dateconverter.utils.DateBS;

/**
 * Sometimes it is easily to run test then search for docs 
 * @author alexk
 *
 */
public class ElStupido {
	@Test
	public void formatIt() {
		System.out.println(String.format("Welcome to %d!", 12));
	}
	@Test
	public void emptyContains() {
		assertTrue("gdsdgsdgshdgshd".contains(""));
	}
	@Test
	public void primitivesForPropertyUtils() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		PrimitiveClass prim = new PrimitiveClass();
		Field[]  flds = PrimitiveClass.class.getDeclaredFields();
		for(Field fld : flds) {
			Object obj = PropertyUtils.getProperty(prim, fld.getName());
			System.out.println(obj.getClass().getName());
		}
	}
	@Test
	public void mailAddressComponents() throws AddressException {
		String a1= "test@theta.com.ua";
		String a2="test/test@test.com";
		String a3="Google Calendar <calendar-notification@google.com>";
		
		InternetAddress ia = new InternetAddress(a3,true);
		System.out.println(ia.getAddress());
		ia = new InternetAddress(a2,true);
		System.out.println(ia.getAddress());
		ia = new InternetAddress(a1,true);
		System.out.println(ia.getAddress());
		ia = new InternetAddress("elStupido",true);
		System.out.println(ia.getAddress());	
	}
	
	@Test
	public void localDate() {
		LocalDate ld;
		try {
			ld = LocalDate.parse("");
		} catch (Exception e) {
			ld=LocalDate.now();
		}
		System.out.println(ld);
	}
	@Test
	public void exploreThing() {
		List<Field> all = FieldUtils.getAllFieldsList(ThingDTO.class);
		for(Field fld :all) {
			Type typ=fld.getGenericType();
			//System.out.println(typ.getTypeName());
			if(typ.getTypeName().startsWith("java.util.Map<java.lang.String, org.msh.pharmadex2.dto.")) {
				System.out.println(fld.getName());
			}
		}
	}
	@Test
	public void nepaliDates() {
		Date date = new Date();                             //default java Date object
		DateBS dateBS = DateConverter.convertADToBS(date);  //returns corresponding DateBS
		System.out.println(dateBS.toString());
	}
	
	@Test
	public void monthBetween() {
		long monthsBetween = ChronoUnit.MONTHS.between(
			     YearMonth.from(LocalDate.parse("2016-12-01")), 
			     YearMonth.from(LocalDate.parse("2016-11-03"))
			);
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
	
}
