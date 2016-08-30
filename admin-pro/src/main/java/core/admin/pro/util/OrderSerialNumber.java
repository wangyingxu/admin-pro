package core.admin.pro.util;

/**
 * Author    : liuxianglong
 * CreateTime:  15/12/13  14:28
 * <p/>
 * Version: 1.0
 * <p/>
 */
public abstract class OrderSerialNumber {

    public synchronized String getSerialNumber() {
        return process();
    }
    protected abstract String process();
}
