package HAUTEECOLEGESTIONARC;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
public class RESTController {
    /*
    {
        "enfantResidance" : "Neuchâtel",
        "parent1Residence" : "Neuchâtel",
        "parent2Residence" : "Bienne",
        "parent1ActiviteLucrative" : true,
        "parent2ActiviteLucrative" : true,
        "parent1Salaire" : 2500,
        "parent2Salaire" : 3000
    }
     */

    @GetMapping("/droits/quel-parent")
    public String getParentDroitAllocation(@RequestBody Map<String, Object> params) {
        return DroitAllocationTS.getParentDroitAllocation(params);
    }

    @GetMapping("/allocataires")
    public List<Map<String, Object>> allocataires(@RequestParam(value = "startsWith", required = false) String start) {
        return FindAllocataireTS.findAllocataires(start);
    }

    @GetMapping("/allocations")
    public List<Map<String, Object>> allocations() {
        return FindAllocationsTS.findAllocationsActuelles();
    }

    @GetMapping("/allocations/{year}/somme")
    public BigDecimal sommeAs(@PathVariable("year") int year) {
        return FindSommeAllocationsTS.savAnnee(year);
    }

    @GetMapping("/allocations-naissances/{year}/somme")
    public BigDecimal sommeAns(@PathVariable("year") int year) {
        return FindSommeAllocationsNaissanceTS.savAnnee(year);
    }

    @GetMapping(value = "/allocataires/{allocataireId}/allocations", produces = MediaType.APPLICATION_PDF_VALUE)
    public  byte[] pdfAllocations(@PathVariable("allocataireId") int allocataireId) {
        return PDFAllocationsTS.pdfAllocations(allocataireId).toByteArray();
    }

    @GetMapping(value = "/allocataires/{allocataireId}/versements", produces = MediaType.APPLICATION_PDF_VALUE)
    public  byte[] pdfVersements(@PathVariable("allocataireId") int allocataireId) {
        return PDFVersementsTS.pdfVersements(allocataireId).toByteArray();
    }
}
