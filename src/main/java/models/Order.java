package models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa um pedido com suas principais características.
 */
@AllArgsConstructor
@Getter
public class Order {
    private Double weight;
    private Double height;
    private Double width;
    private Double length;
    private BigDecimal price;
    private LocalDateTime dateTime;
}
