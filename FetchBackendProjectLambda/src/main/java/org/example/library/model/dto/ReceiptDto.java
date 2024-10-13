package org.example.library.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReceiptDto {
    private String retailer;
    private String purchaseDate;
    private String purchaseTime;
    private List<ItemDto> items;
    private String total;
}
