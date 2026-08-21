package com.chitfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String memberName;
    private String mobileNumber;
    private String emailAddress;
    private String address;

    @ManyToMany
    @JoinTable(
            name = "member_groups",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<ChitGroup> chitGroups = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name != null ? name : memberName; }
    public void setName(String name) { this.name = name; this.memberName = name; }

    public String getMemberName() { return memberName != null ? memberName : name; }
    public void setMemberName(String memberName) { this.memberName = memberName; this.name = memberName; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Set<ChitGroup> getChitGroups() { return chitGroups; }
    public void setChitGroups(Set<ChitGroup> chitGroups) { this.chitGroups = chitGroups; }
}