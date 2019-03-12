package com.jskillcloud.authsvc.it.mapper;

import com.jskillcloud.authsvc.model.User;
import com.jskillcloud.authsvc.mapper.UserMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.text.html.Option;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    private User user1, user2;

    @Before
    public void setUp() {
        
        // sanity check
        userMapper.deleteAll();

        user1 = User.builder()
                .username("testUsername1")
                .password("testPassword1")
                .build();

        user2 = User.builder()
                .username("testUsername2")
                .password("testPassword2")
                .build();
    }

    @Test
    public void testUser() {
        // test save user1
        int count = userMapper.saveUser(user1);
        assertThat(count).isEqualTo(1);
        assertThat(user1.getId()).isNotNull();

        // test save user1 roles
        Map<String, Object> saveParams = new HashMap<>();
        saveParams.put("userId", user1.getId());
        saveParams.put("roleIds", Arrays.asList(Long.valueOf(4), Long.valueOf(5)));
        count = userMapper.saveUserRole(saveParams);
        assertThat(count).isEqualTo(2);

        // test save user2
        count = userMapper.saveUser(user2);
        assertThat(count).isEqualTo(1);
        assertThat(user2.getId()).isNotNull();

        // test save user2 role
        saveParams = new HashMap<>();
        saveParams.put("userId", user2.getId());
        saveParams.put("roleIds", Arrays.asList(Long.valueOf(5)));
        count = userMapper.saveUserRole(saveParams);
        assertThat(count).isEqualTo(1);

        // test exists
        boolean exists = userMapper.existsByUsername("testUser1");
        assertThat(exists);
        exists = userMapper.existsByUsername("testUser3");
        assertThat(!exists);

        // test findByUsername
        Optional<User> user1Found = userMapper.findByUsername("testUsername1");
        validateUser1(user1Found.get());
        Optional<User> user2Found = userMapper.findByUsername("testUsername2");
        validateUser2(user2Found.get());

        // test findById
        user2Found = userMapper.findById(user2Found.get().getId());
        validateUser2(user2Found.get());

        // test findByIdIn
        List<User> users = userMapper.findByIdIn(Arrays.asList(user1.getId(), user2.getId()));
        assertThat(users.size()).isEqualTo(2);
        // result sorted
        validateUser1(users.get(0));
        validateUser2(users.get(1));
    }

    private void validateUser1(User user1Found) {
        assertThat(user1Found.getId()).isEqualTo(user1.getId());
        assertThat(user1Found.getUsername()).isEqualTo(user1.getUsername());
        assertThat(user1Found.getPassword()).isEqualTo(user1.getPassword());
        assertThat(user1Found.getRoles().size()).isEqualTo(2);
    }

    private void validateUser2(User user2Found) {
        assertThat(user2Found.getId()).isEqualTo(user2.getId());
        assertThat(user2Found.getUsername()).isEqualTo(user2.getUsername());
        assertThat(user2Found.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user2Found.getRoles().size()).isEqualTo(1);
    }

    @After
    public void destroy () {
        userMapper.deleteAll();
    }
}
