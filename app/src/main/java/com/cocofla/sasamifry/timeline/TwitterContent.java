package com.cocofla.sasamifry.timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.Status;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class TwitterContent {

    /**
     * An array of items.
     */
    public static final List<Tweet> ITEMS = new ArrayList<Tweet>();

    /**
     * A map of items, by ID.
     */
    public static final Map<Long, Tweet> ITEM_MAP = new HashMap<Long, Tweet>();

    public static void addItem(Tweet item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.status.getId(), item);
    }

    /**
     * A item of the List
     */
    public static class Tweet {
        public final Status status;

        public Tweet(Status status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status.getText();
        }
    }
}
