package ru.coistem.StemDiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class StemDiaryApplication {

	public static void main(String[] args) throws Exception {
		new Thread(() -> SpringApplication.run(StemDiaryApplication.class, args)).start();
		Database.connect();
		if(!Database.query("SELECT * FROM login").next()) {
			Database.query("INSERT INTO login VALUES(1, 'admin', '111')");
			Database.query("INSERT INTO profile VALUES(1, 'Admin', 'Admin', 'img/noroot.png', 111)");
			Database.query("INSERT INTO login VALUES(1, 't', 't')");
		}

	}

}
