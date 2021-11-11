/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import util.enumeration.ReservationStatusEnum;
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
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date startDate;
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date endDate;
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date bookingDateTime;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ReservationStatusEnum status;
    
    @Column(nullable = false)
    @NotNull
    private double totalReservationFee;
    
    @Column(nullable = false)
    @NotNull
    private int numberOfRooms;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ReservationTypeEnum reservationType;
    
    @ManyToMany(cascade = {}, fetch = FetchType.LAZY)
    private List<Room> rooms;
    
    @ManyToOne(optional = true, cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Guest guest;
    
    @ManyToOne(optional = true, cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private RoomType roomType;

    public Reservation() {
        this.rooms = new ArrayList<> ();
    }

    public Reservation(Date startDate, Date endDate, Date bookingDateTime, ReservationStatusEnum status, Double reservationFee, ReservationTypeEnum reservationType, RoomType roomType, int numberOfRooms) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookingDateTime = bookingDateTime;
        this.status = status;
        this.totalReservationFee = reservationFee;
        this.reservationType = reservationType;
        this.roomType = roomType;
        this.numberOfRooms = numberOfRooms;
    }
    
    

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
     * @return the bookingDateTime
     */
    public Date getBookingDateTime() {
        return bookingDateTime;
    }

    /**
     * @param bookingDateTime the bookingDateTime to set
     */
    public void setBookingDateTime(Date bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    /**
     * @return the status
     */
    public ReservationStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ReservationStatusEnum status) {
        this.status = status;
    }

    /**
     * @return the totalReservationFee
     */
    public double getTotalReservationFee() {
        return totalReservationFee;
    }

    /**
     * @param totalReservationFee the totalReservationFee to set
     */
    public void setTotalReservationFee(double totalReservationFee) {
        this.totalReservationFee = totalReservationFee;
    }

    /**
     * @return the reservationType
     */
    public ReservationTypeEnum getReservationType() {
        return reservationType;
    }

    /**
     * @param reservationType the reservationType to set
     */
    public void setReservationType(ReservationTypeEnum reservationType) {
        this.reservationType = reservationType;
    }

    /**
     * @return the guest
     */
    public Guest getGuest() {
        return guest;
    }

    /**
     * @param guest the guest to set
     */
    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    /**
     * @return the rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * @return the roomType
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * @param roomType the roomType to set
     */
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    /**
     * @return the numberOfRooms
     */
    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    /**
     * @param numberOfRooms the numberOfRooms to set
     */
    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }
    
}
