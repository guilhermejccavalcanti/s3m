package com.pa.entity;

public enum OrientationType {
	ORIENTACAO_EM_ANDAMENTO_DE_MESTRADO("ORIENTACAO EM ANDAMENTO DE MESTRADO"), ORIENTACAO_EM_ANDAMENTO_DE_DOUTORADO(
			"ORIENTACAO EM ANDAMENTO DE DOUTORADO"), ORIENTACOES_CONCLUIDAS_PARA_MESTRADO(
					"ORIENTACOES CONCLUIDAS PARA MESTRADO"), ORIENTACOES_CONCLUIDAS_PARA_DOUTORADO(
						"ORIENTACOES CONCLUIDAS PARA DOUTORADO"), ORIENTACAO_INICIACAO_CIENTIFICA("INICIACAO CIENTIFICA");

	private String name;

	private OrientationType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
