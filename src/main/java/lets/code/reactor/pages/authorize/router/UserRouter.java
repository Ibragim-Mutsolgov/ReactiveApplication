package lets.code.reactor.pages.authorize.router;

import lets.code.reactor.pages.authorize.handle.UserHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter { // Класс для указания "роутеров" - путей, по которым обрабатываются запросы

    @Bean
    public RouterFunction<ServerResponse> allRequests(UserHandle userHandle){
        return RouterFunctions.route()
                .GET("/auth", userHandle::findAll) // Поиск всех пользователей
                .GET("/auth/{id}", userHandle::findById) // Поиск одного пользователя по идентификатору. Пример: /auth/53
                .POST("/auth", userHandle::save) //Сохранение пользователя
                .PUT("/auth/{id}", userHandle::update) //Обновление данных пользователя по идентификатору
                .DELETE("/auth/{id}", userHandle::delete) // Удаление пользователя по идентификатору
                .POST("/token", userHandle::getToken) // Выдача токена
                .build();
    }
}