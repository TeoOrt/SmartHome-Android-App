package com.example.smart_home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

class SignUpViewActivity : AppCompatActivity() {
    private var email_text_input: TextInputLayout? = null
    private var password_text_input: TextInputLayout? = null
    private var email_text: TextView? = null
    private var password_text: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_view)
        configureSignUpButton()
        email_text_input = findViewById<View>(R.id.edit_email) as TextInputLayout
        password_text_input = findViewById<View>(R.id.edit_password) as TextInputLayout
        email_text = findViewById<View>(R.id.email_title) as TextView
        password_text = findViewById<View>(R.id.password_title) as TextView

//        sign_up_request();
    }

    private fun CheckField(): Boolean {
        val EmailFormatPattern =
            "^([A-Za-z])([a-zA-Z._0-9]*)(@)(gmail|outlook|hotmail|yahoo|live|asu)(.)([a-z][a-z][a-z])"
        //        we want to actually validate the input
        val Email = email_text_input!!.editText.toString()
        val match_email = Pattern.matches(EmailFormatPattern, Email)
        var can_sendRequest = true
        val password = password_text_input!!.editText.toString()
        if (Email.isEmpty()) {
            email_text!!.text = "Email field is empty try again"
            can_sendRequest = false
        }
        if (match_email) {
            email_text!!.text = Email
            can_sendRequest = false
        }
        if (password.length <= 6 || password.length <= 18) {
            password_text!!.text = "Size of password need to be between 6 and 18 words"
            can_sendRequest = false
        }
        return can_sendRequest
    }

    /*
    private void sign_up_request() {


        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.0.203:8080/get_expert/video/H-LightOn.mp4";

        final FormBody formBody = new FormBody.Builder().add();
        final Request request = new Request.Builder()
                .url(url)
                .post()
                .build();

        // Use enqueue for asynchronous reques
    }
*/
    private fun configureSignUpButton() {
        val singUpViewButton = findViewById<View>(R.id.button_login_screen) as Button
        singUpViewButton.setOnClickListener { finish() }
        val signUp_accountButton = findViewById<View>(R.id.button_submit_login) as Button
        signUp_accountButton.setOnClickListener(View.OnClickListener {
            if (!CheckField()) {
                return@OnClickListener
            }
            val intent = Intent(this@SignUpViewActivity, HomeActivity::class.java)
            startActivity(intent)
            //                finish();
        })
    } /*

   client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Handle response on the background thread
                int responseCode = response.code();
                Log.d("Response Code", String.valueOf(responseCode));

                if (response.isSuccessful()) {
                    final InputStream inputStream = response.body().byteStream();
                    // Update UI on the main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Process the response or update UI components
                            playVideo(inputStream);
                        }
                    });
                }
            }
        });
 */
}