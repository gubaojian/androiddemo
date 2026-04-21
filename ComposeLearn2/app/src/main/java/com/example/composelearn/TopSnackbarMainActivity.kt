package com.example.composelearn

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import razerdp.basepopup.BasePopupWindow


class TopSnackbarMainActivity : ComponentActivity() {

    val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_top_snackbar_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.button).setOnClickListener {
            scope.launch {
                delay(300)
                Alerter.create(this@TopSnackbarMainActivity)
                    .setTitle("Alert Title")
                    .setText("Alert text...")
                    .show()
            }
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            val balloon = Balloon.Builder(baseContext)
                .setWidthRatio(1.0f)
                .setHeight(BalloonSizeSpec.WRAP)
                .setText("Edit your profile here!")
                .setTextColorResource(R.color.white)
                .setTextSize(15f)
                .setIconDrawableResource(R.drawable.ic_launcher_foreground)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowSize(10)
                .setArrowPosition(0.5f)
                .setPadding(12)
                .setCornerRadius(8f)
                .setBackgroundColorResource(R.color.purple_200)
                //.setBalloonAnimation(BalloonAnimation.ELASTIC)
                .setLifecycleOwner(this)
                .build()
            balloon.showAsDropDown(findViewById<Button>(R.id.button2))

            val popup = TestPopup(this@TopSnackbarMainActivity)
            popup.setPopupGravity(Gravity.TOP or  Gravity.CENTER_HORIZONTAL)
            popup.setPopupGravityMode(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR)
            popup.setAutoMirrorEnable(true)
            //popup.showPopupWindow(findViewById<Button>(R.id.button2))

            val popup2 = TestPopup2(this@TopSnackbarMainActivity)
            popup2.showOnAnchor(findViewById<Button>(R.id.button2),
                RelativePopupWindow.VerticalPosition.ABOVE,
                RelativePopupWindow.HorizontalPosition.CENTER)
        }
    }
}