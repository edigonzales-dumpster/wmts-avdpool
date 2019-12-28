package ch.so.agi.avdpool;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IntegrationRoute extends RouteBuilder {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.ftpUserInfogrips}")
    private String ftpUserInfogrips;

    @Value("${app.ftpPwdInfogrips}")
    private String ftpPwdInfogrips;

    @Value("${app.ftpUrlInfogrips}")
    private String ftpUrlInfogrips;

    @Value("${app.pathToDownloadFolder}")
    private String pathToDownloadFolder;

    @Value("${app.pathToUnzipFolder}")
    private String pathToUnzipFolder;

    @Value("${app.downloadDelay}")
    private String downloadDelay;

    @Value("${app.initialDownloadDelay}")
    private String initialDownloadDelay;

    @Value("${app.importDelay}")
    private String importDelay;

    @Value("${app.initialImportDelay}")
    private String initialImportDelay;

    @Value("${app.dbHost}")
    private String dbHost;
    
    @Value("${app.dbPort}")
    private String dbPort;
    
    @Value("${app.dbDatabase}")
    private String dbDatabase;
    
    @Value("${app.dbSchema}")
    private String dbSchema;

    @Value("${app.dbUser}")
    private String dbUser;

    @Value("${app.dbPwd}")
    private String dbPwd;

    @Override
    public void configure() throws Exception {
        // TODO: send email
        onException(Exception.class)
        .continued(true)
        .log(LoggingLevel.ERROR, simple("${exception.stacktrace}").getText())
        .log(LoggingLevel.ERROR, simple("${exception.message}").getText());
        
        from("ftp://"+ftpUserInfogrips+"@"+ftpUrlInfogrips+"/\\dm01avso24lv95\\itf\\?password="+ftpPwdInfogrips+"&antInclude=*.zip&autoCreate=false&noop=true&readLock=changed&stepwise=false&separator=Windows&passiveMode=true&binary=true&maxMessagesPerPoll=120&delay="+downloadDelay+"&initialDelay="+initialDownloadDelay+"&idempotentRepository=#fileConsumerRepo&idempotentKey=ftp-${file:name}-${file:size}-${file:modified}")
        //from("ftp://"+ftpUserInfogrips+"@"+ftpUrlInfogrips+"/\\dm01avso24lv95\\itf\\?password="+ftpPwdInfogrips+"&antInclude=240100.zip&autoCreate=false&noop=true&readLock=changed&stepwise=false&separator=Windows&passiveMode=true&binary=true&delay="+downloadDelay+"&initialDelay="+initialDownloadDelay+"&idempotentRepository=#fileConsumerRepo&idempotentKey=ftp-${file:name}-${file:size}-${file:modified}")
        .routeId("_download_")
        .log(LoggingLevel.INFO, "Downloading and unzipping route: ${in.header.CamelFileNameOnly}")
        .to("file://"+pathToDownloadFolder)
        .split(new ZipSplitter())
        .streaming().convertBodyTo(String.class, "ISO-8859-1") 
            .choice()
                .when(body().isNotNull())
                    .setHeader(Exchange.FILE_NAME, simple("${file:name.noext}.itf"))
                    .to("file://"+pathToUnzipFolder+"?charset=ISO-8859-1")
            .end()
        .end();

        from("file://"+pathToUnzipFolder+"/?noop=true&charset=ISO-8859-1&include=.*\\.itf&delay="+importDelay+"&initialDelay="+initialImportDelay+"&readLock=changed&maxMessagesPerPoll=60&idempotentRepository=#fileConsumerRepo&idempotentKey=ili2pg-${file:name}-${file:size}-${file:modified}")
        .routeId("_ili2pg_")
        .log(LoggingLevel.INFO, "Importing File: ${in.header.CamelFileNameOnly}")
        .setProperty("dbhost", constant(dbHost))
        .setProperty("dbport", constant(dbPort))
        .setProperty("dbdatabase", constant(dbDatabase))
        .setProperty("dbschema", constant(dbSchema))
        .setProperty("dbusr", constant(dbUser))
        .setProperty("dbpwd", constant(dbPwd))
        .setProperty("dataset", simple("${header.CamelFileName.substring(0,4)}"))
        .process(new Ili2pgReplaceProcessor());
    }
}
