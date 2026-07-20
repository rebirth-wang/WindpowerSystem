package com.fastbee.http.model;

import java.util.List;

import lombok.Data;

@Data
public class MapMarker {

    private List<marker> markers;

    @Data
    public static class marker {
        private String name;
        private Integer value;
        private List<Float> position;
    }
}
