package com.neasaa.familytree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {"com.neasaa.base.app", "com.neasaa.familytree"},
    exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class FamilyTreeApplication {
  public static void main(String[] args) {
    SpringApplication.run(FamilyTreeApplication.class, args);
  }
}
