package dao;

import entities.Box;
import org.apache.log4j.Logger;

import java.sql.*;

public class BoxProcedures {

    public static final Logger logger = Logger.getRootLogger();

    private static final String DROP_PROCEDURE_BOX_SAVE = "DROP PROCEDURE IF EXISTS BOX_SAVE";
    private static final String CREATE_PROCEDURE_BOX_SAVE =
            "create procedure BOX_SAVE (IN name VARCHAR(255), IN format VARCHAR(255), IN file LONGBLOB, IN size INT) " +
                    "begin " +
                    "INSERT INTO BOX (NAME, FORMAT, FILE, SIZE) VALUES (name, format, file, size); " +
                    "end";
    private static final String CALL_PROCEDURE_BOX_SAVE = "{CALL BOX_SAVE(?, ?, ?, ?)}";

    private static final String DROP_PROCEDURE_BOX_RETRIEVE = "DROP PROCEDURE IF EXISTS BOX_RETRIEVE";
    private static final String CREATE_PROCEDURE_BOX_RETRIEVE =
            "create procedure BOX_RETRIEVE (IN par1name VARCHAR(255), IN par2format VARCHAR(255)) " +
                    "begin " +
                    "SELECT NAME, FORMAT, FILE, SIZE  FROM BOX WHERE NAME = par1name AND FORMAT = par2format; " +
                    "end";
    private static final String CALL_PROCEDURE_BOX_RETRIEVE = "{CALL BOX_RETRIEVE(?, ?)}";


    private BoxProcedures() {
    }

    public static void createProcedureBoxSave() {
        executeSQL(DROP_PROCEDURE_BOX_SAVE);
        executeSQL(CREATE_PROCEDURE_BOX_SAVE);
    }

    public static void dropProcedureBoxSave() {
        executeSQL(DROP_PROCEDURE_BOX_SAVE);
    }

    public static void createProcedureBoxRetrieve() {
        executeSQL(DROP_PROCEDURE_BOX_RETRIEVE);
        executeSQL(CREATE_PROCEDURE_BOX_RETRIEVE);
    }

    public static void dropProcedureBoxRetrieve() {
        executeSQL(DROP_PROCEDURE_BOX_RETRIEVE);
    }

    public static boolean runProcedureBoxSave(Box box) {
        try (Connection connection = Datasource.getConnection();
             CallableStatement callableStatement = connection.prepareCall(CALL_PROCEDURE_BOX_SAVE)
        ) {
            callableStatement.setString(1, box.getName());
            callableStatement.setString(2, box.getFormat());
            callableStatement.setBytes(3, box.getFile());
            callableStatement.setLong(4, box.getSize());
            callableStatement.execute();
            return true;

        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Box runProcedureBoxRetrieve(String name, String format) {
        Box box = new Box();
        try (Connection connection = Datasource.getConnection();
             CallableStatement callableStatement = connection.prepareCall(CALL_PROCEDURE_BOX_RETRIEVE)
        ) {
            callableStatement.setString(1, name);
            callableStatement.setString(2, format);
            ResultSet resultSet = callableStatement.executeQuery();
            if (resultSet.next()) {
                box.setName(resultSet.getString(1));
                box.setFormat(resultSet.getString(2));
                box.setFile(resultSet.getBytes(3));
                box.setSize(resultSet.getLong(4));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return box;
    }

    private static void executeSQL(String executeSQL) {

        try (Connection connection = Datasource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.execute(executeSQL);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }


}

