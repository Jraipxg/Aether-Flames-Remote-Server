package com.jjaz.aetherflames.messages.matchmaker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;
import org.anddev.andengine.util.Debug;

import com.jjaz.aetherflames.AetherFlamesConstants;
import com.jjaz.aetherflames.GameServer;

public class FreeServerMatchmakerMessage extends ServerMessage implements AetherFlamesConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public GameServer mServer;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public FreeServerMatchmakerMessage() {
		// intentionally empty
	}
	
	public FreeServerMatchmakerMessage(final GameServer pServer)
	{
		mServer = pServer;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public void setServer(final GameServer pServer) {
		mServer = pServer;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_MATCHMAKER_FREE_SERVER;
	}

	@Override
	protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {
		ObjectInputStream reader = new ObjectInputStream(pDataInputStream);
		try {
			this.mServer = (GameServer) reader.readObject();
		} catch (ClassNotFoundException e) {
			Debug.e(e);
		}
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		ObjectOutputStream writer = new ObjectOutputStream(pDataOutputStream);
		writer.writeObject(this.mServer);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
