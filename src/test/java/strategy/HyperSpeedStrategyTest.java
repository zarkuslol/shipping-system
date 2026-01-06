package strategy;

import models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a estratégia de frete HyperSpeed.
 *
 * Regra de negócio:
 * - Taxa base: $12.00
 * - Calcula peso volumétrico: (altura x largura x comprimento) / 139
 * - Cobra pelo maior valor: peso real ou peso volumétrico
 */
@DisplayName("HyperSpeed Strategy Tests")
class HyperSpeedStrategyTest {

    private HyperSpeedStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new HyperSpeedStrategy();
    }

    @Test
    @DisplayName("Deve cobrar baseado no peso real quando maior que peso volumétrico")
    void shouldChargeBasedOnActualWeightWhenHeavier() {
        // Arrange
        Order order = new Order(
            10.0, // weight: 10 libras
            5.0,  // height
            5.0,  // width
            5.0,  // length
            BigDecimal.valueOf(100.00),
            LocalDateTime.now()
        );
        // Peso volumétrico = (5 * 5 * 5) / 139 = 125 / 139 ≈ 0.899

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo = $12.00 * 10 = $120.00 (peso real é maior)
        assertEquals(BigDecimal.valueOf(120.00), cost);
    }

    @Test
    @DisplayName("Deve cobrar baseado no peso volumétrico quando maior que peso real")
    void shouldChargeBasedOnVolumetricWeightWhenLarger() {
        // Arrange
        Order order = new Order(
            1.0,  // weight: 1 libra
            20.0, // height
            20.0, // width
            20.0, // length
            BigDecimal.valueOf(50.00),
            LocalDateTime.now()
        );
        // Peso volumétrico = (20 * 20 * 20) / 139 = 8000 / 139 ≈ 57.55

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo = $12.00 * 57.55... ≈ $690.64...
        assertTrue(cost.compareTo(BigDecimal.valueOf(690.00)) > 0);
        assertTrue(cost.compareTo(BigDecimal.valueOf(691.00)) < 0);
    }

    @Test
    @DisplayName("Deve calcular corretamente para pacote pequeno e leve")
    void shouldCalculateCorrectlyForSmallLightPackage() {
        // Arrange
        Order order = new Order(
            2.0,  // weight: 2 libras
            5.0,  // height
            5.0,  // width
            5.0,  // length
            BigDecimal.valueOf(25.00),
            LocalDateTime.now()
        );
        // Peso volumétrico = (5 * 5 * 5) / 139 = 125 / 139 ≈ 0.899

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo = $12.00 * 2 = $24.00 (peso real é maior)
        assertEquals(BigDecimal.valueOf(24.00), cost);
    }

    @Test
    @DisplayName("Deve calcular corretamente para pacote grande mas leve")
    void shouldCalculateCorrectlyForLargeLightPackage() {
        // Arrange
        Order order = new Order(
            0.5,  // weight: 0.5 libra
            30.0, // height
            30.0, // width
            30.0, // length
            BigDecimal.valueOf(15.00),
            LocalDateTime.now()
        );
        // Peso volumétrico = (30 * 30 * 30) / 139 = 27000 / 139 ≈ 194.24

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo = $12.00 * 194.24... ≈ $2330.93...
        assertTrue(cost.compareTo(BigDecimal.valueOf(2330.00)) > 0);
        assertTrue(cost.compareTo(BigDecimal.valueOf(2332.00)) < 0);
    }

    @Test
    @DisplayName("Deve calcular corretamente quando peso real e volumétrico são iguais")
    void shouldCalculateCorrectlyWhenWeightsAreEqual() {
        // Arrange
        // Calculando dimensões que resultam em peso volumétrico ≈ 5
        // volume / 139 = 5 -> volume = 695
        Order order = new Order(
            5.0,  // weight: 5 libras
            8.84, // height (aproximado para volume = 695)
            8.84, // width
            8.90, // length
            BigDecimal.valueOf(75.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Deve cobrar $12 * 5 = $60 (ou próximo disso)
        assertTrue(cost.compareTo(BigDecimal.valueOf(59.00)) > 0);
        assertTrue(cost.compareTo(BigDecimal.valueOf(61.00)) < 0);
    }

    @Test
    @DisplayName("Deve lidar com pacote de dimensões mínimas")
    void shouldHandleMinimalDimensions() {
        // Arrange
        Order order = new Order(
            1.0,  // weight: 1 libra
            1.0,  // height
            1.0,  // width
            1.0,  // length
            BigDecimal.valueOf(10.00),
            LocalDateTime.now()
        );
        // Peso volumétrico = (1 * 1 * 1) / 139 ≈ 0.007

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo = $12.00 * 1 = $12.00 (peso real é maior)
        assertEquals(BigDecimal.valueOf(12.00), cost);
    }

    @Test
    @DisplayName("Deve calcular corretamente para pacote retangular grande")
    void shouldCalculateCorrectlyForLargeRectangularPackage() {
        // Arrange
        Order order = new Order(
            3.0,  // weight: 3 libras
            40.0, // height
            10.0, // width
            10.0, // length
            BigDecimal.valueOf(150.00),
            LocalDateTime.now()
        );
        // Peso volumétrico = (40 * 10 * 10) / 139 = 4000 / 139 ≈ 28.77

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo = $12.00 * 28.77... ≈ $345.32...
        assertTrue(cost.compareTo(BigDecimal.valueOf(345.00)) > 0);
        assertTrue(cost.compareTo(BigDecimal.valueOf(346.00)) < 0);
    }

    @Test
    @DisplayName("Deve lidar com peso zero mas com volume")
    void shouldHandleZeroWeightWithVolume() {
        // Arrange
        Order order = new Order(
            0.0,  // weight: 0 libras
            10.0, // height
            10.0, // width
            10.0, // length
            BigDecimal.valueOf(5.00),
            LocalDateTime.now()
        );
        // Peso volumétrico = (10 * 10 * 10) / 139 = 1000 / 139 ≈ 7.19

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo = $12.00 * 7.19... ≈ $86.33...
        assertTrue(cost.compareTo(BigDecimal.valueOf(86.00)) > 0);
        assertTrue(cost.compareTo(BigDecimal.valueOf(87.00)) < 0);
    }
}

