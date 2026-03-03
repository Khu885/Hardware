package com.hardwareaplications.hardware;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="usersDB")
public class AdmUsers {
    @Id
    private int id;
    private String username;
    private String password;
}
