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
package com.speedment.internal.core.config.immutable;

import com.speedment.internal.core.config.*;
import com.speedment.config.Project;
import com.speedment.config.ProjectManager;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author pemi
 */
public final class ImmutableProjectManager extends ImmutableAbstractNamedConfigEntity implements ProjectManager, ImmutableParentHelper<Project> {

    private final ChildHolder children;

    public ImmutableProjectManager(ProjectManager projectManager) {
        super(requireNonNull(projectManager).getName(), true);
        // Children
        children = childHolderOf(projectManager.stream().map(p -> new ImmutableProject(this, p)));
    }

    @Override
    public ChildHolder getChildren() {
        return children;
    }

}
