package im.actor.proto.api.parsers;

import static im.actor.proto.api.ActorApiScheme.*;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class RpcParser extends SchemeParser {
    protected void init() {

        // Common

        register(RESPONSE_VOID, ResponseVoid.PARSER, ResponseVoid.class);
        register(RESPONSE_SEQ, ResponseSeq.PARSER, ResponseSeq.class);

        // Auth

        register(REQUEST_AUTH_CODE, RequestAuthCode.PARSER, RequestAuthCode.class);
        register(RESPONSE_AUTH_CODE, ResponseAuthCode.PARSER, ResponseAuthCode.class);

        register(REQUEST_SIGN_IN, RequestSignIn.PARSER, RequestSignIn.class);
        register(REQUEST_SIGN_UP, RequestSignUp.PARSER, RequestSignUp.class);
        register(RESPONSE_AUTH, ResponseAuth.PARSER, ResponseAuth.class);

        // Profile

        register(REQUEST_EDIT_NAME, RequestEditName.PARSER, RequestEditName.class);
        register(REQUEST_EDIT_AVATAR, RequestEditAvatar.PARSER, RequestEditAvatar.class);
        register(RESPONSE_AVATAR_CHANGED, ResponseAvatarChanged.PARSER, ResponseAvatarChanged.class);

        // Contacts

        register(REQUEST_IMPORT_CONTACTS, RequestImportContacts.PARSER, RequestImportContacts.class);
        register(RESPONSE_IMPORT_CONTACTS, ResponseImportedContacts.PARSER, ResponseImportedContacts.class);

        register(REQUEST_FIND_CONTACTS, RequestFindContacts.PARSER, RequestFindContacts.class);
        register(RESPONSE_FIND_CONTACTS, ResponseFindContacts.PARSER, ResponseFindContacts.class);


        register(REQUEST_GET_CONTACTS, RequestGetContacts.PARSER, RequestGetContacts.class);
        register(RESPONSE_GET_CONTACTS, ResponseGetContacts.PARSER, ResponseGetContacts.class);
        register(REQUEST_DELETE_CONTACT, RequestDeleteContact.PARSER, RequestDeleteContact.class);
        register(REQUEST_EDIT_CONTACT, RequestEditContactName.PARSER, RequestEditContactName.class);
        register(REQUEST_ADD_CONTACT, RequestAddContact.PARSER, RequestAddContact.class);

        // Users

        register(REQUEST_PUBLIC_KEYS, RequestPublicKeys.PARSER, RequestPublicKeys.class);
        register(RESPONSE_PUBLIC_KEYS, ResponsePublicKeys.PARSER, ResponsePublicKeys.class);

        // Updates

        register(SEQ_UPDATE, SeqUpdate.PARSER, SeqUpdate.class);
        register(SEQ_UPDATE_TOO_LONG, SeqUpdateTooLong.PARSER, SeqUpdateTooLong.class);
        register(WEAK_UPDATE, WeakUpdate.PARSER, WeakUpdate.class);
        register(FAT_SEQ_UPDATE, FatSeqUpdate.PARSER, FatSeqUpdate.class);

        register(REQUEST_GET_STATE, RequestGetState.PARSER, RequestGetState.class);
        register(REQUEST_GET_DIFFERENCE, RequestGetDifference.PARSER, RequestGetDifference.class);
        register(DIFFERENCE, Difference.PARSER, Difference.class);

        register(REQUEST_SUBSCRIBE_ONLINE, SubscribeToOnline.PARSER, SubscribeToOnline.class);
        register(REQUEST_UNSUBSCRIBE_ONLINE, UnsubscribeFromOnline.PARSER, UnsubscribeFromOnline.class);

        register(REQUEST_SUBSCRIBE_GROUP_ONLINE, SubscribeToGroupOnline.PARSER, SubscribeToGroupOnline.class);
        register(REQUEST_UNSUBSCRIBE_GROUP_ONLINE, UnsubscribeFromGroupOnline.PARSER, UnsubscribeFromGroupOnline.class);

        // Send message

        register(REQUEST_SEND_MESSAGE, RequestSendMessage.PARSER, RequestSendMessage.class);
        register(REQUEST_SEND_GROUP_MESSAGE, RequestSendGroupMessage.PARSER, RequestSendGroupMessage.class);

        register(REQUEST_MESSAGE_RECEIVED, RequestMessageReceived.PARSER, RequestMessageReceived.class);
        register(REQUEST_MESSAGE_READ, RequestMessageRead.PARSER, RequestMessageRead.class);

        // Group chats

        register(REQUEST_CREATE_CHAT, RequestCreateGroup.PARSER, RequestCreateGroup.class);
        register(RESPONSE_CREATE_CHAT, ResponseCreateGroup.PARSER, ResponseCreateGroup.class);
        register(REQUEST_INVITE_USER, RequestInviteUsers.PARSER, RequestInviteUsers.class);
        register(REQUEST_LEAVE_CHAT, RequestLeaveGroup.PARSER, RequestLeaveGroup.class);
        register(REQUEST_REMOVE_USER, RequestRemoveUser.PARSER, RequestRemoveUser.class);
        register(REQUEST_GROUP_EDIT_TITLE, RequestEditGroupTitle.PARSER, RequestEditGroupTitle.class);
        register(REQUEST_GROUP_EDIT_AVATAR, RequestEditGroupAvatar.PARSER, RequestEditGroupAvatar.class);

        // Online

        register(REQUEST_SET_ONLINE, RequestSetOnline.PARSER, RequestSetOnline.class);

        // Typing

        register(REQUEST_TYPING, RequestTyping.PARSER, RequestTyping.class);

        // Files

        register(REQUEST_UPLOAD_START, RequestStartUpload.PARSER, RequestStartUpload.class);
        register(RESPONSE_UPLOAD_STARTED, ResponseUploadStarted.PARSER, ResponseUploadStarted.class);

        register(REQUEST_UPLOAD, RequestUploadPart.PARSER, RequestUploadPart.class);

        register(REQUEST_UPLOAD_COMPLETE, RequestCompleteUpload.PARSER, RequestCompleteUpload.class);
        register(RESPONSE_UPLOAD_COMPLETED, ResponseUploadCompleted.PARSER, ResponseUploadCompleted.class);

        register(REQUEST_GET_FILE, RequestGetFile.PARSER, RequestGetFile.class);
        register(RESPONSE_GET_FILE, ResponseFilePart.PARSER, ResponseFilePart.class);

        // Google Push

        register(REQUEST_REGISTER_GOOGLE_PUSH, RequestRegisterGooglePush.PARSER, RequestRegisterGooglePush.class);
        register(REQUEST_UNREGISTER_GOOGLE_PUSH, RequestUnregisterPush.PARSER, RequestUnregisterPush.class);

        // Auth
        register(REQUEST_GET_AUTH, RequestGetAuth.PARSER, RequestGetAuth.class);
        register(RESPONSE_GET_AUTH, ResponseGetAuth.PARSER, ResponseGetAuth.class);

        register(REQUEST_REMOVE_AUTH, RequestRemoveAuth.PARSER, RequestRemoveAuth.class);
        register(REQUEST_REMOVE_ALL_OTHER_AUTH, RequestRemoveAllOtherAuths.PARSER, RequestRemoveAllOtherAuths.class);

        register(REQUEST_LOGOUT, RequestLogout.PARSER, RequestLogout.class);
    }

    // Common

    private static final int RESPONSE_VOID = 0x32;
    private static final int RESPONSE_SEQ = 0x48;

    // Auth

    private static final int REQUEST_AUTH_CODE = 0x01;
    private static final int RESPONSE_AUTH_CODE = 0x02;

    private static final int REQUEST_SIGN_IN = 0x03;
    private static final int REQUEST_SIGN_UP = 0x04;
    private static final int RESPONSE_AUTH = 0x05;

    private static final int REQUEST_GET_AUTH = 0x50;
    private static final int RESPONSE_GET_AUTH = 0x51;

    private static final int REQUEST_REMOVE_AUTH = 0x52;
    private static final int REQUEST_REMOVE_ALL_OTHER_AUTH = 0x53;

    private static final int REQUEST_LOGOUT = 0x54;

    // Profile

    private static final int REQUEST_EDIT_NAME = 0x35;
    private static final int REQUEST_EDIT_AVATAR = 0x1F;
    private static final int RESPONSE_AVATAR_CHANGED = 0x44;

    // Contacts

    private static final int REQUEST_IMPORT_CONTACTS = 0x07;
    private static final int RESPONSE_IMPORT_CONTACTS = 0x08;

    private static final int REQUEST_FIND_CONTACTS = 0x70;
    private static final int RESPONSE_FIND_CONTACTS = 0x71;

    private static final int REQUEST_GET_CONTACTS = 0x57;
    private static final int RESPONSE_GET_CONTACTS = 0x58;
    private static final int REQUEST_DELETE_CONTACT = 0x59;
    private static final int REQUEST_ADD_CONTACT = 0x72;
    private static final int REQUEST_EDIT_CONTACT = 0x60;

    // Users

    private static final int REQUEST_PUBLIC_KEYS = 0x06;
    private static final int RESPONSE_PUBLIC_KEYS = 0x18;

    // Updates

    private static final int REQUEST_GET_STATE = 0x09;
    private static final int REQUEST_GET_DIFFERENCE = 0x0B;
    private static final int DIFFERENCE = 0x0C;
    private static final int SEQ_UPDATE = 0x0D;
    private static final int FAT_SEQ_UPDATE = 0x49;
    private static final int SEQ_UPDATE_TOO_LONG = 0x19;
    private static final int WEAK_UPDATE = 0x1A;

    private static final int REQUEST_SUBSCRIBE_ONLINE = 0x20;
    private static final int REQUEST_UNSUBSCRIBE_ONLINE = 0x21;
    private static final int REQUEST_SUBSCRIBE_GROUP_ONLINE = 0x4A;
    private static final int REQUEST_UNSUBSCRIBE_GROUP_ONLINE = 0x4B;

    // Send Messages

    private static final int REQUEST_SEND_MESSAGE = 0x0E;
    private static final int REQUEST_SEND_GROUP_MESSAGE = 0x43;

    private static final int REQUEST_MESSAGE_RECEIVED = 0x37;
    private static final int REQUEST_MESSAGE_READ = 0x39;

    // Group chats

    private static final int REQUEST_CREATE_CHAT = 0x41;
    private static final int RESPONSE_CREATE_CHAT = 0x42;

    private static final int REQUEST_INVITE_USER = 0x45;
    private static final int REQUEST_LEAVE_CHAT = 0x46;
    private static final int REQUEST_REMOVE_USER = 0x47;

    private static final int REQUEST_GROUP_EDIT_TITLE = 0x55;
    private static final int REQUEST_GROUP_EDIT_AVATAR = 0x56;

    // Online

    private static final int REQUEST_SET_ONLINE = 0x1D;

    // Typing

    private static final int REQUEST_TYPING = 0x1B;

    // Files

    private static final int REQUEST_UPLOAD_START = 0x12;
    private static final int RESPONSE_UPLOAD_STARTED = 0x13;

    private static final int REQUEST_UPLOAD = 0x14;

    private static final int REQUEST_UPLOAD_COMPLETE = 0x16;
    private static final int RESPONSE_UPLOAD_COMPLETED = 0x17;

    private static final int REQUEST_GET_FILE = 0x10;
    private static final int RESPONSE_GET_FILE = 0x11;

    // Push

    private static final int REQUEST_REGISTER_GOOGLE_PUSH = 0x33;
    private static final int REQUEST_UNREGISTER_GOOGLE_PUSH = 0x34;

}
