/*
 * Copyright 2015, Google Inc.
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

package org.jf.smalidea.findUsages;

import com.google.common.collect.Maps;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UsageTypeTest extends PsiTestCase {
    // e.g. <ref:1>, <ref:1234>, etc.
    private static final Pattern REF_PATTERN = Pattern.compile("(<ref:([0-9]+)>)");

    @NotNull
    private final UsageTypeProvider usageTypeProvider;

    public UsageTypeTest(@NotNull UsageTypeProvider usageTypeProvider) {
        this.usageTypeProvider = usageTypeProvider;
    }

    protected void doTest(@NotNull String fileName, @NotNull String text, @NotNull Object... expectedUsageTypes)
            throws Exception {
        Assert.assertTrue(expectedUsageTypes.length % 2 == 0);

        Map<Integer, UsageType> expectedUsageTypesMap = Maps.newHashMap();
        for (int i=0; i<expectedUsageTypes.length; i+=2) {
            expectedUsageTypesMap.put((Integer) expectedUsageTypes[i], (UsageType) expectedUsageTypes[i + 1]);
        }

        PsiFile psiFile = createFile(fileName, REF_PATTERN.matcher(text).replaceAll(""));
        Map<Integer, Integer> refIndexMap = getRefIndexes(text);

        for (Map.Entry<Integer, Integer> entry: refIndexMap.entrySet()) {
            int refId = entry.getKey();
            int index = entry.getValue();

            PsiReference reference = psiFile.getFirstChild().findReferenceAt(index);
            Assert.assertNotNull(reference);
            if (reference instanceof PsiMultiReference) {
                // If there are multiple reference parents, the default seems to be the last one,
                // i.e. the highest parent. We actually want the lowest one here.
                reference = ((PsiMultiReference) reference).getReferences()[0];
            }

            UsageType usageType = usageTypeProvider.getUsageType(reference.getElement());
            Assert.assertNotNull(usageType);
            Assert.assertSame(expectedUsageTypesMap.get(refId), usageType);
            expectedUsageTypesMap.remove(refId);
        }
        Assert.assertTrue(expectedUsageTypesMap.isEmpty());
    }

    @NotNull
    private Map<Integer, Integer> getRefIndexes(@NotNull String text) {
        Matcher m = REF_PATTERN.matcher(text);
        int correction = 0;
        Map<Integer, Integer> refIndexes = new HashMap<Integer, Integer>();
        while (m.find()) {
            int refId = Integer.parseInt(m.group(2));
            refIndexes.put(refId, m.start() - correction);
            correction += m.end() - m.start();
        }
        return refIndexes;
    }
}
