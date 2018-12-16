import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ResearchReport {
    /**
     * 每个研究员对于的研究报告， 对research中多个研究院进行拆分
     * @return
     */
    public static Map<String,List<Map<String,Object>>> getSurveyData(){
        Connection  connection = ConnectionUtil.getConneciton();
        Map<String,List<Map<String,Object>>> result  = new HashMap<>();
        try {
            PreparedStatement ps = connection.prepareStatement("select * from \"DY\"");
            if(ps.execute()){
                ResultSet rs = ps.getResultSet();
                while(rs.next()){
                    Map<String,Object> record = new HashMap<>();
                    String researcher = rs.getString("researcher");
                    if(null!=researcher){
                        String[] researchers =  researcher.split("、");
                        for(String s : researchers){
//                            record.put("sid",rs.getLong("SID"));
                            record.put("code",rs.getString("code"));
                            record.put("researcherAll",researcher);
                            record.put("researcher",s);
                            record.put("reportdate",rs.getDate("reportdate"));
                            record.put("name",rs.getString("name"));
                            if(record.get(s) ==null){
                                result.put(s,new ArrayList<>());
                            }
                            result.get(s).add(record);
                        }
                    }
                }
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * user ->companey code -> record list
     * @return
     */
    public static Map<String,Map<String,List<Map<String,Object>>>> getSurveyData2(){
        Connection  connection = ConnectionUtil.getConneciton();
        Map<String,Map<String,List<Map<String,Object>>>> result  = new HashMap<>();
        try {
            PreparedStatement ps = connection.prepareStatement("select * from \"DY\"");
            if(ps.execute()){
                ResultSet rs = ps.getResultSet();
                while(rs.next()){
                    String researcher = rs.getString("researcher");
                    if(null!=researcher){
                        String[] researchers =  researcher.split("、");
                        String code = rs.getString("code");
                        for(String s : researchers){
                            Map<String,Object> record = new HashMap<>();
                            record.put("code",code);
                            record.put("researcherAll",researcher);
                            record.put("researcher",s);
                            record.put("reportdate",rs.getDate("reportdate"));
                            record.put("name",rs.getString("name"));
                            if(result.get(s) ==null){
                                result.put(s,new HashMap<>());
                            }
                            if(result.get(s).get(code) ==null){
                                result.get(s).put(code,new ArrayList<>());
                            }
                            result.get(s).get(code).add(record);
                        }
                    }
                }
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }
        return result;
    }

    public static boolean   inSomeMonth(java.util.Date d1, java.util.Date d2,int monthStart ,int monthEnd){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        if(c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)){
            int  days = (c1.get(Calendar.DAY_OF_YEAR)-c2.get(Calendar.DAY_OF_YEAR));
            return days<=30*monthEnd&&days>monthStart*30;
        }
        return false;
    }
    public static  void main(String[] args ){
        Map<String,Map<String,List<Map<String,Object>>>>  surveyData = ResearchReport.getSurveyData2();
        Connection  connection = ConnectionUtil.getConneciton();
        try {
            Statement st  =connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
//            st.executeQuery("select * from \"prediction\"");
            st.executeQuery("select \"rptdt\",\"fenddt\",\"code\",\"researcher\"," +
                    "m1,m1er,m1_date,m3,m3er,m3_date,m6,m6er,m6_date," +
                    "other1,other1desc,other3,other3desc,other6,other6desc"+
                    ",hy11,hy11desc,hy13,hy13desc,hy16,hy16desc,hy21,hy21desc,hy23,hy23desc,hy26,hy26desc,hy31,hy31desc,hy33,hy33desc,hy36,hy36desc  from \"YC\"");
            ResultSet rs =   st.getResultSet();
            System.out.println("总共预测的数据："+rs.getRow());
            int i = 0;
            while(rs.next()){
                Date rptdt = rs.getDate("rptdt");//预测报告公布日
                Date fenddt = rs.getDate("fenddt");//预测终止日
                String code = rs.getString("code");//编码
                String researcher = rs.getString("researcher");
                if(null!=researcher) {

                    boolean  m1= false;
                    Date m1Date = null;
                    String m1er = "";
                    String m2er = "";
                    String m3er = "";
                    boolean m2= false;
                    Date m2Date = null;
                    boolean m3= false;
                    Date m3Date = null;

                    String[] researchers = researcher.split(",");
                    boolean inOtherCompany1 = false; // 一个月内参与过其他公司的调研
                    String other1Desc = "";
                    boolean inOtherCompany3 = false;// 三个月内参与过其他公司的调研
                    String other3Desc = "";
                    boolean inOtherCompany6 = false; //六个月内参与过其他公司的调研
                    String other6Desc = "";

                    for(String s :researchers){
                        Map<String,List<Map<String,Object>>> companyMap =  surveyData.get(s);
                        if(null==companyMap){
                            System.out.println(s+"没有参与过任何公司的调研");
                            continue;
                        }
                        List<Map<String,Object>>  surveyList = companyMap.get(code);
                        //此为分析师参与过这家公司的调研
                        if(surveyList !=null){
                            for(Map<String,Object> surveyRec: surveyList){
                                Date date = (Date) surveyRec.get("reportdate");
                                if(ResearchReport.inSomeMonth(rptdt,date,0,1)){
                                    m1 = true;m1Date=date;
                                    m1er +=s;
                                }

                                if(ResearchReport.inSomeMonth(rptdt,date,0,3)){
                                    m2 = true;m2Date=date;
                                    m3er +=s;
                                }

                                if(ResearchReport.inSomeMonth(rptdt,date,0,6)){
                                    m3 = true;m3Date=date;
                                    m3er +=s;
                                }
                            }
                        }
                        //判断分析师是否分别再三个时间区间内参加过其他公司的调研
                        for(String companyCode:companyMap.keySet()){
                            List<Map<String,Object>> recordList  = companyMap.get(companyCode);
                            for(Map<String,Object> rec:recordList){
                                Date date = (Date) rec.get("reportdate");
                                String desc = rec.get("researcher") + ":" + rec.get("reportdate");
                                if(ResearchReport.inSomeMonth(rptdt,date,0,1)){
                                    inOtherCompany1 = true;
                                    other1Desc+=desc;
                                }

                                if(ResearchReport.inSomeMonth(rptdt,date,0,3)){
                                    inOtherCompany3 = true;
                                    other3Desc+=desc;
                                }

                                if(ResearchReport.inSomeMonth(rptdt,date,0,6)){
                                    inOtherCompany6 = true;
                                    other6Desc+=desc;
                                }
                            }
                        }
                    }
                    rs.updateBoolean("m1",m1);
                    rs.updateDate("m1_date",m1Date);
                    rs.updateString("m1er",m1er);


                    rs.updateBoolean("m3",m2);
                    rs.updateDate("m3_date",m2Date);
                    rs.updateString("m3er",m2er);

                    rs.updateBoolean("m6",m3);
                    rs.updateDate("m6_date",m3Date);
                    rs.updateString("m6er",m3er);

                    rs.updateBoolean("other1",inOtherCompany1);
                    rs.updateString("other1desc",other1Desc);

                    rs.updateBoolean("other3",inOtherCompany3);
                    rs.updateString("other3desc",other3Desc);

                    rs.updateBoolean("other6",inOtherCompany6);
                    rs.updateString("other6desc",other6Desc);


                    rs.updateRow();
                    System.out.print(i+++"|");
                    if(m1){
                        System.out.print(m1er+"一个月内参与调研 ");
                    }
                    if(m2){
                        System.out.print(m2er+"三个月内参与调研 ");
                    }
                    if(m3){
                        System.out.print(m3er+"六个月内参与调研 ");
                    }

                    if(inOtherCompany1){
                        System.out.print(m1er+"一个月内参与调研 "+other1Desc);
                    }
                    if(inOtherCompany3){
                        System.out.print(m2er+"三个月内参与调研 "+other3Desc);
                    }
                    if(inOtherCompany6){
                        System.out.print(m3er+"六个月内参与调研 "+other6Desc);
                    }
                    if(m1&&m2&&m3){
                        System.out.print("没有参与调研 ");
                    }
                    System.out.println("分析师："+researcher+" 股票编码："+code);
                }
            }
            rs.close();
            st.close();
            connection.commit();
            System.out.println("完成！提交事务");
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                System.out.println("关闭数据库连接！");
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
