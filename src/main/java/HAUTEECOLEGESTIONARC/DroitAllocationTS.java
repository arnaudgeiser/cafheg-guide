package HAUTEECOLEGESTIONARC;

import java.math.BigDecimal;
import java.util.Map;

public class DroitAllocationTS {

    public static final String PARENT_1 = "Parent1";
    public static final String PARENT_2 = "Parent2";


    public static String getParentDroitAllocation(Map<String, Object> parameters) {
        System.out.println("Déterminer le droit aux allocations");
        String eR = (String)parameters.getOrDefault("enfantResidance", "");
        Boolean p1AL = (Boolean)parameters.getOrDefault("parent1ActiviteLucrative", false);
        String p1Residence = (String)parameters.getOrDefault("parent1Residence", "");
        Boolean p2AL = (Boolean)parameters.getOrDefault("parent2ActiviteLucrative", false);
        String p2Residence = (String)parameters.getOrDefault("parent2Residence", "");
        Boolean pEnsemble = (Boolean)parameters.getOrDefault("parentsEnsemble", false);
        Number salaireP1 = (Number) parameters.getOrDefault("parent1Salaire", BigDecimal.ZERO);
        Number salaireP2 = (Number) parameters.getOrDefault("parent2Salaire", BigDecimal.ZERO);

        if(eR.equals(p1Residence) || eR.equals(p2Residence)) {
            return PARENT_1;
        }

        if(salaireP1.doubleValue() > salaireP2.doubleValue()) {
            return PARENT_1;
        }

        if(salaireP1.doubleValue() < salaireP2.doubleValue()) {
            return PARENT_2;
        }

        if(eR.equals(p1Residence) && eR.equals(p2Residence)) {
            return PARENT_1;
        }

        if(eR.equals(p1Residence)) {
            return PARENT_1;
        }

        if(eR.equals(p2Residence)) {
            return PARENT_1;
        }

        return PARENT_2;
    }
}
