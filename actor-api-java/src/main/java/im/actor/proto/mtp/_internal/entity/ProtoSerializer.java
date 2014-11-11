package im.actor.proto.mtp._internal.entity;

import im.actor.proto.mtp._internal.entity.message.*;
import im.actor.proto.mtp._internal.entity.message.rpc.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static im.actor.proto.util.StreamingUtils.*;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class ProtoSerializer {
    public static ProtoStruct readMessagePayload(byte[] bs) throws IOException {
        return readMessagePayload(new ByteArrayInputStream(bs));
    }

    public static ProtoStruct readMessagePayload(InputStream bs) throws IOException {
        final byte header = readByte(bs);

        switch (header) {
            case RequestAuthId.HEADER:
                return new RequestAuthId(bs);
            case ResponseAuthId.HEADER:
                return new ResponseAuthId(bs);
            case Ping.HEADER:
                return new Ping(bs);
            case Pong.HEADER:
                return new Pong(bs);
            case Drop.HEADER:
                return new Drop(bs);
            case Container.HEADER:
                return new Container(bs);
            case RpcRequestBox.HEADER:
                return new RpcRequestBox(bs);
            case RpcResponseBox.HEADER:
                return new RpcResponseBox(bs);
            case MessageAck.HEADER:
                return new MessageAck(bs);
            case NewSession.HEADER:
                return new NewSession(bs);
            case UpdateBox.HEADER:
                return new UpdateBox(bs);
            case UnsentMessage.HEADER:
                return new UnsentMessage(bs);
            case UnsentResponse.HEADER:
                return new UnsentResponse(bs);
            case RequestResend.HEADER:
                return new UnsentResponse(bs);
        }

        throw new IOException("Unable to read proto object with header #" + header);
    }

    public static ProtoStruct readRpcResponsePayload(byte[] bs) throws IOException {
        return readRpcResponsePayload(new ByteArrayInputStream(bs));
    }

    public static ProtoStruct readRpcResponsePayload(InputStream bs) throws IOException {
        final byte header = readByte(bs);
        switch (header) {
            case RpcOk.HEADER:
                return new RpcOk(bs);
            case RpcError.HEADER:
                return new RpcError(bs);
            case RpcFloodWait.HEADER:
                return new RpcFloodWait(bs);
            case RpcInternalError.HEADER:
                return new RpcInternalError(bs);
        }
        throw new IOException("Unable to read proto object");
    }

    public static ProtoStruct readRpcRequestPayload(InputStream bs) throws IOException {
        final byte header = readByte(bs);
        switch (header) {
            case RpcRequest.HEADER:
                return new RpcRequest(bs);
        }
        throw new IOException("Unable to read proto object with header #" + header);
    }

    public static Update readUpdate(byte[] bs) throws IOException {
        return readUpdate(new ByteArrayInputStream(bs));
    }

    public static Update readUpdate(InputStream bs) throws IOException {
        return new Update(bs);
    }
}
