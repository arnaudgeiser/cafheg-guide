package ch.hearc.cafheg.business.allocations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.hearc.cafheg.business.common.Montant;
import ch.hearc.cafheg.infrastructure.pdf.PDFExporter;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import ch.hearc.cafheg.infrastructure.persistance.VersementMapper;
import com.google.common.collect.ImmutableMap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AllocationServiceTest {

  private AllocationService allocationService;

  private AllocataireMapper allocataireMapper;
  private AllocationMapper allocationMapper;
  private VersementMapper versementMapper;
  private PDFExporter pdfExporter;

  @BeforeEach
  void setUp() {
    allocataireMapper = Mockito.mock(AllocataireMapper.class);
    allocationMapper = Mockito.mock(AllocationMapper.class);
    versementMapper = Mockito.mock(VersementMapper.class);
    pdfExporter = Mockito.mock(PDFExporter.class);

    allocationService = new AllocationService(allocataireMapper, allocationMapper, versementMapper,
        pdfExporter);
  }

  @Test
  void getParentDroitAllocation_GivenMemeVille_ShouldBeParent1() {
    String droit = allocationService
        .getParentDroitAllocation(mapOf("Berne", "Berne", "Berne", 1000, 2000));
    assertThat(droit).isEqualTo("Parent1");
  }

  @Test
  void getParentDroitAllocation_GivenDifferenteVilleSalaire2PlusGrand_ShouldBeParent2() {
    String droit = allocationService
        .getParentDroitAllocation(mapOf("Fribourg", "Berne", "Berne", 1000, 2000));
    assertThat(droit).isEqualTo("Parent2");
  }

  @Test
  void getParentDroitAllocation_GivenDifferenteVilleSalaire1PlusGrand_ShouldBeParent1() {
    String droit = allocationService
        .getParentDroitAllocation(mapOf("Fribourg", "Berne", "Berne", 2000, 1000));
    assertThat(droit).isEqualTo("Parent1");
  }

  @Test
  void getParentDroitAllocation_GivenDifferenteVilleMemeSalaire_ShouldBeParent2() {
    String droit = allocationService
        .getParentDroitAllocation(mapOf("Fribourg", "Berne", "Berne", 1000, 1000));
    assertThat(droit).isEqualTo("Parent2");
  }

  @Test
  void findAllAllocataires_GivenEmptyAllocataires_ShouldBeEmpty() {
    Mockito.when(allocataireMapper.findAll("Geiser")).thenReturn(Collections.emptyList());
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertThat(all).isEmpty();
  }

  @Test
  void findAllAllocataires_Given2Geiser_ShouldBe2() {
    Mockito.when(allocataireMapper.findAll("Geiser"))
        .thenReturn(Arrays.asList(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"),
            new Allocataire(new NoAVS("1000-2001"), "Geiser", "Aurélie")));
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
        () -> assertThat(all.get(0).getNoAVS()).isEqualTo(new NoAVS("1000-2000")),
        () -> assertThat(all.get(0).getNom()).isEqualTo("Geiser"),
        () -> assertThat(all.get(0).getPrenom()).isEqualTo("Arnaud"),
        () -> assertThat(all.get(1).getNoAVS()).isEqualTo(new NoAVS("1000-2001")),
        () -> assertThat(all.get(1).getNom()).isEqualTo("Geiser"),
        () -> assertThat(all.get(1).getPrenom()).isEqualTo("Aurélie"));
  }

  @Test
  void findAllocationsActuelles() {
    Mockito.when(allocationMapper.findAll())
        .thenReturn(Arrays.asList(new Allocation(new Montant(new BigDecimal(1000)), Canton.NE,
            LocalDate.now(), null), new Allocation(new Montant(new BigDecimal(2000)), Canton.FR,
            LocalDate.now(), null)));
    List<Allocation> all = allocationService.findAllocationsActuelles();
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
        () -> assertThat(all.get(0).getMontant()).isEqualTo(new Montant(new BigDecimal(1000))),
        () -> assertThat(all.get(0).getCanton()).isEqualTo(Canton.NE),
        () -> assertThat(all.get(0).getDebut()).isEqualTo(LocalDate.now()),
        () -> assertThat(all.get(0).getFin()).isNull(),
        () -> assertThat(all.get(1).getMontant()).isEqualTo(new Montant(new BigDecimal(2000))),
        () -> assertThat(all.get(1).getCanton()).isEqualTo(Canton.FR),
        () -> assertThat(all.get(1).getDebut()).isEqualTo(LocalDate.now()),
        () -> assertThat(all.get(1).getFin()).isNull());
  }

  private Map<String, Object> mapOf(String enfantResidance, String parent1Residence,
      String parent2Residence, Number parent1Salaire, Number parent2Salaire) {
    Map<String, Object> map = new HashMap<>();
    map.put("enfantResidance", enfantResidance);
    map.put("parent1Residence", parent1Residence);
    map.put("parent2Residence", parent2Residence);
    map.put("parent1Salaire", parent1Salaire);
    map.put("parent2Salaire", parent2Salaire);
    return map;
  }

}