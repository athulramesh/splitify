package com.splitify.splitify.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER")
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Credential credential;

    public void addCredential(String password) {
        setCredential(Credential.builder().user(this).password(password).build());
    }
}
