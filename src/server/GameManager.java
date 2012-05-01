package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;
import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;
import org.anddev.andengine.extension.multiplayer.protocol.server.IClientMessageHandler;
import org.anddev.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.anddev.andengine.extension.multiplayer.protocol.server.SocketServer.ISocketServerListener;
import org.anddev.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.anddev.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.anddev.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector.ISocketConnectionClientConnectorListener;
import org.anddev.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.anddev.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.anddev.andengine.util.Debug;

import com.jjaz.aetherflames.*;
import com.jjaz.aetherflames.messages.matchmaker.*;
import com.jjaz.aetherflames.messages.phone.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;


public class GameManager implements AetherFlamesConstants{
private static final int SERVER_PORT = 5555;
	
//	private static final short FLAG_MESSAGE_CLIENT_ADD_FACE = 1;
//	private static final short FLAG_MESSAGE_CLIENT_MOVE_FACE = 2;
//	private static final short FLAG_MESSAGE_SERVER_ADD_FACE = 3;
//	private static final short FLAG_MESSAGE_SERVER_MOVE_FACE = 4;
	
	private SocketServer<SocketConnectionClientConnector> mSocketServer;
	private final MessagePool<IMessage> pool = new MessagePool<IMessage>();
	private int doneCount, numPlayers, msgVote;
	private ArrayList<LinkedList<ServerMessage>> frameMessages;
	private int doneList[];
	private HashMap<String, GameServer> myServers;
	
	
	public GameManager()
	{
		initMessagePool();
		doneCount = 0;
		numPlayers = 0;
		msgVote = 0;
		doneList = new int[4];
		frameMessages = new ArrayList<LinkedList<ServerMessage>>();
		myServers = new HashMap<String, GameServer>();
		
		for(int i = 0; i < 8; i++)
		{
			frameMessages.add(new LinkedList<ServerMessage>());
		}
		mSocketServer = new SocketServer<SocketConnectionClientConnector>(SERVER_PORT, new GameClientConnectorListener(), new GameServerStateListener())
		{
			@Override
			protected SocketConnectionClientConnector newClientConnector(final SocketConnection pSocketConnection) throws IOException 
			{
				SocketConnectionClientConnector connector = new SocketConnectionClientConnector(pSocketConnection);

				connector.registerClientMessage(FLAG_MESSAGE_PHONE_CONNECTION_CLOSE, ConnectionClosePhoneMessage.class, new IClientMessageHandler<SocketConnection>() {
					@Override
					public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException {
						pClientConnector.terminate();
					}
				});
				connector.registerClientMessage(FLAG_MESSAGE_PHONE_CONNECTION_ESTABLISH, ConnectionEstablishPhoneMessage.class, new IClientMessageHandler<SocketConnection>() {
					@Override
					public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException {
						final ConnectionEstablishPhoneMessage connectionEstablishPhoneMessage = (ConnectionEstablishPhoneMessage) pClientMessage;
						if(connectionEstablishPhoneMessage.getProtocolVersion() == PROTOCOL_VERSION) {
							final ConnectionEstablishMatchmakerMessage connectionEstablishedServerMessage = (ConnectionEstablishMatchmakerMessage) pool.obtainMessage(FLAG_MESSAGE_MATCHMAKER_CONNECTION_ESTABLISH);
							try {
								pClientConnector.sendServerMessage(connectionEstablishedServerMessage);
							} catch (IOException e) {
								Debug.e(e);
							}
							pool.recycleMessage(connectionEstablishedServerMessage);
						} else {
							final ConnectionRejectedProtocolMismatchMatchmakerMessage connectionRejectedProtocolMismatchMatchmakerMessage = (ConnectionRejectedProtocolMismatchMatchmakerMessage) pool.obtainMessage(FLAG_MESSAGE_MATCHMAKER_CONNECTION_REJECTED_PROTOCOL_MISMATCH);
							connectionRejectedProtocolMismatchMatchmakerMessage.setProtocolVersion(PROTOCOL_VERSION);
							try {
								pClientConnector.sendServerMessage(connectionRejectedProtocolMismatchMatchmakerMessage);
							} catch (IOException e) {
								Debug.e(e);
							}
							pool.recycleMessage(connectionRejectedProtocolMismatchMatchmakerMessage);
						}
					}
				});		
				
				
				connector.registerClientMessage(FLAG_MESSAGE_PHONE_START_SERVER, StartServerPhoneMessage.class, new IClientMessageHandler<SocketConnection>() 
				{
					@Override
					public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException 
					{
						final StartServerPhoneMessage startServerPhoneMessage = (StartServerPhoneMessage)pClientMessage;
						//final HelloServerMessage startServerServerMessage = (helloServerMessage) pool.obtainMessage(FLAG_MESSAGE_MM_CLIENT_HELLO);
						//frameMessages.get(newBulletClientMessage.mShipID).add(newBulletServerMessage);
						//sendServerMessage(helloServerMessage);
						
						myServers.put(startServerPhoneMessage.mServer.getMyIP(), startServerPhoneMessage.mServer);
						System.out.println("Added server: " + startServerPhoneMessage.mServer.toString());
					}
				});
				connector.registerClientMessage(FLAG_MESSAGE_PHONE_GET_FIRST_SERVER, GetFirstServerPhoneMessage.class, new IClientMessageHandler<SocketConnection>() 
				{
					@Override
					public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException 
					{
						final GetFirstServerPhoneMessage getFirstServerPhoneMessage = (GetFirstServerPhoneMessage)pClientMessage;
						//final HelloServerMessage startServerServerMessage = (helloServerMessage) pool.obtainMessage(FLAG_MESSAGE_MM_CLIENT_HELLO);
						//frameMessages.get(newBulletClientMessage.mShipID).add(newBulletServerMessage);
						//sendServerMessage(helloServerMessage);
						short desiredPlayers = getFirstServerPhoneMessage.GetDesiredPlayers();
						System.out.println("Got single server request for " + desiredPlayers + " players.");
						for(GameServer gs: myServers.values())
						{
							if((gs.getNumPlayers() < gs.getMaxPlayers()) && (gs.getMaxPlayers() == desiredPlayers))
							{
								final FreeServerMatchmakerMessage freeServerMatchmakerMessage = (FreeServerMatchmakerMessage) pool.obtainMessage(FLAG_MESSAGE_MATCHMAKER_FREE_SERVER);
								freeServerMatchmakerMessage.setServer(gs);
								pClientConnector.sendServerMessage(freeServerMatchmakerMessage);
								pool.recycleMessage(freeServerMatchmakerMessage);
								System.out.println("Sent back " + gs.toString());
								break;
							}
						}
					}
				});
				
				connector.registerClientMessage(FLAG_MESSAGE_PHONE_GET_SERVER_LIST, GetServerListPhoneMessage.class, new IClientMessageHandler<SocketConnection>() 
				{
					@Override
					public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException 
					{
						final ServerListMatchmakerMessage serverListMatchmakerMessage = (ServerListMatchmakerMessage) pool.obtainMessage(FLAG_MESSAGE_MATCHMAKER_SERVER_LIST);
						System.out.println("Got server list request.");
						serverListMatchmakerMessage.setServerList(myServers);
						pClientConnector.sendServerMessage(serverListMatchmakerMessage);
						pool.recycleMessage(serverListMatchmakerMessage);
					}
				});
				
				connector.registerClientMessage(FLAG_MESSAGE_PHONE_CURRENT_PLAYER_COUNT, CurrentPlayerCountPhoneMessage.class, new IClientMessageHandler<SocketConnection>() 
				{
					@Override
					public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException 
					{
						final CurrentPlayerCountPhoneMessage currentPlayerCountPhoneMessage = (CurrentPlayerCountPhoneMessage)pClientMessage;
						myServers.get(currentPlayerCountPhoneMessage.mServer.getMyIP()).setNumPlayers(currentPlayerCountPhoneMessage.mServer.getNumPlayers());
						System.out.println("Set player count on this server: " + myServers.get(currentPlayerCountPhoneMessage.mServer.getMyIP()));
					}
				});
				
				connector.registerClientMessage(FLAG_MESSAGE_PHONE_GAME_START, GameStartPhoneMessage.class, new IClientMessageHandler<SocketConnection>()
				{
					@Override
					public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException 
					{
						final GameStartPhoneMessage gameStartPhoneMessage = (GameStartPhoneMessage)pClientMessage;
						System.out.println("Got game start message for server " + myServers.get(gameStartPhoneMessage.mServer.getMyIP()));
						myServers.remove(gameStartPhoneMessage.mServer.getMyIP()); // remove this from the list
					}
				});
				
				
				return connector;
			}
		};
		mSocketServer.start();
	}
	
	private void initMessagePool() {
		this.pool.registerMessage(FLAG_MESSAGE_MATCHMAKER_CONNECTION_ESTABLISH, ConnectionEstablishMatchmakerMessage.class);
		this.pool.registerMessage(FLAG_MESSAGE_MATCHMAKER_CONNECTION_CLOSE, ConnectionCloseMatchmakerMessage.class);
		this.pool.registerMessage(FLAG_MESSAGE_MATCHMAKER_CONNECTION_REJECTED_PROTOCOL_MISMATCH, ConnectionRejectedProtocolMismatchMatchmakerMessage.class);
		this.pool.registerMessage(FLAG_MESSAGE_MATCHMAKER_FREE_SERVER, FreeServerMatchmakerMessage.class);
		this.pool.registerMessage(FLAG_MESSAGE_MATCHMAKER_SERVER_LIST, ServerListMatchmakerMessage.class);
	}
	

	
	class GameServerStateListener implements ISocketServerListener<SocketConnectionClientConnector> 
	{
		@Override
		public void onStarted(final SocketServer<SocketConnectionClientConnector> pSocketServer) 
		{
			try {
			    InetAddress addr = InetAddress.getLocalHost();
			   
				System.out.println("SERVER: Started. IP: " + addr.toString());
			} catch (UnknownHostException e) {
				Debug.e(e);
			}
		}

		@Override
		public void onTerminated(final SocketServer<SocketConnectionClientConnector> pSocketServer) 
		{
			System.out.println("SERVER: Terminated.");
		}

		@Override
		public void onException(final SocketServer<SocketConnectionClientConnector> pSocketServer, final Throwable pThrowable) 
		{
			System.out.println("SERVER: Exception: " + pThrowable);
		}
	}
	
	class GameClientConnectorListener implements ISocketConnectionClientConnectorListener 
	{
		@Override
		public void onStarted(final ClientConnector<SocketConnection> pConnector) 
		{
			System.out.println("SERVER: Client connected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
			numPlayers += 1;
		}

		@Override
		public void onTerminated(final ClientConnector<SocketConnection> pConnector) 
		{
			System.out.println("SERVER: Client disconnected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
			numPlayers -= 1;
		}
	}
	
	public static void main(String[] args) 
	{
		GameManager gm = new GameManager();
	}
	

}
