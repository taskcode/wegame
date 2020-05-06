package com.wegame.wegameliveserviceall.controller;

import com.wegame.wegameliveserviceall.base.JsonResult;
import com.wegame.wegameliveserviceall.entity.Room;
import com.wegame.wegameliveserviceall.service.FriedFlowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName：FriedFlowerPortController
 * @Description: 炸金花接口
 * @Author：13738700108
 * @Data 2020/3/20 22:10
 * @Version: v1.0
 **/
@RestController
@RequestMapping("/friedFlowerPort")
public class FriedFlowerPortController {

    @Autowired
    private FriedFlowerService ffs;

    /**
     * @api {POST} /friedFlowerPort/userSitDown 坐下
     * @apiGroup friedFlowerPort
     * @apiVersion 1.0.0
     * @apiDescription 坐下
     * @apiParam {int} type 类型
     * @apiParam {int} roomSerial 房间序号
     * @apiParam {int} userCode 用户code
     * @apiParam {int} seatSerial 座位序号
     * @apiParam {String} userImg 用户头像
     * @apiParam {String} userNickName 用户昵称
     * @apiParam {int} integral 积分
     * @apiParamExample {json} 请求样例：
     *                /friedFlowerPort/userSitDown?type=3&roomSerial=1&userCode=123&seatSerial=3&userImg="11"&userNickName="wang"&integral=11
     * @apiSuccess (200) {String} code 200:成功</br>
     *                                 401:该位置已经有人坐下了</br>
     *                                 402:被人捷足先登</br>
     * @apiSuccess (200) {String} message 信息
     * @apiSuccess (200) {String} data 返回用户信息
     * @apiSuccessExample {json} 返回样例:
     * {
     * 	"code": 200,
     * 	"message": "成功坐下",
     * 	"data": "{}"
     * }
     */
    @PostMapping("/userSitDown")
    public JsonResult userSitDown(int type,
                                  int roomSerial,
                                  int userCode,
                                  int seatSerial,
                                  String userImg,
                                  String userNickName,
                                  int integral){
        //查询这个座位是否有人
        int num = ffs.selSeatHavePeople(roomSerial,seatSerial);
        if(0<num){
            return JsonResult.failure(401,"该位置已经有人坐下了");
        }else{
            int mif = ffs.SaveUserSitDown(userCode,seatSerial,roomSerial);
            if (0<mif){
                ffs.sendUserSitDown(type,roomSerial,userCode,seatSerial,userImg,userNickName,integral);
                return  JsonResult.success("成功坐下");
            }else{
                return JsonResult.failure(402,"被人捷足先登");
            }
        }
    }

    /**
     * @api {POST}  /friedFlowerPort/userSetOut 准备
     * @apiGroup friedFlowerPort
     * @apiVersion 1.0.0
     * @apiDescription 准备
     * @apiParam {int} id 类型
     * @apiParam {int} id 房间序号
     * @apiParam {int} id 用户code
     * @apiParam {int} id 座位序号
     * @apiParamExample {json} 请求样例：
     *               /friedFlowerPort/userSetOut?type=3&roomSerial=1&userCode=123&seatSerial=3
     * @apiSuccess (200) {String} code 200:成功</br>
     *                                 401:准备失败</br>
     * @apiSuccess (200) {String} message 信息
     * @apiSuccess (200) {String} data 返回用户信息
     * @apiSuccessExample {json} 返回样例:
     * {
     * 	"code": 200,
     * 	"message": "准备成功",
     * 	"data": "{}"
     * }
     */
    @PostMapping("/userSetOut")
    public JsonResult userSetOut(int type,
                                 int roomSerial,
                                 int userCode,
                                 int seatSerial){
        int num =  ffs.saveUserSetOut(roomSerial,userCode,seatSerial);
        if (0<num){
            //发送即时通讯有人准备
            ffs.sendUserSetOut(type,roomSerial,seatSerial);
            //查询 是否满足开始游戏条件
            Map<String,Object> countMap= ffs.selGmaeStartCondition(roomSerial);
            //如果等于0 说明进入房间的人都准备了 要开始游戏
            int isSetOut= Integer.parseInt(String.valueOf(countMap.get("isSetOut")));
            int isAllOut= Integer.parseInt(String.valueOf(countMap.get("isAllOut")));
            if(isSetOut==isAllOut){
                //发牌游戏开始
                 ffs.sendAndSaveGmaeStart(roomSerial, isSetOut);
            }
            return JsonResult.success("准备成功");
        }else{
            return JsonResult.failure(401,"准备失败");
        }
    }
}
