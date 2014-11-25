package org.maxwe.android.im;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.maxwe.json.Json;
import org.maxwe.json.JsonArray;
import org.maxwe.json.JsonObject;

import java.net.URI;


/**
 * @Description: [Description]
 * @Author: [DingPengwei]
 * @Email: [www.dingpengwei@gmail.com]
 * @CreateDate: [11/21/14 13:57]
 * @UpdateUser: [dingpengwei]
 * @UpdateDate: [11/21/14 13:57]
 * @UpdateRemark: [UpdateRemark]
 * @Version: [v1.0]
 */
public class MainActivity extends Activity implements View.OnClickListener,WebSocketClient.OnWebSocketCallBack{

    private EditText et_main_name;
    private EditText et_main_message;
    private ListView lv_main_messages;
    private MessageAdapter messageAdapter;
    private JsonArray messages = Json.createJsonArray();

    private WebSocketClient webSocketClient;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            messageAdapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        this.setContentView(R.layout.main_layout);
        this.et_main_name = (EditText) this.findViewById(R.id.et_main_name);
        this.et_main_message = (EditText) this.findViewById(R.id.et_main_message);
        this.lv_main_messages = (ListView) this.findViewById(R.id.lv_main_messages);
        this.lv_main_messages.setAdapter(this.messageAdapter = new MessageAdapter());


        try{
            webSocketClient = new WebSocketClient(new URI( "ws://im.maxwe.org:1121" ),this);
            webSocketClient.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_main_sender :
                String name = et_main_name.getText().toString();
                String message = et_main_message.getText().toString();
                if(name.trim().equals("")){
                    Toast.makeText(this,"name can`t be null",Toast.LENGTH_LONG).show();
                }else
                if(message.trim().equals("")){
                    Toast.makeText(this,"message can`t be null",Toast.LENGTH_LONG).show();
                }else {
                    webSocketClient.send(Json.createJsonObject().set("name", name).set("message", message).toString());
                    this.et_main_message.setText("");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        webSocketClient.close();
    }

    @Override
    public void onOpen() {
//        Toast.makeText(this,"open",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessage(String message) {
        messages.push(Json.parse(message));
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onClose() {
//        Toast.makeText(this,"close",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Exception ex) {
//        Toast.makeText(this,"error",Toast.LENGTH_LONG).show();
    }

    class MessageAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return messages.getLenght();
        }

        @Override
        public Object getItem(int position) {
            return messages.getJsonObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JsonObject message = messages.get(position);
            TextView messageView = new TextView(MainActivity.this);

            if(message.getString("name").equals(et_main_name.getText().toString())){
                messageView.setGravity(Gravity.RIGHT);
                messageView.setText(message.getString("message"));
            }else{
                messageView.setGravity(Gravity.LEFT);
                messageView.setText(message.getString("name") + " say:" + message.getString("message"));
            }
            return messageView;
        }
    }
}
