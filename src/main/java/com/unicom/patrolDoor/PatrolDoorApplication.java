package com.unicom.patrolDoor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan(basePackages = "com.unicom.patrolDoor.dao")
@EnableScheduling
public class PatrolDoorApplication {


	public static void main(String[] args) {
		SpringApplication.run(PatrolDoorApplication.class, args);
	}

}
