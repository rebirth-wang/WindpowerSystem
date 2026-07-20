package com.fastbee.media.model;

import lombok.Data;

@Data
public class RecordItem implements Comparable<RecordItem> {
    Long start;
    Long end;

    @Override
    public int compareTo(RecordItem item) {
        return Long.compare(start, item.getStart());
    }
}
