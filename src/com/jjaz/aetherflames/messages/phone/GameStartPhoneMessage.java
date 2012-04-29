package com.jjaz.aetherflames.messages.phone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;
import org.anddev.andengine.util.Debug;

import com.jjaz.aetherflames.AetherFlamesConstants;
import com.jjaz.aetherflames.GameServer;

public class GameStartPhoneMessage extends ClientMessage implements AetherFlamesConstants {

	public GameServer mServer;
	
	/**
	 * C'tor
	 */
	public GameStartPhoneMessage() {
		// intentionally empty
	}
	
	/**
	 * C'tor
	 * 
	 * @param pServer Server that is starting game.
	 */
	public GameStartPhoneMessage(final GameServer pServer) {
		this.mServer = pServer;
	}
	
	/**
	 * Setter.
	 * 
	 * @param start True to start game.
	 */	
	public void setServer(final GameServer pServer) {
		mServer = pServer;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_PHONE_GAME_START;
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
	
}
