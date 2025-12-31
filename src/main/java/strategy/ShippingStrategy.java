package strategy;

import models.Order;

import java.math.BigDecimal;

/**
 * Define o contrato que cada estratégia deve implementar
 * para o cálculo do custo de frete.
 */
public interface ShippingStrategy {

    /**
     * Calcula o custo de frete baseado na estratégia implementada.
     *
     * @param order O pedido para o qual o custo de frete será calculado.
     * @return O custo de frete calculado.
     */
    BigDecimal calculateShippingCost(Order order);
}
