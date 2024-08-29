package ai.agreement.AiProject.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookieName {

    USER_AUTH_KEY("_service_key");

    private final String cookieName;
}
