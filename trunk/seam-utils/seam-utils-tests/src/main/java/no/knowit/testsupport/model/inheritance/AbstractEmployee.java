package no.knowit.testsupport.model.inheritance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.Version;

import static javax.persistence.TemporalType.DATE;

@MappedSuperclass
public abstract class AbstractEmployee {
    @Id private Long id;
    @Version  private Long version;
    private String name;
    @Temporal(DATE)
    @Column(name="S_DATE")
    private Date startDate;
    transient private long daysHired; 
}