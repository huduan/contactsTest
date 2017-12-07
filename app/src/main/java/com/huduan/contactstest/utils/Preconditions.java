package com.huduan.contactstest.utils;

import java.util.List;

/**
 * Created by huduan on 17-12-2.
 */

public class Preconditions {

    public static <T> void checkNotNull(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("list should not be null");
        }
    }
}
