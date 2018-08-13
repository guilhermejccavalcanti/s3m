class Test {
	public List findByRelation(String colName, Object colValue, Class entityClazz)
	{
		EntityMetadata m = KunderaMetadataManager.getEntityMetadata(entityClazz);
		
		String tableName = m.getTableName();

		StringBuilder queryBuilder = new StringBuilder("Select ");

		queryBuilder.append("* ");
		queryBuilder.append("From ");
		queryBuilder.append(getFromClause(m.getSchema(), tableName));
		queryBuilder.append(" ");

		queryBuilder.append(" Where ");
		queryBuilder.append(colName);
		queryBuilder.append(" = ");
		queryBuilder.append("'");
		queryBuilder.append(colValue);
		queryBuilder.append("'");
		s = getStatelessSession();

		List results = find(queryBuilder.toString(), m.getRelationNames(), m);
		return populateEnhanceEntities(m, m.getRelationNames(), results);
	}
}