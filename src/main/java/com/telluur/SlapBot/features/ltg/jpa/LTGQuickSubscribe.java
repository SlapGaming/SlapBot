package com.telluur.SlapBot.features.ltg.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
