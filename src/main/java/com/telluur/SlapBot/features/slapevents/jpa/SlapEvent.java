package com.telluur.SlapBot.features.slapevents.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_info")
@NamedQueries({
        @NamedQuery(name = "SlapEvent.findAllEvents",
                query = "SELECT e FROM SlapEvent e"),
        @NamedQuery(name = "SlapEvent.findEventById",
                query = "SELECT e FROM SlapEvent e WHERE e.id = :id"),
        @NamedQuery(name = "SlapEvent.findFutureEventsOrderedByStart",
                query = "SELECT e FROM SlapEvent e WHERE e.end > CURRENT_TIMESTAMP ORDER BY e.start"),
})
public class SlapEvent {
    @Id
    private String id;
    private String name, description;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime start, end;

    public SlapEvent(String id) {
        this.id = id;
    }

    /**
     * Checks whether the event has all non null fields and the end is chronologically after start date.
     *
     * @return whether event is valid
     */
    public boolean isValid() {
        return id != null && name != null && description != null && start != null && end != null && end.isAfter(start);
    }
}
