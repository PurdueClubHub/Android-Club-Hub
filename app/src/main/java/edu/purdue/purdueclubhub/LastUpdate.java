package edu.purdue.purdueclubhub;

/**
 * Created by zsoehren on 3/23/2015.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Date;


public class LastUpdate {
    long latest;

    public LastUpdate(){
        Date now = new Date();
        latest = now.getTime();
    }

    public Map<String, Long> toMap()
    {
        Map<String, Long> r = new HashMap<String, Long>();

        r.put("lastUpdate",latest);

        return r;
    }
}
