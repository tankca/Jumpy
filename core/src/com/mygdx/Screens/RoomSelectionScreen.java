package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.mygdx.appwarp.WarpController;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;

import java.util.HashMap;

/**
 * Created by user on 11/3/2016.
 */
public class RoomSelectionScreen extends AbstractScreen{

    private WarpClient warpClient;

    private final TextButton buttonCreateRoom;
    private final TextButton buttonRefreshRoom;
    private final TextButton buttonConnectRoom;
    private final TextField textNewRoom;
    private final Label labelWelcome;
    private final Label labelNewRoom;
    private final Label labelRoomList;
    private final List listRooms;

    HashMap<String,String> roomMap;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public RoomSelectionScreen() {
        getWarpClient();
        // look for rooms with 1 to 3 players already
        // consider running a Thread to pull info on new rooms
        warpClient.getRoomInRange(0, 3);
        RoomData[] roomDataList = WarpController.getRoomDatas();
        buttonCreateRoom = new TextButton("Create Room",skin);
        buttonCreateRoom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String text = textNewRoom.getText();
                if (text.length() > 0) {
                    System.out.println("New Room " + text + " is created.");
                    warpClient.createRoom(text, WarpController.getLocalUser(), 4, null);
                    ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);
                }
                return false;
            }
        });
        buttonRefreshRoom = new TextButton("Refresh",skin);
        buttonRefreshRoom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                warpClient.getRoomInRange(0, 3);
                RoomData[] roomDataList = WarpController.getRoomDatas();
                addRoomToList(roomDataList);
                return false;
            }
        });
        buttonConnectRoom = new TextButton("Connect Room",skin);
        buttonConnectRoom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (listRooms.getSelected() != null){
                    String selected = (String) listRooms.getSelected();
                    String roomName = selected.substring(4);
                    String roomId = roomMap.get(roomName);
                    System.out.println("Joining Room " + roomId + ".");
                    warpClient.subscribeRoom(roomId);
                    ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);
                }
                return false;
            }
        });
        textNewRoom = new TextField("",skin);
        labelNewRoom = new Label("New Room:",skin);
        labelWelcome = new Label("Welcome, " + WarpController.getLocalUser(),skin);
        labelRoomList = new Label("Room List", skin);
        listRooms = new List(skin);
        addRoomToList(roomDataList);
//        listRooms.setItems("ISTD 3/4","EPD 2/4","ESD 3/4","ASD 4/4");
        buildStage();
    }

    private void getWarpClient(){
        try {
            warpClient = WarpClient.getInstance();
        } catch (Exception ex) {
            System.out.println("Fail to get warpClient");
        }
    }

    @Override
    public void buildStage() {
        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.setDebug(true);

        table.add(labelWelcome).colspan(2).pad(1);
        table.row();
        table.add(labelNewRoom).pad(5);
        table.add(textNewRoom).pad(5);
        table.row();
        table.add(buttonCreateRoom).colspan(2).pad(5);

        table.row();
        table.add(labelRoomList).colspan(2).space(30);
        table.row();
        table.add(listRooms).colspan(2);
        table.row();
        table.add(buttonRefreshRoom).colspan(2).space(10);
        table.row();
        table.add(buttonConnectRoom).colspan(2).space(10);
        addActor(table);
    }

    public void addRoomToList(RoomData[] roomDatas){
        roomMap = new HashMap<String, String>();
        if (roomDatas != null){
            String[] roomList = new String[roomDatas.length];
            for (int i = 0;i<roomDatas.length;i++){
                RoomData roomData = roomDatas[i];
                roomMap.put(roomData.getName(),roomData.getId());
                roomList[i] = "Rm: " + roomData.getName();
            }
            listRooms.setItems(roomList);
        }
    }
}