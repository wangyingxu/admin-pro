package core.admin.pro.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DATE:8/15/15 11:23
 * AUTHOR:wangzhen
 */
public class CipherUtils {
    private static final Logger logger = LoggerFactory.getLogger(core.admin.util.CipherUtils.class);

    public static String md5(String raw){
        return DigestUtils.md5Hex(raw);
    }

    public static void main(String[] args) {
        System.out.println(core.admin.util.CipherUtils.md5("123456"));
        System.out.println(core.admin.util.CipherUtils.md5("654321"));
    }
}
