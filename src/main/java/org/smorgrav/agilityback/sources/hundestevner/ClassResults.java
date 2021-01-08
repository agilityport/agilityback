package org.smorgrav.agilityback.sources.hundestevner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.smorgrav.agilityback.model.Equipage;
import org.smorgrav.agilityback.model.EquipageState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassResults {

    @JsonProperty("results")
    public List<ClassResult> results;

    static ClassResults fromJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ClassResults.class);
    }

    private static EquipageState toState(ClassResult result) {
        if (result.disqualified) return EquipageState.DISQUALIFIED;
        if (result.noShow) return EquipageState.NOSHOW;
        if (!result.finished && !result.here && !result.inHeat) return EquipageState.WAITING;
        if (!result.finished && !result.inHeat) return EquipageState.HERE;
        return result.finished ? EquipageState.FINISHED : EquipageState.INHEAT;
    }

    public List<Equipage> toEventEquipageList() throws IOException {
        List<Equipage> list = new ArrayList<>();
        results.forEach(result -> {
            Equipage ee = null;
            list.add(ee);
        });

        return list;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ClassResult {
        @JsonProperty("id")
        public int id;

        @JsonProperty("dogId")
        public int dogId;

        @JsonProperty("dogName")
        public String dogName;

        @JsonProperty("breedName")
        public String breedName;

        @JsonProperty("handlerId")
        public int handlerId;

        @JsonProperty("handlerName")
        public String handlerName;

        @JsonProperty("handlerAltName")
        public String handlerAltName;

        @JsonProperty("startNumber")
        public int startNumber;

        @JsonProperty("place")
        public int place;

        @JsonProperty("fault")
        public double fault;

        @JsonProperty("faultRefusal")
        public double faultRefusal;

        @JsonProperty("timeFault")
        public double timeFault;

        @JsonProperty("runTime")
        public double runTime;

        @JsonProperty("disqualified")
        public boolean disqualified;

        @JsonProperty("noShow")
        public boolean noShow;

        @JsonProperty("here")
        public boolean here;

        @JsonProperty("inHeat")
        public boolean inHeat;

        @JsonProperty("finished")
        public boolean finished;
    }
}
