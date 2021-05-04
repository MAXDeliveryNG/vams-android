package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.google.android.material.progressindicator.CircularProgressIndicator
import ng.max.vams.R
import ng.max.vams.util.hide
import ng.max.vams.util.show

class ProgressButton : FrameLayout {

    private lateinit var containerFrame: FrameLayout
    private lateinit var button: Button
    private lateinit var progressView: CircularProgressIndicator

    private var buttonText: CharSequence? = "OK"
    private var buttonTextColor: Int? = 0
    private var buttonBgColor: Int? = 0

    private lateinit var clickListener: OnClickListener

    private var isProgressEnabled: Boolean = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val ta: TypedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.ProgressButton)
        buttonText = ta.getString(R.styleable.ProgressButton_text)
        buttonTextColor = ta.getColor(R.styleable.ProgressButton_textColor, 0)
        buttonBgColor = ta.getColor(R.styleable.ProgressButton_bgColor, 0)


        ta.recycle()
        init()
    }

    private fun init() {
        containerFrame = View.inflate(context, R.layout.progress_button, this) as FrameLayout
        button = containerFrame.findViewById(R.id.button)
        progressView = containerFrame.findViewById(R.id.progress)

        button.text = buttonText
        button.setTextColor(buttonTextColor!!)
        button.setBackgroundColor(buttonBgColor!!)

        loaded() // default state
    }


    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        button.isEnabled = enabled
    }

    private fun setLoading() {
        button.text = ""
        button.isEnabled = false
        progressView.show()
    }

    fun loaded() {
        button.text = buttonText
        button.isEnabled = true
        progressView.hide()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        clickListener = OnClickListener { view ->
            if (isProgressEnabled) {
                setLoading()
            } else {
                loaded()
            }

            l?.onClick(view)
        }

        button.setOnClickListener(clickListener)
    }
}