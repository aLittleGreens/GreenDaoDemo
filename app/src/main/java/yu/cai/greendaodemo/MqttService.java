package yu.cai.greendaodemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.List;

import bean.Event;
import utils.ThreadTask;

public class MqttService extends Service{

	// this is the log tag
	private static final String TAG = "MQTTPushService2";
	// public static final String BROKER_URL = "tcp://120.24.166.83:1883";
	/**
	 * 服务器地址
	 */
	private static String BROKER_URL = null;
	
	/**
	 * 设置服务器是否应该在重新连接时记住客户端的状态
	 * 当CleanSession设置为false时，当客户端未连接时，MQTT服务器将代表客户端存储离线消息
	 * 下一次客户端连接相同的客户端ID 服务器将把存储的消息传递给客户机
	 */
	private static boolean MQTT_CLEAN_START = false;
	
	/**
	 * 按KeepAlive周期定时发送2字节的PINGREQ心跳报文，服务端收到PINGREQ报文后，回复2字节的PINGRESP报文。
	 * 客户端检测服务器是否不再可用,客户端发送一个非常小的“ping”消息，服务器将确认该消息,买每个间隔内至少有一条消息
	 */
	private static short MQTT_KEEP_ALIVE = 30;
	
	/**
	 * 客户端等待建立到MQTT服务器的网络连接的最长时间间隔。默认超时时间为30秒。
	 */
	private static final int MQTT_TIMEOUT = 10;
	
	private static int[] MQTT_QUALITIES_OF_SERVICE = { 2 };//订阅多个主题时，可用数组来装消息质量
	/**
	 * 消息质量设为2，表示只接收一次消息
	 * 消息质量设为1，表示至少接收一次消息
	 * 消息质量设为0，最多一次，不能接受离线消息
	 */
	private static int MQTT_QUALITY_OF_SERVICE = 2;
	
	/**
	 * retained - 该消息是否应由服务器保留。
	 * 保留消息会驻留在消息服务器，后来的订阅者订阅主题时仍可以接收该消息。
	 */
	private static boolean MQTT_RETAINED_PUBLISH = false;

	private static String MQTT_CLIENT_ID = "mtClient";

	// These are the actions for the service (name are descriptive enough)
	private static final String ACTION_START = MQTT_CLIENT_ID + ".START";
	private static final String ACTION_STOP = MQTT_CLIENT_ID + ".STOP";
	private static final String ACTION_KEEPALIVE = MQTT_CLIENT_ID
			+ ".KEEP_ALIVE";
	private static final String ACTION_RECONNECT = MQTT_CLIENT_ID
			+ ".RECONNECT";


	// Connectivity manager to determining, when the phone loses connection
	private ConnectivityManager mConnMan;

	/**
	 * mqtt连接标示
	 */
	private boolean mStarted;

	/**
	 *  AlarmManager 心跳时间间隔
	 */
	private static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 6;// 1000 * 60 *
																	// 28

	// Retry intervals, when the connection is lost.
	private static final long INITIAL_RETRY_INTERVAL = 1000 * 10;
	private static final long MAXIMUM_RETRY_INTERVAL = 1000 * 60;// 1000 *
																	// 60 *
																	// 30

	// Preferences instance
	private SharedPreferences mPrefs;
	// We store in the preferences, whether or not the service has been started
	private static final String PREF_STARTED = "isStarted";
	// We also store the deviceID (target)
	private static final String PREF_DEVICE_ID = "deviceID";
	// We store the last retry interval
	private static final String PREF_RETRY = "retryInterval";

	// Notification title
	private static String NOTIF_TITLE = "Tokudu";
	// Notification id
	// private static final int NOTIF_CONNECTED = 0;

	// This is the instance of an MQTT connection.
	private MQTTConnection mConnection;
	private long mStartTime;
	private Context mContext;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private String mMsg = "";
	/**
	 * 用户名，用来设置cientID
	 */


	private String userName = "admin";
	private String passWord = "password";

	int index = 1000;
	/**
	 * 此类实现非阻塞IMqttAsyncClient客户端接口，后台线程上完成MQTT操作时携带工作
	 */
	private MqttAsyncClient mqttClient;
	private MqttConnectOptions options;
	private long interval = 1000 * 10;
	MediaPlayer mPlayer = null;

	/**
	 * 是否退出服务标志
	 */
	private boolean isExitService = false;
	private String subscribe;
	private String publish;

	// Static method to start the service
	public static void actionStart(Context ctx) {
		Intent i = new Intent(ctx, MqttService.class);
		// i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		i.setAction(ACTION_START);
		ctx.startService(i);
//		BROKER_URL = "tcp://"
//				+ AppConfig.getInstance().getString(Const.BD_IP,
//						mActivity.getString(R.string.defaultBdIp)) + ":1883";
		
		BROKER_URL ="tcp://172.16.8.160:1883" ;
		// ctx.bindService(i, connection, BIND_AUTO_CREATE);
	}

	// Static method to stop the service
	public static void actionStop(Context ctx) {
		Intent i = new Intent(ctx, MqttService.class);
		i.setAction(ACTION_STOP);
		ctx.startService(i);
	}

	// Static method to send a keep alive message
	public static void actionPing(Context ctx) {
		Intent i = new Intent(ctx, MqttService.class);
		i.setAction(ACTION_KEEPALIVE);
		ctx.startService(i);
	}

	public static boolean isServiceRunning(Context context, String className) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = activityManager
				.getRunningServices(0x7FFFFFFF);
		if (infos == null || infos.size() == 0)
			return false;
		for (RunningServiceInfo info : infos) {
			if (className.equals(info.service.getClassName()))
				return true;
		}
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate, creating service...");
		mStartTime = System.currentTimeMillis();

		// Get instances of preferences, connectivity manager and notification
		// manager
		mPrefs = getSharedPreferences("msg_db",
				Activity.MODE_PRIVATE);
		mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		/*
		 * If our process was reaped by the system for any reason we need to
		 * restore our state with merely a call to onCreate. We record the last
		 * "started" value and restore it here if necessary.
		 */
		initOption();
		// handleCrashedService();

		mContext = this;
		subscribe = mPrefs.getString("subscribe","cyk");
		publish = mPrefs.getString("publish","wk");
		// Register a connectivity listener
//		registerReceiver(mConnectivityChanged, new IntentFilter(
//				ConnectivityManager.CONNECTIVITY_ACTION));
		EventBus.getDefault().register(this);
	}

	private void initOption() {

		// MQTT的连接设置
		options = new MqttConnectOptions();
		// 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
		options.setCleanSession(MQTT_CLEAN_START);
		// 设置连接的用户名
		options.setUserName(userName);
		// 设置连接的密码
		options.setPassword(passWord.toCharArray());
		options.setConnectionTimeout(MQTT_TIMEOUT);
		options.setKeepAliveInterval(MQTT_KEEP_ALIVE);
//		options.setWill(topic, payload, qos, retained)//设置连接的“最后意愿和遗嘱”（LWT）。如果此客户端意外失去与服务器的连接，则服务器将使用提供的详细信息向其自己发布消息。
		// 设置回调
	}

	private void handleCrashedService() {
		if (wasStarted() == true) {
			Log.i(TAG, "Handle crashed service");
			// stop the keep alives
			stopKeepAlives();
			// Do a clean start
			ThreadTask.getInstance().executorNetThread(new Runnable() {

				@Override
				public void run() {
					startService();
				}
			}, ThreadTask.ThreadPeriod.PERIOD_HIGHT);

		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy, Service started: " + mStarted);
		// // Remove the connectivity receiver
//		unregisterReceiver(mConnectivityChanged);

		// Stop the services, if it has been started
		if (mStarted == true) {
			stopService();
		}
		isExitService = true;
		
		ThreadTask.getInstance().shutDownAll();//停止所有线程
		EventBus.getDefault().unregister(this);
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		Log.i(TAG, "onStartCommand, flags: " + flags + ", startId: " + startId);
		if (intent == null) {
			Log.e(TAG, "onStartCommand intent = null");
			stopSelf();
			return START_STICKY_COMPATIBILITY;
		}

		ThreadTask.getInstance().executorNetThread(new Runnable() {

			@Override
			public void run() {
				// Do an appropriate action based on the intent.
				Log.e(TAG, Thread.currentThread().getName() + "*");
				if (intent.getAction().equals(ACTION_STOP) == true) {
					Log.i(TAG, "ACTION_STOP");
					isExitService = true;
					stopService();
					stopSelf();
				} else if (intent.getAction().equals(ACTION_START) == true) {
					Log.i(TAG, "ACTION_START");
					startService();
				} else if (intent.getAction().equals(ACTION_KEEPALIVE) == true) {
					Log.i(TAG, "ACTION_KEEPALIVE");
					keepAlive();
				} else if (intent.getAction().equals(ACTION_RECONNECT) == true) {
					Log.i(TAG, "ACTION_RECONNECT");
					if (isNetworkAvailable()) {
						// if (mConnection == null)
						reconnectIfNecessary();
					}
				}
			}
		}, ThreadTask.ThreadPeriod.PERIOD_HIGHT);

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 启动心跳，时间间隔
	 */
	private void startKeepAlives() {
		Log.i(TAG, "startKeepAlives");
		Intent i = new Intent();
		i.setClass(this, MqttService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + KEEP_ALIVE_INTERVAL,
				KEEP_ALIVE_INTERVAL, pi);
	}

	// Remove all scheduled keep alives
	/**
	 * 取消已经存在的闹�?
	 */
	private void stopKeepAlives() {
		Log.i(TAG, "stopKeepAlives");
		Intent i = new Intent();
		i.setClass(this, MqttService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	private synchronized void keepAlive() {
		Log.i(TAG, "keepAlive");
		try {
			Log.i(TAG, "sendKeepAlive, mStarted: " + mStarted
					+ ", mConnection: " + mConnection);
			// Send a keep alive, if there is a connection.
			if (mStarted == true && mConnection != null) {
				mConnection.sendKeepAlive();
			}
		} catch (MqttException e) {
			Log.e(TAG, "sendKeepAlive MqttException: "
					+ (e.getMessage() != null ? e.getMessage() : "NULL"));
			mConnection.disconnect();
			mConnection = null;
			cancelReconnect();
		}
	}

	// We schedule a reconnect based on the starttime of the service
	private void scheduleReconnect(long startTime) {
		// the last keep-alive interval
		Log.i(TAG, "scheduleReconnect");
		interval = mPrefs.getLong(PREF_RETRY, INITIAL_RETRY_INTERVAL);

		// Calculate the elapsed time since the start
		long now = System.currentTimeMillis();
		long elapsed = now - startTime;

		// Set an appropriate interval based on the elapsed time since start
		if (elapsed < interval) {
			interval = Math.min(interval * 4, MAXIMUM_RETRY_INTERVAL);
		} else {
			interval = INITIAL_RETRY_INTERVAL;
		}

		// Save the new internval
		Log.i(TAG, "Rescheduling connection interval:  " + interval + "(ms).");
		mPrefs.edit().putLong(PREF_RETRY, interval).commit();

		// Schedule a reconnect using the alarm manager.
		Intent i = new Intent();
		i.setClass(this, MqttService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, now + interval, pi);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private boolean wasStarted() {
		return mPrefs.getBoolean(PREF_STARTED, false);
	}

	// Sets whether or not the services has been started in the preferences.
	private void setStarted(boolean started) {
		mPrefs.edit().putBoolean(PREF_STARTED, started).commit();
		mStarted = started;
	}

	private synchronized void startService() {
		Log.e(TAG, "startService, Starting...");
		// Do nothing, if the service is already running.
		if (mStarted == true) {
			Log.e(TAG, "Attempt to connect MQTT that is already active");
			return;
		}

		// Establish an MQTT connection

		connect();

	}

	// Check if we are online
	private boolean isNetworkAvailable() {
		// isAvailable():判断该网络是否可用。 isConnected():判断是否已经连接。
		NetworkInfo info = mConnMan.getActiveNetworkInfo();
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	private synchronized void stopService() {
		// Do nothing, if the service is not running.
		Log.e(TAG, "Attempt to stop...");
		cancelReconnect();//停止服务，取消重连
		if (mStarted == false) {
			Log.e(TAG, "Attempt to stop connection that not active.");
			return;
		}
		setStarted(false);
		
		if (mConnection != null) {
			mConnection.disconnect();
			mConnection = null;
		}
	}

	// Remove the scheduled reconnect
	private void cancelReconnect() {
		Log.i(TAG, "cancelReconnect");
		Intent i = new Intent();
		i.setClass(this, MqttService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	private synchronized void reconnectIfNecessary() {
		Log.i(TAG, "reconnectIfNecessary mStarted: " + mStarted
				+ ", mConnection: " + mConnection);


		if (mStarted == false && mConnection == null) {
			ThreadTask.getInstance().executorNetThread(new Runnable() {
				@Override
				public void run() {
					connect();
				}
			}, ThreadTask.ThreadPeriod.PERIOD_MIDDLE);
		}
	}

	private synchronized void connect() {
//		Log.e(TAG, Thread.currentThread().getName() + "*");
		// fetch the device ID from the preferences.
		// Create a new connection only if the device id is not NULL
		String deviceID = mPrefs.getString(PREF_DEVICE_ID, null);
		index++;
		try {
			mConnection = new MQTTConnection(deviceID);
		} catch (MqttException e) {
			setStarted(false);
			Log.e(TAG, Thread.currentThread().getName() + "*");
			// Schedule a reconnect, if we failed to connect 无法连接服务器/已连接客户机
			Log.e(TAG, "MQTTConnection MqttException "
					+ (e.getMessage() != null ? e.getMessage() : "NULL"));

			while (!isNetworkAvailable() && !isExitService) {
				try {
					Log.e(TAG, Thread.currentThread().getName()
							+ " !isNetworkAvailable()");
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			if (isNetworkAvailable()) {
				Log.e(TAG, Thread.currentThread().getName() + "*");
				Log.e(TAG, "Network Available, scheduleReconnect");
				scheduleReconnect(mStartTime);
			} else {
				Log.e(TAG, Thread.currentThread().getName() + "*");
				Log.e(TAG, "MqttException Network not Available");
			}
		}

	}


	public void messageArrive() {



	}


	private class MQTTConnection implements MqttCallback {

		public MQTTConnection(String initTopic) throws MqttException {
			
			// host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
			String clientID = "MT/" + subscribe;// + "_" +
												// Integer.toString(index);
			Log.e(TAG, "BROKER_URL:"+BROKER_URL);
			if (null == mqttClient) {
				// clientID 不能改变，否则消息会很收到重复的。//new MemoryPersistence()
//				mqttClient = new MqttClient(BROKER_URL, clientID,
//						new MemoryPersistence());
				
				mqttClient = new MqttAsyncClient(BROKER_URL, clientID,new MemoryPersistence());
			}
			Log.e(TAG, "mqttClient" + Integer.toString(index));
			if(!mqttClient.isConnected()){
				mqttClient.connect(options,new IMqttActionListener() {
					
					@Override
					public void onSuccess(IMqttToken token) {
						Log.e(TAG, Thread.currentThread().getName()+"onSuccess");
						Log.i(TAG, "onSuccess: mqttClient.isconnect():"+mqttClient.isConnected());
//						String subTopic = subscribe + "Client";// MyState.getInstance().ucmUser;

						// subscribeToTopic(initTopic);
						try {
//							Log.i(TAG, "subscribeToTopic initTopic: " + subTopic);
							mqttClient.subscribe(subscribe, MQTT_QUALITY_OF_SERVICE);
							setStarted(true);//是否开启服务
							// Save start time
							mStartTime = System.currentTimeMillis();
							// Star the keep-alives
							startKeepAlives();
						} catch (MqttException e) {
							Log.e(TAG, "MqttException_subscribe:"+e.getLocalizedMessage());
							e.printStackTrace();
							mConnection = null;
//							reconnectIfNecessary();
							scheduleReconnect(mStartTime);
						}
						
					}
					
					@Override
					public void onFailure(IMqttToken token, Throwable cause) {
						Log.i(TAG, "onFailure: cause:"+cause.getLocalizedMessage());
						if(isExitService){
							return;
						}
//						SystemClock.sleep(2000);
						mConnection = null;
//						reconnectIfNecessary();
						scheduleReconnect(mStartTime);
					}
				}); // 无法连接服务器，会导致异常
			}
			
			mqttClient.setCallback(this);


		}

		// Disconnect
		private void disconnect() {
			Log.i(TAG, "disconnect");
			Log.e(TAG, Thread.currentThread().getName() + "*");
			setStarted(false);
			try {
				stopKeepAlives();
				if(mqttClient != null && mqttClient.isConnected()){
					mqttClient.disconnect();
				}
				mqttClient = null;
				Log.e(TAG, "mqttClient = null");
			} catch (MqttException e) {
				Log.e(TAG, "disconnect MqttException: "
						+ (e.getMessage() != null ? e.getMessage() : " NULL"));
				e.printStackTrace();
			}
		}

		private void subscribeToTopic(String topicName) throws MqttException {
			Log.i(TAG, "subscribeToTopic, topicName:" + topicName);
			if ((mqttClient == null) || (mqttClient.isConnected() == false)) {
				// quick sanity check - don't try and subscribe if we don't have
				// a connection
				Log.e(TAG, "subscribeToTopic :Connection error"
						+ "No connection");
			} else {
				String[] topics = { topicName };
				mqttClient.subscribe(topics, MQTT_QUALITIES_OF_SERVICE);

				Log.e(TAG, "subscribeToTopic :topic=" + topicName);
			}
		}

		/*
		 * Sends a message to the message broker, requesting that it be
		 * published to the specified topic.
		 */
		private void publishToTopic(final String topicName, final String message)
				throws MqttException {
			Log.i(TAG, "publishToTopic, topicName: " + topicName
					+ ", message: " + message);
			ThreadTask.getInstance().executorNetThread(new Runnable() {
				@Override
				public void run() {
					if ((mqttClient == null) /*
											 * || (mqttClient.isConnected() ==
											 * false)
											 */) {
						Log.e(TAG, "publishToTopic, mqttClient is null");
						connect();
					} else {
						try {
							mqttClient.publish(topicName,
									message.getBytes("UTF-8"),
									MQTT_QUALITY_OF_SERVICE,
									MQTT_RETAINED_PUBLISH);


						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (MqttException e) {
							e.printStackTrace();
						}
					}
				}
			}, ThreadTask.ThreadPeriod.PERIOD_HIGHT);

		}

		/*
		 * Called if the application loses it's connection to the message
		 * broker.
		 */
		@Override
		public void connectionLost(Throwable cause) {
			Log.e(TAG, "connectionLost" + " connection downed:"+cause.getLocalizedMessage());
		 // 网络异常 与服务器断开连接
			stopKeepAlives();
			setStarted(false);
			mConnection = null;

			ThreadTask.getInstance().executorNetThread(new Runnable() {

				@Override
				public void run() {
					int i = 0;
					while (i < 10) {
						if (isNetworkAvailable() == true) {
							Log.e(TAG,
									"network available, reconnectIfNecessary");
							reconnectIfNecessary();
							break;
						} else {
							Log.e(TAG, "network not available, sleep");
							i++;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}, ThreadTask.ThreadPeriod.PERIOD_HIGHT);
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			// publish后会执行到这里
			Log.i(TAG, "deliveryComplete---------" + token.isComplete());
		}

		@Override
		public void messageArrived(String topicName, MqttMessage message)
				throws Exception {
			// subscribe后得到的消息会执行到这里面
			
			Log.i(TAG, "messageArrived----------");


			try {
				mMsg = new String(message.getPayload(), "UTF-8");

				EventBus.getDefault().post(new Event.ReceiveEvent(mMsg));

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			Log.e(TAG, "cpublishArrived topic: " + topicName + ", msg: " + mMsg);

		}

		private void sendKeepAlive() throws MqttException {
			Log.i(TAG, "Sending keepalive");
			// publish to a keep-alive topic
			publishToTopic(MQTT_CLIENT_ID + "/keepalive",
					mPrefs.getString(PREF_DEVICE_ID, "123"));
		}

	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void messageArrive(Event.SendEvent sendEvent) {
		String msg = sendEvent.msg;
		Log.e(TAG,msg);
		if (mConnection != null) {
			try {
				mConnection.publishToTopic(publish, msg);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		} else {
			reconnectIfNecessary();
		}
	}
}
