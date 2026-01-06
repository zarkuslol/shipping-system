package integration;

import factory.ShippingStrategyFactory;
import models.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import services.ShippingService;
import strategy.ShippingStrategy;
import strategy.ShippingType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração que simulam cenários reais de uso do sistema de frete.
 *
 * Testa o fluxo completo: Factory -> Strategy -> Service
 */
@DisplayName("Integration Tests - Real World Scenarios")
class ShippingIntegrationTest {

    @Test
    @DisplayName("Cenário 1: Cliente escolhe frete econômico para produto leve")
    void scenario1_EconomyShippingForLightProduct() {
        // Arrange - Livro pequeno e leve
        Order order = new Order(
            2.0,  // 2 libras
            8.0,  // 8 polegadas
            5.0,  // 5 polegadas
            1.0,  // 1 polegada
            BigDecimal.valueOf(25.00),
            LocalDateTime.now()
        );

        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
        ShippingService service = new ShippingService(strategy);

        // Act
        BigDecimal shippingCost = service.calculateShippingCost(order);

        // Assert
        assertEquals(BigDecimal.valueOf(5.00), shippingCost);
        assertTrue(shippingCost.compareTo(order.getPrice()) < 0,
            "Frete deve ser menor que o valor do produto");
    }

    @Test
    @DisplayName("Cenário 2: Cliente escolhe entrega expressa para produto urgente")
    void scenario2_ExpressShippingForUrgentProduct() {
        // Arrange - Documento importante que precisa chegar rápido
        Order order = new Order(
            0.5,  // 0.5 libra
            11.0, // tamanho A4
            8.5,
            0.5,
            BigDecimal.valueOf(100.00),
            LocalDateTime.now()
        );

        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED);
        ShippingService service = new ShippingService(strategy);

        // Act
        BigDecimal shippingCost = service.calculateShippingCost(order);

        // Assert
        assertTrue(shippingCost.compareTo(BigDecimal.ZERO) > 0,
            "HyperSpeed deve ter custo");
        assertTrue(shippingCost.compareTo(BigDecimal.valueOf(100.00)) < 0,
            "Custo de frete deve ser razoável");
    }

    @Test
    @DisplayName("Cenário 3: Cliente opta por retirada na loja para economia")
    void scenario3_StorePickupToSaveMoney() {
        // Arrange - Produto qualquer, cliente quer economizar
        Order order = new Order(
            50.0,  // produto pesado
            20.0,
            20.0,
            20.0,
            BigDecimal.valueOf(500.00),
            LocalDateTime.now()
        );

        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(ShippingType.STORE_PICKUP);
        ShippingService service = new ShippingService(strategy);

        // Act
        BigDecimal shippingCost = service.calculateShippingCost(order);

        // Assert
        assertEquals(BigDecimal.ZERO, shippingCost,
            "Retirada na loja deve ser gratuita");
    }

    @Test
    @DisplayName("Cenário 4: Comparar custos entre diferentes estratégias para mesmo pedido")
    void scenario4_CompareCostsBetweenStrategies() {
        // Arrange - Produto médio
        Order order = new Order(
            15.0,
            12.0,
            12.0,
            12.0,
            BigDecimal.valueOf(150.00),
            LocalDateTime.now()
        );

        // Act - Calcular com todas as estratégias
        ShippingService economyService = new ShippingService(
            ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER)
        );
        ShippingService hyperService = new ShippingService(
            ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED)
        );
        ShippingService storeService = new ShippingService(
            ShippingStrategyFactory.getStrategy(ShippingType.STORE_PICKUP)
        );

        BigDecimal economyCost = economyService.calculateShippingCost(order);
        BigDecimal hyperCost = hyperService.calculateShippingCost(order);
        BigDecimal storeCost = storeService.calculateShippingCost(order);

        // Assert - Retirada na loja é mais barata
        assertTrue(storeCost.compareTo(economyCost) < 0);
        assertTrue(storeCost.compareTo(hyperCost) < 0);

        // Assert - Economy é mais barato que HyperSpeed
        assertTrue(economyCost.compareTo(hyperCost) < 0,
            "Economy deve ser mais barato que HyperSpeed");

        // Assert - Valores específicos
        assertEquals(BigDecimal.valueOf(7.50), economyCost);  // $5 + (15-10)*$0.50
        assertEquals(BigDecimal.valueOf(180.00), hyperCost);  // $12 * 15
        assertEquals(BigDecimal.ZERO, storeCost);
    }

    @Test
    @DisplayName("Cenário 5: Produto volumoso mas leve - peso volumétrico prevalece")
    void scenario5_VolumetricWeightPrevails() {
        // Arrange - Caixa grande com travesseiro (leve mas volumoso)
        Order order = new Order(
            1.0,   // apenas 1 libra
            30.0,  // dimensões grandes
            30.0,
            30.0,
            BigDecimal.valueOf(50.00),
            LocalDateTime.now()
        );

        ShippingStrategy hyperStrategy = ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED);
        ShippingService hyperService = new ShippingService(hyperStrategy);

        ShippingStrategy economyStrategy = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
        ShippingService economyService = new ShippingService(economyStrategy);

        // Act
        BigDecimal hyperCost = hyperService.calculateShippingCost(order);
        BigDecimal economyCost = economyService.calculateShippingCost(order);

        // Assert
        // HyperSpeed deve cobrar muito mais devido ao peso volumétrico
        assertTrue(hyperCost.compareTo(economyCost) > 0,
            "HyperSpeed deve cobrar mais devido ao peso volumétrico");

        // Economy não considera volume, apenas peso real
        assertEquals(BigDecimal.valueOf(5.00), economyCost);

        // HyperSpeed considera volume: (30*30*30)/139 ≈ 194 libras volumétricas
        assertTrue(hyperCost.compareTo(BigDecimal.valueOf(2000.00)) > 0);
    }

    @Test
    @DisplayName("Cenário 6: Produto pesado mas pequeno - peso real prevalece")
    void scenario6_ActualWeightPrevails() {
        // Arrange - Peso de academia (pesado mas pequeno)
        Order order = new Order(
            25.0,  // 25 libras
            6.0,   // dimensões pequenas
            6.0,
            6.0,
            BigDecimal.valueOf(75.00),
            LocalDateTime.now()
        );

        ShippingStrategy hyperStrategy = ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED);
        ShippingService hyperService = new ShippingService(hyperStrategy);

        // Act
        BigDecimal hyperCost = hyperService.calculateShippingCost(order);

        // Assert
        // Peso volumétrico = (6*6*6)/139 ≈ 1.55 libras
        // Peso real (25) é maior, então deve prevalecer
        assertEquals(BigDecimal.valueOf(300.00), hyperCost); // $12 * 25
    }

    @ParameterizedTest
    @CsvSource({
        "5.0,  10.0, 10.0, 10.0, ECONOMY_SAVER, 5.00",
        "15.0, 10.0, 10.0, 10.0, ECONOMY_SAVER, 7.50",
        "25.0, 10.0, 10.0, 10.0, ECONOMY_SAVER, 12.50",
        "10.0, 5.0,  5.0,  5.0,  HYPER_SPEED,   120.00",
        "20.0, 6.0,  6.0,  6.0,  HYPER_SPEED,   240.00",
        "100.0, 5.0,  5.0,  5.0,  STORE_PICKUP,  0"
    })
    @DisplayName("Cenário 7: Testes parametrizados com diferentes combinações")
    void scenario7_ParameterizedShippingCosts(
        Double weight,
        Double height,
        Double width,
        Double length,
        ShippingType strategyType,
        String expectedCost
    ) {
        // Arrange
        Order order = new Order(
            weight, height, width, length,
            BigDecimal.valueOf(100.00),
            LocalDateTime.now()
        );

        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(strategyType);
        ShippingService service = new ShippingService(strategy);

        // Act
        BigDecimal actualCost = service.calculateShippingCost(order);

        // Assert
        assertEquals(0, new BigDecimal(expectedCost).compareTo(actualCost),
            "Expected cost " + expectedCost + " but got " + actualCost);
    }

    @Test
    @DisplayName("Cenário 8: Pedidos simultâneos com diferentes estratégias")
    void scenario8_SimultaneousOrdersWithDifferentStrategies() {
        // Arrange - 3 clientes fazendo pedidos ao mesmo tempo
        Order order1 = new Order(5.0, 5.0, 5.0, 5.0, BigDecimal.valueOf(50.00), LocalDateTime.now());
        Order order2 = new Order(10.0, 5.0, 5.0, 5.0, BigDecimal.valueOf(100.00), LocalDateTime.now());
        Order order3 = new Order(15.0, 10.0, 10.0, 10.0, BigDecimal.valueOf(150.00), LocalDateTime.now());

        ShippingService service1 = new ShippingService(
            ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER)
        );
        ShippingService service2 = new ShippingService(
            ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED)
        );
        ShippingService service3 = new ShippingService(
            ShippingStrategyFactory.getStrategy(ShippingType.STORE_PICKUP)
        );

        // Act
        BigDecimal cost1 = service1.calculateShippingCost(order1);
        BigDecimal cost2 = service2.calculateShippingCost(order2);
        BigDecimal cost3 = service3.calculateShippingCost(order3);

        // Assert - Cada serviço mantém sua estratégia independentemente
        assertEquals(BigDecimal.valueOf(5.00), cost1);   // Economy: $5 (peso <= 10)
        assertEquals(BigDecimal.valueOf(120.00), cost2);  // HyperSpeed: $12 * 10 (peso real > volumétrico)
        assertEquals(BigDecimal.ZERO, cost3);            // Store Pickup: gratuito
    }

    @Test
    @DisplayName("Cenário 9: Pedido de alto valor - verificar se frete é proporcional")
    void scenario9_HighValueOrder() {
        // Arrange - Joias ou eletrônicos caros
        Order expensiveOrder = new Order(
            2.0,
            5.0,
            5.0,
            2.0,
            BigDecimal.valueOf(5000.00), // produto muito caro
            LocalDateTime.now()
        );

        ShippingStrategy economyStrategy = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
        ShippingStrategy hyperStrategy = ShippingStrategyFactory.getStrategy(ShippingType.HYPER_SPEED);

        ShippingService economyService = new ShippingService(economyStrategy);
        ShippingService hyperService = new ShippingService(hyperStrategy);

        // Act
        BigDecimal economyCost = economyService.calculateShippingCost(expensiveOrder);
        BigDecimal hyperCost = hyperService.calculateShippingCost(expensiveOrder);

        // Assert - Frete não depende do valor do produto, apenas de peso/dimensões
        assertEquals(BigDecimal.valueOf(5.00), economyCost);
        assertEquals(BigDecimal.valueOf(24.00), hyperCost);

        // Frete é uma pequena fração do valor do produto
        assertTrue(economyCost.compareTo(expensiveOrder.getPrice()) < 0);
        assertTrue(hyperCost.compareTo(expensiveOrder.getPrice()) < 0);
    }

    @Test
    @DisplayName("Cenário 10: Carrinho com múltiplos itens - simular consolidação")
    void scenario10_MultipleItemsCart() {
        // Arrange - Cliente compra 3 produtos diferentes
        Order item1 = new Order(3.0, 8.0, 8.0, 4.0, BigDecimal.valueOf(30.00), LocalDateTime.now());
        Order item2 = new Order(5.0, 10.0, 10.0, 5.0, BigDecimal.valueOf(50.00), LocalDateTime.now());
        Order item3 = new Order(2.0, 6.0, 6.0, 3.0, BigDecimal.valueOf(20.00), LocalDateTime.now());

        // Simular peso e dimensões consolidados
        double totalWeight = item1.getWeight() + item2.getWeight() + item3.getWeight();
        double maxHeight = Math.max(Math.max(item1.getHeight(), item2.getHeight()), item3.getHeight());
        double maxWidth = Math.max(Math.max(item1.getWidth(), item2.getWidth()), item3.getWidth());
        double maxLength = item1.getLength() + item2.getLength() + item3.getLength();

        Order consolidatedOrder = new Order(
            totalWeight,
            maxHeight,
            maxWidth,
            maxLength,
            item1.getPrice().add(item2.getPrice()).add(item3.getPrice()),
            LocalDateTime.now()
        );

        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(ShippingType.ECONOMY_SAVER);
        ShippingService service = new ShippingService(strategy);

        // Act
        BigDecimal shippingCost = service.calculateShippingCost(consolidatedOrder);

        // Assert
        assertEquals(BigDecimal.valueOf(5.00), shippingCost); // 10 libras = custo base
        assertTrue(consolidatedOrder.getWeight().equals(10.0));
    }
}

