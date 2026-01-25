package com.reindecar.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update user notification settings")
public record UpdateUserSettingsRequest(
        @Schema(description = "Enable email notifications")
        boolean emailNotifications,

        @Schema(description = "Enable SMS notifications")
        boolean smsNotifications,

        @Schema(description = "Enable push notifications")
        boolean pushNotifications
) {
}
