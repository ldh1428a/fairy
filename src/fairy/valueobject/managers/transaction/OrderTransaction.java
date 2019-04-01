package fairy.valueobject.managers.transaction;

public class OrderTransaction extends Transaction{

	
	private String buyerAddress = null;
	private short hydrogenType = 0;
	private long maxTime = 0;
	
	public OrderTransaction() {
		super(TransactionType.ORDER);
	}
	
	@Override
	protected byte[] getDatasBytes() {
		
		return null;
	}
	
}