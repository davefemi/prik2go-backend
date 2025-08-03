package nl.davefemi.prik2go;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "nl.davefemi.prik2go")
public class Prik2GoServer {
    public static void main(String[] args) {
        SpringApplication.run(Prik2GoServer.class, args);
    }
}