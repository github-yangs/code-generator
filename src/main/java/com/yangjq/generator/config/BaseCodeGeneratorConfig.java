package com.yangjq.generator.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.yangjq.generator.util.YmlParameterUtil;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成工具类
 *
 * @author yangjq
 * @since 2022/6/28
 */
public abstract class BaseCodeGeneratorConfig {

  private static String author = YmlParameterUtil.getYmlParameter("author");
  private static String baseController = YmlParameterUtil.getYmlParameter("base.controller");
  private static String baseService = YmlParameterUtil.getYmlParameter("base.service");
  private static String baseServiceImpl = YmlParameterUtil.getYmlParameter("base.serviceImpl");
  private static String baseEntity = YmlParameterUtil.getYmlParameter("base.entity");
  private static String baseQueryCriteria = YmlParameterUtil.getYmlParameter("base.criteria");

  /**
   * 数据库相关配置
   */
  private static String ip = YmlParameterUtil.getYmlParameter("datasource.ip");
  private static String db = YmlParameterUtil.getYmlParameter("datasource.database");
  private static String username = YmlParameterUtil.getYmlParameter("datasource.username");
  private static String password = YmlParameterUtil.getYmlParameter("datasource.password");

  /**
   * 路径相关配置
   */
  private static String projectPath = YmlParameterUtil.getYmlParameter("path.projectPath");
  private static String parent = YmlParameterUtil.getYmlParameter("path.parentPath");
  private static String moduleName = YmlParameterUtil.getYmlParameter("path.moduleName");
  private static String pkgPath = projectPath + "/src/main/java";
  private static String pkgXml = projectPath + "/src/main/resources/mapper/" + moduleName;
  private static Boolean enableCopyRight = true;

  /**
   * 表相关配置
   */
  private static String[] tables = StrUtil
      .splitToArray(YmlParameterUtil.getYmlParameter("table.names").replaceAll(" ", ""), ",");
  private static String prefix = YmlParameterUtil.getYmlParameter("table.prefix");

  protected static void generate() {
    String url = String.format(
        "jdbc:mysql://%s:3306/%s?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false",
        ip, db);
    generate(url, username, password);
  }

  private static void generate(String url, String username, String password) {

    FastAutoGenerator.create(url, username, password)
        /*全局配置*/
        .globalConfig(builder -> builder
                .outputDir(pkgPath) //指定全局输出目录（基本目录）
                .author(author) //作者名称
                .enableSwagger() //开启Swagger2
                .fileOverride() //是否覆盖
            //.disableOpenDir() //生成完成后不弹窗提醒
        )
//        .globalConfig((scanner, builder) -> builder
//            .author(scanner.apply("请输入作者名称:"))
//        )

        /*包配置*/
        .packageConfig(builder -> builder
            .parent(parent) //父包名
            .moduleName(moduleName) //模块名
            .entity("domain.entity")
            .other("domain.criteria")
            .pathInfo(new HashMap<OutputFile, String>(2) { //如果需要输出到指定输出目录（globalConfig）外的地方
              {
                put(OutputFile.mapperXml, pkgXml);
              }
            })
        )

        /*模板配置*/
        .templateConfig(builder -> {
            }
            //.disable(TemplateType.XML, TemplateType.MAPPER, TemplateType.ENTITY, TemplateType.SERVICEIMPL, TemplateType.SERVICE)
        )

        /*注入配置*/
        .injectionConfig(builder -> builder
            .beforeOutputFile((tableInfo, objectMap) -> { //预处理模板信息

            })
            .customMap(new HashMap<String, Object>(1) { //需要传入的自定义参数
              {
                put("enableCopyRight", enableCopyRight);
                put("superQueryCriteriaClassPackage", baseQueryCriteria);
              }
            })
            .customFile(new HashMap<String, String>(1) { //需要输出的自定义文件名和自定义模板路径
              {
                //key 区分 模板
                put("queryCriteria", "/templates/queryCriteria.java.ftl");
              }
            })
        )

        /*设置模板引擎*/
        .templateEngine(new FreemarkerTemplateEngine() {
          @Override
          protected void outputCustomFile(Map<String, String> customFile, TableInfo tableInfo,
              Map<String, Object> objectMap) {
            String otherPath = getPathInfo(OutputFile.other);
            customFile.forEach((key, value) -> {
              key = objectMap.get("entity") + "QueryCriteria.java"; //重新命名文件名
              String fileName = String.format((otherPath + File.separator + "%s"), key);
              outputFile(new File(fileName), objectMap, value);
            });
          }
        })

        /*策略配置*/
        .strategyConfig(builder -> builder
            .addInclude(
                tables
            ) //配置要生成的表名
            .addTablePrefix(prefix) //过滤表前缀
        )
        .strategyConfig(builder -> {
              try {
                builder
                    .controllerBuilder() /*controller 策略配置*/
                    .superClass(Class.forName(baseController))
                    .enableRestStyle()

                    .serviceBuilder() /*service 策略配置*/
                    .convertServiceFileName(
                        entityName -> entityName + ConstVal.SERVICE) /*service 去掉I前缀*/
                    .superServiceClass(Class.forName(baseService))
                    .superServiceImplClass(Class.forName(baseServiceImpl))

                    .mapperBuilder() /*mapper 策略配置*/
                    .enableBaseResultMap()
                    .enableBaseColumnList()

                    .entityBuilder() /*entity 策略配置*/
                    .superClass(Class.forName(baseEntity))
                    .addSuperEntityColumns("id", "create_time", "update_time", "del")
                    //            .enableChainModel() //开启链式模式
                    .disableSerialVersionUID() //不允许生成序列号ID（基类已经有了）
                    .enableRemoveIsPrefix() //Boolean类型字段移除is前缀
                    //.enableTableFieldAnnotation() //开启生成实体时生成字段注解
                    .enableLombok() //开启lombok
                    //            .addTableFills(
                    //                new Property("createTime", FieldFill.INSERT),
                    //                new Property("updateTime", FieldFill.INSERT_UPDATE)
                    //            )
                    .logicDeletePropertyName("del")
                    .idType(IdType.ASSIGN_ID); //主键类型设置为雪花键
              } catch (ClassNotFoundException e) {
                e.printStackTrace();
              }
            }

        )

        .execute();/*执行*/ //开启sql运行时间是怎么设置的忘了；设置select不返回is_del字段怎么设置的也忘了（TableField？）
  }

}
