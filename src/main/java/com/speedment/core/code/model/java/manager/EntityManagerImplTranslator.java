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
package com.speedment.core.code.model.java.manager;

import com.speedment.codegen.base.Generator;
import com.speedment.codegen.lang.models.Class;
import com.speedment.codegen.lang.models.*;
import com.speedment.core.code.model.java.BaseEntityAndManagerTranslator;
import com.speedment.core.config.model.Table;
import com.speedment.core.exception.SpeedmentException;
import com.speedment.core.manager.sql.AbstractSqlManager;
import com.speedment.core.platform.Platform;
import com.speedment.core.platform.component.JavaTypeMapperComponent;
import com.speedment.core.runtime.typemapping.JavaTypeMapping;

import java.sql.SQLException;
import java.util.stream.Stream;

import static com.speedment.codegen.lang.models.constants.DefaultAnnotationUsage.OVERRIDE;
import static com.speedment.codegen.util.Formatting.block;
import com.speedment.core.db.crud.Result;
import static com.speedment.util.java.JavaLanguage.javaStaticFieldName;

/**
 *
 * @author pemi
 */
public class EntityManagerImplTranslator extends BaseEntityAndManagerTranslator<Class> {

    public EntityManagerImplTranslator(Generator cg, Table configEntity) {
        super(cg, configEntity);
    }

    @Override
    protected Class make(File file) {
        return new ClassBuilder(MANAGER.getImplName()).build()
            .public_()
            .setSupertype(Type.of(AbstractSqlManager.class)
                .add(Generic.of().add(typeOfPK()))
                .add(Generic.of().add(ENTITY.getType()))
                .add(Generic.of().add(BUILDER.getType()))
            )
            .add(MANAGER.getType())

            .call(i -> file.add(Import.of(ENTITY.getImplType())))
            .add(Constructor.of()
                .public_()
                .add("setEntityMapper(this::defaultReadEntity);"))
            .add(Method.of("builder", BUILDER.getType()).public_().add(OVERRIDE)
                .add("return new " + ENTITY.getImplName() + "();"))
            .add(Method.of("toBuilder", BUILDER.getType()).public_().add(OVERRIDE)
                .add(Field.of("prototype", ENTITY.getType()))
                .add("return new " + ENTITY.getImplName() + "(prototype);"))
            .call(i -> file.add(Import.of(Type.of(Stream.class))))
            .add(defaultReadEntity(file));
    }

    private Method defaultReadEntity(File file) {

        //file.add(Import.of(Type.of(SQLException.class)));
        file.add(Import.of(Type.of(SpeedmentException.class)));
        file.add(Import.of(FIELD.getType()));

        final Method method = Method.of("defaultReadEntity", ENTITY.getType())
            .protected_()
            .add(Field.of("result", Type.of(Result.class)))
            .add("final " + BUILDER.getName() + " builder = builder();");

        final JavaTypeMapperComponent mapperComponent = Platform.get().get(JavaTypeMapperComponent.class);
        final Stream.Builder<String> streamBuilder = Stream.builder();

        columns().forEachOrdered(c -> {

            final JavaTypeMapping<?> mapping = mapperComponent.apply(dbms().getType(), c.getMapping());
            final StringBuilder sb = new StringBuilder()
                .append("builder.set")
                .append(typeName(c))
                .append("(");
            
            final String getterName = "get" + mapping.getResultSetMethodName(dbms());

            if (Stream.of(Result.class.getMethods())
                .map(java.lang.reflect.Method::getName)
                .anyMatch(getterName::equals)
            &&  !c.isNullable()) {
                sb
                    .append("result.")
                    .append("get")
                    .append(mapping.getResultSetMethodName(dbms()))
                    .append("(")
                    .append(FIELD.getName()).append(".").append(javaStaticFieldName(c.getName())).append(".getColumn()");
            } else {
                sb
                    .append("get")
                    .append(mapping.getResultSetMethodName(dbms()))
                    .append("(result, ")
                    .append(FIELD.getName()).append(".").append(javaStaticFieldName(c.getName())).append(".getColumn()");
            }

            sb.append(");");
            streamBuilder.add(sb.toString());

        });

        method
            .add("try " + block(streamBuilder.build()))
            .add("catch (" + SQLException.class.getSimpleName() + " sqle) " + block(
                    "throw new " + SpeedmentException.class.getSimpleName() + "(sqle);"
                ))
            .add("return builder;");

        return method;
    }

    @Override
    protected String getJavadocRepresentText() {
        return "A manager implementation";
    }

    @Override
    protected String getFileName() {
        return MANAGER.getImplName();
    }

    @Override
    protected boolean isInImplPackage() {
        return true;
    }

    public Type getImplType() {
        return MANAGER.getImplType();
    }

}
