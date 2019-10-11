package br.ufpe.cin.printers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import br.ufpe.cin.mergers.util.IndentationUtils;
import de.ovgu.cide.fstgen.ast.AbstractFSTPrintVisitor;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Visitor to retrieve FSTNodes' contents to be printed.
 * 
 * @author João Victor (jvsfc@cin.ufpe.br)
 */
public abstract class S3MPrettyPrinter extends AbstractFSTPrintVisitor {

    private final StringBuilder result;
    private final Queue<String> tokensCurrentLine;

    private final Pattern conflictPattern = Pattern
            .compile("<<<<<<< MINE(.*)(||||||| BASE)?(.*)=======(.*)>>>>>>> YOURS(.*)", Pattern.DOTALL);

    public S3MPrettyPrinter() {
        this.result = new StringBuilder();
        this.tokensCurrentLine = new LinkedList<String>();
    }

    private boolean printedStatementOnFirstLine = false;

    @Override
    public boolean visit(FSTTerminal terminal) {

        String body = terminal.getBody();
        String prefix = terminal.getSpecialTokenPrefix();

        if (body.isEmpty()) { // if node has been deleted, we trim the prefix to remove unnecessary blank
                              // lines
            printToken(IndentationUtils.removePostIndentationAndLineBreaks(prefix));
        } else if (hasConflict(body)) {
            printToken(IndentationUtils.removePostIndentation(prefix) + body);
        } else if(isFirstLineOfCode(prefix) && isImportOrPackageStatement(terminal)) { 
            // if there are statements on the first line, there is no line break for them.
            if(printedStatementOnFirstLine) {
                printToken('\n' + prefix + body);
            } else {
                printToken(prefix + body);
                printedStatementOnFirstLine = true;
            }
        } else {
            printToken(prefix + body);
        }

        return false;
    }

    private boolean isImportOrPackageStatement(FSTTerminal terminal) {
        return terminal.getType().equals("ImportDeclaration") || terminal.getType().equals("PackageDeclaration");
    }

    private boolean isFirstLineOfCode(String prefix) {
        return !prefix.contains("\n");
    }

    private boolean hasConflict(String content) {
        return conflictPattern.matcher(content).matches();
    }

    protected void printToken(String token) {
        tokensCurrentLine.add(token);
    }

    @Override
    protected void hintNewLine() {
        Iterator<String> it = tokensCurrentLine.iterator();
        while (it.hasNext()) {
            result.append(it.next());
        }

        reset();
    }

    private void reset() {
        tokensCurrentLine.clear();
    }

    public String getResult() {
        hintNewLine();
        return result.toString();
    }

}