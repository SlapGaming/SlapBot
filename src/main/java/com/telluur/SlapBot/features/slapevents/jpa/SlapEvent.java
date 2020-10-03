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
        @NamedQuery(name = "LTGGame.findAllIds",
                query = "SELECT id FROM SlapEvent"),
})
public class SlapEvent {
    @Id
    String description;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    DateTime start, end;
}
