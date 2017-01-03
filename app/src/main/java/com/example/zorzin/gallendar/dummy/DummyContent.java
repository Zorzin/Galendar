package com.example.zorzin.gallendar.dummy;

import com.google.api.services.calendar.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
    }

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static DummyItem createDummyItem(int position, Event event) {
        Date date = null;
        if (event.getStart().getDateTime()!=null)
        {
            date = new Date(event.getStart().getDateTime().getValue());
        }
        else
        {
            date = new Date(event.getStart().getDate().getValue());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = dateFormat.format(date);
        if (event.getEnd().getDateTime()!=null)
        {
            date = new Date(event.getEnd().getDateTime().getValue());
        }
        else
        {
            date = new Date(event.getEnd().getDate().getValue());
        }
        String endDate = dateFormat.format(date);
        return new DummyItem(String.valueOf(position), event.getSummary() + " " + startDate, makeDetails(position,event), event.getSummary(), event.getDescription(), startDate, endDate, event.getLocation());
    }

    private static String makeDetails(int position, Event event) {
        StringBuilder builder = new StringBuilder();
        builder.append(event.getSummary()).append(position);

        Date date = null;

        if (event.getStart().getDateTime()!=null)
        {
            date = new Date(event.getStart().getDateTime().getValue());
        }
        else
        {
            date = new Date(event.getStart().getDate().getValue());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = dateFormat.format(date);

        if (event.getEnd().getDateTime()!=null)
        {
            date = new Date(event.getEnd().getDateTime().getValue());
        }
        else
        {
            date = new Date(event.getEnd().getDate().getValue());
        }
        String endDate = dateFormat.format(date);


        builder.append(
                "\n" + startDate +
                "\n" + endDate +
                "\n" + event.getStatus() +
                "\n" + event.getDescription()

        );
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getPlace() {
            return place;
        }

        private final String title;
        private final String description;
        private final String startDate;
        private final String endDate;
        private final String place;

        public DummyItem(String id, String content, String details, String title, String description, String startDate, String endDate, String place) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.title = title;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.place = place;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
