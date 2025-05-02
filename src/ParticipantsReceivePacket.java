import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParticipantsReceivePacket extends ChatPacket
{
    private final String[] participants;

    public ParticipantsReceivePacket(String[] _participants)
    {
        super(ID.PARTICIPANTS_RECEIVE);
        participants = _participants;
    }

    public String[] getParticipants()
    {
        return participants;
    }

    @Override
    public void write(DataOutputStream out) throws IOException
    {
        super.write(out);
        out.writeByte(participants.length);
        for (String participant : participants)
            out.writeUTF(participant);
    }

    public static ParticipantsReceivePacket read(DataInputStream in) throws IOException
    {
        byte count = in.readByte();
        String[] participants = new String[count];
        for (int i = 0; i < count; ++i)
            participants[i] = in.readUTF();
        return new ParticipantsReceivePacket(participants);
    }
}
