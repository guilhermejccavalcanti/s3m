public class Test{
	private static int getInitialValue()
    {
        String newvalue = System.getProperty("cassandra.fd_initial_value_ms");
        if (newvalue == null)
        {
            return Gossiper.intervalInMillis * 30;
        }
        else
        {
            logger.info("Overriding FD INITIAL_VALUE to {}ms", newvalue);
            return Integer.parseInt(newvalue);
        }
    }
}