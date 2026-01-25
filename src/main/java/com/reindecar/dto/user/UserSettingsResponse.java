package com.reindecar.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User notification settings")
public record UserSettingsResponse(
        boolean emailNotifications,
        boolean smsNotifications,
        boolean pushNotifications
) {
}
