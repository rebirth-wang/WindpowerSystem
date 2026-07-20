package com.fastbee.media.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Data;

@Data
public class RecordList {
    String deviceID;
    Integer sumNum;
    private List<RecordItem> recordItems = new ArrayList<>();

    public void addItem(RecordItem item) {
        this.recordItems.add(item);
    }

    public void mergeItems() {
        if (recordItems.isEmpty()) {
            return;
        }
        recordItems.sort(Comparator.naturalOrder());
        List<RecordItem> temp = new ArrayList<>();
        RecordItem current = recordItems.get(0);
        long start = current.getStart();
        long end = current.getEnd();
        for (int i = 1; i < recordItems.size(); i++) {
            RecordItem item = recordItems.get(i);
            if (end >= item.getStart()) {
                end = Math.max(end, item.getEnd());
            } else {
                temp.add(buildItem(start, end));
                start = item.getStart();
                end = item.getEnd();
            }
        }
        temp.add(buildItem(start, end));
        this.recordItems = temp;
    }

    private RecordItem buildItem(long start, long end) {
        RecordItem item = new RecordItem();
        item.setStart(start);
        item.setEnd(end);
        return item;
    }
}
