package com.laioffer.matrix;

import java.util.ArrayList;
import java.util.List;

public class DataService {

    public static List<Event> getEventData() {
        List<Event> eventData = new ArrayList<Event>();
        for (int i = 0; i < 10; ++i) {
            eventData.add(
                    new Event("Resident", "333 River Street, NJ 07030",
                            "This is a huge pig"));
        }
        return eventData;
    }
}
