package com.jskillcloud.authsvc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    @NotBlank
    @Size(max = 15)
    private String username;
    @NotBlank
    @Size(max = 100)
    private String password;
    @Singular
    private Set<Role> roles;
    @JsonIgnore
    private Instant createdAt;
    @JsonIgnore
    private Instant updatedAt;
}
