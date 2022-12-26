package cc.zdj.coder.controller;

import com.alibaba.fastjson.JSON;
import com.github.kevinsawicki.http.HttpRequest;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 测试接口数据，不用管
 */
public class DelYingji {


    private static String delGMbranch = "http://hrss-saas-server.production1.svc.k8s.bj.wdcloud.cc/saas/v1/sdyjGmUser/deleteBranch?branchId=";
    private static String delDzUser = "http://hrss-saas-server.production1.svc.k8s.bj.wdcloud.cc/saas/v1/sdyjGmUser/deleteDzUser?id=";
    private static String delWhpUser = "http://hrss-saas-server.production1.svc.k8s.bj.wdcloud.cc/saas/v1/sdyjWhpUser/deleteWhpUser?id=";

//    private static String delWHuserPhone = "http://hrss-saas-server.production1.svc.k8s.bj.wdcloud.cc/saas/v1/sdyjWhpUser/deleteUserByPhoneList";
    private static String delGmSmuser = "http://hrss-saas-server.production1.svc.k8s.bj.wdcloud.cc/saas/v1/sdyjDelUserRecord/manualDel";
    private static String delWhpBranch = "http://hrss-saas-server.production1.svc.k8s.bj.wdcloud.cc/saas/v1/sdyjWhpUser/deleteBranch?branchId=";
    private static String delbranchCache = "http://hrss-user-center.production1.svc.k8s.bj.wdcloud.cc/usercenter/v1/account/cleanAccountBranch?phone=";
    private static String branchTypeChange = "http://hrss-saas-server.production1.svc.k8s.bj.wdcloud.cc/saas/v1/GmBranch/changeEnterType?branchId={{branchId}}&enterType={{enterType}}&needSms=";

    public static void main(String[] args) {
//        //删除用户中心缓存
//        delBranshCache();
//
//        //删除工贸监管人员
//        delDzUser();
//
//        //危化品删除员工
//        delWhpUser();
//
//        //删除工贸实名认证学员
//        delGmSmuser();
//
//        //变更企业类型
//        branchTypeChange();
//
//        //删除工贸机构
//        delGMBranch();
//
//        //删除危化机构
//        delWHBranch();

    }

    // 删除机构缓存  user_branch_x 表处理过之后，需要删除缓存
    private static void delBranshCache() {
        List<String> strings = Arrays.asList("13791058432", "13176007894", "15628903563", "13589138595", "13665416052", "13220563630", "15806659900", "15764106535", "13563498007", "13963498686", "13054837777", "13963493961", "15263490988", "18663492741", "13963436644", "13022763332", "17763432727", "18763485551", "15165416036", "15163496103", "15588948393", "15563407754", "13563433212", "13561737816", "15163437995", "13863497827", "15154097625", "13963490975", "13656342917", "18563790880", "13963492981", "18663477701", "17865517719", "18263429626", "15318166411", "13626340462", "13963496211", "18763409539", "13963494925", "15866347972", "17663499979", "13563426502", "18263489562", "18763492945", "13616340803", "15563405308", "17563436090", "19811710931", "15763465100", "15563409261", "17563413040", "13605448887", "13863423185", "15020853769", "13863453653", "15606347776", "15336342078", "15263494028", "15063421390", "15725891508", "13863420405", "18663469345", "13963499543", "17686396391", "18663496687", "15064308065", "13696349798", "17563409794", "15066347895", "13863493675", "13656342612", "13561726661", "15253863516", "15154098191", "13563495992", "18596347186", "13963498018", "18663499901", "13626344660", "15063420605", "15336346338", "18563417659", "15965523694", "15163455585", "15725722705", "13863498257", "16776231234", "15066349460", "18763495859", "15589387232", "18506349444", "15506341117", "15063425776", "16252171234", "13356340831", "15066346397", "15589352405", "17863538125", "15163439829", "13561734155", "18963429279", "18263438065", "17606344445", "13626342721", "13054823721", "15550388216", "15588926623", "15163402170", "15063427685", "18263440332", "13696345266", "13156056595");
        for (String string : strings) {
            try {
                String resp = HttpRequest.get(delbranchCache + string).body("utf-8");
                System.out.println(resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 删除工贸监管人员  何梦华
    private static void delDzUser() {
        List<Long> userIds = Arrays.asList(179770L, 707248L, 927832L, 927833L, 927834L, 927835L, 941032L, 941033L, 941034L, 941035L);

        for (Long userId : userIds) {
            String resp = HttpRequest.post(delDzUser + userId).contentType("application/json").send("{}").body("utf-8");
            System.out.println(userId + "::::" + resp);
        }

    }

    // 危化品删除员工 何梦华
    private static void delWhpUser() {
        List<Long> sdyjWhpUserIds = Arrays.asList(344493L,484730L,484731L);
        for (Long userId : sdyjWhpUserIds) {
            String resp = HttpRequest.post(delWhpUser + userId).contentType("application/json").send("{}").body("utf-8");
            System.out.println(userId + "::::" + resp);
        }
    }



    // 删除工贸实名认证学员
    private static void delGmSmuser() {
        List<Long> gnUserIdList = Arrays.asList(2675996L,2800707L,2800708L,2800709L,2800710L,2800711L);
        int size = gnUserIdList.size();
        int pageSize = 10;
        int a = size % pageSize;
        int i = size / pageSize;
        if (a > 0) {
            i++;
        }
        for (int j = 0; j < i; j++) {
            List<Long> del;
            if ((j + 1) * pageSize >= size) {
                del = gnUserIdList.subList(j * pageSize, size);
            } else {
                del = gnUserIdList.subList(j * pageSize, j * pageSize + pageSize);
            }
            if (CollectionUtils.isEmpty(del)) {
                return;
            }

            try {
                String resp = HttpRequest.post(delGmSmuser).contentType("application/json").send(JSON.toJSONString(del)).body("utf-8");
                System.out.println(resp);
            } catch (Exception e) {
                System.out.println(JSON.toJSONString(del));
                e.printStackTrace();
                return;
            }
        }
    }

    // 更该企业类型
    private static void branchTypeChange() {
        List<String> strings = Arrays.asList("54593");
        String enterType = "41";
        boolean needSms = true;
        for (String branchId : strings) {
            String replace = branchTypeChange.replace("{{branchId}}", branchId).replace("{{enterType}}", enterType);
            try {
                String resp = HttpRequest.get(replace + needSms).body("utf-8");
                System.out.println(replace + needSms);
                System.out.println(resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 删除工贸机构  何梦华
    private static void delGMBranch() {
        List<Long> branchIds = Arrays.asList(75142L);

        for (Long branchId : branchIds) {
            String resp = HttpRequest.post(delGMbranch + branchId).contentType("application/json").send("{}").body("utf-8");
            System.out.println(branchId + "::::" + resp);
        }

    }

    // 删除危化机构
    private static void delWHBranch() {
        List<Long> branchIds = Arrays.asList(19817L, 19329L, 26415L, 27860L, 26399L, 24792L, 26384L, 26403L, 29828L, 24812L, 25140L, 25517L, 27349L, 27715L, 27619L, 27618L, 28043L, 29839L, 32001L, 34749L, 34966L);

        for (Long branchId : branchIds) {
            String resp = HttpRequest.post(delWhpBranch + branchId).contentType("application/json").send("{}").body("utf-8");
            System.out.println(branchId + "::::" + resp);
        }
    }


}
