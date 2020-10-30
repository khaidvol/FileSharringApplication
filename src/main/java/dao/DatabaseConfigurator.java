package dao;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;

public class DatabaseConfigurator {

    private static final Logger logger = Logger.getRootLogger();

    public static final String SCHEMA = "src/main/resources/db/schema.sql";
    public static final String MAX_ALLOWED_PACKET = "src/main/resources/db/max_allowed_packet.sql";

    private DatabaseConfigurator() {
    }

    public static void autoConfiguration() {
        //create schema and change packet limits
        executeSqlScript(SCHEMA);
        executeSqlScript(MAX_ALLOWED_PACKET);
        //create stored procedures for saving and retrieving files
        BoxProcedures.createProcedureBoxSave();
        BoxProcedures.createProcedureBoxRetrieve();
    }

    private static void executeSqlScript(String sqlScript) {
        try {
            ScriptRunner scriptRunner = new ScriptRunner(Datasource.getConnection());
            scriptRunner.runScript(new BufferedReader(new FileReader(sqlScript)));
        } catch (FileNotFoundException | SQLException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }
}
