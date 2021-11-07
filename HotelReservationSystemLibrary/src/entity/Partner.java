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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author yuenz
 */
@Entity
public class Partner implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;
    
    @OneToMany(cascade = {}, fetch = FetchType.LAZY)
    private List<Reservation> partnerReservations;
    
    @Column(nullable = false, length = 32, unique = true)
    @NotNull
    @Size(min = 6, max = 32)
    private String username;
    
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 8, max = 32)
    private String password;
    
    @Column(nullable = false, unique = true)
    @NotNull
    private String organisation;

    public Partner() {
        partnerReservations = new ArrayList<>();
    }

    public Partner(String username, String password, String organisation) {
        this();
        this.username = username;
        this.password = password;
        this.organisation = organisation;
    }

    public List<Reservation> getPartnerReservations() {
        return partnerReservations;
    }

    public void setPartnerReservations(List<Reservation> partnerReservations) {
        this.partnerReservations = partnerReservations;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerId != null ? partnerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerId fields are not set
        if (!(object instanceof Partner)) {
            return false;
        }
        Partner other = (Partner) object;
        if ((this.partnerId == null && other.partnerId != null) || (this.partnerId != null && !this.partnerId.equals(other.partnerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Partner[ id=" + partnerId + " ]";
    }

    /**
     * @return the organisation
     */
    public String getOrganisation() {
        return organisation;
    }

    /**
     * @param organisation the organisation to set
     */
    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }
    
}
