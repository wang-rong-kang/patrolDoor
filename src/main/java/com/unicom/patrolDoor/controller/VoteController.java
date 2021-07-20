package com.unicom.patrolDoor.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicom.patrolDoor.entity.User;
import com.unicom.patrolDoor.entity.Vote;
import com.unicom.patrolDoor.entity.VoteLog;
import com.unicom.patrolDoor.service.UserService;
import com.unicom.patrolDoor.service.VoteLogService;
import com.unicom.patrolDoor.service.VoteService;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author wrk
 * @Date 2021/6/15 15:21
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/vote")
public class VoteController {
    private static final Logger log = LogManager.getLogger(QuestionController.class);

    @Resource
    private VoteService voteService;

    @Resource
    private VoteLogService voteLogService;

    @Resource
    private UserService userService;

    //添加投票
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(@RequestBody Map<Object, Object> map) {
        log.info("------------------/vote/add-------------begin---------");
        Result result = new Result();
        Vote vote = new Vote();
        if (map.get("theme") != null && !(map.get("theme").equals(""))) {
            vote.setTheme(map.get("theme").toString());
        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("主题不能为空");
            return result;
        }
        if (map.get("option") != null && !(map.get("option").equals(""))) {
            List<String> strings = (List<String>) map.get("option");
            if (strings.size() == 0) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("选项不能为空");
                return result;
            }
            StringBuffer buffer = new StringBuffer();
            StringBuffer subscript = new StringBuffer();
            for (int i = 0; i < strings.size(); i++) {
                buffer.append(strings.get(i) + ",");
                subscript.append((i + 1) + ",");
            }

            vote.setOpt(buffer.toString().substring(0, buffer.toString().length() - 1));
            vote.setSubscript(subscript.toString().substring(0, subscript.toString().length() - 1));

        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("选项不能为空");
            return result;
        }
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            vote.setUserId(Integer.parseInt(map.get("userId").toString()));
        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("主题不能为空");
            return result;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        vote.setCreateTime(time);
        if (map.get("startNow") != null && !(map.get("startNow").equals(""))) {
            if (Integer.parseInt(map.get("startNow").toString()) == 1) {
                vote.setBeginTime(time);
                vote.setVoteStatus(3);
            } else {
                vote.setBeginTime(map.get("beginTime").toString());
            }
        }
        if (map.get("endTime") != null && !(map.get("endTime").equals(""))) {
            vote.setEndTime(map.get("endTime").toString());
        }
        vote.setCreateTime(time);
        try {
            voteService.insert(vote);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("添加成功");
            log.info("------------------/vote/add-------------end---------");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("添加失败");
            log.info("------------------/vote/add-------------end---------");
        }
        return result;
    }


    //查看投票
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public Result select(@RequestBody Map<Object, Object> map) {
        log.info("------------------/vote/select-------------begin---------");
        Result result = new Result();
        List<Map<Object, Object>> mapList = new ArrayList<>();
        try {
            Vote vote = voteService.selectById(Integer.parseInt(map.get("id").toString()));
            if (StringUtils.isNotBlank(vote.getOpt())) {
                String[] strings = vote.getOpt().split(",");
                if (strings.length > 0) {
                    for (int i = 0; i < strings.length; i++) {
                        Map optMap = new HashMap();
                        optMap.put("subscript", (i + 1));//标识
                        optMap.put("option", strings[i]);//选项
                        List<VoteLog> list = voteLogService.selectByOptionAndVoteId(Integer.parseInt(map.get("id").toString()), String.valueOf(i + 1));
                        if (list.size() > 0) {
                            optMap.put("optionNum", list.size());//选项 投票数量
                        } else {
                            optMap.put("optionNum", 0);
                        }
                        mapList.add(optMap);
                    }
                }
            }
            User user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
            if ((vote.getUserId() == Integer.parseInt(map.get("userId").toString())) || user.getIsNo() == 1) {
                if (vote.getVoteStatus() == 3) {
                    vote.setEndStop(1);
                } else {
                    vote.setEndStop(0);
                }
            } else {
                vote.setEndStop(0);
            }

            vote.setList(mapList);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("查看成功");
            log.info("------------------/vote/select-------------end---------");
            result.setData(vote);
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("查看失败");
            log.info("------------------/vote/select-------------end---------");
        }
        return result;
    }


    //查看投票列表
    @RequestMapping(value = "/selectList", method = RequestMethod.POST)
    @ResponseBody
    public Result selectList(@RequestBody Map<Object, Object> map) {
        log.info("------------------/vote/selectList-------------begin---------");
        Result result = new Result();
        //判断是不是有已经过期的投票
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String nowTime = simpleDateFormat.format(new Date());
        try {
            voteService.updateVoteStatuByTime(nowTime);
        }catch (Exception e){
            log.info("------------------/vote/selectList-------------修改投票状态失败---------------");
        }

        List<Map<Object, Object>> mapList = new ArrayList<>();
        String theme = "";
        String userName = "";
        String beginTime = "";
        String endTime = "";
        Integer pageNo = 1;
        Integer pageNum = 10;
        String voteStatus = "";
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("theme") != null && !(map.get("theme").equals(""))) {
            theme = map.get("theme").toString();
        }
        if (map.get("userName") != null && !(map.get("userName").equals(""))) {
            userName = map.get("userName").toString();
        }
        if (map.get("beginTime") != null && !(map.get("beginTime").equals(""))) {
            beginTime = map.get("beginTime").toString();
        }
        if (map.get("endTime") != null && !(map.get("endTime").equals(""))) {
            endTime = map.get("endTime").toString();
        }
        if (map.get("voteStatus") != null && !(map.get("voteStatus").equals(""))) {
            voteStatus = map.get("voteStatus").toString();
        }
        PageHelper.startPage(pageNo, pageNum);
        try {
            mapList = voteService.selectList(theme, userName, beginTime, endTime, voteStatus);
            if (mapList.size() > 0) {
                for (Map m : mapList) {
                    User user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
                    if ((Integer.parseInt(m.get("userId").toString()) == user.getId()) || user.getIsNo() == 1) {
                        //添加一个删除的按钮 前提当前投票的创建人和当前登陆人是同一个  或者是当前登陆人是超级管理员
                        m.put("delete", 1);
                    } else {
                        m.put("delete", 0);
                    }
                    /**
                     * 获取当前时间
                     *     当前时间<投票开始时间   将投票状态修改成------未开始
                     *     当前时间>投票时间的结束时间 将投票状态修改成------已结束
                     *     投票开始时间<当前时间<投票结束时间 Or 或者是投票开始时间<当前时间并且结束时间是空------进行中
                     */
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String time = df.format(new Date());
                    //当前时间<投票开始时间------未开始
                    if (timeCompare(time, m.get("beginTime").toString()) < 0) {
                        voteService.updateVoteStatusById(2, Integer.parseInt(m.get("id").toString()));
                    }
                    if (m.get("endTime") != null && !(m.get("endTime").equals(""))) {
                        //当前时间>投票时间的结束时间------已结束
                        if (timeCompare(time, m.get("endTime").toString()) > 0) {
                            voteService.updateVoteStatusById(4, Integer.parseInt(m.get("id").toString()));
                        }
                        //投票开始时间<当前时间<投票结束时间------进行中
                        if (timeCompare(time, m.get("beginTime").toString()) > 0 && timeCompare(time, m.get("endTime").toString()) < 0) {
                            voteService.updateVoteStatusById(3, Integer.parseInt(m.get("id").toString()));
                        }
                    } else {
                        m.put("endTime", "");
//                        //结束时间是空并且投票开始时间<当前时间
//                        if (timeCompare(time, m.get("beginTime").toString()) > 0) {
//                            voteService.updateVoteStatusById(3, Integer.parseInt(m.get("id").toString()));
//                        }
                    }
                }
            }
            PageInfo<Map<Object, Object>> pageInfo = new PageInfo<>(mapList);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("查看成功");
            result.setData(pageInfo);
            log.info("------------------/vote/selectList-------------end---------");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("查看失败");
            log.info("------------------/vote/selectList-------------end---------");
        }
        return result;
    }

    //点击立即结束
    @RequestMapping(value = "/endNow", method = RequestMethod.POST)
    @ResponseBody
    public Result endNow(@RequestBody Map<Object, Object> map) {
        log.info("------------------/vote/endNow-------------begin---------");
        Result result = new Result();
        Vote vote = new Vote();
        if (map.get("id") == null || map.get("id").equals("")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("立即结束失败");
            log.info("------------------/vote/endNow-------------end---------");
            return result;
        } else {
            vote.setId(Integer.parseInt(map.get("id").toString()));
        }
        vote.setVoteStatus(4);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        vote.setEndTime(time);
        try {
            voteService.updateVote(vote);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("立即结束成功");
            log.info("------------------/vote/endNow-------------end---------");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("立即结束失败");
            log.info("------------------/vote/endNow-------------end---------");
        }
        return result;
    }


    //删除投票
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Map<Object, Object> map) {
        log.info("------------------/vote/delete-------------begin---------");
        Result result = new Result();
        try {
            voteService.updateVoteStatusById(1, Integer.parseInt(map.get("id").toString()));
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("删除成功");
            log.info("------------------/vote/delete-------------end---------");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("删除失败");
            log.info("------------------/vote/delete-------------end---------");
        }
        return result;
    }

    //点击投票按钮
    @RequestMapping(value = "/newVoting", method = RequestMethod.POST)
    @ResponseBody
    public Result newVoting(@RequestBody Map<Object, Object> map) {
        log.info("------------------/vote/newVoting-------------begin---------");
        Result result = new Result();
        VoteLog voteLog = new VoteLog();
        if (map.get("voteId") == null || (map.get("voteId").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("投票失败");
            log.info("------------------/vote/newVoting-------------end---------");
            return result;
        } else {
            voteLog.setVoteId(Integer.parseInt(map.get("voteId").toString()));
        }
        if (map.get("userId") == null || (map.get("userId").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("投票失败");
            log.info("------------------/vote/newVoting-------------end---------");

            return result;
        } else {
            voteLog.setUserId(Integer.parseInt(map.get("userId").toString()));
        }
        if (map.get("option") == null || (map.get("option").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("投票失败");
            log.info("------------------/vote/newVoting-------------end---------");
            return result;
        } else {
            voteLog.setOpt(map.get("option").toString());
        }
        List<VoteLog> list = voteLogService.selectByVoteIdAndUserId(Integer.parseInt(map.get("voteId").toString()), Integer.parseInt(map.get("userId").toString()));
        if (list.size() > 0) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("投票失败,只能投票一次");
            log.info("------------------/vote/newVoting-------------end---------");
            return result;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        voteLog.setVoteTime(df.format(new Date()));
        try {
            voteLogService.insert(voteLog);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("投票成功");
            log.info("------------------/vote/newVoting-------------end---------");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("投票失败");
            log.info("------------------/vote/newVoting-------------end---------");
        }
        return result;
    }

    //历史记录
    @RequestMapping(value = "/history", method = RequestMethod.POST)
    @ResponseBody
    public Result history(@RequestBody Map<Object, Object> map) {
        log.info("------------------/vote/history-------------begin---------");
        Result result = new Result();
        List<Map<Object, Object>> mapList = new ArrayList<>();
        String theme = "";
        String beginTime = "";
        String endTime = "";
        String voteStatus = "";
        Integer pageNo = 1;
        Integer pageNum = 10;
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("theme") != null && !(map.get("theme").equals(""))) {
            theme = map.get("theme").toString();
        }
        if (map.get("beginTime") != null && !(map.get("beginTime").equals(""))) {
            beginTime = map.get("beginTime").toString();
        }
        if (map.get("endTime") != null && !(map.get("endTime").equals(""))) {
            endTime = map.get("endTime").toString();
        }

        if (map.get("voteStatus") != null && !(map.get("voteStatus").equals(""))) {
            voteStatus = map.get("voteStatus").toString();
        }
        if (map.get("userId") == null || (map.get("userId").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("查看失败");
            log.info("------------------/vote/history-------------end---------");
            return result;
        }
        PageHelper.startPage(pageNo, pageNum);
        try {
            mapList = voteService.selectHistoryList(theme, Integer.parseInt(map.get("userId").toString()), beginTime, endTime, voteStatus);
            if (mapList.size() > 0) {
                for (Map m : mapList) {
                    if (Integer.parseInt(m.get("voteStatus").toString()) != 1) {//1:已经删除
                        User user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
                        if ((Integer.parseInt(m.get("userId").toString()) == user.getId()) || user.getIsNo() == 1) {
                            //添加一个删除的按钮 前提当前投票的创建人和当前登陆人是同一个  或者是当前登陆人是超级管理员
                            m.put("delete", 1);
                        } else {
                            m.put("delete", 0);
                        }
                        /**
                         * 获取当前时间
                         *     当前时间<投票开始时间   将投票状态修改成------未开始
                         *     当前时间>投票时间的结束时间 将投票状态修改成------已结束
                         *     投票开始时间<当前时间<投票结束时间 Or 或者是投票开始时间<当前时间并且结束时间是空------进行中
                         */
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                        String time = df.format(new Date());
                        //当前时间<投票开始时间------未开始
                        if (timeCompare(time, m.get("beginTime").toString()) < 0) {
                            voteService.updateVoteStatusById(2, Integer.parseInt(m.get("id").toString()));
                        }
                        if (m.get("endTime") != null && !(m.get("endTime").equals(""))) {
                            //当前时间>投票时间的结束时间------已结束
                            if (timeCompare(time, m.get("endTime").toString()) > 0) {
                                voteService.updateVoteStatusById(4, Integer.parseInt(m.get("id").toString()));
                            }
                            //投票开始时间<当前时间<投票结束时间------进行中
                            if (timeCompare(time, m.get("beginTime").toString()) > 0 && timeCompare(time, m.get("endTime").toString()) < 0) {
                                voteService.updateVoteStatusById(3, Integer.parseInt(m.get("id").toString()));
                            }
                        } else {
                            m.put("endTime", "");
//                        //结束时间是空并且投票开始时间<当前时间
//                        if (timeCompare(time, m.get("beginTime").toString()) > 0) {
//                            voteService.updateVoteStatusById(3, Integer.parseInt(m.get("id").toString()));
//                        }
                        }
                    }
                }
            }
            PageInfo<Map<Object, Object>> pageInfo = new PageInfo<>(mapList);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("查看成功");
            result.setData(pageInfo);
            log.info("------------------/vote/history-------------end---------");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("查看失败");
            log.info("------------------/vote/history-------------end---------");
        }
        return result;
    }

    //时间比较
    public static Integer timeCompare(String timeA, String timeB) {
        int res = timeA.compareTo(timeB);
        if (res > 0) {
            res = 1;
        }
        if (res < 0) {
            res = -1;
        }
        if (res == 0) {
            res = 0;
        }
        return res;
    }
}
