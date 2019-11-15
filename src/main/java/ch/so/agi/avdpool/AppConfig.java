package ch.so.agi.avdpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import org.apache.camel.CamelContext;
import org.apache.camel.support.processor.idempotent.FileIdempotentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AppConfig {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CamelContext camelContext;

    @Value("${app.idempotentFileUrl}")
    private String idempotentFileUrl;

    @Bean
    public FileIdempotentRepository fileConsumerRepo() {
        FileIdempotentRepository fileConsumerRepo = null;
        try {
            fileConsumerRepo = new FileIdempotentRepository();
            fileConsumerRepo.setFileStore(new File(idempotentFileUrl));
            fileConsumerRepo.setCacheSize(5000);
            fileConsumerRepo.setMaxFileStoreSize(51200000);
        } catch (Exception e) {
            log.error("Caught exception inside Creating fileConsumerRepo ..." + e.getMessage());
        }
        if (fileConsumerRepo == null) {
            log.error("fileConsumerRepo == null ...");
        }
        return fileConsumerRepo;
    }
}
