package org.smorgrav.agilityback;

import org.junit.Ignore;
import org.junit.Test;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Dog;
import org.smorgrav.agilityback.model.Equipage;
import org.smorgrav.agilityback.model.EquipageState;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.Person;
import org.smorgrav.agilityback.model.Size;
import org.smorgrav.agilityback.model.TrialType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class ScheduleEstimatorTest {

    @Ignore // Until new compare function for events are implemented
    @Test
    public void updateSchedule() {
        LocalDate startDate = LocalDate.of(2019, 10, 1);
        Competition comp = Competition.EMPTY.withId("1").withName("Test1");

        // First event
        LocalDateTime schedule1 = LocalDateTime.of(2019, 10, 1, 14, 23);
        Event event1 = Event.newTrial("1", "Custom name", Size.small, TrialType.JOpen, schedule1);
        comp.addEvent(event1);

        // Second event three minutes after (should be possible with only two equipages)
        LocalDateTime schedule2 = LocalDateTime.of(2019, 10, 1, 14, 26);
        Event event2 = Event.newTrial("2", "Custom name 2", Size.medium, TrialType.JOpen, schedule2);
        comp.addEvent(event2);

        // Assert on preconditions
        assertEquals(schedule1, event1.estimatedEnd());
        assertEquals(schedule2, event2.estimatedEnd());

        // Add equipages - two in first event, one in last
        Equipage ep1 = Equipage.EMPTY.withId("1").withDog(Dog.EMPTY.withName("doggy")).withHandler(Person.EMPTY.withName("handy")).withStartNumber(1);
        Equipage ep2 = Equipage.EMPTY.withId("2").withDog(Dog.EMPTY.withName("doggx")).withHandler(Person.EMPTY.withName("handx")).withStartNumber(2);
        Equipage ep3 = Equipage.EMPTY.withId("3").withDog(Dog.EMPTY.withName("doggz")).withHandler(Person.EMPTY.withName("handz")).withStartNumber(3);

        event1.add(ep1);
        event1.add(ep2);
        event2.add(ep3);

        // Update schedule and assert on end of event estimation
        event1.updateSchedule();
        assertEquals(schedule1.plus(Duration.ofSeconds(120)), event1.estimatedEnd());
        assertEquals(schedule2.plus(Duration.ofSeconds(60)), event2.estimatedEnd());

        // Now update equipage times so that eq1 is actually using 2 minutes - event 2 must then be rescheduled
        ep1.setActualStart(schedule1);
        ep1.setRunTime(120);
        ep1.setState(EquipageState.FINISHED);

        // Update schedule again and see that
        comp.updateSchedule();
        assertEquals(schedule1.plus(Duration.ofSeconds(180)), event1.estimatedEnd());

        // Now check that estimated start has been moved and that eq3 has got the later estimated start
        assertEquals(schedule2.plus(Duration.ofSeconds(60)), event2.estimatedStart());
        assertEquals(schedule2.plus(Duration.ofSeconds(120)), event2.estimatedEnd());
        assertEquals(schedule2.plus(Duration.ofSeconds(60)), ep3.estimatedStart());
    }
}