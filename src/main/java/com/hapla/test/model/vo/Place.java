package com.hapla.test.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Place {
    private String name;
    private String address;
    private String phone;
    private String website;
    private String photoUrl;
    private double rating;
    private String description;

    // Getter, Setter, Constructor
}
