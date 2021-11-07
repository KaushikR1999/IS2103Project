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
import util.enumeration.RoomStatusEnum;

/**
 *
 * @author Roy Chua
 */
@Entity
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    
    @Column(length = 2, nullable = false)
    private int floor;
    
    @Column(length =  2, nullable = false)
    private int seqNum;
    
    @Column(nullable = false)
    private RoomStatusEnum roomStatus;
   
    @Column(nullable = false)
    private boolean assignable;
    
    @ManyToOne(optional = false, cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private RoomType roomType;
    
    @ManyToMany(mappedBy = "rooms", cascade = {}, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    public Room() {
        reservations = new ArrayList<>();
        assignable = true;
    }

    public Room(int floor, int seqNum, RoomStatusEnum roomStatus, RoomType roomType) {
        this();
        this.floor = floor;
        this.seqNum = seqNum;
        this.roomStatus = RoomStatusEnum.AVAILABLE;
        this.roomType = roomType;
    }
    
    public String getRoomNumber() {
        String s1 = Integer.toString(this.getFloor());
        String s2 = Integer.toString(this.getSeqNum());
        
        String s = s1 + s2;
        return s;
    }

    public int getFloor() {
        return floor;
    }


    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public RoomStatusEnum getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatusEnum roomStatus) {
        this.roomStatus = roomStatus;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
    
    

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomId != null ? roomId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomId fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.roomId == null && other.roomId != null) || (this.roomId != null && !this.roomId.equals(other.roomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Room[ id=" + roomId + " ]";
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
