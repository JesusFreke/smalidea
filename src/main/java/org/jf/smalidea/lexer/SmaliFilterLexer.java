/*
 * Copyright 2020, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
