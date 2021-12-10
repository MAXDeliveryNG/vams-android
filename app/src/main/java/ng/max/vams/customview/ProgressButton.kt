package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import ng.max.vams.R

class ProgressButton : FrameLayout {

    private lateinit var containerFrame: FrameLayout
    private lateinit var button: MaterialButton
    private lateinit var progressView: CircularProgressIndicator

    private var buttonText: CharSequence? = "Submit"
    private var buttonTextColor: Int? = 0

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


        ta.recycle()
        init()
    }

    private fun init() {
        containerFrame = View.inflate(context, R.layout.progress_button, this) as FrameLayout
        button = containerFrame.findViewById(R.id.button)
        progressView = containerFrame.findViewById(R.id.progress)

        button.text = buttonText
        button.setTextColor(buttonTextColor!!)
        button.backgroundTintList = ContextCompat.getColorStateList(context,
            R.color.button_state)

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

    fun setButtonEnable(isEnabled: Boolean) {
        button.isEnabled = isEnabled
    }

    fun setButtonText(label: String) {
        buttonText = label
    }
}