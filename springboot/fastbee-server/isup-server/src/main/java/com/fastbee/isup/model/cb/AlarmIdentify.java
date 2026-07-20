package com.fastbee.isup.model.cb;

import java.util.List;

import lombok.Data;

@Data
public class AlarmIdentify {
    private String relationId;
    private Double maxsimilarity;
    private Integer similarityRange;
    private List<Candidate> candidate;
}
