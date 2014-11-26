package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.rpc.*;
import im.actor.api.*;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.concurrency.FutureCallback;
import java.util.concurrent.TimeoutException;

public class ApiRequests {
    private ActorApi api;

    public ApiRequests(ActorApi api) {
        this.api = api;
    }

    public Future<ResponseSendAuthCode> sendAuthCode(long phoneNumber, int appId, String apiKey) {
        return this.api.rpc(new RequestSendAuthCode(phoneNumber, appId, apiKey));
    }

    public Future<ResponseSendAuthCode> sendAuthCode(long phoneNumber, int appId, String apiKey, long requestTimeout) {
        return this.api.rpc(new RequestSendAuthCode(phoneNumber, appId, apiKey), requestTimeout);
    }

    public Future<ResponseSendAuthCode> sendAuthCode(long phoneNumber, int appId, String apiKey, FutureCallback<ResponseSendAuthCode> callback) {
        return this.api.rpc(new RequestSendAuthCode(phoneNumber, appId, apiKey), callback);
    }

    public Future<ResponseSendAuthCode> sendAuthCode(long phoneNumber, int appId, String apiKey, long requestTimeout, FutureCallback<ResponseSendAuthCode> callback) {
        return this.api.rpc(new RequestSendAuthCode(phoneNumber, appId, apiKey), requestTimeout, callback);
    }

    public ResponseSendAuthCode sendAuthCodeSync (long phoneNumber, int appId, String apiKey) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSendAuthCode(phoneNumber, appId, apiKey));
    }

    public ResponseSendAuthCode sendAuthCodeSync (long phoneNumber, int appId, String apiKey, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSendAuthCode(phoneNumber, appId, apiKey), requestTimeout);
    }

    public Future<ResponseVoid> sendAuthCall(long phoneNumber, String smsHash, int appId, String apiKey) {
        return this.api.rpc(new RequestSendAuthCall(phoneNumber, smsHash, appId, apiKey));
    }

    public Future<ResponseVoid> sendAuthCall(long phoneNumber, String smsHash, int appId, String apiKey, long requestTimeout) {
        return this.api.rpc(new RequestSendAuthCall(phoneNumber, smsHash, appId, apiKey), requestTimeout);
    }

    public Future<ResponseVoid> sendAuthCall(long phoneNumber, String smsHash, int appId, String apiKey, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSendAuthCall(phoneNumber, smsHash, appId, apiKey), callback);
    }

    public Future<ResponseVoid> sendAuthCall(long phoneNumber, String smsHash, int appId, String apiKey, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSendAuthCall(phoneNumber, smsHash, appId, apiKey), requestTimeout, callback);
    }

    public ResponseVoid sendAuthCallSync (long phoneNumber, String smsHash, int appId, String apiKey) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSendAuthCall(phoneNumber, smsHash, appId, apiKey));
    }

    public ResponseVoid sendAuthCallSync (long phoneNumber, String smsHash, int appId, String apiKey, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSendAuthCall(phoneNumber, smsHash, appId, apiKey), requestTimeout);
    }

    public Future<ResponseAuth> signIn(long phoneNumber, String smsHash, String smsCode, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey) {
        return this.api.rpc(new RequestSignIn(phoneNumber, smsHash, smsCode, publicKey, deviceHash, deviceTitle, appId, appKey));
    }

    public Future<ResponseAuth> signIn(long phoneNumber, String smsHash, String smsCode, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, long requestTimeout) {
        return this.api.rpc(new RequestSignIn(phoneNumber, smsHash, smsCode, publicKey, deviceHash, deviceTitle, appId, appKey), requestTimeout);
    }

    public Future<ResponseAuth> signIn(long phoneNumber, String smsHash, String smsCode, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, FutureCallback<ResponseAuth> callback) {
        return this.api.rpc(new RequestSignIn(phoneNumber, smsHash, smsCode, publicKey, deviceHash, deviceTitle, appId, appKey), callback);
    }

    public Future<ResponseAuth> signIn(long phoneNumber, String smsHash, String smsCode, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, long requestTimeout, FutureCallback<ResponseAuth> callback) {
        return this.api.rpc(new RequestSignIn(phoneNumber, smsHash, smsCode, publicKey, deviceHash, deviceTitle, appId, appKey), requestTimeout, callback);
    }

    public ResponseAuth signInSync (long phoneNumber, String smsHash, String smsCode, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSignIn(phoneNumber, smsHash, smsCode, publicKey, deviceHash, deviceTitle, appId, appKey));
    }

    public ResponseAuth signInSync (long phoneNumber, String smsHash, String smsCode, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSignIn(phoneNumber, smsHash, smsCode, publicKey, deviceHash, deviceTitle, appId, appKey), requestTimeout);
    }

    public Future<ResponseAuth> signUp(long phoneNumber, String smsHash, String smsCode, String name, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, boolean isSilent) {
        return this.api.rpc(new RequestSignUp(phoneNumber, smsHash, smsCode, name, publicKey, deviceHash, deviceTitle, appId, appKey, isSilent));
    }

    public Future<ResponseAuth> signUp(long phoneNumber, String smsHash, String smsCode, String name, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, boolean isSilent, long requestTimeout) {
        return this.api.rpc(new RequestSignUp(phoneNumber, smsHash, smsCode, name, publicKey, deviceHash, deviceTitle, appId, appKey, isSilent), requestTimeout);
    }

    public Future<ResponseAuth> signUp(long phoneNumber, String smsHash, String smsCode, String name, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, boolean isSilent, FutureCallback<ResponseAuth> callback) {
        return this.api.rpc(new RequestSignUp(phoneNumber, smsHash, smsCode, name, publicKey, deviceHash, deviceTitle, appId, appKey, isSilent), callback);
    }

    public Future<ResponseAuth> signUp(long phoneNumber, String smsHash, String smsCode, String name, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, boolean isSilent, long requestTimeout, FutureCallback<ResponseAuth> callback) {
        return this.api.rpc(new RequestSignUp(phoneNumber, smsHash, smsCode, name, publicKey, deviceHash, deviceTitle, appId, appKey, isSilent), requestTimeout, callback);
    }

    public ResponseAuth signUpSync (long phoneNumber, String smsHash, String smsCode, String name, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, boolean isSilent) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSignUp(phoneNumber, smsHash, smsCode, name, publicKey, deviceHash, deviceTitle, appId, appKey, isSilent));
    }

    public ResponseAuth signUpSync (long phoneNumber, String smsHash, String smsCode, String name, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, boolean isSilent, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSignUp(phoneNumber, smsHash, smsCode, name, publicKey, deviceHash, deviceTitle, appId, appKey, isSilent), requestTimeout);
    }

    public Future<ResponseGetAuthSessions> getAuthSessions() {
        return this.api.rpc(new RequestGetAuthSessions());
    }

    public Future<ResponseGetAuthSessions> getAuthSessions(long requestTimeout) {
        return this.api.rpc(new RequestGetAuthSessions(), requestTimeout);
    }

    public Future<ResponseGetAuthSessions> getAuthSessions(FutureCallback<ResponseGetAuthSessions> callback) {
        return this.api.rpc(new RequestGetAuthSessions(), callback);
    }

    public Future<ResponseGetAuthSessions> getAuthSessions(long requestTimeout, FutureCallback<ResponseGetAuthSessions> callback) {
        return this.api.rpc(new RequestGetAuthSessions(), requestTimeout, callback);
    }

    public ResponseGetAuthSessions getAuthSessionsSync () throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetAuthSessions());
    }

    public ResponseGetAuthSessions getAuthSessionsSync (long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetAuthSessions(), requestTimeout);
    }

    public Future<ResponseVoid> terminateSession(int id) {
        return this.api.rpc(new RequestTerminateSession(id));
    }

    public Future<ResponseVoid> terminateSession(int id, long requestTimeout) {
        return this.api.rpc(new RequestTerminateSession(id), requestTimeout);
    }

    public Future<ResponseVoid> terminateSession(int id, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestTerminateSession(id), callback);
    }

    public Future<ResponseVoid> terminateSession(int id, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestTerminateSession(id), requestTimeout, callback);
    }

    public ResponseVoid terminateSessionSync (int id) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestTerminateSession(id));
    }

    public ResponseVoid terminateSessionSync (int id, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestTerminateSession(id), requestTimeout);
    }

    public Future<ResponseVoid> terminateAllSessions() {
        return this.api.rpc(new RequestTerminateAllSessions());
    }

    public Future<ResponseVoid> terminateAllSessions(long requestTimeout) {
        return this.api.rpc(new RequestTerminateAllSessions(), requestTimeout);
    }

    public Future<ResponseVoid> terminateAllSessions(FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestTerminateAllSessions(), callback);
    }

    public Future<ResponseVoid> terminateAllSessions(long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestTerminateAllSessions(), requestTimeout, callback);
    }

    public ResponseVoid terminateAllSessionsSync () throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestTerminateAllSessions());
    }

    public ResponseVoid terminateAllSessionsSync (long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestTerminateAllSessions(), requestTimeout);
    }

    public Future<ResponseVoid> signOut() {
        return this.api.rpc(new RequestSignOut());
    }

    public Future<ResponseVoid> signOut(long requestTimeout) {
        return this.api.rpc(new RequestSignOut(), requestTimeout);
    }

    public Future<ResponseVoid> signOut(FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSignOut(), callback);
    }

    public Future<ResponseVoid> signOut(long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSignOut(), requestTimeout, callback);
    }

    public ResponseVoid signOutSync () throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSignOut());
    }

    public ResponseVoid signOutSync (long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSignOut(), requestTimeout);
    }

    public Future<ResponseSeq> editUserLocalName(int uid, long accessHash, String name) {
        return this.api.rpc(new RequestEditUserLocalName(uid, accessHash, name));
    }

    public Future<ResponseSeq> editUserLocalName(int uid, long accessHash, String name, long requestTimeout) {
        return this.api.rpc(new RequestEditUserLocalName(uid, accessHash, name), requestTimeout);
    }

    public Future<ResponseSeq> editUserLocalName(int uid, long accessHash, String name, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestEditUserLocalName(uid, accessHash, name), callback);
    }

    public Future<ResponseSeq> editUserLocalName(int uid, long accessHash, String name, long requestTimeout, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestEditUserLocalName(uid, accessHash, name), requestTimeout, callback);
    }

    public ResponseSeq editUserLocalNameSync (int uid, long accessHash, String name) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditUserLocalName(uid, accessHash, name));
    }

    public ResponseSeq editUserLocalNameSync (int uid, long accessHash, String name, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditUserLocalName(uid, accessHash, name), requestTimeout);
    }

    public Future<ResponseSeq> editName(String name) {
        return this.api.rpc(new RequestEditName(name));
    }

    public Future<ResponseSeq> editName(String name, long requestTimeout) {
        return this.api.rpc(new RequestEditName(name), requestTimeout);
    }

    public Future<ResponseSeq> editName(String name, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestEditName(name), callback);
    }

    public Future<ResponseSeq> editName(String name, long requestTimeout, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestEditName(name), requestTimeout, callback);
    }

    public ResponseSeq editNameSync (String name) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditName(name));
    }

    public ResponseSeq editNameSync (String name, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditName(name), requestTimeout);
    }

    public Future<ResponseEditAvatar> editAvatar(FileLocation fileLocation) {
        return this.api.rpc(new RequestEditAvatar(fileLocation));
    }

    public Future<ResponseEditAvatar> editAvatar(FileLocation fileLocation, long requestTimeout) {
        return this.api.rpc(new RequestEditAvatar(fileLocation), requestTimeout);
    }

    public Future<ResponseEditAvatar> editAvatar(FileLocation fileLocation, FutureCallback<ResponseEditAvatar> callback) {
        return this.api.rpc(new RequestEditAvatar(fileLocation), callback);
    }

    public Future<ResponseEditAvatar> editAvatar(FileLocation fileLocation, long requestTimeout, FutureCallback<ResponseEditAvatar> callback) {
        return this.api.rpc(new RequestEditAvatar(fileLocation), requestTimeout, callback);
    }

    public ResponseEditAvatar editAvatarSync (FileLocation fileLocation) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditAvatar(fileLocation));
    }

    public ResponseEditAvatar editAvatarSync (FileLocation fileLocation, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditAvatar(fileLocation), requestTimeout);
    }

    public Future<ResponseSeq> removeAvatar() {
        return this.api.rpc(new RequestRemoveAvatar());
    }

    public Future<ResponseSeq> removeAvatar(long requestTimeout) {
        return this.api.rpc(new RequestRemoveAvatar(), requestTimeout);
    }

    public Future<ResponseSeq> removeAvatar(FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestRemoveAvatar(), callback);
    }

    public Future<ResponseSeq> removeAvatar(long requestTimeout, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestRemoveAvatar(), requestTimeout, callback);
    }

    public ResponseSeq removeAvatarSync () throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRemoveAvatar());
    }

    public ResponseSeq removeAvatarSync (long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRemoveAvatar(), requestTimeout);
    }

    public Future<ResponseImportContacts> importContacts(List<PhoneToImport> phones, List<EmailToImport> emails) {
        return this.api.rpc(new RequestImportContacts(phones, emails));
    }

    public Future<ResponseImportContacts> importContacts(List<PhoneToImport> phones, List<EmailToImport> emails, long requestTimeout) {
        return this.api.rpc(new RequestImportContacts(phones, emails), requestTimeout);
    }

    public Future<ResponseImportContacts> importContacts(List<PhoneToImport> phones, List<EmailToImport> emails, FutureCallback<ResponseImportContacts> callback) {
        return this.api.rpc(new RequestImportContacts(phones, emails), callback);
    }

    public Future<ResponseImportContacts> importContacts(List<PhoneToImport> phones, List<EmailToImport> emails, long requestTimeout, FutureCallback<ResponseImportContacts> callback) {
        return this.api.rpc(new RequestImportContacts(phones, emails), requestTimeout, callback);
    }

    public ResponseImportContacts importContactsSync (List<PhoneToImport> phones, List<EmailToImport> emails) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestImportContacts(phones, emails));
    }

    public ResponseImportContacts importContactsSync (List<PhoneToImport> phones, List<EmailToImport> emails, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestImportContacts(phones, emails), requestTimeout);
    }

    public Future<ResponseGetContacts> getContacts(String contactsHash) {
        return this.api.rpc(new RequestGetContacts(contactsHash));
    }

    public Future<ResponseGetContacts> getContacts(String contactsHash, long requestTimeout) {
        return this.api.rpc(new RequestGetContacts(contactsHash), requestTimeout);
    }

    public Future<ResponseGetContacts> getContacts(String contactsHash, FutureCallback<ResponseGetContacts> callback) {
        return this.api.rpc(new RequestGetContacts(contactsHash), callback);
    }

    public Future<ResponseGetContacts> getContacts(String contactsHash, long requestTimeout, FutureCallback<ResponseGetContacts> callback) {
        return this.api.rpc(new RequestGetContacts(contactsHash), requestTimeout, callback);
    }

    public ResponseGetContacts getContactsSync (String contactsHash) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetContacts(contactsHash));
    }

    public ResponseGetContacts getContactsSync (String contactsHash, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetContacts(contactsHash), requestTimeout);
    }

    public Future<ResponseSeq> removeContact(int uid, long accessHash) {
        return this.api.rpc(new RequestRemoveContact(uid, accessHash));
    }

    public Future<ResponseSeq> removeContact(int uid, long accessHash, long requestTimeout) {
        return this.api.rpc(new RequestRemoveContact(uid, accessHash), requestTimeout);
    }

    public Future<ResponseSeq> removeContact(int uid, long accessHash, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestRemoveContact(uid, accessHash), callback);
    }

    public Future<ResponseSeq> removeContact(int uid, long accessHash, long requestTimeout, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestRemoveContact(uid, accessHash), requestTimeout, callback);
    }

    public ResponseSeq removeContactSync (int uid, long accessHash) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRemoveContact(uid, accessHash));
    }

    public ResponseSeq removeContactSync (int uid, long accessHash, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRemoveContact(uid, accessHash), requestTimeout);
    }

    public Future<ResponseSeq> addContact(int uid, long accessHash) {
        return this.api.rpc(new RequestAddContact(uid, accessHash));
    }

    public Future<ResponseSeq> addContact(int uid, long accessHash, long requestTimeout) {
        return this.api.rpc(new RequestAddContact(uid, accessHash), requestTimeout);
    }

    public Future<ResponseSeq> addContact(int uid, long accessHash, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestAddContact(uid, accessHash), callback);
    }

    public Future<ResponseSeq> addContact(int uid, long accessHash, long requestTimeout, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestAddContact(uid, accessHash), requestTimeout, callback);
    }

    public ResponseSeq addContactSync (int uid, long accessHash) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestAddContact(uid, accessHash));
    }

    public ResponseSeq addContactSync (int uid, long accessHash, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestAddContact(uid, accessHash), requestTimeout);
    }

    public Future<ResponseSearchContacts> searchContacts(String request) {
        return this.api.rpc(new RequestSearchContacts(request));
    }

    public Future<ResponseSearchContacts> searchContacts(String request, long requestTimeout) {
        return this.api.rpc(new RequestSearchContacts(request), requestTimeout);
    }

    public Future<ResponseSearchContacts> searchContacts(String request, FutureCallback<ResponseSearchContacts> callback) {
        return this.api.rpc(new RequestSearchContacts(request), callback);
    }

    public Future<ResponseSearchContacts> searchContacts(String request, long requestTimeout, FutureCallback<ResponseSearchContacts> callback) {
        return this.api.rpc(new RequestSearchContacts(request), requestTimeout, callback);
    }

    public ResponseSearchContacts searchContactsSync (String request) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSearchContacts(request));
    }

    public ResponseSearchContacts searchContactsSync (String request, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSearchContacts(request), requestTimeout);
    }

    public Future<ResponseSeqDate> sendEncryptedMessage(OutPeer peer, long rid, byte[] encryptedMessage, List<EncryptedAesKey> keys, List<EncryptedAesKey> ownKeys) {
        return this.api.rpc(new RequestSendEncryptedMessage(peer, rid, encryptedMessage, keys, ownKeys));
    }

    public Future<ResponseSeqDate> sendEncryptedMessage(OutPeer peer, long rid, byte[] encryptedMessage, List<EncryptedAesKey> keys, List<EncryptedAesKey> ownKeys, long requestTimeout) {
        return this.api.rpc(new RequestSendEncryptedMessage(peer, rid, encryptedMessage, keys, ownKeys), requestTimeout);
    }

    public Future<ResponseSeqDate> sendEncryptedMessage(OutPeer peer, long rid, byte[] encryptedMessage, List<EncryptedAesKey> keys, List<EncryptedAesKey> ownKeys, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestSendEncryptedMessage(peer, rid, encryptedMessage, keys, ownKeys), callback);
    }

    public Future<ResponseSeqDate> sendEncryptedMessage(OutPeer peer, long rid, byte[] encryptedMessage, List<EncryptedAesKey> keys, List<EncryptedAesKey> ownKeys, long requestTimeout, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestSendEncryptedMessage(peer, rid, encryptedMessage, keys, ownKeys), requestTimeout, callback);
    }

    public ResponseSeqDate sendEncryptedMessageSync (OutPeer peer, long rid, byte[] encryptedMessage, List<EncryptedAesKey> keys, List<EncryptedAesKey> ownKeys) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSendEncryptedMessage(peer, rid, encryptedMessage, keys, ownKeys));
    }

    public ResponseSeqDate sendEncryptedMessageSync (OutPeer peer, long rid, byte[] encryptedMessage, List<EncryptedAesKey> keys, List<EncryptedAesKey> ownKeys, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSendEncryptedMessage(peer, rid, encryptedMessage, keys, ownKeys), requestTimeout);
    }

    public Future<ResponseSeqDate> sendMessage(OutPeer peer, long rid, MessageContent message) {
        return this.api.rpc(new RequestSendMessage(peer, rid, message));
    }

    public Future<ResponseSeqDate> sendMessage(OutPeer peer, long rid, MessageContent message, long requestTimeout) {
        return this.api.rpc(new RequestSendMessage(peer, rid, message), requestTimeout);
    }

    public Future<ResponseSeqDate> sendMessage(OutPeer peer, long rid, MessageContent message, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestSendMessage(peer, rid, message), callback);
    }

    public Future<ResponseSeqDate> sendMessage(OutPeer peer, long rid, MessageContent message, long requestTimeout, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestSendMessage(peer, rid, message), requestTimeout, callback);
    }

    public ResponseSeqDate sendMessageSync (OutPeer peer, long rid, MessageContent message) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSendMessage(peer, rid, message));
    }

    public ResponseSeqDate sendMessageSync (OutPeer peer, long rid, MessageContent message, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSendMessage(peer, rid, message), requestTimeout);
    }

    public Future<ResponseVoid> encryptedReceived(OutPeer peer, long rid) {
        return this.api.rpc(new RequestEncryptedReceived(peer, rid));
    }

    public Future<ResponseVoid> encryptedReceived(OutPeer peer, long rid, long requestTimeout) {
        return this.api.rpc(new RequestEncryptedReceived(peer, rid), requestTimeout);
    }

    public Future<ResponseVoid> encryptedReceived(OutPeer peer, long rid, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestEncryptedReceived(peer, rid), callback);
    }

    public Future<ResponseVoid> encryptedReceived(OutPeer peer, long rid, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestEncryptedReceived(peer, rid), requestTimeout, callback);
    }

    public ResponseVoid encryptedReceivedSync (OutPeer peer, long rid) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEncryptedReceived(peer, rid));
    }

    public ResponseVoid encryptedReceivedSync (OutPeer peer, long rid, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEncryptedReceived(peer, rid), requestTimeout);
    }

    public Future<ResponseVoid> encryptedRead(OutPeer peer, long rid) {
        return this.api.rpc(new RequestEncryptedRead(peer, rid));
    }

    public Future<ResponseVoid> encryptedRead(OutPeer peer, long rid, long requestTimeout) {
        return this.api.rpc(new RequestEncryptedRead(peer, rid), requestTimeout);
    }

    public Future<ResponseVoid> encryptedRead(OutPeer peer, long rid, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestEncryptedRead(peer, rid), callback);
    }

    public Future<ResponseVoid> encryptedRead(OutPeer peer, long rid, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestEncryptedRead(peer, rid), requestTimeout, callback);
    }

    public ResponseVoid encryptedReadSync (OutPeer peer, long rid) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEncryptedRead(peer, rid));
    }

    public ResponseVoid encryptedReadSync (OutPeer peer, long rid, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEncryptedRead(peer, rid), requestTimeout);
    }

    public Future<ResponseVoid> messageReceived(OutPeer peer, long date) {
        return this.api.rpc(new RequestMessageReceived(peer, date));
    }

    public Future<ResponseVoid> messageReceived(OutPeer peer, long date, long requestTimeout) {
        return this.api.rpc(new RequestMessageReceived(peer, date), requestTimeout);
    }

    public Future<ResponseVoid> messageReceived(OutPeer peer, long date, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestMessageReceived(peer, date), callback);
    }

    public Future<ResponseVoid> messageReceived(OutPeer peer, long date, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestMessageReceived(peer, date), requestTimeout, callback);
    }

    public ResponseVoid messageReceivedSync (OutPeer peer, long date) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestMessageReceived(peer, date));
    }

    public ResponseVoid messageReceivedSync (OutPeer peer, long date, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestMessageReceived(peer, date), requestTimeout);
    }

    public Future<ResponseVoid> messageRead(OutPeer peer, long date) {
        return this.api.rpc(new RequestMessageRead(peer, date));
    }

    public Future<ResponseVoid> messageRead(OutPeer peer, long date, long requestTimeout) {
        return this.api.rpc(new RequestMessageRead(peer, date), requestTimeout);
    }

    public Future<ResponseVoid> messageRead(OutPeer peer, long date, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestMessageRead(peer, date), callback);
    }

    public Future<ResponseVoid> messageRead(OutPeer peer, long date, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestMessageRead(peer, date), requestTimeout, callback);
    }

    public ResponseVoid messageReadSync (OutPeer peer, long date) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestMessageRead(peer, date));
    }

    public ResponseVoid messageReadSync (OutPeer peer, long date, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestMessageRead(peer, date), requestTimeout);
    }

    public Future<ResponseVoid> deleteMessage(OutPeer peer, List<Long> rids) {
        return this.api.rpc(new RequestDeleteMessage(peer, rids));
    }

    public Future<ResponseVoid> deleteMessage(OutPeer peer, List<Long> rids, long requestTimeout) {
        return this.api.rpc(new RequestDeleteMessage(peer, rids), requestTimeout);
    }

    public Future<ResponseVoid> deleteMessage(OutPeer peer, List<Long> rids, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestDeleteMessage(peer, rids), callback);
    }

    public Future<ResponseVoid> deleteMessage(OutPeer peer, List<Long> rids, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestDeleteMessage(peer, rids), requestTimeout, callback);
    }

    public ResponseVoid deleteMessageSync (OutPeer peer, List<Long> rids) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestDeleteMessage(peer, rids));
    }

    public ResponseVoid deleteMessageSync (OutPeer peer, List<Long> rids, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestDeleteMessage(peer, rids), requestTimeout);
    }

    public Future<ResponseSeq> clearChat(OutPeer peer) {
        return this.api.rpc(new RequestClearChat(peer));
    }

    public Future<ResponseSeq> clearChat(OutPeer peer, long requestTimeout) {
        return this.api.rpc(new RequestClearChat(peer), requestTimeout);
    }

    public Future<ResponseSeq> clearChat(OutPeer peer, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestClearChat(peer), callback);
    }

    public Future<ResponseSeq> clearChat(OutPeer peer, long requestTimeout, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestClearChat(peer), requestTimeout, callback);
    }

    public ResponseSeq clearChatSync (OutPeer peer) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestClearChat(peer));
    }

    public ResponseSeq clearChatSync (OutPeer peer, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestClearChat(peer), requestTimeout);
    }

    public Future<ResponseSeq> deleteChat(OutPeer peer) {
        return this.api.rpc(new RequestDeleteChat(peer));
    }

    public Future<ResponseSeq> deleteChat(OutPeer peer, long requestTimeout) {
        return this.api.rpc(new RequestDeleteChat(peer), requestTimeout);
    }

    public Future<ResponseSeq> deleteChat(OutPeer peer, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestDeleteChat(peer), callback);
    }

    public Future<ResponseSeq> deleteChat(OutPeer peer, long requestTimeout, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestDeleteChat(peer), requestTimeout, callback);
    }

    public ResponseSeq deleteChatSync (OutPeer peer) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestDeleteChat(peer));
    }

    public ResponseSeq deleteChatSync (OutPeer peer, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestDeleteChat(peer), requestTimeout);
    }

    public Future<ResponseCreateGroup> createGroup(long rid, String title, List<UserOutPeer> users) {
        return this.api.rpc(new RequestCreateGroup(rid, title, users));
    }

    public Future<ResponseCreateGroup> createGroup(long rid, String title, List<UserOutPeer> users, long requestTimeout) {
        return this.api.rpc(new RequestCreateGroup(rid, title, users), requestTimeout);
    }

    public Future<ResponseCreateGroup> createGroup(long rid, String title, List<UserOutPeer> users, FutureCallback<ResponseCreateGroup> callback) {
        return this.api.rpc(new RequestCreateGroup(rid, title, users), callback);
    }

    public Future<ResponseCreateGroup> createGroup(long rid, String title, List<UserOutPeer> users, long requestTimeout, FutureCallback<ResponseCreateGroup> callback) {
        return this.api.rpc(new RequestCreateGroup(rid, title, users), requestTimeout, callback);
    }

    public ResponseCreateGroup createGroupSync (long rid, String title, List<UserOutPeer> users) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestCreateGroup(rid, title, users));
    }

    public ResponseCreateGroup createGroupSync (long rid, String title, List<UserOutPeer> users, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestCreateGroup(rid, title, users), requestTimeout);
    }

    public Future<ResponseSeqDate> editGroupTitle(GroupOutPeer groupPeer, String title) {
        return this.api.rpc(new RequestEditGroupTitle(groupPeer, title));
    }

    public Future<ResponseSeqDate> editGroupTitle(GroupOutPeer groupPeer, String title, long requestTimeout) {
        return this.api.rpc(new RequestEditGroupTitle(groupPeer, title), requestTimeout);
    }

    public Future<ResponseSeqDate> editGroupTitle(GroupOutPeer groupPeer, String title, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestEditGroupTitle(groupPeer, title), callback);
    }

    public Future<ResponseSeqDate> editGroupTitle(GroupOutPeer groupPeer, String title, long requestTimeout, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestEditGroupTitle(groupPeer, title), requestTimeout, callback);
    }

    public ResponseSeqDate editGroupTitleSync (GroupOutPeer groupPeer, String title) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditGroupTitle(groupPeer, title));
    }

    public ResponseSeqDate editGroupTitleSync (GroupOutPeer groupPeer, String title, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditGroupTitle(groupPeer, title), requestTimeout);
    }

    public Future<ResponseEditGroupAvatar> editGroupAvatar(GroupOutPeer groupPeer, FileLocation fileLocation) {
        return this.api.rpc(new RequestEditGroupAvatar(groupPeer, fileLocation));
    }

    public Future<ResponseEditGroupAvatar> editGroupAvatar(GroupOutPeer groupPeer, FileLocation fileLocation, long requestTimeout) {
        return this.api.rpc(new RequestEditGroupAvatar(groupPeer, fileLocation), requestTimeout);
    }

    public Future<ResponseEditGroupAvatar> editGroupAvatar(GroupOutPeer groupPeer, FileLocation fileLocation, FutureCallback<ResponseEditGroupAvatar> callback) {
        return this.api.rpc(new RequestEditGroupAvatar(groupPeer, fileLocation), callback);
    }

    public Future<ResponseEditGroupAvatar> editGroupAvatar(GroupOutPeer groupPeer, FileLocation fileLocation, long requestTimeout, FutureCallback<ResponseEditGroupAvatar> callback) {
        return this.api.rpc(new RequestEditGroupAvatar(groupPeer, fileLocation), requestTimeout, callback);
    }

    public ResponseEditGroupAvatar editGroupAvatarSync (GroupOutPeer groupPeer, FileLocation fileLocation) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditGroupAvatar(groupPeer, fileLocation));
    }

    public ResponseEditGroupAvatar editGroupAvatarSync (GroupOutPeer groupPeer, FileLocation fileLocation, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestEditGroupAvatar(groupPeer, fileLocation), requestTimeout);
    }

    public Future<ResponseSeqDate> removeGroupAvatar(GroupOutPeer groupPeer) {
        return this.api.rpc(new RequestRemoveGroupAvatar(groupPeer));
    }

    public Future<ResponseSeqDate> removeGroupAvatar(GroupOutPeer groupPeer, long requestTimeout) {
        return this.api.rpc(new RequestRemoveGroupAvatar(groupPeer), requestTimeout);
    }

    public Future<ResponseSeqDate> removeGroupAvatar(GroupOutPeer groupPeer, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestRemoveGroupAvatar(groupPeer), callback);
    }

    public Future<ResponseSeqDate> removeGroupAvatar(GroupOutPeer groupPeer, long requestTimeout, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestRemoveGroupAvatar(groupPeer), requestTimeout, callback);
    }

    public ResponseSeqDate removeGroupAvatarSync (GroupOutPeer groupPeer) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRemoveGroupAvatar(groupPeer));
    }

    public ResponseSeqDate removeGroupAvatarSync (GroupOutPeer groupPeer, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRemoveGroupAvatar(groupPeer), requestTimeout);
    }

    public Future<ResponseSeqDate> inviteUser(GroupOutPeer groupPeer, UserOutPeer user) {
        return this.api.rpc(new RequestInviteUser(groupPeer, user));
    }

    public Future<ResponseSeqDate> inviteUser(GroupOutPeer groupPeer, UserOutPeer user, long requestTimeout) {
        return this.api.rpc(new RequestInviteUser(groupPeer, user), requestTimeout);
    }

    public Future<ResponseSeqDate> inviteUser(GroupOutPeer groupPeer, UserOutPeer user, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestInviteUser(groupPeer, user), callback);
    }

    public Future<ResponseSeqDate> inviteUser(GroupOutPeer groupPeer, UserOutPeer user, long requestTimeout, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestInviteUser(groupPeer, user), requestTimeout, callback);
    }

    public ResponseSeqDate inviteUserSync (GroupOutPeer groupPeer, UserOutPeer user) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestInviteUser(groupPeer, user));
    }

    public ResponseSeqDate inviteUserSync (GroupOutPeer groupPeer, UserOutPeer user, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestInviteUser(groupPeer, user), requestTimeout);
    }

    public Future<ResponseSeqDate> leaveGroup(GroupOutPeer groupPeer) {
        return this.api.rpc(new RequestLeaveGroup(groupPeer));
    }

    public Future<ResponseSeqDate> leaveGroup(GroupOutPeer groupPeer, long requestTimeout) {
        return this.api.rpc(new RequestLeaveGroup(groupPeer), requestTimeout);
    }

    public Future<ResponseSeqDate> leaveGroup(GroupOutPeer groupPeer, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestLeaveGroup(groupPeer), callback);
    }

    public Future<ResponseSeqDate> leaveGroup(GroupOutPeer groupPeer, long requestTimeout, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestLeaveGroup(groupPeer), requestTimeout, callback);
    }

    public ResponseSeqDate leaveGroupSync (GroupOutPeer groupPeer) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestLeaveGroup(groupPeer));
    }

    public ResponseSeqDate leaveGroupSync (GroupOutPeer groupPeer, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestLeaveGroup(groupPeer), requestTimeout);
    }

    public Future<ResponseSeqDate> deleteGroup(GroupOutPeer groupPeer) {
        return this.api.rpc(new RequestDeleteGroup(groupPeer));
    }

    public Future<ResponseSeqDate> deleteGroup(GroupOutPeer groupPeer, long requestTimeout) {
        return this.api.rpc(new RequestDeleteGroup(groupPeer), requestTimeout);
    }

    public Future<ResponseSeqDate> deleteGroup(GroupOutPeer groupPeer, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestDeleteGroup(groupPeer), callback);
    }

    public Future<ResponseSeqDate> deleteGroup(GroupOutPeer groupPeer, long requestTimeout, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestDeleteGroup(groupPeer), requestTimeout, callback);
    }

    public ResponseSeqDate deleteGroupSync (GroupOutPeer groupPeer) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestDeleteGroup(groupPeer));
    }

    public ResponseSeqDate deleteGroupSync (GroupOutPeer groupPeer, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestDeleteGroup(groupPeer), requestTimeout);
    }

    public Future<ResponseSeqDate> removeUser(GroupOutPeer groupPeer, UserOutPeer user) {
        return this.api.rpc(new RequestRemoveUser(groupPeer, user));
    }

    public Future<ResponseSeqDate> removeUser(GroupOutPeer groupPeer, UserOutPeer user, long requestTimeout) {
        return this.api.rpc(new RequestRemoveUser(groupPeer, user), requestTimeout);
    }

    public Future<ResponseSeqDate> removeUser(GroupOutPeer groupPeer, UserOutPeer user, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestRemoveUser(groupPeer, user), callback);
    }

    public Future<ResponseSeqDate> removeUser(GroupOutPeer groupPeer, UserOutPeer user, long requestTimeout, FutureCallback<ResponseSeqDate> callback) {
        return this.api.rpc(new RequestRemoveUser(groupPeer, user), requestTimeout, callback);
    }

    public ResponseSeqDate removeUserSync (GroupOutPeer groupPeer, UserOutPeer user) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRemoveUser(groupPeer, user));
    }

    public ResponseSeqDate removeUserSync (GroupOutPeer groupPeer, UserOutPeer user, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRemoveUser(groupPeer, user), requestTimeout);
    }

    public Future<ResponseLoadHistory> loadHistory(OutPeer peer, long startDate, int limit) {
        return this.api.rpc(new RequestLoadHistory(peer, startDate, limit));
    }

    public Future<ResponseLoadHistory> loadHistory(OutPeer peer, long startDate, int limit, long requestTimeout) {
        return this.api.rpc(new RequestLoadHistory(peer, startDate, limit), requestTimeout);
    }

    public Future<ResponseLoadHistory> loadHistory(OutPeer peer, long startDate, int limit, FutureCallback<ResponseLoadHistory> callback) {
        return this.api.rpc(new RequestLoadHistory(peer, startDate, limit), callback);
    }

    public Future<ResponseLoadHistory> loadHistory(OutPeer peer, long startDate, int limit, long requestTimeout, FutureCallback<ResponseLoadHistory> callback) {
        return this.api.rpc(new RequestLoadHistory(peer, startDate, limit), requestTimeout, callback);
    }

    public ResponseLoadHistory loadHistorySync (OutPeer peer, long startDate, int limit) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestLoadHistory(peer, startDate, limit));
    }

    public ResponseLoadHistory loadHistorySync (OutPeer peer, long startDate, int limit, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestLoadHistory(peer, startDate, limit), requestTimeout);
    }

    public Future<ResponseLoadDialogs> loadDialogs(long startDate, int limit) {
        return this.api.rpc(new RequestLoadDialogs(startDate, limit));
    }

    public Future<ResponseLoadDialogs> loadDialogs(long startDate, int limit, long requestTimeout) {
        return this.api.rpc(new RequestLoadDialogs(startDate, limit), requestTimeout);
    }

    public Future<ResponseLoadDialogs> loadDialogs(long startDate, int limit, FutureCallback<ResponseLoadDialogs> callback) {
        return this.api.rpc(new RequestLoadDialogs(startDate, limit), callback);
    }

    public Future<ResponseLoadDialogs> loadDialogs(long startDate, int limit, long requestTimeout, FutureCallback<ResponseLoadDialogs> callback) {
        return this.api.rpc(new RequestLoadDialogs(startDate, limit), requestTimeout, callback);
    }

    public ResponseLoadDialogs loadDialogsSync (long startDate, int limit) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestLoadDialogs(startDate, limit));
    }

    public ResponseLoadDialogs loadDialogsSync (long startDate, int limit, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestLoadDialogs(startDate, limit), requestTimeout);
    }

    public Future<ResponseGetPublicKeys> getPublicKeys(List<PublicKeyRequest> keys) {
        return this.api.rpc(new RequestGetPublicKeys(keys));
    }

    public Future<ResponseGetPublicKeys> getPublicKeys(List<PublicKeyRequest> keys, long requestTimeout) {
        return this.api.rpc(new RequestGetPublicKeys(keys), requestTimeout);
    }

    public Future<ResponseGetPublicKeys> getPublicKeys(List<PublicKeyRequest> keys, FutureCallback<ResponseGetPublicKeys> callback) {
        return this.api.rpc(new RequestGetPublicKeys(keys), callback);
    }

    public Future<ResponseGetPublicKeys> getPublicKeys(List<PublicKeyRequest> keys, long requestTimeout, FutureCallback<ResponseGetPublicKeys> callback) {
        return this.api.rpc(new RequestGetPublicKeys(keys), requestTimeout, callback);
    }

    public ResponseGetPublicKeys getPublicKeysSync (List<PublicKeyRequest> keys) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetPublicKeys(keys));
    }

    public ResponseGetPublicKeys getPublicKeysSync (List<PublicKeyRequest> keys, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetPublicKeys(keys), requestTimeout);
    }

    public Future<ResponseVoid> typing(OutPeer peer, int typingType) {
        return this.api.rpc(new RequestTyping(peer, typingType));
    }

    public Future<ResponseVoid> typing(OutPeer peer, int typingType, long requestTimeout) {
        return this.api.rpc(new RequestTyping(peer, typingType), requestTimeout);
    }

    public Future<ResponseVoid> typing(OutPeer peer, int typingType, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestTyping(peer, typingType), callback);
    }

    public Future<ResponseVoid> typing(OutPeer peer, int typingType, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestTyping(peer, typingType), requestTimeout, callback);
    }

    public ResponseVoid typingSync (OutPeer peer, int typingType) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestTyping(peer, typingType));
    }

    public ResponseVoid typingSync (OutPeer peer, int typingType, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestTyping(peer, typingType), requestTimeout);
    }

    public Future<ResponseVoid> setOnline(boolean isOnline, long timeout) {
        return this.api.rpc(new RequestSetOnline(isOnline, timeout));
    }

    public Future<ResponseVoid> setOnline(boolean isOnline, long timeout, long requestTimeout) {
        return this.api.rpc(new RequestSetOnline(isOnline, timeout), requestTimeout);
    }

    public Future<ResponseVoid> setOnline(boolean isOnline, long timeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSetOnline(isOnline, timeout), callback);
    }

    public Future<ResponseVoid> setOnline(boolean isOnline, long timeout, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSetOnline(isOnline, timeout), requestTimeout, callback);
    }

    public ResponseVoid setOnlineSync (boolean isOnline, long timeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSetOnline(isOnline, timeout));
    }

    public ResponseVoid setOnlineSync (boolean isOnline, long timeout, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSetOnline(isOnline, timeout), requestTimeout);
    }

    public Future<ResponseGetFile> getFile(FileLocation fileLocation, int offset, int limit) {
        return this.api.rpc(new RequestGetFile(fileLocation, offset, limit));
    }

    public Future<ResponseGetFile> getFile(FileLocation fileLocation, int offset, int limit, long requestTimeout) {
        return this.api.rpc(new RequestGetFile(fileLocation, offset, limit), requestTimeout);
    }

    public Future<ResponseGetFile> getFile(FileLocation fileLocation, int offset, int limit, FutureCallback<ResponseGetFile> callback) {
        return this.api.rpc(new RequestGetFile(fileLocation, offset, limit), callback);
    }

    public Future<ResponseGetFile> getFile(FileLocation fileLocation, int offset, int limit, long requestTimeout, FutureCallback<ResponseGetFile> callback) {
        return this.api.rpc(new RequestGetFile(fileLocation, offset, limit), requestTimeout, callback);
    }

    public ResponseGetFile getFileSync (FileLocation fileLocation, int offset, int limit) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetFile(fileLocation, offset, limit));
    }

    public ResponseGetFile getFileSync (FileLocation fileLocation, int offset, int limit, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetFile(fileLocation, offset, limit), requestTimeout);
    }

    public Future<ResponseStartUpload> startUpload() {
        return this.api.rpc(new RequestStartUpload());
    }

    public Future<ResponseStartUpload> startUpload(long requestTimeout) {
        return this.api.rpc(new RequestStartUpload(), requestTimeout);
    }

    public Future<ResponseStartUpload> startUpload(FutureCallback<ResponseStartUpload> callback) {
        return this.api.rpc(new RequestStartUpload(), callback);
    }

    public Future<ResponseStartUpload> startUpload(long requestTimeout, FutureCallback<ResponseStartUpload> callback) {
        return this.api.rpc(new RequestStartUpload(), requestTimeout, callback);
    }

    public ResponseStartUpload startUploadSync () throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestStartUpload());
    }

    public ResponseStartUpload startUploadSync (long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestStartUpload(), requestTimeout);
    }

    public Future<ResponseVoid> uploadPart(UploadConfig config, int blockIndex, byte[] payload) {
        return this.api.rpc(new RequestUploadPart(config, blockIndex, payload));
    }

    public Future<ResponseVoid> uploadPart(UploadConfig config, int blockIndex, byte[] payload, long requestTimeout) {
        return this.api.rpc(new RequestUploadPart(config, blockIndex, payload), requestTimeout);
    }

    public Future<ResponseVoid> uploadPart(UploadConfig config, int blockIndex, byte[] payload, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestUploadPart(config, blockIndex, payload), callback);
    }

    public Future<ResponseVoid> uploadPart(UploadConfig config, int blockIndex, byte[] payload, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestUploadPart(config, blockIndex, payload), requestTimeout, callback);
    }

    public ResponseVoid uploadPartSync (UploadConfig config, int blockIndex, byte[] payload) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestUploadPart(config, blockIndex, payload));
    }

    public ResponseVoid uploadPartSync (UploadConfig config, int blockIndex, byte[] payload, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestUploadPart(config, blockIndex, payload), requestTimeout);
    }

    public Future<ResponseCompleteUpload> completeUpload(UploadConfig config, int blocksCount, long crc32) {
        return this.api.rpc(new RequestCompleteUpload(config, blocksCount, crc32));
    }

    public Future<ResponseCompleteUpload> completeUpload(UploadConfig config, int blocksCount, long crc32, long requestTimeout) {
        return this.api.rpc(new RequestCompleteUpload(config, blocksCount, crc32), requestTimeout);
    }

    public Future<ResponseCompleteUpload> completeUpload(UploadConfig config, int blocksCount, long crc32, FutureCallback<ResponseCompleteUpload> callback) {
        return this.api.rpc(new RequestCompleteUpload(config, blocksCount, crc32), callback);
    }

    public Future<ResponseCompleteUpload> completeUpload(UploadConfig config, int blocksCount, long crc32, long requestTimeout, FutureCallback<ResponseCompleteUpload> callback) {
        return this.api.rpc(new RequestCompleteUpload(config, blocksCount, crc32), requestTimeout, callback);
    }

    public ResponseCompleteUpload completeUploadSync (UploadConfig config, int blocksCount, long crc32) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestCompleteUpload(config, blocksCount, crc32));
    }

    public ResponseCompleteUpload completeUploadSync (UploadConfig config, int blocksCount, long crc32, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestCompleteUpload(config, blocksCount, crc32), requestTimeout);
    }

    public Future<ResponseVoid> registerGooglePush(long projectId, String token) {
        return this.api.rpc(new RequestRegisterGooglePush(projectId, token));
    }

    public Future<ResponseVoid> registerGooglePush(long projectId, String token, long requestTimeout) {
        return this.api.rpc(new RequestRegisterGooglePush(projectId, token), requestTimeout);
    }

    public Future<ResponseVoid> registerGooglePush(long projectId, String token, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestRegisterGooglePush(projectId, token), callback);
    }

    public Future<ResponseVoid> registerGooglePush(long projectId, String token, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestRegisterGooglePush(projectId, token), requestTimeout, callback);
    }

    public ResponseVoid registerGooglePushSync (long projectId, String token) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRegisterGooglePush(projectId, token));
    }

    public ResponseVoid registerGooglePushSync (long projectId, String token, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRegisterGooglePush(projectId, token), requestTimeout);
    }

    public Future<ResponseVoid> registerApplePush(int apnsKey, String token) {
        return this.api.rpc(new RequestRegisterApplePush(apnsKey, token));
    }

    public Future<ResponseVoid> registerApplePush(int apnsKey, String token, long requestTimeout) {
        return this.api.rpc(new RequestRegisterApplePush(apnsKey, token), requestTimeout);
    }

    public Future<ResponseVoid> registerApplePush(int apnsKey, String token, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestRegisterApplePush(apnsKey, token), callback);
    }

    public Future<ResponseVoid> registerApplePush(int apnsKey, String token, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestRegisterApplePush(apnsKey, token), requestTimeout, callback);
    }

    public ResponseVoid registerApplePushSync (int apnsKey, String token) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRegisterApplePush(apnsKey, token));
    }

    public ResponseVoid registerApplePushSync (int apnsKey, String token, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestRegisterApplePush(apnsKey, token), requestTimeout);
    }

    public Future<ResponseVoid> unregisterPush() {
        return this.api.rpc(new RequestUnregisterPush());
    }

    public Future<ResponseVoid> unregisterPush(long requestTimeout) {
        return this.api.rpc(new RequestUnregisterPush(), requestTimeout);
    }

    public Future<ResponseVoid> unregisterPush(FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestUnregisterPush(), callback);
    }

    public Future<ResponseVoid> unregisterPush(long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestUnregisterPush(), requestTimeout, callback);
    }

    public ResponseVoid unregisterPushSync () throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestUnregisterPush());
    }

    public ResponseVoid unregisterPushSync (long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestUnregisterPush(), requestTimeout);
    }

    public Future<ResponseSeq> getState() {
        return this.api.rpc(new RequestGetState());
    }

    public Future<ResponseSeq> getState(long requestTimeout) {
        return this.api.rpc(new RequestGetState(), requestTimeout);
    }

    public Future<ResponseSeq> getState(FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestGetState(), callback);
    }

    public Future<ResponseSeq> getState(long requestTimeout, FutureCallback<ResponseSeq> callback) {
        return this.api.rpc(new RequestGetState(), requestTimeout, callback);
    }

    public ResponseSeq getStateSync () throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetState());
    }

    public ResponseSeq getStateSync (long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetState(), requestTimeout);
    }

    public Future<ResponseGetDifference> getDifference(int seq, byte[] state) {
        return this.api.rpc(new RequestGetDifference(seq, state));
    }

    public Future<ResponseGetDifference> getDifference(int seq, byte[] state, long requestTimeout) {
        return this.api.rpc(new RequestGetDifference(seq, state), requestTimeout);
    }

    public Future<ResponseGetDifference> getDifference(int seq, byte[] state, FutureCallback<ResponseGetDifference> callback) {
        return this.api.rpc(new RequestGetDifference(seq, state), callback);
    }

    public Future<ResponseGetDifference> getDifference(int seq, byte[] state, long requestTimeout, FutureCallback<ResponseGetDifference> callback) {
        return this.api.rpc(new RequestGetDifference(seq, state), requestTimeout, callback);
    }

    public ResponseGetDifference getDifferenceSync (int seq, byte[] state) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetDifference(seq, state));
    }

    public ResponseGetDifference getDifferenceSync (int seq, byte[] state, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestGetDifference(seq, state), requestTimeout);
    }

    public Future<ResponseVoid> subscribeToOnline(List<UserOutPeer> users) {
        return this.api.rpc(new RequestSubscribeToOnline(users));
    }

    public Future<ResponseVoid> subscribeToOnline(List<UserOutPeer> users, long requestTimeout) {
        return this.api.rpc(new RequestSubscribeToOnline(users), requestTimeout);
    }

    public Future<ResponseVoid> subscribeToOnline(List<UserOutPeer> users, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSubscribeToOnline(users), callback);
    }

    public Future<ResponseVoid> subscribeToOnline(List<UserOutPeer> users, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSubscribeToOnline(users), requestTimeout, callback);
    }

    public ResponseVoid subscribeToOnlineSync (List<UserOutPeer> users) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSubscribeToOnline(users));
    }

    public ResponseVoid subscribeToOnlineSync (List<UserOutPeer> users, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSubscribeToOnline(users), requestTimeout);
    }

    public Future<ResponseVoid> subscribeFromOnline(List<UserOutPeer> users) {
        return this.api.rpc(new RequestSubscribeFromOnline(users));
    }

    public Future<ResponseVoid> subscribeFromOnline(List<UserOutPeer> users, long requestTimeout) {
        return this.api.rpc(new RequestSubscribeFromOnline(users), requestTimeout);
    }

    public Future<ResponseVoid> subscribeFromOnline(List<UserOutPeer> users, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSubscribeFromOnline(users), callback);
    }

    public Future<ResponseVoid> subscribeFromOnline(List<UserOutPeer> users, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSubscribeFromOnline(users), requestTimeout, callback);
    }

    public ResponseVoid subscribeFromOnlineSync (List<UserOutPeer> users) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSubscribeFromOnline(users));
    }

    public ResponseVoid subscribeFromOnlineSync (List<UserOutPeer> users, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSubscribeFromOnline(users), requestTimeout);
    }

    public Future<ResponseVoid> subscribeToGroupOnline(List<GroupOutPeer> groups) {
        return this.api.rpc(new RequestSubscribeToGroupOnline(groups));
    }

    public Future<ResponseVoid> subscribeToGroupOnline(List<GroupOutPeer> groups, long requestTimeout) {
        return this.api.rpc(new RequestSubscribeToGroupOnline(groups), requestTimeout);
    }

    public Future<ResponseVoid> subscribeToGroupOnline(List<GroupOutPeer> groups, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSubscribeToGroupOnline(groups), callback);
    }

    public Future<ResponseVoid> subscribeToGroupOnline(List<GroupOutPeer> groups, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSubscribeToGroupOnline(groups), requestTimeout, callback);
    }

    public ResponseVoid subscribeToGroupOnlineSync (List<GroupOutPeer> groups) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSubscribeToGroupOnline(groups));
    }

    public ResponseVoid subscribeToGroupOnlineSync (List<GroupOutPeer> groups, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSubscribeToGroupOnline(groups), requestTimeout);
    }

    public Future<ResponseVoid> subscribeFromGroupOnline(List<GroupOutPeer> groups) {
        return this.api.rpc(new RequestSubscribeFromGroupOnline(groups));
    }

    public Future<ResponseVoid> subscribeFromGroupOnline(List<GroupOutPeer> groups, long requestTimeout) {
        return this.api.rpc(new RequestSubscribeFromGroupOnline(groups), requestTimeout);
    }

    public Future<ResponseVoid> subscribeFromGroupOnline(List<GroupOutPeer> groups, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSubscribeFromGroupOnline(groups), callback);
    }

    public Future<ResponseVoid> subscribeFromGroupOnline(List<GroupOutPeer> groups, long requestTimeout, FutureCallback<ResponseVoid> callback) {
        return this.api.rpc(new RequestSubscribeFromGroupOnline(groups), requestTimeout, callback);
    }

    public ResponseVoid subscribeFromGroupOnlineSync (List<GroupOutPeer> groups) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSubscribeFromGroupOnline(groups));
    }

    public ResponseVoid subscribeFromGroupOnlineSync (List<GroupOutPeer> groups, long requestTimeout) throws TimeoutException, ApiRequestException {
        return this.api.rpcSync(new RequestSubscribeFromGroupOnline(groups), requestTimeout);
    }

}
