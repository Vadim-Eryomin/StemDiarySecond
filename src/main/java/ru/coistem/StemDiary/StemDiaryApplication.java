package ru.coistem.StemDiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class StemDiaryApplication {

	public static void main(String[] args) throws SQLException {
		new Thread(() -> SpringApplication.run(StemDiaryApplication.class, args)).start();
		Database.connect();
	}

}
