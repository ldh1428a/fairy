package fairy.core.net.communicator;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import fairy.core.managers.key.KeyManager;
import fairy.core.managers.ledger.LedgerManager;
import fairy.core.managers.transaction.TransactionManager;
import fairy.core.utils.Debugger;
import fairy.valueobject.managers.block.Block;
import fairy.valueobject.managers.key.WalletKey;
import fairy.valueobject.managers.transaction.Transaction;

public class Node extends Thread {
	private Socket nodeSock = null;
	
	private String version = null;
	
	private InputStream nodeInputStream = null;
	private OutputStream nodeOutputStream = null;
	
	private ObjectInputStream nodeObjectInputStream = null;
	private ObjectOutputStream nodeObjectOutputStream = null;
	
	private boolean isDead = false;
	
	public Node(Socket nodeSock) {
		try {
			this.nodeSock = nodeSock;
			
			this.nodeInputStream = nodeSock.getInputStream();
			this.nodeOutputStream = nodeSock.getOutputStream();
			this.nodeObjectInputStream = new ObjectInputStream(nodeInputStream);
			this.nodeObjectOutputStream= new ObjectOutputStream(nodeOutputStream);
			
			this.start();
		}catch(Exception e) {
			Debugger.Log(this, e);
		}
	}
	
	@Override
	public void run()
	{
		Debugger.Log(this, "node(" + nodeSock.getInetAddress().getHostAddress() +":10080) is connected !"); 

		while(!isDead)
		{
			try {
				if(nodeInputStream.available() > 0)
				{
					
					Object object = nodeObjectInputStream.readObject();
					
					if(object.getClass().getName().contains("transaction"))
					{
						Transaction tx = (Transaction)object;
						
						if(tx.getPublicKey() == null) {
							WalletKey key = KeyManager.getInstance().Get();
							tx.setPublicKey(key.getPair().getPublic());
							tx.setSignature(TransactionManager.getInstance().Sign(tx.getBytes(), key.getPair().getPrivate()));
						}
						
						if(TransactionManager.getInstance().Push(tx, tx.getPublicKey())) {	
							System.out.println("VALID TRANSACTION RECV:" + tx.toString());
						}else {
							System.out.println("INVALID TRANSACTION RECV:" + tx.toString());
						}
					}
					else
					{
						Block block = (Block)object;
						
						if(LedgerManager.getInstance().generateBlock(block))
						{
							Debugger.Log(this, "BLOCK RECV: " + block.toString());
						}
					}
				}
			} catch (Exception e) {
				Debugger.Log(this, e);
				
				try {
					this.nodeInputStream.close();
					this.nodeOutputStream.close();
					this.nodeSock.close();
					isDead = true;	
				}catch(Exception socketErr) {
					Debugger.Log(this, socketErr);
				}
			}
		}
	}
	
	public boolean sendTransaction(Transaction tx)
	{
		try {
			nodeObjectOutputStream.write(tx.getBytes());
			return true;
		} catch (Exception e) {
			Debugger.Log(this, e);
			return false;
		}
	}
	
	public String getNodeIP()
	{
		return nodeSock.getInetAddress().getHostAddress(); 
	}
	
	public int getNodePort()
	{
		return nodeSock.getPort();
	}
}
