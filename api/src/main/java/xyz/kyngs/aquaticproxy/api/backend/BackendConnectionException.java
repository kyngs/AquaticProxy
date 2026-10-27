package xyz.kyngs.aquaticproxy.api.backend;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BackendConnectionException extends RuntimeException {
    @Nullable
    private final Component reason;

    public BackendConnectionException(Throwable cause, @Nullable Component reason) {
        super(cause);
        this.reason = reason;
    }

    public BackendConnectionException(Throwable cause) {
        super(cause);
        this.reason = null;
    }

    public BackendConnectionException() {
        this.reason = null;
    }

    public BackendConnectionException(@Nullable Component reason) {
        this.reason = reason;
    }

    public @Nullable Component getReason() {
        return reason;
    }

    public @NotNull Component getOrGenerateReason() {
        if (reason != null) {
            return reason;
        }
        if (getCause() != null) {
            return Component.text("Backend connection failed: " + getCause().getMessage());
        }
        return Component.text("Backend connection failed");
    }
}
