public class Test{
	private static int getInitialValue()
    {
        String newvalue = System.getProperty("cassandra.fd_initial_value_ms");
        if (newvalue != null)
        {
            logger.info("Overriding FD INITIAL_VALUE to {}ms", newvalue);
            return Integer.parseInt(newvalue);
        }
        else
            return Gossiper.intervalInMillis * 2;
    }
}