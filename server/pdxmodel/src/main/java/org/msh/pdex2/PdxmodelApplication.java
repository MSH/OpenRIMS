package org.msh.pdex2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.msh"})
public class PdxmodelApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdxmodelApplication.class, args);
	}

}
