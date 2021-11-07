/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author kaushikr
 */
@Entity
public class ReservationLineItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationLineItemId;
    
    @Column(nullable = false)
    @NotNull
    private double reservationFee;
    
    @ManyToOne(optional = false, cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reservation reservation;
    
    @OneToOne(cascade = {}, fetch = FetchType.LAZY)
    private Room room;
    
    @OneToMany(cascade = {}, fetch = FetchType.LAZY)
    private List <RoomType> bookedRoomTypes;

    public ReservationLineItem() {
        bookedRoomTypes = new ArrayList<>();
    }

    public ReservationLineItem(double reservationFee, Reservation reservation, Room room) {
        this();
        this.reservationFee = reservationFee;
        this.reservation = reservation;
        this.room = room;
    }

    public Long getReservationLineItemId() {
        return reservationLineItemId;
    }

    public void setReservationLineItemId(Long reservationLineItemId) {
        this.reservationLineItemId = reservationLineItemId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationLineItemId != null ? reservationLineItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationLineItemId fields are not set
        if (!(object instanceof ReservationLineItem)) {
            return false;
        }
        ReservationLineItem other = (ReservationLineItem) object;
        if ((this.reservationLineItemId == null && other.reservationLineItemId != null) || (this.reservationLineItemId != null && !this.reservationLineItemId.equals(other.reservationLineItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservationLineItem[ id=" + reservationLineItemId + " ]";
    }

    /**
     * @return the reservationFee
     */
    public double getReservationFee() {
        return reservationFee;
    }

    /**
     * @param reservationFee the reservationFee to set
     */
    public void setReservationFee(double reservationFee) {
        this.reservationFee = reservationFee;
    }

    /**
     * @return the reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * @param reservation the reservation to set
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    /**
     * @return the rooms
     */
    public Room getRoom() {
        return room;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRoom(Room rooms) {
        this.room = room;
    }

    /**
     * @return the bookedRoomTypes
     */
    public List<RoomType> getBookedRoomTypes() {
        return bookedRoomTypes;
    }

    /**
     * @param bookedRoomTypes the bookedRoomTypes to set
     */
    public void setBookedRoomTypes(List<RoomType> bookedRoomTypes) {
        this.bookedRoomTypes = bookedRoomTypes;
    }
}
