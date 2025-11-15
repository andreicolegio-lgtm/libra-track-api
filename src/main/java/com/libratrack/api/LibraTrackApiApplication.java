// Archivo: src/main/java/com/libratrack/api/LibraTrackApiApplication.java
// (¡ACTUALIZADO!)

package com.libratrack.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // <-- ¡NUEVA IMPORTACIÓN!

@SpringBootApplication
@EnableScheduling // <-- ¡LÍNEA AÑADIDA!
public class LibraTrackApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraTrackApiApplication.class, args);
	}

}