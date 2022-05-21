package lets.code.reactor.pages.authorize.repository;

import lets.code.reactor.pages.authorize.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<UserDetails> findByUsername(String username); // Основной метод поиска пользователя по логину. В основном используется в авторизации
}
