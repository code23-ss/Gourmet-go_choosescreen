package com.example.mainscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedHashMap;
import java.util.Map;

public class OpeningHoursActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_hours);

        LinearLayout openingHoursContainer = findViewById(R.id.opening_hours_container);

        // Firestore에서 데이터 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("restaurants").document("restaurant_id_1");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();

                // 요일별 데이터를 저장하는 Map
                Map<String, Map<String, Object>> openingHoursData = new LinkedHashMap<>();

                // 요일별 데이터를 가져오기
                openingHoursData.put("월요일", (Map<String, Object>) document.get("opening_hours.Monday"));
                openingHoursData.put("화요일", (Map<String, Object>) document.get("opening_hours.Tuesday"));
                openingHoursData.put("수요일", (Map<String, Object>) document.get("opening_hours.Wednesday"));
                openingHoursData.put("목요일", (Map<String, Object>) document.get("opening_hours.Thursday"));
                openingHoursData.put("금요일", (Map<String, Object>) document.get("opening_hours.Friday"));
                openingHoursData.put("토요일", (Map<String, Object>) document.get("opening_hours.Saturday"));
                openingHoursData.put("일요일", (Map<String, Object>) document.get("opening_hours.Sunday"));

                // 반복문을 통해 요일별 데이터 처리
                for (Map.Entry<String, Map<String, Object>> entry : openingHoursData.entrySet()) {
                    String day = entry.getKey();
                    Map<String, Object> hoursData = entry.getValue();

                    // 해당 요일의 UI 요소 동적 생성
                    View dayView = LayoutInflater.from(this).inflate(R.layout.item_opening_hour, openingHoursContainer, false);

                    TextView tvDay = dayView.findViewById(R.id.tv_day);
                    TextView tvOpeningHours = dayView.findViewById(R.id.tv_opening_hours);
                    TextView tvBreakHours = dayView.findViewById(R.id.tv_break_hours);
                    TextView tvLastOrder = dayView.findViewById(R.id.tv_last_order);
                    TextView tvLastOrder2 = dayView.findViewById(R.id.tv_last_order_2);
                    //LinearLayout llHours = dayView.findViewById(R.id.ll_hours);

                    // 요일 설정
                    tvDay.setText(day);

                    // 데이터가 없거나 휴무일인 경우
                    if (hoursData == null || Boolean.TRUE.equals(hoursData.get("closed"))) {
                        tvOpeningHours.setText("closed");
                    } else {
                        String open = (String) hoursData.get("open");
                        String close = (String) hoursData.get("close");
                        String breakStart = (String) hoursData.get("break_start");
                        String breakEnd = (String) hoursData.get("break_end");
                        String lastOrder = (String) hoursData.get("last_order");
                        String lastOrder1 = (String) hoursData.get("last_order_1");
                        String lastOrder2 = (String) hoursData.get("last_order_2");

                        // 영업 시간 설정
                        tvOpeningHours.setText(open + " - " + close);

                        // 쉬는 시간 설정
                        if (breakStart != null && breakEnd != null) {
                            tvBreakHours.setVisibility(View.VISIBLE);
                            tvBreakHours.setText(breakStart + " - " + breakEnd);
                        }

                        // 라스트 오더 설정
                        if (lastOrder != null) {
                            tvLastOrder.setVisibility(View.VISIBLE);
                            tvLastOrder.setText(lastOrder + " last order");
                        } else {
                            if (lastOrder1 != null) {
                                tvLastOrder.setVisibility(View.VISIBLE);
                                tvLastOrder.setText(lastOrder1);
                            }
                            if (lastOrder2 != null) {
                                tvLastOrder2.setVisibility(View.VISIBLE);
                                tvLastOrder2.setText(lastOrder2);
                            }
                        }
                    }

                    // 생성된 뷰를 부모 레이아웃에 추가
                    openingHoursContainer.addView(dayView);
                }
            } else {
                // Firestore에서 데이터를 가져오지 못한 경우
                TextView errorText = new TextView(this);
                errorText.setText("데이터를 불러오지 못했습니다.");
                openingHoursContainer.addView(errorText);
            }
        });
    }
}
