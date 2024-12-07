package app.revanced.patches.youtube.misc.fix.playback

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val buildInitPlaybackRequestFingerprint = fingerprint {
    returns("Lorg/chromium/net/UrlRequest\$Builder;")
    opcodes(
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IGET_OBJECT, // Moves the request URI string to a register to build the request with.
    )
    strings(
        "Content-Type",
        "Range",
    )
}

internal val buildPlayerRequestURIFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    opcodes(
        Opcode.INVOKE_VIRTUAL, // Register holds player request URI.
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IPUT_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.MONITOR_EXIT,
        Opcode.RETURN_OBJECT,
    )
    strings(
        "youtubei/v1",
        "key",
        "asig",
    )
}

internal val buildRequestFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("Lorg/chromium/net/UrlRequest;")
    custom { methodDef, _ ->
        // Different targets have slightly different parameters

        // Earlier targets have parameters:
        // L
        // Ljava/util/Map;
        // [B
        // L
        // L
        // L
        // Lorg/chromium/net/UrlRequest$Callback;

        // Later targets have parameters:
        // L
        // Ljava/util/Map;
        // [B
        // L
        // L
        // L
        // Lorg/chromium/net/UrlRequest\$Callback;
        // L

        val parameterTypes = methodDef.parameterTypes
        (parameterTypes.size == 7 || parameterTypes.size == 8) &&
            parameterTypes[1] == "Ljava/util/Map;" // URL headers.
    }
}

internal val protobufClassParseByteBufferFingerprint = fingerprint {
    accessFlags(AccessFlags.PROTECTED, AccessFlags.STATIC)
    returns("L")
    parameters("L", "Ljava/nio/ByteBuffer;")
    opcodes(
        Opcode.SGET_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.RETURN_OBJECT,
    )
    custom { method, _ -> method.name == "parseFrom" }
}

internal val createStreamingDataFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
    returns("V")
    parameters("L")
    opcodes(
        Opcode.IPUT_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.IF_NEZ,
        Opcode.SGET_OBJECT,
        Opcode.IPUT_OBJECT,
    )
    custom { method, classDef ->
        classDef.fields.any { field ->
            field.name == "a" && field.type.endsWith("/StreamingDataOuterClass\$StreamingData;")
        }
    }
}

internal val buildMediaDataSourceFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
    returns("V")
    parameters(
        "Landroid/net/Uri;",
        "J",
        "I",
        "[B",
        "Ljava/util/Map;",
        "J",
        "J",
        "Ljava/lang/String;",
        "I",
        "Ljava/lang/Object;",
    )
}
