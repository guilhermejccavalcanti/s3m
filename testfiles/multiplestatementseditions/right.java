public class Test{
	 public void initRowCache()
    {
        int cachedRowsRead = CacheService.instance.rowCache.loadSaved(this);
        if (cachedRowsRead > 0)
            logger.info(String.format("completed loading (%d ms; %d keys) row cache for %s.%s",
                        System.currentTimeMillis() - start,
                        cachedRowsRead,
                        table.name,
                        columnFamily));
    }
}