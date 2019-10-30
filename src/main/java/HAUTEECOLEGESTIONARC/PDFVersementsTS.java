package HAUTEECOLEGESTIONARC;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFVersementsTS {
    public static ByteArrayOutputStream pdfVersements(long allocataireId) {
        System.out.println("Imprimer le PDF des versements");
        try(Connection connection = Application.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT AL.NUMERO AS PARENT_ID, A.MONTANT, V.DATE_VERSEMENT, V.MOIS_VERSEMENT FROM VERSEMENTS V JOIN VERSEMENTS_ALLOCATIONS VA ON V.NUMERO=VA.FK_VERSEMENTS JOIN ALLOCATIONS_ENFANTS AE ON AE.NUMERO=VA.FK_ALLOCATIONS_ENFANTS JOIN ALLOCATIONS A ON A.NUMERO=AE.FK_ALLOCATIONS JOIN ALLOCATAIRES AL ON AL.NUMERO=V.FK_ALLOCATAIRES JOIN ENFANTS E ON E.NUMERO=AE.FK_ENFANTS");
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


            Map<Date, BigDecimal> versements = new HashMap<>();
            for(int i=0;i<queryResult.size();i++) {
                long pId = (Long)queryResult.get(i).get("PARENT_ID");
                Date dateVersement = (Date) queryResult.get(i).get("DATE_VERSEMENT");
                BigDecimal montant = (BigDecimal) queryResult.get(i).get("MONTANT");

                if(pId == allocataireId) {
                    BigDecimal mv = versements.getOrDefault(dateVersement, BigDecimal.ZERO);
                    versements.put(dateVersement, mv.add(montant));
                }
            }


            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 500);
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStream.showText("Les versement suivants ont été fait à l'allocataire : " + nom + " " + prenom + " ("+noAVS+")");
            contentStream.endText();

            int i = 0;
            for (Map.Entry<Date, BigDecimal> entry : versements.entrySet()) {
                Date dv = entry.getKey();
                contentStream.beginText();
                contentStream.newLineAtOffset(25, 450 - (i * 24));
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.showText(dv.toString());
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(300, 450 - (i * 24));
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.showText(entry.getValue() + " CHF");
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
