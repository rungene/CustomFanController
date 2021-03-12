package com.example.android.customfancontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.min
import kotlin.math.cos
import kotlin.math.sin

/*add a top-level enum to represent the available fan speeds. Note that this enum is of
type Int because the values are string resources rather than actual strings.*/

private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

   // add an extension function next() that changes the current fan speed to the next speed in the list
   fun next() = when (this) {
       OFF -> LOW
       LOW -> MEDIUM
       MEDIUM -> HIGH
       HIGH -> OFF
   }


}

//add these constants. You'll use these as part of drawing the dial indicators and labels.

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35



/*Android Studio adds the constructor from the View class. The @JvmOverloads annotation
instructs the Kotlin compiler to generate overloads for this function that substitute
default parameter values.*/
class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
   /* These values are created and initialized here instead of when the view is actually drawn to
    ensure that the actual drawing step runs as fast as possible.*/
    //define several variables you need in order to draw the custom view
    private var radius = 0.0f                   // Radius of the circle.
    private var fanSpeed = FanSpeed.OFF         // The active selection.
    // position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)


//declare variables to cache the attribute values.
    private var fanSpeedLowColor = 0
    private var fanSpeedMediumColor = 0
    private var fanSeedMaxColor = 0


    //initialize a Paint object with a handful of basic styles.

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    //Setting the view's isClickable property to true enables that view to accept user input
    init {
        isClickable = true

        //supply the attributes and view, and and set your local variables.
        //use withStyledAttributes extension function
        context.withStyledAttributes(attrs, R.styleable.DialView) {
            fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
            fanSpeedMediumColor = getColor(R.styleable.DialView_fanColor2, 0)
            fanSeedMaxColor = getColor(R.styleable.DialView_fanColor3, 0)
        }
    }

    override fun performClick(): Boolean {
        //The call to super.performClick() must happen first, which enables accessibility events
        // as well as calls onClickListener().
        if (super.performClick()) return true

        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.label)
// invalidates the entire view, forcing a call to onDraw() to redraw the view.
        invalidate()
        return true
    }

    /*override the onSizeChanged() method from the View class
    to calculate the size for the custom view's dial*/

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }
    //define a computeXYForSpeed() extension function for the PointF class.
    /*This extension function on the PointF class calculates the X, Y coordinates on the screen for
        the text label and current indicator (0, 1, 2, or 3), given the current FanSpeed position
    and radius of the dial. You'll use this in onDraw(). */

    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    //Override the onDraw() method to render the view on the screen with the Canvas
// and Paint classes.

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Set dial background color to green if selection not off.
       // paint.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN
     //using the  local variables set the dial color based on the current fan speed.
        paint.color = when (fanSpeed) {
            FanSpeed.OFF -> Color.GRAY
            FanSpeed.LOW -> fanSpeedLowColor
            FanSpeed.MEDIUM -> fanSpeedMediumColor
            FanSpeed.HIGH -> fanSeedMaxColor
        } as Int
       // to draw a circle for the dial, with the drawCircle() method
        // Draw the dial.
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
    // to draw a smaller circle for the fan speed indicator mark, also with the drawCircle() method.
        //this part uses the PointF.computeXYforSpeed() extension method to calculate the X,Y
    // coordinates for the indicator center based on the current fan speed.
        // Draw the indicator circle.
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas.drawCircle(pointPosition.x, pointPosition.y, radius/12, paint)
/*
        draw the fan speed labels (0, 1, 2, 3) at the appropriate positions around the dial.
        This part of the method calls PointF.computeXYForSpeed() again to get the position for each
        label, and reuses the pointPosition object each time to avoid allocations. Use drawText()
        to draw the labels.*/

        // Draw the text labels.
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
            val label = resources.getString(i.label)
            canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
        }



    }


}