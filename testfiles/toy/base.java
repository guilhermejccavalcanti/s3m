class Test {
	public List findByRelation(String colName, Object colValue, Class entityClazz)
	{
		EntityMetadata m = KunderaMetadataManager.getEntityMetadata(entityClazz);
		
		String tableName = m.getTableName();
		
		String aliasName = "_" + tableName;
		
		StringBuilder queryBuilder = new StringBuilder("Select ");
		// queryBuilder.append(aliasName);
		queryBuilder.append("* ");
		queryBuilder.append("From ");
		queryBuilder.append(getFromClause(m.getSchema(), tableName));
		queryBuilder.append(" ");
		// queryBuilder.append(aliasName);
		queryBuilder.append(" Where ");
		queryBuilder.append(colName);
		queryBuilder.append(" = ");
		queryBuilder.append("'");
		queryBuilder.append(colValue);
		queryBuilder.append("'");
		s = getStatelessSession();
		//        s.beginTransaction();

		List results = find(queryBuilder.toString(), m.getRelationNames(), m);
		return populateEnhanceEntities(m, m.getRelationNames(), results);

		// SQLQuery q =
		// s.createSQLQuery(queryBuilder.toString()).addEntity(m.getEntityClazz());
		// // tx.commit();
		// return q.list();
	}
}