package com.unicom.patrolDoor.utils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import java.io.*;


public class OnlinePreview {

    /**
     * 连接OpenOffice.org 并且启动OpenOffice.org
     *
     * @return
     */
    public  OfficeManager getOfficeManager(String officeHome,Integer host) {
        DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration();

        // 设置OpenOffice的安装目录
        config.setOfficeHome(officeHome);
        //可设置openoffice端口号，8100
        config.setPortNumber(host);

        // 启动OpenOffice的服务
        OfficeManager officeManager = config.buildOfficeManager();
        officeManager.start();
        return officeManager;
    }


    /**
     * openoffice转换文件
     *
     * @param inputFile
     * @param outputFilePath_end
     * @param
     * @param converter
     */
    public  File converterFile(File inputFile, String outputFilePath_end, OfficeDocumentConverter converter) {
        File outputFile = new File(outputFilePath_end);
        // 假如目标路径不存在,则新建该路径
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }
        converter.convert(inputFile, outputFile);
        return outputFile;
    }
}
