import cn.hutool.core.util.IdUtil;
import org.example.rpc.constant.RpcConstant;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.protocol.ProtocolConstant;
import org.example.rpc.protocol.ProtocolMessage;
import org.example.rpc.protocol.ProtocolMessageDecoder;
import org.example.rpc.protocol.ProtocolMessageEncoder;
import org.example.rpc.protocol.enums.MessageSerializer;
import org.example.rpc.protocol.enums.MessageStatus;
import org.example.rpc.protocol.enums.MessageType;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ProtocolMessageTest {
    @Test
    public void testEncodeAndDecode() throws IOException {
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) MessageSerializer.JDK.getKey());
        header.setType((byte) MessageType.REQUEST.getKey());
        header.setStatus((byte) MessageStatus.OK.getVal());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("service");
        rpcRequest.setMethodName("method");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[] {String.class});
        rpcRequest.setArgs(new Object[] {"aaa", "bbb"});
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);

        Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
        ProtocolMessage<?> message = ProtocolMessageDecoder.decode(encode);
        Assert.assertNotNull(message);
    }
}