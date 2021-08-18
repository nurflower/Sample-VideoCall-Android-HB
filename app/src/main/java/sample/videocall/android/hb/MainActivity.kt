package sample.videocall.android.hb

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import tsa.videocall.sdk.hb.publisher.TSAVideoCallPublisher
import tsa.videocall.sdk.hb.publisher.TSAVideoCallPublisherListener
import tsa.videocall.sdk.hb.session.TSAVideoCallSession
import tsa.videocall.sdk.hb.session.TSAVideoCallSessionListener
import tsa.videocall.sdk.hb.subscriber.TSAVideoCallSubscriber
import tsa.videocall.sdk.hb.subscriber.TSAVideoCallSubscriberListener
import tsa.videocall.sdk.hb.utils.TSAVideoCallConfig
import tsa.videocall.sdk.hb.utils.TSAVideoCallStream
import tsa.videocall.sdk.hb.utils.TsaVideoCallError

class MainActivity : AppCompatActivity() {

    private val myConfig = TSAVideoCallConfig(
        webURL = "enter-web-url",
        webSocketMediaServerURL = "enter-media-server-url",
        webSocketBrokerURL = "enter-broker-url",
        webSocketBrokerPath = "enter-broker-path",
        callHash = "enter-callHash",
        authData = "enter-credentials"
    )

    private var session: TSAVideoCallSession? = null

    private val sessionListener = object : TSAVideoCallSessionListener {

        override fun onConnected(session: TSAVideoCallSession){
            initializePublisher(session)
        }

        override fun onDisconnected(session: TSAVideoCallSession) {

        }

        override fun onStreamReceived(session: TSAVideoCallSession, stream: TSAVideoCallStream) {
            initializeSubscriber(session, stream)
        }

        override fun onStreamDropped(session: TSAVideoCallSession, stream: TSAVideoCallStream) {

        }

        override fun onMessageReceived(session: TSAVideoCallSession, message: String) {

        }

        override fun onFileReceived(
            session: TSAVideoCallSession,
            fileName: String,
            filePath: String
        ) {

        }

        override fun onError(session: TSAVideoCallSession, error: TsaVideoCallError) {

        }

    }

    private var publisher: TSAVideoCallPublisher? = null
    private val publisherListener = object : TSAVideoCallPublisherListener {

        override fun onStreamCreated(publisher: TSAVideoCallPublisher) {

        }

        override fun onStreamDestroyed(publisher: TSAVideoCallPublisher) {

        }

        override fun onError(publisher: TSAVideoCallPublisher, error: TsaVideoCallError) {

        }
    }

    private var subscriber: TSAVideoCallSubscriber? = null
    private val subscriberListener = object : TSAVideoCallSubscriberListener {
        override fun onConnected(subscriber: TSAVideoCallSubscriber) {

        }

        override fun onDisconnected(subscriber: TSAVideoCallSubscriber) {

        }

        override fun onError(subscriber: TSAVideoCallSubscriber, error: TsaVideoCallError) {

        }
    }

    private lateinit var publisherViewContainer: FrameLayout
    private lateinit var subscriberViewContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        publisherViewContainer = findViewById(R.id.publisherViewContainer)
        subscriberViewContainer = findViewById(R.id.subscriberViewContainer)

        callPermissions.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )

    }

    private val callPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.isNotEmpty() && result.values.all { it }) {
            initSession(myConfig)
        }
    }

    private fun initSession(tsaVideoCallConfig: TSAVideoCallConfig) {
        session = TSAVideoCallSession(this, tsaVideoCallConfig).apply {
            setSessionListener(sessionListener)
            connect()
        }
    }


    private fun initializePublisher(session: TSAVideoCallSession){
        publisher = TSAVideoCallPublisher(this, session).apply {
            setParentContainer(publisherViewContainer)
            setPublisherListener(publisherListener)
        }
        session.publish(publisher!!)
    }

    private fun initializeSubscriber(session: TSAVideoCallSession, stream: TSAVideoCallStream){
        subscriber = TSAVideoCallSubscriber(this, stream, session).apply {
            setSubscriberListener(subscriberListener)
            setParentContainer(subscriberViewContainer)
        }
        session.subscribe(subscriber!!)
    }

}