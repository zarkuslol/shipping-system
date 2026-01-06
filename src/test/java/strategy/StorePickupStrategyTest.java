package strategy;

import models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a estratégia de retirada na loja.
 *
 * Regra de negócio:
 * - Custo de frete: $0.00 (gratuito)
 * - Sistema notifica cliente quando pedido estiver pronto
 */
@DisplayName("Store Pickup Strategy Tests")
class StorePickupStrategyTest {

    private StorePickupStrategy strategy;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        strategy = new StorePickupStrategy();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    @DisplayName("Deve retornar custo zero para retirada na loja")
    void shouldReturnZeroCostForStorePickup() {
        // Arrange
        Order order = new Order(
            10.0,
            15.0,
            15.0,
            15.0,
            BigDecimal.valueOf(100.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(order);

        // Assert
        assertEquals(BigDecimal.ZERO, cost);
    }

    @Test
    @DisplayName("Deve imprimir mensagem de notificação ao cliente")
    void shouldPrintNotificationMessage() {
        // Arrange
        Order order = new Order(
            5.0,
            10.0,
            10.0,
            10.0,
            BigDecimal.valueOf(50.00),
            LocalDateTime.now()
        );

        // Act
        strategy.calculateShippingCost(order);

        // Assert
        String output = outputStreamCaptor.toString().trim();
        assertTrue(output.contains("Avisando o cliente quando o pedido ficar pronto para retirada na loja"));
    }

    @Test
    @DisplayName("Deve retornar custo zero independente do peso do pedido")
    void shouldReturnZeroCostRegardlessOfWeight() {
        // Arrange - Pedido muito pesado
        Order heavyOrder = new Order(
            500.0, // 500 libras
            50.0,
            50.0,
            50.0,
            BigDecimal.valueOf(1000.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(heavyOrder);

        // Assert
        assertEquals(BigDecimal.ZERO, cost);
    }

    @Test
    @DisplayName("Deve retornar custo zero independente das dimensões do pedido")
    void shouldReturnZeroCostRegardlessOfDimensions() {
        // Arrange - Pedido muito grande
        Order largeOrder = new Order(
            1.0,
            100.0, // dimensões muito grandes
            100.0,
            100.0,
            BigDecimal.valueOf(50.00),
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(largeOrder);

        // Assert
        assertEquals(BigDecimal.ZERO, cost);
    }

    @Test
    @DisplayName("Deve retornar custo zero independente do valor do pedido")
    void shouldReturnZeroCostRegardlessOfOrderValue() {
        // Arrange - Pedido de alto valor
        Order expensiveOrder = new Order(
            2.0,
            10.0,
            10.0,
            10.0,
            BigDecimal.valueOf(10000.00), // valor muito alto
            LocalDateTime.now()
        );

        // Act
        BigDecimal cost = strategy.calculateShippingCost(expensiveOrder);

        // Assert
        assertEquals(BigDecimal.ZERO, cost);
    }

    @Test
    @DisplayName("Deve funcionar para múltiplas chamadas consecutivas")
    void shouldWorkForMultipleConsecutiveCalls() {
        // Arrange
        Order order1 = new Order(5.0, 10.0, 10.0, 10.0, BigDecimal.valueOf(50.00), LocalDateTime.now());
        Order order2 = new Order(15.0, 20.0, 20.0, 20.0, BigDecimal.valueOf(150.00), LocalDateTime.now());
        Order order3 = new Order(3.0, 8.0, 8.0, 8.0, BigDecimal.valueOf(30.00), LocalDateTime.now());

        // Act
        BigDecimal cost1 = strategy.calculateShippingCost(order1);
        BigDecimal cost2 = strategy.calculateShippingCost(order2);
        BigDecimal cost3 = strategy.calculateShippingCost(order3);

        // Assert
        assertEquals(BigDecimal.ZERO, cost1);
        assertEquals(BigDecimal.ZERO, cost2);
        assertEquals(BigDecimal.ZERO, cost3);

        // Verifica que a mensagem foi impressa 3 vezes
        String output = outputStreamCaptor.toString();
        int occurrences = output.split("Avisando o cliente", -1).length - 1;
        assertEquals(3, occurrences);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}

