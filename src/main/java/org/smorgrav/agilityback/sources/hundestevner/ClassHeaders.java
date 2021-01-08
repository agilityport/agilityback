package org.smorgrav.agilityback.sources.hundestevner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.Size;
import org.smorgrav.agilityback.model.TrialType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassHeaders {

    @JsonProperty("results")
    public List<ClassHeader> results;

    static ClassHeaders fromJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ClassHeaders.class);
    }

    List<Event> toEventInfo() {
        ArrayList<Event> list = new ArrayList<>();
        results.forEach(classHeader -> {
            list.add(Event.newTrial("" + classHeader.id, classHeader.eventClassName, classHeader.size(), TrialType.A1, LocalDateTime.now()));
        });

        return list;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ClassHeader {
        @JsonProperty("id")
        public int id;

        @JsonProperty("eventClassName")
        public String eventClassName;

        @JsonProperty("eventClassTypeNameShort")
        public String eventClassTypeNameShort;

        @JsonProperty("eventSizeNameShort")
        public String eventSizeNameShort;

        @JsonProperty("judgeId")
        public int judgeId;

        @JsonProperty("judgeName")
        public String judgeName;

        @JsonProperty("standardCourseTime")
        public double standardCourseTime;

        @JsonProperty("courseLength")
        public double courseLength;

        @JsonProperty("started")
        public boolean started;

        @JsonProperty("finished")
        public boolean finished;

        @JsonProperty("eventDate")
        public String eventDate;

        @JsonProperty("entires")
        public int entries;

        @JsonProperty("results")
        public int results;

        Size size() {
            if (eventSizeNameShort.equals("small")) return Size.small;
            return Size.large;
        }

    }

}
