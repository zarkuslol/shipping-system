package factory;

import strategy.*;

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
    public static ShippingStrategy getStrategy(ShippingType strategyType) {
        return switch (strategyType) {
            case ECONOMY_SAVER -> new EconomySaverStrategy();
            case HYPER_SPEED -> new HyperSpeedStrategy();
            case STORE_PICKUP -> new StorePickupStrategy();
            default -> throw new IllegalArgumentException("Tipo de estratégia desconhecido: " + strategyType);
        };
    }
}
