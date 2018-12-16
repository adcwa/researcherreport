import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ResearchReport2 {


    /**
     * name ->code ->行业->record list
     *
     * @return
     */
    public static Map<String, Map<String, Map<String, List<Map<String, Object>>>>> getSurveyData3() {
        Connection connection = ConnectionUtil.getConneciton();
//        Map<String,Map<String,List<Map<String,Object>>>> result  = new HashMap<>();
        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> result = new HashMap<>();
        try {
            PreparedStatement ps = connection.prepareStatement("select * from \"DY\"");
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    String researcher = rs.getString("researcher");
                    if (null != researcher) {
                        String[] researchers = researcher.split("、");
                        String code = rs.getString("code");
                        for (String s : researchers) {
                            Map<String, Object> record = new HashMap<>();
//                            record.put("sid",rs.getLong("SID"));
                            record.put("code", code);
                            record.put("researcherAll", researcher);
                            record.put("researcher", s);
                            record.put("reportdate", rs.getDate("reportdate"));
                            record.put("name", rs.getString("name"));
                            String hy1 = rs.getString("nnindcd");
                            String hy2 = rs.getString("ind");
                            String hy3 = rs.getString("industry");
                            record.put("hy1", hy1);
                            record.put("hy2", hy2);
                            record.put("hy3", hy3);
                            if (result.get(s) == null) {
                                result.put(s, new HashMap<>());
                            }
                            if (result.get(s).get(code) == null) {
                                result.get(s).put(code, new HashMap<>());
                            }
                            if (result.get(s).get(code).get(hy1) == null) {
                                result.get(s).get(code).put(hy1, new ArrayList<>());
                            }
                            if (result.get(s).get(code).get(hy2) == null) {
                                result.get(s).get(code).put(hy2, new ArrayList<>());
                            }
                            if (result.get(s).get(code).get(hy3) == null) {
                                result.get(s).get(code).put(hy3, new ArrayList<>());
                            }
                            result.get(s).get(code).get(hy1).add(record);
                            result.get(s).get(code).get(hy2).add(record);
                            result.get(s).get(code).get(hy3).add(record);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) {
        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> surveyData = ResearchReport2.getSurveyData3();
        Connection connection = ConnectionUtil.getConneciton();
        try {
            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
//            st.executeQuery("select * from \"prediction\"");
            st.executeQuery("select \"rptdt\",\"fenddt\",\"code\",\"researcher\",\"nnindcd\",\"ind\",\"industry\"," +
                    "hy11,hy11desc,hy13,hy13desc,hy16,hy16desc,hy21,hy21desc,hy23,hy23desc,hy26,hy26desc,hy31,hy31desc,hy33,hy33desc,hy36,hy36desc  from \"YC\"");
            ResultSet rs = st.getResultSet();
            System.out.println("总共预测的数据：" + rs.getRow());
            int i = 0;
            while (rs.next()) {
                Date rptdt = rs.getDate("rptdt");//预测报告公布日
                Date fenddt = rs.getDate("fenddt");//预测终止日
                String code = rs.getString("code");//编码
                String researcher = rs.getString("researcher");
                String hy1 = rs.getString("nnindcd");
                String hy2 = rs.getString("ind");
                String hy3 = rs.getString("industry");
                if (null != researcher) {
                    String[] researchers = researcher.split(",");
                    YCResult ycResult = new YCResult();
                    for (String s : researchers) {
                        Map<String, Map<String, List<Map<String, Object>>>> companyMap = surveyData.get(s);
                        if (null == companyMap) {
                            System.out.println(s + "没有参与过任何行业、任何公司的调研");
                            continue;
                        }
                        //获取对当前公司及所属行业的调研数据 ，但是我们需要的是时间区间相同行业内参与过其他公司调研的预测报告
                        Map<String, List<Map<String, Object>>> surveyList = companyMap.get(code);

                        //所以直接遍历分析员所有调研的公司
                        //判断分析师是否分别再三个时间区间内参加过其他公司的调研
                        for (String companyCode : companyMap.keySet()) {
                            if (companyCode.equals(code)) {
                                //相同公司， 暂时不处理，直接跳过
                                continue;
                            }
                            //公司所细分的行业，对细分后相同行业，分别对每个行业出份结果
                            Map<String, List<Map<String, Object>>> hyMap = companyMap.get(companyCode);
                            //行业1
                            List<Map<String, Object>> hy1List = hyMap.get(hy1);
                            if (null != hy1List)
                                for (Map<String, Object> rec : hy1List) {
                                    //细分行业下调研报告时间
                                    Date date = (Date) rec.get("reportdate");
                                    String desc = rec.get("researcher") + ":" + rec.get("reportdate");
                                    if (ResearchReport.inSomeMonth(rptdt, date, 0, 1)) {
                                        ycResult.hy11 = true;
                                        ycResult.hy11Desc += desc;
                                    }
                                    if (ResearchReport.inSomeMonth(rptdt, date, 0, 3)) {
                                        ycResult.hy13 = true;
                                        ycResult.hy13Desc += desc;
                                    }
                                    if (ResearchReport.inSomeMonth(rptdt, date, 0, 6)) {
                                        ycResult.hy16 = true;
                                        ycResult.hy13Desc += desc;
                                    }
                                }
                            List<Map<String, Object>> hy2List = hyMap.get(hy2);
                            if (null != hy2List)
                                for (Map<String, Object> rec : hy2List) {
                                    //细分行业下调研报告时间
                                    Date date = (Date) rec.get("reportdate");
                                    String desc = rec.get("researcher") + ":" + rec.get("reportdate");
                                    if (ResearchReport.inSomeMonth(rptdt, date, 0, 1)) {
                                        ycResult.hy21 = true;
                                        ycResult.hy21Desc += desc;
                                    }
                                    if (ResearchReport.inSomeMonth(rptdt, date, 0, 3)) {
                                        ycResult.hy23 = true;
                                        ycResult.hy23Desc += desc;
                                    }
                                    if (ResearchReport.inSomeMonth(rptdt, date, 0, 6)) {
                                        ycResult.hy26 = true;
                                        ycResult.hy26Desc += desc;
                                    }
                                }
                            List<Map<String, Object>> hy3List = hyMap.get(hy3);
                            if (null != hy3List)
                                for (Map<String, Object> rec : hy3List) {
                                    //细分行业下调研报告时间
                                    Date date = (Date) rec.get("reportdate");
                                    String desc = rec.get("researcher") + ":" + rec.get("reportdate");
                                    if (ResearchReport.inSomeMonth(rptdt, date, 0, 1)) {
                                        ycResult.hy31 = true;
                                        ycResult.hy31Desc += desc;
                                    }
                                    if (ResearchReport.inSomeMonth(rptdt, date, 0, 3)) {
                                        ycResult.hy33 = true;
                                        ycResult.hy31Desc += desc;
                                    }
                                    if (ResearchReport.inSomeMonth(rptdt, date, 0, 6)) {
                                        ycResult.hy36 = true;
                                        ycResult.hy31Desc += desc;
                                    }
                                }
                        }
                    }
                    rs.updateBoolean("hy11", ycResult.hy11);
                    rs.updateString("hy11desc", ycResult.hy11Desc);
                    rs.updateBoolean("hy13", ycResult.hy11);
                    rs.updateString("hy13desc", ycResult.hy11Desc);
                    rs.updateBoolean("hy16", ycResult.hy11);
                    rs.updateString("hy16desc", ycResult.hy11Desc);

                    rs.updateBoolean("hy21", ycResult.hy11);
                    rs.updateString("hy21desc", ycResult.hy11Desc);
                    rs.updateBoolean("hy23", ycResult.hy11);
                    rs.updateString("hy23desc", ycResult.hy11Desc);
                    rs.updateBoolean("hy26", ycResult.hy11);
                    rs.updateString("hy26desc", ycResult.hy11Desc);

                    rs.updateBoolean("hy31", ycResult.hy11);
                    rs.updateString("hy31desc", ycResult.hy11Desc);
                    rs.updateBoolean("hy33", ycResult.hy11);
                    rs.updateString("hy33desc", ycResult.hy11Desc);
                    rs.updateBoolean("hy36", ycResult.hy11);
                    rs.updateString("hy36desc", ycResult.hy11Desc);
                    rs.updateRow();
                    System.out.println("分析师：" + researcher + " 股票编码：" + code + ":" + ycResult.toString());
                }
            }
            rs.close();
            st.close();
            connection.commit();
            System.out.println("完成！提交事务");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("关闭数据库连接！");
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
