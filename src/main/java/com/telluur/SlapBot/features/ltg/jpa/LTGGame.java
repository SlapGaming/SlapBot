package com.telluur.SlapBot.features.ltg.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ltg_game")
@NamedQueries({
        @NamedQuery(name = "LTGGame.findAllIds",
                query = "SELECT id FROM LTGGame"),
})
public class LTGGame {
    @Id
    private String id;
    private String abbreviation, fullName;
}
