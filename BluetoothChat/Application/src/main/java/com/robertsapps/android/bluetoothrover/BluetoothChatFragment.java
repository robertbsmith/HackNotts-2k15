/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.robertsapps.android.bluetoothrover;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.robertsapps.android.common.logger.Log;

import java.util.Arrays;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {
    public static char[] midiFile = new char[] {
            0x90,69, 2,88, 0x80, 0x90,72, 2,88, 2,88, 0x80, 0x90,74, 2,88, 0x80, 0x90,76,
            2,88, 1,44, 0x80, 0x90,77, 1,44, 0x80, 0x90,76, 2,88, 0x80, 0x90,74, 2,88, 2,88,
            0x80, 0x90,71, 2,88, 0x80, 0x90,67, 2,88, 1,44, 0x80, 0x90,69, 1,44, 0x80, 0x90,71, 2,88,
            0x80, 0x90,72, 2,88, 2,88, 0x80, 0x90,69, 2,88, 0x80, 0x90,69, 2,88, 1,44, 0x80, 0x90,68,
            1,44, 0x80, 0x90,69, 2,88, 0x80, 0x90,71, 2,88, 2,88, 0x80, 0x90,68, 2,88, 0x80, 0x90,64,
            2,88, 1,245, 0x80, 0,98, 0x90,69, 2,88, 0x80, 0x90,72, 2,88, 2,88, 0x80, 0x90,74,
            2,88, 0x80, 0x90,76, 2,88, 1,44, 0x80, 0x90,77, 1,44, 0x80, 0x90,76, 2,88, 0x80, 0x90,74,
            2,88, 2,88, 0x80, 0x90,71, 2,88, 0x80, 0x90,67, 2,88, 1,44, 0x80, 0x90,69, 1,44,
            0x80, 0x90,71, 2,88, 0x80, 0x90,72, 2,88, 1,44, 0x80, 0x90,71, 1,44, 0x80, 0x90,69, 2,88,
            0x80, 0x90,68, 2,88, 1,44, 0x80, 0x90,66, 1,44, 0x80, 0x90,68, 2,88, 0x80, 0x90,69, 2,88,
            2,88, 2,88, 0x80, 0x90,69, 6,165, 0x80, 0,98, 0x90,79, 7,8, 0x80, 0x90,79, 3,132,
            0x80, 0x90,77, 1,44, 0x80, 0x90,76, 2,88, 0x80, 0x90,74, 4,176, 0x80, 0x90,71, 2,88, 0x80,
            0x90,67, 3,132, 0x80, 0x90,69, 1,44, 0x80, 0x90,71, 2,88, 0x80, 0x90,72, 4,176, 0x80, 0x90,69,
            2,88, 0x80, 0x90,69, 3,132, 0x80, 0x90,68, 1,44, 0x80, 0x90,69, 2,88, 0x80, 0x90,71, 4,176,
            0x80, 0x90,68, 2,88, 0x80, 0x90,64, 6,165, 0x80, 0,98, 0x90,79, 7,8, 0x80, 0x90,79, 3,132,
            0x80, 0x90,77, 1,44, 0x80, 0x90,76, 2,88, 0x80, 0x90,74, 4,176, 0x80, 0x90,71, 2,88, 0x80,
            0x90,67, 3,132, 0x80, 0x90,69, 1,44, 0x80, 0x90,71, 2,88, 0x80, 0x90,72, 2,88, 1,44,
            0x80, 0x90,71, 1,44, 0x80, 0x90,69, 2,88, 0x80, 0x90,68, 2,88, 1,44, 0x80, 0x90,66, 1,44,
            0x80, 0x90,68, 2,88, 0x80, 0x90,69, 2,88, 2,88, 2,88, 0x80, 0x90,69, 6,165, 0x80, 0,98,
            0xf0};

    private boolean cruiseControlStatus = false;
    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    private Boolean btStatus = false;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    ImageView centre;
    Image centreImage;
    ImageButton buttonUp, buttonDown, buttonLeft, buttonRight;
    private Switch CruiseControlSwitch;
    private ImageButton lights, music;

    private Boolean lightsStatus = false;
    private Boolean musicStatus = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    public void setListeners(){

        CruiseControlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
            if(btStatus) {
                if (isChecked) {
                    sendMessage("C");
                    cruiseControlStatus = true;
                } else {
                    sendMessage("C");
                    cruiseControlStatus = false;
                }
            } else
            {
                CruiseControlSwitch.setChecked(false);
            }
            }
        });
        lights.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(btStatus) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (lightsStatus) {
                            lights.setImageResource(R.drawable.lights);
                            lightsStatus = false;
                        } else {

                            lights.setImageResource(R.drawable.light_on);
                            lightsStatus = true;
                        }
                        sendMessage("F");
                        return true;
                    }
                }
                    return true;
            }
        });

        music.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(btStatus) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (musicStatus) {
                            music.setImageResource(R.drawable.musicoff);
                            musicStatus = false;
                            sendMessage("s");
                        } else {

                            music.setImageResource(R.drawable.musicon);
                            musicStatus = true;
                            sendMessage("S");
                            byte[] temp = new byte[]{(byte) ((midiFile.length & 0xFF00) >> 8), (byte) (midiFile.length & 0xff)};

                            if(midiFile.length > 64)
                            {
                                char[] temp2 = Arrays.copyOf(midiFile, 64);
                                //sendMessage(new String(new byte[] {0,64}));
                                //sendMessage(new String(temp2));
                            }
                            //else
                                //sendMessage(new String(temp));
                                //sendMessage(new String(midiFile));

                            //sendMessage("0");


                        }

                        return true;
                    }
                }
                return true;
            }
        });
        buttonUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(btStatus) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        centre.setImageResource(R.drawable.movedsticku);
                        sendMessage("1");
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        centre.setImageResource(R.drawable.centre);
                        sendMessage("0");
                        return true;
                    }
                }
                return true;
            }
        });
        buttonDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(btStatus) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        centre.setImageResource(R.drawable.movedstickd);
                        sendMessage("4");
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        centre.setImageResource(R.drawable.centre);
                        sendMessage("0");
                        return true;
                    }
                }
                    return true;

            }
        });
        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(btStatus) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        centre.setImageResource(R.drawable.movedstickl);
                        sendMessage("2");
                        sendMessage("L");
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        centre.setImageResource(R.drawable.centre);
                        sendMessage("0");
                        return true;
                    }
                }
                return true;
            }
        });
        buttonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(btStatus) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        centre.setImageResource(R.drawable.movedstickr);
                        sendMessage("3");
                        sendMessage("R");
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        centre.setImageResource(R.drawable.centre);
                        sendMessage("0");
                        return true;
                    }
                }
                return true;
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        centre = (ImageView) getView().findViewById(R.id.stickImage);
        buttonUp = (ImageButton)getView().findViewById(R.id.ButtonUp);
        buttonDown = (ImageButton)getView().findViewById(R.id.ButtonDown);
        buttonLeft = (   ImageButton)getView().findViewById(R.id.ButtonLeft);
        buttonRight = (ImageButton)getView().findViewById(R.id.ButtonRight);
        CruiseControlSwitch = (Switch)getView().findViewById(R.id.CruiseControl);
        CruiseControlSwitch.setChecked(false);
        lights = (ImageButton)getView().findViewById(R.id.lights);
        music = (ImageButton)getView().findViewById(R.id.music);

        setListeners();
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

//        // Initialize the array adapter for the conversation thread
//        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);
//
//        mConversationView.setAdapter(mConversationArrayAdapter);
//
//        // Initialize the compose field with a listener for the return key
//        mOutEditText.setOnEditorActionListener(mWriteListener);
//
//        // Initialize the send button with a listener that for click events
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Send a message using content of the edit text widget
//                View view = getView();
//                if (null != view) {
//                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
//                    String message = textView.getText().toString();
//                    sendMessage(message);
//                }
//            }
//        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
        }
    }

//    /**
//     * The action listener for the EditText widget, to listen for the return key
//     */
//    private TextView.OnEditorActionListener mWriteListener
//            = new TextView.OnEditorActionListener() {
//        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//            // If the action is a key-up event on the return key, send the message
//            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
//                String message = view.getText().toString();
//                sendMessage(message);
//            }
//            return true;
//        }
//    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            btStatus = true;
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                        default:
                            btStatus = false;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG,readMessage);
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentActivity activity = getActivity();
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
            case R.id.help: {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                final TextView et = new TextView(activity);
                et.setText(getResources().getString(R.string.intro_message));
                et.setPadding(7, 7, 7, 7);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                return true;
            }
            case R.id.about: {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                final TextView et = new TextView(activity);
                et.setText(getResources().getString(R.string.about_message));
                et.setPadding(7,7,7,7);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                return true;
            }
        }
        return false;
    }

}
