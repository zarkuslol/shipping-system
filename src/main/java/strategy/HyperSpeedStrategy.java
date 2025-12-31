package strategy;

import models.Order;

import java.math.BigDecimal;

/**
 * Estratégia de frete rápido Hyper Speed.<br><br>
 * Nesta estratégia, o custo do frete é calculado com base no maior valor entre o peso do pedido
 * e um fator derivado do volume do pedido, garantindo uma entrega rápida.
 */
public class HyperSpeedStrategy implements ShippingStrategy {
    // Exemplo de taxa base para cálculo do frete rápido
    private final BigDecimal BASE_RATE = BigDecimal.valueOf(12);
    private final Integer DIVISOR = 139; // Divisor para ajustar o custo com base no peso

    /**
     * Se o fator de peso (volume / DIVISOR) for maior que o peso do pedido,
     * o custo do frete é calculado com base nesse fator. Caso contrário, o custo é baseado no peso do pedido.
     */
    @Override
    public BigDecimal calculateShippingCost(Order order) {
        Double volume = order.getHeight() * order.getWidth() * order.getLength();
        double weightFactor = volume / DIVISOR;

        if (weightFactor > order.getWeight()) {
            return BASE_RATE.multiply(BigDecimal.valueOf(weightFactor));
        }

        return BASE_RATE.multiply(BigDecimal.valueOf(order.getWeight()));
    }
}
