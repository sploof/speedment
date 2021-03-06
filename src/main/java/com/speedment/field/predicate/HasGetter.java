/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.field.predicate;

import com.speedment.annotation.Api;
import com.speedment.field.methods.Getter;

/**
 *
 * @author pemi
 * @param <ENTITY> entity type
 * @param <V> value type
 */
@Api(version = "2.2")
public interface HasGetter<ENTITY, V> {

    Getter<ENTITY, V> getter();
}