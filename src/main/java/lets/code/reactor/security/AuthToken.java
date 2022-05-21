package lets.code.reactor.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthToken { // Класс создан, чтобы выдача токена была оформлена грамотно
    private String token;
}
