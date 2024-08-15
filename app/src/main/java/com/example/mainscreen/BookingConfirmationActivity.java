package com.example.mainscreen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView bookingId, bookingAddress, partySize, date, time, contactInfo;
    private LinearLayout btnCancelReservation, btnCallRestaurant;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        bookingId = findViewById(R.id.booking_id);
        bookingAddress = findViewById(R.id.booking_address);
        partySize = findViewById(R.id.party_size);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        contactInfo = findViewById(R.id.contact_info);
        btnCancelReservation = findViewById(R.id.btn_cancel_reservation);
        btnCallRestaurant = findViewById(R.id.btn_call_restaurant);
        btnConfirm = findViewById(R.id.btn_confirm);

        // Set data from server
        bookingId.setText("Booking ID: US9YD"); // Replace with data from server(서버 수정 부분)
        bookingAddress.setText("15 Stamford (The Capitol Kempinski Hotel Singapore)\n15 Stamford Road The Capitol Kempinski Hotel Singapore (178906)");
        partySize.setText("Party Size: 2 Adults");
        date.setText("Date: 9 Aug, Fri");
        time.setText("Time: 12:00 PM");
        contactInfo.setText("Ms. 김 유진\n+82 1028702298 · jinbe47@naver.com");

        // Create spannable string with image span
        String text = bookingAddress.getText().toString();
        SpannableString spannableString = new SpannableString(text + "  ");

        // Create spannable string with image span
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_copy_holo_light);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 40, 40, true); // 크기 조정
        Drawable drawable = new BitmapDrawable(getResources(), resizedBitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        //이미지 스팬 생성
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        int start = text.length() + 1;
        int end = start + 1;
        spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        //접근성 텍스트 추가 및 클릭 이벤트 처리
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Address", bookingAddress.getText().toString());
                clipboard.setPrimaryClip(clip);
                // Notify user that address has been copied
                Toast.makeText(BookingConfirmationActivity.this, "Copied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // 밑줄 제거
            }
        }, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        bookingAddress.setText(spannableString);
        bookingAddress.setMovementMethod(LinkMovementMethod.getInstance());

        btnCancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 예약 취소 처리
            }
        });

        btnCallRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Restaurant Contact", "+82 1028702298");
                clipboard.setPrimaryClip(clip);
                // Notify user that contact has been copied
                Toast.makeText(BookingConfirmationActivity.this, "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to main.xml
                Intent intent = new Intent(BookingConfirmationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
