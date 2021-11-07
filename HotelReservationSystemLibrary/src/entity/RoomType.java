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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Roy Chua
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    
    @Column(nullable = false, unique = true)
    private String typeName;
    
    @Column (nullable = false)
    private String description;
    
    @Column (nullable = false)
    private double size;
    
    @Column (nullable = false)
    private int bed;
    
    @Column (nullable = false)
    private int capacity;
    
    @Column (nullable = false)
    private List<String> amenities;
    
    @Column(nullable = false)
    private boolean assignable;
    
    @OneToOne (fetch = FetchType.LAZY, cascade = {})
    private RoomType nextHighestRoomType;
    
    @OneToMany (fetch = FetchType.LAZY, cascade = {})
    private List<RoomRate> roomRates;

    public RoomType() {
        roomRates = new ArrayList<>();
        amenities = new ArrayList<>();
        assignable = true;
    }

    public RoomType(String typeName, String description, double size, int bed, int capacity) {
        this();
        this.typeName = typeName;
        this.description = description;
        this.size = size;
        this.bed = bed;
        this.capacity = capacity;
        this.assignable = assignable;
    }

    public RoomType getNextHighestRoomType() {
        return nextHighestRoomType;
    }

    public void setNextHighestRoomType(RoomType nextHighestRoomType) {
        this.nextHighestRoomType = nextHighestRoomType;
    } 

    public boolean isAssignable() {
        return assignable;
    }

    public void setAssignable(boolean assignable) {
        this.assignable = assignable;
    }

    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getBed() {
        return bed;
    }

    public void setBed(int bed) {
        this.bed = bed;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomTypeId fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + roomTypeId + " ]";
    }
    
}
