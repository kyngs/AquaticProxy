package xyz.kyngs.aquaticproxy.api.player;

import net.kyori.adventure.key.Key;
import xyz.kyngs.aquaticproxy.api.module.Module;
import xyz.kyngs.aquaticproxy.api.module.ModuleKey;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;

public interface PlayerModule extends Module {

    ModuleKey<PlayerModule> KEY = new ModuleKey<>(Key.key("aquaticproxy", "player"), PlayerModule.class);

    Player registerPlayer(GameProfile profile, ClientConnection connection);
}
