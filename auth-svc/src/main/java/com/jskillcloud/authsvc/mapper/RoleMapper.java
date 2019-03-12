package com.jskillcloud.authsvc.mapper;

import com.jskillcloud.authsvc.model.Role;
import com.jskillcloud.authsvc.model.RoleName;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface RoleMapper {
    Optional<Role> findByName(RoleName roleName);
}
