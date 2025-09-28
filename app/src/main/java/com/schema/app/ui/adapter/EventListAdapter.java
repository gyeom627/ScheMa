package com.schema.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.schema.app.R;
import com.schema.app.data.model.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RecyclerView에 일정 목록을 표시하기 위한 어댑터입니다.
 * ListAdapter를 상속받아 DiffUtil을 사용하므로, 리스트가 변경될 때 효율적으로 UI를 업데이트합니다.
 */
public class EventListAdapter extends ListAdapter<Event, EventListAdapter.EventViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    public EventListAdapter(@NonNull DiffUtil.ItemCallback<Event> diffCallback, OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    /**
     * ViewHolder가 생성될 때 호출됩니다.
     * recyclerview_item.xml 레이아웃을 인플레이트하여 ViewHolder를 생성하고 반환합니다.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new EventViewHolder(view, listener);
    }

    /**
     * ViewHolder가 특정 위치의 데이터와 바인딩될 때 호출됩니다.
     * 해당 위치의 Event 객체를 가져와 ViewHolder에 데이터를 설정합니다.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event current = getItem(position);
        holder.bind(current);
    }

    /**
     * RecyclerView의 각 아이템 뷰를 보관하는 ViewHolder 클래스입니다.
     */
    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView dateView;
        private final OnItemClickListener listener;

        private EventViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            titleView = itemView.findViewById(R.id.textview_title);
            dateView = itemView.findViewById(R.id.textview_date);
        }

        /**
         * Event 객체의 데이터를 뷰에 바인딩합니다.
         * @param event 표시할 Event 객체
         */
        public void bind(Event event) {
            titleView.setText(event.getTitle());
            // 밀리초 형태의 시간을 "yyyy-MM-dd HH:mm" 형식의 문자열로 변환하여 표시합니다.
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            dateView.setText(dateFormat.format(new Date(event.getEventTimeMillis())));
            itemView.setOnClickListener(v -> listener.onItemClick(event));
        }
    }

    /**
     * 리스트 업데이트 시 변경 사항을 계산하기 위한 DiffUtil.ItemCallback 구현체입니다.
     * 이를 통해 RecyclerView는 필요한 최소한의 애니메이션과 업데이트만 수행할 수 있습니다.
     */
    public static class EventDiff extends DiffUtil.ItemCallback<Event> {

        /**
         * 두 아이템이 동일한 항목을 나타내는지 확인합니다. (보통 고유 ID를 비교)
         */
        @Override
        public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.getId() == newItem.getId();
        }

        /**
         * 두 아이템의 내용이 동일한지 확인합니다.
         * areItemsTheSame이 true일 때만 호출됩니다.
         */
        @Override
        public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getEventTimeMillis() == newItem.getEventTimeMillis();
        }
    }
}