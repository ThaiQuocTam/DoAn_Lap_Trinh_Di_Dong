package com.example.tracnghiemdtck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tracnghiemdtck.model.Database;
import com.example.tracnghiemdtck.model.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class QuestionActivity extends AppCompatActivity {

    private TextView txtCauhoi, txtDiem, txtTongcauhoi, txtTongTG, txtCatelogy;
    private RadioGroup rgbGroup;
    private RadioButton rb1, rb2, rb3, rb4;
    private Button btnXacnhan;
    private CountDownTimer countDownTimer;
    private ArrayList<Question> questionList;
    private long timeLeftInMillis;
    private int questionCounter;
    private int questionSize;
    private int diem;
    private boolean answered;
    private int count = 0;
    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Anhxa();

        //Nhận dữ liệu chủ đề
        Intent intent = getIntent();
        int categooryID = intent.getIntExtra("idcategoríe",0);
        String categoryName = intent.getStringExtra("catgoriesname");

        txtCatelogy.setText("Chủ đề : " + categoryName);

        Database database = new Database(this);

        //Danh sách chứa câu hỏi
        questionList = database.getQuestions(categooryID);
        //Lấy kích cỡ danh sách
        questionSize = questionList.size();
        //Đảo vị trí các phần tử câu hỏi;
        Collections.shuffle(questionList);
        //Show câu hỏi và đáp an
        showNextQuestion();

        //Button xác nhận câu tiếp
        btnXacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nếu chưa trả lời câu hỏi
                if(!answered){
                    //Nếu chọn 1 trong 4 đáp án
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()){
                        //Kiểm tra đáp án
                        checkAnswer();
                    }
                    else {
                        Toast.makeText(QuestionActivity.this, "Hãy chọn đáp án", Toast.LENGTH_SHORT).show();
                    }
                }
                //Nếu đã trả lời thực hiện chuyển câu hỏi
               else {
                   showNextQuestion();
                }
            }
        });
    }

    //Hiển thị câu hỏi
    private void showNextQuestion() {
        rb1.setTextColor(Color.BLACK);
        rb2.setTextColor(Color.BLACK);
        rb3.setTextColor(Color.BLACK);
        rb4.setTextColor(Color.BLACK);

        //Xóa chọn
        rgbGroup.clearCheck();



        //Nếu còn câu hỏi
        if(questionCounter < questionSize){
            //Lấy dữ liệu ở vị trí counter
            currentQuestion = questionList.get(questionCounter);
            //Hiển thị câu hỏi
            txtCauhoi.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());

            //Tăng sau mỗi câu hỏi
            questionCounter++;

            //set vị trí câu hỏi hện tại
            txtTongcauhoi.setText("Câu hỏi " + questionCounter+ "/ " + questionSize);

            //Giá trị false, chưa trả lời, đang showw
            answered = false;

            //Gán tên cho button
            btnXacnhan.setText("Xác nhận");

            //Thời gian chạy 30s;
            timeLeftInMillis = 30000;

            //Đếm ngươc thời gian trả lời
            startCountDown();
        }
        else {
            finishQuestion();
        }
    }

    //Đếm ngược thời gian
    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;

                //Update
                updateCountText();
            }

            @Override
            public void onFinish() {
                //Hết giờ
                timeLeftInMillis = 0;
                updateCountText();

                //Phương thức kiếm tra đáp án
                checkAnswer();
            }
        }.start();
    }

    private void  checkAnswer(){
        //true đã trả lời
        answered = true;

        //Trả về RadioButton trong fbGroup được check
        RadioButton rbSeleced = findViewById(rgbGroup.getCheckedRadioButtonId());

        //Vị trí cả câu đã chọn
        int answer = rgbGroup.indexOfChild(rbSeleced) + 1;

        if(answer == currentQuestion.getAnswer()){
            //Tăng 10 điểm
            diem = diem + 10;

            //Hiển thị
            txtDiem.setText("Điểm : " + diem);

        }

        //Phương thức hiển thị đáp án
        showSolution();

    }

    private void showSolution() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);

        //Kiểm tra đáp án set màu và hiển thị đáp án lên màn hình
        switch (currentQuestion.getAnswer()){
            case 1:
                rb1.setTextColor(Color.GREEN);
                txtCauhoi.setText("Đáp án là A");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                txtCauhoi.setText("Đáp án là B");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                txtCauhoi.setText("Đáp án là C");
                break;
            case 4:
                rb4.setTextColor(Color.GREEN);
                txtCauhoi.setText("Đáp án là D");
                break;
        }

        //Nếu còn câu trả lời thì sẽ setText câu tiếp
        if(questionCounter < questionSize){
            btnXacnhan.setText("Câu tiếp");
        }
        //settext hoàn thành
        else {
            btnXacnhan.setText("Hoàn thành");
        }

        //dừng thời gian lại
        countDownTimer.cancel();
    }

    private void updateCountText(){
        //Tính phút
        int minutes = (int)((timeLeftInMillis/1000)/60);

        //Tính giây
        int seconds = (int)(((timeLeftInMillis/1000)%60));

        //Định dạng kiểu time
        String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);

        //Hiển thị lên màn hình
        txtTongTG.setText(timeFormatted);

        //NẾu thời gian dưới 10s thì sẽ chuyển màu đỏ
        if(timeLeftInMillis < 10000){
            txtTongTG.setTextColor(Color.RED);

        }
        //Không thì vẫn màu đen
        else {
            txtTongTG.setTextColor(Color.BLACK);
        }
    }

    //Thoát qua giao diện chính
    private void finishQuestion() {
        //Chưa dữ liệu gửi qua activityMain
        Intent resultIntetnt = new Intent();
        resultIntetnt.putExtra("diem",diem);
        setResult(RESULT_OK,resultIntetnt);
        finish();
    }

    @Override
    public void onBackPressed(){
        count++;
        if(count>=1){
            finishQuestion();
        }
        count = 0;
    }

    public void Anhxa(){
        txtCauhoi = (TextView) findViewById(R.id.txtCauhoi);
        txtDiem = (TextView)findViewById(R.id.txtDiem);
        txtTongcauhoi = (TextView) findViewById(R.id.txtTongCauHoi);
        txtCatelogy = (TextView) findViewById(R.id.txtCatelogy);
        txtTongTG = (TextView) findViewById(R.id.txtTongTG);

        rgbGroup = (RadioGroup) findViewById(R.id.rdgGroup);
        rb1 = (RadioButton) findViewById(R.id.rdnButton1);
        rb2 = (RadioButton) findViewById(R.id.rdnButton2);
        rb3 =  (RadioButton)findViewById(R.id.rdnButton3);
        rb4 =  (RadioButton)findViewById(R.id.rdnButton4);

        btnXacnhan = (Button) findViewById(R.id.btnXacnhan);
    }
}