package cc.zdj.coder.generatorFreemarker;

import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 生成VO的重写类。使用第二种生成方式
 *
 * @author zhangdj
 * @version 1.0.0
 * @createdAt 2022/7/4 10:04
 * @updatedAt 2022/7/4 10:04
 */
public class MybatisFreemarkerTemplateEngine extends FreemarkerTemplateEngine {

    @Override
    public AbstractTemplateEngine batchOutput() {
        System.out.println(" 进子类方法");
        try {
            ConfigBuilder config = this.getConfigBuilder();
            List<TableInfo> tableInfoList = config.getTableInfoList();
            tableInfoList.forEach((tableInfo) -> {
                Map<String, Object> objectMap = this.getObjectMap(config, tableInfo);
                Optional.ofNullable(config.getInjectionConfig()).ifPresent((t) -> {
                    t.beforeOutputFile(tableInfo, objectMap);
                });
                this.outputEntity(tableInfo, objectMap);
                this.outputMapper(tableInfo, objectMap);
                this.outputService(tableInfo, objectMap);
                this.outputController(tableInfo, objectMap);
                this.outputVo(tableInfo, objectMap);
            });
            return this;
        } catch (Exception var3) {
            throw new RuntimeException("无法创建文件，请检查配置信息！", var3);
        }
    }

    /**
     * 输出VO文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.0.0
     */
    protected void outputVo(TableInfo tableInfo, Map<String, Object> objectMap) {
        String voName = tableInfo.getEntityName() + "VO";
        String voPath = getPathInfo(OutputFile.entity);
        if (StringUtils.isNotBlank(voName) && StringUtils.isNotBlank(voPath)) {
            getTemplateFilePath(template -> template.getEntity(getConfigBuilder().getGlobalConfig().isKotlin())).ifPresent((entity) -> {
                String entityFile = String.format((voPath + File.separator + "%s" + suffixJavaOrKt()), voName);
                outputFile(new File(entityFile), objectMap, entity);
            });
        }
    }
}