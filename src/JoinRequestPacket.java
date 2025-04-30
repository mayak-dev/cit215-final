public class JoinRequestPacket extends ChatPacket
{
    public JoinRequestPacket(String clientId)
    {
        super(ID.JOIN_REQUEST, clientId);
    }
}
