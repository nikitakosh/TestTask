package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class Product {
    private String certificateDocument;
    private LocalDate certificateDocumentDate;
    private String certificateDocumentNumber;
    private String ownerInn;
    private String producerInn;
    private LocalDate productionDate;
    private String tnvedCode;
    private String uitCode;
    private String uituCode;
}
