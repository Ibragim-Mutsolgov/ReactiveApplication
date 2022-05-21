package lets.code.reactor.pages.authorize.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.annotation.Documented;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@Table("users")
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails { //Сущность базы данных. Класс должен реализовать интерфейс UserDetails, т.к. используется для авторизации
    @Id
    private Long id;
    private String username; //Логин
    //@JsonIgnore - это очень полезная вещь, если мы хотим скрыть пароль при получении json-объекта, но если его использовать, то Spring Security не видит пароль
    private String password; //Пароль
    private String roles; //Роль
    private boolean active = true; //Активность, не уверен нужно ли

    public User(String username, String password, String roles, boolean active) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.active = active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //Все методы идущие далее принадлежат интерфейсу UserDetails
        return Collections.singleton(new GrantedAuthority() { // Создаем новый объект GrantedAuthority
            @Override
            public String getAuthority() {
                return roles;
            } // Возвращаем роль
        });
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
