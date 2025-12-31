package strategy;

import models.Order;

import java.math.BigDecimal;

/**
 * Estratégia de baixo custo que calcula o frete baseado no custo base e no peso do produto.<br><br>
 * <strong>Custo base é de $5.00</strong>, com um adicional de $0.50 para cada libra, quando o pedido tem mais de 10 libras.
 */
public class EconomySaverStrategy implements ShippingStrategy {
    @Override
    public BigDecimal calculateShippingCost(Order order) {
        BigDecimal baseCost = BigDecimal.valueOf(5.00);
        double weightFactor = 0.0;

        if (order.getWeight() > 10) {
            weightFactor = (order.getWeight() - 10) * 0.5;
        }

        return baseCost.add(BigDecimal.valueOf(weightFactor));
    }
}
