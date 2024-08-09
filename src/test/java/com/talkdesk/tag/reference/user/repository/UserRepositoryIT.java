package com.talkdesk.tag.reference.user.repository;

import com.talkdesk.tag.reference.common.ExtendedAccountBean;
import com.talkdesk.tag.reference.user.entity.User;
import com.talkdesk.tag.reference.user.model.UserPaginated;
import com.talkdesk.tag.reference.user.pagination.UserSortOption;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.talkdesk.tag.reference.TokenUtils.token;
import static com.td.athena.pagination.util.SortHelper.parseSort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class UserRepositoryIT {
    @Inject
    UserRepository userRepository;

    @Inject
    ExtendedAccountBean accountBean;

    @Inject
    Flyway flyway;

    @BeforeEach
    public void beforeEach() {
        // needed to set the account context.
        accountBean.setToken(token("5c938b75ba67ef0008bbfe2a", "jwt.json"));
        flyway.clean();
        flyway.migrate();
    }

    @AfterEach
    void afterEach() {
        flyway.clean();
    }

    @Test
    void persist() {
        final User user = User.builder()
                              .firstName("Sasuke")
                              .lastName("Uchiha")
                              .age(17)
                              .build();

        userRepository.create(user);

        assertNotNull(user.getId());
    }

    @Test
    @Transactional
    void merge() {
        final User user = userRepository.findByFirstName("Naruto");
        assertNotNull(user);

        user.setAge(19);

        final User updated = userRepository.findByFirstName("Naruto");
        assertEquals(19, updated.getAge());
    }

    @Test
    @Transactional
    void delete() {
        final User user = userRepository.findByFirstName("Naruto-delete");
        assertNotNull(user);

        final Optional<User> deletedUser = userRepository.delete(user.getId());
        assertNotNull(deletedUser.get());
        assertNull(userRepository.findByFirstName("Naruto-delete"));
    }

    @Test
    void listAll() {
        final List<User> users = userRepository.listAll();
        assertFalse(users.isEmpty());
    }

    @Test
    void listPaginated() {
        final List<UserSortOption> sortOptions = parseSort(new ArrayList<>(),
                UserSortOption.class,
                UserSortOption.FIRST_NAME_DESC);
        final UserPaginated userPaginated = userRepository.listPaginated(1, 2, sortOptions);
        assertEquals(2, userPaginated.getEmbeddedItems().getItems().size());
        assertEquals(3, userPaginated.getTotalPages());
        assertEquals(2, userPaginated.getPerPage());
        assertEquals(6, userPaginated.getTotal());
        assertEquals(2, userPaginated.getCount());
        assertFalse(userPaginated.getEmbeddedItems().getItems().isEmpty());
    }

    @Test
    void findByFirstName() {
        final User user = userRepository.findByFirstName("Naruto");
        assertNotNull(user);
    }
}
