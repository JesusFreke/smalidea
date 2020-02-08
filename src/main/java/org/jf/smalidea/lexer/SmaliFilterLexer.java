package org.jf.smalidea.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.cache.impl.BaseFilterLexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jf.smalidea.SmaliTokens;

public class SmaliFilterLexer extends BaseFilterLexer {

    private static final TokenSet COMMENT_TOKENS = TokenSet.create(SmaliTokens.LINE_COMMENT);

    private static final TokenSet STRING_TOKENS = TokenSet.create(SmaliTokens.STRING_LITERAL);

    private static final TokenSet IN_CODE_TOKENS = TokenSet.create(
            SmaliTokens.BOOL_LITERAL,
            SmaliTokens.BYTE_LITERAL,
            SmaliTokens.CHAR_LITERAL,
            SmaliTokens.DOUBLE_LITERAL,
            SmaliTokens.DOUBLE_LITERAL_OR_ID,
            SmaliTokens.FLOAT_LITERAL,
            SmaliTokens.FLOAT_LITERAL_OR_ID,
            SmaliTokens.LONG_LITERAL,
            SmaliTokens.NEGATIVE_INTEGER_LITERAL,
            SmaliTokens.NULL_LITERAL,
            SmaliTokens.POSITIVE_INTEGER_LITERAL,
            SmaliTokens.SHORT_LITERAL,
            SmaliTokens.SIMPLE_NAME);

    public SmaliFilterLexer(Lexer originalLexer, OccurrenceConsumer occurrenceConsumer) {
        super(originalLexer, occurrenceConsumer);
    }

    @Override public void advance() {
        final IElementType tokenType = myDelegate.getTokenType();

        if (IN_CODE_TOKENS.contains(tokenType)) {
            addOccurrenceInToken(UsageSearchContext.IN_CODE);
        } else if (STRING_TOKENS.contains(tokenType)) {
            scanWordsInToken(UsageSearchContext.IN_STRINGS | UsageSearchContext.IN_FOREIGN_LANGUAGES, false, true);
        } else if (COMMENT_TOKENS.contains(tokenType)) {
            scanWordsInToken(UsageSearchContext.IN_COMMENTS, false, false);
            advanceTodoItemCountsInToken();
        } else if (tokenType != TokenType.WHITE_SPACE) {
            scanWordsInToken(UsageSearchContext.IN_PLAIN_TEXT, false, false);
        }

        myDelegate.advance();
    }
}
