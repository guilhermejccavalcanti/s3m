package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.util.MergeContext;

/**
 * Interface responsbile for dealing with language specific conflicts that
 * just superimposition of trees is not able to detect/resolve.
 * @author Guilherme
 */
public interface ConflictHandler {
	void handle(MergeContext context) throws TextualMergeException;
}
