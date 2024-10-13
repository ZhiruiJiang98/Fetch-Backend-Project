package org.example.library.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AwardedPointsDto {
    public String id;
    public String points;
}
