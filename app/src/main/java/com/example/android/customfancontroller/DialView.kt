package com.example.android.customfancontroller

import android.content.Context
import android.util.AttributeSet
import android.view.View





/*Android Studio adds the constructor from the View class. The @JvmOverloads annotation
instructs the Kotlin compiler to generate overloads for this function that substitute
default parameter values.*/
class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
}