package lets.code.reactor.pages.authorize.service;

import lets.code.reactor.pages.authorize.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService { // Набор методов, для работы с базой данных
    Mono<User> findById(Long id);
    Flux<User> findAll();
    Mono<User> save(User user);
    Mono<User> delete(Long id);
}
