public class Test{
	protected void push(Entry entry) {
		if (destroyed.get()) {
		    return;
		}
		recentActivity.set(true);
		int a;
		Object finalMsg = translate(entry.message);
		if (finalMsg == null) {
		    logger.trace("Broascast message was null {}", finalMsg);
		    return;
		}
		Object prevM = entry.originalMessage;
		entry.originalMessage = (entry.originalMessage != entry.message ? translate(entry.originalMessage) : finalMsg);
		if (entry.originalMessage == null) {
		logger.trace("Broascast message was null {}", prevM);
		    return;
		}
		entry.message = finalMsg;
		if (resources.isEmpty()) {
			int a;
		}
		try {
			int a;
		} catch (InterruptedException ex) {
		    logger.debug(ex.getMessage(), ex);
		}
	}
}