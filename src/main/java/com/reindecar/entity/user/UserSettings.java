package com.reindecar.entity.user;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSettings extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "email_notifications", nullable = false)
    private boolean emailNotifications = true;

    @Column(name = "sms_notifications", nullable = false)
    private boolean smsNotifications = true;

    @Column(name = "push_notifications", nullable = false)
    private boolean pushNotifications = true;

    public static UserSettings createDefault(Long userId) {
        UserSettings settings = new UserSettings();
        settings.userId = userId;
        settings.emailNotifications = true;
        settings.smsNotifications = true;
        settings.pushNotifications = true;
        return settings;
    }

    public void update(boolean emailNotifications, boolean smsNotifications, boolean pushNotifications) {
        this.emailNotifications = emailNotifications;
        this.smsNotifications = smsNotifications;
        this.pushNotifications = pushNotifications;
    }
}
