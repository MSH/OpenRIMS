package org.msh.pharmadex2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.service.common.BoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.i18n.LocaleContextHolder;

@SpringBootTest(classes=Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class I18nTest {
	@Autowired
	Messages mess;
	@Autowired
	BoilerService boilerServ;
	
	@Test
	public void testBundle() {
		LocaleContextHolder.setLocale(new Locale("en", "US"));
		System.out.println(mess.getCurrentLocale().toString());
		mess.loadLanguages();
		assertEquals("Close",mess.get("global_close"));
	}
	
	@Test
	public void testNepaliDate() {
		String dates = boilerServ.localDateToNepali(LocalDate.now(), false);
		System.out.println(dates);
		dates = boilerServ.localDateToNepali(LocalDate.now(), true);
		System.out.println(dates);
	}
}
