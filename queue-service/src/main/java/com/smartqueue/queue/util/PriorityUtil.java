package com.smartqueue.queue.util;

import com.smartqueue.queue.entity.PriorityType;

public class PriorityUtil {

    public static int getPriorityWeight(PriorityType priority) {

        if (priority == null) return 3;

        switch (priority) {
            case EMERGENCY:
                return 1;
            case PREMIUM:
                return 2;
            case NORMAL:
            default:
                return 3;
        }
    }
}