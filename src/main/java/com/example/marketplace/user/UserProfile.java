package com.example.marketplace.user;

import jakarta.persistence.*;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    private Long id;                       // тот же id, что у пользователя

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private User owner;

    private String specialization;
    private String city;
    @Column(length = 2000) private String about;
}
