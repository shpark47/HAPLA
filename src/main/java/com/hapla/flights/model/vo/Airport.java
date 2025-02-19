package com.hapla.flights.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airport {
    private String iataCode;
    private String localName;
    private String latitude;
    private String longitude;
    private String cityCode;
}
