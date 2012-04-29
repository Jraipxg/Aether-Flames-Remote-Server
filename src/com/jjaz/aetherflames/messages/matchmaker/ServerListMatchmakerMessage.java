package com.jjaz.aetherflames.messages.matchmaker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;
import org.anddev.andengine.util.Debug;

import com.jjaz.aetherflames.AetherFlamesConstants;
import com.jjaz.aetherflames.GameServer;

public class ServerListMatchmakerMessage extends ServerMessage implements AetherFlamesConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public HashMap<String, GameServer> mServerList;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Deprecated
	public ServerListMatchmakerMessage() {

	}

	public ServerListMatchmakerMessage(final HashMap<String, GameServer> pServerList) {
		this.mServerList = pServerList;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setServerList(HashMap<String, GameServer> pServerList) {
		this.mServerList = pServerList;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_MATCHMAKER_SERVER_LIST;
	}

	@Override
	protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {
		ObjectInputStream reader = new ObjectInputStream(pDataInputStream);
		try {
			this.mServerList = (HashMap<String, GameServer>) reader.readObject();
		} catch (ClassNotFoundException e) {
			Debug.e(e);
		}
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		ObjectOutputStream writer = new ObjectOutputStream(pDataOutputStream);
		writer.writeObject(this.mServerList);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
