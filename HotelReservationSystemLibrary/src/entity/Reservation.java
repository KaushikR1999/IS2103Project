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
import javax.persistence.OneToMany;
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
    @Column(name="RESERVATION_ID")
    private Long reservationId;
    
    @Column(name = "START_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date startDate;
    
    @Column(name = "END_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date endDate;
    
    @Column(name = "BOOKING_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date bookingDate;
    
    @Column(name = "BOOKING_TIME", nullable = false)
    @Temporal(TemporalType.TIME)
    @NotNull
    private Date bookingTime;
    
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ReservationStatusEnum status;
    
    @Column(name = "RESERVATION_FEE", nullable = false)
    @NotNull
    private Double reservationFee;
    
    @Column(name = "RESERVATION_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ReservationTypeEnum reservationType;
    
    @ManyToMany
    private List <Room> rooms;
    
    @OneToMany(mappedBy="reservation")
    private List <RoomType> roomTypes;
    
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true)
    private Guest guest;

    public Reservation() {
        rooms = new ArrayList<>();
        roomTypes = new ArrayList<>();
    }

    public Reservation(Date startDate, Date endDate, Date bookingDate, Date bookingTime, ReservationStatusEnum status, Double reservationFee, ReservationTypeEnum reservationType, Guest guest) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.status = status;
        this.reservationFee = reservationFee;
        this.reservationType = reservationType;
        this.guest = guest;
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
    public Date getBookingTime() {
        return bookingTime;
    }

    /**
     * @param bookingTime the bookingTime to set
     */
    public void setBookingTime(Date bookingTime) {
        this.bookingTime = bookingTime;
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
     * @return the reservationFee
     */
    public Double getReservationFee() {
        return reservationFee;
    }

    /**
     * @param reservationFee the reservationFee to set
     */
    public void setReservationFee(Double reservationFee) {
        this.reservationFee = reservationFee;
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
     * @return the rooms
     */
    public List <Room> getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(List <Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * @return the roomTypes
     */
    public List <RoomType> getRoomTypes() {
        return roomTypes;
    }

    /**
     * @param roomTypes the roomTypes to set
     */
    public void setRoomTypes(List <RoomType> roomTypes) {
        this.roomTypes = roomTypes;
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
    
}
