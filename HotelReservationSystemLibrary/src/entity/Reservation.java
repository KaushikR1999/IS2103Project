/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import util.enumeration.StatusEnum;
import util.enumeration.ReservationTypeEnum;

/**
 *
 * @author kaushikr
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    private Date startDate;
    private Date endDate;
    private Date bookingDate;
    private LocalTime bookingTime;
    private StatusEnum statusEnum;
    private Long reservationFee;
    private ReservationTypeEnum reservationTypeEnum;
    

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the bookingDate
     */
    public Date getBookingDate() {
        return bookingDate;
    }

    /**
     * @param bookingDate the bookingDate to set
     */
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    /**
     * @return the bookingTime
     */
    public LocalTime getBookingTime() {
        return bookingTime;
    }

    /**
     * @param bookingTime the bookingTime to set
     */
    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    /**
     * @return the statusEnum
     */
    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

    /**
     * @param statusEnum the statusEnum to set
     */
    public void setStatusEnum(StatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    /**
     * @return the reservationFee
     */
    public Long getReservationFee() {
        return reservationFee;
    }

    /**
     * @param reservationFee the reservationFee to set
     */
    public void setReservationFee(Long reservationFee) {
        this.reservationFee = reservationFee;
    }

    /**
     * @return the reservationTypeEnum
     */
    public ReservationTypeEnum getReservationTypeEnum() {
        return reservationTypeEnum;
    }

    /**
     * @param reservationTypeEnum the reservationTypeEnum to set
     */
    public void setReservationTypeEnum(ReservationTypeEnum reservationTypeEnum) {
        this.reservationTypeEnum = reservationTypeEnum;
    }
    
}
