package org.jf.smalidea.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.IdAndToDoScannerBasedOnFilterLexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer;
import org.jf.smalidea.SmaliLexer;
import org.jf.smalidea.lexer.SmaliFilterLexer;

public class SmaliTodoIndexer extends LexerBasedTodoIndexer implements IdAndToDoScannerBasedOnFilterLexer {
    @Override
    public Lexer createLexer(OccurrenceConsumer consumer) {
        return new SmaliFilterLexer(new SmaliLexer(), consumer);
    }
}
