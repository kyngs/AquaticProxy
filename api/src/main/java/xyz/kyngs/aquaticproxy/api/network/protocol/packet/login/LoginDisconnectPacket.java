package xyz.kyngs.aquaticproxy.api.network.protocol.packet.login;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.shared.DisconnectPacket;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.*;

public class LoginDisconnectPacket extends DisconnectPacket {
    public LoginDisconnectPacket(Component reason) {
        super(reason);
    }

    public  LoginDisconnectPacket() {
    }
}
