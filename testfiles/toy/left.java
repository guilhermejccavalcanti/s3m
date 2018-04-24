public class Test{
    @Override
    public BroadcastFilter.BroadcastAction filter(Object originalMessage, Object o) {
        if (!(receivedMessages.remove(originalMessage.toString()))) {
            jedisPublisher.publish(bc.getID(), originalMessage.toString());
        }
        return new BroadcastFilter.BroadcastAction(BroadcastAction.ACTION.CONTINUE, o);
    }
}