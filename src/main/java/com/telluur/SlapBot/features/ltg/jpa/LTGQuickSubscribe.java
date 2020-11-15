package com.telluur.SlapBot.features.ltg.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ltg_quicksubscribe")
public class LTGQuickSubscribe {
    @Id
    private String id; //This is the message id
    private String roleId;
}
