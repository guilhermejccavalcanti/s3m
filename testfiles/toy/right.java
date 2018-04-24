public class Test{
    @Override
	public BroadcastFilter.BroadcastAction filter(Object originalMessage, Object o) {
	    String contents = originalMessage.toString();
	
	    if (!(receivedMessages.remove(contents))) {
	        jedisPublisher.publish(bc.getID(), contents);
	        return new BroadcastFilter.BroadcastAction(BroadcastAction.ACTION.CONTINUE, o);
	    }
	    return new BroadcastFilter.BroadcastAction(BroadcastAction.ACTION.ABORT, o);
	}
}