package com.ijourney.conbowcontrol;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.ijourney.conbowcontrol.databinding.ActivityChatBinding;
import com.ijourney.p2plib.connect.NearConnect;
import com.ijourney.p2plib.model.Host;

public class ChatActivity extends AppCompatActivity {

    public static final String BUNDLE_PARTICIPANT = "bundle_participant";
    public static final String MSG_PREFIX = "msg:";
    private static final String STATUS_EXIT_CHAT = "status:exit_chat";
    private ActivityChatBinding binding;
    private NearConnect mNearConnect;
    private Host mParticipant;
    private ChatAdapter mChatAdapter;

    public static void start(Activity activity, Host participant) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(BUNDLE_PARTICIPANT, participant);
        activity.startActivityForResult(intent, 1234);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        binding.chatRv.setLayoutManager(new LinearLayoutManager(this));
        mChatAdapter = new ChatAdapter();
        binding.chatRv.setAdapter(mChatAdapter);
        mChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                binding.chatRv.scrollToPosition(positionStart);
            }
        });

        mParticipant = getIntent().getParcelableExtra(BUNDLE_PARTICIPANT);
        setTitle("Controlling " + mParticipant.getName());
        initNearConnect();
        initMessagingUi();
    }

    private void initNearConnect() {
        ArraySet<Host> peers = new ArraySet<>();
        peers.add(mParticipant);
        mNearConnect = new NearConnect.Builder()
                .forPeers(peers)
                .setContext(this)
                .setListener(getNearConnectListener(), Looper.getMainLooper()).build();
        mNearConnect.startReceiving();
    }

    private NearConnect.Listener getNearConnectListener() {
        return new NearConnect.Listener() {
            @Override
            public void onReceive(byte[] bytes, Host sender) {
                if (bytes != null) {
                    String data = new String(bytes);
                    switch (data) {
                        case STATUS_EXIT_CHAT:
                            binding.msgLl.setVisibility(View.GONE);
                            new AlertDialog.Builder(ChatActivity.this)
                                    .setMessage(String.format("%s has left the control.", sender.getName()))
                                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mNearConnect.stopReceiving(true);
                                            ChatActivity.this.setResult(RESULT_OK);
                                            ChatActivity.this.finish();
                                        }
                                    }).create().show();
                            break;
                        default:
                            if (data.startsWith(MSG_PREFIX)) {
                                mChatAdapter.addMessage(data.substring(4), sender.getName());
                            }
                            break;
                    }
                }
            }

            @Override
            public void onSendComplete(long jobId) {
//                update UI with sent status if necessary
            }

            @Override
            public void onSendFailure(Throwable e, long jobId) {
                e.printStackTrace();
            }

            @Override
            public void onStartListenFailure(Throwable e) {
                e.printStackTrace();
            }
        };
    }

    private void initMessagingUi() {
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.msgEt.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mChatAdapter.addMessage(message, "Send TTS message:");
                binding.msgEt.setText("");
            }
        });
    }

    private void sendMessage(String message) {
        mNearConnect.send((MSG_PREFIX + message).getBytes(), mParticipant);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mNearConnect.send(STATUS_EXIT_CHAT.getBytes(), mParticipant);
        mNearConnect.stopReceiving(true);
    }
}
