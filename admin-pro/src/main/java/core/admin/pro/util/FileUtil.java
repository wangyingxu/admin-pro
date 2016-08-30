package core.admin.pro.util;

import com.google.common.base.Strings;
import core.admin.common.AdminConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author    : liuxianglong
 * CreateTime:  15/12/13  14:32
 * <p/>
 * Version: 1.0
 * <p/>
 */
public  class FileUtil {
    private static final Logger logger = Logger.getLogger(core.admin.util.FileUtil.class);
    public static void rewrite(File file, String data) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bw != null) {
                try {
                    bw.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<String> readList(File file) {
        BufferedReader br = null;
        List<String> data = new ArrayList<String>();
        try {
            br = new BufferedReader(new FileReader(file));
            for(String str = null; (str = br.readLine()) != null; ) {
                data.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * 文件上传
     * @param files 上传文件列表
     * @return List<String> 文件路径地址
     */
    public static List<String> uploadFiles(List<MultipartFile> files){

        List<String> paths = new ArrayList<>();
        if (files != null && files.size() > 0) {
            for (MultipartFile posterFile : files) {
                String name = posterFile.getOriginalFilename();

                if (!Strings.isNullOrEmpty(name)) {
                    Long now = DateTime.now().getMillis();
                    String ext = FilenameUtils.getExtension(name);
                    String YYYY_MM_DD = DateTime.now().toString("YYYY-MM-dd");
                    String POSTER_URI = String.format("%s.%s", now, ext);

                    String POSTER_FOLDER = String.format("%s/%s/%s", AdminConstants.NGINX_WWW_ROOT, YYYY_MM_DD, "goods");
                    File POSTER_FOLDER_FILE = new File(POSTER_FOLDER);
                    if (!POSTER_FOLDER_FILE.exists()) {
                        boolean mkdirs = POSTER_FOLDER_FILE.mkdirs();
                        logger.info("make directory："+ mkdirs);
                    }

                    File DEST = new File(POSTER_FOLDER_FILE,POSTER_URI);
                    try {
                        posterFile.transferTo(DEST);
                        String poster = String.format("/%s/%s/%s",YYYY_MM_DD,"poster",POSTER_URI);
                        paths.add(poster);
                    } catch (IOException e) {
                        logger.error("save user avatar error!",e);
                    }
                }
            }
        }

    return paths;
    }
}
