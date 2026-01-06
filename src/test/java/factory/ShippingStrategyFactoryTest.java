package factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import strategy.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a fábrica de estratégias de frete.
 *
 * Verifica se a fábrica cria corretamente as instâncias das estratégias
 * com base no tipo solicitado.
 */
@DisplayName("Shipping Strategy Factory Tests")
class ShippingStrategyFactoryTest {

    @Test
    @DisplayName("Deve criar instância de EconomySaverStrategy quando tipo é ECONOMY_SAVER")
    void shouldCreateEconomySaverStrategy() {
        // Act
        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);

        // Assert
        assertNotNull(strategy);
        assertInstanceOf(EconomySaverStrategy.class, strategy);
    }

    @Test
    @DisplayName("Deve criar instância de HyperSpeedStrategy quando tipo é HYPER_SPEED")
    void shouldCreateHyperSpeedStrategy() {
        // Act
        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED);

        // Assert
        assertNotNull(strategy);
        assertInstanceOf(HyperSpeedStrategy.class, strategy);
    }

    @Test
    @DisplayName("Deve criar instância de StorePickupStrategy quando tipo é STORE_PICKUP")
    void shouldCreateStorePickupStrategy() {
        // Act
        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(ShippingType.STORE_PICKUP);

        // Assert
        assertNotNull(strategy);
        assertInstanceOf(StorePickupStrategy.class, strategy);
    }

    @Test
    @DisplayName("Deve criar instâncias diferentes a cada chamada (não singleton)")
    void shouldCreateNewInstancesEachTime() {
        // Act
        ShippingStrategy strategy1 = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
        ShippingStrategy strategy2 = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);

        // Assert
        assertNotNull(strategy1);
        assertNotNull(strategy2);
        assertNotSame(strategy1, strategy2, "Factory deve criar novas instâncias a cada chamada");
    }

    @Test
    @DisplayName("Deve criar todas as estratégias disponíveis sem erros")
    void shouldCreateAllAvailableStrategies() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            ShippingStrategy economy = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
            ShippingStrategy hyper = ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED);
            ShippingStrategy store = ShippingStrategyFactory.getStrategy(ShippingType.STORE_PICKUP);

            assertNotNull(economy);
            assertNotNull(hyper);
            assertNotNull(store);
        });
    }

    @Test
    @DisplayName("Deve criar estratégias de tipos diferentes em sequência")
    void shouldCreateDifferentStrategiesInSequence() {
        // Act
        ShippingStrategy strategy1 = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
        ShippingStrategy strategy2 = ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED);
        ShippingStrategy strategy3 = ShippingStrategyFactory.getStrategy(ShippingType.STORE_PICKUP);

        // Assert
        assertInstanceOf(EconomySaverStrategy.class, strategy1);
        assertInstanceOf(HyperSpeedStrategy.class, strategy2);
        assertInstanceOf(StorePickupStrategy.class, strategy3);
    }

    @Test
    @DisplayName("Deve retornar estratégias que implementam a interface ShippingStrategy")
    void shouldReturnStrategiesThatImplementShippingStrategy() {
        // Act
        ShippingStrategy economyStrategy = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
        ShippingStrategy hyperStrategy = ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED);
        ShippingStrategy storeStrategy = ShippingStrategyFactory.getStrategy(ShippingType.STORE_PICKUP);

        // Assert
        assertTrue(economyStrategy instanceof ShippingStrategy);
        assertTrue(hyperStrategy instanceof ShippingStrategy);
        assertTrue(storeStrategy instanceof ShippingStrategy);
    }

    @Test
    @DisplayName("Deve criar estratégias funcionais que podem ser usadas imediatamente")
    void shouldCreateFunctionalStrategies() {
        // Arrange
        models.Order testOrder = new models.Order(
            10.0, 10.0, 10.0, 10.0,
            java.math.BigDecimal.valueOf(100.00),
            java.time.LocalDateTime.now()
        );

        // Act & Assert
        assertDoesNotThrow(() -> {
            ShippingStrategy economy = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
            java.math.BigDecimal cost = economy.calculateShippingCost(testOrder);
            assertNotNull(cost);
            assertTrue(cost.compareTo(java.math.BigDecimal.ZERO) >= 0);
        });
    }

    @Test
    @DisplayName("Deve manter tipo de estratégia independente para cada criação")
    void shouldMaintainIndependentStrategyTypes() {
        // Act
        ShippingStrategy first = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
        ShippingStrategy second = ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED);
        ShippingStrategy third = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);

        // Assert
        assertInstanceOf(EconomySaverStrategy.class, first);
        assertInstanceOf(HyperSpeedStrategy.class, second);
        assertInstanceOf(EconomySaverStrategy.class, third);
        assertNotSame(first, third, "Mesmo tipo deve criar instâncias diferentes");
    }
}

