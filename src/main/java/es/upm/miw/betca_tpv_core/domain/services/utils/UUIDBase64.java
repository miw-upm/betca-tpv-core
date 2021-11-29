package es.upm.miw.betca_tpv_core.domain.services.utils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public enum UUIDBase64 {

    BASIC(Base64.getEncoder(), Base64.getDecoder()),
    MIME(Base64.getMimeEncoder(), Base64.getMimeDecoder()),
    URL(Base64.getUrlEncoder(), Base64.getUrlDecoder());

    private final Base64.Encoder encoder;
    private final Base64.Decoder decoder;

    UUIDBase64(Base64.Encoder encoder, Base64.Decoder decoder) {
        this.encoder = encoder.withoutPadding();
        this.decoder = decoder;
    }

    public String encode() {
        UUID value = UUID.randomUUID();
        ByteBuffer buffer = ByteBuffer.allocate(16).putLong(value.getMostSignificantBits()).putLong(value.getLeastSignificantBits());
        return encoder.encodeToString(buffer.array());
    }

    public UUID decode(String value) {
        ByteBuffer buffer = ByteBuffer.wrap(decoder.decode(value));
        return new UUID(buffer.getLong(), buffer.getLong());
    }

}
