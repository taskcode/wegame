package com.wegame.wegameliveserviceall.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.wegame.wegameliveserviceall.base.flower.Player;
import com.wegame.wegameliveserviceall.base.flower.calculator.impl.FlowerValueCalculator;
import com.wegame.wegameliveserviceall.base.flower.calculator.impl.NonFlowerValueCalculator;
import com.wegame.wegameliveserviceall.base.flower.compare.PlayerComparator;
import com.wegame.wegameliveserviceall.base.flower.provider.PlayerProvider;
import com.wegame.wegameliveserviceall.base.flower.provider.impl.LimitedPlayerProvider;
import com.wegame.wegameliveserviceall.entity.Room;
import com.wegame.wegameliveserviceall.entity.Seat;
import com.wegame.wegameliveserviceall.repository.RoomRepository;
import com.wegame.wegameliveserviceall.repository.SeatRepository;
import com.wegame.wegameliveserviceall.service.FriedFlowerService;
import com.wegame.wegameliveserviceall.base.flower.FriedFlowerJsonObject;
import com.wegame.wegameliveserviceall.vo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName：FriedFlowerServiceImpl
 * @Description: 即时通讯
 * @Author：13738700108
 * @Data 2020/2/12 14:41
 * @Version: v1.0
 **/
@Service
public class FriedFlowerServiceImpl implements FriedFlowerService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public void sendMessage(Message message) {

        template.convertAndSend("/friedFlower/one", FriedFlowerJsonObject.userOutRoom("111", "aaa"));
    }

    @Override
    public Room findRoomMessageByRoomSerial(int roomSerial) {
        Room room = roomRepository.findRoomMessageByRoomSerial(roomSerial);
        return room;
    }

    @Override
    public List<Seat> findSeatsByRoom(Room room) {
        List<Seat> seats = seatRepository.findSeatsByRoom(room);
        return seats;
    }

    @Override
    public void sendUserInToRoomMessage(Map<String, Object> msg) {
        template.convertAndSend("/friedFlower/" + FriedFlowerJsonObject.serial(Integer.parseInt(msg.get("type").toString())),
                FriedFlowerJsonObject.userInToRoom(Integer.parseInt(msg.get("type").toString()),
                        msg.get("userId").toString(),
                        msg.get("userImg").toString(),
                        msg.get("userNickName").toString(),
                        Integer.parseInt(msg.get("integral").toString())));
    }

    @Override
    public void sendRoomMessage(Room room, Map<String, Object> msg) {

        System.out.println("11：" + room.getStatus().ordinal());
        /* 0 未开始  1 一开始*/
        List<JSONObject> objects = seatRepository.findRoomInfo(room.getSerial());
        System.out.println("objects:" + objects);
        template.convertAndSend("/friedFlowerServer/" + FriedFlowerJsonObject.serial(Integer.parseInt(msg.get("roomSerial").toString())),
                FriedFlowerJsonObject.room(Integer.parseInt(msg.get("type").toString())-1 , objects));
    }

    @Override
    public int selSeatHavePeople(int roomSerial, int seatSerial) {
        return roomRepository.selSeatHavePeople(roomSerial, seatSerial);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int SaveUserSitDown(int userCode, int seatSerial,int roomSerial) {
        return seatRepository.saveUserSitDown(userCode,seatSerial,roomSerial);
    }

    @Override
    public void sendUserSitDown(int type, int roomSerial, int userCode, int seatSerial, String userImg, String userNickName, int integral) {
        template.convertAndSend("/friedFlowerServer/" + FriedFlowerJsonObject.serial(roomSerial),
                FriedFlowerJsonObject.userSitDown(type, userCode, seatSerial, userImg,userNickName,integral));
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int saveUserSetOut(int roomSerial, int userCode, int seatSerial) {
        return  seatRepository.saveUserSetOut(roomSerial,userCode,seatSerial);

    }


    @Override
    public void sendUserSetOut(int type, int roomSerial, int seatSerial) {
        template.convertAndSend("/friedFlowerServer/" + FriedFlowerJsonObject.serial(roomSerial),
                FriedFlowerJsonObject.userSetOut(type,seatSerial));
    }

    @Override
    public Map<String,Object> selGmaeStartCondition(int roomSerial) {
        return  seatRepository.selGmaeStartCondition(roomSerial);

    }

    @Override
    public void sendAndSaveGmaeStart(int roomSerial, int isSetOut) {
        //使用有人数下限制的发牌器
        PlayerProvider playerProvider = new LimitedPlayerProvider();
        //使用花色不参与牌大小比较的计算器
        PlayerComparator playerComparator = new PlayerComparator(new NonFlowerValueCalculator());
        //创建多副牌
        List<Player> players = playerProvider.getPlayers(isSetOut);

    }


}
