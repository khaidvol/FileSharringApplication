package dao;

import entities.Box;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoxDao {

    public static final Logger logger = Logger.getRootLogger();

    private static final String BOX_SAVE_SQL = "INSERT INTO BOX (NAME, FORMAT, FILE, SIZE) VALUES (?, ?, ?, ?)";
    private static final String BOX_RETRIEVE_SQL = "SELECT NAME, FORMAT, FILE, SIZE  FROM BOX WHERE NAME = ? AND FORMAT = ?";


    private BoxDao() {
    }

    public static boolean save(Box box) {
        try (Connection connection = Datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(BOX_SAVE_SQL)
        ) {
            statement.setString(1, box.getName());
            statement.setString(2, box.getFormat());
            statement.setBytes(3, box.getFile());
            statement.setLong(4, box.getSize());
            statement.execute();
            return true;

        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Box retrieve(String name, String format) {
        Box box = new Box();
        try (Connection connection = Datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(BOX_RETRIEVE_SQL)
        ) {
            statement.setString(1, name);
            statement.setString(2, format);
            ResultSet resultSet = statement.executeQuery();
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

    public static boolean delete(String name, String format) {
        throw new UnsupportedOperationException();
    }

    public static boolean update(Box box) {
        throw new UnsupportedOperationException();
    }

}
