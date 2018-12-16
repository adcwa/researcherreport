import java.util.Map;

public class YCResult {
    boolean hy11 = false; // 一个月内参与过其他公司的调研
    String hy11Desc = "";
    boolean hy13 = false;// 三个月内参与过其他公司的调研
    String hy13Desc = "";
    boolean hy16 = false; //六个月内参与过其他公司的调研
    String hy16Desc = "";

    boolean hy21 = false; // 一个月内参与过其他公司的调研
    String hy21Desc = "";
    boolean hy23 = false;// 三个月内参与过其他公司的调研
    String hy23Desc = "";
    boolean hy26 = false; //六个月内参与过其他公司的调研
    String hy26Desc = "";


    boolean hy31 = false; // 一个月内参与过其他公司的调研
    String hy31Desc = "";
    boolean hy33 = false;// 三个月内参与过其他公司的调研
    String hy33Desc = "";
    boolean hy36 = false; //六个月内参与过其他公司的调研
    String hy36Desc = "";

    public void setHy1Info(boolean hy1,String hy1Desc,boolean hy3 ,String hy3Desc ,boolean hy6, String hy6Desc){
        hy11 = hy1; // 一个月内参与过其他公司的调研
        hy11Desc = hy1Desc;
        hy13 = hy3;// 三个月内参与过其他公司的调研
        hy13Desc = hy3Desc;
        hy16 = hy6; //六个月内参与过其他公司的调研
        hy16Desc = hy6Desc;
    }
    public void setHy2Info(boolean hy1,String hy1Desc,boolean hy3 ,String hy3Desc ,boolean hy6, String hy6Desc){
        hy21 = hy1; // 一个月内参与过其他公司的调研
        hy21Desc = hy1Desc;
        hy23 = hy3;// 三个月内参与过其他公司的调研
        hy23Desc = hy3Desc;
        hy26 = hy6; //六个月内参与过其他公司的调研
        hy26Desc = hy6Desc;
    }
    public void setHy3Info(boolean hy1,String hy1Desc,boolean hy3 ,String hy3Desc ,boolean hy6, String hy6Desc){
        hy31 = hy1; // 一个月内参与过其他公司的调研
        hy31Desc = hy1Desc;
        hy33 = hy3;// 三个月内参与过其他公司的调研
        hy33Desc = hy3Desc;
        hy36 = hy6; //六个月内参与过其他公司的调研
        hy36Desc = hy6Desc;
    }

    @Override
    public String toString() {
        return "YCResult{" +
                "hy11=" + hy11 +
                ", hy11Desc='" + hy11Desc + '\'' +
                ", hy13=" + hy13 +
                ", hy13Desc='" + hy13Desc + '\'' +
                ", hy16=" + hy16 +
                ", hy16Desc='" + hy16Desc + '\'' +
                ", hy21=" + hy21 +
                ", hy21Desc='" + hy21Desc + '\'' +
                ", hy23=" + hy23 +
                ", hy23Desc='" + hy23Desc + '\'' +
                ", hy26=" + hy26 +
                ", hy26Desc='" + hy26Desc + '\'' +
                ", hy31=" + hy31 +
                ", hy31Desc='" + hy31Desc + '\'' +
                ", hy33=" + hy33 +
                ", hy33Desc='" + hy33Desc + '\'' +
                ", hy36=" + hy36 +
                ", hy36Desc='" + hy36Desc + '\'' +
                '}';
    }
}
