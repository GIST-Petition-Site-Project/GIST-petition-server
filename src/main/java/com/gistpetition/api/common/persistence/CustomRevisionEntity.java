package com.gistpetition.api.common.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Proxy;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@RevisionEntity(CustomRevisionListener.class)
@Proxy(lazy = false)
@Table(name = "REVINFO")
public class CustomRevisionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name = "REV")
    private Long id;

    @RevisionTimestamp
    @Column(name = "REVTSTMP")
    private Long timestamp;

    private Long userId;

    @Transient
    public Date getRevisionDate() {
        return new Date(timestamp);
    }

    @Override
    public String toString() {
        return String.format("CustomRevisionEntity(id = %d, revisionDate = %s, userId = %d)",
                id, DateFormat.getDateTimeInstance().format(getRevisionDate()), userId);
    }
}