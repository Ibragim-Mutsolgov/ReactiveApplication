package lets.code.reactor.security;

import lets.code.reactor.pages.authorize.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class WebFluxSecurityDBConfiguration {

    private UserRepository userRepository; // Репозиторий сущности User
    private AuthenticationManager authenticationManager; // Особых знаний нет. Пока: необходимо для создания токена
    private SecurityContextRepository securityContextRepository; // Необходимо для создания токена

    @Bean
    ReactiveUserDetailsService userDetailsService(){
        return (name) -> userRepository.findByUsername(name); // Поиск пользователя по имени
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){
        return http.authorizeExchange(
                        authorizeExchangeSpec -> authorizeExchangeSpec
                                .pathMatchers("/token").permitAll() // Доступный адрес без авторизации
                                .anyExchange().authenticated() // Остальное только через авторизацию
                )
                .exceptionHandling()
                .authenticationEntryPoint((response, error) -> Mono.fromRunnable(() -> {
                    response.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // Не авторизовался
                })).accessDeniedHandler((response, error) -> Mono.fromRunnable(() -> {
                    response.getResponse().setStatusCode(HttpStatus.FORBIDDEN); // Запрещено
                })).and()
                .httpBasic().disable() // Отключаем
                .formLogin().disable() // Отключаем
                .csrf().disable() // Отключаем
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .requestCache().requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .build();
    }
}
