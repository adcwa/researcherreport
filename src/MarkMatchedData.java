import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 需求，
 * 分析师数据T1
 * 预测表数据T2
 *同一个分析师，同一个机构，在同一年，在T2有预测数据,则在T1上标记为1
 *同一个分析师，同一个机构，在同一年，在T1是有数据，则在T2上标记为1
 *
 * ps : 预测数据上分析师用逗号隔开，所以分列成多列
 */
public class MarkMatchedData {

    public static  Connection connection;
    public static  Statement statement1;
    static {
        connection = ConnectionUtil.getConneciton();

    }
    //    t2 tt2 left  join t1 tt1 on tt1.year = tt2.year2 and tt1."ananm" is not null and
    // （tt1."ananm" = tt2."Ananm1" or tt1."ananm" = tt2."Ananm2" or tt1."ananm" = tt2."Ananm3" or
    // tt1."ananm" = tt2."Ananm4" or tt1."ananm" = tt2."Ananm5" or tt1."ananm" = tt2."Ananm6" or tt1."ananm" = tt2."Ananm7" or
    // tt1."ananm" = tt2."Ananm8"）  and tt1."institutionid" = tt2."InstitutionID";
    public static Map<String, List<Integer>> t1RsIndex= new HashMap<>();
    public static  ResultSet t1Re;
    public static void initT1Data(){
//        Map<String,Integer> keyRowIndexMap = new HashMap<>();
        try {
            statement1 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            t1Re = statement1.executeQuery("select flag,\"ananmid\",\"institutionid\",year from \"T1\"");

            int i = 0;
            while(t1Re.next()){
//                Boolean flag = rs1.getBoolean(1);
                String ananmId = t1Re.getString(2);
                String insitiutionId = t1Re.getString(3);
                String year = t1Re.getString(4);
                String key = getKey(year,insitiutionId,ananmId);
                //可能有当年同一个分析员对同一家公司的 有多次调研
                if(t1RsIndex.get(key)==null){
                    t1RsIndex.put(key,new ArrayList<>());
                }
                t1RsIndex.get(key).add(t1Re.getRow());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        Long before = System.currentTimeMillis();
        //1、查询分析师数据，存储index,保留result，用于更新标记
        initT1Data();
        //2、查询预测表数据，遍历预测表
        int count  =0;
        Statement statement2 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs2 = statement2.executeQuery("select year2,flag2,\"AnanmID\",\"InstitutionID\" from \"T2\"");

        while(rs2.next()){
            System.out.print(count+++":");
            String year2 = rs2.getString(1);
            Boolean flag2 = rs2.getBoolean(2);
            String ananmIds = rs2.getString("AnanmID");
            String insititutionId = rs2.getString("InstitutionID");
            Boolean  matched = false;
            if(null!=ananmIds){
                String[] ananms = ananmIds.split(",");
                for(String ananmId:ananms){
                    String key = getKey(year2,insititutionId,ananmId);
                    List<Integer>  indexes = t1RsIndex.get(key);
                    if(null!= indexes){
                        for(Integer index :indexes){
                        //匹配到当年相同公司的分析师，
                            t1Re.absolute(index);
                            t1Re.updateBoolean("flag",true);
                            t1Re.updateRow();
                            matched =true;
                        }
                    }
                }
                //预测表匹配到当年参加过调研的
                if(matched){
                    rs2.updateBoolean("flag2",matched);
                    rs2.updateRow();
                }
            }
            System.out.println("ananmIds:matched:"+matched);
        }

        System.out.println("总共预测的数据："+rs2.getRow());
        t1Re.close();
        rs2.close();
        statement1.close();
        statement2.close();
        connection.commit();
        System.out.println("提交事务成功");
        System.out.println("供共花费："+(System.currentTimeMillis()-before)/1000/60);
    }

    public static String getKey(String year, String insitiutionId,String name){
        return year+"_"+insitiutionId+"_"+name;
    }
}
