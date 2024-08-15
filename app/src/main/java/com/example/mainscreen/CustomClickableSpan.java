package com.example.mainscreen;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class CustomClickableSpan extends ClickableSpan {
    @Override
    public void onClick(View widget) {
        // 클릭 이벤트 처리
        // 예: 주소를 복사하는 기능 추가
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false); // 밑줄 제거
    }
}
