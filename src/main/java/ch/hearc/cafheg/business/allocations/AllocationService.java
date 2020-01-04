package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.business.common.Montant;
import ch.hearc.cafheg.business.versements.VersementAllocation;
import ch.hearc.cafheg.business.versements.VersementAllocationNaissance;
import ch.hearc.cafheg.business.versements.VersementParentEnfant;
import ch.hearc.cafheg.infrastructure.pdf.PDFExporter;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import ch.hearc.cafheg.infrastructure.persistance.VersementMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AllocationService {

  private static final String PARENT_1 = "Parent1";
  private static final String PARENT_2 = "Parent2";

  private final AllocataireMapper allocataireMapper;
  private final AllocationMapper allocationMapper;
  private final VersementMapper versementMapper;
  private final PDFExporter pdfExporter;

  public AllocationService(
      AllocataireMapper allocataireMapper,
      AllocationMapper allocationMapper,
      VersementMapper versementMapper,
      PDFExporter pdfExporter) {
    this.allocataireMapper = allocataireMapper;
    this.allocationMapper = allocationMapper;
    this.versementMapper = versementMapper;
    this.pdfExporter = pdfExporter;
  }

  public List<Allocataire> findAllAllocataires(String likeNom) {
    return allocataireMapper.findAll(likeNom);
  }

  public List<Allocation> findAllocationsActuelles() {
    return allocationMapper.findAll();
  }

  public String getParentDroitAllocation(Map<String, Object> parameters) {
    String eR = (String) parameters.getOrDefault("enfantResidance", "");
    String p1Residence = (String) parameters.getOrDefault("parent1Residence", "");
    String p2Residence = (String) parameters.getOrDefault("parent2Residence", "");
    Number salaireP1 = (Number) parameters.getOrDefault("parent1Salaire", BigDecimal.ZERO);
    Number salaireP2 = (Number) parameters.getOrDefault("parent2Salaire", BigDecimal.ZERO);

    if (eR.equals(p1Residence) || eR.equals(p2Residence)) {
      return PARENT_1;
    }

    if (salaireP1.doubleValue() > salaireP2.doubleValue()) {
      return PARENT_1;
    }

    if (salaireP1.doubleValue() < salaireP2.doubleValue()) {
      return PARENT_2;
    }

    return PARENT_2;
  }
}
