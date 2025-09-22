package com.graph.visualization.service;

import com.graph.visualization.dto.ImportResult;
import com.graph.visualization.enums.ExportFormatEnum;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 图谱导入导出服务接口
 */
public interface GraphImportExportService {
    /**
     * 导入三元组文件到图谱
     * 
     * @param file 上传的文件
     * @return 导入结果，包含导入的节点数和关系数
     * @throws IOException 如果文件处理出错
     */
    ImportResult importTriples(MultipartFile file) throws IOException;
    
    /**
     * 导出图谱数据
     * 
     * @param format 导出格式（JSON或RDF）
     * @return 包含导出数据的资源
     * @throws IOException 如果导出过程出错
     */
    Resource exportGraph(ExportFormatEnum format) throws IOException;
}
