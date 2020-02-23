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

import com.intellij.usages.impl.rules.UsageType;

public class MethodUsageTypeTest extends UsageTypeTest {
    public MethodUsageTypeTest() {
        super(new SmaliUsageTypeProvider());
    }

    public void testMethodUsageTypes() throws Exception {
        doTest("blah.smali", "" +
                        ".class public Lblah;\n" +
                        ".super Ljava/lang/Object;\n" +
                        "\n" +
                        ".annotation runtime Lblah;\n" +
                        "    element = Lblah;->bl<ref:1>ah()V;\n" +
                        ".end annotation\n" +
                        "\n" +
                        ".method public blah()V\n" +
                        "    .registers 2\n" +
                        "\n" +
                        "    invoke-direct {v0}, Lblah;->bl<ref:2>ah()V\n" +
                        "    invoke-direct/empty {v0}, Lblah;->bl<ref:3>ah()V\n" +
                        "    invoke-direct/range {v0}, Lblah;->bl<ref:4>ah()V\n" +
                        "    invoke-interface {v0}, Lblah;->bl<ref:5>ah()V\n" +
                        "    invoke-interface/range {v0}, Lblah;->bl<ref:6>ah()V\n" +
                        "    invoke-object-init/range {v0}, Lblah;->bl<ref:7>ah()V\n" +
                        "    invoke-static {v0}, Lblah;->bl<ref:8>ah()V\n" +
                        "    invoke-static/range {v0}, Lblah;->bl<ref:9>ah()V\n" +
                        "    invoke-super {v0}, Lblah;->bl<ref:10>ah()V\n" +
                        "    invoke-super/range {v0}, Lblah;->bl<ref:11>ah()V\n" +
                        "    invoke-virtual {v0}, Lblah;->bl<ref:12>ah()V\n" +
                        "    invoke-virtual/range {v0}, Lblah;->bl<ref:13>ah()V\n" +
                        "\n" +
                        "    throw-verification-error generic-error, Lblah;->bl<ref:14>ah()V\n" +
                        "\n" +
                        "    return-void\n" +
                        ".end method\n",
                1, SmaliUsageTypeProvider.LITERAL,
                2, UsageType.UNCLASSIFIED,
                3, UsageType.UNCLASSIFIED,
                4, UsageType.UNCLASSIFIED,
                5, UsageType.UNCLASSIFIED,
                6, UsageType.UNCLASSIFIED,
                7, UsageType.UNCLASSIFIED,
                8, UsageType.UNCLASSIFIED,
                9, UsageType.UNCLASSIFIED,
                10, UsageType.UNCLASSIFIED,
                11, UsageType.UNCLASSIFIED,
                12, UsageType.UNCLASSIFIED,
                13, UsageType.UNCLASSIFIED,
                14, SmaliUsageTypeProvider.VERIFICATION_ERROR);
    }
}
