package services;

import lombok.AllArgsConstructor;
import models.Order;
import strategy.ShippingStrategy;

import java.math.BigDecimal;

/**
 * Serviço que utiliza uma estratégia de frete para calcular o custo de envio de um pedido.
 */
@AllArgsConstructor
public class ShippingService {
    private ShippingStrategy shippingStrategy;

    /**
     * Calcula o custo de frete para o pedido fornecido usando a estratégia de frete configurada.
     *
     * @param order O pedido para o qual o custo de frete será calculado.
     * @return O custo de frete calculado.
     */
    public BigDecimal calculateShippingCost(Order order) {
        return this.shippingStrategy.calculateShippingCost(order);
    }
}
