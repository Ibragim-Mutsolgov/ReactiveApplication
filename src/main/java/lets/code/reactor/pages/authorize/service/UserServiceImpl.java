package lets.code.reactor.pages.authorize.service;

import lets.code.reactor.pages.authorize.domain.User;
import lets.code.reactor.pages.authorize.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<User> delete(Long id) {
        return userRepository.findById(id)
                .flatMap(user -> userRepository.deleteById(id)
                .thenReturn(user));
    }
}
