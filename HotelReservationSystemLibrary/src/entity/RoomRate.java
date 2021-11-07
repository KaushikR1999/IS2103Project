/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import util.enumeration.RoomRateTypeEnum;

/**
 *
 * @author Roy Chua
 */
@Entity
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull (message = "Room rate type cannot be null")
    private RoomRateTypeEnum roomRateType;
    
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;
    
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;
    
    @Column(nullable = false)
    private String name;
    
    @Column (nullable = false)
    private double ratePerNight;
    
    @Column(nullable = false)
    private boolean assignable;

    public RoomRate() {
        assignable = true;
    }

    public RoomRate(RoomRateTypeEnum roomRateType, Date startDate, Date endDate, String name, double ratePerNight) {
        this();
        this.roomRateType = roomRateType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.ratePerNight = ratePerNight;
    }
    

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(double ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomRateTypeEnum getRoomRateType() {
        return roomRateType;
    }

    public void setRoomRateType(RoomRateTypeEnum roomRateType) {
        this.roomRateType = roomRateType;
    }

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRates[ id=" + roomRateId + " ]";
    }

    /**
     * @return the assignable
     */
    public boolean isAssignable() {
        return assignable;
    }

    /**
     * @param assignable the assignable to set
     */
    public void setAssignable(boolean assignable) {
        this.assignable = assignable;
    }
    
}
