// à supprimer après usage
package com.educeasy.core.dev;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PrintBCrypt {
	@Bean
	CommandLineRunner print(PasswordEncoder pe) {
		return args -> System.out.println("{bcrypt}" + pe.encode("Passw0rd!").replace("{bcrypt}", ""));
	}
}
