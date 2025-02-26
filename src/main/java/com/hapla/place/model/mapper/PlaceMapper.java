package com.hapla.place.model.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlaceMapper {
    int countStar(String placeId);
}
