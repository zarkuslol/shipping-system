package services;

import models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import strategy.EconomySaverStrategy;
import strategy.HyperSpeedStrategy;
import strategy.ShippingStrategy;
import strategy.StorePickupStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o serviço de frete que utiliza o padrão Strategy.
 *
 * Verifica se o ShippingService delega corretamente o cálculo para a estratégia configurada.
 */
@DisplayName("Shipping Service Tests")
class ShippingServiceTest {

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order(
            15.0, // weight
            12.0, // height
            12.0, // width
            12.0, // length
            BigDecimal.valueOf(100.00),
            LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve calcular frete usando estratégia EconomySaver")
    void shouldCalculateShippingUsingEconomySaverStrategy() {
        // Arrange
        ShippingStrategy strategy = new EconomySaverStrategy();
        ShippingService service = new ShippingService(strategy);

        // Act
        BigDecimal cost = service.calculateShippingCost(testOrder);

        // Assert
        // EconomySaver: $5.00 + (15 - 10) * $0.50 = $7.50
        assertEquals(BigDecimal.valueOf(7.50), cost);
    }

    @Test
    @DisplayName("Deve calcular frete usando estratégia HyperSpeed")
    void shouldCalculateShippingUsingHyperSpeedStrategy() {
        // Arrange
        ShippingStrategy strategy = new HyperSpeedStrategy();
        ShippingService service = new ShippingService(strategy);

        // Act
        BigDecimal cost = service.calculateShippingCost(testOrder);

        // Assert
        // HyperSpeed: $12.00 * peso (peso real > volumétrico)
        assertEquals(BigDecimal.valueOf(180.00), cost);
    }

    @Test
    @DisplayName("Deve calcular frete usando estratégia StorePickup")
    void shouldCalculateShippingUsingStorePickupStrategy() {
        // Arrange
        ShippingStrategy strategy = new StorePickupStrategy();
        ShippingService service = new ShippingService(strategy);

        // Act
        BigDecimal cost = service.calculateShippingCost(testOrder);

        // Assert
        assertEquals(BigDecimal.ZERO, cost);
    }

    @Test
    @DisplayName("Deve permitir trocar estratégia em tempo de execução")
    void shouldAllowStrategySwappingAtRuntime() {
        // Arrange
        ShippingStrategy economyStrategy = new EconomySaverStrategy();
        ShippingService service = new ShippingService(economyStrategy);

        // Act & Assert - Primeira estratégia
        BigDecimal economyCost = service.calculateShippingCost(testOrder);
        assertEquals(BigDecimal.valueOf(7.50), economyCost);

        // Troca de estratégia (simulando nova instância do serviço)
        ShippingStrategy hyperStrategy = new HyperSpeedStrategy();
        service = new ShippingService(hyperStrategy);

        // Act & Assert - Segunda estratégia
        BigDecimal hyperCost = service.calculateShippingCost(testOrder);
        assertEquals(BigDecimal.valueOf(180.00), hyperCost);
    }

    @Test
    @DisplayName("Deve calcular diferentes custos para diferentes pedidos com mesma estratégia")
    void shouldCalculateDifferentCostsForDifferentOrders() {
        // Arrange
        ShippingStrategy strategy = new EconomySaverStrategy();
        ShippingService service = new ShippingService(strategy);

        Order lightOrder = new Order(
            5.0, 10.0, 10.0, 10.0,
            BigDecimal.valueOf(50.00),
            LocalDateTime.now()
        );

        Order heavyOrder = new Order(
            30.0, 20.0, 20.0, 20.0,
            BigDecimal.valueOf(300.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal lightCost = service.calculateShippingCost(lightOrder);
        BigDecimal heavyCost = service.calculateShippingCost(heavyOrder);

        // Assert
        assertEquals(BigDecimal.valueOf(5.00), lightCost);  // $5.00 (sem adicional)
        assertEquals(BigDecimal.valueOf(15.00), heavyCost); // $5.00 + (30-10)*$0.50 = $15.00
        assertTrue(heavyCost.compareTo(lightCost) > 0);
    }

    @Test
    @DisplayName("Deve funcionar com pedidos de valores extremos")
    void shouldWorkWithExtremeOrderValues() {
        // Arrange
        ShippingStrategy strategy = new EconomySaverStrategy();
        ShippingService service = new ShippingService(strategy);

        Order tinyOrder = new Order(
            0.1, 1.0, 1.0, 1.0,
            BigDecimal.valueOf(1.00),
            LocalDateTime.now()
        );

        Order hugeOrder = new Order(
            1000.0, 100.0, 100.0, 100.0,
            BigDecimal.valueOf(50000.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal tinyCost = service.calculateShippingCost(tinyOrder);
        BigDecimal hugeCost = service.calculateShippingCost(hugeOrder);

        // Assert
        assertEquals(BigDecimal.valueOf(5.00), tinyCost);     // $5.00 (base)
        assertEquals(BigDecimal.valueOf(500.00), hugeCost);   // $5.00 + (1000-10)*$0.50 = $500.00
    }

    @Test
    @DisplayName("Deve manter consistência ao calcular o mesmo pedido múltiplas vezes")
    void shouldMaintainConsistencyForMultipleCalculations() {
        // Arrange
        ShippingStrategy strategy = new HyperSpeedStrategy();
        ShippingService service = new ShippingService(strategy);

        // Act - Calcula 3 vezes
        BigDecimal cost1 = service.calculateShippingCost(testOrder);
        BigDecimal cost2 = service.calculateShippingCost(testOrder);
        BigDecimal cost3 = service.calculateShippingCost(testOrder);

        // Assert - Todos devem ser iguais
        assertEquals(cost1, cost2);
        assertEquals(cost2, cost3);
        assertEquals(BigDecimal.valueOf(180.00), cost1);
    }
}

