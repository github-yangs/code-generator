<#if enableCopyRight>
  <#include "copyright_footer.ftl">
</#if>

package ${package.Controller};

<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import ${package.Service}.${table.serviceName};
import ${package.Other}.${entity}QueryCriteria;
import ${package.Entity}.${entity};
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * ${table.comment!} 控制器
 *
 * @author ${author}
 * @since ${date}
 */
@Api(tags = "${table.comment!}控制器")
@RestController
@RequiredArgsConstructor
@RequestMapping("<#if package.ModuleName!?length gt 0>/${package.ModuleName}</#if>/${table.entityPath}")
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass}<${table.serviceName}, ${entity}, ${entity}QueryCriteria>{
<#else>
public class ${table.controllerName} {
</#if>

}
