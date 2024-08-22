package com.example.mainscreen;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyReservationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter reservationAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_reservation);

        recyclerView = findViewById(R.id.recycler_view_my_reservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 초기화
        reservationAdapter = new ReservationAdapter(new ArrayList<>(), new ArrayList<>());
        recyclerView.setAdapter(reservationAdapter);

        loadReservationData(); // Firestore에서 예약 데이터를 불러오는 메서드 호출
        loadWaitingData(); // Firestore에서 대기 데이터를 불러오는 메서드 호출
    }

    private void loadReservationData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            FirebaseFirestore.getInstance().collection("reservations")
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            Date currentDate = new Date();

                            List<Reservation> validReservations = new ArrayList<>();

                            for (DocumentSnapshot document : task.getResult()) {
                                Long peopleLong = document.getLong("people");  // 'people' 필드를 Long 타입으로 가져옴
                                int reservationPeople = (peopleLong != null) ? peopleLong.intValue() : 0;

                                String date = document.getString("date");
                                String time = document.getString("time");
                                String restaurantId = document.getString("restaurantId");

                                try {
                                    Date reservationDate = dateFormat.parse(date);

                                    // 현재 날짜 이후의 예약만 유효한 예약 목록에 추가
                                    if (reservationDate != null && !reservationDate.before(currentDate)) {
                                        Reservation reservation = new Reservation(reservationPeople, date, time, restaurantId);
                                        validReservations.add(reservation);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            // 어댑터에 필터링된 예약 목록 설정
                            reservationAdapter.setReservationList(validReservations);
                        }
                    });
        }
    }

    private void loadWaitingData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            FirebaseFirestore.getInstance().collection("waitings")
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            Date currentDate = new Date();

                            List<Reservation> validWaitings = new ArrayList<>();

                            for (DocumentSnapshot document : task.getResult()) {
                                Long peopleLong = document.getLong("people");  // 'people' 필드를 Long 타입으로 가져옴
                                int reservationPeople = (peopleLong != null) ? peopleLong.intValue() : 0;

                                String date = document.getString("date");
                                String time = document.getString("time");
                                String restaurantId = document.getString("restaurantId");

                                try {
                                    Date waitingDate = dateFormat.parse(date);

                                    // 현재 날짜 이후의 대기만 유효한 대기 목록에 추가
                                    if (waitingDate != null && !waitingDate.before(currentDate)) {
                                        Reservation waiting = new Reservation(reservationPeople, date, time, restaurantId);
                                        validWaitings.add(waiting);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            // 어댑터에 필터링된 대기 목록 설정
                            reservationAdapter.setWaitingList(validWaitings);
                        }
                    });
        }
    }
}
