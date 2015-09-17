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
package com.speedment.db;

import com.speedment.annotation.Api;
import com.speedment.db.crud.Result;
import java.util.function.Function;
import java.util.stream.Stream;
import com.speedment.db.crud.CrudOperation;

/**
 *
 * @author pemi
 * @param <ENTITY> The type that the result shall be mapped to
 */
@Api(version = "2.1")
public interface AsynchronousQueryResult<ENTITY> extends AutoCloseable {

    Stream<ENTITY> stream();

    CrudOperation getOperation();
    
    void setOperation(CrudOperation operation);

    Function<Result, ENTITY> getResultMapper();

    void setResultMapper(Function<Result, ENTITY> rsMapper);
    
    @Override
    void close();

}