package im.actor.torlib.crypto;

public interface TorKeyAgreement {
	byte[] createOnionSkin();
	boolean deriveKeysFromHandshakeResponse(byte[] handshakeResponse, byte[] keyMaterialOut, byte[] verifyHashOut);
}
