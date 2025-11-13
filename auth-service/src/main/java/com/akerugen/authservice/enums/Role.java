package com.akerugen.authservice.enums;

/**
 * Enum для ролей пользователей в системе
 * - USER: обычный пользователь (по дефолту)
 * - ADMIN: администратор (может редактировать контент, управлять пользователями)
 * - SUPER_USER: суперпользователь (может назначать администраторов, только один в системе)
 */
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    SUPER_USER("ROLE_SUPER_USER");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    /**
     * Получить Role из String значения
     * @param value значение (USER, ADMIN, SUPER_USER)
     * @return Role enum
     */
    public static Role fromString(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }
}