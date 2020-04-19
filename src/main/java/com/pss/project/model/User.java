package com.pss.project.model;

import com.pss.project.util.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 30)
    private String name;

    @NotNull
    @Size(min = 3, max = 30)
    private String lastName;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, " +
                    "at least one lower case letter, " +
                    "at least one upper case letter,  " +
                    "at least one special character, " +
                    "no whitespace are allowed within " +
                    "and there must be at least eight places at all")
    private String password;

    @NotNull
    @Size(min = 3, max = 30)
    private String companyName;

    @NotNull
    @Size(min = 3, max = 30)
    private String companyAddress;

    @NotNull
    @Size(min = 10)
    private String companyNip;

    private Boolean status = true;
    private LocalDate registrationDate = LocalDate.now();
    private Role role = Role.USER;

    public User(String name, String lastName, String email, String password,
                String companyName, String companyAddress, String companyNip) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyNip = companyNip;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authority = new HashSet<>();
        authority.add(new SimpleGrantedAuthority(role.name()));

        return authority;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
