package factory;

import strategy.EconomySaverStrategy;
import strategy.HyperSpeedStrategy;
import strategy.ShippingStrategy;
import strategy.StorePickupStrategy;

import java.util.Map;

/**
 * Fábrica para criar instâncias de estratégias de frete com base no tipo fornecido.
 */
public class ShippingStrategyFactory {

    /**
     * Retorna a estratégia de frete correspondente ao tipo fornecido.
     *
     * @param strategyType O tipo de estratégia de frete ("economy", "fast", "store").
     * @return A instância da estratégia de frete correspondente.
     */
    public static ShippingStrategy getStrategy(String strategyType) {
        return switch (strategyType) {
            case "economy" -> new EconomySaverStrategy();
            case "fast" -> new HyperSpeedStrategy();
            case "store" -> new StorePickupStrategy();
            default -> throw new IllegalArgumentException("Tipo de estratégia desconhecido: " + strategyType);
        };
    }
}
