public class Test{
	protected void push(Entry entry) {
		if (destroyed.get()) {
		    return;
		}
		recentActivity.set(true);
		int a;
		Object finalMsg = translate(entry.message);
		entry.originalMessage = (entry.originalMessage != entry.message ? translate(entry.originalMessage) : finalMsg);
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