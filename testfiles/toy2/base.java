class Test{
    @Test
    public void testEsIndexBolt()
            throws Exception {
        EsConfig esConfig = new EsConfig();
        esConfig.setClusterName("test-cluster");
        esConfig.setNodes(new String[]{"127.0.0.1:9300"});

        bolt = new EsIndexBolt(esConfig);
        bolt.prepare(config, null, collector);

        String source = "{\"user\":\"user1\"}";
        String index = "index1";
        String type = "type1";
        String id = "docId";
        Tuple tuple = EsTestUtil.generateTestTuple(source, index, type, id);

        bolt.execute(tuple);

        verify(collector).ack(tuple);

        node.client().admin().indices().prepareRefresh(index).execute().actionGet();
        CountResponse resp = node.client().prepareCount(index)
                .setQuery(new TermQueryBuilder("_type", type))
                .execute().actionGet();

        Assert.assertEquals(1, resp.getCount());

        bolt.cleanup();
    }
}