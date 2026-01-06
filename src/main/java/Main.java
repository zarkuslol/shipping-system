import factory.ShippingStrategyFactory;
import models.Order;
import services.ShippingService;
import strategy.ShippingType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Simulando os dados vindos do payload do frontend
        Double weight = 12.0;
        Double height = 10.0;
        Double width = 5.0;
        Double length = 8.0;
        BigDecimal price = BigDecimal.valueOf(120);
        LocalDateTime dateTime = LocalDateTime.now();
        ShippingType shippingMethod = ShippingType.ECONOMY_SAVER;

        // Simulando a chamada do serviço de frete com a estratégia escolhida
        ShippingService shippingService = new ShippingService(
                ShippingStrategyFactory.getStrategy(shippingMethod)
        );

        Order order = new Order(
                weight,
                height,
                width,
                length,
                price,
                dateTime
        );

        // Simulando o retorno do frete para o frontend
        System.out.println(shippingService.calculateShippingCost(order));
    }
}
