package com.common.util;

import java.util.HashMap;
import java.util.Map;

public class FileUtil {
    
    public static String getExtension(String fileName) {
        return StringUtil.substringStart(fileName, fileName.lastIndexOf('.')+1);
    }
    
    public static final Map<String, String> fileExtensionMap;
    static {
        fileExtensionMap = new HashMap<String, String>();
        // MS Office
        fileExtensionMap.put("doc", "application/msword");
        fileExtensionMap.put("dot", "application/msword");
        fileExtensionMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        fileExtensionMap.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        fileExtensionMap.put("docm", "application/vnd.ms-word.document.macroEnabled.12");
        fileExtensionMap.put("dotm", "application/vnd.ms-word.template.macroEnabled.12");
        fileExtensionMap.put("xls", "application/vnd.ms-excel");
        fileExtensionMap.put("xlt", "application/vnd.ms-excel");
        fileExtensionMap.put("xla", "application/vnd.ms-excel");
        fileExtensionMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileExtensionMap.put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        fileExtensionMap.put("xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12");
        fileExtensionMap.put("xltm", "application/vnd.ms-excel.template.macroEnabled.12");
        fileExtensionMap.put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
        fileExtensionMap.put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        fileExtensionMap.put("ppt", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("pot", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("pps", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("ppa", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        fileExtensionMap.put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
        fileExtensionMap.put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        fileExtensionMap.put("ppam", "application/vnd.ms-powerpoint.addin.macroEnabled.12");
        fileExtensionMap.put("pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        fileExtensionMap.put("potm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        fileExtensionMap.put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
        // Open Office
        fileExtensionMap.put("odt", "application/vnd.oasis.opendocument.text");
        fileExtensionMap.put("ott", "application/vnd.oasis.opendocument.text-template");
        fileExtensionMap.put("oth", "application/vnd.oasis.opendocument.text-web");
        fileExtensionMap.put("odm", "application/vnd.oasis.opendocument.text-master");
        fileExtensionMap.put("odg", "application/vnd.oasis.opendocument.graphics");
        fileExtensionMap.put("otg", "application/vnd.oasis.opendocument.graphics-template");
        fileExtensionMap.put("odp", "application/vnd.oasis.opendocument.presentation");
        fileExtensionMap.put("otp", "application/vnd.oasis.opendocument.presentation-template");
        fileExtensionMap.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        fileExtensionMap.put("ots", "application/vnd.oasis.opendocument.spreadsheet-template");
        fileExtensionMap.put("odc", "application/vnd.oasis.opendocument.chart");
        fileExtensionMap.put("odf", "application/vnd.oasis.opendocument.formula");
        fileExtensionMap.put("odb", "application/vnd.oasis.opendocument.database");
        fileExtensionMap.put("odi", "application/vnd.oasis.opendocument.image");
        fileExtensionMap.put("oxt", "application/vnd.openofficeorg.extension");
        // Other
        fileExtensionMap.put("txt", "text/plain");
        fileExtensionMap.put("html", "text/html");
        fileExtensionMap.put("json", "application/json");
        fileExtensionMap.put("rtf", "application/rtf");
        fileExtensionMap.put("pdf", "application/pdf");
        fileExtensionMap.put("css", "text/css");
        fileExtensionMap.put("js", "text/javascript");
        fileExtensionMap.put("xml", "application/xml");
        fileExtensionMap.put("xhtml", "application/xhtml+xml");
        // Image
        fileExtensionMap.put("gif", "image/gif");
        fileExtensionMap.put("png", "image/png");
        fileExtensionMap.put("jpg", "image/jpeg");
        fileExtensionMap.put("jpeg", "image/jpeg");
        fileExtensionMap.put("svg", "image/svg+xml");
        fileExtensionMap.put("bmp", "image/bmp");
        fileExtensionMap.put("webp", "image/webp");
        fileExtensionMap.put("aac", "audio/aac");
        fileExtensionMap.put("abw", "application/x-abiword");
        fileExtensionMap.put("arc", "application/octet-stream");
        fileExtensionMap.put("avi", "video/x-msvideo");
        fileExtensionMap.put("azw", "application/vnd.amazon.ebook");
        fileExtensionMap.put("bin", "application/octet-stream");
        fileExtensionMap.put("bz", "application/x-bzip");
        fileExtensionMap.put("bz2", "application/x-bzip2");
        fileExtensionMap.put("csh", "application/x-csh");
        fileExtensionMap.put("css", "text/css");
        fileExtensionMap.put("csv", "text/csv");
        fileExtensionMap.put("doc", "application/msword");
        fileExtensionMap.put("epub", "application/epub+zip");
        fileExtensionMap.put("gif", "image/gif");
        fileExtensionMap.put("htm", "text/html");
        fileExtensionMap.put("html", "text/html");
        fileExtensionMap.put("ico", "image/x-icon");
        fileExtensionMap.put("ics", "text/calendar");
        fileExtensionMap.put("jar", "application/java-archive");
        fileExtensionMap.put("jpeg", "image/jpeg");
        fileExtensionMap.put("jpg", "image/jpeg");
        fileExtensionMap.put("js", "application/js");
        fileExtensionMap.put("json", "application/json");
        fileExtensionMap.put("mid", "audio/midi");
        fileExtensionMap.put("midi", "audio/midi");
        fileExtensionMap.put("mpeg", "video/mpeg");
        fileExtensionMap.put("mpkg", "application/vnd.apple.installer+xml");
        fileExtensionMap.put("odp", "application/vnd.oasis.opendocument.presentation");
        fileExtensionMap.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        fileExtensionMap.put("odt", "application/vnd.oasis.opendocument.text");
        fileExtensionMap.put("oga", "audio/ogg");
        fileExtensionMap.put("ogv", "video/ogg");
        fileExtensionMap.put("ogx", "application/ogg");
        fileExtensionMap.put("pdf", "application/pdf");
        fileExtensionMap.put("ppt", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("rar", "application/x-rar-compressed");
        fileExtensionMap.put("rtf", "application/rtf");
        fileExtensionMap.put("sh", "application/x-sh");
        fileExtensionMap.put("svg", "image/svg+xml");
        fileExtensionMap.put("swf", "application/x-shockwave-flash");
        fileExtensionMap.put("tar", "application/x-tar");
        fileExtensionMap.put("tif", "image/tiff");
        fileExtensionMap.put("tiff", "image/tiff");
        fileExtensionMap.put("ttf", "application/x-font-ttf");
        fileExtensionMap.put("vsd", "application/vnd.visio");
        fileExtensionMap.put("wav", "audio/x-wav");
        fileExtensionMap.put("weba", "audio/webm");
        fileExtensionMap.put("webm", "video/webm");
        fileExtensionMap.put("webp", "image/webp");
        fileExtensionMap.put("woff", "application/x-font-woff");
        fileExtensionMap.put("xhtml", "application/xhtml+xml");
        fileExtensionMap.put("xls", "application/vnd.ms-excel");
        fileExtensionMap.put("xml", "application/xml");
        fileExtensionMap.put("xul", "application/vnd.mozilla.xul+xml");
        fileExtensionMap.put("zip", "application/zip");
        fileExtensionMap.put("3gp", "video/3gpp");
        fileExtensionMap.put("3g2", "video/3gpp2");
        fileExtensionMap.put("7z", "application/x-7z-compressed");
    }
}
