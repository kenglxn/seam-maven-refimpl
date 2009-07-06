package org.open18.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Min;

@Entity
@Table(name = "SCORE")
public class Score implements java.io.Serializable {

    private Long id;
    private Round round;
    private Hole hole;
    private Boolean fairway;
    private Boolean greenInRegulation;
    private Integer putts;
    private Integer strokes;

    public Score() {}

    public Score(long id, Round round, Hole hole, Boolean fairway,
        Integer putts, Integer strokes) {
        this.id = id;
        this.round = round;
        this.hole = hole;
        this.fairway = fairway;
        this.putts = putts;
        this.strokes = strokes;
    }

    public Score(long id, Round round, Hole hole, Boolean fairway,
        Boolean greenInRegulation, Integer putts, Integer strokes) {
        this.id = id;
        this.round = round;
        this.hole = hole;
        this.fairway = fairway;
        this.greenInRegulation = greenInRegulation;
        this.putts = putts;
        this.strokes = strokes;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROUND_ID", nullable = false)
    public Round getRound() {
        return this.round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "HOLE_ID", nullable = false)
    public Hole getHole() {
        return this.hole;
    }

    public void setHole(Hole hole) {
        this.hole = hole;
    }

    @Column(name = "FAIRWAY")
    public Boolean getFairway() {
        return this.fairway;
    }

    public void setFairway(Boolean fairway) {
        this.fairway = fairway;
    }

    @Column(name = "GIR")
    public Boolean getGreenInRegulation() {
        return this.greenInRegulation;
    }

    public void setGreenInRegulation(Boolean greenInRegulation) {
        this.greenInRegulation = greenInRegulation;
    }

    @Column(name = "PUTTS")
    public Integer getPutts() {
        return this.putts;
    }

    public void setPutts(Integer putts) {
        this.putts = putts;
    }

	/**
	 * The number of raw strokes taken per hole. Must be
	 * an Integer wrapper to allow blank values in the UI.
	 */
    @Min(1)
    @NotNull
    @Column(name = "STROKES", nullable = false)
    public Integer getStrokes() {
        return this.strokes;
    }

    public void setStrokes(Integer strokes) {
        this.strokes = strokes;
    }

}
