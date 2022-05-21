package lets.code.reactor.pages.authorize.handle;

import lets.code.reactor.pages.authorize.domain.User;
import lets.code.reactor.pages.authorize.repository.UserRepository;
import lets.code.reactor.pages.authorize.service.UserService;
import lets.code.reactor.security.AuthToken;
import lets.code.reactor.security.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class UserHandle { // Handle - это методы, которые будут вызываться при обращении к тому или иному запросу
    private UserService userService; // Реализованные интерфейсы для сущности User
    private UserRepository userRepository; // Репозиторий для сущности User
    private JWTUtil jwtUtil; // Класс для создания токена

    public Mono<ServerResponse> findAll(ServerRequest request){ // Метод для поиска всех пользователей
        return ServerResponse.ok()
                .body(userService.findAll(), User.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request){ // Метод для поиска одного пользователя по идентификатору
        Long id = Long.valueOf(request.pathVariable("id"));
        return userService.findById(id)
                .flatMap(user -> ServerResponse.ok()
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request){ // Метод для сохранения пользователя. Данные пользователя передаются в теле запроса
        Mono<User> userMono = request.bodyToMono(User.class)
                .flatMap(user -> userService.save(new User(user.getUsername(),
                        PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(user.getPassword()),
                        user.getRoles(),
                        user.isActive())));
        return ServerResponse.status(HttpStatus.CREATED)
                .body(userMono, User.class);
    }

    public Mono<ServerResponse> update(ServerRequest request){ //Метод для обновления данных пользователя. Передается идентификатор пользователя в адрес, а данные в теле запроса
        Long id = Long.valueOf(request.pathVariable("id"));
        return userService.findById(id)
                .flatMap(user -> {
                    Mono<User> userMono = request.bodyToMono(User.class)
                            .flatMap(userResult -> userService.save(new User(
                                    userResult.getUsername(),
                                    PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(userResult.getPassword()),
                                    userResult.getRoles(),
                                    userResult.isActive()
                            )));
                    return ServerResponse.ok()
                            .body(userMono, User.class);
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request){ // Метод для удаления пользователя
        Long id = Long.valueOf(request.pathVariable("id"));
        return userService.findById(id)
                .flatMap(user -> userService.delete(user.getId())
                        .then(ServerResponse.ok().bodyValue(user)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getToken(ServerRequest request){ // Метод для выдачи токена
        Mono<User> userMono = request.bodyToMono(User.class);

        return userMono.flatMap(user -> userRepository.findByUsername(user.getUsername())
                .flatMap(userDetails -> {
                    if(PasswordEncoderFactories.createDelegatingPasswordEncoder().matches(user.getPassword(), userDetails.getPassword())){
                        return ServerResponse.ok()
                                .bodyValue(new AuthToken(jwtUtil.generateToken(user)));
                    }else{
                        return ServerResponse.badRequest().build();
                    }
                }).switchIfEmpty(ServerResponse.badRequest().build())
        );
    }
}