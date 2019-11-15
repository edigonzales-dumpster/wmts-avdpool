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

    @Value("${app.dbHostWmts}")
    private String dbHostWmts;
    
    @Value("${app.dbPortWmts}")
    private String dbPortWmts;
    
    @Value("${app.dbDatabaseWmts}")
    private String dbDatabaseWmts;
    
    @Value("${app.dbSchemaWmts}")
    private String dbSchemaWmts;

    @Value("${app.dbUserWmts}")
    private String dbUserWmts;

    @Value("${app.dbPwdWmts}")
    private String dbPwdWmts;

    @Override
    public void configure() throws Exception {
        from("ftp://"+ftpUserInfogrips+"@"+ftpUrlInfogrips+"/\\dm01avso24lv95\\itf\\?password="+ftpPwdInfogrips+"&antInclude=*.zip&autoCreate=false&noop=true&readLock=changed&stepwise=false&separator=Windows&passiveMode=true&binary=true&delay="+downloadDelay+"&initialDelay="+initialDownloadDelay+"&idempotentRepository=#fileConsumerRepo&idempotentKey=ftp-${file:name}-${file:size}-${file:modified}")
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

        from("file://"+pathToUnzipFolder+"/?noop=true&charset=ISO-8859-1&include=.*\\.itf&delay=30000&initialDelay=2000&readLock=changed")
        .routeId("_ili2pg_")
        .log(LoggingLevel.INFO, "Importing File: ${in.header.CamelFileNameOnly}")
        .setProperty("dbhost", constant(dbHostWmts))
        .setProperty("dbport", constant(dbPortWmts))
        .setProperty("dbdatabase", constant(dbDatabaseWmts))
        .setProperty("dbschema", constant(dbSchemaWmts))
        .setProperty("dbusr", constant(dbUserWmts))
        .setProperty("dbpwd", constant(dbPwdWmts))
        .setProperty("dataset", simple("${header.CamelFileName.substring(0,4)}"))
        .process(new Ili2pgReplaceProcessor());

        
        
    }

}
