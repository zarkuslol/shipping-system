package strategy;

import models.Order;

import java.math.BigDecimal;

/**
 * Estratégia de frete para retirada na loja.<br><br>
 * Nesta estratégia, o cliente é notificado quando o pedido estiver pronto para retirada na loja física,
 * e não há custo de frete associado.
 */
public class StorePickupStrategy implements ShippingStrategy {
    @Override
    public BigDecimal calculateShippingCost(Order order) {
        System.out.println("Avisando o cliente quando o pedido ficar pronto para retirada na loja.");
        return BigDecimal.ZERO;
    }
}
