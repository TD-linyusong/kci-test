package com.talkdesk.tag.reference.user.repository;

import com.talkdesk.tag.reference.user.entity.User;
import com.talkdesk.tag.reference.user.mapper.UserMapper;
import com.talkdesk.tag.reference.user.model.UserPaginated;
import com.talkdesk.tag.reference.user.model.UserRead;
import com.talkdesk.tag.reference.user.pagination.UserSortOption;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import lombok.NonNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.transaction.Transactional.TxType.REQUIRED;

/**
 * A simple Repository for the User class to interact with the Database. This uses a Quarkus extensions called Panache
 * that provides out of the box a set of commons methods to interact with the Database table, like CRUD and finder
 * methods.
 * <p>
 * The Repository must implement PanacheRepositoryBase and use the Entity type plus the primary key type in the
 * generics arguments. The Repository must be a CDI Bean, so it needs the @ApplicationScoped annotation, plus
 * transactions with the @Transactional annotation.
 * <p>
 * Repositories should be "dumb", meaning that they shouldn't aggregate complex business logic and that each method
 * should only perform a single operation.
 * <p>
 * Please check {@link PanacheRepositoryBase} or <a href="https://quarkus.io/guides/hibernate-orm-panache-guide"></a> for more information.
 * <p>
 * The test UserRepositoryIT shows the Repository in action.
 */
@ApplicationScoped
@Transactional(REQUIRED)
public class UserRepository implements PanacheRepositoryBase<User, String> {
    @Inject
    UserMapper userMapper;

    /**
     * For convenience, you can add a method to create the entity and return the same instance.
     *
     * It is useful to have a method that return the entity straight away, so it can be used in fluent API calls, for
     * instance with {@link Optional#map(Function)}.
     *
     * @param user the entity to persist.
     * @return the attached entity, with the id set.
     */
    public Optional<User> create(final User user) {
        persist(user);
        return Optional.of(user);
    }

    /**
     * This is used to find the entity and update it in the database.
     * Panache doesn't require calling merge or update explicitly but it updates attached entities directly
     *
     * It is useful to have a method that return the entity straight away, so it can be used in fluent API calls, for
     * instance with {@link Optional#map(Function)}.
     *
     * @param user the entity to persist.
     * @return the attached entity, with the id set.
     */
    public Optional<User> update(final User user) {
        return findByIdOptional(user.getId()).map(userToBeUpdated -> {
            userMapper.toUser(user, userToBeUpdated);
            return userToBeUpdated;
        });
    }

    /**
     *  For convenience, you can add a method to delete the entity and return the same instance.
     *
     * Since, we may not be able to find the entity, we just use the find with {@link Optional} and map the delete
     * and return the {@link Optional}, so it can be safely used in REST endpoints to return a 404 for instance.
     *
     * @param id the entity primary key.
     * @return the detached entity that was deleted.
     */
    public Optional<User> delete(final String id) {
        return findByIdOptional(id).map(user -> {
            delete(user);
            return user;
        });
    }

    /**
     * Additional methods can be added to the Repository class. Keep in mind that you can leverage existent methods
     * to implement specific behaviour.
     *
     * This finds an User by its firstName. It uses the generic method from Panache that takes a query string and a
     * parameter value to bind the value to search.
     *
     * @param firstName the firstName of the User to search.
     * @return a User with the firstName
     */
    public User findByFirstName(final String firstName) {
        return find("firstName", firstName).singleResultOptional().orElse(null);
    }

    /**
     * This finds an User by its firstName and lastName. It uses find from Panache with two parameters.
     *
     * @param firstName the firstName of the User to search.
     * @param lastName the lastName of the User to search.
     * @return a User
     */
    public List<User> findByFirstNameAndLastName(final String firstName, final String lastName) {
        return find("firstName = ?1 AND lastName = ?2", firstName, lastName).list();
    }

    /**
     * This finds the list of Users paginated
     *
     * @param page        the selected page
     * @param perPage     number of users per page
     * @param sortOptions the sorting configuration
     * @return a list of users paginated
     */
    public UserPaginated listPaginated(final Integer page, final Integer perPage, final List<UserSortOption> sortOptions) {
        final PanacheQuery<User> query = findAll(getSort(sortOptions)).page(page - 1, perPage);
        final List<UserRead> users = query.list()
                .stream()
                .map(userMapper::toUserRead)
                .collect(Collectors.toList());

        return UserPaginated.builder()
                .total((int) query.count())
                .embeddedItems(new UserPaginated.Users(users))
                .page(page)
                .perPage(perPage)
                .build();
    }

    private Sort getSort(@NotEmpty final List<UserSortOption> original) {
        final List<UserSortOption> sortOptions = new ArrayList<>(original);
        final UserSortOption firstElement = sortOptions.remove(0);
        final Sort sort = Sort.by(firstElement.getColumnName(), getSortingDirection(firstElement.getSortDirection()));
        sortOptions.forEach(requestSortOption ->
                sort.and(requestSortOption.getColumnName(), getSortingDirection(requestSortOption.getSortDirection())));
        return sort;
    }

    private Sort.Direction getSortingDirection(@NonNull final String direction) {
        return "desc".equals(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending;
    }
}
