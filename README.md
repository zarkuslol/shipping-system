# **Shipping Strategy System**

Este projeto é uma implementação de referência para um sistema de cálculo de fretes flexível e escalável. Ele resolve o problema comum de complexidade crescente em regras de negócios logísticas utilizando Padrões de Projeto para tornar o código limpo, testável e fácil de manter.

## **🎯 O Problema**

Em sistemas de e-commerce, a lógica de cálculo de frete tende a se tornar um pesadelo de manutenção conforme o negócio cresce. As regras variam drasticamente:

* Algumas transportadoras cobram preço fixo.
* Outras utilizam cálculo de peso volumétrico (dimensões vs peso real).
* Outras são gratuitas (como retirada em loja).

A abordagem ingênua ("Bad Approach") frequentemente resulta em uma única classe de serviço inchada, repleta de condicionais if-else ou switch-case, violando princípios de design e tornando qualquer alteração arriscada.

## **🏗️ A Solução Arquitetural**

A solução adota o **Padrão Strategy** para encapsular cada regra de cálculo de frete em sua própria classe isolada. A arquitetura é composta por:

1. **Contexto (ShippingService):** Recebe o pedido e delega o cálculo para a estratégia ativa.
2. **Estratégia Abstrata (ShippingStrategy):** Define o contrato comum (calculateShippingCost) que todas as regras devem seguir.
3. **Estratégias Concretas:** Implementações específicas para cada regra de negócio (EconomySaver, HyperSpeed, StorePickup).
4. **Fábrica (ShippingStrategyFactory):** Centraliza a lógica de criação e decisão de qual estratégia instanciar.

## **🧩 Principais Padrões de Projeto**

### **1\. Strategy Pattern**

Permite trocar o algoritmo de cálculo de frete em tempo de execução. O ShippingService não precisa saber *como* o frete é calculado, apenas que ele *será* calculado por alguém competente.

**Exemplo (ShippingStrategy.java e HyperSpeedStrategy.java):**

```java
// Contrato comum  
public interface ShippingStrategy {  
    BigDecimal calculateShippingCost(Order order);  
}
```

```java
// Implementação concreta (Lógica de Peso Volumétrico)  
public class HyperSpeedStrategy implements ShippingStrategy {  
    public BigDecimal calculateShippingCost(Order order) {  
        // Cálculo do peso volumétrico: (A x L x C) / Divisor  
        Double volume = order.getHeight() * order.getWidth() * order.getLength();  
        double weightFactor = volume / 139;
    
        // Cobra pelo maior valor: peso real ou peso volumétrico  
        if (weightFactor > order.getWeight()) {  
            return BASE_RATE.multiply(BigDecimal.valueOf(weightFactor));  
        }  
        return BASE_RATE.multiply(BigDecimal.valueOf(order.getWeight()));  
    }  
}
```

### **2\. Simple Factory Idiom**

Isola a lógica de instanciação da lógica de negócio. O cliente solicita um tipo de frete e a fábrica decide qual classe concreta entregar.

**Exemplo (ShippingStrategyFactory.java):**

```java
public static ShippingStrategy getStrategy(ShippingType strategyType) {  
    return switch (strategyType) {  
        case ECONOMY_SAVER -> new EconomySaverStrategy();  
        case HYPER_SPEED -> new HyperSpeedStrategy();  
        case STORE_PICKUP -> new StorePickupStrategy();  
        default -> throw new IllegalArgumentException("Tipo de estratégia desconhecido");  
    };  
}
```


## **💎 Clean Code e SOLID**

O código foi estruturado seguindo as melhores práticas de desenvolvimento:

* **S \- Single Responsibility Principle (SRP):**
    * Cada classe de estratégia tem apenas **uma responsabilidade**: calcular o frete daquela modalidade específica. A classe Order é apenas um DTO (Data Transfer Object).
* **O \- Open/Closed Principle (OCP):**
    * O sistema está **aberto para extensão** (podemos adicionar InternationalStrategy criando apenas uma nova classe) mas **fechado para modificação** (não é necessário alterar o ShippingService ou estratégias existentes para adicionar novas regras).
* **L \- Liskov Substitution Principle (LSP):**
    * Qualquer implementação de ShippingStrategy pode substituir outra sem quebrar o funcionamento do ShippingService.
* **Nomes Significativos:** O código utiliza nomes que revelam intenção, como ```weightFactor```, ```BASE_RATE``` e ```calculateShippingCost```.

## **🚀 Como Rodar o Projeto**

Este projeto utiliza **Java 21** e **Gradle**.

### **Pré-requisitos**

* JDK 21 instalado e configurado no PATH.

### **Executando via Terminal**

1. Clone o repositório.
2. Navegue até a pasta raiz do projeto.
3. Execute a classe Main (que simula o frontend enviando um payload):

**Linux/Mac:**

```./gradlew run```

**Windows:**

```gradlew.bat run```

## **📊 Impactos da Arquitetura**

| Aspecto | Antes (If-Else / Monólito) | Depois (Strategy \+ Factory) |
| :---- | :---- | :---- |
| **Manutenibilidade** | **Baixa.** Alterar uma regra exige editar um arquivo gigante, com risco de quebrar regras vizinhas. | **Alta.** Alterações são isoladas em classes específicas. Bugs não "vazam" para outras estratégias. |
| **Escalabilidade** | **Limitada.** O código cresce verticalmente e se torna ilegível rapidamente. | **Ilimitada.** Adicionar 50 novas transportadoras significa apenas criar 50 novos arquivos pequenos e organizados. |
| **Testabilidade** | **Difícil.** Necessário testar todos os caminhos do if em um único teste complexo. | **Simples.** Cada estratégia pode ter seus testes unitários simples e isolados. |
| **Performance** | Levemente mais rápida (micro-otimização por não instanciar objetos). | Custo ínfimo de criação de objetos, amplamente compensado pela organização e facilidade de manutenção. |

