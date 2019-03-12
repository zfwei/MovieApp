package com.jskillcloud.authsvc.mapper;

import com.jskillcloud.authsvc.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    List<User> findByIdIn(@Param("userIds") List<Long> userIds);

    boolean existsByUsername(String username);

    int saveUser(@Param("user")User user);

    // userId -> string
    // roleIds -> List
    int saveUserRole(Map<String, Object> saveParams);

    // for testing
    // will delete both user and roles
    boolean deleteAll();
}
