package com.example.mainscreen;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_RESERVATION = 1;
    private static final int VIEW_TYPE_WAITING = 2;

    private List<Reservation> reservationList = new ArrayList<>();
    private List<Reservation> waitingList = new ArrayList<>();

    public ReservationAdapter(List<Reservation> reservationList, List<Reservation> waitingList) {
        this.reservationList = reservationList;
        this.waitingList = waitingList;
    }

    public void setReservationList(List<Reservation> reservationList) {
        this.reservationList = reservationList;
        notifyDataSetChanged();
    }

    public void setWaitingList(List<Reservation> waitingList) {
        this.waitingList = waitingList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < reservationList.size()) {
            return VIEW_TYPE_RESERVATION;
        } else {
            return VIEW_TYPE_WAITING;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_RESERVATION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reservation_item, parent, false);
            return new ReservationViewHolder(view);
        } else { // VIEW_TYPE_WAITING
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_waiting_item, parent, false);
            return new WaitingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_RESERVATION) {
            Reservation reservation = reservationList.get(position);
            ReservationViewHolder reservationHolder = (ReservationViewHolder) holder;

            // 예약 데이터 바인딩
            reservationHolder.restaurantName.setText(reservation.getRestaurantName());
            reservationHolder.reservationPeople.setText("People: " + reservation.getReservationPeople());
            reservationHolder.reservationDate.setText("Date: " + reservation.getReservationDate());
            reservationHolder.reservationTime.setText("Time: " + reservation.getReservationTime());

            // 이미지 로딩 (예: Glide를 사용해 Firebase Storage에서 가져온 이미지 사용)
            Glide.with(holder.itemView.getContext())
                    .load(reservation.getRestaurantImage())
                    .into(reservationHolder.restaurantImage);

            // 예약 아이템 클릭 시 BookingConfirmationActivity로 이동
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), BookingConfirmationActivity.class);
                intent.putExtra("reservationId", reservation.getReservationId());
                intent.putExtra("restaurantId", reservation.getRestaurantId());
                holder.itemView.getContext().startActivity(intent);
            });

        } else {
            int waitingPosition = position - reservationList.size();
            Reservation waiting = waitingList.get(waitingPosition);
            WaitingViewHolder waitingHolder = (WaitingViewHolder) holder;

            // 대기 데이터 바인딩
            waitingHolder.restaurantName.setText(waiting.getRestaurantName());
            waitingHolder.reservationPeople.setText("People: " + waiting.getReservationPeople());
            waitingHolder.reservationDate.setText("Date: " + waiting.getReservationDate());
            waitingHolder.reservationTime.setText("Time: " + waiting.getReservationTime());

            // 이미지 로딩
            Glide.with(holder.itemView.getContext())
                    .load(waiting.getRestaurantImage())
                    .into(waitingHolder.restaurantImage);

            // 대기 아이템 클릭 시 WaitingConfirmationActivity로 이동
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), WaitingConfirmationActivity.class);
                intent.putExtra("waitingId", waiting.getReservationId());
                intent.putExtra("restaurantId", waiting.getRestaurantId());
                holder.itemView.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return reservationList.size() + waitingList.size();
    }

    // Reservation ViewHolder
    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        ImageView restaurantImage;
        TextView restaurantName, reservationPeople, reservationDate, reservationTime;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            reservationPeople = itemView.findViewById(R.id.reservation_people);
            reservationDate = itemView.findViewById(R.id.reservation_date);
            reservationTime = itemView.findViewById(R.id.reservation_time);
        }
    }

    // Waiting ViewHolder
    public static class WaitingViewHolder extends RecyclerView.ViewHolder {
        ImageView restaurantImage;
        TextView restaurantName, reservationPeople, reservationDate, reservationTime;

        public WaitingViewHolder(View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            reservationPeople = itemView.findViewById(R.id.reservation_people);
            reservationDate = itemView.findViewById(R.id.reservation_date);
            reservationTime = itemView.findViewById(R.id.reservation_time);
        }
    }
}
