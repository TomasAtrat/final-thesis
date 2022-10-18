package com.gmail.tomasatrat.backend.data.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "preparation")
public class Preparation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "starting_date")
    private Date startingDate;

    @Column(name = "ending_date")
    private Date endingDate;

    @Column(name = "is_finished")
    private Boolean isFinished;

    @Column(name = "has_problems")
    private Boolean hasProblems;

    @Column(name = "add_date")
    private Date addDate;

    @Column(name = "update_date")
    private Date updateDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_assigned", nullable = false)
    private User userAssigned;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderInfo order;

    @Column(name = "is_shipped")
    private Boolean isShipped;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public Boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(Boolean isFinished) {
        this.isFinished = isFinished;
    }

    public Boolean getHasProblems() {
        return hasProblems;
    }

    public void setHasProblems(Boolean hasProblems) {
        this.hasProblems = hasProblems;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public User getUserAssigned() {
        return userAssigned;
    }

    public void setUserAssigned(User userAssigned) {
        this.userAssigned = userAssigned;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }

    public Boolean getIsShipped() {
        return isShipped;
    }

    public void setIsShipped(Boolean isShipped) {
        this.isShipped = isShipped;
    }

}