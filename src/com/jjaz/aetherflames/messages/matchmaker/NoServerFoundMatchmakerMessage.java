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

public class NoServerFoundMatchmakerMessage extends ServerMessage implements AetherFlamesConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public NoServerFoundMatchmakerMessage() {
		// intentionally empty
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_MATCHMAKER_NO_SERVER_FOUND;
	}

	@Override
	protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {

	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
