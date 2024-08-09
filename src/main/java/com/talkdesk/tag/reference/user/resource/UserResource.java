package com.talkdesk.tag.reference.user.resource;

import com.talkdesk.tag.reference.user.mapper.UserMapper;
import com.talkdesk.tag.reference.user.model.UserCreate;
import com.talkdesk.tag.reference.user.model.UserPaginated;
import com.talkdesk.tag.reference.user.model.UserUpdate;
import com.talkdesk.tag.reference.user.pagination.UserSortOption;
import com.talkdesk.tag.reference.user.repository.UserRepository;
import com.td.athena.pagination.util.LinksItemHelper;
import com.td.athena.pagination.validation.PaginatedResource;
import com.td.athena.security.account.AccountRequired;
import com.td.athena.security.policy.RequiresPolicy;
import com.td.athena.security.policy.Secured;
import com.td.athena.utils.service.LocalUriFormatter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static com.td.athena.pagination.util.SortHelper.parseSort;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Implementation of a REST API.
 *
 * This must be a CDI bean, so we can inject dependent services, like the Repository. The bean must be annotated with
 * {@link ApplicationScoped} since there is no state involved and we can safely use a single instance of the bean.
 *
 * Inject any required services required to implement the desired business logic. If you require a particular service
 * or piece of code that you are not able to inject, because is not a CDI bean, create a CDI producer and then inject
 * it.
 *
 * The {@link AccountRequired} annotation demands a valid JWT with an aid (account id) present in order to serve the
 * request. If the requirement is not met, an HTTP 401 error will be thrown.
 */
@ApplicationScoped
@AccountRequired
@Slf4j
@Secured
public class UserResource implements UserApi {
    @Context
    UriInfo uriInfo;
    @Inject
    LocalUriFormatter localUriFormatter;
    @Inject
    UserRepository userRepository;
    @Inject
    UserMapper userMapper;
    @Inject
    LinksItemHelper linksItemHelper;

    /**
     * The REST get method should search the object by id in the Repository, and map it to a read object structure.
     *
     * The Response must be a 200 with the object attached or a 404 if the entity was not found.
     *
     * @param id the object to get.
     * @return a Response with the execution result.
     */
    @Override
    @RequiresPolicy("reference.read")
    public Response get(final String id) {
        log.info("Get user");
        return userRepository.findByIdOptional(id)
                             .map(userMapper::toUserRead)
                             .map(Response::ok)
                             .orElse(Response.status(NOT_FOUND))
                             .build();
    }

    /**
     * The REST create method must validate the incoming object using Bean Validation. Just annotate the model with
     * the proper validations, plus the @Valid annotation in the method parameter itself.
     *
     * Right now, the method parameter annotations are required to be present both in the implementation and the
     * interface. This seems to be a Quarkus bug.
     *
     * The Respose must be a 201 with the Location header pointing to the newly created resource. If the validation
     * fails, a {@link javax.validation.ValidationException} is thrown that should be handled by the REST Exception
     * mapper.
     *
     * @param userCreate the object to create.
     * @return a Response with the execution result.
     */
    @Override
    @RequiresPolicy("reference.write")
    public Response create(@Valid @NotNull final UserCreate userCreate) {
        return userRepository.create(userMapper.toUser(userCreate))
                             .map(userMapper::toUserRead)
                             .map(user -> localUriFormatter.createdWithHeaderLocation(uriInfo.getRequestUriBuilder(), user.getId(), user))
                             .orElse(Response.status(NOT_FOUND).build());
    }

    /**
     * The REST update method must validate the incoming object using Bean Validation. Just annotate the model with
     * the proper validations, plus the @Valid annotation in the method parameter itself.
     *
     * Right now, the method parameter annotations are required to be present both in the implementation and the
     * interface. This seems to be a Quarkus bug.
     *
     * The Response must be a 200 with the object attached or a 404 if the entity was not found. If the validation
     * fails, a {@link javax.validation.ValidationException} is thrown that should be handled by the REST Exception
     * mapper.
     *
     * @param userUpdate the object to update.
     * @return a Response with the execution result.
     */
    @Override
    @RequiresPolicy("reference.write")
    public Response update(final String id, @Valid @NotNull final UserUpdate userUpdate) {
        return userRepository.update(userMapper.toUser(userUpdate).setId(id))
                             .map(userMapper::toUserRead)
                             .map(Response::ok)
                             .orElse(Response.status(NOT_FOUND))
                             .build();
    }

    /**
     * The REST delete method should delete the object by id in the Repository.
     *
     * The Response must be a 204 or a 404 if the entity was not found.
     *
     * @param id the object to delete.
     * @return a Response with the execution result.
     */
    @Override
    @RequiresPolicy("reference.write")
    public Response delete(final String id) {
        return userRepository.delete(id)
                             .map(user -> Response.noContent())
                             .orElse(Response.status(NOT_FOUND))
                             .build();
    }

    /**
     * The REST listPaginated method should list the objects paginated
     *
     * The Response must be 200 with the list of objects for a page
     *
     * @param page the page to list
     * @param perPage the number of objects per page
     * @param orderBy the field to order
     * @return a Response with the execution result.
     */
    @Override
    @PaginatedResource
    public Response listPaginated(final Integer page,
                                  final Integer perPage,
                                  final List<String> orderBy) {
        final List<UserSortOption> sortOptions = parseSort(orderBy,
                UserSortOption.class,
                UserSortOption.FIRST_NAME_DESC);

        final UserPaginated userPaginated = userRepository.listPaginated(page, perPage, sortOptions);
        userPaginated.setLinks(linksItemHelper.getLinksItem(
                uriInfo,
                userPaginated.getPage(),
                userPaginated.getPerPage(),
                userPaginated.getTotalPages()));

        return Response.ok(userPaginated).build();
    }
}
