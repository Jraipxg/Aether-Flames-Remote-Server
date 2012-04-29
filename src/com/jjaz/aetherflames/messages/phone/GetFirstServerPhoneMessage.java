package com.jjaz.aetherflames.messages.phone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

import com.jjaz.aetherflames.AetherFlamesConstants;

public class GetFirstServerPhoneMessage extends ClientMessage implements AetherFlamesConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private short mDesiredNumPlayers;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public GetFirstServerPhoneMessage() {
		// intentionally empty
	}


	public GetFirstServerPhoneMessage(final short pDesiredNumPlayers) {
		mDesiredNumPlayers = pDesiredNumPlayers;
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void SetDesiredPlayers(final short pDesiredNumPlayers) {
		mDesiredNumPlayers = pDesiredNumPlayers;
	}
	
	public short GetDesiredPlayers() {
		return mDesiredNumPlayers;
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_PHONE_GET_FIRST_SERVER;
	}

	@Override
	protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {
		mDesiredNumPlayers = pDataInputStream.readShort();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeShort(mDesiredNumPlayers);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
