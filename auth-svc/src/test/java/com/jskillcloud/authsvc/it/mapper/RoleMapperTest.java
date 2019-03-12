package com.jskillcloud.authsvc.it.mapper;

import com.jskillcloud.authsvc.mapper.RoleMapper;
import com.jskillcloud.authsvc.model.Role;
import com.jskillcloud.authsvc.model.RoleName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleMapperTest {
    @Autowired
    private RoleMapper roleMapper;

    @Test
    public void testFindByName() {
        Role adminRole = roleMapper.findByName(RoleName.ROLE_ADMIN).get();
        assertThat(adminRole).isNotNull();
        assertThat(adminRole.getId()).isEqualTo(5);
        assertThat(adminRole.getName()).isEqualTo(RoleName.ROLE_ADMIN);
    }
}
