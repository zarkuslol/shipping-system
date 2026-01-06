package strategy;

import models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a estratégia de frete EconomySaver.
 *
 * Regra de negócio:
 * - Custo base: $5.00
 * - Adicional de $0.50 por libra para pedidos acima de 10 libras
 */
@DisplayName("EconomySaver Strategy Tests")
class EconomySaverStrategyTest {

    private EconomySaverStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new EconomySaverStrategy();
    }

    @Test
    @DisplayName("Deve calcular custo base de $5.00 para pedidos até 10 libras")
    void shouldCalculateBaseCostForLightPackages() {
        // Arrange
        Order order = new Order(
            5.0,  // weight: 5 libras
            10.0, // height
            10.0, // width
            10.0, // length
            BigDecimal.valueOf(50.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        assertEquals(BigDecimal.valueOf(5.00), cost);
    }

    @Test
    @DisplayName("Deve calcular custo base de $5.00 para pedidos com exatamente 10 libras")
    void shouldCalculateBaseCostForExactlyTenPounds() {
        // Arrange
        Order order = new Order(
            10.0, // weight: exatamente 10 libras
            5.0,
            5.0,
            5.0,
            BigDecimal.valueOf(100.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        assertEquals(BigDecimal.valueOf(5.00), cost);
    }

    @Test
    @DisplayName("Deve adicionar $0.50 por libra acima de 10 libras")
    void shouldAddExtraChargeForHeavyPackages() {
        // Arrange
        Order order = new Order(
            20.0, // weight: 20 libras (10 libras extras)
            15.0,
            15.0,
            15.0,
            BigDecimal.valueOf(200.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo base: $5.00 + (20 - 10) * $0.50 = $5.00 + $5.00 = $10.00
        assertEquals(BigDecimal.valueOf(10.00), cost);
    }

    @Test
    @DisplayName("Deve calcular corretamente para pedidos muito pesados")
    void shouldCalculateCorrectlyForVeryHeavyPackages() {
        // Arrange
        Order order = new Order(
            50.0, // weight: 50 libras (40 libras extras)
            20.0,
            20.0,
            20.0,
            BigDecimal.valueOf(500.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo base: $5.00 + (50 - 10) * $0.50 = $5.00 + $20.00 = $25.00
        assertEquals(BigDecimal.valueOf(25.00), cost);
    }

    @Test
    @DisplayName("Deve calcular corretamente para peso com valores decimais")
    void shouldCalculateCorrectlyForDecimalWeight() {
        // Arrange
        Order order = new Order(
            15.5, // weight: 15.5 libras (5.5 libras extras)
            10.0,
            10.0,
            10.0,
            BigDecimal.valueOf(75.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo base: $5.00 + (15.5 - 10) * $0.50 = $5.00 + $2.75 = $7.75
        assertEquals(BigDecimal.valueOf(7.75), cost);
    }

    @Test
    @DisplayName("Deve calcular custo zero adicional para peso zero")
    void shouldCalculateBaseCostForZeroWeight() {
        // Arrange
        Order order = new Order(
            0.0,  // weight: 0 libras
            5.0,
            5.0,
            5.0,
            BigDecimal.valueOf(10.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        assertEquals(BigDecimal.valueOf(5.00), cost);
    }

    @Test
    @DisplayName("Deve lidar com pedidos de 11 libras (primeira libra acima do limite)")
    void shouldCalculateCorrectlyForElevenPounds() {
        // Arrange
        Order order = new Order(
            11.0, // weight: 11 libras (1 libra extra)
            8.0,
            8.0,
            8.0,
            BigDecimal.valueOf(80.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        // Custo base: $5.00 + (11 - 10) * $0.50 = $5.00 + $0.50 = $5.50
        assertEquals(BigDecimal.valueOf(5.50), cost);
    }
}

