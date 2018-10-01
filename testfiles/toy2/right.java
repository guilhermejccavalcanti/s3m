class Test{
    @Test
    public void testEsIndexBolt()
            throws Exception {
        String index = "index1";
        String type = "type1";

        Tuple tuple = createTestTuple(index, type);

        bolt.execute(tuple);

        verify(outputCollector).ack(tuple);

        node.client().admin().indices().prepareRefresh(index).execute().actionGet();
        CountResponse resp = node.client().prepareCount(index)
                .setQuery(new TermQueryBuilder("_type", type))
                .execute().actionGet();

        Assert.assertEquals(1, resp.getCount());
    }
}