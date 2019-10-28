package HAUTEECOLEGESTIONARC;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class PDFAllocationsTS {
    public static ByteArrayOutputStream pdfAllocations(long allocataireId) {
        try(Connection connection = Application.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT AL.NUMERO AS PARENT_ID, E.NUMERO AS ENFANT_ID, A.MONTANT FROM VERSEMENTS V JOIN VERSEMENTS_ALLOCATIONS VA ON V.NUMERO=VA.FK_VERSEMENTS JOIN ALLOCATIONS_ENFANTS AE ON AE.NUMERO=VA.FK_ALLOCATIONS_ENFANTS JOIN ALLOCATIONS A ON A.NUMERO=AE.FK_ALLOCATIONS JOIN ALLOCATAIRES AL ON AL.NUMERO=V.FK_ALLOCATAIRES JOIN ENFANTS E ON E.NUMERO=AE.FK_ENFANTS");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, Object>> queryResult = resultSetToListOfMap(resultSet);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PreparedStatement pq = connection.prepareStatement("SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?");
            pq.setLong(1, allocataireId);
            ResultSet rsP = pq.executeQuery();
            rsP.next();
            String noAVS = rsP.getString("NO_AVS");
            String nom = rsP.getString("NOM");
            String prenom = rsP.getString("PRENOM");

            Map<Long, BigDecimal> enfants = new HashMap<>();

            for(int i=0;i<queryResult.size();i++) {
                long pId = (Long)queryResult.get(i).get("PARENT_ID");
                long eId = (Long)queryResult.get(i).get("ENFANT_ID");
                BigDecimal montant = (BigDecimal) queryResult.get(i).get("MONTANT");

                if(pId == allocataireId) {
                    enfants.put(eId, montant);
                }
            }


            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 500);
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStream.showText("L'allocataire " + nom + " " + prenom + " ("+noAVS+") possèdent des droits d'allocations pour " + enfants.size() + " enfant(s) : ");
            contentStream.endText();

            int i = 0;
            for (Map.Entry<Long, BigDecimal> entry : enfants.entrySet()) {
                long eId = entry.getKey();
                PreparedStatement pq1 = connection.prepareStatement("SELECT NO_AVS, NOM, PRENOM FROM ENFANTS WHERE NUMERO=?");
                pq1.setLong(1, eId);
                ResultSet rsE = pq1.executeQuery();
                rsE.next();
                String noAVSE = rsE.getString("NO_AVS");
                String nomE = rsE.getString("NOM");
                String prenomE = rsE.getString("PRENOM");
                contentStream.beginText();
                contentStream.newLineAtOffset(25, 450 - (i * 24));
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.showText(nomE + " " + prenomE + " ("+noAVSE+")");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(300, 450 - (i * 24));
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.showText(entry.getValue().toString() + " CHF");
                contentStream.endText();

                i++;
            }

            contentStream.close();

            try {

                document.save(baos);
                document.close();
                return baos;
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                    row.put(md.getColumnLabel(i),resultSet.getObject(i));
                }
                result.add(row);
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
        }

        return result;
    }
}
