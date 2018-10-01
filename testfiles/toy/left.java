class Test{
    public void testCRUD()
    {
        try
        {
            tryOperation(ALL_PUs_UNDER_TEST);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
        }
    }
}