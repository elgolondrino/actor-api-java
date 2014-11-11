package im.actor.proto.api._internal.parsers;

import static im.actor.proto.api.ActorApiScheme.*;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class UpdatesParser extends SchemeParser {
    protected void init() {

        // Sequence

        register(NEW_MESSAGE, UpdateMessage.PARSER, UpdateMessage.class);
        register(NEW_GROUP_MESSAGE, UpdateGroupMessage.PARSER, UpdateGroupMessage.class);

        register(GROUP_CREATED, UpdateGroupCreated.PARSER, UpdateGroupCreated.class);
        register(GROUP_INVITE, UpdateGroupInvite.PARSER, UpdateGroupInvite.class);
        register(GROUP_ADDED, UpdateGroupUserAdded.PARSER, UpdateGroupUserAdded.class);
        register(GROUP_LEAVE, UpdateGroupUserLeave.PARSER, UpdateGroupUserLeave.class);
        register(GROUP_KICKED, UpdateGroupUserKick.PARSER, UpdateGroupUserKick.class);

        register(GROUP_CHANGED_TITLE, UpdateGroupTitleChanged.PARSER, UpdateGroupTitleChanged.class);
        register(GROUP_CHANGED_AVATAR, UpdateGroupAvatarChanged.PARSER, UpdateGroupAvatarChanged.class);

        register(UPDATE_NEW_DEVICE, UpdateNewDevice.PARSER, UpdateNewDevice.class);
        register(UPDATE_NEW_YOUR_DEVICE, UpdateNewFullDevice.PARSER, UpdateNewFullDevice.class);
        register(UPDATE_DEVICE_REMOVED, UpdateRemoveDevice.PARSER, UpdateRemoveDevice.class);

        register(MESSAGE_SENT, UpdateMessageSent.PARSER, UpdateMessageSent.class);
        register(MESSAGE_GROUP_SENT, UpdateMessageGroupSent.PARSER, UpdateMessageGroupSent.class);

        register(CONTACT_REGISTERED, UpdateContactRegistered.PARSER, UpdateContactRegistered.class);

        register(AVATAR_CHANGED, UpdateAvatarChanged.PARSER, UpdateAvatarChanged.class);
        register(NAME_CHANGED, UpdateUserNameChanged.PARSER, UpdateUserNameChanged.class);
        register(NAME_LOCAL_CHANGED, UpdateUserLocalNameChanged.PARSER, UpdateUserLocalNameChanged.class);

        register(MESSAGE_RECEIVED, UpdateMessageReceived.PARSER, UpdateMessageReceived.class);
        register(MESSAGE_READ, UpdateMessageRead.PARSER, UpdateMessageRead.class);

        register(CONTACT_ADDED, UpdateContactsAdded.PARSER, UpdateContactsAdded.class);
        register(CONTACT_REMOVED, UpdateContactsRemoved.PARSER, UpdateContactsRemoved.class);

        // Weak

        register(TYPING_UPDATE, UpdateTyping.PARSER, UpdateTyping.class);
        register(TYPING_GROUP_UPDATE, UpdateTypingGroup.PARSER, UpdateTypingGroup.class);

        register(USER_ONLINE, UpdateUserOnline.PARSER, UpdateUserOnline.class);
        register(USER_OFFLINE, UpdateUserOffline.PARSER, UpdateUserOffline.class);
        register(USER_LAST_SEEN, UpdateUserLastSeen.PARSER, UpdateUserLastSeen.class);

        register(GROUP_ONLINE, UpdateGroupOnline.PARSER, UpdateGroupOnline.class);
    }

    // Sequence

    private static final int NEW_MESSAGE = 0x01;
    private static final int NEW_GROUP_MESSAGE = 0x14;

    private static final int GROUP_CREATED = 0x24;
    private static final int GROUP_INVITE = 0x19;
    private static final int GROUP_ADDED = 0x15;
    private static final int GROUP_LEAVE = 0x17;
    private static final int GROUP_KICKED = 0x18;

    private static final int GROUP_CHANGED_TITLE = 0x26;
    private static final int GROUP_CHANGED_AVATAR = 0x27;

    private static final int UPDATE_NEW_DEVICE = 0x02;
    private static final int UPDATE_NEW_YOUR_DEVICE = 0x03;
    private static final int UPDATE_DEVICE_REMOVED = 0x25;

    private static final int MESSAGE_SENT = 0x04;
    private static final int MESSAGE_GROUP_SENT = 0x23;

    private static final int CONTACT_REGISTERED = 0x05;

    private static final int AVATAR_CHANGED = 0x10;
    private static final int NAME_CHANGED = 0x20;
    private static final int NAME_LOCAL_CHANGED = 0x33;

    private static final int MESSAGE_RECEIVED = 0x12;
    private static final int MESSAGE_READ = 0x13;

    private static final int CONTACT_ADDED = 0x28;
    private static final int CONTACT_REMOVED = 0x29;


    // Weak

    private static final int TYPING_UPDATE = 0x06;
    private static final int TYPING_GROUP_UPDATE = 0x22;

    private static final int USER_ONLINE = 0x07;
    private static final int USER_OFFLINE = 0x08;
    private static final int USER_LAST_SEEN = 0x09;

    private static final int GROUP_ONLINE = 0x21;
}
