package com.example.tracnghiemdtck;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tracnghiemdtck.model.Category;
import com.example.tracnghiemdtck.model.Database;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txtHighScore, txtTongDiem;
    private Spinner spinnerCategory;
    private Button btnBatdau;

    private int diemCao, tongDiem;
    private static final int REQUEST_CODE_QUESTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Anhxa();

        //Load chủ đề
        loadCategories();

        //Load điểm
        loadTongdiem();

        btnBatdau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuestion();
            }
        });
    }

    //Load điểm hiển thị
    private void loadTongdiem() {
        SharedPreferences preferences = getSharedPreferences("share",MODE_PRIVATE);
        diemCao = preferences.getInt("tongDiem",0);
        txtHighScore.setText("Điểm cao nhất : " + diemCao);

        tongDiem = preferences.getInt("tongDiem",0);
        txtTongDiem.setText("Tổng điểm : 0");
    }

    private void startQuestion(){
        //Lấy id, name của chủ đề đã chọn
        Category category = (Category) spinnerCategory.getSelectedItem();
        int categoryID = category.getId();
        String categoryName = category.getName();

        //Chuyển đến trang activity Question
        Intent intent = new Intent(MainActivity.this,QuestionActivity.class);

        intent.putExtra("idcategoríe",categoryID);
        intent.putExtra("catgoriesname",categoryName);

        //Sử dụng StartActivityForResult để có thể nhận lại dữ liệu kết quả trả về thông qua phương thức onActivityResult()
        startActivityForResult(intent,REQUEST_CODE_QUESTION);

    }

    private void Anhxa() {
        txtHighScore = findViewById(R.id.txtDiemcao);
        btnBatdau = findViewById(R.id.btnBatdau);
        spinnerCategory = findViewById(R.id.spiner_category);
        txtTongDiem = findViewById(R.id.txtTongDiem);
    }

    //Load chủ đề
    private void loadCategories(){
        Database database = new Database(this);

        //Lấy dữ liệu danh sách chủ đề
        List<Category> categories = database.getDataCategories();

        //Tạo adapter
        ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categories);

        //Bố cục hiển thị chủ đề
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //gán chủ đề lên spiner
        spinnerCategory.setAdapter(categoryArrayAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_QUESTION){
            if(resultCode == RESULT_OK){
                int diem = data.getIntExtra("diem",0);

                Sodiem(diem);

                if(diem > diemCao){
                    updateTongdiem(diem);
                }

            }
        }
    }

    //Cập nhật tổng điểm
    private void updateTongdiem(int diem) {
        //Gán tổng điểm mới
        diemCao = diem;
        //Hiển thị
        txtHighScore.setText("Điểm cao nhất : " + diemCao);
        //Lưu trữ điểm
        SharedPreferences preferences = getSharedPreferences("share",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //Gán giá trị cho tổng điểm mới vào khóa
        editor.putInt("tongDiem",diemCao);

        //Hoàn tất
        editor.apply();
    }

    //Lấy điểm vừa thi
    private void Sodiem(int diem) {
        //Gán tổng điểm mới
        tongDiem = diem;
        //Hiển thị
        txtTongDiem.setText("Tổng điểm : " + tongDiem);
        //Lưu trữ điểm
        SharedPreferences preferences = getSharedPreferences("share",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //Gán giá trị cho tổng điểm mới vào khóa
        editor.putInt("tongDiem",tongDiem);

        //Hoàn tất
        editor.apply();
    }
}