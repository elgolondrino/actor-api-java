package im.actor.api.scheme.parser;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.updates.*;

public class UpdatesParser extends BaseParser<Update> {
    @Override
    public Update read(int type, byte[] payload) throws IOException {
        switch(type) {
            case 16: return UpdateUserAvatarChanged.fromBytes(payload);
            case 32: return UpdateUserNameChanged.fromBytes(payload);
            case 51: return UpdateUserLocalNameChanged.fromBytes(payload);
            case 5: return UpdateContactRegistered.fromBytes(payload);
            case 40: return UpdateContactsAdded.fromBytes(payload);
            case 41: return UpdateContactsRemoved.fromBytes(payload);
            case 1: return UpdateEncryptedMessage.fromBytes(payload);
            case 55: return UpdateMessage.fromBytes(payload);
            case 4: return UpdateMessageSent.fromBytes(payload);
            case 18: return UpdateEncryptedReceived.fromBytes(payload);
            case 52: return UpdateEncryptedRead.fromBytes(payload);
            case 53: return UpdateEncryptedReadByMe.fromBytes(payload);
            case 54: return UpdateMessageReceived.fromBytes(payload);
            case 19: return UpdateMessageRead.fromBytes(payload);
            case 50: return UpdateMessageReadByMe.fromBytes(payload);
            case 46: return UpdateMessageDelete.fromBytes(payload);
            case 47: return UpdateChatClear.fromBytes(payload);
            case 48: return UpdateChatDelete.fromBytes(payload);
            case 36: return UpdateGroupInvite.fromBytes(payload);
            case 21: return UpdateGroupUserAdded.fromBytes(payload);
            case 23: return UpdateGroupUserLeave.fromBytes(payload);
            case 24: return UpdateGroupUserKick.fromBytes(payload);
            case 44: return UpdateGroupMembersUpdate.fromBytes(payload);
            case 38: return UpdateGroupTitleChanged.fromBytes(payload);
            case 39: return UpdateGroupAvatarChanged.fromBytes(payload);
            case 2: return UpdateNewDevice.fromBytes(payload);
            case 37: return UpdateRemovedDevice.fromBytes(payload);
            case 6: return UpdateTyping.fromBytes(payload);
            case 7: return UpdateUserOnline.fromBytes(payload);
            case 8: return UpdateUserOffline.fromBytes(payload);
            case 9: return UpdateUserLastSeen.fromBytes(payload);
            case 33: return UpdateGroupOnline.fromBytes(payload);
            case 42: return UpdateConfig.fromBytes(payload);
        }
        throw new IOException();
    }
}
