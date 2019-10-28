package HAUTEECOLEGESTIONARC;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindAllocataireTS {
    public static List<Map<String,Object>> findAllocataires(String start) {
        try(Connection connection = Application.getConnection()) {
            PreparedStatement preparedStatement;
            if(start==null) {
                preparedStatement = connection.prepareStatement("SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES");
            } else {
                preparedStatement = connection.prepareStatement("SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?");
                preparedStatement.setString(1,start + "%");
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSetToListOfMap(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static  List<Map<String, Object>> resultSetToListOfMap(ResultSet resultSet) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            ResultSetMetaData md = resultSet.getMetaData();
            while (resultSet.next()) {

                Map<String, Object> row = new HashMap<>();
                for(int i=1;i<=md.getColumnCount();i++) {
                    row.put(md.getColumnName(i),resultSet.getObject(i));
                }
                result.add(row);
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
        }

        return result;
    }
}
