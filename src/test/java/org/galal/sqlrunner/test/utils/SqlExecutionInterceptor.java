package org.galal.sqlrunner.test.utils;


import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static org.galal.sqlrunner.test.utils.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.galal.sqlrunner.test.utils.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class SqlExecutionInterceptor {

    @Inject
    DaoUtil dao;

    @AroundInvoke
    public Object runSqlScripts(InvocationContext ctx) throws Exception {
        List<Sql> annotations = getSqlAnnotations(ctx);
        executeSqlAtPhase(annotations, BEFORE_TEST_METHOD);
        try{
            Object result = ctx.proceed();
            executeSqlAtPhase(annotations, AFTER_TEST_METHOD);
            return result;
        }catch(Throwable t){
            executeSqlAtPhase(annotations, AFTER_TEST_METHOD);
            throw t;
        }
    }



    private List<Sql> getSqlAnnotations(InvocationContext ctx) {
        return ofNullable(ctx.getMethod().getAnnotation(Sql.class))
                    .map(Sql.class::cast)
                    .map(Arrays::asList)
                    .orElseGet(() -> readRepeatedAnnotations(ctx));
    }



    private List<Sql> readRepeatedAnnotations(InvocationContext ctx){
        return  ofNullable(ctx.getMethod().getAnnotation(Sql.List.class))
                    .map(Sql.List::value)
                    .map(Arrays::asList)
                    .orElseGet(Collections::emptyList);
    }



    private void executeSqlAtPhase(List<Sql> annotations, Sql.ExecutionPhase phase){
        annotations
        .stream()
        .filter(a -> phase.equals(a.executionPhase()))
        .map(Sql::scripts)
        .forEach(this::runSqlScripts);
    }



    private void runSqlScripts(String[] paths) {
        stream(paths)
        .map(TestUtils::readTestResourceAsString)
        .forEach(dao::execute);
    }
}
