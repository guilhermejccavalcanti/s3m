import pcktright.*;

public enum StatusQDM {
	VALIDO("Válido", "Validar QDM")      , //
	ESTUDO("Estudo", "Gerar QDM"), //
	DESATUALIZADO("Desatualizado", ""), //
	INEXISTENTE("Inexistente", "Excluir QDM"), //
	REVOGADO("Revogado", "Revogar QDM"), //
	EM_HOMOLOGACAO("Em Homologação", "Enviar para Homologação"), //
	HOMOLOGADO("Homologado", "Homologar QDM"); //
}