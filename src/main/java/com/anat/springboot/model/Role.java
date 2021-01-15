package com.anat.springboot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@Data
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq_gen")
    @SequenceGenerator(name = "role_seq_gen", sequenceName = "ROLE_GEN")
    private long id;
    private String roleName;
    private boolean removed;

    public Role(String roleName) {
        this.roleName = roleName;
        this.removed = true;
    }

    public String getSimpleRoleName(){
        return roleName.replace("ROLE_", "");
    }

    @Override
    public String getAuthority() {
        return roleName;
    }
}
