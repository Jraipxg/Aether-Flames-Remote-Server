package com.jjaz.aetherflames;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GameServer implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 17680176284646976L; // (j * j * a * z)^2
	private String myIP;
	private String myName;
	private int numPlayers;
	private int maxPlayers;
	private boolean gameActive;
	
	public GameServer(String ip, String name, int max)
	{
		myIP = ip;
		myName = name;
		maxPlayers = max;
		numPlayers = 1;
		gameActive = false;
	}
	
	public void setMyIP(String myIP) {
		this.myIP = myIP;
	}
	public String getMyIP() {
		return myIP;
	}
	public void setMyName(String myName) {
		this.myName = myName;
	}
	public String getMyName() {
		return myName;
	}
	public void addPlayer() {
		this.numPlayers++;
	}
	public void removePlayer() {
		this.numPlayers--;
	}
	public void setNumPlayers(int numPlayers)
	{
		this.numPlayers = numPlayers;
	}
	public int getNumPlayers() {
		return numPlayers;
	}
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public void setGameActive(boolean gameActive) {
		this.gameActive = gameActive;
	}
	public boolean isGameActive() {
		return gameActive;
	}
	
	/**
	 * De-serialize using default reader.
	 */
	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
		//always perform the default de-serialization first
		aInputStream.defaultReadObject();
	
		// TODO: Perform validation here
	}
	
    /**
    * Serialize using default writer.
    */
    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
    	//perform the default serialization for all non-transient, non-static fields
    	aOutputStream.defaultWriteObject();
    }
	
	public String toString()
	{
		return myName + "|" + myIP + "|" + numPlayers + "|" + maxPlayers + "|" + gameActive;
	}
	
}