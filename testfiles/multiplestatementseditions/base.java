public class Test{
    public void initRowCache()
    {
        int cachedRowsRead = 0;
        for (DecoratedKey key : rowCache.readSaved(table.name, columnFamily, partitioner))
        {
            ColumnFamily data = getTopLevelColumns(QueryFilter.getIdentityFilter(key, new QueryPath(columnFamily)),
                                                   Integer.MIN_VALUE,
                                                   true);
            CacheService.instance.rowCache.put(new RowCacheKey(metadata.cfId, key), data);
            cachedRowsRead++;
        }

        if (cachedRowsRead > 0)
            logger.info(String.format("completed loading (%d ms; %d keys) row cache for %s.%s",
                        System.currentTimeMillis() - start,
                        cachedRowsRead,
                        table.name,
                        columnFamily));
    }
}