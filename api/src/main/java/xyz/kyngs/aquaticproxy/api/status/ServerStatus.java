package xyz.kyngs.aquaticproxy.api.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;

import java.util.UUID;

public record ServerStatus(
        int protocol,
        String version,
        int onlinePlayers,
        int maxPlayers,
        SamplePlayer[] samplePlayers,
        Component description,
        String favicon
) {

    private static final Gson GSON = new GsonBuilder().create();

    public String toJSON(ProtocolVersion version) {
        var statusObject = new JsonObject();

        var versionObject = new JsonObject();
        versionObject.addProperty("name", this.version);
        versionObject.addProperty("protocol", this.protocol);
        statusObject.add("version", versionObject);

        var playersObject = new JsonObject();
        playersObject.addProperty("max", this.maxPlayers);
        playersObject.addProperty("online", this.onlinePlayers);

        var sampleArray = new JsonArray();
        for (SamplePlayer player : this.samplePlayers) {
            var playerObject = new JsonObject();
            playerObject.addProperty("name", player.name());
            playerObject.addProperty("id", player.uuid().toString());
            sampleArray.add(playerObject);
        }
        playersObject.add("sample", sampleArray);
        statusObject.add("players", playersObject);

        statusObject.add("description", ProtocolUtil.getJsonChatSerializer(version).serializeToTree(this.description));

        if (this.favicon != null) {
            statusObject.addProperty("favicon", this.favicon);
        }

        return GSON.toJson(statusObject);
    }

    public record SamplePlayer(String name, UUID uuid) {
    }
}
