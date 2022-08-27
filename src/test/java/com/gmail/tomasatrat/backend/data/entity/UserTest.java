package com.gmail.tomasatrat.backend.data.entity;

import com.gmail.tomasatrat.backend.repositories.UserRepository;
import com.gmail.tomasatrat.backend.service.UserService;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;


public class UserTest {

    public UserTest(UserService userService){
        this.userService = userService;
    }

    private UserService userService;

    @Test
    public void userShouldBeCreated() {
        var initialCount = userService.count();

        User o1 = new User();
        o1.setPasswordHash("hash");
        o1.setEmail("abc@vaadin.co");
        o1.setFirstName("first");
        o1.setLastName("last");
        o1.setRole("admin");
        o1.setUsername("username");

        userService.createNew(o1);

        var second = userService.count();
        assertEquals(initialCount + 1, second);
    }

    @Test
    public void findByUsernameShouldEquals() {
        User o1 = new User();
        o1.setPasswordHash("hash");
        o1.setEmail("abc@vaadin.co");
        o1.setFirstName("first");
        o1.setLastName("last");
        o1.setRole("admin");
        o1.setUsername("username");

        userService.createNew(o1);

        System.out.println(userService.count());

        var user2 = userService.findByUsername(o1.getUsername());

        assertEquals(o1, user2);

    }


}
