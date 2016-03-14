/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2015 the original author or authors.
 */
package org.assertj.assertions.generator.data.skipped;

import org.assertj.assertions.generator.annotations.SkipAssertJGeneration;

public class PartiallySkipped {


    private boolean aBoolean;
    private boolean anotherBoolean;

    @SkipAssertJGeneration
    public boolean aField;
    public boolean anotherField;

    @SkipAssertJGeneration
    public boolean isABoolean() {
        return aBoolean;
    }

    public boolean isAnotherBoolean() {
        return anotherBoolean;
    }
}
