package HAUTEECOLEGESTIONARC;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.util.*;

public class FindSommeAllocationsNaissanceTS {
    public static BigDecimal savAnnee(int year) {
        try(Connection connection = Application.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT V.DATE_VERSEMENT,A.MONTANT FROM VERSEMENTS V JOIN VERSEMENTS_NAISSANCE VN ON V.NUMERO=VN.FK_VERSEMENTS");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, Object>> queryResult = resultSetToListOfMap(resultSet);

            BigDecimal somme = BigDecimal.ZERO;
            for(int i=0;i<queryResult.size();i++) {
                Map<String, Object> row = queryResult.get(i);
                Date date = (Date)row.get("DATE_VERSEMENT");
                Calendar instance = Calendar.getInstance();
                instance.setTime(date);
                int aa = instance.get(Calendar.YEAR);
                if(aa==year) {
                    somme = somme.add((BigDecimal)row.get("MONTANT"));
                }
            }

            return somme;

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